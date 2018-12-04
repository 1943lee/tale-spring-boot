package com.lcy.tale.service.impl;

import com.lcy.tale.dao.OptionsVoMapper;
import com.lcy.tale.model.entity.OptionsVo;
import com.lcy.tale.service.IOptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * options表的service
 * Created by BlueT on 2017/3/7.
 */
@Slf4j
@Service
public class OptionServiceImpl implements IOptionService {

    @Resource
    private OptionsVoMapper optionDao;

    @Override
    public void insertOption(OptionsVo optionVo) {
        log.debug("Enter insertOption method:optionVo={}", optionVo);
        optionDao.insertSelective(optionVo);
        log.debug("Exit insertOption method.");
    }

    @Override
    @Transactional
    public void insertOption(String name, String value) {
        log.debug("Enter insertOption method:name={},value={}", name, value);
        OptionsVo optionVo = new OptionsVo();
        optionVo.setName(name);
        optionVo.setValue(value);
        if (optionDao.selectByPrimaryKey(name) == null) {
            optionDao.insertSelective(optionVo);
        } else {
            optionDao.updateByPrimaryKeySelective(optionVo);
        }
        log.debug("Exit insertOption method.");
    }

    @Override
    @Transactional
    public void saveOptions(Map<String, String> options) {
        if (null != options && !options.isEmpty()) {
            options.forEach(this::insertOption);
        }
    }

    @Override
    public OptionsVo getOptionByName(String name) {
        return optionDao.selectByPrimaryKey(name);
    }

    @Override
    public List<OptionsVo> getOptions() {
        return optionDao.selectAll();
    }
}
