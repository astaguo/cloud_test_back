package com.cloud.test.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.test.user.domain.User;

public interface UserMapper extends BaseMapper<User> {
    //通过用户名取出用户对象
    //User userMysql = baseMapper.selectByName(user.getName());
    User selectByName(String name);
}
