package com.cloud.test.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "发送AI的Dto")
public class SendDto {
    @Schema(title = "用户消息id")
    private Integer userMessageId;

    @Schema(title = "Ai消息id")
    private Integer aiMessageId;

    @Schema(title = "是否启用RAG 1: 启用 0: 禁用")
    private String enableRAG = "0";

    @Schema(title = "调用Ai的类型， 1是Deepseek，2是智谱")
    private Integer aiType = 1;
}
