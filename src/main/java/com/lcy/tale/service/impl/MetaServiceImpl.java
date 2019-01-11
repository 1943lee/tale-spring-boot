package com.lcy.tale.service.impl;

import com.lcy.tale.constant.WebConst;
import com.lcy.tale.dao.MetasVoMapper;
import com.lcy.tale.model.dto.MetaDto;
import com.lcy.tale.model.dto.Types;
import com.lcy.tale.exception.TipException;
import com.lcy.tale.model.entity.ContentsVo;
import com.lcy.tale.model.entity.MetasVo;
import com.lcy.tale.model.entity.RelationshipsVo;
import com.lcy.tale.service.IContentService;
import com.lcy.tale.service.IMetaService;
import com.lcy.tale.service.IRelationshipService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by BlueT on 2017/3/17.
 */
@Slf4j
@Service
public class MetaServiceImpl implements IMetaService {
    @Resource
    private MetasVoMapper metaDao;

    @Resource
    private IRelationshipService relationshipService;

    @Resource
    private IContentService contentService;

    @Override
    public MetaDto getMeta(String type, String name) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(name)) {
            return metaDao.selectDtoByNameAndType(name, type);
        }
        return null;
    }

    @Override
    public Integer countMeta(Integer mid) {
        return metaDao.countWithSql(mid);
    }

    @Override
    public List<MetasVo> getMetas(String types) {
        if (StringUtils.isNotBlank(types)) {
            Example metaVoExample = new Example(MetasVo.class);
            metaVoExample.setOrderByClause("sort desc, mid desc");
            metaVoExample.createCriteria().andEqualTo("type", types);
            return metaDao.selectByExample(metaVoExample);
        }
        return null;
    }

    @Override
    public List<MetaDto> getMetaList(String type, String orderby, int limit) {
        if (StringUtils.isNotBlank(type)) {
            if (StringUtils.isBlank(orderby)) {
                orderby = "count desc, a.mid desc";
            }
            if (limit < 1 || limit > WebConst.MAX_POSTS) {
                limit = 10;
            }
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("type", type);
            paraMap.put("order", orderby);
            paraMap.put("limit", limit);
            return metaDao.selectFromSql(paraMap);
        }
        return null;
    }

    @Override
    @Transactional
    public void delete(int mid) {
        MetasVo metas = metaDao.selectByPrimaryKey(mid);
        if (null != metas) {
            String type = metas.getType();
            String name = metas.getName();

            metaDao.deleteByPrimaryKey(mid);

            List<RelationshipsVo> rlist = relationshipService.getRelationshipById(null, mid);
            if (null != rlist) {
                for (RelationshipsVo r : rlist) {
                    ContentsVo contents = contentService.getContents(String.valueOf(r.getCid()));
                    if (null != contents) {
                        ContentsVo temp = new ContentsVo();
                        temp.setCid(r.getCid());
                        if (type.equals(Types.CATEGORY.getType())) {
                            temp.setCategories(reMeta(name, contents.getCategories()));
                        }
                        if (type.equals(Types.TAG.getType())) {
                            temp.setTags(reMeta(name, contents.getTags()));
                        }
                        contentService.updateContentByCid(temp);
                    }
                }
            }
            relationshipService.deleteById(null, mid);
        }
    }

    @Override
    @Transactional
    public void saveMeta(String type, String name, Integer mid) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(name)) {
            Example metaVoExample = new Example(MetasVo.class);
            metaVoExample.createCriteria()
                    .andEqualTo("type", type)
                    .andEqualTo("name", name);
            List<MetasVo> metaVos = metaDao.selectByExample(metaVoExample);
            MetasVo metas;
            if (metaVos.size() != 0) {
                throw new TipException("已经存在该项");
            } else {
                metas = new MetasVo();
                metas.setName(name);
                if (null != mid) {
                    MetasVo original = metaDao.selectByPrimaryKey(mid);
                    metas.setMid(mid);
                    metaDao.updateByPrimaryKeySelective(metas);
                    //更新原有文章的categories
                    contentService.updateCategory(original.getName(), name);
                } else {
                    metas.setType(type);
                    metaDao.insertSelective(metas);
                }
            }
        }
    }

    @Override
    @Transactional
    public void saveMetas(Integer cid, String names, String type) {
        if (null == cid) {
            throw new TipException("项目关联id不能为空");
        }
        if (StringUtils.isNotBlank(names) && StringUtils.isNotBlank(type)) {
            String[] nameArr = StringUtils.split(names, ",");
            for (String name : nameArr) {
                this.saveOrUpdate(cid, name, type);
            }
        }
    }

    private void saveOrUpdate(Integer cid, String name, String type) {
        Example metaVoExample = new Example(MetasVo.class);
        metaVoExample.createCriteria()
                .andEqualTo("type", type)
                .andEqualTo("name", name);
        List<MetasVo> metaVos = metaDao.selectByExample(metaVoExample);

        int mid;
        MetasVo metas;
        if (metaVos.size() == 1) {
            metas = metaVos.get(0);
            mid = metas.getMid();
        } else if (metaVos.size() > 1) {
            throw new TipException("查询到多条数据");
        } else {
            metas = new MetasVo();
            metas.setSlug(name);
            metas.setName(name);
            metas.setType(type);
            metaDao.insertSelective(metas);
            mid = metas.getMid();
        }
        if (mid != 0) {
            Long count = relationshipService.countById(cid, mid);
            if (count == 0) {
                RelationshipsVo relationships = new RelationshipsVo();
                relationships.setCid(cid);
                relationships.setMid(mid);
                relationshipService.insertVo(relationships);
            }
        }
    }


    private String reMeta(String name, String metas) {
        String[] ms = StringUtils.split(metas, ",");
        StringBuilder sbuf = new StringBuilder();
        for (String m : ms) {
            if (!name.equals(m)) {
                sbuf.append(",").append(m);
            }
        }
        if (sbuf.length() > 0) {
            return sbuf.substring(1);
        }
        return "";
    }

    @Override
    @Transactional
    public void saveMeta(MetasVo metas) {
        if (null != metas) {
            metaDao.insertSelective(metas);
        }
    }

    @Override
    @Transactional
    public void update(MetasVo metas) {
        if (null != metas && null != metas.getMid()) {
            metaDao.updateByPrimaryKeySelective(metas);
        }
    }
}
