package com.iflytek.medicalsdk_nursing.net;

import com.google.gson.annotations.SerializedName;

/**
 * @Title: com.iflytek.medicalassistant.net
 * @Copyright: IFlytek Co., Ltd. Copyright 16/4/26-下午4:54,  All rights reserved
 * @Description: TODO 网络请求类;
 * @author: chenzhilei
 * @data: 16/4/26 下午4:54
 * @version: V1.0
 */
public class RequestParam {

    @SerializedName("address")
    private String address;

    @SerializedName("skey")
    private String skey;

    @SerializedName("method")
    private String method;

    @SerializedName("params")
    private String params;

    @SerializedName("appId")
    private String appId;

    @SerializedName("appVersion")
    private String appVersion;

    @SerializedName("osInfo")
    private String osInfo;

    @SerializedName("deviceInfo")
    private String deviceInfo;

    @SerializedName("phoneNum")
    private String phoneNum;

    @SerializedName("token")
    private String token;

    @SerializedName("requestSn")
    private String requestSn;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getRequestSn() {
        return requestSn;
    }

    public void setRequestSn(String requestSn) {
        this.requestSn = requestSn;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOsInfo() {
        return osInfo;
    }

    public void setOsInfo(String osInfo) {
        this.osInfo = osInfo;
    }
}
