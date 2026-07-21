package com.cloud.test.ai.controller;

import com.cloud.test.ai.domain.Message;
import com.cloud.test.ai.service.IMessageService;
import com.cloud.test.base.utils.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "消息控制器",description = "消息操作接口")
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    public IMessageService messageService;

    @Operation(summary = "保存和更新",description = "这是保存和更新的方法")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public AjaxResult<Message> saveOrUpdate(@RequestBody Message conversation) {
        return AjaxResult.<Message>me().setSuccess(messageService.saveOrUpdate(conversation)).setResultObj(conversation);
    }

    @Operation(summary = "删除",description = "这是删除的方法")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public AjaxResult<Void> remove(@PathVariable("id") Integer id) {
        return AjaxResult.<Void>me().setSuccess(messageService.removeById(id));
    }

    @Operation(summary = "通过id查询",description = "通过id查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AjaxResult<Message> getDataById(@PathVariable("id") Integer id) {
        return AjaxResult.<Message>me().setResultObj(messageService.getById(id));
    }

    @Operation(summary = "查询所有数据",description = "查询所有数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public AjaxResult<List<Message>> getDataList() {
        return AjaxResult.<List<Message>>me().setResultObj(messageService.list());
    }

    @Operation(summary = "根据对话id获取数据", description = "根据对话id获取数据")
    @GetMapping(value = "/list/{conversationId}")
    public AjaxResult<List<Message>> getDataListByConversationId(@PathVariable("conversationId") Integer conversationId) {
        return AjaxResult.<List<Message>>me().setResultObj(messageService.getDataListByConversationId(conversationId));
    }
}
