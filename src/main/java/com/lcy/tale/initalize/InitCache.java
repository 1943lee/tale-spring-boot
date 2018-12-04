package com.lcy.tale.initalize;

import com.lcy.tale.constant.WebConst;
import com.lcy.tale.model.entity.OptionsVo;
import com.lcy.tale.service.IOptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lcy
 * @since 2018/12/4
 */
@Slf4j
@Component
public class InitCache implements CommandLineRunner {
    @Resource
    private IOptionService optionService;

    @Override
    public void run(String... args) {
        List<OptionsVo> optionsVoList = optionService.getOptions();

        // initialize config
        optionsVoList.forEach(o -> WebConst.initConfig.put(o.getName(), o.getValue()));

        log.debug("web constant configurations has been initialized!");
    }
}
