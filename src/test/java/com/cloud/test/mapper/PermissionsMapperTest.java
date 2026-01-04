package com.cloud.test.mapper;

import com.cloud.test.user.domain.Permissions;
import com.cloud.test.user.mapper.PermissionsMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class PermissionsMapperTest {

    @Autowired
    public PermissionsMapper permissionsMapper;

    @Test
    public void addRolePermissions() {
        Permissions permissions = new Permissions();
        permissions.setName("用户列表");
        permissions.setPath("/user/list");
        permissionsMapper.insert(permissions);
        permissionsMapper.insertRolePermission(2, permissions.getId());
    }

    @Test
    public void selectPermissionsByRoleIdTest() {
        List<Permissions> permissions = permissionsMapper.selectPermissionsByRoleId(2);
        permissions.forEach(System.out::println);
    }
}
