package com.iflytek.medicalsdk_nursing.domain;

import com.iflytek.android.framework.db.Column;
import com.iflytek.android.framework.db.Entity;

/**
 * @Title: com.iflytek.medicalsdk_nursing.domain
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/10/14-上午10:19,  All rights reserved
 * @Description: TODO 选项字典;
 * @author: chenzhilei
 * @data: 2016/10/14 上午10:19
 * @version: V1.0
 */
@Entity(table = "IFLY_OPTION")
public class OptionDic {

    /**
     * 数据代码
     */
    @Column
    private String codeID;
    /**
     * 选项编码
     */
    @Column
    private String optCode;
    /**
     * 选项名称
     */
    @Column
    private String optName;
    /**
     * 父级代码
     */
    @Column
    private String parentOpt;

    public String getCodeID() {
        return codeID;
    }

    public void setCodeID(String codeID) {
        this.codeID = codeID;
    }

    public String getOptCode() {
        return optCode;
    }

    public void setOptCode(String optCode) {
        this.optCode = optCode;
    }

    public String getOptName() {
        return optName;
    }

    public void setOptName(String optName) {
        this.optName = optName;
    }

    public String getParentOpt() {
        return parentOpt;
    }

    public void setParentOpt(String parentOpt) {
        this.parentOpt = parentOpt;
    }

    public OptionDic(String codeID, String optCode, String optName, String parentOpt) {
        this.codeID = codeID;
        this.optCode = optCode;
        this.optName = optName;
        this.parentOpt = parentOpt;
    }

    public OptionDic() {
    }
}
