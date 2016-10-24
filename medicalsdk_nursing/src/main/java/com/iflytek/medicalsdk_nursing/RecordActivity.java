package com.iflytek.medicalsdk_nursing;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.medicalsdk_nursing.base.IFlyNursing;
import com.iflytek.medicalsdk_nursing.domain.BusinessDataInfo;
import com.iflytek.medicalsdk_nursing.domain.DocumentDetailDic;
import com.iflytek.medicalsdk_nursing.domain.DocumentDic;
import com.iflytek.medicalsdk_nursing.domain.OptionDic;
import com.iflytek.medicalsdk_nursing.domain.PatientInfo;
import com.iflytek.medicalsdk_nursing.domain.WSData;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: com.iflytek.medicalsdk_nursing
 * @Copyright: IFlytek Co., Ltd. Copyright 16/10/8-上午10:56,  All rights reserved
 * @Description: TODO 录音页面;
 * @author: chenzhilei
 * @data: 16/10/8 上午10:56
 * @version: V1.0
 */
public class RecordActivity extends Activity{

    private static String TAG = "Speech";

    /**
     * 录音按键
     */
    private ImageView voiceImage;
    /**
     * 结果文书
     */
    private TextView resultText;

    // 函数调用返回值
    private int ret = 0;

    private SpeechUnderstander mSpeechUnderstander;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        voiceImage = (ImageView) findViewById(R.id.record_voice_image);
        resultText = (TextView) findViewById(R.id.record_text_result);
        listView = (ListView) findViewById(R.id.record_listView);
        voiceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //播放
                speech();
            }
        });
        mSpeechUnderstander = new SpeechHelper(RecordActivity.this).getmSpeechUnderstander();
        PatientInfo patientInfo = new PatientInfo("0480392","04803921","221","0233154","1","1000654","5","张三","1989-09-21","男","12","00023","内科一病区","5646","中级","住院","2014-03-22","2014-06-11","123123","内科");
        PatientInfo patientInfo2 = new PatientInfo("0480393","04803922","222","0233155","1","1000654","5","李四","1989-09-21","男","12","00023","内科一病区","5646","中级","住院","2014-03-22","2014-06-11","123123","内科");
        List<PatientInfo> patientInfos =new ArrayList<>();
        patientInfos.add(patientInfo);
        patientInfos.add(patientInfo2);
        String patientStr = new Gson().toJson(patientInfos);
        Log.d("PATIENT",patientStr);

        DocumentDic documentDic = new DocumentDic("1001","入院评估单","A0002","");
        DocumentDic documentDic2 = new DocumentDic("1002","体温单","B0002","");
        List<DocumentDic> documentDics = new ArrayList<>();
        documentDics.add(documentDic);
        documentDics.add(documentDic2);
        String documentDicStr = new Gson().toJson(documentDics);
        Log.d("DOCUMENT",documentDicStr);

        DocumentDetailDic documentDetailDic = new DocumentDetailDic("1001","c1001","神志","6","zd001");
        DocumentDetailDic documentDetailDic2 = new DocumentDetailDic("1001","c1002","表情","6","zd002");
        DocumentDetailDic documentDetailDic3 = new DocumentDetailDic("1001","c1003","视力","6","zd003");
        DocumentDetailDic documentDetailDic4 = new DocumentDetailDic("1002","d1001","体温","3","");
        DocumentDetailDic documentDetailDic5 = new DocumentDetailDic("1002","d1002","脉搏","3","");
        List<DocumentDetailDic> documentDetailDics = new ArrayList<>();
        documentDetailDics.add(documentDetailDic);
        documentDetailDics.add(documentDetailDic2);
        documentDetailDics.add(documentDetailDic3);
        documentDetailDics.add(documentDetailDic4);
        documentDetailDics.add(documentDetailDic5);
        String documentDetailStr = new Gson().toJson(documentDetailDics);
        Log.d("DOCUMENTDETAIL",documentDetailStr);

        OptionDic optionDic = new OptionDic("zd001","63001","清楚","");
        OptionDic optionDic2 = new OptionDic("zd001","63002","嗜睡","");
        OptionDic optionDic3 = new OptionDic("zd001","63003","神志模糊","");
        OptionDic optionDic4 = new OptionDic("zd001","63004","昏睡","");
        OptionDic optionDic5 = new OptionDic("zd001","63004","浅昏迷","");
        OptionDic optionDic6 = new OptionDic("zd001","63004","深昏迷","");
        List<OptionDic> optionDics = new ArrayList<>();
        optionDics.add(optionDic);
        optionDics.add(optionDic2);
        optionDics.add(optionDic3);
        optionDics.add(optionDic4);
        optionDics.add(optionDic5);
        optionDics.add(optionDic6);
        String optionDicStr = new Gson().toJson(optionDics);
        Log.d("OPTION",optionDicStr);

        BusinessDataInfo businessDataInfo = new BusinessDataInfo();
        businessDataInfo.setSyxh(patientInfo.getHosID());
        businessDataInfo.setYexh("");
        businessDataInfo.setPatName(patientInfo.getPatName());
        businessDataInfo.setNmrCode("");
        businessDataInfo.setDate("2016-09-10");
        businessDataInfo.setRecorderDate("2016-10-21");
        WSData wsData = new WSData(documentDetailDic.getItemID(),documentDetailDic.getItemName(),optionDic.getOptName(),"神志描述");
        WSData wsData2 = new WSData(documentDetailDic2.getItemID(),documentDetailDic2.getItemName(),optionDic2.getOptName()," 表情描述");
        WSData wsData3 = new WSData(documentDetailDic4.getItemID(),documentDetailDic4.getItemName(),"36℃","体温");
        List<WSData> wsDatas = new ArrayList<>();
        wsDatas.add(wsData);
        wsDatas.add(wsData2);
        wsDatas.add(wsData3);
        businessDataInfo.setWsDataList(wsDatas);
        String dataStr = new Gson().toJson(businessDataInfo);
        Log.d("DATA",dataStr);

    }


    private void speech() {
        if (mSpeechUnderstander.isUnderstanding()) {// 开始前检查状态
            mSpeechUnderstander.stopUnderstanding();
            Toast.makeText(RecordActivity.this,"停止录音",Toast.LENGTH_SHORT).show();
        } else {
            ret = mSpeechUnderstander.startUnderstanding(mSpeechUnderstanderListener);
            if (ret != 0) {
                Toast.makeText(this, "语义理解失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "请说话", Toast.LENGTH_SHORT).show();
            }
        }

    }



    /**
     * 语义理解回调。
     */
    private SpeechUnderstanderListener mSpeechUnderstanderListener = new SpeechUnderstanderListener() {

        @Override
        public void onResult(final UnderstanderResult result) {
            resultText.append(result.getResultString());
            IFlyNursing.getInstance().getNursingListener().onDataSavedListener(result.getResultString());
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            //showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, data.length + "");
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));

        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
//             以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            		Log.d(TAG, "session id =" + sid);
            	}
        }
    };





    private void showTip(String text){
        Toast.makeText(RecordActivity.this,text,Toast.LENGTH_LONG).show();
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                //播放
                speech();
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                //暂停
                speech();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
