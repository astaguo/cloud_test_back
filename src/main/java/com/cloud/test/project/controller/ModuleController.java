package com.cloud.test.project.controller;

import com.cloud.test.base.utils.AjaxResult;
import com.cloud.test.project.domain.Module;
import com.cloud.test.project.dto.RefreshModuleDto;
import com.cloud.test.project.service.IModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "模块控制器",description = "模块操作接口")
@RestController
@RequestMapping("/module")
public class ModuleController {

    @Autowired
    public IModuleService moduleService;

    @Operation(summary = "通过项目id获取模块",description = "通过项目id获取模块")
    @GetMapping("/getModuleByProjectId/{projectId}")
    public AjaxResult<List<Module>> getModuleByProjectId(@PathVariable Integer projectId) {
        return AjaxResult.<List<Module>>me().setResultObj(moduleService.getModuleByProjectId(projectId));
    }

    @Operation(summary = "通过项目id刷新模块",description = "通过项目id刷新模块")
    @PostMapping("/refreshModuleByProjectId")
    public AjaxResult<List<Module>> refreshModuleByProjectId(@RequestBody RefreshModuleDto refreshModuleDto) {
        return AjaxResult.<List<Module>>me().setResultObj(moduleService.refreshModuleByProjectId(refreshModuleDto.getProjectId()));
    }

    @Operation(summary = "保存和更新",description = "这是保存和更新的方法")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public AjaxResult<Void> saveOrUpdate(@RequestBody Module project) {
        return AjaxResult.<Void>me().setSuccess(moduleService.saveOrUpdate(project));
    }

    @Operation(summary = "删除",description = "这是删除的方法")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public AjaxResult<Void> remove(@PathVariable("id") Integer id) {
        return AjaxResult.<Void>me().setSuccess(moduleService.removeById(id));
    }

    @Operation(summary = "通过id查询",description = "通过id查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AjaxResult<Module> getDataById(@PathVariable("id") Integer id) {
        return AjaxResult.<Module>me().setResultObj(moduleService.getById(id));
    }

    @Operation(summary = "查询所有数据",description = "查询所有数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public AjaxResult<List<Module>> getDataList() {
        return AjaxResult.<List<Module>>me().setResultObj(moduleService.list());
    }
}
