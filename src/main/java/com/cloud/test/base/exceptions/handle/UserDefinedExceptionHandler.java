//package com.example.practice.exceptions.handle;
//
//import com.example.practice.exceptions.UserDefinedException;
//import com.example.practice.utils.AjaxResult;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestController;
//
////拦截全局异常
//@ControllerAdvice  // 打了这个标签的类可以在执行其他的controller之前/之后做执行
//@RestController   // 这个类也相当于一个Controller
//public class UserDefinedExceptionHandler {
//    // @ExceptionHandler: 处理异常: 拦截异常 拦截自定义异常
//    @ExceptionHandler(UserDefinedException.class)
//    public AjaxResult exceptionHandler(UserDefinedException e){
//        e.printStackTrace();
//        return AjaxResult.me().setSuccess(false).setCode(1).setMessage(e.getMessage());
//    }
//
//    //@ExceptionHandler: 处理异常: 拦截异常 拦截系统内部异常
//    @ExceptionHandler(Exception.class)
//    public AjaxResult exceptionHandler(Exception e){
//        e.printStackTrace();
//        return AjaxResult.me().setSuccess(false).setCode(2).setMessage("系统异常!"+e.getMessage());
//    }
//}
