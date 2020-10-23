package com.polaris.blog.response;

public enum ResponseState {
    SUCCESS(true,2000,"操作成功"),
    LOGIN_SUCCESS(true,2001,"登录成功"),
    REGISTER_SUCCESS(true,2002,"注册成功"),
    GET_RESOURCE_SUCCESS(true,2003,"获取资源成功"),

    FAILED(false,4000,"操作失败"),
    LOGIN_FAILED(false,4001,"登录失败"),
    REGISTER_FAILED(true,4002,"注册失败"),
    ERROR_403(false,4003,"权限不足"),
    ERROR_404(false,4004,"页面丢失"),
    GET_RESOURCE_FAILED(false,4005,"获取资源失败"),
    ACCOUNT_DENIED(false,4006,"该账号已被封禁"),
    PERMISSION_DENIED(false,4007,"无权修改"),
    ACCOUNT_NOT_LOGIN(false,4008,"账号未登录"),
    WAITING_FOR_SCAN(false,4009,"等待用户扫码登录二维码中"),
    QR_CODE_DEPRECATE(false,4010,"登录二维码已过期"),


    ERROR_504(false,5004,"系统繁忙，请稍后重试"),
    ERROR_505(false,5005,"请求错误，请检查提交的数据");

    ResponseState(boolean success,int code,String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    private boolean success;
    private int code;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
