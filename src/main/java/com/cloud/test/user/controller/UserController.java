package com.cloud.test.user.controller;

import com.cloud.test.user.domain.User;
import com.cloud.test.user.dto.LoginUserDto;
import com.cloud.test.user.dto.RegisterDto;
import com.cloud.test.user.service.IUserService;
import com.cloud.test.base.utils.AjaxResult;
import com.cloud.test.user.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户控制器",description = "用户操作接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    public IUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = "登陆", description = "登陆接口")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public AjaxResult<LoginVO> login(@RequestBody LoginUserDto loginUserDto){
        return AjaxResult.<LoginVO>me().setResultObj(userService.login(loginUserDto));
    }

    @Operation(summary = "注册", description = "注册接口")
    @PostMapping(value = "/register")
    public AjaxResult<Void> register(@RequestBody RegisterDto registerDto){
        return AjaxResult.<Void>me().setSuccess(userService.register(registerDto));
    }

    @Operation(summary = "保存和更新",description = "这是保存和更新的方法")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public AjaxResult<Void> saveOrUpdate(@RequestBody User user) {
        if (user.getPassword() != null) user.setPassword(passwordEncoder.encode(user.getPassword()));

        return AjaxResult.<Void>me().setSuccess(userService.saveOrUpdate(user));
    }

    @Operation(summary = "删除",description = "这是删除的方法")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public AjaxResult<Void> removeUser(@PathVariable("id") Integer id) {
        return AjaxResult.<Void>me().setSuccess(userService.removeById(id));
    }

    @Operation(summary = "通过id查询",description = "通过id查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AjaxResult<User> getUserById(@PathVariable("id") Integer id) {
        return AjaxResult.<User>me().setResultObj(userService.getById(id));
    }

    @Operation(summary = "查询所有数据",description = "查询所有数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public AjaxResult<List<User>> getUsers() {
        return AjaxResult.<List<User>>me().setResultObj(userService.list());
    }

    @Operation(summary = "根据用户名查询数据", description = "根据用户名查询数据")
    @RequestMapping(value = "/name/{username}", method = RequestMethod.GET)
    public AjaxResult<User> getUserByUsername(@PathVariable("username") String username) {
        return AjaxResult.<User>me().setResultObj(userService.selectByUsername(username));
    }
}
