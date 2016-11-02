package com.iflytek.medicalsdk_nursing.domain;

import com.iflytek.android.framework.db.Column;
import com.iflytek.android.framework.db.Entity;

/**
 * @Title: com.iflytek.medicalsdk_nursing.domain
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/10/13-下午3:08,  All rights reserved
 * @Description: TODO 患者信息;
 * @author: chenzhilei
 * @data: 2016/10/13 下午3:08
 * @version: V1.0
 */
@Entity(table = "IFLY_PATIENT")
public class PatientInfoOld {

    /**
     * 患者标识
     */
    @Column
    private String patID;

    /**
     * 本次住院标识
     */
    @Column
    private String hosID;
    /**
     * 病案号
     */
    @Column
    private String binganID;

    /**
     * 门诊号
     */
    @Column
    private String outpatientNo;
    /**
     * 婴儿标识
     */
    @Column
    private String babyTg;
    /**
     * 关联住院流水号
     */
    @Column
    private String connectHosID;
    /**
     * 住院次数
     */
    @Column
    private String hosCount;
    /**
     * 病人姓名
     */
    @Column
    private String patName;
    /**
     * 病人出生日期
     */
    @Column
    private String patBirth;
    /**
     * 病人性别
     */
    @Column
    private String patSex;
    /**
     * 住院床号
     */
    @Column
    private String hosBedNum;
    /**
     * 所在病区编码
     */
    @Column
    private String areaCode;
    /**
     * 所在病区名称
     */
    @Column
    private String areaName;
    /**
     * 护理级别编码
     */
    @Column
    private String nurLevelCode;
    /**
     * 护理级别名称
     */
    @Column
    private String nurLevelName;
    /**
     * 病人在院类型
     */
    @Column
    private String patHosStatus;
    /**
     * 入院日期
     */
    @Column
    private String patHosDateIn;
    /**
     * 出院日期
     */
    @Column
    private String getPatHosDateOut;
    /**
     * 所在科室代码
     */
    @Column
    private String dptCode;
    /**
     * 所在科室名称
     */
    @Column
    private String dptName;

    public String getPatID() {
        return patID;
    }

    public void setPatID(String patID) {
        this.patID = patID;
    }

    public String getHosID() {
        return hosID;
    }

    public void setHosID(String hosID) {
        this.hosID = hosID;
    }

    public String getBinganID() {
        return binganID;
    }

    public void setBinganID(String binganID) {
        this.binganID = binganID;
    }

    public String getOutpatientNo() {
        return outpatientNo;
    }

    public void setOutpatientNo(String outpatientNo) {
        this.outpatientNo = outpatientNo;
    }

    public String getBabyTg() {
        return babyTg;
    }

    public void setBabyTg(String babyTg) {
        this.babyTg = babyTg;
    }

    public String getConnectHosID() {
        return connectHosID;
    }

    public void setConnectHosID(String connectHosID) {
        this.connectHosID = connectHosID;
    }

    public String getHosCount() {
        return hosCount;
    }

    public void setHosCount(String hosCount) {
        this.hosCount = hosCount;
    }

    public String getPatName() {
        return patName;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public String getPatBirth() {
        return patBirth;
    }

    public void setPatBirth(String patBirth) {
        this.patBirth = patBirth;
    }

    public String getPatSex() {
        return patSex;
    }

    public void setPatSex(String patSex) {
        this.patSex = patSex;
    }

    public String getHosBedNum() {
        return hosBedNum;
    }

    public void setHosBedNum(String hosBedNum) {
        this.hosBedNum = hosBedNum;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getNurLevelCode() {
        return nurLevelCode;
    }

    public void setNurLevelCode(String nurLevelCode) {
        this.nurLevelCode = nurLevelCode;
    }

    public String getNurLevelName() {
        return nurLevelName;
    }

    public void setNurLevelName(String nurLevelName) {
        this.nurLevelName = nurLevelName;
    }

    public String getPatHosStatus() {
        return patHosStatus;
    }

    public void setPatHosStatus(String patHosStatus) {
        this.patHosStatus = patHosStatus;
    }

    public String getPatHosDateIn() {
        return patHosDateIn;
    }

    public void setPatHosDateIn(String patHosDateIn) {
        this.patHosDateIn = patHosDateIn;
    }

    public String getGetPatHosDateOut() {
        return getPatHosDateOut;
    }

    public void setGetPatHosDateOut(String getPatHosDateOut) {
        this.getPatHosDateOut = getPatHosDateOut;
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

    public PatientInfoOld(String patID, String hosID, String binganID, String outpatientNo, String babyTg, String connectHosID, String hosCount, String patName, String patBirth, String patSex, String hosBedNum, String areaCode, String areaName, String nurLevelCode, String nurLevelName, String patHosStatus, String patHosDateIn, String getPatHosDateOut, String dptCode, String dptName) {
        this.patID = patID;
        this.hosID = hosID;
        this.binganID = binganID;
        this.outpatientNo = outpatientNo;
        this.babyTg = babyTg;
        this.connectHosID = connectHosID;
        this.hosCount = hosCount;
        this.patName = patName;
        this.patBirth = patBirth;
        this.patSex = patSex;
        this.hosBedNum = hosBedNum;
        this.areaCode = areaCode;
        this.areaName = areaName;
        this.nurLevelCode = nurLevelCode;
        this.nurLevelName = nurLevelName;
        this.patHosStatus = patHosStatus;
        this.patHosDateIn = patHosDateIn;
        this.getPatHosDateOut = getPatHosDateOut;
        this.dptCode = dptCode;
        this.dptName = dptName;
    }

    public PatientInfoOld(){

    }
}
