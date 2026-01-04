package com.cloud.test.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.test.user.domain.AuthUserDetails;
import com.cloud.test.user.domain.User;
import com.cloud.test.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AutoUserDetailsService implements UserDetailsService {

    /*
     *  UserDetailsService：提供查询用户功能，如根据用户名查询用户，并返回UserDetails
     *UserDetails，SpringSecurity定义的类， 记录用户信息，如用户名、密码、权限等
     * */
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(username != null, User::getUsername, username));
        if (user==null){
            throw new UsernameNotFoundException("用户不存在");
        }
        return new AuthUserDetails(user);
    }
}
