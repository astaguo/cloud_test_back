package com.cloud.test.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "用户登陆Dto")
public class LoginUserDto {
    @Schema(title = "用户名")
    private String username;
    @Schema(title = "密码")
    private String password;
}
