package com.lcy.tale.dao;

import com.lcy.tale.core.Mapper;
import com.lcy.tale.model.bo.ArchiveBo;
import com.lcy.tale.model.entity.ContentsVo;

import java.util.List;

public interface ContentsVoMapper extends Mapper<ContentsVo> {
    List<ArchiveBo> findReturnArchiveBo();

    List<ContentsVo> findByCatalog(Integer mid);
}