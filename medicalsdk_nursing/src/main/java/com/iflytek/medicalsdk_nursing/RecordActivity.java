package com.iflytek.medicalsdk_nursing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.android.framework.volley.Request;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.medicalsdk_nursing.adapter.RecordAdapter;
import com.iflytek.medicalsdk_nursing.base.IFlyNursing;
import com.iflytek.medicalsdk_nursing.dao.PatientInfoDao;
import com.iflytek.medicalsdk_nursing.domain.BusinessDataInfo;
import com.iflytek.medicalsdk_nursing.domain.DocumentDetailDic;
import com.iflytek.medicalsdk_nursing.domain.DocumentDic;
import com.iflytek.medicalsdk_nursing.domain.OptionDic;
import com.iflytek.medicalsdk_nursing.domain.PatientInfo;
import com.iflytek.medicalsdk_nursing.domain.WSData;
import com.iflytek.medicalsdk_nursing.net.SoapResult;
import com.iflytek.medicalsdk_nursing.net.VolleyTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
     * 时间基础格式化
     */
    public static final String DEAFULTFORMAT = "yyyy-MM-dd HH:mm:ss";

    private Spinner spinner;

    // 函数调用返回值
    private int ret = 0;

    private SpeechUnderstander mSpeechUnderstander;

    private ListView listView;

    private List<BusinessDataInfo> businessDataInfoList;

    private RecordAdapter recordAdapter;

    private GLWaveformView glWaveFormView;

    private TextView timeText;
    /**
     * 当前编辑患者标记
     */
    private int position;

    private PatientInfoDao patientInfoDao;
    /**
     * 返回
     */
    private LinearLayout backLayout;

    private LinearLayout saveLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        listView = (ListView) findViewById(R.id.record_listView);
        spinner = (Spinner) findViewById(R.id.record_spinner);
        glWaveFormView = (GLWaveformView) findViewById(R.id.record_voice_image);
        timeText = (TextView) findViewById(R.id.recored_time_text);
        backLayout = (LinearLayout) findViewById(R.id.record_back);
        saveLayout = (LinearLayout) findViewById(R.id.record_save);
        initView();

        mSpeechUnderstander = new SpeechHelper(RecordActivity.this).getmSpeechUnderstander();
        initData();
        initVolley();
    }

    private void initVolley() {
        VolleyTool vollTool = new VolleyTool(this) {
            @Override
            public void getRequest(int msgWhat, SoapResult result) throws JSONException, Exception {

            }

            @Override
            public void onNetUnConnected() {

            }

            @Override
            public void onErrorRequest(SoapResult result) throws Exception {

            }
        };
        //获取轮播图地址
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("hosId", "");
        dataMap.put("updateTime", "");
//        dataMap.put("updateTime", "111111111");
        String list = CommUtil.changeJson(dataMap);

//        String requestMethod = "GetNursingDocumentsList";
//        String serverUrl = "http://192.168.1.117:8732/ws/NRService";
////        volleyTool.sendJsonRequest(1004, false, new Gson().toJson(CommUtil.getRequestParam("S006", null)), Request
////                .Method.POST, requestMethod, IPConfig.CONFIG_SERVER_IP);
//        vollTool.sendJsonRequest(1004, false, null, Request
//                .Method.GET, requestMethod, serverUrl);

        //文书基本信息接口
//        String requestMethod = "GetNursingDocumentMetaDataList";
//        String serverUrl = "http://192.168.1.117:8732/ws/NRService";
////        volleyTool.sendJsonRequest(1004, false, new Gson().toJson(CommUtil.getRequestParam("S006", null)), Request
////                .Method.POST, requestMethod, IPConfig.CONFIG_SERVER_IP);
//        vollTool.sendJsonRequest(1005, false, null, Request
//                .Method.GET, requestMethod, serverUrl);


        String requestMethod = "GetNursingDocumentCodeItemList";
        String serverUrl = "http://192.168.1.117:8732/ws/NRService";
//        volleyTool.sendJsonRequest(1004, false, new Gson().toJson(CommUtil.getRequestParam("S006", null)), Request
//                .Method.POST, requestMethod, IPConfig.CONFIG_SERVER_IP);
        vollTool.sendJsonRequest(1006, false, null, Request
                .Method.GET, requestMethod, serverUrl);
    }

    /**
     * 初始化view
     */
    private void initView(){
        timeText.setText(getDate());
        // 建立数据源
        String[] mItems = {"入院评估","体温单"};
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner.setAdapter(adapter);
        glWaveFormView.init();
        Display display = getWindowManager().getDefaultDisplay(); //Activity#getWindowManager()
        Point size = new Point(); display.getSize(size);
        int width = size.x;
//        int height = size.y;
//        Log.d("SCREAN_X_Y",width+"------"+height);
        float circleRadius = width*80.0f/1080;
        glWaveFormView.setCircleRadius(circleRadius);

        glWaveFormView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //播放
                speech();
            }
        });
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRecordInfo();
            }
        });
    }

    /**
     * 保存护理记录数据
     */
    private void saveRecordInfo() {


    }


    /**
     * 初始化数据
     */
    private void initData(){
        PatientInfo patientInfo = new PatientInfo("0480392","04803921","221","0233154","1","1000654","5","张三","1989-09-21","男","11","00023","内科一病区","5646","中级","住院","2014-03-22","2014-06-11","123123","内科");
        PatientInfo patientInfo2 = new PatientInfo("0480393","04803922","222","0233155","1","1000654","5","李四","1989-09-21","男","12","00023","内科一病区","5646","中级","住院","2014-03-22","2014-06-11","123123","内科");
        PatientInfo patientInfo3 = new PatientInfo("0480394","04803923","223","0233156","1","1000654","5","王石泉","1989-09-21","男","13","00023","内科一病区","5646","中级","住院","2014-03-22","2014-06-11","123123","内科");
        PatientInfo patientInfo4 = new PatientInfo("0480395","04803924","224","0233157","1","1000654","5","孙文静","1989-09-21","女","14","00023","内科一病区","5646","中级","住院","2014-03-22","2014-06-11","123123","内科");
        PatientInfo patientInfo5 = new PatientInfo("0480396","04803925","225","0233158","1","1000654","5","刘伟","1989-09-21","男","15","00023","内科一病区","5646","中级","住院","2014-03-22","2014-06-11","123123","内科");
        PatientInfo patientInfo6 = new PatientInfo("0480397","04803926","226","0233159","1","1000654","5","陈俊","1989-09-21","男","16","00023","内科一病区","5646","中级","住院","2014-03-22","2014-06-11","123123","内科");
        List<PatientInfo> patientInfos =new ArrayList<>();
        patientInfos.add(patientInfo);
        patientInfos.add(patientInfo2);
        patientInfos.add(patientInfo3);
        patientInfos.add(patientInfo4);
        patientInfos.add(patientInfo5);
        patientInfos.add(patientInfo6);
        patientInfoDao = new PatientInfoDao(RecordActivity.this);
        patientInfoDao.deletePatientInfo();
        patientInfoDao.saveOrUpdatePaintInfoList(patientInfos);
        String patientStr = new Gson().toJson(patientInfos);
        Log.d("PATIENT",patientStr);

        DocumentDic documentDic = new DocumentDic("1001","入院评估单","");
        DocumentDic documentDic2 = new DocumentDic("1002","体温单","");
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
        businessDataInfo.setSex(patientInfo.getPatSex());
        businessDataInfo.setBedNo(patientInfo.getHosBedNum());
        businessDataInfo.setAge(patientInfo.getPatBirth());
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

        businessDataInfoList = new ArrayList<>();
        businessDataInfoList.add(businessDataInfo);
        recordAdapter = new RecordAdapter(RecordActivity.this,businessDataInfoList);
        listView.setAdapter(recordAdapter);
        position = businessDataInfoList.size()-1;
    }


    private void speech() {
        if (mSpeechUnderstander.isUnderstanding()) {// 开始前检查状态
            mSpeechUnderstander.stopUnderstanding();
//            Toast.makeText(RecordActivity.this,"停止录音",Toast.LENGTH_SHORT).show();
            glWaveFormView.stopListening();
        } else {
            ret = mSpeechUnderstander.startUnderstanding(mSpeechUnderstanderListener);
            if (ret != 0) {
                Toast.makeText(this, "语义理解失败", Toast.LENGTH_SHORT).show();
                glWaveFormView.reset();
            } else {
//                Toast.makeText(this, "请说话", Toast.LENGTH_SHORT).show();
            }
        }

    }


    /**
     * 语义理解回调。
     */
    private SpeechUnderstanderListener mSpeechUnderstanderListener = new SpeechUnderstanderListener() {

        @Override
        public void onResult(final UnderstanderResult result) {
            glWaveFormView.reset();
            BusinessDataInfo businessDataInfo;
            List<WSData> wsDataList = new ArrayList<>();
            JSONObject jsonObject;
            String service;
            String key = "";
            String value = "";
            String name = "";
            String bed = "";
            String type = "";
            try {
                jsonObject = new JSONObject(result.getResultString());
                service = jsonObject.optString("service");
                JSONObject semanticObject = jsonObject.optJSONObject("semantic");
                if (semanticObject != null){
                    JSONObject slotsObject = semanticObject.optJSONObject("slots");
                    Iterator<String> iterator = slotsObject.keys();
                    bed = slotsObject.optString("bed");
                    type = slotsObject.optString("type");
                    //遍历结果
                    while (iterator.hasNext()){
                        key = iterator.next();
                        value = slotsObject.optString(key);
                        if (StringUtils.isEquals(key,"type")||StringUtils.isEquals(key,"bed")){
                            break;
                        }
                        WSData wsData = new WSData();
                        wsData.setWsName(key);
                        wsData.setWsValue(value);
                        wsDataList.add(wsData);
                    }
                }
                //护理业务
                if (StringUtils.isEquals(service,"nursing")){
                    if (StringUtils.isNotBlank(type)){
                        //切换种类


                    }
                    if (StringUtils.isNotBlank(bed)){
                        BusinessDataInfo busInfo = null;
                        int i = 0;
                        for (BusinessDataInfo info:businessDataInfoList){
                            if (StringUtils.isEquals(info.getBedNo(),bed)){
                                busInfo = info;
                                i = businessDataInfoList.indexOf(info);
                                break;
                            }
                        }
                        if (busInfo == null){
                            busInfo = new BusinessDataInfo();
                            busInfo.setBedNo(bed);
                            PatientInfoDao patientInfoDao = new PatientInfoDao(RecordActivity.this);
                            PatientInfo patientInfo = patientInfoDao.getPatientInfo(bed);
                            if (patientInfo !=null){
                                busInfo.setPatName(patientInfo.getPatName());
                                busInfo.setAge(patientInfo.getPatBirth());
                                busInfo.setSex(patientInfo.getPatSex());
                            }
                            busInfo.setWsDataList(new ArrayList<WSData>());
                            businessDataInfoList.add(busInfo);
                            position = businessDataInfoList.size()-1;
                        }else {
                            position = i;
                            listView.setSelection(position);
                        }
                    }

                    if (wsDataList != null){
                        businessDataInfo = businessDataInfoList.get(position);
                        businessDataInfo.getWsDataList().addAll(wsDataList);
                        businessDataInfoList.set(position,businessDataInfo);
                    }
                }else {
                    showTip("暂不支持您的说法");
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            recordAdapter = new RecordAdapter(RecordActivity.this,businessDataInfoList);
            listView.setAdapter(recordAdapter);
            listView.setSelection(position);
            IFlyNursing.getInstance().getNursingListener().onDataSavedListener(result.getResultString());
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            //showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, data.length + "");
            glWaveFormView.setVolume(volume);
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//            showTip("结束说话");
            glWaveFormView.stopListening();
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//            showTip("开始说话");
            glWaveFormView.start();
        }

        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(false));
            glWaveFormView.reset();

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
        Toast.makeText(RecordActivity.this,text,Toast.LENGTH_SHORT).show();
    }

    /**
     * @return 得到基础时间  yyyy-MM-dd HH:mm:ss
     */
    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                DEAFULTFORMAT);
        return simpleDateFormat.format(new Date());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1002){
            String bedNo = data.getStringExtra("PATIENT_BEDNO");
            int count = data.getIntExtra("POSITION",0);
            int i = -1;
            for (BusinessDataInfo info:businessDataInfoList){
                if (StringUtils.isEquals(info.getBedNo(),bedNo)){
                    i = businessDataInfoList.indexOf(info);
                    break;
                }
            }
            if (i == -1){
                PatientInfo patientInfo = patientInfoDao.getPatientInfo(bedNo);
                BusinessDataInfo businessDataInfo = new BusinessDataInfo();
                businessDataInfo.setSyxh(patientInfo.getHosID());
                businessDataInfo.setSex(patientInfo.getPatSex());
                businessDataInfo.setBedNo(patientInfo.getHosBedNum());
                businessDataInfo.setAge(patientInfo.getPatBirth());
                businessDataInfo.setYexh("");
                businessDataInfo.setPatName(patientInfo.getPatName());
                businessDataInfo.setNmrCode("");
                businessDataInfo.setDate("2016-09-10");
                businessDataInfo.setRecorderDate("2016-10-21");
                businessDataInfo.setWsDataList(new ArrayList<WSData>());
                businessDataInfoList.set(count,businessDataInfo);
                recordAdapter = new RecordAdapter(this,businessDataInfoList);
                listView.setAdapter(recordAdapter);
                listView.setSelection(count);
                position = count;
            }else {
                listView.setSelection(i);
                position = i;
                showTip("该患者已录入，可直接录入信息");
            }


        }
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
