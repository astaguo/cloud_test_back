package com.cloud.test.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "用户注册Dto")
public class RolePermissionsDto {
    @Schema(title = "角色ID")
    private Integer roleId;

    @Schema(title = "权限ID列表")
    private List<Integer> permissionIds;
}
