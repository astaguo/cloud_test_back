package com.cloud.test.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.test.user.domain.Role;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {

    List<Role> selectRoleByPath(String path);

    Role selectRoleByUserName(String username);
}
