package com.cloud.test.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "代码分析请求对象")
public class AnalyzeRequest {
    @Schema(description = "Git仓库地址", example = "https://github.com/user/repo.git")
    private String repoUrl;
}
