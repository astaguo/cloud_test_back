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
}
