package com.lcy.tale.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lcy.tale.constant.WebConst;
import com.lcy.tale.dao.ContentsVoMapper;
import com.lcy.tale.dao.MetasVoMapper;
import com.lcy.tale.model.dto.Types;
import com.lcy.tale.exception.TipException;
import com.lcy.tale.model.entity.ContentsVo;
import com.lcy.tale.service.IContentService;
import com.lcy.tale.service.IMetaService;
import com.lcy.tale.service.IRelationshipService;
import com.lcy.tale.utils.DateKit;
import com.lcy.tale.utils.TaleUtils;
import com.lcy.tale.utils.Tools;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2017/3/13 013.
 */
@Slf4j
@Service
public class ContentServiceImpl implements IContentService {
    @Resource
    private ContentsVoMapper contentDao;

    @Resource
    private MetasVoMapper metaDao;

    @Resource
    private IRelationshipService relationshipService;

    @Resource
    private IMetaService metasService;

    @Override
    @Transactional
    public String publish(ContentsVo contents) {
        if (null == contents) {
            return "文章对象为空";
        }
        if (StringUtils.isBlank(contents.getTitle())) {
            return "文章标题不能为空";
        }
        if (StringUtils.isBlank(contents.getContent())) {
            return "文章内容不能为空";
        }
        int titleLength = contents.getTitle().length();
        if (titleLength > WebConst.MAX_TITLE_COUNT) {
            return "文章标题过长";
        }
        int contentLength = contents.getContent().length();
        if (contentLength > WebConst.MAX_TEXT_COUNT) {
            return "文章内容过长";
        }
        if (null == contents.getAuthorId()) {
            return "请登录后发布文章";
        }
        if (StringUtils.isNotBlank(contents.getSlug())) {
            if (contents.getSlug().length() < 5) {
                return "路径太短了";
            }
            if (!TaleUtils.isPath(contents.getSlug())) return "您输入的路径不合法";
            Example example = new Example(ContentsVo.class);
            example.createCriteria().andEqualTo("type", contents.getType()).andEqualTo("slug", contents.getSlug());
            long count = contentDao.selectCountByExample(example);
            if (count > 0) return "该路径已经存在，请重新输入";
        } else {
            contents.setSlug(null);
        }

        contents.setContent(EmojiParser.parseToAliases(contents.getContent()));

        int time = DateKit.getCurrentUnixTime();
        contents.setCreated(time);
        contents.setModified(time);
        contents.setHits(0);
        contents.setCommentsNum(0);

        String tags = contents.getTags();
        String categories = contents.getCategories();
        contentDao.insert(contents);
        Integer cid = contents.getCid();
        metasService.saveMetas(cid, tags, Types.TAG.getType());
        metasService.saveMetas(cid, categories, Types.CATEGORY.getType());
        return WebConst.SUCCESS_RESULT;
    }

    @Override
    public PageInfo<ContentsVo> getContents(Integer p, Integer limit) {
        log.debug("Enter getContents method");
        Example example = new Example(ContentsVo.class);
        example.setOrderByClause("created desc");
        example.createCriteria().andEqualTo("type", Types.ARTICLE.getType())
                .andEqualTo("status", Types.PUBLISH.getType());
        PageHelper.startPage(p, limit);
        List<ContentsVo> data = contentDao.selectByExample(example);
        PageInfo<ContentsVo> pageInfo = new PageInfo<>(data);
        log.debug("Exit getContents method");
        return pageInfo;
    }

    @Override
    public ContentsVo getContents(String id) {
        if (StringUtils.isNotBlank(id)) {
            if (Tools.isNumber(id)) {
                ContentsVo contentVo = contentDao.selectByPrimaryKey(Integer.valueOf(id));
                return contentVo;
            } else {
                Example example = new Example(ContentsVo.class);
                example.createCriteria().andEqualTo("slug", id);
                List<ContentsVo> contentVos = contentDao.selectByExample(example);
                if (contentVos.size() != 1) {
                    throw new TipException("query content by id and return is not one");
                }
                return contentVos.get(0);
            }
        }
        return null;
    }

    @Override
    public void updateContentByCid(ContentsVo contentVo) {
        if (null != contentVo && null != contentVo.getCid()) {
            contentDao.updateByPrimaryKeySelective(contentVo);
        }
    }

    @Override
    public PageInfo<ContentsVo> getArticles(Integer mid, int page, int limit) {
        int total = metaDao.countWithSql(mid);

        PageHelper.startPage(page, limit);
        List<ContentsVo> list = contentDao.findByCatalog(mid);
        PageInfo<ContentsVo> paginator = new PageInfo<>(list);
        paginator.setTotal(total);
        return paginator;
    }

    @Override
    public PageInfo<ContentsVo> getArticles(String keyword, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        Example example = new Example(ContentsVo.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", Types.ARTICLE.getType());
        criteria.andEqualTo("status", Types.PUBLISH.getType());
        criteria.andLike("title", keyword);
        example.setOrderByClause("created desc");
        List<ContentsVo> contentVos = contentDao.selectByExample(example);
        return new PageInfo<>(contentVos);
    }

    @Override
    public PageInfo<ContentsVo> getArticlesWithpage(Example commentVoExample, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<ContentsVo> contentVos = contentDao.selectByExample(commentVoExample);
        return new PageInfo<>(contentVos);
    }

    @Override
    @Transactional
    public String deleteByCid(Integer cid) {
        ContentsVo contents = this.getContents(cid + "");
        if (null != contents) {
            contentDao.deleteByPrimaryKey(cid);
            relationshipService.deleteById(cid, null);
            return WebConst.SUCCESS_RESULT;
        }
        return "数据为空";
    }

    @Override
    public void updateCategory(String ordinal, String newCatefory) {
        ContentsVo contentVo = new ContentsVo();
        contentVo.setCategories(newCatefory);
        Example example = new Example(ContentsVo.class);
        example.createCriteria().andEqualTo("categories", ordinal);
        contentDao.updateByExampleSelective(contentVo, example);
    }

    @Override
    @Transactional
    public String updateArticle(ContentsVo contents) {
        if (null == contents) {
            return "文章对象为空";
        }
        if (StringUtils.isBlank(contents.getTitle())) {
            return "文章标题不能为空";
        }
        if (StringUtils.isBlank(contents.getContent())) {
            return "文章内容不能为空";
        }
        int titleLength = contents.getTitle().length();
        if (titleLength > WebConst.MAX_TITLE_COUNT) {
            return "文章标题过长";
        }
        int contentLength = contents.getContent().length();
        if (contentLength > WebConst.MAX_TEXT_COUNT) {
            return "文章内容过长";
        }
        if (null == contents.getAuthorId()) {
            return "请登录后发布文章";
        }
        if (StringUtils.isBlank(contents.getSlug())) {
            contents.setSlug(null);
        }
        int time = DateKit.getCurrentUnixTime();
        contents.setModified(time);
        Integer cid = contents.getCid();
        contents.setContent(EmojiParser.parseToAliases(contents.getContent()));

        contentDao.updateByPrimaryKeySelective(contents);
        relationshipService.deleteById(cid, null);
        metasService.saveMetas(cid, contents.getTags(), Types.TAG.getType());
        metasService.saveMetas(cid, contents.getCategories(), Types.CATEGORY.getType());
        return WebConst.SUCCESS_RESULT;
    }
}
