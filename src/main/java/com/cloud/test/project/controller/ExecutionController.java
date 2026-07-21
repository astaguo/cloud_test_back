package com.cloud.test.project.controller;

import com.cloud.test.base.utils.AjaxResult;
import com.cloud.test.project.domain.Execution;
import com.cloud.test.project.service.IExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "执行记录控制器",description = "执行记录操作接口")
@RestController
@RequestMapping("/execution")
public class ExecutionController {

    @Autowired
    public IExecutionService executionService;

    @Operation(summary = "保存和更新",description = "这是保存和更新的方法")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public AjaxResult<Void> saveOrUpdate(@RequestBody Execution obj) {
        return AjaxResult.<Void>me().setSuccess(executionService.saveOrUpdate(obj));
    }

    @Operation(summary = "删除",description = "这是删除的方法")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public AjaxResult<Void> remove(@PathVariable("id") Integer id) {
        return AjaxResult.<Void>me().setSuccess(executionService.removeById(id));
    }

    @Operation(summary = "通过id查询",description = "通过id查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AjaxResult<Execution> getDataById(@PathVariable("id") Integer id) {
        return AjaxResult.<Execution>me().setResultObj(executionService.getById(id));
    }

    @Operation(summary = "查询所有数据",description = "查询所有数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public AjaxResult<List<Execution>> getDataList() {
        return AjaxResult.<List<Execution>>me().setResultObj(executionService.list());
    }

    @Operation(summary = "查询所有数据",description = "查询所有数据")
    @RequestMapping(value = "/list/{caseId}", method = RequestMethod.GET)
    public AjaxResult<List<Execution>> getDataListByCaseList(@PathVariable("caseId") Integer caseId) {
        return AjaxResult.<List<Execution>>me().setResultObj(executionService.listByCaseId(caseId));
    }
}
