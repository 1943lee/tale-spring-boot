package com.lcy.tale.service.impl;

import com.github.pagehelper.PageHelper;
import com.lcy.tale.constant.WebConst;
import com.lcy.tale.controller.admin.AttachController;
import com.lcy.tale.dao.AttachVoMapper;
import com.lcy.tale.dao.CommentsVoMapper;
import com.lcy.tale.dao.ContentsVoMapper;
import com.lcy.tale.dao.MetasVoMapper;
import com.lcy.tale.dto.MetaDto;
import com.lcy.tale.dto.Types;
import com.lcy.tale.exception.TipException;
import com.lcy.tale.model.bo.ArchiveBo;
import com.lcy.tale.model.bo.BackResponseBo;
import com.lcy.tale.model.bo.StatisticsBo;
import com.lcy.tale.model.entity.AttachVo;
import com.lcy.tale.model.entity.CommentsVo;
import com.lcy.tale.model.entity.ContentsVo;
import com.lcy.tale.model.entity.MetasVo;
import com.lcy.tale.service.ISiteService;
import com.lcy.tale.utils.DateKit;
import com.lcy.tale.utils.TaleUtils;
import com.lcy.tale.utils.ZipUtils;
import com.lcy.tale.utils.backup.Backup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 统计页面
 * Created by BlueT on 2017/3/7.
 */
@Slf4j
@Service
public class SiteServiceImpl implements ISiteService {
    @Resource
    private CommentsVoMapper commentDao;

    @Resource
    private ContentsVoMapper contentDao;

    @Resource
    private AttachVoMapper attachDao;

    @Resource
    private MetasVoMapper metaDao;

    @Override
    public List<CommentsVo> recentComments(int limit) {
        log.debug("Enter recentComments method:limit={}", limit);
        if (limit < 0 || limit > 10) {
            limit = 10;
        }
        Example example = new Example(CommentsVo.class);
        example.setOrderByClause("created desc");
        PageHelper.startPage(1, limit);
        List<CommentsVo> byPage = commentDao.selectByExample(example);
        log.debug("Exit recentComments method");
        return byPage;
    }

    @Override
    public List<ContentsVo> recentContents(int limit) {
        log.debug("Enter recentContents method");
        if (limit < 0 || limit > 10) {
            limit = 10;
        }
        Example example = new Example(ContentsVo.class);
        example.createCriteria()
                .andEqualTo("status", Types.PUBLISH.getType())
                .andEqualTo("type", Types.ARTICLE.getType());
        example.setOrderByClause("created desc");
        PageHelper.startPage(1, limit);
        List<ContentsVo> list = contentDao.selectByExample(example);
        log.debug("Exit recentContents method");
        return list;
    }

    @Override
    public BackResponseBo backup(String bk_type, String bk_path, String fmt) throws Exception {
        BackResponseBo backResponse = new BackResponseBo();
        if (bk_type.equals("attach")) {
            if (StringUtils.isBlank(bk_path)) {
                throw new TipException("请输入备份文件存储路径");
            }
            if (!(new File(bk_path)).isDirectory()) {
                throw new TipException("请输入一个存在的目录");
            }
            String bkAttachDir = AttachController.CLASSPATH + "upload";
            String bkThemesDir = AttachController.CLASSPATH + "templates/themes";

            String fname = DateKit.dateFormat(new Date(), fmt) + "_" + TaleUtils.getRandomNumber(5) + ".zip";

            String attachPath = bk_path + "/" + "attachs_" + fname;
            String themesPath = bk_path + "/" + "themes_" + fname;

            ZipUtils.zipFolder(bkAttachDir, attachPath);
            ZipUtils.zipFolder(bkThemesDir, themesPath);

            backResponse.setAttachPath(attachPath);
            backResponse.setThemePath(themesPath);
        }
        if (bk_type.equals("db")) {

            String bkAttachDir = AttachController.CLASSPATH + "upload/";
            if (!(new File(bkAttachDir)).isDirectory()) {
                File file = new File(bkAttachDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
            String sqlFileName = "tale_" + DateKit.dateFormat(new Date(), fmt) + "_" + TaleUtils.getRandomNumber(5) + ".sql";
            String zipFile = sqlFileName.replace(".sql", ".zip");

            Backup backup = new Backup(TaleUtils.getNewDataSource().getConnection());
            String sqlContent = backup.execute();

            File sqlFile = new File(bkAttachDir + sqlFileName);
            write(sqlContent, sqlFile, Charset.forName("UTF-8"));

            String zip = bkAttachDir + zipFile;
            ZipUtils.zipFile(sqlFile.getPath(), zip);

            if (!sqlFile.exists()) {
                throw new TipException("数据库备份失败");
            }
            sqlFile.delete();

            backResponse.setSqlPath(zipFile);

            // 10秒后删除备份文件
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    new File(zip).delete();
                }
            }, 10 * 1000);
        }
        return backResponse;
    }

    @Override
    public CommentsVo getComment(Integer coid) {
        if (null != coid) {
            return commentDao.selectByPrimaryKey(coid);
        }
        return null;
    }

    @Override
    public StatisticsBo getStatistics() {
        log.debug("Enter getStatistics method");
        StatisticsBo statistics = new StatisticsBo();

        Example contentVoExample = new Example(ContentsVo.class);
        contentVoExample.createCriteria()
                .andEqualTo("type", Types.ARTICLE.getType())
                .andEqualTo("status", Types.PUBLISH.getType());
        int articles =   contentDao.selectCountByExample(contentVoExample);

        int comments = commentDao.selectCountByExample(new Example(CommentsVo.class));

        int attachs = attachDao.selectCountByExample(new Example(AttachVo.class));

        Example metaVoExample = new Example(MetasVo.class);
        metaVoExample.createCriteria()
                .andEqualTo("type", Types.LINK.getType());
        int links = metaDao.selectCountByExample(metaVoExample);

        statistics.setArticles(articles);
        statistics.setComments(comments);
        statistics.setAttachs(attachs);
        statistics.setLinks(links);
        log.debug("Exit getStatistics method");
        return statistics;
    }

    @Override
    public List<ArchiveBo> getArchives() {
        log.debug("Enter getArchives method");
        List<ArchiveBo> archives = contentDao.findReturnArchiveBo();
        if (null != archives) {
            archives.forEach(archive -> {
                Example example = new Example(ContentsVo.class);
                Criteria criteria = example.createCriteria()
                        .andEqualTo("type", Types.ARTICLE.getType())
                        .andEqualTo("status", Types.PUBLISH.getType());
                example.setOrderByClause("created desc");
                String date = archive.getDate();
                Date sd = DateKit.dateFormat(date, "yyyy年MM月");
                int start = DateKit.getUnixTimeByDate(sd);
                int end = DateKit.getUnixTimeByDate(DateKit.dateAdd(DateKit.INTERVAL_MONTH, sd, 1)) - 1;
                criteria.andGreaterThan("created", start);
                criteria.andLessThan("created", end);
                List<ContentsVo> contentss = contentDao.selectByExample(example);
                archive.setArticles(contentss);
            });
        }
        log.debug("Exit getArchives method");
        return archives;
    }

    @Override
    public List<MetaDto> metas(String type, String orderBy, int limit){
        log.debug("Enter metas method:type={},order={},limit={}", type, orderBy, limit);
        List<MetaDto> retList=null;
        if (StringUtils.isNotBlank(type)) {
            if(StringUtils.isBlank(orderBy)){
                orderBy = "count desc, a.mid desc";
            }
            if(limit < 1 || limit > WebConst.MAX_POSTS){
                limit = 10;
            }
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("type", type);
            paraMap.put("order", orderBy);
            paraMap.put("limit", limit);
            retList= metaDao.selectFromSql(paraMap);
        }
        log.debug("Exit metas method");
        return retList;
    }


    private void write(String data, File file, Charset charset) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data.getBytes(charset));
        } catch (IOException var8) {
            throw new IllegalStateException(var8);
        } finally {
            if(null != os) {
                try {
                    os.close();
                } catch (IOException var2) {
                    var2.printStackTrace();
                }
            }
        }

    }

}
