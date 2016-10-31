package com.iflytek.medicalsdk_nursing.domain;

import com.iflytek.android.framework.db.Column;
import com.iflytek.android.framework.db.Entity;

/**
 * @Title: com.iflytek.medicalsdk_nursing.domain
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/10/27-下午4:03,  All rights reserved
 * @Description: TODO 映射关系类;
 * @author: chenzhilei
 * @data: 2016/10/27 下午4:03
 * @version: V1.0
 */
@Entity(table = "IFLY_MAPPING")
public class MappingInfo {
    /**
     * 键
     */
    @Column
    private String key;
    /**
     * 值
     */
    @Column
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
