package com.cloud.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "用户注册Dto")
public class RegisterDto {
    @Schema(title = "用户名")
    private String username;
    @Schema(title = "邮箱")
    private String email;
    @Schema(title = "密码")
    private String password;
}
