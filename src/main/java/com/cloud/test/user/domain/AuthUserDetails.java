package com.cloud.test.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "认证用户类")
public class AuthUserDetails implements UserDetails {
    @Schema(title = "用户ID")
    private Integer id;

    @Schema(title = "用户名")
    private String username;

    @Schema(title = "密码")
    private String password;

    //    用户拥有的权限集合，我这里先设置为null，将来会再更改的
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return null;
    }

    public AuthUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
    }

    //    后面四个方法都是用户是否可用、是否过期之类的。我都设置为true
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
