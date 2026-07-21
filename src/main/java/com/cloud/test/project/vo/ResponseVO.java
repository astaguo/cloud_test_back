package com.cloud.test.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "响应内容")
public class ResponseVO <T> {
    @Schema(description = "响应头")
    private HttpHeaders headers;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "响应状态")
    private String status;

    @Schema(description = "响应时间")
    private String time;

    @Schema(description = "测试结果")
    private Boolean testResult;

    @Schema(description = "失败返回的信息")
    private String errorMsg;
}
