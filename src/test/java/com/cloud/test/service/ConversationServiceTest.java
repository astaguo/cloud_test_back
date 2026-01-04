package com.cloud.test.service;

import com.cloud.test.ai.domain.Conversation;
import com.cloud.test.ai.service.IConversationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ConversationServiceTest {

    @Autowired
    private IConversationService conversationService;

    @Test
    public void test() {
        List<Conversation> list = conversationService.getDataListByUserId(6);
        for (Conversation conversation : list) {
            System.out.println("===================================");
            System.out.println(conversation.toString());
        }
    }
}
