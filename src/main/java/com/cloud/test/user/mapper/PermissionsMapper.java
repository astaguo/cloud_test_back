package com.cloud.test.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.test.user.domain.Permissions;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PermissionsMapper extends BaseMapper<Permissions> {
    @Select("SELECT p.* FROM t_permissions p LEFT JOIN t_role_permissions rp ON p.id = rp.permissions_id WHERE rp.role_id = #{roleId}")
    List<Permissions> selectPermissionsByRoleId(Integer roleId);

    // 添加中间表的数据
    @Insert("INSERT INTO t_role_permissions (role_id, permissions_id) VALUES (#{roleId}, #{permId})")
    void insertRolePermission(@Param("roleId") Integer roleId, @Param("permId") Integer permId);
}
