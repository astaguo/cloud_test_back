package com.cloud.test.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.test.ai.domain.Conversation;
import com.cloud.test.ai.mapper.ConversationMapper;
import com.cloud.test.ai.service.IConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IConversationImpl extends ServiceImpl<ConversationMapper, Conversation> implements IConversationService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Override
    public List<Conversation> getDataListByUserId(Integer userId) {
        Wrapper<Conversation> queryWrapper = new QueryWrapper<Conversation>().eq("user_id", userId);
        return conversationMapper.selectList(queryWrapper);
    }
}
