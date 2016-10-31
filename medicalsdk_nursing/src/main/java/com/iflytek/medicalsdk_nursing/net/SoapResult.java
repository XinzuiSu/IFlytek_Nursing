package com.iflytek.medicalsdk_nursing.net;

/**
 * WebService 返回类
 *
 * @author nanHuang
 */
public class SoapResult {

    /**
     * 是否成功 标志位
     */
    private boolean flag;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorName;

    /**
     * 成功信息
     */
    private String data;
    /**
     * 对应的请求方法
     */
    private String method;

    /**
     * @return the flag
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorName
     */
    public String getErrorName() {
        return errorName;
    }

//	public String getErrorName(String errorCode) {
//		return ResultCode.getMsg(errorCode);
//	}

    /**
     * @param errorName the errorName to set
     */
    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }

    public String getJsMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
