package com.cloud.test.ai.service;

import com.cloud.test.ai.dto.ChatMessageDto;
import reactor.core.publisher.Flux;

public interface IChatService {
    Flux<String> sendMessage(ChatMessageDto messageDto);
}
