package com.cloud.test.exceptions.security;

import com.cloud.test.utils.AjaxResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 3.2 403 被拒绝状态
 * HTTP 403 错误 - 被禁止(Forbidden) 出现该错误表明您在访问受限资源时没有得到许可。
 * 服务器理解了本次请求但是拒绝执行该任务，该请求不该重发给服务器。
 * 并且服务器想让客户端知道为什么没有权限访问特定的资源，服务器应该在返回的信息中描述拒绝的理由。
 * 一般实践中我们会比较模糊的表明原因。该错误对应了我们上面的 AccessDeniedException 。
 */
public class SimpleAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // 设置返回的header
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);


        PrintWriter printWriter = response.getWriter();
        printWriter.print(AjaxResult.me().setCode(HttpServletResponse.SC_FORBIDDEN).setMessage(accessDeniedException.getMessage()));
        printWriter.flush();
        printWriter.close();
    }
}
