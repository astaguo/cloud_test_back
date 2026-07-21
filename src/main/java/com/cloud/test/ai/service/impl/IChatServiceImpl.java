package com.cloud.test.ai.service.impl;

import com.cloud.test.ai.domain.Message;
import com.cloud.test.ai.dto.SendDto;
import com.cloud.test.ai.mapper.MessageMapper;
import com.cloud.test.ai.service.IChatService;
import com.cloud.test.ai.tools.WeatherTool;
import com.cloud.test.ai.tools.WebPageFetcherTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class IChatServiceImpl implements IChatService {

    @Autowired
    private RedisVectorStore redisVectorStore;

    @Autowired
    private SyncMcpToolCallbackProvider toolCallbackProvider;

    @Autowired
    @Qualifier("deepSeekChatClient")
    public ChatClient deepSeekChatClient;

    @Autowired
    @Qualifier("zhiPuChatClient")
    public ChatClient zhiPuChatClient;

    @Autowired
    private MessageMapper messageMapper;

    ChatMemory chatMemory;

    public IChatServiceImpl(JdbcChatMemoryRepository chatMemoryRepository) {
        chatMemory = MessageWindowChatMemory
                .builder()
                .maxMessages(20)  // 设置存储为上面我们传的变量的 jdbc 的存储方式
                .chatMemoryRepository(chatMemoryRepository).build();
    }

    @Override
    public Flux<String> send(SendDto sendDto) {
        // 1.验证参数
        Objects.requireNonNull(sendDto, "参数不能为空。");
        Objects.requireNonNull(sendDto.getAiMessageId(), "ai消息id不能为空。");
        Objects.requireNonNull(sendDto.getAiType(), "aiType参数不能为空。");
        Objects.requireNonNull(sendDto.getEnableRAG(), "enableRAG参数不能为空。");
        Objects.requireNonNull(sendDto.getUserMessageId(), "ai消息id不能为空。");

        // 2.获取用户发送的消息
        var userMessageId = sendDto.getUserMessageId();
        var userMessage = messageMapper.selectById(userMessageId);
        var conversationId = userMessage.getConversationId().toString();
        chatMemory.add(conversationId, new UserMessage(userMessage.getContent()));

        // 3.区分用户使用的是那个ai
        var client1 = switch (sendDto.getAiType()) {
            case 2 -> zhiPuChatClient;
            default -> deepSeekChatClient;
        };
        var client2 = client1.prompt(new Prompt(chatMemory.get(conversationId)))
                .toolCallbacks(combineToolCallback());

        // 4.是否使用RAG
        if (sendDto.getEnableRAG().equals(("1"))) {
            client2.advisors(QuestionAnswerAdvisor.builder(redisVectorStore).build());
        }

        // 5.处理接收流数据
        // 收集流逝数据
        var fullResponse = new CopyOnWriteArrayList<String>();
        return client2
                .stream().content()
                .doOnNext(fullResponse::add)
                .doOnComplete(() -> {
                    String fullAnswer = String.join("", fullResponse);
                    System.out.println("✅ 对话已保存：" + fullAnswer);
                    chatMemory.add(conversationId, new AssistantMessage(fullAnswer));
                    // 5.1查询ai消息
                    Message aiMessage = messageMapper.selectById(sendDto.getAiMessageId());
                    aiMessage.setContent(fullAnswer);
                    // 5.2更新数据库中的ai消息
                    messageMapper.insertOrUpdate(aiMessage);
                });
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
