package com.cloud.test.ai.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(title = "对话消息表")
@Data
@TableName("t_message")
public class Message {

    @Schema(title = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(title = "对话内容")
    private String content;

    @Schema(title = "对话ID")
    @TableField(value = "conversation_id")
    private Integer conversationId;

    @Schema(title = "发送消息的角色")
    private String role;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 时间格式化注解,返回给前端的样式
    @TableField(value = "create_time", fill = FieldFill.INSERT)  // 注明此属性不是数据库的字段, 但在项目中必须使用,这样在新增等bean的时候就会忽略
    @Schema(title = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 时间格式化注解,返回给前端的样式
    @TableField(value = "update_time", fill = FieldFill.INSERT)  // 注明此属性不是数据库的字段, 但在项目中必须使用,这样在新增等bean的时候就会忽略
    @Schema(title = "更新时间")
    private Date updateTime;
}
