package com.lcy.tale.service.impl;

import com.github.pagehelper.PageHelper;
import com.lcy.tale.constant.WebConst;
import com.lcy.tale.dao.LogsVoMapper;
import com.lcy.tale.model.entity.LogsVo;
import com.lcy.tale.service.ILogService;
import com.lcy.tale.utils.DateKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by BlueT on 2017/3/4.
 */
@Slf4j
@Service
public class LogServiceImpl implements ILogService {
    @Resource
    private LogsVoMapper logDao;

    @Override
    public void insertLog(LogsVo logVo) {
        logDao.insert(logVo);
    }

    @Override
    public void insertLog(String action, String data, String ip, Integer authorId) {
        LogsVo logs = new LogsVo();
        logs.setAction(action);
        logs.setData(data);
        logs.setIp(ip);
        logs.setAuthorId(authorId);
        logs.setCreated(DateKit.getCurrentUnixTime());
        logDao.insert(logs);
    }

    @Override
    public List<LogsVo> getLogs(int page, int limit) {
        log.debug("Enter getLogs method:page={},linit={}",page,limit);
        if (page <= 0) {
            page = 1;
        }
        if (limit < 1 || limit > WebConst.MAX_POSTS) {
            limit = 10;
        }
        Example example = new Example(LogsVo.class);
        example.setOrderByClause("id desc");
        PageHelper.startPage((page - 1) * limit, limit);
        List<LogsVo> logVos = logDao.selectByExample(example);
        log.debug("Exit getLogs method");
        return logVos;
    }
}
