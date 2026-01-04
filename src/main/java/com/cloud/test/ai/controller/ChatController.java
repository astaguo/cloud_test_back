package com.cloud.test.ai.controller;

import com.cloud.test.ai.dto.ChatMessageDto;
import com.cloud.test.ai.service.IChatService;
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
    public Flux<String> sendMessage(@RequestBody ChatMessageDto messageDto) {
        return chatService.sendMessage(messageDto);
    }
}
