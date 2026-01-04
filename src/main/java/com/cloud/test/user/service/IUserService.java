package com.cloud.test.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.test.user.domain.User;
import com.cloud.test.user.dto.LoginUserDto;
import com.cloud.test.user.dto.RegisterDto;

import java.util.Map;

public interface IUserService extends IService<User> {

    Map<String,Object> login(LoginUserDto user);

    boolean register(RegisterDto registerDto);

    boolean saveOrUpdate(User user);

    User selectByUsername(String username);

    void batchEncodePasswords();
}
