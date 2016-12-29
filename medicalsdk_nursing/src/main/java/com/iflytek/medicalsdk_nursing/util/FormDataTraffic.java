package com.iflytek.medicalsdk_nursing.util;

import com.iflytek.medicalsdk_nursing.domain.BusinessDataInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: com.iflytek.medicalsdk_nursing.util
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/12/26-下午4:29,  All rights reserved
 * @Description: TODO 表单数据转换;
 * @author: chenzhilei
 * @data: 2016/12/26 下午4:29
 * @version: V1.0
 */

public class FormDataTraffic {

    private List<BusinessDataInfo> businessDataInfoList;

    public FormDataTraffic(ArrayList<BusinessDataInfo> businessDataInfos){
        this.businessDataInfoList = businessDataInfos;
    }

//    public List<BusinessDataInfo> trafficData(String type){
//        for (BusinessDataInfo businessDataInfo:businessDataInfoList){
//            List<WSData> wsDataList = new ArrayList<>();
//            for (WSData wsData:businessDataInfo.getWsDataList()){
//            }
//
//        }
//    }


}
