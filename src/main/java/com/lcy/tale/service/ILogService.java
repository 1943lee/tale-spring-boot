package com.lcy.tale.service;

import com.lcy.tale.model.entity.LogsVo;

import java.util.List;

/**
 * Created by BlueT on 2017/3/4.
 */
public interface ILogService {

    /**
     * 保存操作日志
     *
     * @param logVo
     */
    void insertLog(LogsVo logVo);

    /**
     *  保存
     * @param action
     * @param data
     * @param ip
     * @param authorId
     */
    void insertLog(String action, String data, String ip, Integer authorId);

    /**
     * 获取日志分页
     * @param page 当前页
     * @param limit 每页条数
     * @return 日志
     */
    List<LogsVo> getLogs(int page, int limit);
}
