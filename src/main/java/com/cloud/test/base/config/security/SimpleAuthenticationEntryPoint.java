package com.cloud.test.base.config.security;

import com.cloud.test.base.utils.AjaxResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 3.1 401 未授权状态
 * HTTP 401 错误 - 未授权(Unauthorized) 一般来说该错误消息表明您首先需要登录（输入有效的用户名和密码）。
 * 如果你刚刚输入这些信息，立刻就看到一个 401 错误，就意味着，无论出于何种原因您的用户名和密码其中之一或两者都无效（输入有误，用户名暂时停用，账户被锁定，凭证失效等） 。
 * 总之就是认证失败了。其实正好对应我们上面的 AuthenticationException 。
 */
public class SimpleAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // 设置返回的header
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();

        // 设置需要响应的内容
        String resBody = objectMapper.writeValueAsString(AjaxResult.me().setCode(HttpServletResponse.SC_UNAUTHORIZED).setMessage(authException.getMessage()));
        PrintWriter printWriter = response.getWriter();
        printWriter.print(resBody);
        printWriter.flush();
        printWriter.close();
    }
}
