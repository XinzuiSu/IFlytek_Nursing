package com.iflytek.medicalsdk_nursing.domain;

/**
 * @Title: com.iflytek.medicalsdk_nursing.domain
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/10/13-下午3:08,  All rights reserved
 * @Description: TODO 用户信息;
 * @author: chenzhilei
 * @data: 2016/10/13 下午3:08
 * @version: V1.0
 */
public class UserInfo {

    private String userCode;
    private String userName;
    private String userLever;
    private String dptCode;
    private String dptName;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserLever() {
        return userLever;
    }

    public void setUserLever(String userLever) {
        this.userLever = userLever;
    }

    public String getDptCode() {
        return dptCode;
    }

    public void setDptCode(String dptCode) {
        this.dptCode = dptCode;
    }

    public String getDptName() {
        return dptName;
    }

    public void setDptName(String dptName) {
        this.dptName = dptName;
    }
}
