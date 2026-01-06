package com.cloud.test.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.test.user.domain.Permissions;
import com.cloud.test.user.dto.RolePermissionsDto;
import com.cloud.test.user.mapper.PermissionsMapper;
import com.cloud.test.user.service.IPermissionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IPermissionsServiceImpl extends ServiceImpl<PermissionsMapper, Permissions> implements IPermissionsService {

    @Autowired
    public PermissionsMapper permissionsMapper;

    @Override
    public boolean saveRolePermissions(RolePermissionsDto rolePermissionsDto) {
        try {
            Integer roleId = rolePermissionsDto.getRoleId();
            List<Integer> permissionIds = rolePermissionsDto.getPermissionIds();

            // 删除现在角色关联的所有权限
            permissionsMapper.deleteRoleWithPermission(roleId);

            // 添加最新的角色与权限关系
            permissionIds.forEach(permissionId -> {
                permissionsMapper.insertRolePermission(roleId, permissionId);
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Permissions> getPermissionsByRoleId(Integer roleId) {
        return permissionsMapper.selectPermissionsByRoleId((roleId));
    }
}
