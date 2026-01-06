package com.cloud.test.base.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

//Ajax请求响应对象的类
@Schema(title = "响应实体类")
@Getter
public class AjaxResult<T> {
    @Schema(title = "返回成功与否结果")
    private boolean success = true;
    @Schema(title = "返回状态码: 200 成功状态 1 用户自定义异常 2 系统异常")
    private Integer code = 200;
    @Schema(title = "返回操作信息")
    private String message = "操作成功!";
    //返回到前台对象
    @Schema(title = "返回数据")
    private T resultObj;

    //AjaxResult.me()成功
    //AjaxResult.me().setMessage()成功
    //AjaxResult.me().setSuccess(false),setMessage("失败");
    /**
     * 创建泛型化的AjaxResult实例
     * @param <T> 泛型类型
     * @return AjaxResult<T>
     */
    public static <T> AjaxResult<T> me() {
        return new AjaxResult<>();
    }

    public AjaxResult<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public AjaxResult<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public AjaxResult<T> setResultObj(T data) {
        this.resultObj = data;
        return this;
    }

    public AjaxResult<T> setMessage(String message) {
        this.message = message;
        return this;
    }
}
