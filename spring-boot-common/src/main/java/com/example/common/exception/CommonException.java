package com.example.common.exception;

/**
 * 自定义异常
 * @author Created by L.C.Y on 2018-8-28
 */
public class CommonException extends RuntimeException{
    private static final long serialVersionUID = 2305653807231756702L;
    //异常信息
    private String message;
    //构造器
    public CommonException(String message){
        super(message);
        this.message=message;
    }
    @Override
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}
