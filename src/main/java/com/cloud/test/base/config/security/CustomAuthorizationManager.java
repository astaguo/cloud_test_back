package com.cloud.test.base.config.security;

import com.cloud.test.user.domain.Permissions;
import com.cloud.test.user.domain.Role;
import com.cloud.test.user.mapper.PermissionsMapper;
import com.cloud.test.user.mapper.RoleMapper;
import com.cloud.test.base.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Supplier;


@Component
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Autowired
    public PermissionsMapper permissionsMapper;

    @Autowired
    public RoleMapper roleMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // 1. 声明匹配器
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        // 获取访问url
        String requestURI = object.getRequest().getRequestURI();
        System.out.println("requestURI:" + requestURI);
        // 通过Token获取当前用户并查询出拥有的角色id
        String requestTokenHeader = object.getRequest().getHeader("Authorization");

        // 检查是否需要认证路径权限
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            try {
                String jwtToken = requestTokenHeader.substring(7);
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                Role role = roleMapper.selectRoleByUserName(username);

                // 查询角色所拥有的所有权限路径
                List<Permissions> permissions = permissionsMapper.selectPermissionsByRoleId(role.getId());

                // 判断当前角色是否允许访问
                for (Permissions permission : permissions) {
                    if (PATH_MATCHER.match(permission.getPath(), requestURI)) {
                        return new AuthorizationDecision(true);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        return new AuthorizationDecision(false);
    }
}
