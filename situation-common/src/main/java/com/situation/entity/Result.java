package com.situation.entity;

import java.io.Serializable;

/**
 * web 返回消息实体的封装
 * @author lgd
 * @date 2021/10/27 21:10
 */
public class Result<T> implements Serializable {
    
    //是否成功
    private boolean success;

    //状态标识
    private Integer status;

    //消息
    private String message;

    //数据
    private T data;

    public Result(boolean success, Integer status, String message) {
        this.success = success;
        this.status = status;
        this.message = message;
    }


    public Result(boolean success, Integer status, String message, T data) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
