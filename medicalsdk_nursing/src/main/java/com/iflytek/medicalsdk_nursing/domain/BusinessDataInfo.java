package com.iflytek.medicalsdk_nursing.domain;

import com.iflytek.android.framework.db.Column;
import com.iflytek.android.framework.db.Entity;

import java.util.List;

/**
 * @Title: com.iflytek.medicalsdk_nursing.domain
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/10/13-下午3:08,  All rights reserved
 * @Description: TODO 记录详细信息;
 * @author: chenzhilei
 * @data: 2016/10/13 下午3:08
 * @version: V1.0
 */
public class BusinessDataInfo {

    /**
     * 患者住院号
     */
    private String syxh;
    /**
     * 婴儿住院号
     */
    private String yexh;
    /**
     * 患者姓名
     */
    private String patName;
    /**
     * 病历代码
     */
    private String nmrCode;

    /**
     * 数据日期
     */
    private String date;

    /**
     * 记录日期
     */
    private String recorderDate;

    /**
     * 业务数据对象
     */
    private List<WSData> wsDataList;

    public String getSyxh() {
        return syxh;
    }

    public void setSyxh(String syxh) {
        this.syxh = syxh;
    }

    public String getYexh() {
        return yexh;
    }

    public void setYexh(String yexh) {
        this.yexh = yexh;
    }

    public String getPatName() {
        return patName;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public String getNmrCode() {
        return nmrCode;
    }

    public void setNmrCode(String nmrCode) {
        this.nmrCode = nmrCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRecorderDate() {
        return recorderDate;
    }

    public void setRecorderDate(String recorderDate) {
        this.recorderDate = recorderDate;
    }

    public List<WSData> getWsDataList() {
        return wsDataList;
    }

    public void setWsDataList(List<WSData> wsDataList) {
        this.wsDataList = wsDataList;
    }
}
