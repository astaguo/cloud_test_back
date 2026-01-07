package com.cloud.test.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.test.user.domain.AuthUserDetails;
import com.cloud.test.user.domain.User;
import com.cloud.test.user.dto.LoginUserDto;
import com.cloud.test.user.dto.RegisterDto;
import com.cloud.test.base.exceptions.UserDefinedException;
import com.cloud.test.user.mapper.UserMapper;
import com.cloud.test.user.service.IUserService;
import com.cloud.test.base.utils.JwtTokenUtil;
import com.cloud.test.user.vo.LoginVO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class IUserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    // 构造器注入
    public IUserServiceImpl(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
    }

    @Override
    public LoginVO login(LoginUserDto user) {
        // 不需要连接数据库
        // 1. 把登陆时候的用户名与密码封装成一个UsernamePasswordAuthenticationToken对象
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());
        // 2. 通过AuthenticationManager的authenticate方法进行用户认证
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 3. 如果认证不通过， 就返回自定义的异常
        if (Objects.isNull(authenticate)) throw new UserDefinedException("登陆失败！");
        // 4. 如果认证成功， 就从authenticate对象的getPrincipal方法中拿到认证通过后的用户对象
        AuthUserDetails authUserDetails = (AuthUserDetails) authenticate.getPrincipal();
        // 5. 生成token
        String token = new JwtTokenUtil().generateToken(authUserDetails);
        // 6. 拼接返回信息
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUser(authUserDetails);
        // 7. 返回数据
        return loginVO;
    }

    @Override
    public boolean register(RegisterDto registerDto) {
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());
        try {
            userMapper.insert(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public User selectByUsername(String username) {
        return userMapper.selectByName(username);
    }

    @Override
    // 批量加密密码（用于数据迁移）
    @Transactional
    public void batchEncodePasswords() {
        List<User> users = userMapper.selectList(null);
        System.out.println(users);
        for (User user : users) {
            // 检查密码是否已经加密（BCrypt密码以$2a$开头）
            if (!user.getPassword().startsWith("$2a$")) {
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                System.out.println(encodedPassword);
                user.setPassword(encodedPassword);
            }
        }
        updateBatchById(users);
    }
}
