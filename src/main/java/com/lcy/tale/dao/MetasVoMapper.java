package com.lcy.tale.dao;

import com.lcy.tale.core.Mapper;
import com.lcy.tale.dto.MetaDto;
import com.lcy.tale.model.entity.MetasVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MetasVoMapper extends Mapper<MetasVo> {

    List<MetaDto> selectFromSql(Map<String,Object> paraMap);

    MetaDto selectDtoByNameAndType(@Param("name") String name, @Param("type") String type);

    Integer countWithSql(Integer mid);
}