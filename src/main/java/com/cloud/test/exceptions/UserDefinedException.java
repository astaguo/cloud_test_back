package com.cloud.test.exceptions;

/**
 * 用户自定义异常
 */
public class UserDefinedException extends RuntimeException{
    public UserDefinedException(String message) {
        super(message);
    }
}
