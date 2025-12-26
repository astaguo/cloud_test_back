package com.cloud.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.test.domain.Message;
import com.cloud.test.mapper.MessageMapper;
import com.cloud.test.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IMessageImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public List<Message> getDataListByConversationId(Integer conversationId) {
        Wrapper<Message> queryWrapper = new QueryWrapper<Message>().eq("conversation_id", conversationId);
        return messageMapper.selectList(queryWrapper);
    }
}
