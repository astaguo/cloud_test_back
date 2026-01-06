package com.cloud.test.user.controller;

import com.cloud.test.base.utils.AjaxResult;
import com.cloud.test.user.domain.Permissions;
import com.cloud.test.user.dto.RolePermissionsDto;
import com.cloud.test.user.service.IPermissionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "权限控制器",description = "权限操作接口")
@RestController
@RequestMapping("/permissions")
public class PermissionsController {

    @Autowired
    public IPermissionsService permissionsService;

    @Operation(summary = "保存和更新",description = "这是保存和更新的方法")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public AjaxResult<Void> saveOrUpdate(@RequestBody Permissions permissions) {
        return AjaxResult.<Void>me().setSuccess(permissionsService.saveOrUpdate(permissions));
    }

    @Operation(summary = "删除",description = "这是删除的方法")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public AjaxResult<Void> remove(@PathVariable("id") Integer id) {
        return AjaxResult.<Void>me().setSuccess(permissionsService.removeById(id));
    }

    @Operation(summary = "通过id查询",description = "通过id查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AjaxResult<Permissions> getDataById(@PathVariable("id") Integer id) {
        return AjaxResult.<Permissions>me().setResultObj(permissionsService.getById(id));
    }

    @Operation(summary = "查询所有数据",description = "查询所有数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public AjaxResult<List<Permissions>> getDataList() {
        return AjaxResult.<List<Permissions>>me().setResultObj(permissionsService.list());
    }

    @Operation(summary = "通过角色id查询权限", description = "通过角色id查询权限")
    @GetMapping(value = "/getPermissionsByRoleId/{roleId}")
    public AjaxResult<List<Permissions>> getPermissionsByRoleId(@PathVariable("roleId") Integer roleId) {
        return AjaxResult.<List<Permissions>>me().setResultObj(permissionsService.getPermissionsByRoleId(roleId));
    }

    @Operation(summary = "保存role和permission中间表", description = "保存role和permission中间表")
    @PostMapping(value = "/saveRolePermissions")
    public AjaxResult<Void> saveRolePermissions(@RequestBody RolePermissionsDto rolePermissionsDto) {
        return AjaxResult.<Void>me().setSuccess(permissionsService.saveRolePermissions(rolePermissionsDto));
    };
}
