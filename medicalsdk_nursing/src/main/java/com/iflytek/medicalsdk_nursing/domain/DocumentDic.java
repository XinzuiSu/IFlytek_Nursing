package com.iflytek.medicalsdk_nursing.domain;

import com.google.gson.annotations.SerializedName;
import com.iflytek.android.framework.db.Column;
import com.iflytek.android.framework.db.Entity;

/**
 * @Title: com.iflytek.medicalsdk_nursing.domain
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/10/14-上午10:19,  All rights reserved
 * @Description: TODO 文书字典;
 * @author: chenzhilei
 * @data: 2016/10/14 上午10:19
 * @version: V1.0
 */
@Entity(table = "IFLY_DOCUMENT")
public class DocumentDic {

    /**
     * 文书编码
     */
    @Column
    @SerializedName("Doc_Id")
    private String nmrID;
    /**
     * 文书名称
     */
    @Column
    @SerializedName("Doc_Name")
    private String nmrName;
    /**
     * 接口名称
     */
    @Column
    @SerializedName("Interface_Name")
    private String interfaceName;

    public String getNmrID() {
        return nmrID;
    }

    public void setNmrID(String nmrID) {
        this.nmrID = nmrID;
    }

    public String getNmrName() {
        return nmrName;
    }

    public void setNmrName(String nmrName) {
        this.nmrName = nmrName;
    }


    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public DocumentDic(String nmrID, String nmrName, String interfaceName) {
        this.nmrID = nmrID;
        this.nmrName = nmrName;
        this.interfaceName = interfaceName;
    }

    public DocumentDic(){

    }
}
