package com.cloud.test.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cloud.test.domain.Conversation;

import java.util.List;

public interface IConversationService extends IService<Conversation> {
    List<Conversation> getDataListByUserId(Integer userId);
}
