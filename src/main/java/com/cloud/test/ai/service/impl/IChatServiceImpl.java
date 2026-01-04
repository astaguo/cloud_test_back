package com.cloud.test.ai.service.impl;

import com.cloud.test.ai.domain.Message;
import com.cloud.test.ai.dto.ChatMessageDto;
import com.cloud.test.ai.mapper.MessageMapper;
import com.cloud.test.ai.service.IChatService;
import com.cloud.test.ai.tools.WeatherTool;
import com.cloud.test.ai.tools.WebPageFetcherTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Objects;

@Service
public class IChatServiceImpl implements IChatService {

    private final ChatClient chatClient;

    @Autowired
    private RedisVectorStore redisVectorStore;

    @Autowired
    private SyncMcpToolCallbackProvider toolCallbackProvider;

    @Autowired
    private MessageMapper messageMapper;

    public IChatServiceImpl(ChatClient.Builder chatClientBuilder, JdbcChatMemoryRepository chatMemoryRepository) {
        ChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .maxMessages(20)  // 设置存储为上面我们传的变量的 jdbc 的存储方式
                .chatMemoryRepository(chatMemoryRepository).build();

        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        PromptChatMemoryAdvisor
                                .builder(chatMemory)
                                .build()
                )
                .build();;
    }

    @Override
    public Flux<String> sendMessage(ChatMessageDto messageDto) {
        /* 参数检查 */
        Objects.requireNonNull(messageDto, "参数不能为空。");
        Objects.requireNonNull(messageDto.getMessage(), "message不能为空。");
        Objects.requireNonNull(messageDto.getConversationId(), "conversationId参数不能为空。");

        String message = messageDto.getMessage();
        Integer conversationId = messageDto.getConversationId();

        // 创建并保存用户发送的消息
        Message userMessage = createMessage(conversationId, message, "USER");
        messageMapper.insert(userMessage);

        /*接受消息，并保存到DB*/
        Flux<String> originalFlux;
        if(messageDto.getEnableRAG().equals(("1"))) {
            // 启用RAG
            originalFlux = this.chatClient.prompt()
                    .user(message)
                    .toolCallbacks(combineToolCallback())
                    .advisors(new QuestionAnswerAdvisor(redisVectorStore))
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .stream()
                    .content();
        } else {
            // 禁用RAG
            originalFlux = this.chatClient.prompt()
                    .user(message)
                    .toolCallbacks(combineToolCallback())
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .stream()
                    .content();
        }

        /*
            处理异步情况下，即给前段返回流式输出， 也不影响我们最终存储数据
            利用 cache 或 replay 实现多订阅（更安全）
        */
        // 使用 cache() 让 Flux 可被多订阅（缓存所有元素）
        Flux<String> cachedFlux = originalFlux.cache();

        // 第二个订阅：收集所有元素并存储（并行执行，不阻塞前端响应）
        cachedFlux
                .collectList() // 收集所有元素到 List
                .subscribeOn(Schedulers.boundedElastic()) // 切换线程执行存储
                .subscribe(allContent -> {
                    Message aiMessage = createMessage(conversationId, String.join("", allContent),"ASSISTANT");
                    messageMapper.insert(aiMessage);
                });

        // 第一个订阅：返回给前端, 返回给前段
        return cachedFlux;
    }

    private Message createMessage(Integer conversationId, String content, String role) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setContent(content);
        message.setRole(role);
        return message;
    }

    private ToolCallback[] combineToolCallback() {
        /* 工具调用 & MCP */
        // 1. 获取MCP工具的ToolCallback数组
        ToolCallback[] mcpToolCallbacks = toolCallbackProvider.getToolCallbacks();

        // 2. 获取自定义工具的ToolCallback数组 WebPageFetcherTool
        ToolCallback[] customToolCallbacks = ToolCallbacks.from(new WebPageFetcherTool(), new WeatherTool());

        // 3. 合并工具数组
        return java.util.stream.Stream.concat(
                java.util.Arrays.stream(customToolCallbacks),
                java.util.Arrays.stream(mcpToolCallbacks)
        ).toArray(ToolCallback[]::new);
    }
}
