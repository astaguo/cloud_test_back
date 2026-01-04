package com.cloud.test.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.test.ai.domain.Message;

import java.util.List;

public interface IMessageService extends IService<Message> {

    List<Message> getDataListByConversationId(Integer conversationId);
}
