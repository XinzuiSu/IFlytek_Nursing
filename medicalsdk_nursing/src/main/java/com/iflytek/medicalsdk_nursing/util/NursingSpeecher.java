package com.iflytek.medicalsdk_nursing.util;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.medicalsdk_nursing.base.IFlyNursing;
import com.iflytek.medicalsdk_nursing.dao.MappingDao;
import com.iflytek.medicalsdk_nursing.domain.MappingInfo;
import com.iflytek.medicalsdk_nursing.view.RecordActivity;

import java.util.List;

/**
 * @Title: com.iflytek.medicalsdk_nursing
 * @Copyright: IFlytek Co., Ltd. Copyright 16/10/9-上午10:03,  All rights reserved
 * @Description: TODO 护理语音助手实际业务处理;
 * @author: chenzhilei
 * @data: 16/10/9 上午10:03
 * @version: V1.0
 */
public class NursingSpeecher {

    private Context mContext;

    public NursingSpeecher(Context context){
        this.mContext  = context;
        saveMappingInfo();
    }

    /**
     * 保存映射关系字典
     */
    private void saveMappingInfo() {
        //TODO 目前阶段暂时使用静态映射，试用阶段通过后采用动态映射
        String mappingStr = "[\n" +
                "{\"key\":\"type\",\"value\":\"评估类型\"},\n" +
                "{\"key\":\"rysj\",\"value\":\"入院时间\"},\n" +
                "{\"key\":\"ryfs\",\"value\":\"入院方式\"},\n" +
                "{\"key\":\"ryzd\",\"value\":\"入院诊断\"},\n" +
                "{\"key\":\"nation\",\"value\":\"民族\"},\n" +
                "{\"key\":\"eduDegree\",\"value\":\"文化程度\"},\n" +
                "{\"key\":\"profession\",\"value\":\"职业\"},\n" +
                "{\"key\":\"maritalStatus\",\"value\":\"婚姻状况\"},\n" +
                "{\"key\":\"bscsz\",\"value\":\"病史陈述者(与患者关系)\"},\n" +
                "{\"key\":\"parentsName\",\"value\":\"家长姓名\"},\n" +
                "{\"key\":\"address\",\"value\":\"联系地址\"},\n" +
                "{\"key\":\"phoneNumber\",\"value\":\"联系电话\"},\n" +
                "{\"key\":\"lxrjdh\",\"value\":\"联系人及电话\"},\n" +
                "{\"key\":\"pastHistory\",\"value\":\"既往史\"},\n" +
                "{\"key\":\"allergicHistory\",\"value\":\"过敏史\"},\n" +
                "{\"key\":\"scfs\",\"value\":\"生产方式\"},\n" +
                "{\"key\":\"wyfs\",\"value\":\"喂养方式\"},\n" +
                "{\"key\":\"bodyTemp\",\"value\":\"体温\"},\n" +
                "{\"key\":\"pulse\",\"value\":\"脉搏\"},\n" +
                "{\"key\":\"breath\",\"value\":\"呼吸\"},\n" +
                "{\"key\":\"bloodPressure\",\"value\":\"血压\"},\n" +
                "{\"key\":\"height\",\"value\":\"身长\"},\n" +
                "{\"key\":\"headCircumference\",\"value\":\"头围\"},\n" +
                "{\"key\":\"weight\",\"value\":\"体重/体重(kg)\"},\n" +
                "{\"key\":\"tzwcyy\",\"value\":\"体重未测原因\"},\n" +
                "{\"key\":\"mind\",\"value\":\"神志\"},\n" +
                "{\"key\":\"look\",\"value\":\"表情\"},\n" +
                "{\"key\":\"vision\",\"value\":\"视力\"},\n" +
                "{\"key\":\"hearing\",\"value\":\"听力\"},\n" +
                "{\"key\":\"gtfs\",\"value\":\"沟通方式\"},\n" +
                "{\"key\":\"ljnl\",\"value\":\"理解能力\"},\n" +
                "{\"key\":\"oralMucosa\",\"value\":\"口腔黏膜\"},\n" +
                "{\"key\":\"dentures\",\"value\":\"义齿\"},\n" +
                "{\"key\":\"skin\",\"value\":\"皮肤\"},\n" +
                "{\"key\":\"diet\",\"value\":\"饮食\"},\n" +
                "{\"key\":\"sleep\",\"value\":\"睡眠\"},\n" +
                "{\"key\":\"ywfzzz\",\"value\":\"药物辅助治疗\"},\n" +
                "{\"key\":\"smoke\",\"value\":\"吸烟\"},\n" +
                "{\"key\":\"drink\",\"value\":\"饮酒\"},\n" +
                "{\"key\":\"micturition\",\"value\":\"排尿\"},\n" +
                "{\"key\":\"defecation\",\"value\":\"排便\"},\n" +
                "{\"key\":\"diarrhea\",\"value\":\"腹泻\"},\n" +
                "{\"key\":\"limbsActivity1\",\"value\":\"四肢活动\"},\n" +
                "{\"key\":\"BradenQ\",\"value\":\"Braden-Q评分\"},\n" +
                "{\"key\":\"ddzc\",\"value\":\"跌倒坠床评分\"},\n" +
                "{\"key\":\"Morse\",\"value\":\"Morse评分\"},\n" +
                "{\"key\":\"gdhl\",\"value\":\"管道滑落评分\"},\n" +
                "{\"key\":\"pain\",\"value\":\"疼痛评分\"},\n" +
                "{\"key\":\"emotion\",\"value\":\"情绪\"},\n" +
                "{\"key\":\"familySupport\",\"value\":\"家庭支持\"},\n" +
                "{\"key\":\"familyAttitude\",\"value\":\"家属态度\"},\n" +
                "{\"key\":\"emotionalState\",\"value\":\"情绪状态\"},\n" +
                "{\"key\":\"verbalSkills\",\"value\":\"语言能力\"},\n" +
                "{\"key\":\"fontanelle\",\"value\":\"卤门\"},\n" +
                "{\"key\":\"lips\",\"value\":\"口唇\"},\n" +
                "{\"key\":\"pregnancies\",\"value\":\"孕( )次\"},\n" +
                "{\"key\":\"childbirth\",\"value\":\"产( )次\"},\n" +
                "{\"key\":\"abactio\",\"value\":\"人流( )次\"},\n" +
                "{\"key\":\"LMP\",\"value\":\"末次月经\"},\n" +
                "{\"key\":\"dueDate\",\"value\":\"预产期\"},\n" +
                "{\"key\":\"yqcx\",\"value\":\"孕期出血\"},\n" +
                "{\"key\":\"yqyy\",\"value\":\"孕期用药\"},\n" +
                "{\"key\":\"cxms\",\"value\":\"出血描述\"},\n" +
                "{\"key\":\"medicine\",\"value\":\"药物名称\"},\n" +
                "{\"key\":\"mrwyzs\",\"value\":\"母乳喂养知识\"},\n" +
                "{\"key\":\"fetusPosition \",\"value\":\"胎位\"},\n" +
                "{\"key\":\"FHR\",\"value\":\"胎心\"},\n" +
                "{\"key\":\"fetalMovement\",\"value\":\"胎动\"},\n" +
                "{\"key\":\"ptycms\",\"value\":\"胚胎异常描述\"},\n" +
                "{\"key\":\"foetalMembrane\",\"value\":\"胎膜\"},\n" +
                "{\"key\":\"yscd\",\"value\":\"羊水长度\"},\n" +
                "{\"key\":\"ydcx\",\"value\":\"阴道出血\"},\n" +
                "{\"key\":\"zigongshousuo\",\"value\":\"宫缩\"},\n" +
                "{\"key\":\"rffy\",\"value\":\"乳房发育\"},\n" +
                "{\"key\":\"rtqk\",\"value\":\"乳头情况\"},\n" +
                "{\"key\":\"edema\",\"value\":\"水肿\"},\n" +
                "{\"key\":\"edemaSite\",\"value\":\"部位\"},\n" +
                "{\"key\":\"edemaDegree\",\"value\":\"水肿程度\"},\n" +
                "{\"key\":\"protein\",\"value\":\"蛋白质\"},\n" +
                "{\"key\":\"urineSugar\",\"value\":\"尿糖\"},\n" +
                "{\"key\":\"scqk\",\"value\":\"生产情况\"},\n" +
                "{\"key\":\"consciousness\",\"value\":\"意识/意识水平\"},\n" +
                "{\"key\":\"cry\",\"value\":\"哭声\"},\n" +
                "{\"key\":\"limbsActivity2\",\"value\":\"肢体活动\"},\n" +
                "{\"key\":\"muscularTension\",\"value\":\"肌张力\"},\n" +
                "{\"key\":\"hug\",\"value\":\"拥抱\"},\n" +
                "{\"key\":\"suck\",\"value\":\"吸吮\"},\n" +
                "{\"key\":\"hold\",\"value\":\"握持\"},\n" +
                "{\"key\":\"foraging\",\"value\":\"觅食\"},\n" +
                "{\"key\":\"swallow\",\"value\":\"吞咽\"},\n" +
                "{\"key\":\"complexion\",\"value\":\"面色\"},\n" +
                "{\"key\":\"alimentarySystem\",\"value\":\"消化系统\"},\n" +
                "{\"key\":\"umbilicalCord\",\"value\":\"脐带\"},\n" +
                "{\"key\":\"periumbilical \",\"value\":\"脐周\"},\n" +
                "{\"key\":\"zkqk\",\"value\":\"专科情况\"},\n" +
                "{\"key\":\"identification\",\"value\":\"身份确认方式\"},\n" +
                "{\"key\":\"notice\",\"value\":\"住院告知\"},\n" +
                "{\"key\":\"HOD\",\"value\":\"住院天数\"},\n" +
                "{\"key\":\"stoolExcretion\",\"value\":\"大便次数\"},\n" +
                "{\"key\":\"enemaBefore\",\"value\":\"灌肠前大便次数\"},\n" +
                "{\"key\":\"enemaAfter\",\"value\":\"灌肠后大便次数\"},\n" +
                "{\"key\":\"urineDischarge\",\"value\":\"尿量(ml)\"},\n" +
                "{\"key\":\"dnbs\",\"value\":\"导尿标识\"},\n" +
                "{\"key\":\"outVolume\",\"value\":\"出量(ml)\"},\n" +
                "{\"key\":\"inVolume\",\"value\":\"入量(ml)\"},\n" +
                "{\"key\":\"drugAllergy\",\"value\":\"药物过敏\"},\n" +
                "{\"key\":\"datetime\",\"value\":\"时间\"},\n" +
                "{\"key\":\"tempPhysics\",\"value\":\"物理降温\"},\n" +
                "{\"key\":\"tempReview\",\"value\":\"复查\"},\n" +
                "{\"key\":\"tempAnus\",\"value\":\"肛温\"},\n" +
                "{\"key\":\"tempMouth\",\"value\":\"口表\"},\n" +
                "{\"key\":\"tempArmpit\",\"value\":\"腋表\"},\n" +
                "{\"key\":\"tempEar\",\"value\":\"耳温\"},\n" +
                "{\"key\":\"heartRate\",\"value\":\"心率\"},\n" +
                "{\"key\":\"fzhx\",\"value\":\"辅助呼吸\"},\n" +
                "{\"key\":\"max\",\"value\":\"上标\"},\n" +
                "{\"key\":\"min\",\"value\":\"下标\"},\n" +
                "{\"key\":\"qbxl\",\"value\":\"起搏心率\"},\n" +
                "{\"key\":\"score\",\"value\":\"预警总得分\"},\n" +
                "{\"key\":\"bed\",\"value\":\"床号\"},\n" +
                "{\"key\":\"name\",\"value\":\"患者\"}\n" +
                "]";
        MappingDao mappingDao = new MappingDao(mContext);
        mappingDao.deleteMappingInfo();
        List<MappingInfo> mappingInfoList = new Gson().fromJson(mappingStr,new TypeToken<List<MappingInfo>>(){}.getType());
        mappingDao.saveOrUpdateMappingInfoList(mappingInfoList);
    }

    /**
     * 开始记录
     */
    public void startRecord(){
        Intent intent = new Intent(mContext,RecordActivity.class);
        mContext.startActivity(intent);
        IFlyNursing.getInstance().getNursingListener().onStartListener(true);
    }


    public void setNursingListener(NursingListener nursingListener){
        IFlyNursing.getInstance().setNursingListener(nursingListener);
    }


}
