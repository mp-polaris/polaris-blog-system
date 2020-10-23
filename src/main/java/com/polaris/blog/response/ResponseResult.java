package com.polaris.blog.response;

public class ResponseResult {
    private boolean success;
    private int code;
    private String message;
    private Object data;

    public ResponseResult() {
    }

    public ResponseResult(ResponseState responseState) {
        this.success = responseState.isSuccess();
        this.code = responseState.getCode();
        this.message = responseState.getMessage();
    }

    public static ResponseResult GET(ResponseState state){
        return new ResponseResult(state);
    }

    public static ResponseResult SUCCESS(){
        return new ResponseResult(ResponseState.SUCCESS);
    }

    public static ResponseResult LOGIN_SUCCESS(){
        return new ResponseResult(ResponseState.LOGIN_SUCCESS);
    }

    public static ResponseResult ACCOUNT_NOT_LOGIN(){
        return new ResponseResult(ResponseState.ACCOUNT_NOT_LOGIN);
    }

    public static ResponseResult PERMISSION_DENIED(){
        return new ResponseResult(ResponseState.PERMISSION_DENIED);
    }

    public static ResponseResult ACCOUNT_DENIED(){
        return new ResponseResult(ResponseState.ACCOUNT_DENIED);
    }

    public static ResponseResult ERROR_403(){
        return new ResponseResult(ResponseState.ERROR_403);
    }
    public static ResponseResult ERROR_404(){
        return new ResponseResult(ResponseState.ERROR_404);
    }
    public static ResponseResult ERROR_504(){
        return new ResponseResult(ResponseState.ERROR_504);
    }
    public static ResponseResult ERROR_505(){
        return new ResponseResult(ResponseState.ERROR_505);
    }

    public static ResponseResult WAITING_FOR_SCAN(){
        return new ResponseResult(ResponseState.WAITING_FOR_SCAN);
    }
    public static ResponseResult QR_CODE_DEPRECATE(){
        return new ResponseResult(ResponseState.QR_CODE_DEPRECATE);
    }

    public static ResponseResult SUCCESS(String message){
        ResponseResult result = new ResponseResult(ResponseState.SUCCESS);
        result.setMessage(message);
        return result;
    }

    public static ResponseResult FAILED(){
        return new ResponseResult(ResponseState.FAILED);
    }

    public static ResponseResult FAILED(String message){
        ResponseResult result = new ResponseResult(ResponseState.FAILED);
        result.setMessage(message);
        return result;
    }

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

    public Object getData() {
        return data;
    }

    public ResponseResult setData(Object data) {
        this.data = data;
        return this;
    }
}
