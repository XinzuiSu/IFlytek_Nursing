package com.iflytek.medicalsdk_nursing.domain;

import java.util.List;

/**
 * @Title: com.iflytek.medicalsdk_nursing.domain
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/10/13-下午3:08,  All rights reserved
 * @Description: TODO 业务数据对象;
 * @author: chenzhilei
 * @data: 2016/10/13 下午3:08
 * @version: V1.0
 */
public class WSData {

    /**
     * 代码
     */
    private String wsID;

    /**
     * 名称
     */
    private String wsName;

    /**
     * 值
     */
    private String wsValue;

    /**
     * 代码项描述
     */
    private String wsValueCaption;

    public String getWsID() {
        return wsID;
    }

    public void setWsID(String wsID) {
        this.wsID = wsID;
    }

    public String getWsName() {
        return wsName;
    }

    public void setWsName(String wsName) {
        this.wsName = wsName;
    }

    public String getWsValue() {
        return wsValue;
    }

    public void setWsValue(String wsValue) {
        this.wsValue = wsValue;
    }

    public String getWsValueCaption() {
        return wsValueCaption;
    }

    public void setWsValueCaption(String wsValueCaption) {
        this.wsValueCaption = wsValueCaption;
    }

    public WSData(String wsID, String wsName, String wsValue, String wsValueCaption) {
        this.wsID = wsID;
        this.wsName = wsName;
        this.wsValue = wsValue;
        this.wsValueCaption = wsValueCaption;
    }

    public WSData() {
    }
}
