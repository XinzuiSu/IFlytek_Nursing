package com.iflytek.medicalsdk_nursing.domain;

/**
 * @Title: com.iflytek.medicalsdk_nursing.domain
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/11/11-上午11:44,  All rights reserved
 * @Description: TODO 表单选择类;
 * @author: chenzhilei
 * @data: 2016/11/11 上午11:44
 * @version: V1.0
 */

public class FormCheck {

    /**
     * 名称
     */
    private String Name;
    /**
     * 代码
     */
    private String Bldm;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBldm() {
        return Bldm;
    }

    public void setBldm(String bldm) {
        Bldm = bldm;
    }
}
