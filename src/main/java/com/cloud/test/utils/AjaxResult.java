package com.cloud.test.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

//Ajax请求响应对象的类
@Schema(title = "响应实体类")
@Getter
public class AjaxResult {
    @Schema(title = "返回成功与否结果")
    private boolean success = true;
    @Schema(title = "返回状态码: 200 成功状态 1 用户自定义异常 2 系统异常")
    private Integer code = 200;
    @Schema(title = "返回操作信息")
    private String message = "操作成功!";
    //返回到前台对象
    @Schema(title = "返回数据")
    private Object resultObj;

    //AjaxResult.me()成功
    //AjaxResult.me().setMessage()成功
    //AjaxResult.me().setSuccess(false),setMessage("失败");
    public  static AjaxResult me(){
        return new AjaxResult();
    }

    public AjaxResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public AjaxResult setCode(Integer code) {
        this.code = code;
        return this;
    }

    public AjaxResult setResultObj(Object data) {
        this.resultObj = data;
        return this;
    }

    public AjaxResult setMessage(String message) {
        this.message = message;
        return this;
    }
}
