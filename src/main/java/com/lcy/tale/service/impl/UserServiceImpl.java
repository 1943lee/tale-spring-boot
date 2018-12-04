package com.lcy.tale.service.impl;

import com.lcy.tale.dao.UsersVoMapper;
import com.lcy.tale.exception.TipException;
import com.lcy.tale.model.entity.UsersVo;
import com.lcy.tale.service.IUserService;
import com.lcy.tale.utils.TaleUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by BlueT on 2017/3/3.
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UsersVoMapper userDao;

    @Override
    @Transactional
    public Integer insertUser(UsersVo userVo) {
        Integer uid = null;
        if (StringUtils.isNotBlank(userVo.getUsername()) && StringUtils.isNotBlank(userVo.getEmail())) {
            // 用户密码加密
            String encodePwd = TaleUtils.MD5encode(userVo.getUsername() + userVo.getPassword());
            userVo.setPassword(encodePwd);
            userDao.insertSelective(userVo);
        }
        return userVo.getUid();
    }

    @Override
    public UsersVo queryUserById(Integer uid) {
        UsersVo userVo = null;
        if (uid != null) {
            userVo = userDao.selectByPrimaryKey(uid);
        }
        return userVo;
    }

    @Override
    public UsersVo login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new TipException("用户名和密码不能为空");
        }
        Example example = new Example(UsersVo.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        int count = userDao.selectCountByExample(example);
        if (count < 1) {
            throw new TipException("不存在该用户");
        }
        String pwd = TaleUtils.MD5encode(username + password);
        criteria.andEqualTo("password", pwd);
        List<UsersVo> userVos = userDao.selectByExample(example);
        if (userVos.size() != 1) {
            throw new TipException("用户名或密码错误");
        }
        return userVos.get(0);
    }

    @Override
    @Transactional
    public void updateByUid(UsersVo userVo) {
        if (null == userVo || null == userVo.getUid()) {
            throw new TipException("userVo is null");
        }
        int i = userDao.updateByPrimaryKeySelective(userVo);
        if (i != 1) {
            throw new TipException("update user by uid and retrun is not one");
        }
    }
}
