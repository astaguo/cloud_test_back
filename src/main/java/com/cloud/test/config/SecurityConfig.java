package com.cloud.test.config;

import com.cloud.test.exceptions.security.SimpleAccessDeniedHandler;
import com.cloud.test.exceptions.security.SimpleAuthenticationEntryPoint;
import com.cloud.test.filter.JwtAuthenticationTokenFilter;
import com.cloud.test.service.impl.AutoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity //开启webSecurity服务
public class SecurityConfig {

    @Autowired
    private AutoUserDetailsService autoUserDetailsService;

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        // Spring security的新特性， 直接传userDetailsService就可以了
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(autoUserDetailsService);
        //将使用的密码编译器加入进来
        provider.setPasswordEncoder(passwordEncoder);
        //将provider放置到AuthenticationManager 中
        return new ProviderManager(provider);
    }


    /*
     * 配置权限相关的配置
     * 安全框架本质上是一堆的过滤器，称之为过滤器链，每一个过滤器链的功能都不同
     * 设置一些链接不要拦截
     * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // 1. 启用 CORS（使用上面配置的 CorsFilter）
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 2. 关闭 CSRF（前后端分离项目通常关闭，根据实际情况调整）
                .csrf(AbstractHttpConfigurer::disable)
                // 3. 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // 允许所有 OPTIONS 方法（预检请求）
                        .requestMatchers(request -> "OPTIONS".equals(request.getMethod())).permitAll()
                        // 其他请求规则（示例：放行登录接口，其他需要认证）
                        .requestMatchers("/user/login", "/user/register", "/webjars/**", "/v3/api-docs/**", "/doc.html", "/ai/*").permitAll()
                        .anyRequest().authenticated()
                )
                // 4. 自定义过滤器放在UsernamePasswordAuthenticationFilter过滤器之前
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // 5. 自定义异常 (6.1+版本)
                .exceptionHandling(exceptionHandling -> {
                    // 1. 配置未认证/认证失败的异常处理（如未登录访问受保护资源）
                    exceptionHandling.authenticationEntryPoint(new SimpleAuthenticationEntryPoint());
                     //2. 配置权限不足的异常处理（如已登录但无权限或者token失效）
                    exceptionHandling.accessDeniedHandler(new SimpleAccessDeniedHandler());
                })
                .build();
    }

    /*
     * 在security安全框架中，提供了若干密码解析器实现类型。
     * 其中BCryptPasswordEncoder 叫强散列加密。可以保证相同的明文，多次加密后，
     * 密码有相同的散列数据，而不是相同的结果。
     * 匹配时，是基于相同的散列数据做的匹配。
     * Spring Security 推荐使用 BCryptPasswordEncoder 作为密码加密和解析器。
     * */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS配置源 : 配置跨域的 这个是必须的
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许的源（生产环境应替换为实际域名）
        config.setAllowedOrigins(Arrays.asList(
                "https://www.example.com",
                "https://app.example.com",
                "http://localhost:5173"
        ));

        // 允许的方法
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // 允许的请求头
        config.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-Requested-With",
                "Accept", "X-CSRF-Token", "X-API-Version"
        ));

        // 暴露的响应头
        config.setExposedHeaders(Arrays.asList(
                "Content-Disposition", "X-Content-Disposition",
                "X-RateLimit-Limit", "X-RateLimit-Remaining"
        ));

        // 允许携带凭证（cookies等）
        config.setAllowCredentials(true);

        // 预检请求缓存时间（30分钟）
        config.setMaxAge(1800L);

        // 为所有路径应用此配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
