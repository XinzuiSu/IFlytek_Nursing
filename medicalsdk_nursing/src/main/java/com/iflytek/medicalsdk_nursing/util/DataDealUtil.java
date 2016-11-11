//package com.iflytek.medicalsdk_nursing.util;
//
//import android.content.Context;
//
//import com.iflytek.android.framework.util.StringUtils;
//import com.iflytek.medicalsdk_nursing.dao.PatientInfoDao;
//import com.iflytek.medicalsdk_nursing.domain.BusinessDataInfo;
//import com.iflytek.medicalsdk_nursing.domain.DocumentDetailDic;
//import com.iflytek.medicalsdk_nursing.domain.MappingInfo;
//import com.iflytek.medicalsdk_nursing.domain.OptionDic;
//import com.iflytek.medicalsdk_nursing.domain.PatientInfo;
//import com.iflytek.medicalsdk_nursing.domain.WSData;
//import com.iflytek.medicalsdk_nursing.view.RecordActivity;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * @Title: com.iflytek.medicalsdk_nursing.util
// * @Copyright: IFlytek Co., Ltd. Copyright 2016/11/10-下午3:55,  All rights reserved
// * @Description: TODO 数据处理工具类;
// * @author: chenzhilei
// * @data: 2016/11/10 下午3:55
// * @version: V1.0
// */
//
//public class DataDealUtil {
//
//    private Context mContext;
//    //数据集
//    private BusinessDataInfo businessDataInfo;
//    //结果集
//    private List<WSData> wsDataList = new ArrayList<>();
//    //转义json
//    private JSONObject jsonObject;
//    //业务
//    private String service;
//    //key
//    private String key = "";
//    //value
//    private String value = "";
//    //患者姓名
//    private String name = "";
//    //床号
//    private String bed = "";
//    //类型
//    private String type = "";
//
//    public DataDealUtil(Context context,String resultStr){
//        this.mContext = context;
//        BusinessDataInfo businessDataInfo = new BusinessDataInfo();
//        List<WSData> wsDataList = new ArrayList<>();
//        JSONObject jsonObject;
//        String service;
//        String key = "";
//        String value = "";
//        String name = "";
//        String bed = "";
//        String type = "";
//        try {
//            jsonObject = new JSONObject(resultStr);
//            service = jsonObject.optString("service");
//            JSONObject semanticObject = jsonObject.optJSONObject("semantic");
//            if (semanticObject != null && semanticObject.has("slots")) {
//                JSONObject slotsObject = semanticObject.optJSONObject("slots");
//                Iterator<String> iterator = slotsObject.keys();
//                bed = slotsObject.optString("bed");
//                type = slotsObject.optString("type");
//                name = slotsObject.optString("name");
//                //遍历结果
//                while (iterator.hasNext()) {
//                    key = iterator.next();
//                    value = slotsObject.optString(key);
//                    if (value.contains("date")){
//                        value = slotsObject.optJSONObject(key).optString("date")+" "+slotsObject.optJSONObject(key).optString("time");
//
//                    }
//                    if (StringUtils.isEquals(key, "type") || StringUtils.isEquals(key, "bed")) {
//                        continue;
//                    }
//                    String[] keys;
//                    if (key.contains(",")){
//                        keys = key.split(",");
//                    }else {
//                        keys = new String[]{key};
//                    }
//                    for (String keyStr:keys){
//                        //组装数据
//                        WSData wsData = new WSData();
//                        //设置项目值
//                        MappingInfo mappingInfo = mappingDao.getMappingDic(keyStr);
//                        wsData.setName(mappingInfo.getValue());
//                        DocumentDetailDic documentDetailDic = documentDetailDicDao.getDocumentDetailDic(mappingInfo.getValue());
//                        if (documentDetailDic != null) {
//                            wsData.setID(documentDetailDic.getItemID());
//                            if (StringUtils.isNotBlank(documentDetailDic.getCodeID())) {
//                                //判断选项是值还是选择项
//                                OptionDic optionDic = optionDicDao.getOptionDic(documentDetailDic.getCodeID(),value);
//                                if (optionDic != null) {
//                                    wsData.setValue(optionDic.getOptCode());
//                                    wsData.setValueCaption(optionDic.getOptName());
//                                } else {
//                                    wsData.setValueCaption(value);
//                                }
//                            } else {
//                                wsData.setValue(value);
//                            }
//                        }else {
//                            wsData.setValue(value);
//                        }
////                        wsDataList.add(wsData);
//                    }
//                }
//            }
//            //护理业务
//            if (StringUtils.isEquals(service, "nursing")) {
//                if (result.getResultString().contains("体温单")) {
//                    type = "体温单";
//                }
//                if (StringUtils.isNotBlank(type)) {
//                    //切换种类
//                    spinner.setSelection(typeList.indexOf(type));
//                }
//                if (StringUtils.isNotBlank(bed)) {
//                    BusinessDataInfo busInfo = null;
//                    int i = 0;
//                    for (BusinessDataInfo info : businessDataInfoList) {
//                        if (StringUtils.isEquals(info.getBedNo(), bed)) {
//                            busInfo = info;
//                            i = businessDataInfoList.indexOf(info);
//                            break;
//                        }
//                    }
//                    if (busInfo == null) {
//                        busInfo = new BusinessDataInfo();
//                        busInfo.setBedNo(bed);
//                        PatientInfoDao patientInfoDao = new PatientInfoDao(RecordActivity.this);
//                        PatientInfo patientInfo = patientInfoDao.getPatientInfo(bed);
//                        if (patientInfo != null) {
//                            busInfo.setPatName(patientInfo.getHzxm());
//                            busInfo.setAge(patientInfo.getAge());
//                            busInfo.setSex(patientInfo.getSex());
//                            busInfo.setSyxh(patientInfo.getSyxh());
//                            busInfo.setYexh(patientInfo.getYexh());
//                        }
//                        busInfo.setWsDataList(new ArrayList<WSData>());
//                        businessDataInfoList.add(busInfo);
//                        position = businessDataInfoList.size() - 1;
//                    } else {
//                        position = i;
//                    }
//                }
//                if (wsDataList != null && wsDataList.size() > 0) {
//                    //如果当前的数据列表为空则默认一床用户
//                    if (businessDataInfoList.size() == 0){
//                        businessDataInfo = new BusinessDataInfo();
//                        businessDataInfo.setWsDataList(new ArrayList<WSData>());
//                    }else {
//                        businessDataInfo = businessDataInfoList.get(position);
//                    }
//                    businessDataInfo.getWsDataList().addAll(wsDataList);
//                    if (businessDataInfoList.size() == 0){
//                        businessDataInfoList.add(businessDataInfo);
//                    }else {
//                        businessDataInfoList.set(position, businessDataInfo);
//                    }
//                }
//            } else {
//                showTip("暂不支持您的说法");
//                return;
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    private List<BusinessDataInfo> transJsonToList(){
//
//    }
//
//
//
//    private void transResult(){
//        jsonObject = new JSONObject(resultStr);
//        service = jsonObject.optString("service");
//        JSONObject semanticObject = jsonObject.optJSONObject("semantic");
//        if (semanticObject != null && semanticObject.has("slots")) {
//            JSONObject slotsObject = semanticObject.optJSONObject("slots");
//            Iterator<String> iterator = slotsObject.keys();
//            bed = slotsObject.optString("bed");
//            type = slotsObject.optString("type");
//            name = slotsObject.optString("name");
//            //遍历结果
//            while (iterator.hasNext()) {
//                key = iterator.next();
//                value = slotsObject.optString(key);
//                if (value.contains("date")){
//                    value = slotsObject.optJSONObject(key).optString("date")+" "+slotsObject.optJSONObject(key).optString("time");
//
//                }
//                if (StringUtils.isEquals(key, "type") || StringUtils.isEquals(key, "bed")) {
//                    continue;
//                }
//                String[] keys;
//                if (key.contains(",")){
//                    keys = key.split(",");
//                }else {
//                    keys = new String[]{key};
//                }
//                for (String keyStr:keys){
//                    //组装数据
//                    WSData wsData = new WSData();
//                    //设置项目值
//                    MappingInfo mappingInfo = mappingDao.getMappingDic(keyStr);
//                    wsData.setName(mappingInfo.getValue());
//                    DocumentDetailDic documentDetailDic = documentDetailDicDao.getDocumentDetailDic(mappingInfo.getValue());
//                    if (documentDetailDic != null) {
//                        wsData.setID(documentDetailDic.getItemID());
//                        if (StringUtils.isNotBlank(documentDetailDic.getCodeID())) {
//                            //判断选项是值还是选择项
//                            OptionDic optionDic = optionDicDao.getOptionDic(documentDetailDic.getCodeID(),value);
//                            if (optionDic != null) {
//                                wsData.setValue(optionDic.getOptCode());
//                                wsData.setValueCaption(optionDic.getOptName());
//                            } else {
//                                wsData.setValueCaption(value);
//                            }
//                        } else {
//                            wsData.setValue(value);
//                        }
//                    }else {
//                        wsData.setValue(value);
//                    }
//                    wsDataList.add(wsData);
//                }
//            }
//        }
//    }
//}
