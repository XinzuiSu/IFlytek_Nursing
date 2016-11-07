package com.iflytek.medicalsdk_nursing.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.android.framework.volley.Request;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.medicalsdk_nursing.util.GLWaveformView;
import com.iflytek.medicalsdk_nursing.R;
import com.iflytek.medicalsdk_nursing.util.SpeechHelper;
import com.iflytek.medicalsdk_nursing.adapter.RecordAdapter;
import com.iflytek.medicalsdk_nursing.base.IFlyNursing;
import com.iflytek.medicalsdk_nursing.dao.DocumentDetailDicDao;
import com.iflytek.medicalsdk_nursing.dao.DocumentDicDao;
import com.iflytek.medicalsdk_nursing.dao.MappingDao;
import com.iflytek.medicalsdk_nursing.dao.OptionDicDao;
import com.iflytek.medicalsdk_nursing.dao.PatientInfoDao;
import com.iflytek.medicalsdk_nursing.domain.BusinessDataInfo;
import com.iflytek.medicalsdk_nursing.domain.DocumentDetailDic;
import com.iflytek.medicalsdk_nursing.domain.DocumentDic;
import com.iflytek.medicalsdk_nursing.domain.MappingInfo;
import com.iflytek.medicalsdk_nursing.domain.OptionDic;
import com.iflytek.medicalsdk_nursing.domain.PatientInfo;
import com.iflytek.medicalsdk_nursing.domain.WSData;
import com.iflytek.medicalsdk_nursing.net.SoapResult;
import com.iflytek.medicalsdk_nursing.net.VolleyTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.iflytek.medicalsdk_nursing.R.id.record_spinner;

/**
 * @Title: com.iflytek.medicalsdk_nursing
 * @Copyright: IFlytek Co., Ltd. Copyright 16/10/8-上午10:56,  All rights reserved
 * @Description: TODO 录音页面;
 * @author: chenzhilei
 * @data: 16/10/8 上午10:56
 * @version: V1.0
 */
public class RecordActivity extends Activity {

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
    private int position = 0;

    private PatientInfoDao patientInfoDao;
    /**
     * 返回
     */
    private LinearLayout backLayout;

    private LinearLayout saveLayout;
    /**
     * volleytool
     */
    private VolleyTool volleyTool;

    private List<String> typeList;

    private DocumentDetailDicDao documentDetailDicDao;

    private OptionDicDao optionDicDao;

    private MappingDao mappingDao;

    private DocumentDic documentDic;

    private DocumentDicDao documentDicDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        listView = (ListView) findViewById(R.id.record_listView);
        spinner = (Spinner) findViewById(record_spinner);
        glWaveFormView = (GLWaveformView) findViewById(R.id.record_voice_image);
        timeText = (TextView) findViewById(R.id.recored_time_text);
        backLayout = (LinearLayout) findViewById(R.id.record_back);
        saveLayout = (LinearLayout) findViewById(R.id.record_save);

        mappingDao = new MappingDao(this);
        documentDic = new DocumentDic();
        documentDicDao = new DocumentDicDao(this);
        documentDetailDicDao = new DocumentDetailDicDao(this);
        optionDicDao = new OptionDicDao(this);
        initView();

        mSpeechUnderstander = new SpeechHelper(RecordActivity.this).getmSpeechUnderstander();
        initData();
        initVolley();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG",i+"");
            }
        });
    }

    private void initVolley() {
        volleyTool = new VolleyTool(this) {
            @Override
            public void getRequest(int msgWhat, SoapResult result) throws JSONException, Exception {

                switch (msgWhat) {
                    case 1004:
                        result.setData("[{\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Doc_Name\":\"体温单\",\"Interface_Name\":\"我是体温单的接口名称\"},{\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Doc_Name\":\"入院评估单\",\"Interface_Name\":\"我是入院评估单的接口名称\"}]");
                        saveDocumentDic(result.getData());
                        break;
                    case 1005:
                        result.setData("[{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Temperature.Value\",\"Item_Name\":\"体温\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Pulse\",\"Item_Name\":\"脉搏\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Heart.HeartRate\",\"Item_Name\":\"心率\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Breathe\",\"Item_Name\":\"呼吸\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"BloodPressure.BP\",\"Item_Name\":\"血压\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"PainScores.TotalScores\",\"Item_Name\":\"疼痛评分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"ConsciousnessScore\",\"Item_Name\":\"意识评分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"MewsScore\",\"Item_Name\":\"预警总得分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.Weight\",\"Item_Name\":\"体重\",\"Item_Type\":1},{\"Code_Id\":\"sSbsm\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.TopMaker\",\"Item_Name\":\"上标说明\",\"Item_Type\":6},{\"Code_Id\":\"sXbsm\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.BottomMaker\",\"Item_Name\":\"下标说明\",\"Item_Type\":6},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.BowelMovements\",\"Item_Name\":\"大便次数\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.OutUrine\",\"Item_Name\":\"小便量\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.TotalOutput\",\"Item_Name\":\"出量\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.IntakeValue\",\"Item_Name\":\"入量\",\"Item_Type\":1},{\"Code_Id\":\"YWGM2\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.AllergyInfo\",\"Item_Name\":\"药物过敏\",\"Item_Type\":6},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Temperature.PhysicalValue\",\"Item_Name\":\"物理降温\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.IsAssistedBreathing\",\"Item_Name\":\"人工呼吸\",\"Item_Type\":5},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Heart.IsPacemaker\",\"Item_Name\":\"起搏心率\",\"Item_Type\":5},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.SurgeryDays\",\"Item_Name\":\"术后天数\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemValue1\",\"Item_Name\":\"备注1\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemValue2\",\"Item_Name\":\"备注2\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemValue3\",\"Item_Name\":\"备注3\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemValue4\",\"Item_Name\":\"备注4\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemCaption1\",\"Item_Name\":\"备注1标题\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemCaption2\",\"Item_Name\":\"备注2标题\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemCaption3\",\"Item_Name\":\"备注3标题\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemCaption4\",\"Item_Name\":\"备注4标题\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Temperature.IsCheckedTemperature\",\"Item_Name\":\"核实温度\",\"Item_Type\":5},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Temperature.IsCheckedTemperature\",\"Item_Name\":\"核实温度\",\"Item_Type\":5},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Temperature.Way\",\"Item_Name\":\"肛温\",\"Item_Type\":5},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.BowelBeforEnema\",\"Item_Name\":\"输入量\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.BowelAfterEnema\",\"Item_Name\":\"痰量\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.NoWeightReason\",\"Item_Name\":\"体重\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.CatheterizationMarker\",\"Item_Name\":\"口腔白点\",\"Item_Type\":5},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.HeadCircumference\",\"Item_Name\":\"头围\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.BodyLength\",\"Item_Name\":\"月经\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.AbdominalCircumference\",\"Item_Name\":\"T管引流\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemValue7\",\"Item_Name\":\"备用10\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemCaption7\",\"Item_Name\":\"备注9\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"JarredScores\",\"Item_Name\":\"降低疼痛评分\",\"Item_Type\":2},{\"Code_Id\":\"A005\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"ADL评分\",\"Item_Type\":2},{\"Code_Id\":\"A009\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"表情\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"BradenScores.TotalScores\",\"Item_Name\":\"Braden评分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PipeSlippageRisk.TotalScores\",\"Item_Name\":\"管道滑脱评分\",\"Item_Type\":2},{\"Code_Id\":\"A002\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"GMS.Content\",\"Item_Name\":\"过敏史\",\"Item_Type\":1},{\"Code_Id\":\"A016\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"沟通方式\",\"Item_Type\":1},{\"Code_Id\":\"A017\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"理解能力\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Breathe\",\"Item_Name\":\"呼吸\",\"Item_Type\":3},{\"Code_Id\":\"A010\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"婚姻状况\",\"Item_Type\":1},{\"Code_Id\":\"A012\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"家庭社会情况\",\"Item_Type\":1},{\"Code_Id\":\"A014\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"文化程度\",\"Item_Type\":1},{\"Code_Id\":\"A013\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"职业\",\"Item_Type\":1},{\"Code_Id\":\"A011\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"既往史\",\"Item_Type\":1},{\"Code_Id\":\"A018\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"口腔黏膜\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"联系地址\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"联系人及电话\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Pulse\",\"Item_Name\":\"脉搏\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"MorseScores.TotalScores\",\"Item_Name\":\"Morse评分\",\"Item_Type\":2},{\"Code_Id\":\"A021\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"皮肤\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"评估时间\",\"Item_Type\":4},{\"Code_Id\":\"A027\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"排便\",\"Item_Type\":1},{\"Code_Id\":\"A026\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"排尿\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"护士签名\",\"Item_Type\":1},{\"Code_Id\":\"A001\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"入院方式\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"入院时间\",\"Item_Type\":4},{\"Code_Id\":\"\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"入院诊断\",\"Item_Type\":1},{\"Code_Id\":\"A024\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"吸烟\",\"Item_Type\":1},{\"Code_Id\":\"A025\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"饮酒\",\"Item_Type\":1},{\"Code_Id\":\"A004\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"视力\",\"Item_Type\":1},{\"Code_Id\":\"A023\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"睡眠\",\"Item_Type\":1},{\"Code_Id\":\"A003\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"SZ.Content\",\"Item_Name\":\"神志\",\"Item_Type\":1},{\"Code_Id\":\"A028\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"四肢活动\",\"Item_Type\":1},{\"Code_Id\":\"A015\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"听力\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PainScores.TotalScores\",\"Item_Name\":\"疼痛评分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Temperature.Value\",\"Item_Name\":\"体温\",\"Item_Type\":3},{\"Code_Id\":\"A071\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"TemperatureNmrs.Weight\",\"Item_Name\":\"体重\",\"Item_Type\":3},{\"Code_Id\":\"A072\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"体重未测原因\",\"Item_Type\":1},{\"Code_Id\":\"A030\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"家庭支持\",\"Item_Type\":1},{\"Code_Id\":\"A029\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"情绪\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"BloodPressure.BP\",\"Item_Name\":\"血压\",\"Item_Type\":1},{\"Code_Id\":\"A020\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"义齿\",\"Item_Type\":1},{\"Code_Id\":\"A022\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"饮食\",\"Item_Type\":1},{\"Code_Id\":\"A020\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"药物辅助睡眠\",\"Item_Type\":1},{\"Code_Id\":\"A031\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"\",\"Item_Name\":\"住院告知\",\"Item_Type\":1}]");
                        saveDocumentDetailDic(result.getData());
                        break;
                    case 1006:
                        result.setData("[{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"1\",\"Opt_Name\":\"入院\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"2\",\"Opt_Name\":\"出院\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"3\",\"Opt_Name\":\"转入\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"4\",\"Opt_Name\":\"手术\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"5\",\"Opt_Name\":\"分娩\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"6\",\"Opt_Name\":\"死亡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sXbsm\",\"Opt_Code\":\"1\",\"Opt_Name\":\"外出\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sXbsm\",\"Opt_Code\":\"2\",\"Opt_Name\":\"拒测\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sXbsm\",\"Opt_Code\":\"3\",\"Opt_Name\":\"请假\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"1\",\"Opt_Name\":\"入院\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"2\",\"Opt_Name\":\"出院\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"3\",\"Opt_Name\":\"转入\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"4\",\"Opt_Name\":\"手术\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"5\",\"Opt_Name\":\"分娩\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"6\",\"Opt_Name\":\"死亡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sXbsm\",\"Opt_Code\":\"1\",\"Opt_Name\":\"外出\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sXbsm\",\"Opt_Code\":\"2\",\"Opt_Name\":\"拒测\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sXbsm\",\"Opt_Code\":\"3\",\"Opt_Name\":\"请假\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"YWGM2\",\"Opt_Code\":\"1\",\"Opt_Name\":\"青霉素\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"YWGM2\",\"Opt_Code\":\"2\",\"Opt_Name\":\"阿莫西林\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"YWGM2\",\"Opt_Code\":\"3\",\"Opt_Name\":\"破伤风抗毒素\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"YWGM2\",\"Opt_Code\":\"4\",\"Opt_Name\":\"普鲁卡因\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"YWGM2\",\"Opt_Code\":\"5\",\"Opt_Name\":\"碘\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"YWGM2\",\"Opt_Code\":\"6\",\"Opt_Name\":\"酒精\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"YWGM2\",\"Opt_Code\":\"7\",\"Opt_Name\":\"头孢替胺\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sYwgm\",\"Opt_Code\":\"1\",\"Opt_Name\":\"青霉素\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sYwgm\",\"Opt_Code\":\"2\",\"Opt_Name\":\"阿莫西林\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sYwgm\",\"Opt_Code\":\"3\",\"Opt_Name\":\"破伤风抗毒素\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sYwgm\",\"Opt_Code\":\"4\",\"Opt_Name\":\"普鲁卡因\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sYwgm\",\"Opt_Code\":\"5\",\"Opt_Name\":\"碘\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sYwgm\",\"Opt_Code\":\"6\",\"Opt_Name\":\"酒精\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sYwgm\",\"Opt_Code\":\"7\",\"Opt_Name\":\"头孢替胺\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A001\",\"Opt_Code\":\"A001_0001\",\"Opt_Name\":\"步行\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A001\",\"Opt_Code\":\"A001_0002\",\"Opt_Name\":\"轮椅\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A001\",\"Opt_Code\":\"A001_0003\",\"Opt_Name\":\"平车\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A001\",\"Opt_Code\":\"A001_0004\",\"Opt_Name\":\"其它\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0003\",\"Opt_Name\":\"药物\",\"Parent_Opt\":\"A002_0002\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0004\",\"Opt_Name\":\"食物\",\"Parent_Opt\":\"A002_0002\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0005\",\"Opt_Name\":\"其它\",\"Parent_Opt\":\"A002_0002\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0001\",\"Opt_Name\":\"清楚\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0002\",\"Opt_Name\":\"嗜睡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0003\",\"Opt_Name\":\"神志模糊\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0004\",\"Opt_Name\":\"昏睡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0005\",\"Opt_Name\":\"浅昏迷\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0006\",\"Opt_Name\":\"深昏迷\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A004\",\"Opt_Code\":\"A004_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A004\",\"Opt_Code\":\"A004_0002\",\"Opt_Name\":\"失明\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A004\",\"Opt_Code\":\"A004_0003\",\"Opt_Name\":\"其它\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A005\",\"Opt_Code\":\"A005_0001\",\"Opt_Name\":\"自理(100分)\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A005\",\"Opt_Code\":\"A005_0002\",\"Opt_Name\":\"轻度依赖(60-90分)\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A005\",\"Opt_Code\":\"A005_0003\",\"Opt_Name\":\"中度依赖(41-59分)\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A005\",\"Opt_Code\":\"A005_0004\",\"Opt_Name\":\"重度依赖(0-40分)\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A009\",\"Opt_Code\":\"A009_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A009\",\"Opt_Code\":\"A009_0002\",\"Opt_Name\":\"淡漠\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A009\",\"Opt_Code\":\"A009_0003\",\"Opt_Name\":\"痛苦\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A010\",\"Opt_Code\":\"A010_0001\",\"Opt_Name\":\"未婚\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A010\",\"Opt_Code\":\"A010_0002\",\"Opt_Name\":\"已婚\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0001\",\"Opt_Name\":\"高血压\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0002\",\"Opt_Name\":\"心脏病\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0003\",\"Opt_Name\":\"糖尿病\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0004\",\"Opt_Name\":\"脑血管病\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0005\",\"Opt_Name\":\"手术史\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0006\",\"Opt_Name\":\"精神病\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0007\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A012\",\"Opt_Code\":\"A012_0001\",\"Opt_Name\":\"汉族\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A012\",\"Opt_Code\":\"A012_0002\",\"Opt_Name\":\"回族\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A012\",\"Opt_Code\":\"A012_0003\",\"Opt_Name\":\"满族\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0001\",\"Opt_Name\":\"工人\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0002\",\"Opt_Name\":\"干部\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0003\",\"Opt_Name\":\"农民\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0004\",\"Opt_Name\":\"职员\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0005\",\"Opt_Name\":\"学生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0006\",\"Opt_Name\":\"军人\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0007\",\"Opt_Name\":\"教师\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0008\",\"Opt_Name\":\"医务人员\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0009\",\"Opt_Name\":\"个休\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0010\",\"Opt_Name\":\"演员\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0011\",\"Opt_Name\":\"退休\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0012\",\"Opt_Name\":\"自由职业\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0001\",\"Opt_Name\":\"文盲\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0002\",\"Opt_Name\":\"小学\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0003\",\"Opt_Name\":\"中学\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0004\",\"Opt_Name\":\"高中\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0005\",\"Opt_Name\":\"大专\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0006\",\"Opt_Name\":\"大学本科\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0007\",\"Opt_Name\":\"硕士研究生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0008\",\"Opt_Name\":\"博士研究生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A015\",\"Opt_Code\":\"A015_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A015\",\"Opt_Code\":\"A015_0002\",\"Opt_Name\":\"重听\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A015\",\"Opt_Code\":\"A015_0003\",\"Opt_Name\":\"失聪\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A015\",\"Opt_Code\":\"A015_0004\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A016\",\"Opt_Code\":\"A016_0001\",\"Opt_Name\":\"语言\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A016\",\"Opt_Code\":\"A016_0002\",\"Opt_Name\":\"文字\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A016\",\"Opt_Code\":\"A016_0003\",\"Opt_Name\":\"手势\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A017\",\"Opt_Code\":\"A017_0001\",\"Opt_Name\":\"良好\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A017\",\"Opt_Code\":\"A017_0002\",\"Opt_Name\":\"一般\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A017\",\"Opt_Code\":\"A017_0003\",\"Opt_Name\":\"差\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A018\",\"Opt_Code\":\"A018_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A018\",\"Opt_Code\":\"A018_0002\",\"Opt_Name\":\"充血\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A018\",\"Opt_Code\":\"A018_0003\",\"Opt_Name\":\"破损\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A018\",\"Opt_Code\":\"A018_0004\",\"Opt_Name\":\"溃疡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A018\",\"Opt_Code\":\"A018_0005\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A020\",\"Opt_Code\":\"A020_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A020\",\"Opt_Code\":\"A020_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A020\",\"Opt_Code\":\"A020_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A020\",\"Opt_Code\":\"A020_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A021\",\"Opt_Code\":\"A021_0001\",\"Opt_Name\":\"完整\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A021\",\"Opt_Code\":\"A021_0002\",\"Opt_Name\":\"破损\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A021\",\"Opt_Code\":\"A021_0003\",\"Opt_Name\":\"压疮\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A021\",\"Opt_Code\":\"A021_0004\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A021\",\"Opt_Code\":\"A021_0005\",\"Opt_Name\":\"部位\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A021\",\"Opt_Code\":\"A021_0006\",\"Opt_Name\":\"范围\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0002\",\"Opt_Name\":\"咸\",\"Parent_Opt\":\"A022_0001\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0003\",\"Opt_Name\":\"甜\",\"Parent_Opt\":\"A022_0001\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0004\",\"Opt_Name\":\"辛辣\",\"Parent_Opt\":\"A022_0001\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0005\",\"Opt_Name\":\"油腻\",\"Parent_Opt\":\"A022_0001\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0006\",\"Opt_Name\":\"清淡\",\"Parent_Opt\":\"A022_0001\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0007\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"A022_0001\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0008\",\"Opt_Name\":\"忌食\",\"Parent_Opt\":\"A022_0001\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0009\",\"Opt_Name\":\"异常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0010\",\"Opt_Name\":\"食欲不振\",\"Parent_Opt\":\"A022_0009\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0011\",\"Opt_Name\":\"吞咽困难\",\"Parent_Opt\":\"A022_0009\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0012\",\"Opt_Name\":\"咀嚼困难\",\"Parent_Opt\":\"A022_0009\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0013\",\"Opt_Name\":\"恶心\",\"Parent_Opt\":\"A022_0009\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0014\",\"Opt_Name\":\"呕吐\",\"Parent_Opt\":\"A022_0009\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0015\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"A022_0009\"},{\"Code_Id\":\"A023\",\"Opt_Code\":\"A023_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A023\",\"Opt_Code\":\"A023_0002\",\"Opt_Name\":\"入睡困难\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A023\",\"Opt_Code\":\"A023_0003\",\"Opt_Name\":\"多梦\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A023\",\"Opt_Code\":\"A023_0004\",\"Opt_Name\":\"易醒\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A023\",\"Opt_Code\":\"A023_0005\",\"Opt_Name\":\"，每日睡眠{0}小时\",\"Parent_Opt\":\"A023_0004\"},{\"Code_Id\":\"A024\",\"Opt_Code\":\"A024_0001\",\"Opt_Name\":\"否\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A024\",\"Opt_Code\":\"A024_0002\",\"Opt_Name\":\"是\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A024\",\"Opt_Code\":\"A024_0003\",\"Opt_Name\":\"{0}支\\/天\",\"Parent_Opt\":\"A024_0002\"},{\"Code_Id\":\"A025\",\"Opt_Code\":\"A025_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A025\",\"Opt_Code\":\"A025_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A025\",\"Opt_Code\":\"A025_0003\",\"Opt_Name\":\"{0}两\\/天\",\"Parent_Opt\":\"A025_0002\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0002\",\"Opt_Name\":\"失禁\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0003\",\"Opt_Name\":\"尿频\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0004\",\"Opt_Name\":\"尿急\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0005\",\"Opt_Name\":\"尿痛\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0006\",\"Opt_Name\":\"尿潴留\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0007\",\"Opt_Name\":\"留置尿管\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0008\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0002\",\"Opt_Name\":\"失禁\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0003\",\"Opt_Name\":\"便秘\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0004\",\"Opt_Name\":\"黑便\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0005\",\"Opt_Name\":\"造口\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0006\",\"Opt_Name\":\"腹泻\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0007\",\"Opt_Name\":\"{0}次\\/日\",\"Parent_Opt\":\"A027_0006\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0009\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0001\",\"Opt_Name\":\"自如\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0002\",\"Opt_Name\":\"无力\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0003\",\"Opt_Name\":\"偏瘫\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0004\",\"Opt_Name\":\"截瘫\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0005\",\"Opt_Name\":\"全瘫\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0006\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0002\",\"Opt_Name\":\"兴奋\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0003\",\"Opt_Name\":\"焦虑\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0004\",\"Opt_Name\":\"恐惧\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0005\",\"Opt_Name\":\"易激动\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0006\",\"Opt_Name\":\"孤独\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0007\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A030\",\"Opt_Code\":\"A030_0001\",\"Opt_Name\":\"关心\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A030\",\"Opt_Code\":\"A030_0002\",\"Opt_Name\":\"不关心\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A030\",\"Opt_Code\":\"A030_0003\",\"Opt_Name\":\"过于关心\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A030\",\"Opt_Code\":\"A030_0004\",\"Opt_Name\":\"无人照顾\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0001\",\"Opt_Name\":\"床位医生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0002\",\"Opt_Name\":\"责任护士\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0003\",\"Opt_Name\":\"病房环境\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0004\",\"Opt_Name\":\"住院制度\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0005\",\"Opt_Name\":\"探视饮食制度\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0006\",\"Opt_Name\":\"安全告知\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0007\",\"Opt_Name\":\"饮食（喂养）指导\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0008\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A072\",\"Opt_Code\":\"A072_0001\",\"Opt_Name\":\"卧床\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A072\",\"Opt_Code\":\"A072_0002\",\"Opt_Name\":\"平车\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A072\",\"Opt_Code\":\"A072_0003\",\"Opt_Name\":\"轮椅\",\"Parent_Opt\":\"\"}]");
                        saveOptionDic(result.getData());
                        break;
                }

            }

            @Override
            public void onNetUnConnected() {

            }

            @Override
            public void onErrorRequest(SoapResult result) throws Exception {

            }
        };
        getDocumentDicFromWeb();
        getDocumentDetailDicFromWeb();
        getOptionDicFromWeb();
    }

    /**
     * 获取文书基本信息
     */
    private void getDocumentDicFromWeb() {
        String requestMethod = "";
        String serverUrl = "http://www.baidu.com/";
//        String requestMethod = "GetNursingDocumentsList";
//        String serverUrl = "http://192.168.1.109:8732/ws/NRService";
        volleyTool.sendJsonRequest(1004, false, null, Request.Method.GET, requestMethod, serverUrl);
    }

    /**
     * 获取文书详细信息
     */
    private void getDocumentDetailDicFromWeb() {
        String requestMethod = "";
        String serverUrl = "http://www.baidu.com/";
//        String requestMethod = "GetNursingDocumentMetaDataList";
//        String serverUrl = "http://192.168.1.109:8732/ws/NRService";
        volleyTool.sendJsonRequest(1005, false, null, Request.Method.GET, requestMethod, serverUrl);
    }

    /**
     * 获取选项信息
     */
    private void getOptionDicFromWeb() {
        String requestMethod = "";
        String serverUrl = "http://www.baidu.com/";
//        String requestMethod = "GetNursingDocumentCodeItemList";
//        String serverUrl = "http://192.168.1.109:8732/ws/NRService";
        volleyTool.sendJsonRequest(1006, false, null, Request.Method.GET, requestMethod, serverUrl);
    }

    /**
     * 保存文书基本信息
     *
     * @param str
     */
    private void saveDocumentDic(String str) {
        List<DocumentDic> documentDicList = new Gson().fromJson(str, new TypeToken<List<DocumentDic>>() {
        }.getType());
        documentDicDao.deleteDocumentDic();
        documentDicDao.saveOrUpdateDocumentDicList(documentDicList);

    }

    /**
     * 保存文书详细信息
     *
     * @param str
     */
    private void saveDocumentDetailDic(String str) {
        List<DocumentDetailDic> documentDetailDicList = new Gson().fromJson(str, new TypeToken<List<DocumentDetailDic>>() {
        }.getType());
        documentDetailDicDao.deleteItemDic();
        documentDetailDicDao.saveOrUpdateDocumentDetailDicList(documentDetailDicList);
    }

    /**
     * 保存选项信息
     *
     * @param str
     */
    private void saveOptionDic(String str) {
        List<OptionDic> optionDicList = new Gson().fromJson(str, new TypeToken<List<OptionDic>>() {
        }.getType());
        optionDicDao.deleteOptionDic();
        optionDicDao.saveOrUpdateOptionDicList(optionDicList);
    }

    /**
     * 删除
     * @param groupID
     * @param itemID
     */
    public void delete(int groupID,int itemID){
        position = groupID;
        listView.setSelection(groupID);
        businessDataInfoList.get(groupID).getWsDataList().remove(itemID);
        recordAdapter.updateList(businessDataInfoList);
    }

    /**
     * 初始化view
     */
    private void initView() {
        timeText.setText(getDate());
        // 建立数据源
        String[] mItems = getResources().getStringArray(R.array.type);
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner.setAdapter(adapter);
        documentDic = documentDicDao.getDocumentDic(mItems[0]);

        glWaveFormView.init();
        Display display = getWindowManager().getDefaultDisplay(); //Activity#getWindowManager()
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
//        int height = size.y;
//        Log.d("SCREAN_X_Y",width+"------"+height);
        float circleRadius = width * 80.0f / 1080;
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
        String dateStr = timeText.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEAFULTFORMAT);
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            date = new Date();
        }
        int hours = date.getHours();
        int minutes = date.getMinutes();
        if (date.getMinutes()>0){
            hours++;
        }
        if (StringUtils.isEquals(typeList.get(spinner.getSelectedItemPosition()),"体温单")){
             String[] times = IFlyNursing.getInstance().getTimes();
             for (String string:times){
                if (Math.abs(hours - Integer.parseInt(string))<2){
                    hours = Integer.parseInt(string);
                    break;
                }
             }
        }
        DocumentDic documentDic = documentDicDao.getDocumentDic(typeList.get(spinner.getSelectedItemPosition()));
        date.setHours(hours);
        date.setMinutes(0);
        date.setSeconds(0);
        for (BusinessDataInfo businessDataInfo:businessDataInfoList){
            businessDataInfo.setDate(dateFormat.format(date));
            businessDataInfo.setNmrCode(documentDic.getNmrID());
        }
        String saveData = new Gson().toJson(businessDataInfoList);
        Log.d("保存", saveData);
        IFlyNursing.getInstance().getNursingListener().onDataSavedListener(saveData);
        finish();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        typeList = Arrays.asList(getResources().getStringArray(R.array.type));
        String patientStr = "[{\"age\":\"3\",\"cwdm\":\"1\",\"hzxm\":\"真空箱\",\"patid\":\"8184\",\"sex\":\"男  \",\"syxh\":\"8918\",\"yexh\":\"0\"},{\"age\":\"28\",\"cwdm\":\"2\",\"hzxm\":\"家床A\",\"patid\":\"5053\",\"sex\":\"女  \",\"syxh\":\"5583\",\"yexh\":\"0\"},{\"age\":\"28\",\"cwdm\":\"3\",\"hzxm\":\"家床5\",\"patid\":\"5041\",\"sex\":\"女  \",\"syxh\":\"5572\",\"yexh\":\"0\"},{\"age\":\"26\",\"cwdm\":\"4\",\"hzxm\":\"娃娃\",\"patid\":\"6059\",\"sex\":\"女  \",\"syxh\":\"6586\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"0104\",\"hzxm\":\"家床病人\",\"patid\":\"5067\",\"sex\":\"女  \",\"syxh\":\"5597\",\"yexh\":\"0\"},{\"age\":\"23\",\"cwdm\":\"0105\",\"hzxm\":\"出区-3\",\"patid\":\"6663\",\"sex\":\"女  \",\"syxh\":\"7189\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"5\",\"hzxm\":\"李五\",\"patid\":\"5436\",\"sex\":\"女  \",\"syxh\":\"5952\",\"yexh\":\"0\"},{\"age\":\"60\",\"cwdm\":\"6\",\"hzxm\":\"JC3\",\"patid\":\"5045\",\"sex\":\"男  \",\"syxh\":\"5575\",\"yexh\":\"0\"},{\"age\":\"28\",\"cwdm\":\"7\",\"hzxm\":\"JC2\",\"patid\":\"5044\",\"sex\":\"女  \",\"syxh\":\"5574\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"8\",\"hzxm\":\"李三\",\"patid\":\"5418\",\"sex\":\"女  \",\"syxh\":\"5935\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"0110\",\"hzxm\":\"李四\",\"patid\":\"5420\",\"sex\":\"男  \",\"syxh\":\"5937\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"9\",\"hzxm\":\"李六\",\"patid\":\"5437\",\"sex\":\"女  \",\"syxh\":\"5953\",\"yexh\":\"0\"},{\"age\":\"26\",\"cwdm\":\"10\",\"hzxm\":\"打1\",\"patid\":\"5515\",\"sex\":\"女  \",\"syxh\":\"6028\",\"yexh\":\"0\"},{\"age\":\"64\",\"cwdm\":\"11\",\"hzxm\":\"报告3\",\"patid\":\"5562\",\"sex\":\"女  \",\"syxh\":\"6075\",\"yexh\":\"0\"},{\"age\":\"36\",\"cwdm\":\"12\",\"hzxm\":\"234\",\"patid\":\"8197\",\"sex\":\"未知\",\"syxh\":\"8932\",\"yexh\":\"0\"},{\"age\":\"33\",\"cwdm\":\"13\",\"hzxm\":\"篮球队\",\"patid\":\"8186\",\"sex\":\"男  \",\"syxh\":\"8920\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"14\",\"hzxm\":\"宋2\",\"patid\":\"5587\",\"sex\":\"女  \",\"syxh\":\"6100\",\"yexh\":\"0\"},{\"age\":\"23\",\"cwdm\":\"15\",\"hzxm\":\"丁伟1\",\"patid\":\"8196\",\"sex\":\"    \",\"syxh\":\"8931\",\"yexh\":\"0\"},{\"age\":\"30\",\"cwdm\":\"16\",\"hzxm\":\"绣花针\",\"patid\":\"8191\",\"sex\":\"男  \",\"syxh\":\"8925\",\"yexh\":\"0\"},{\"age\":\"33\",\"cwdm\":\"17\",\"hzxm\":\"zc01\",\"patid\":\"5648\",\"sex\":\"女  \",\"syxh\":\"6160\",\"yexh\":\"0\"},{\"age\":\"23\",\"cwdm\":\"18\",\"hzxm\":\"出区-5\",\"patid\":\"6664\",\"sex\":\"女  \",\"syxh\":\"7190\",\"yexh\":\"0\"},{\"age\":\"26\",\"cwdm\":\"19\",\"hzxm\":\"出院-2\",\"patid\":\"6659\",\"sex\":\"女  \",\"syxh\":\"7185\",\"yexh\":\"0\"},{\"age\":\"37\",\"cwdm\":\"20\",\"hzxm\":\"测测4\",\"patid\":\"6656\",\"sex\":\"女  \",\"syxh\":\"7182\",\"yexh\":\"0\"},{\"age\":\"32\",\"cwdm\":\"21\",\"hzxm\":\"维护1\",\"patid\":\"8278\",\"sex\":\"女  \",\"syxh\":\"9005\",\"yexh\":\"0\"},{\"age\":\"47\",\"cwdm\":\"22\",\"hzxm\":\"总经销\",\"patid\":\"8274\",\"sex\":\"女  \",\"syxh\":\"9001\",\"yexh\":\"0\"},{\"age\":\"32\",\"cwdm\":\"23\",\"hzxm\":\"加床\",\"patid\":\"8942\",\"sex\":\"男  \",\"syxh\":\"9753\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"24\",\"hzxm\":\"测测5\",\"patid\":\"6657\",\"sex\":\"女  \",\"syxh\":\"7183\",\"yexh\":\"0\"},{\"age\":\"23\",\"cwdm\":\"25\",\"hzxm\":\"出院-上\",\"patid\":\"6658\",\"sex\":\"女  \",\"syxh\":\"7184\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"26\",\"hzxm\":\"测测5\",\"patid\":\"6657\",\"sex\":\"女  \",\"syxh\":\"7183\",\"yexh\":\"0\"},{\"age\":\"37\",\"cwdm\":\"27\",\"hzxm\":\"测测4\",\"patid\":\"6656\",\"sex\":\"女  \",\"syxh\":\"7182\",\"yexh\":\"0\"},{\"age\":\"23\",\"cwdm\":\"28\",\"hzxm\":\"出区-3\",\"patid\":\"6663\",\"sex\":\"女  \",\"syxh\":\"7189\",\"yexh\":\"0\"}]";
        List<PatientInfo> patientInfos = new Gson().fromJson(patientStr,new TypeToken<List<PatientInfo>>(){}.getType());
        patientInfoDao = new PatientInfoDao(RecordActivity.this);
        patientInfoDao.deletePatientInfo();
        patientInfoDao.saveOrUpdatePaintInfoList(patientInfos);
        Log.d("PATIENT", patientStr);
        businessDataInfoList = new ArrayList<>();
//        businessDataInfoList.add(businessDataInfo);
        recordAdapter = new RecordAdapter(RecordActivity.this, businessDataInfoList);
        listView.setAdapter(recordAdapter);
        if (businessDataInfoList.size() >0){
            position = businessDataInfoList.size() - 1;
        }

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
            BusinessDataInfo businessDataInfo = new BusinessDataInfo();
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
                if (semanticObject != null && semanticObject.has("slots")) {
                    JSONObject slotsObject = semanticObject.optJSONObject("slots");
                    Iterator<String> iterator = slotsObject.keys();
                    bed = slotsObject.optString("bed");
                    type = slotsObject.optString("type");
                    //遍历结果
                    while (iterator.hasNext()) {
                        key = iterator.next();
                        value = slotsObject.optString(key);
                        if (value.contains("date")){
                            value = slotsObject.optJSONObject(key).optString("date");
                        }
                        if (StringUtils.isEquals(key, "type") || StringUtils.isEquals(key, "bed")) {
                            break;
                        }
                        //组装数据
                        WSData wsData = new WSData();
                        //设置项目值
                        MappingInfo mappingInfo = mappingDao.getMappingDic(key);
                        wsData.setWsName(mappingInfo.getValue());
                        DocumentDetailDic documentDetailDic = documentDetailDicDao.getDocumentDetailDic(mappingInfo.getValue());
                        if (documentDetailDic != null) {
                            wsData.setWsID(documentDetailDic.getItemID());
                            if (StringUtils.isNotBlank(documentDetailDic.getCodeID())) {
                                //判断选项是值还是选择项
                                OptionDic optionDic = optionDicDao.getOptionDic(value);
                                if (optionDic != null) {
                                    wsData.setWsValue(optionDic.getOptCode());
                                    wsData.setWsValueCaption(optionDic.getOptName());
                                } else {
                                    wsData.setWsValueCaption(value);
                                }

                            } else {
                                wsData.setWsValue(value);
                            }
                        }
//                        wsData.setWsValue(value);
                        wsDataList.add(wsData);
                    }
                }
                //护理业务
                if (StringUtils.isEquals(service, "nursing")) {
                    if (result.getResultString().contains("体温单")) {
                        type = "体温单";
                    }

                    if (StringUtils.isNotBlank(type)) {
                        //切换种类
                        spinner.setSelection(typeList.indexOf(type));
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
                            PatientInfoDao patientInfoDao = new PatientInfoDao(RecordActivity.this);
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
                            position = businessDataInfoList.size() - 1;
                        } else {
                            position = i;
                            listView.setSelection(position);
                        }
                    }
                    if (wsDataList != null && wsDataList.size() > 0) {
                        //如果当前的数据列表为空则默认一床用户
                        if (businessDataInfoList.size() == 0){
                            businessDataInfo = new BusinessDataInfo();
                            businessDataInfo.setWsDataList(new ArrayList<WSData>());
                        }else {
                            businessDataInfo = businessDataInfoList.get(position);
                        }
                        businessDataInfo.getWsDataList().addAll(wsDataList);
                        if (businessDataInfoList.size() == 0){
                           businessDataInfoList.add(businessDataInfo);
                        }else {
                            businessDataInfoList.set(position, businessDataInfo);
                        }
                    }
                } else {
                    showTip("暂不支持您的说法");
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            recordAdapter = new RecordAdapter(RecordActivity.this, businessDataInfoList);
            listView.setAdapter(recordAdapter);
            listView.setSelection(position);
//            IFlyNursing.getInstance().getNursingListener().onDataSavedListener(result.getResultString());
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


    private void showTip(String text) {
        Toast.makeText(RecordActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * @return 得到基础时间  yyyy-MM-dd HH:mm:ss
     */
    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEAFULTFORMAT);
        return simpleDateFormat.format(new Date());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1002) {
            String bedNo = data.getStringExtra("PATIENT_BEDNO");
            int count = data.getIntExtra("POSITION", 0);
            int i = -1;
            for (BusinessDataInfo info : businessDataInfoList) {
                if (StringUtils.isEquals(info.getBedNo(), bedNo)) {
                    i = businessDataInfoList.indexOf(info);
                    break;
                }
            }
            if (i == -1) {
                PatientInfo patientInfo = patientInfoDao.getPatientInfo(bedNo);
                BusinessDataInfo businessDataInfo = new BusinessDataInfo();
                businessDataInfo.setSyxh(patientInfo.getSyxh());
                businessDataInfo.setSex(patientInfo.getSex());
                businessDataInfo.setBedNo(patientInfo.getCwdm());
                businessDataInfo.setAge(patientInfo.getAge());
                businessDataInfo.setYexh(patientInfo.getYexh());
                businessDataInfo.setPatName(patientInfo.getHzxm());
                businessDataInfo.setWsDataList(businessDataInfoList.get(count).getWsDataList());
                businessDataInfoList.set(count, businessDataInfo);
                recordAdapter = new RecordAdapter(this, businessDataInfoList);
                listView.setAdapter(recordAdapter);
                listView.setSelection(count);
                position = count;
            } else {
                listView.setSelection(i);
                position = i;
                showTip("该患者已录入，可直接录入信息");
            }


        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
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
