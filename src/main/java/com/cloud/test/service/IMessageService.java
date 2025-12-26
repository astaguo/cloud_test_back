package com.cloud.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.test.domain.Message;

import java.util.List;

public interface IMessageService extends IService<Message> {

    List<Message> getDataListByConversationId(Integer conversationId);
}
