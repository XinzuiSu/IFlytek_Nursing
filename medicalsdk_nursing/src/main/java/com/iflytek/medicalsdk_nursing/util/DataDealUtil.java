package com.iflytek.medicalsdk_nursing.util;

import android.content.Context;

import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.medicalsdk_nursing.dao.DocumentDetailDicDao;
import com.iflytek.medicalsdk_nursing.dao.MappingDao;
import com.iflytek.medicalsdk_nursing.dao.OptionDicDao;
import com.iflytek.medicalsdk_nursing.domain.BusinessDataInfo;
import com.iflytek.medicalsdk_nursing.domain.DocumentDetailDic;
import com.iflytek.medicalsdk_nursing.domain.MappingInfo;
import com.iflytek.medicalsdk_nursing.domain.OptionDic;
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

public class DataDealUtil {

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
    //结果集
    private List<WSData> wsDataList = new ArrayList<>();
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
    //映射关系Dao类
    private MappingDao mappingDao;
    //文书字典Dao
    private DocumentDetailDicDao documentDetailDicDao;
    //选项字典Dao
    private OptionDicDao optionDicDao;

    /**
     * 数据处理
     * @param context
     * @param resultStr
     */
    public DataDealUtil(Context context,String resultStr){
        this.mContext = context;
        mappingDao = new MappingDao(context);
        documentDetailDicDao = new DocumentDetailDicDao(context);
        optionDicDao = new OptionDicDao(context);
    }


    /**
     * 取出基本数据
     * @return
     */
    private void transDataForBase(String result) throws JSONException {
        jsonObject = new JSONObject(result);
        service = jsonObject.optString("service");
        JSONObject semanticObject = jsonObject.optJSONObject("semantic");
        if (semanticObject != null && semanticObject.has("slots")) {
            JSONObject slotsObject = semanticObject.optJSONObject("slots");
            Iterator<String> iterator = slotsObject.keys();
            bed = slotsObject.optString("bed");
            type = slotsObject.optString("type");
            name = slotsObject.optString("name");
        }
    }

    /**
     * 循环便利取出wsData
     * @return
     */
    private List<WSData> doWhileForWsData(Iterator<String> iterator,JSONObject slotsObject) throws ParseException {

        List<WSData> wsDataList = new ArrayList<>();
        while (iterator.hasNext()){
            key = iterator.next();
            value = slotsObject.optString(key);
            //处理时间格式
            if (value.contains("date")) {
                String time = "00:00:00";
                if (StringUtils.isNotBlank(slotsObject.optJSONObject(key).optString("time"))){
                    time = slotsObject.optJSONObject(key).optString("time");
                }
                value = slotsObject.optJSONObject(key).optString("date") +" " + time;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(NISDATEFORMAT);
                Date date = dateFormat.parse(value);
                value = simpleDateFormat.format(date);
            }
            if (StringUtils.isEquals(key, "type") || StringUtils.isEquals(key, "bed")||StringUtils.isEquals(key,"name")) {
                continue;
            }
            //组装数据
            WSData wsData = new WSData();
            //根据映射关系表设置项目值
            MappingInfo mappingInfo = mappingDao.getMappingDic(key);
            //项目名确定
            wsData.setName(mappingInfo.getValue());
            DocumentDetailDic documentDetailDic = documentDetailDicDao.getDocumentDetailDic(mappingInfo.getValue());
            String[] values;
            if (value.contains(",")) {
                values = value.split(",");
            } else {
                values = new String[]{value};
            }
            for (String valueStr: values){
                if (documentDetailDic != null) {
                    //项目具有相关ID
                    wsData.setID(documentDetailDic.getItemID());
                    if (StringUtils.isNotBlank(documentDetailDic.getCodeID())) {
                        //判断选项是值还是选择项
                        OptionDic optionDic = optionDicDao.getOptionDic(documentDetailDic.getCodeID(), valueStr);
                        if (optionDic != null) {
                            wsData.setValue(optionDic.getOptCode());
                            wsData.setValueCaption(optionDic.getOptName());
                        } else {
                            wsData.setValueCaption(valueStr);
                        }
                    } else {
                        wsData.setValue(valueStr);
                    }
                } else {
                    wsData.setValue(valueStr);
                }
                wsDataList.add(wsData);
            }
        }
        return wsDataList;
    }




}
