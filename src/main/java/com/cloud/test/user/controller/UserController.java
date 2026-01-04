package com.cloud.test.user.controller;

import com.alibaba.fastjson.JSON;
import com.cloud.test.user.domain.User;
import com.cloud.test.user.dto.LoginUserDto;
import com.cloud.test.user.dto.RegisterDto;
import com.cloud.test.user.service.IUserService;
import com.cloud.test.base.utils.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public AjaxResult login(@RequestBody LoginUserDto loginUserDto){
        Map<String, Object> data = userService.login(loginUserDto);
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setSuccess(true);
        ajaxResult.setResultObj(JSON.toJSONString(data));

        return ajaxResult;
    }

    @Operation(summary = "注册", description = "注册接口")
    @PostMapping(value = "/register")
    public AjaxResult register(@RequestBody RegisterDto registerDto){
        boolean result = userService.register(registerDto);
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setSuccess(result);
        ajaxResult.setResultObj(JSON.toJSONString("注册成功！！！"));
        return ajaxResult;
    }

    @Operation(summary = "保存和更新",description = "这是保存和更新的方法")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public boolean saveOrUpdate(@RequestBody User user) {
        try {
            if (user.getPassword() != null) user.setPassword(passwordEncoder.encode(user.getPassword()));

            userService.saveOrUpdate(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Operation(summary = "删除",description = "这是删除的方法")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public boolean removeUser(@PathVariable("id") Integer id) {
        try {
            userService.removeById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Operation(summary = "通过id查询",description = "通过id查询")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getUserById(@PathVariable("id") Integer id) {
        return userService.getById(id);
    }

    @Operation(summary = "查询所有数据",description = "查询所有数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<User> getUsers() {
        return userService.list();
    }

    @Operation(summary = "根据用户名查询数据", description = "根据用户名查询数据")
    @RequestMapping(value = "/name/{username}", method = RequestMethod.GET)
    public User getUserByUsername(@PathVariable("username") String username) {
        return userService.selectByUsername(username);
    }
}
