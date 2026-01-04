package com.cloud.test.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.test.ai.domain.Conversation;

import java.util.List;

public interface IConversationService extends IService<Conversation> {
    List<Conversation> getDataListByUserId(Integer userId);
}
