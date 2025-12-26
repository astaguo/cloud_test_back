package com.cloud.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "用户登陆Dto")
public class ChatMessageDto {

    @Schema(title = "对话ID")
    private Integer conversationId;

    @Schema(title = "消息")
    private String message;

    @Schema(title = "是否启用RAG 1: 启用 0: 禁用")
    private String enableRAG = "0";
}
