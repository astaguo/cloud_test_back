package com.cloud.test.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.test.domain.Conversation;
import com.cloud.test.domain.Message;
import com.cloud.test.service.impl.IConversationImpl;
import com.cloud.test.service.impl.IMessageImpl;
import com.cloud.test.utils.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "对话控制器",description = "对话操作接口")
@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Autowired
    public IConversationImpl conversationService;

    @Autowired
    public IMessageImpl messageService;

    @Operation(summary = "保存和更新",description = "这是保存和更新的方法")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public AjaxResult saveOrUpdate(@RequestBody Conversation conversation) {
        conversationService.saveOrUpdate(conversation);
        return AjaxResult.me().setResultObj(conversationService.getById(conversation.getId()));
    }

    @Operation(summary = "删除",description = "这是删除的方法")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public AjaxResult remove(@PathVariable("id") Integer id) {
        // 1.需要判断子表是否有数据 t_message
        LambdaQueryWrapper<Message> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.eq(Message::getConversationId, id);
        messageService.remove(messageWrapper);

        // 2.删除conversation
        conversationService.removeById(id);
        return AjaxResult.me();
    }

    @Operation(summary = "通过id查询",description = "通过id查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AjaxResult getDataById(@PathVariable("id") Integer id) {
        return AjaxResult.me().setResultObj(conversationService.getById(id));
    }

    @Operation(summary = "查询所有数据",description = "查询所有数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public AjaxResult getDataList() {
        return AjaxResult.me().setResultObj(conversationService.list());
    }

    /*
    * 复杂查询
    * */
    @Operation(summary = "根据用户id查询数据", description = "根据用户id查询数据")
    @GetMapping(value = "/list/{userId}")
    public AjaxResult getDataListByUserId(@PathVariable("userId") Integer userId) {
        return AjaxResult.me().setResultObj(conversationService.getDataListByUserId(userId));
    }
}
