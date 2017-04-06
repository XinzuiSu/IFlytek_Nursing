package com.iflytek.medicalsdk_nursing.net;

/**
 * WebService 返回类
 *
 * @author nanHuang
 */
public class SoapResult1 {

    /**
     * 是否成功 标志位
     */
    private String flag;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String result;

    /**
     * 成功信息
     */
    private String data;
    /**
     * 对应的请求方法
     */
    private String method;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
