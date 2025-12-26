package com.cloud.test.controller;

import com.cloud.test.domain.Message;
import com.cloud.test.service.IMessageService;
import com.cloud.test.utils.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "消息控制器",description = "消息操作接口")
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    public IMessageService messageService;

    @Operation(summary = "保存和更新",description = "这是保存和更新的方法")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public AjaxResult saveOrUpdate(@RequestBody Message conversation) {
        messageService.saveOrUpdate(conversation);
        return AjaxResult.me();
    }

    @Operation(summary = "删除",description = "这是删除的方法")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public AjaxResult remove(@PathVariable("id") Integer id) {
        messageService.removeById(id);
        return AjaxResult.me();
    }

    @Operation(summary = "通过id查询",description = "通过id查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AjaxResult getDataById(@PathVariable("id") Integer id) {
        return AjaxResult.me().setResultObj(messageService.list());
    }

    @Operation(summary = "查询所有数据",description = "查询所有数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public AjaxResult getDataList() {
        return AjaxResult.me().setResultObj(messageService.list());
    }

    @Operation(summary = "根据对话id获取数据", description = "根据对话id获取数据")
    @GetMapping(value = "/list/{conversationId}")
    public AjaxResult getDataListByConversationId(@PathVariable("conversationId") Integer conversationId) {
        return AjaxResult.me().setResultObj(messageService.getDataListByConversationId(conversationId));
    }
}
