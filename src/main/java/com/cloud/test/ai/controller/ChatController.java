package com.cloud.test.ai.controller;

import com.cloud.test.ai.dto.SendDto;
import com.cloud.test.ai.service.IChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@Tag(name = "AI接口",description = "AI操作接口")
@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    public IChatService chatService;

    @PostMapping("/send")
    @Operation(summary = "发送AI消息",description = "发送AI消息")
    public Flux<String> send(@RequestBody SendDto sendDto) {
        return chatService.send(sendDto);
    }
}
