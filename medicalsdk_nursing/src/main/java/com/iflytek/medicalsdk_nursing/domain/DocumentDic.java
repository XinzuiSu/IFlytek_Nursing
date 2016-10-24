package com.iflytek.medicalsdk_nursing.domain;

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
    private String nmrID;
    /**
     * 文书名称
     */
    @Column
    private String nmrName;
    /**
     * 护理工作项Id
     */
    @Column
    private String workID;
    /**
     * 接口名称
     */
    @Column
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

    public String getWorkID() {
        return workID;
    }

    public void setWorkID(String workID) {
        this.workID = workID;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public DocumentDic(String nmrID, String nmrName, String workID, String interfaceName) {
        this.nmrID = nmrID;
        this.nmrName = nmrName;
        this.workID = workID;
        this.interfaceName = interfaceName;
    }

    public DocumentDic(){

    }
}
