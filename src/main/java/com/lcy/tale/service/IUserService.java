package com.lcy.tale.service;

import com.lcy.tale.model.entity.UsersVo;

/**
 * Created by BlueT on 2017/3/3.
 */
public interface IUserService {

    /**
     * 保存用户数据
     *
     * @param userVo 用户数据
     * @return 主键
     */

    Integer insertUser(UsersVo userVo);

    /**
     * 通过uid查找对象
     * @param uid
     * @return
     */
    UsersVo queryUserById(Integer uid);

    /**
     * 用戶登录
     * @param username
     * @param password
     * @return
     */
    UsersVo login(String username, String password);

    /**
     * 根据主键更新user对象
     * @param userVo
     * @return
     */
    void updateByUid(UsersVo userVo);
}
