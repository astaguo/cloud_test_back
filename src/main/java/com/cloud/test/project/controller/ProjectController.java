package com.cloud.test.project.controller;

import com.cloud.test.base.utils.AjaxResult;
import com.cloud.test.project.domain.Project;
import com.cloud.test.project.service.IProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "项目控制器",description = "项目操作接口")
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    public IProjectService projectService;

    @Operation(summary = "保存和更新",description = "这是保存和更新的方法")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public AjaxResult<Void> saveOrUpdate(@RequestBody Project project) {
        return AjaxResult.<Void>me().setSuccess(projectService.saveOrUpdate(project));
    }

    @Operation(summary = "删除",description = "这是删除的方法")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public AjaxResult<Void> remove(@PathVariable("id") Integer id) {
        return AjaxResult.<Void>me().setSuccess(projectService.removeById(id));
    }

    @Operation(summary = "通过id查询",description = "通过id查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AjaxResult<Project> getDataById(@PathVariable("id") Integer id) {
        return AjaxResult.<Project>me().setResultObj(projectService.getById(id));
    }

    @Operation(summary = "查询所有数据",description = "查询所有数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public AjaxResult<List<Project>> getDataList() {
        return AjaxResult.<List<Project>>me().setResultObj(projectService.list());
    }
}
