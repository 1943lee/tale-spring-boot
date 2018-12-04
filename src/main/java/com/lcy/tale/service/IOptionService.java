package com.lcy.tale.service;

import com.lcy.tale.model.entity.OptionsVo;

import java.util.List;
import java.util.Map;

/**
 * options的接口
 * Created by BlueT on 2017/3/7.
 */
public interface IOptionService {

    void insertOption(OptionsVo optionVo);

    void insertOption(String name, String value);

    List<OptionsVo> getOptions();


    /**
     * 保存一组配置
     *
     * @param options
     */
    void saveOptions(Map<String, String> options);

    OptionsVo getOptionByName(String name);
}
