package com.cloud.test;

import com.cloud.test.user.dto.LoginUserDto;
import com.cloud.test.user.service.impl.IUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private IUserServiceImpl userService;

    @Test
    void testBatchEncodePasswords() {
        userService.batchEncodePasswords();
    }

    @Test
    void testLogin() {
        LoginUserDto user = new LoginUserDto();
        user.setUsername("renjie1");
        user.setPassword("111111");
        userService.login(user);
    }
}
