package com.cloud.test.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.test.user.domain.Permissions;
import com.cloud.test.user.dto.RolePermissionsDto;

import java.util.List;

public interface IPermissionsService extends IService<Permissions> {
    boolean saveRolePermissions(RolePermissionsDto rolePermissionsDto);

    List<Permissions> getPermissionsByRoleId(Integer roleId);
}
