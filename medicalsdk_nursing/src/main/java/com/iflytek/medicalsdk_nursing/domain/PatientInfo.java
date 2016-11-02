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
public class PatientInfo {

    /**
     * 年龄
     */
    @Column
    private String age;

    /**
     * 床位代码
     */
    @Column
    private String cwdm;
    /**
     * 患者姓名
     */
    @Column
    private String hzxm;

    /**
     * 患者id
     */
    @Column
    private String patid;
    /**
     * 性别
     */
    @Column
    private String sex;
    /**
     * 首页序号
     */
    @Column
    private String syxh;
    /**
     * 婴儿序号
     */
    @Column
    private String yexh;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCwdm() {
        return cwdm;
    }

    public void setCwdm(String cwdm) {
        this.cwdm = cwdm;
    }

    public String getHzxm() {
        return hzxm;
    }

    public void setHzxm(String hzxm) {
        this.hzxm = hzxm;
    }

    public String getPatid() {
        return patid;
    }

    public void setPatid(String patid) {
        this.patid = patid;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

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
}
