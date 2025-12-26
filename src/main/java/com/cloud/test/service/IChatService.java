package com.cloud.test.service;

import com.cloud.test.dto.ChatMessageDto;
import reactor.core.publisher.Flux;

public interface IChatService {
    Flux<String> sendMessage(ChatMessageDto messageDto);
}
