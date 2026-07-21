package com.cloud.test.ai.service;

import com.cloud.test.ai.dto.SendDto;
import reactor.core.publisher.Flux;

public interface IChatService {
    Flux<String> send(SendDto sendDto);
}
