package com.cloud.test.user.controller;

import com.cloud.test.base.utils.AjaxResult;
import com.cloud.test.user.domain.Role;
import com.cloud.test.user.service.IRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "角色控制器",description = "角色操作接口")
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    public IRoleService roleService;

    @Operation(summary = "保存和更新",description = "这是保存和更新的方法")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public AjaxResult saveOrUpdate(@RequestBody Role role) {
        return AjaxResult.me().setSuccess(roleService.saveOrUpdate(role));
    }

    @Operation(summary = "删除",description = "这是删除的方法")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public AjaxResult remove(@PathVariable("id") Integer id) {
        return AjaxResult.me().setSuccess(roleService.removeById(id));
    }

    @Operation(summary = "通过id查询",description = "通过id查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AjaxResult getDataById(@PathVariable("id") Integer id) {
        return AjaxResult.me().setResultObj(roleService.getById(id));
    }

    @Operation(summary = "查询所有数据",description = "查询所有数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public AjaxResult getDataList() {
        return AjaxResult.me().setResultObj(roleService.list());
    }
}
