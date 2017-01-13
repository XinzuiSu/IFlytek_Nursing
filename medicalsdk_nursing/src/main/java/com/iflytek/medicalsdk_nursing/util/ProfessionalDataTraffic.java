package com.iflytek.medicalsdk_nursing.util;

import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.medicalsdk_nursing.dao.DocumentDetailDicDao;
import com.iflytek.medicalsdk_nursing.dao.OptionDicDao;
import com.iflytek.medicalsdk_nursing.domain.DocumentDetailDic;
import com.iflytek.medicalsdk_nursing.domain.MappingInfo;
import com.iflytek.medicalsdk_nursing.domain.OptionDic;
import com.iflytek.medicalsdk_nursing.domain.WSData;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Title: com.iflytek.medicalsdk_nursing.util
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/12/7-上午11:26,  All rights reserved
 * @Description: TODO 特殊值特殊处理（专业的！！）;
 * @author: chenzhilei
 * @data: 2016/12/7 上午11:26
 * @version: V1.0
 */

public class ProfessionalDataTraffic {

    private DocumentDetailDicDao documentDetailDicDao;

    private OptionDicDao optionDicDao;

    private WSData wsData;

    private List<String> proList;

    private String type;

    public ProfessionalDataTraffic(DocumentDetailDicDao mDocumentDetailDicDao,OptionDicDao mOptionDicDao,String nmrID){
        this.documentDetailDicDao = mDocumentDetailDicDao;
        this.optionDicDao = mOptionDicDao;
        String[] proStrs = new String[]{"pregnancies","childbirth","abactio","cxms","medicine","rtqk","edemaSite","edemaDegree","scqk","allergicfood","allergicdrug","ysxz","fetus","birth","gqtime","zctime","urineproteinLevel","urineSugarLevel","smokenum","drinknum","defecationnum","smoke","drink","diarrhea","position","lxrjdh","sleep","urineprotein","skin","weight","zigongshousuo","adlscore"};
        this.proList = Arrays.asList(proStrs);
        this.type = nmrID;
    }

    /**
     * 是否需要特殊处理
     * @param value
     * @return
     */
    public boolean isDataProfessional(String value){
        return proList.contains(value);
    }


    /**
     * 制造数据
     * @param documentName
     * @param expandName
     * @param optionName
     * @param wsValue
     */
    private void makeData(String documentName,String expandName,String optionName,String wsValue){
        DocumentDetailDic documentDetailDic = documentDetailDicDao.getDocumentDetailDic(documentName,type);
        wsData.setName(documentName);
        if (StringUtils.isNotBlank(expandName)){
            wsData.setExpandName(expandName);
        }
        if (documentDetailDic!=null){
            wsData.setID(StringUtils.nullStrToEmpty(documentDetailDic.getItemID()));
            if (StringUtils.isNotBlank(documentDetailDic.getCodeID())){
                OptionDic optionDic = optionDicDao.getOptionDic(documentDetailDic.getCodeID(), optionName);
                if (optionDic!=null){
                    wsData.setValue(optionDic.getOptCode());
                    wsData.setValueCaption(wsValue);
                }else {
                    wsData.setValueCaption(wsValue);
                }
            }else {
                wsData.setValue(wsValue);
            }
        }
    }


    /**
     * 制造数据
     * @param documentName
     * @param expandName
     * @param wsValue
     */
    private void makeNumberData(String documentName,String expandName,String wsValue){
        DocumentDetailDic documentDetailDic = documentDetailDicDao.getDocumentDetailDic(documentName,type);
        wsData.setName(documentName);
        if (StringUtils.isNotBlank(expandName)){
            wsData.setExpandName(expandName);
        }
        if (documentDetailDic!=null){
            wsData.setID(StringUtils.nullStrToEmpty(documentDetailDic.getItemID()));
            wsData.setValue(wsValue);
        }
    }



    /**
     * 制造数据
     * @param documentName
     * @param expandName
     * @param optionName
     * @param wsValue
     * @param codeID
     */
    private void makeDataWithID(String documentName,String expandName,String optionName,String wsValue,String codeID){
        DocumentDetailDic documentDetailDic = documentDetailDicDao.getDocumentDetailDic(documentName,type,codeID);
        wsData.setName(documentName);
        if (StringUtils.isNotBlank(expandName)){
            wsData.setExpandName(expandName);
        }
        if (documentDetailDic!=null){
            wsData.setID(StringUtils.nullStrToEmpty(documentDetailDic.getItemID()));
            if (StringUtils.isNotBlank(documentDetailDic.getCodeID())){
                OptionDic optionDic = optionDicDao.getOptionDic(documentDetailDic.getCodeID(), optionName);
                if (optionDic!=null){
                    wsData.setValue(optionDic.getOptCode());
                    wsData.setValueCaption(wsValue);
                }else {
                    wsData.setValueCaption(wsValue);
                }
            }else {
                wsData.setValue(wsValue);
            }
        }
    }

    /**
     * 转换数据
     * @param mappingInfo
     * @param value
     * @return
     */
    public WSData trafficData(MappingInfo mappingInfo,String value){
        wsData = new WSData();
        switch (proList.indexOf(mappingInfo.getKey())){
            //怀孕{0}次
            case 0:
                //"pregnancies"
                makeData("孕产史","孕","孕{0}次",value);
                break;

            case 1:
                //"childbirth"
                makeData("孕产史","产","产{0}次",value);
                break;

            case 2:
                //"abactio"
                makeData("孕产史","人流","人流{0}次",value);
                break;

            case 3:
                //"cxms"
                makeData("孕期出血","出血描述","出血描述",value);
                break;

            case 4:
                //"medicine"
                makeData("药物过敏","药物名称","药物名称",value);
                break;

            case 5:
                //"rtqk"
                makeData("乳房发育","乳头情况","乳头情况",value);
                break;

            case 6:
                //"edemaSite"
                makeData("水肿","部位","(部位：",value);
                break;

            case 7:
                //"edemaDegree"
                makeData("水肿","程度","水肿程度",value);
                break;

            case 8:
                //"scqk"
                //特殊情况A061数据问题，卫宁需要处理
                makeData("生产情况","",value,value);
                break;

            case 9:
                //"allergicfood"
                makeData("过敏史","食物","食物",value);
                break;

            case 10:
                //"allergicdrug"
                makeData("过敏史","药物","药物",value);
                break;

            case 11:
                //"ysxz"
                makeData("胎膜","羊水性状","羊水长度",value);
                break;

            case 12:
                //"fetus"
                makeData("生产情况","胎","第{0}胎","第{"+value+"}胎");
                break;

            case 13:
                //"birth"
                makeData("生产情况","产","{0}产","{"+value+"}产");
                break;

            case 14:
                //"gqtime"
                makeData("生产情况","过期"," 过期（{0}）周"," 过期（{"+value+"}）周");
                break;

            case 15:
                //"zctime"
                makeData("生产情况","早产","（{0}）周","（{"+value+"}）周");
                break;

            case 16:
                //"urineproteinLevel"
                makeData("蛋白质","尿蛋白",value,value);
                break;

            case 17:
                //"urineSugarLevel"
                makeData("尿糖","程度",value,value);
                break;

            case 18:
                //"smokenum"
                makeData("吸烟","","{0}支/天",value);
                break;

            case 19:
                //"drinknum"
                makeData("饮酒","","{0}两/天",value);
                break;
            case 20:
                //"defecationnum"
                makeData("排便","","{0}次/日",value);
                break;
            case 21:
                //"smoke"
                String smokeValue = StringUtils.isEquals(value,"无")?"否":"是";
                makeData("吸烟","",smokeValue,smokeValue);
                break;
            case 22:
                //"drink"
                String drinkValue = StringUtils.isEquals(value,"无")?"否":"是";
                makeData("饮酒","",drinkValue,drinkValue);
                break;
            case 23:
                //diarrhea
                makeData("排便","腹泻","{0}次/日",value);
                break;
            case 24:
                //position
                makeData("皮肤","部位","部位",value);
                break;
            case 25:
                //lxrjdh
                if (value.contains("`")){
                    value = value.replace("`"," ");
                }
                makeData("联系人及电话","","",value);
                break;
            case 26:
                //sleep
                Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
                if (pattern.matcher(value).matches()){
                    makeData("睡眠","","，每日睡眠{0}小时",value);
                }else {
                    makeData("睡眠","",value,value);
                }
                break;
            case 27:
                //urineprotein
                makeData("蛋白质","尿蛋白",value,value);
                break;
            case 28:
                //skin
                String skinValue = value;
                if (value.contains("黄染")){
                    if (value.contains("轻")){
                        skinValue = "黄染（{0}轻";
                    }else if (value.contains("中")){
                        skinValue = "中";
                    }else if (value.contains("重")){
                        skinValue = "重）";
                    }else{
                        skinValue = "黄染（{0}轻";
                    }
                }
                makeData("皮肤","",skinValue,value);
                break;
            case 29:
                //weight
                makeNumberData("体重","",value);
                break;
            case 30:
                if (value.contains("规律")){
                    makeDataWithID("宫缩","",value,value,"A038");
                }else{
                    makeDataWithID("宫缩","",value,value,"A020");
                }
                break;
            case 31:
                //adlscore
                int adlScore = Integer.parseInt(value);
                if (adlScore == 100){
                    makeData("ADL评分","","自理(100分)","自理(100分)");
                }else if (60<=adlScore&&adlScore<=90){
                    makeData("ADL评分","","轻度依赖(60-99分)","轻度依赖(60-99分)");
                }else if (41<=adlScore&&adlScore<=59){
                    makeData("ADL评分","","中度依赖(41-59分)","中度依赖(41-59分)");
                }else if (0<=adlScore&&adlScore<=40){
                    makeData("ADL评分","","重度依赖(0-40分)","重度依赖(0-40分)");
                }else {
                    makeData("ADL评分","","自理(100分)","自理(100分)");
                }
                break;
        }
        return wsData;
    }
}
