package com.cloud.test.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.test.domain.AuthUserDetails;
import com.cloud.test.domain.User;
import com.cloud.test.dto.LoginUserDto;
import com.cloud.test.dto.RegisterDto;
import com.cloud.test.exceptions.UserDefinedException;
import com.cloud.test.mapper.UserMapper;
import com.cloud.test.service.IUserService;
import com.cloud.test.utils.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, Object> login(LoginUserDto user) {
        // 传入用户名和密码
        UsernamePasswordAuthenticationToken usernamePassword =
                new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());
        // 是实现登录逻辑，此时就回去调用LoadUserByUsername方法
        Authentication authenticate = authenticationManager.authenticate(usernamePassword);
        // 获取返回的用户信息
        Object principal = authenticate.getPrincipal();
        // 强转为MySysUserDetails类型
        AuthUserDetails mySysUserDetails = (AuthUserDetails) principal;
        // 输出用户信息
        System.err.println(mySysUserDetails);

        if(user.getUsername() == null || user.getPassword() == null) throw new UserDefinedException("请检查您的参数！");

        // 从DB中获取用户信息
        User userByDb = userMapper.selectByName(user.getUsername());
        // 验证用户名和密码
        String rawPassword = passwordEncoder.encode(user.getPassword());
        if(userByDb == null || passwordEncoder.matches(rawPassword, userByDb.getPassword())) throw new UserDefinedException("用户名和密码错误！");

        // 生成token
        JwtTokenUtil  jwtTokenUtil = new JwtTokenUtil();
        final String token = jwtTokenUtil.generateToken(mySysUserDetails);

        userMapper.updateById(userByDb);

        // 返回拼接信息
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", JSON.toJSONString(userByDb));

        //返回token
        return map;
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
