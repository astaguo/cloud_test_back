package com.cloud.test.project.controller;

import com.cloud.test.base.utils.AjaxResult;
import com.cloud.test.project.domain.Case;
import com.cloud.test.project.dto.RequestInfoDto;
import com.cloud.test.project.service.ICaseService;
import com.cloud.test.project.vo.ResponseVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "用例控制器",description = "用例操作接口")
@RestController
@RequestMapping("/case")
public class CaseController {

    @Autowired
    public ICaseService caseService;

    @Operation(summary = "保存和更新",description = "这是保存和更新的方法")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public AjaxResult<Void> saveOrUpdate(@RequestBody Case obj) {
        return AjaxResult.<Void>me().setSuccess(caseService.saveOrUpdate(obj));
    }

    @Operation(summary = "删除",description = "这是删除的方法")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public AjaxResult<Void> remove(@PathVariable("id") Integer id) {
        return AjaxResult.<Void>me().setSuccess(caseService.removeById(id));
    }

    @Operation(summary = "通过id查询",description = "通过id查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AjaxResult<Case> getDataById(@PathVariable("id") Integer id) {
        return AjaxResult.<Case>me().setResultObj(caseService.getById(id));
    }

    @Operation(summary = "查询所有数据",description = "查询所有数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public AjaxResult<List<Case>> getDataList() {
        return AjaxResult.<List<Case>>me().setResultObj(caseService.list());
    }

    @Operation(summary = "发送请求", description = "发送接口请求")
    @RequestMapping(value = "/request", method = RequestMethod.POST)
    public AjaxResult<ResponseVO<Object>> sendRequest(@RequestBody RequestInfoDto requestInfoDto) {
        return AjaxResult.<ResponseVO<Object>>me().setResultObj(caseService.sendRequest(requestInfoDto));
    }

    @Operation(summary = "执行用例", description = "通过用例id执行用例")
    @RequestMapping(value = "/execution/{id}", method = RequestMethod.POST)
    public AjaxResult<Void> executionCase(@PathVariable("id") Integer id) {
        return AjaxResult.<Void>me().setSuccess(caseService.executionCase(id));
    }
}
