package com.cloud.test.project.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(title = "用例类")
@Data
@TableName("t_cases")
public class Case {
    @Schema(title = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(title = "用例名称")
    private String name;

    @Schema(title = "用例描述")
    private String description;

    @Schema(title = "用例优先级")
    private String priority;

    @Schema(title = "模块id")
    @TableField(value = "module_id")
    private Integer moduleId;

    @Schema(title = "请求地址")
    private String url;

    @Schema(title = "请求地址")
    @TableField(value = "request_type")
    private String requestType;

    @Schema(title = "请求头")
    @TableField(value = "request_header")
    private String requestHeader;

    @Schema(title = "请求参数")
    @TableField(value = "request_params")
    private String requestParams;

    @Schema(title = "请求体")
    @TableField(value = "request_body")
    private String requestBody;

    @Schema(title = "断言状态")
    @TableField(value = "expect_state")
    private String expectState;

    @Schema(title = "后置脚本")
    @TableField(value = "post_script")
    private String postScript;

    @Schema(title = "前置脚本")
    @TableField(value = "pre_script")
    private String preScript;

    @Schema(title = "测试结果， 1: Pass 2: Fail 0: Null")
    @TableField(value = "test_result")
    private int testResult;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 时间格式化注解,返回给前端的样式
    @TableField(value = "create_time", fill = FieldFill.INSERT)  // 注明此属性不是数据库的字段, 但在项目中必须使用,这样在新增等bean的时候就会忽略
    @Schema(title = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 时间格式化注解,返回给前端的样式
    @TableField(value = "update_time", fill = FieldFill.INSERT)  // 注明此属性不是数据库的字段, 但在项目中必须使用,这样在新增等bean的时候就会忽略
    @Schema(title = "更新时间")
    private Date updateTime;
}
