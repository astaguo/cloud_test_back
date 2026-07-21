package com.cloud.test.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "请求信息")
public class RequestInfoDto {

    @Schema(description = "用例ID")
    private Integer caseId;

    @Schema(description = "请求地址")
    private String url;

    @Schema(description = "请求类型")
    private String requestType;

    @Schema(description = "请求头")
    private Map<String, String> requestHeader;

    @Schema(description = "请求参数 params")
    private Map<String, Object>  requestParams;

    @Schema(description = "请求参数 body")
    private Map<String, Object>  requestBody;

    @Schema(description = "预期状态")
    private String  expectState;

    @Schema(description = "后置脚本")
    private String  postScript;

    @Schema(description = "前置脚本")
    private String  perScript;
}
