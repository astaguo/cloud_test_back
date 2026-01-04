package com.cloud.test.mapper;

import com.cloud.test.user.domain.Role;
import com.cloud.test.user.mapper.RoleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class RoleMapperTest {

    @Autowired
    public RoleMapper roleMapper;

    @Test
    public void testAdd() {
        Role role = new Role();
        role.setName("Admin");
        roleMapper.insert(role);
    }

    @Test
    public void testSelectRoleByPath() {
        List<Role> roles = roleMapper.selectRoleByPath("/user/add");
        roles.forEach(System.out::println);
    }

    @Test
    public void testSelectRoleByUserName() {
        Role role = roleMapper.selectRoleByUserName("xiurong");
        System.out.println(role.toString());
    }
}
