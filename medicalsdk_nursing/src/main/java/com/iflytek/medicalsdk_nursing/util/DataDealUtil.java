package com.iflytek.medicalsdk_nursing.util;

import android.content.Context;
import android.util.Log;

import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.medicalsdk_nursing.dao.DocumentDetailDicDao;
import com.iflytek.medicalsdk_nursing.dao.DocumentDicDao;
import com.iflytek.medicalsdk_nursing.dao.MappingDao;
import com.iflytek.medicalsdk_nursing.dao.OptionDicDao;
import com.iflytek.medicalsdk_nursing.dao.PatientInfoDao;
import com.iflytek.medicalsdk_nursing.domain.BusinessDataInfo;
import com.iflytek.medicalsdk_nursing.domain.DocumentDetailDic;
import com.iflytek.medicalsdk_nursing.domain.MappingInfo;
import com.iflytek.medicalsdk_nursing.domain.OptionDic;
import com.iflytek.medicalsdk_nursing.domain.PatientInfo;
import com.iflytek.medicalsdk_nursing.domain.WSData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * @Title: com.iflytek.medicalsdk_nursing.util
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/11/10-下午3:55,  All rights reserved
 * @Description: TODO 数据处理工具类;
 * @author: chenzhilei
 * @data: 2016/11/10 下午3:55
 * @version: V1.0
 */

public abstract class DataDealUtil {

    /**
     * 时间基础格式化
     */
    public static final String DEAFULTFORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * NIS系统时间格式
     */
    private static String NISDATEFORMAT = "yyyyMMddHH:mm:ss";

    /**
     * 时间格式化
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DEAFULTFORMAT);

    private Context mContext;
    //数据集
    private BusinessDataInfo businessDataInfo;
    //转义json
    private JSONObject jsonObject;
    //业务
    private String service;
    //key
    private String key = "";
    //value
    private String value = "";
    //患者姓名
    private String name = "";
    //床号
    private String bed = "";
    //类型
    private String type = "";

    private String recordTime = "";
    //映射关系Dao类
    private MappingDao mappingDao;
    //文书字典Dao
    private DocumentDetailDicDao documentDetailDicDao;
    //选项字典Dao
    private OptionDicDao optionDicDao;
    //选中项
    private int selectPosition;
    //数据列表
    private List<BusinessDataInfo> businessDataInfoList;
    private ProfessionalDataTraffic professionalDataTraffic;

    private DocumentDicDao documentDicDao;

    private String mSelectType;

    /**
     * 数据处理
     *
     * @param context
     * @param businessDataInfos
     */
    public DataDealUtil(Context context, List<BusinessDataInfo> businessDataInfos, int position, String selectType) {
        this.mContext = context;
        mappingDao = new MappingDao(context);
        documentDetailDicDao = new DocumentDetailDicDao(context);
        optionDicDao = new OptionDicDao(context);
        this.businessDataInfoList = businessDataInfos;
        documentDicDao = new DocumentDicDao(context);
        this.selectPosition = position;
        this.mSelectType = selectType;
        professionalDataTraffic = new ProfessionalDataTraffic(documentDetailDicDao, optionDicDao,documentDicDao.getDocumentDic(mSelectType).getNmrID());
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    /**
     * 取出基本数据
     *
     * @return
     */
    public List<BusinessDataInfo> transDataForBase(String result) throws JSONException, ParseException {
        Log.d("RESULT",result);
        jsonObject = new JSONObject(result);
        service = jsonObject.optString("service");
        //护理业务
        if (StringUtils.isEquals(service, "nursing")) {
            //QA返回结果
            JSONObject semanticObject = jsonObject.optJSONObject("semantic");
            List<WSData> wsDataList = new ArrayList<>();
            if (semanticObject != null && semanticObject.has("slots")) {
                JSONObject slotsObject = semanticObject.optJSONObject("slots");
                Iterator<String> iterator = slotsObject.keys();
                bed = slotsObject.optString("bed");
                type = slotsObject.optString("type");
                name = slotsObject.optString("name");
                wsDataList = doWhileForWsData(iterator, slotsObject);
            }
            traceType(result);
            tracePatientInfo();
            return assembleData(wsDataList);
        } else if (StringUtils.isEquals(service, "chat")) {
            JSONObject answerObject = jsonObject.optJSONObject("answer");
            String typeName = answerObject.optString("text");
            String text = jsonObject.optString("text");
            return assembleData(traceQA(typeName, text));
        } else {
//            BaseToast.showToastNotRepeat(mContext, "暂不支持您的说法", 2000);
            onError(1001, "暂不支持您的说法");
            return businessDataInfoList;
        }
    }


    private List<WSData> traceQA(String typeName, String valueText) {
        List<WSData> wsDataList = new ArrayList<>();
        //组装数据
        WSData wsData = new WSData();
        //项目名确定
        wsData.setName(typeName);
        DocumentDetailDic documentDetailDic = documentDetailDicDao.getDocumentDetailDic(typeName);
        String[] values;
        if (valueText.contains(",")) {
            values = valueText.split(",");
        } else {
            values = new String[]{valueText};
        }
        //多结果遍历解析
        for (String valueStr : values) {
            if (documentDetailDic != null) {
                //项目具有相关ID
                wsData.setID(documentDetailDic.getItemID());
                if (StringUtils.isNotBlank(documentDetailDic.getCodeID())) {
                    //判断选项是值还是选择项
                    OptionDic optionDic = optionDicDao.getOptionDic(documentDetailDic.getCodeID(), "其他");
                    if (optionDic != null) {
                        wsData.setValue(optionDic.getOptCode());
                        wsData.setValueCaption(optionDic.getOptName());
                    } else {
                        wsData.setValueCaption(valueStr);
                    }
                } else {
                    wsData.setValue(valueStr);
                }
                wsDataList.add(wsData);
            } else {
                onError(1002, typeName + "不在" + mSelectType + "单据中");
            }
        }
        return wsDataList;
    }

    /**
     * 构建护理类型
     *
     * @param resultText
     */
    private void traceType(String resultText) {
        if (resultText.contains("体温单")) {
            type = "体温单";
        }
        if (StringUtils.isNotBlank(type)) {
            //切换种类
            onTypeSelected(type);
            if (StringUtils.isNotBlank(recordTime)) {
                onRecordTime(recordTime);
            }
//            spinner.setSelection(typeList.indexOf(type));
        }
    }

    /***
     * 构建患者信息
     */
    private void tracePatientInfo() {
        if (StringUtils.isNotBlank(name)) {
            BusinessDataInfo busInfo = null;
            int i = 0;
            for (BusinessDataInfo info : businessDataInfoList) {
                if (StringUtils.isEquals(info.getPatName(), name)) {
                    busInfo = info;
                    i = businessDataInfoList.indexOf(info);
                    break;
                }
            }
            if (busInfo == null) {
                busInfo = new BusinessDataInfo();
                busInfo.setPatName(name);
                PatientInfoDao patientInfoDao = new PatientInfoDao(mContext);
                PatientInfo patientInfo = patientInfoDao.getPatientInfoByName(name);
                if (patientInfo != null) {
                    busInfo.setPatName(patientInfo.getHzxm());
                    busInfo.setAge(patientInfo.getAge());
                    busInfo.setSex(patientInfo.getSex());
                    busInfo.setSyxh(patientInfo.getSyxh());
                    busInfo.setYexh(patientInfo.getYexh());
                }
                busInfo.setWsDataList(new ArrayList<WSData>());
                businessDataInfoList.add(busInfo);
                selectPosition = businessDataInfoList.size() - 1;
            } else {
                selectPosition = i;
                onPatientSelected(selectPosition);
            }
        }
        if (StringUtils.isNotBlank(bed)) {
            BusinessDataInfo busInfo = null;
            int i = 0;
            for (BusinessDataInfo info : businessDataInfoList) {
                if (StringUtils.isEquals(info.getBedNo(), bed)) {
                    busInfo = info;
                    i = businessDataInfoList.indexOf(info);
                    break;
                }
            }
            if (busInfo == null) {
                busInfo = new BusinessDataInfo();
                busInfo.setBedNo(bed);
                PatientInfoDao patientInfoDao = new PatientInfoDao(mContext);
                PatientInfo patientInfo = patientInfoDao.getPatientInfo(bed);
                if (patientInfo != null) {
                    busInfo.setPatName(patientInfo.getHzxm());
                    busInfo.setAge(patientInfo.getAge());
                    busInfo.setSex(patientInfo.getSex());
                    busInfo.setSyxh(patientInfo.getSyxh());
                    busInfo.setYexh(patientInfo.getYexh());
                }
                busInfo.setWsDataList(new ArrayList<WSData>());
                businessDataInfoList.add(busInfo);
                selectPosition = businessDataInfoList.size() - 1;
            } else {
                selectPosition = i;
                onPatientSelected(selectPosition);
            }
        }
    }

    /**
     * 循环便利取出wsData
     *
     * @return
     */
    private List<WSData> doWhileForWsData(Iterator<String> iterator, JSONObject slotsObject) throws ParseException {

        List<WSData> wsDataList = new ArrayList<>();
        while (iterator.hasNext()) {
            key = iterator.next();
            value = slotsObject.optString(key);
            //过滤掉时间
            if (StringUtils.isEquals(key, "datetime")) {
                continue;
            }
            if (StringUtils.isEquals(key, "type") || StringUtils.isEquals(key, "bed") || StringUtils.isEquals(key, "name")) {
                //护理类型时间
                if (slotsObject.optJSONObject("datetime") != null) {
                    String time = "00:00:00";
                    if (StringUtils.isNotBlank(slotsObject.optJSONObject("datetime").optString("time"))) {
                        time = slotsObject.optJSONObject("datetime").optString("time");
                    }
                    recordTime = slotsObject.optJSONObject("datetime").optString("date") + " " + time;
                }
                continue;
            }
            //处理时间格式
            if (!StringUtils.isEquals(key, "datetime") && value.contains("date")) {
                String time = "00:00:00";
                if (StringUtils.isNotBlank(slotsObject.optJSONObject(key).optString("time"))) {
                    time = slotsObject.optJSONObject(key).optString("time");
                }
                value = slotsObject.optJSONObject(key).optString("date") + " " + time;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(NISDATEFORMAT);
                Date date = dateFormat.parse(value);
                value = simpleDateFormat.format(date);
            }


            //根据映射关系表设置项目值
            MappingInfo mappingInfo = mappingDao.getMappingDic(key);

            //特殊值特殊对待
            if (professionalDataTraffic.isDataProfessional(key)) {
                //组装数据
                WSData professWsData = new WSData();
                professWsData = professionalDataTraffic.trafficData(mappingInfo, value);
                wsDataList.add(professWsData);
                continue;
            }

            String[] values;
            if (value.contains("`")) {
                values = value.split("`");
            } else {
                values = new String[]{value};
            }
            DocumentDetailDic documentDetailDic = documentDetailDicDao.getDocumentDetailDic(mappingInfo.getValue(), documentDicDao.getDocumentDic(mSelectType).getNmrID());
            if (documentDetailDic == null) {
                onError(1002, mappingInfo.getValue() + "不在" + mSelectType + "单据中");
            } else {
                //多结果遍历解析
                for (String valueStr : values) {
                    //组装数据
                    WSData wsData = new WSData();
                    //项目名确定
                    wsData.setName(mappingInfo.getValue());
                    //项目具有相关ID
                    wsData.setID(documentDetailDic.getItemID());
                    if (StringUtils.isNotBlank(documentDetailDic.getCodeID())) {
                        //判断选项是值还是选择项
                        OptionDic optionDic = optionDicDao.getOptionDic(documentDetailDic.getCodeID(), valueStr);
                        if (optionDic != null) {
                            wsData.setValue(optionDic.getOptCode());
                            wsData.setValueCaption(optionDic.getOptName());
                        } else {
                            //判断选项是其他
                            optionDic = optionDicDao.getOptionDic(documentDetailDic.getCodeID(), "其他");
                            if (optionDic !=null){
                                wsData.setValue(optionDic.getOptCode());
                                wsData.setValueCaption(valueStr);
                            }else {
                                onError(1002, mappingInfo.getValue()+ "没有" + valueStr + "选项");
                                continue;
                            }
                        }
                    } else {
                        wsData.setValue(valueStr);
                    }
                    wsDataList.add(wsData);
                }
            }

        }
        return wsDataList;
    }


    /**
     * 组装数据
     *
     * @param wsDataList
     * @return
     */
    private List<BusinessDataInfo> assembleData(List<WSData> wsDataList) {
        if (wsDataList != null && wsDataList.size() > 0) {
            //如果当前的数据列表为空则默认一床用户
            if (businessDataInfoList.size() == 0) {
                businessDataInfo = new BusinessDataInfo();
                businessDataInfo.setWsDataList(new ArrayList<WSData>());
            } else {
                businessDataInfo = businessDataInfoList.get(selectPosition);
            }
            businessDataInfo.getWsDataList().addAll(wsDataList);
            if (businessDataInfoList.size() == 0) {
                businessDataInfoList.add(businessDataInfo);
            } else {
                businessDataInfoList.set(selectPosition, businessDataInfo);
            }
        }
        return businessDataInfoList;
    }


    /**
     * 选中患者
     *
     * @param position
     */
    public abstract void onPatientSelected(int position);

    /**
     * 选中护理类型
     *
     * @param type
     */
    public abstract void onTypeSelected(String type);

    /**
     * 异常
     */
    public abstract void onError(int errorCode, String errorMsg);

    /**
     * 修改时间
     *
     * @param recordTime
     */
    public abstract void onRecordTime(String recordTime);


}
