package com.cloud.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.test.domain.User;
import com.cloud.test.dto.LoginUserDto;
import com.cloud.test.dto.RegisterDto;

import java.util.Map;

public interface IUserService extends IService<User> {

    Map<String,Object> login(LoginUserDto user);

    boolean register(RegisterDto registerDto);

    boolean saveOrUpdate(User user);

    User selectByUsername(String username);

    void batchEncodePasswords();
}
