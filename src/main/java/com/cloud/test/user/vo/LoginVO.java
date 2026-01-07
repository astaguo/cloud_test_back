package com.cloud.test.user.vo;

import com.cloud.test.user.domain.AuthUserDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "登陆的返回数据类型 VO")
public class LoginVO {
    @Schema(title = "token")
    private String token;
    @Schema(title = "用户信息")
    private AuthUserDetails user;
}
