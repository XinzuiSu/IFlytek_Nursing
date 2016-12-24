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

    public ProfessionalDataTraffic(DocumentDetailDicDao mDocumentDetailDicDao,OptionDicDao mOptionDicDao){
        this.documentDetailDicDao = mDocumentDetailDicDao;
        this.optionDicDao = mOptionDicDao;
        String[] proStrs = new String[]{"pregnancies","childbirth","abactio","cxms","medicine","rtqk","edemaSite","edemaDegree","scqk","allergicfood","allergicdrug","ysxz","fetus","birth","gqtime","zctime","urineproteinLevel","urineSugarLevel","smokenum","drinknum","defecationnum"};
        this.proList = Arrays.asList(proStrs);
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
        DocumentDetailDic documentDetailDic = documentDetailDicDao.getDocumentDetailDic(documentName);
        wsData.setName(documentName);
        if (StringUtils.isNotBlank(expandName)){
            wsData.setExpandName(expandName);
        }
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
                makeData("孕产史","孕","孕{0}次","孕{"+value+"}次");
                break;

            case 1:
                //"childbirth"
                makeData("孕产史","产","产{0}次","产{"+value+"}次");
                break;

            case 2:
                //"abactio"
                makeData("孕产史","人流","人流{0}次","人流{"+value+"}次");
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
                makeData("尿蛋白","程度",value,value);
                break;

            case 17:
                //"urineSugarLevel"
                makeData("尿糖","程度",value,value);
                break;

            case 18:
                //"smokenum"
                makeData("吸烟","","{0}支\\/天","{"+value+"}支/天");
                break;

            case 19:
                //"drinknum"
                makeData("饮酒 ","","{0}两\\/天","{"+value+"}两/天");
                break;
            case 20:
                //"defecationnum"
                makeData("排便 ","","{0}次\\/日","{"+value+"}次/天");
                break;
        }
        return wsData;
    }
}
