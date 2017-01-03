package com.iflytek.medicalsdk_nursing.util;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.medicalsdk_nursing.base.IFlyNursing;
import com.iflytek.medicalsdk_nursing.dao.MappingDao;
import com.iflytek.medicalsdk_nursing.domain.MappingInfo;
import com.iflytek.medicalsdk_nursing.view.StandingRecordActivity;

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
        String mappingStr = "[{\"key\":\"type\",\"value\":\"评估类型\"},{\"key\":\"rysj\",\"value\":\"入院时间\"},{\"key\":\"ryfs\",\"value\":\"入院方式\"},{\"key\":\"ryzd\",\"value\":\"入院诊断\"},{\"key\":\"nation\",\"value\":\"民族\"},{\"key\":\"eduDegree\",\"value\":\"文化程度\"},{\"key\":\"profession\",\"value\":\"职业\"},{\"key\":\"maritalStatus\",\"value\":\"婚姻状况\"},{\"key\":\"bscsz\",\"value\":\"病史陈述者（与患者关系）\"},{\"key\":\"parentsName\",\"value\":\"家长姓名\"},{\"key\":\"address\",\"value\":\"联系地址\"},{\"key\":\"phoneNumber\",\"value\":\"联系电话\"},{\"key\":\"lxrjdh\",\"value\":\"联系人及电话\"},{\"key\":\"pastHistory\",\"value\":\"既往史\"},{\"key\":\"allergicHistory\",\"value\":\"过敏史\"},{\"key\":\"scfs\",\"value\":\"生产方式\"},{\"key\":\"wyfs\",\"value\":\"喂养方式\"},{\"key\":\"bodyTemp\",\"value\":\"体温\"},{\"key\":\"pulse\",\"value\":\"脉搏\"},{\"key\":\"breath\",\"value\":\"呼吸\"},{\"key\":\"bloodPressure\",\"value\":\"血压\"},{\"key\":\"height\",\"value\":\"身长\"},{\"key\":\"headCircumference\",\"value\":\"头围\"},{\"key\":\"weight\",\"value\":\"体重\"},{\"key\":\"tzwcyy\",\"value\":\"体重未测原因\"},{\"key\":\"mind\",\"value\":\"神志\"},{\"key\":\"look\",\"value\":\"表情\"},{\"key\":\"vision\",\"value\":\"视力\"},{\"key\":\"hearing\",\"value\":\"听力\"},{\"key\":\"gtfs\",\"value\":\"沟通方式\"},{\"key\":\"ljnl\",\"value\":\"理解能力\"},{\"key\":\"oralMucosa\",\"value\":\"口腔黏膜\"},{\"key\":\"dentures\",\"value\":\"义齿\"},{\"key\":\"skin\",\"value\":\"皮肤\"},{\"key\":\"diet\",\"value\":\"饮食\"},{\"key\":\"sleep\",\"value\":\"睡眠\"},{\"key\":\"ywfzzz\",\"value\":\"药物辅助睡眠\"},{\"key\":\"smoke\",\"value\":\"吸烟\"},{\"key\":\"drink\",\"value\":\"饮酒\"},{\"key\":\"micturition\",\"value\":\"排尿\"},{\"key\":\"defecation\",\"value\":\"排便\"},{\"key\":\"diarrhea\",\"value\":\"腹泻\"},{\"key\":\"limbsActivity1\",\"value\":\"四肢活动\"},{\"key\":\"BradenQ\",\"value\":\"Braden评分\"},{\"key\":\"ddzc\",\"value\":\"跌倒坠床评分\"},{\"key\":\"Morse\",\"value\":\"Morse评分\"},{\"key\":\"gdhl\",\"value\":\"管道滑脱评分\"},{\"key\":\"pain\",\"value\":\"疼痛评分\"},{\"key\":\"emotion\",\"value\":\"情绪\"},{\"key\":\"familySupport\",\"value\":\"家庭支持\"},{\"key\":\"familyAttitude\",\"value\":\"家属态度\"},{\"key\":\"emotionalState\",\"value\":\"情绪状态\"},{\"key\":\"verbalSkills\",\"value\":\"语言能力\"},{\"key\":\"fontanelle\",\"value\":\"囟门\"},{\"key\":\"lips\",\"value\":\"口唇\"},{\"key\":\"pregnancies\",\"value\":\"孕{0}次\"},{\"key\":\"childbirth\",\"value\":\"产{0}次\"},{\"key\":\"abactio\",\"value\":\"人流{0}次\"},{\"key\":\"LMP\",\"value\":\"末次月经\"},{\"key\":\"dueDate\",\"value\":\"预产期\"},{\"key\":\"yqcx\",\"value\":\"孕期出血\"},{\"key\":\"yqyy\",\"value\":\"孕期用药\"},{\"key\":\"cxms\",\"value\":\"出血描述\"},{\"key\":\"medicine\",\"value\":\"药物名称\"},{\"key\":\"mrwyzs\",\"value\":\"母乳喂养知识\"},{\"key\":\"fetusPosition\",\"value\":\"胎位\"},{\"key\":\"FHR\",\"value\":\"胎心\"},{\"key\":\"fetalMovement\",\"value\":\"胎动\"},{\"key\":\"ptycms\",\"value\":\"胚胎异常描述\"},{\"key\":\"foetalMembrane\",\"value\":\"胎膜\"},{\"key\":\"yscd\",\"value\":\"羊水长度\"},{\"key\":\"ydcx\",\"value\":\"阴道出血\"},{\"key\":\"zigongshousuo\",\"value\":\"宫缩\"},{\"key\":\"rffy\",\"value\":\"乳房发育\"},{\"key\":\"rtqk\",\"value\":\"乳头情况\"},{\"key\":\"edema\",\"value\":\"水肿\"},{\"key\":\"edemaSite\",\"value\":\"部位\"},{\"key\":\"edemaDegree\",\"value\":\"水肿程度\"},{\"key\":\"protein\",\"value\":\"蛋白质\"},{\"key\":\"urineSugar\",\"value\":\"尿糖\"},{\"key\":\"scqk\",\"value\":\"生产情况\"},{\"key\":\"consciousness\",\"value\":\"意识\"},{\"key\":\"cry\",\"value\":\"哭声\"},{\"key\":\"limbsActivity2\",\"value\":\"肢体活动\"},{\"key\":\"muscularTension\",\"value\":\"肌张力\"},{\"key\":\"hug\",\"value\":\"拥抱\"},{\"key\":\"suck\",\"value\":\"吸吮\"},{\"key\":\"hold\",\"value\":\"握持\"},{\"key\":\"foraging\",\"value\":\"觅食\"},{\"key\":\"swallow\",\"value\":\"吞咽\"},{\"key\":\"complexion\",\"value\":\"面色\"},{\"key\":\"alimentarySystem\",\"value\":\"消化系统\"},{\"key\":\"umbilicalCord\",\"value\":\"脐带\"},{\"key\":\"periumbilical\",\"value\":\"脐周\"},{\"key\":\"zkqk\",\"value\":\"专科情况\"},{\"key\":\"identification\",\"value\":\"身份确认方式\"},{\"key\":\"notice\",\"value\":\"住院告知\"},{\"key\":\"HOD\",\"value\":\"住院天数\"},{\"key\":\"stoolExcretion\",\"value\":\"大便次数\"},{\"key\":\"enemaBefore\",\"value\":\"灌肠前大便次数\"},{\"key\":\"enemaAfter\",\"value\":\"灌肠后大便次数\"},{\"key\":\"urineDischarge\",\"value\":\"尿量\"},{\"key\":\"dnbs\",\"value\":\"导尿标识\"},{\"key\":\"outVolume\",\"value\":\"出量\"},{\"key\":\"inVolume\",\"value\":\"入量\"},{\"key\":\"drugAllergy\",\"value\":\"药物过敏\"},{\"key\":\"datetime\",\"value\":\"时间\"},{\"key\":\"tempPhysics\",\"value\":\"物理降温\"},{\"key\":\"tempReview\",\"value\":\"复查\"},{\"key\":\"tempAnus\",\"value\":\"肛温\"},{\"key\":\"tempMouth\",\"value\":\"口表\"},{\"key\":\"tempArmpit\",\"value\":\"腋表\"},{\"key\":\"tempEar\",\"value\":\"耳温\"},{\"key\":\"heartRate\",\"value\":\"心率\"},{\"key\":\"fzhx\",\"value\":\"辅助呼吸\"},{\"key\":\"max\",\"value\":\"上标\"},{\"key\":\"min\",\"value\":\"下标\"},{\"key\":\"qbxl\",\"value\":\"起搏心率\"},{\"key\":\"score\",\"value\":\"预警总得分\"},{\"key\":\"bed\",\"value\":\"床号\"},{\"key\":\"name\",\"value\":\"患者\"},{\"key\":\"others\",\"value\":\"其他\"},{\"key\":\"allergicfood\",\"value\":\"食物\"},{\"key\":\"position\",\"value\":\"部位\"},{\"key\":\"range\",\"value\":\"范围\"},{\"key\":\"sex\",\"value\":\"性别\"},{\"key\":\"age\",\"value\":\"年龄\"},{\"key\":\"zyh\",\"value\":\"住院号\"},{\"key\":\"urineprotein\",\"value\":\"尿蛋白\"},{\"key\":\"ywfzsm\",\"value\":\"药物辅助睡眠\"},{\"key\":\"allergicdrug\",\"value\":\"药物\"},{\"key\":\"category\",\"value\":\"科别\"},{\"key\":\"adlscore\",\"value\":\"ADL评分\"},{\"key\":\"ysxz\",\"value\":\"羊水性状\"},{\"key\":\"fetus\",\"value\":\"胎数\"},{\"key\":\"birth\",\"value\":\"产数\"},{\"key\":\"gqtime\",\"value\":\"过期周数\"},{\"key\":\"zctime\",\"value\":\"早产周数\"},{\"key\":\"urineproteinLevel\",\"value\":\"尿蛋白程度\"},{\"key\":\"urineSugarLevel\",\"value\":\"尿糖程度\"},{\"key\":\"smokenum\",\"value\":\"吸烟支数\"},{\"key\":\"drinknum\",\"value\":\"喝酒两数\"},{\"key\":\"defecationnum\",\"value\":\"排便次数\"}]";
        MappingDao mappingDao = new MappingDao(mContext);
        mappingDao.deleteMappingInfo();
        List<MappingInfo> mappingInfoList = new Gson().fromJson(mappingStr,new TypeToken<List<MappingInfo>>(){}.getType());
        mappingDao.saveOrUpdateMappingInfoList(mappingInfoList);
    }

    /**
     * 开始记录
     */
    public void startRecord(){
        Intent intent = new Intent(mContext,StandingRecordActivity.class);
        mContext.startActivity(intent);
        IFlyNursing.getInstance().getNursingListener().onStartListener(true);
    }


    public void setNursingListener(NursingListener nursingListener){
        IFlyNursing.getInstance().setNursingListener(nursingListener);
    }


}
