package com.cloud.test.user.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(title = "权限类")
@Data
@TableName("t_permissions")
public class Permissions {
    @Schema(title = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(title = "权限名称")
    private String name;

    @Schema(title = "路径")
    private String path;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 时间格式化注解,返回给前端的样式
    @TableField(value = "create_time", fill = FieldFill.INSERT)  // 注明此属性不是数据库的字段, 但在项目中必须使用,这样在新增等bean的时候就会忽略
    @Schema(title = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // 时间格式化注解,返回给前端的样式
    @TableField(value = "update_time", fill = FieldFill.INSERT)  // 注明此属性不是数据库的字段, 但在项目中必须使用,这样在新增等bean的时候就会忽略
    @Schema(title = "更新时间")
    private Date updateTime;
}
