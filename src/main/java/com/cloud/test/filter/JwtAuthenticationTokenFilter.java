package com.cloud.test.filter;

import com.cloud.test.service.impl.AutoUserDetailsService;
import com.cloud.test.utils.AjaxResult;
import com.cloud.test.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AutoUserDetailsService autoUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取请求头中的token
        final String requestTokenHeader = request.getHeader("Authorization");
        System.out.println("前端的token信息=======>"+requestTokenHeader);

        String username = null;
        String jwtToken = null;

        // JWT Token格式: Bearer token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (Exception e) {
                logger.error("JWT Token解析失败", e);

                // 设置返回的header
                response.setStatus(HttpServletResponse.SC_OK);
                response.setCharacterEncoding("utf-8");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                ObjectMapper objectMapper = new ObjectMapper();

                // 设置需要响应的内容
                String resBody = objectMapper.writeValueAsString(AjaxResult.me().setCode(HttpServletResponse.SC_UNAUTHORIZED).setMessage("Token已过期或无效"));
                PrintWriter printWriter = response.getWriter();
                printWriter.print(resBody);
                printWriter.flush();
                printWriter.close();

                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = autoUserDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request,response);
    }
}
