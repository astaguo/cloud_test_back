package com.cloud.test.project.controller;

import com.cloud.test.base.utils.AjaxResult;
import com.cloud.test.project.dto.AnalyzeRequest;
import com.cloud.test.project.vo.ImpactResult;
import com.cloud.test.project.service.IImpactAnalysisService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "分析影响模块",description = "分析影响模块操作接口")
@RestController
@RequestMapping("/analyzer")
public class AnalyzerController {

    private final IImpactAnalysisService impactAnalysisService;

    public AnalyzerController(IImpactAnalysisService impactAnalysisService) {
        this.impactAnalysisService = impactAnalysisService;
    }

    @Operation(summary = "通过Github项目地址获取影响的范围",description = "通过Github项目地址获取影响的范围")
    @PostMapping("/analyze")
    public AjaxResult<ImpactResult> analyze(@RequestBody AnalyzeRequest request) {
        ImpactResult result = impactAnalysisService.analyze(request.getRepoUrl());
        return AjaxResult.<ImpactResult>me().setResultObj(result);
    }
}
