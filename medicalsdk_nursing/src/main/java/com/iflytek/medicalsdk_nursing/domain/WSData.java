package com.iflytek.medicalsdk_nursing.domain;


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
    private String ID;

    /**
     * 名称
     */
    private String Name;

    /**
     * 扩展名称
     */
    private String expandName;

    /**
     * 值
     */
    private String Value;

    /**
     * 代码项描述
     */
    private String ValueCaption;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        this.Value = value;
    }

    public String getValueCaption() {
        return ValueCaption;
    }

    public void setValueCaption(String valueCaption) {
        this.ValueCaption = valueCaption;
    }

    public String getExpandName() {
        return expandName;
    }

    public void setExpandName(String expandName) {
        this.expandName = expandName;
    }

    public WSData(String ID, String Name, String Value, String ValueCaption) {
        this.ID = ID;
        this.Name = Name;
        this.Value = Value;
        this.ValueCaption = ValueCaption;
    }

    public WSData() {
    }
}
