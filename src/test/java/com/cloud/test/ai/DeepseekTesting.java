package com.cloud.test.ai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest
public class DeepseekTesting {

    @Value("${spring.ai.deepseek.api-key}")
    private String apiKey;

    @Test
    public void chatTest () {
        DeepSeekApi deepSeekApi = DeepSeekApi.builder().apiKey(apiKey).build();
        DeepSeekApi.ChatCompletionMessage chatCompletionMessage = new DeepSeekApi.ChatCompletionMessage("Hello world", DeepSeekApi.ChatCompletionMessage.Role.USER);
        ResponseEntity<DeepSeekApi.ChatCompletion> response = deepSeekApi.chatCompletionEntity(
                new DeepSeekApi.ChatCompletionRequest(List.of(chatCompletionMessage), DeepSeekApi.ChatModel.DEEPSEEK_CHAT.value, 1D, false));

        System.out.println(response.getBody());
    }
}
