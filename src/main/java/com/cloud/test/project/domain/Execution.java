package com.cloud.test.project.domain;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(title = "执行记录类")
@Data
@TableName("t_execution")
public class Execution {
    @Schema(title = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(title = "用例id")
    @TableField(value = "case_id")
    private Integer caseId;

    @Schema(title = "测试结果 1: Pass 2: Fail")
    private Integer result;

    @Schema(title = "响应头")
    @TableField(value = "headers")
    private String headers;

    @Schema(title = "响应内容")
    @TableField(value = "body")
    private String body;

    @Schema(title = "响应状态")
    @TableField(value = "status_code")
    private String statusCode;

    @Schema(title = "执行时间")
    @TableField(value = "execution_time")
    private String executionTime;

    @Schema(title = "错误信息")
    @TableField(value = "error")
    private String error;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 时间格式化注解,返回给前端的样式
    @TableField(value = "create_time", fill = FieldFill.INSERT)  // 注明此属性不是数据库的字段, 但在项目中必须使用,这样在新增等bean的时候就会忽略
    @Schema(title = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 时间格式化注解,返回给前端的样式
    @TableField(value = "update_time", fill = FieldFill.INSERT)  // 注明此属性不是数据库的字段, 但在项目中必须使用,这样在新增等bean的时候就会忽略
    @Schema(title = "更新时间")
    private Date updateTime;
}
