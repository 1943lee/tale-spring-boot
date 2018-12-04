package com.lcy.tale.service.impl;

import com.lcy.tale.dao.RelationshipsVoMapper;
import com.lcy.tale.model.entity.RelationshipsVo;
import com.lcy.tale.service.IRelationshipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by BlueT on 2017/3/18.
 */
@Slf4j
@Service
public class RelationshipServiceImpl implements IRelationshipService {
    @Resource
    private RelationshipsVoMapper relationshipVoMapper;

    @Override
    public void deleteById(Integer cid, Integer mid) {
        Example relationshipVoExample = new Example(RelationshipsVo.class);
        Criteria criteria = relationshipVoExample.createCriteria();
        if (cid != null) {
            criteria.andEqualTo("cid", cid);
        }
        if (mid != null) {
            criteria.andEqualTo("mid", mid);
        }
        relationshipVoMapper.deleteByExample(relationshipVoExample);
    }

    @Override
    public List<RelationshipsVo> getRelationshipById(Integer cid, Integer mid) {
        Example relationshipVoExample = new Example(RelationshipsVo.class);
        Criteria criteria = relationshipVoExample.createCriteria();
        if (cid != null) {
            criteria.andEqualTo("cid", cid);
        }
        if (mid != null) {
            criteria.andEqualTo("mid", mid);
        }
        return relationshipVoMapper.selectByExample(relationshipVoExample);
    }

    @Override
    public void insertVo(RelationshipsVo relationshipVoKey) {
        relationshipVoMapper.insert(relationshipVoKey);
    }

    @Override
    public Long countById(Integer cid, Integer mid) {
        log.debug("Enter countById method:cid={},mid={}",cid,mid);
        Example relationshipVoExample = new Example(RelationshipsVo.class);
        Criteria criteria = relationshipVoExample.createCriteria();
        if (cid != null) {
            criteria.andEqualTo("cid", cid);
        }
        if (mid != null) {
            criteria.andEqualTo("mid", mid);
        }
        long num = relationshipVoMapper.selectCountByExample(relationshipVoExample);
        log.debug("Exit countById method return num={}",num);
        return num;
    }
}
