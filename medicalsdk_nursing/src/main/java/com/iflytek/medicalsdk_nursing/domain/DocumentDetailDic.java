package com.iflytek.medicalsdk_nursing.domain;

import com.google.gson.annotations.SerializedName;
import com.iflytek.android.framework.db.Column;
import com.iflytek.android.framework.db.Entity;

/**
 * @Title: com.iflytek.medicalsdk_nursing.domain
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/10/14-上午10:19,  All rights reserved
 * @Description: TODO 文书基本信息字典;
 * @author: chenzhilei
 * @data: 2016/10/14 上午10:19
 * @version: V1.0
 */
@Entity(table = "IFLY_DOCUMENT_DETAIL")
public class DocumentDetailDic {

    /**
     * 文书编码
     */
    @Column
    @SerializedName("Doc_Id")
    private String nmrID;
    /**
     * 项目编码
     */
    @Column
    @SerializedName("Item_Id")
    private String itemID;
    /**
     * 项目名称
     */
    @Column
    @SerializedName("Item_Name")
    private String itemName;
    /**
     * 项目类型
     */
    @Column
    @SerializedName("Item_Type")
    private String itemType;
    /**
     * 数据代码
     */
    @Column
    @SerializedName("Code_Id")
    private String codeID;

    @Column
    @SerializedName("Control_Type")
    private String controlType;

    public String getNmrID() {
        return nmrID;
    }

    public void setNmrID(String nmrID) {
        this.nmrID = nmrID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getCodeID() {
        return codeID;
    }

    public void setCodeID(String codeID) {
        this.codeID = codeID;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public DocumentDetailDic(String nmrID, String itemID, String itemName, String itemType, String codeID) {
        this.nmrID = nmrID;
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemType = itemType;
        this.codeID = codeID;
    }

    public DocumentDetailDic(){

    }
}
