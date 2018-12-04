package com.lcy.tale.dao;

import com.lcy.tale.core.Mapper;
import com.lcy.tale.model.entity.OptionsVo;

import java.util.List;

public interface OptionsVoMapper extends Mapper<OptionsVo> {

    /**
     * 批量保存
     * @param optionVos list
     * @return 保存的个数
     */
    int insertOptions(List<OptionsVo> optionVos);
}