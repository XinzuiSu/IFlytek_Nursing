package com.iflytek.medicalsdk_nursing.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.android.framework.toast.BaseToast;
import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.android.framework.volley.Request;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.medicalsdk_nursing.R;
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
import com.iflytek.medicalsdk_nursing.util.DataDealUtil;
import com.iflytek.medicalsdk_nursing.util.GLWaveformView;
import com.iflytek.medicalsdk_nursing.util.IatSpeechHelper;
import com.iflytek.medicalsdk_nursing.util.JsonParser;
import com.iflytek.medicalsdk_nursing.util.MediaplayerUtil;
import com.iflytek.medicalsdk_nursing.util.MiniWaveSurface;
import com.iflytek.medicalsdk_nursing.util.NursingSpeecher;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//import winning.NurseTouch.model.IFLYPaint;


/**
 * @Title: com.iflytek.medicalsdk_nursing
 * @Copyright: IFlytek Co., Ltd. Copyright 16/10/8-上午10:56,  All rights reserved
 * @Description: TODO 持续录音页面;
 * @author: chenzhilei
 * @data: 16/10/8 上午10:56
 * @version: V1.0
 */
public class StandingRecordActivity extends Activity {

    private static String TAG = "Speech";

    /**
     * 时间基础格式化
     */
    public static final String DEAFULTFORMAT = "yyyy-MM-dd HH:mm:ss";

    private Spinner spinner;

    private Spinner tempSpinner;

    private LinearLayout tempLayout;

    // 函数调用返回值
    private int ret = 0;

    // 语音听写对象
    private SpeechRecognizer mIat;

    // 语义理解对象（文本到语义）。
    private TextUnderstander mTextUnderstander;

    private ListView listView;

    private List<BusinessDataInfo> businessDataInfoList;

    private RecordAdapter recordAdapter;

    private GLWaveformView glWaveFormView;

    private TextView timeText;
    /**
     * 当前编辑患者标记
     */
    private int patPosition = 0;

    private PatientInfoDao patientInfoDao;
    /**
     * 返回
     */
    private LinearLayout backLayout;
    //保存按钮
    private LinearLayout saveLayout;
    //网络请求类
    private VolleyTool volleyTool;

    private List<String> typeList;
    //项目字典dao
    private DocumentDetailDicDao documentDetailDicDao;

    private OptionDicDao optionDicDao;

    private DocumentDicDao documentDicDao;

    private IatSpeechHelper speechHelper;

    private String filePath;
    /**
     * 录音文件列表
     */
    private ArrayList<String> voicePathList;
    /**
     * 语音播放工具类
     */
    private MediaplayerUtil mediaplayerUtil;
    /**
     * 时间格式化
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DEAFULTFORMAT);
    //语音相关保存业务操作
//   	private IFLYAction iflyAction;
   	//NIS    IP地址
//   	private String NISUrl = "http://192.168.1.253:8732";

    private String NISUrl = "http://192.168.200.62:8732";
    /**
     * 播放按钮
     */
    private LinearLayout voiceLayout;

    /**
     * 音频管理器
     */
    private AudioManager mAudioManager;

    /**
     * 是否长按语音按钮
     */
    private boolean isLongClick = false;
    /**
     * 顶部语音时长
     */
    private int topVoiceTime = 0;

    /**
     * 顶部语音时长
     */
    private TextView topVoiceTimeTextView;

    private RelativeLayout title;

    /**
     * 顶部语音效果布局
     */
    private LinearLayout topVoice;

    private MiniWaveSurface miniWaveSurface;

    private String selectedType;

    @Override
    protected void onResume() {
        super.onResume();
        businessDataInfoList = IFlyNursing.getInstance().getBusinessDataInfoList();
        if (businessDataInfoList == null){
            businessDataInfoList = new ArrayList<>();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        listView = (ListView) findViewById(R.id.record_listView);
        spinner = (Spinner) findViewById(R.id.record_spinner);
        tempSpinner = (Spinner) findViewById(R.id.record_temp_spinner);
        glWaveFormView = (GLWaveformView) findViewById(R.id.record_voice_image);
        timeText = (TextView) findViewById(R.id.recored_time_text);
        backLayout = (LinearLayout) findViewById(R.id.record_back);
        saveLayout = (LinearLayout) findViewById(R.id.record_save);
        tempLayout = (LinearLayout) findViewById(R.id.record_temptype_layout);
        voiceLayout = (LinearLayout) findViewById(R.id.record_voice);
        topVoiceTimeTextView = (TextView) findViewById(R.id.tv_top_voice_time);
        title = (RelativeLayout) findViewById(R.id.record_title_layout);
        topVoice = (LinearLayout) findViewById(R.id.ll_top_voice);
        miniWaveSurface = (MiniWaveSurface) findViewById(R.id.mws_speech);

        documentDicDao = new DocumentDicDao(this);
        documentDetailDicDao = new DocumentDetailDicDao(this);
        optionDicDao = new OptionDicDao(this);
        mediaplayerUtil = new MediaplayerUtil();

//        iflyAction = new IFLYAction(StandingRecordActivity.this);
        //启动语音
        NursingSpeecher nursingSpeecher = new NursingSpeecher(this);

        initView();

        voicePathList = new ArrayList<String>();
        initData();
        initVolley();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG", i + "");
            }
        });
        voiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (voicePathList!=null&&voicePathList.size()>0){
                    playVoice();
                }else {
                    BaseToast.showToastNotRepeat(StandingRecordActivity.this,"请先录入语音",2000);
                }

            }
        });
        initSpeech();
//        startRecording();

    }

    /**
     * PDA测试使用
     */
    private void startRecording() {

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        //获得文件保存路径。记得添加android.permission.WRITE_EXTERNAL_STORAGE权限
//        mRecorder = new MediaRecorder();
//        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mRecorder.setOutputFile(mFileName);
//        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        try {
//            mRecorder.prepare();//如果文件打开失败，此步将会出错。
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }

        if (!mAudioManager.isBluetoothScoAvailableOffCall()) {
            Log.d("BluetoothTest", "系统不支持蓝牙录音&quot");
            return;
        }
//蓝牙录音的关键，启动SCO连接，耳机话筒才起作用
        mAudioManager.startBluetoothSco();
        //蓝牙SCO连接建立需要时间，连接建立后会发出ACTION_SCO_AUDIO_STATE_CHANGED消息，通过接收该消息而进入后续逻辑。
        //也有可能此时SCO已经建立，则不会收到上述消息，可以startBluetoothSco()前先stopBluetoothSco()
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                    mAudioManager.setBluetoothScoOn(true);  //打开SCO
//                    mRecorder.start();//开始录音
//					BaseToast.showToastNotRepeat(CreateOrEditCaseActivity.this,"蓝牙连接已建立", SysCode.TOAST);
                    unregisterReceiver(this);  //别遗漏
                } else {//等待一秒后再尝试启动SCO
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mAudioManager.startBluetoothSco();
                }
            }
        }, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
    }

    private void initSpeech(){
        mTextUnderstander = TextUnderstander.createTextUnderstander(StandingRecordActivity.this, mTextUdrInitListener);
        speechHelper = new IatSpeechHelper(StandingRecordActivity.this);
        mIat = speechHelper.getmIat();
    }


    /**
     * 初始化监听器（文本到语义）。
     */
    private InitListener mTextUdrInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "textUnderstanderListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码："+code);
            }
        }
    };

    private void initVolley() {
        volleyTool = new VolleyTool(this) {
            @Override
            public void getRequest(int msgWhat, SoapResult result) throws JSONException, Exception {

                switch (msgWhat) {
                    case 1004:
                        result.setData("[{\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Doc_Name\":\"体温单\",\"Interface_Name\":\"我是体温单的接口名称\"},{\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Doc_Name\":\"新通用入院评估单\",\"Interface_Name\":\"我是新通用入院评估单的接口名称\"},{\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Doc_Name\":\"儿科入院评估单\",\"Interface_Name\":\"我是儿科入院评估单的接口名称\"},{\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Doc_Name\":\"产科入院评估单\",\"Interface_Name\":\"我是产科入院评估单的接口名称\"},{\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Doc_Name\":\"新生儿入院评估单\",\"Interface_Name\":\"我是新生儿入院评估单的接口名称\"}]");
                        saveDocumentDic(result.getData());
                        break;
                    case 1005:
                        result.setData("[{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Temperature.Value\",\"Item_Name\":\"体温\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Pulse\",\"Item_Name\":\"脉搏\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Heart.HeartRate\",\"Item_Name\":\"心率\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Breathe\",\"Item_Name\":\"呼吸\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"BloodPressure.BP\",\"Item_Name\":\"血压\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"PainScores.TotalScores\",\"Item_Name\":\"疼痛评分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"ConsciousnessScore\",\"Item_Name\":\"意识水平\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"MewsScore\",\"Item_Name\":\"预警总得分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.Weight\",\"Item_Name\":\"体重\",\"Item_Type\":1},{\"Code_Id\":\"sSbsm\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.TopMaker\",\"Item_Name\":\"上标说明\",\"Item_Type\":6},{\"Code_Id\":\"sXbsm\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.BottomMaker\",\"Item_Name\":\"下标说明\",\"Item_Type\":6},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.BowelMovements\",\"Item_Name\":\"大便次数\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.OutUrine\",\"Item_Name\":\"尿量\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.TotalOutput\",\"Item_Name\":\"出量\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.IntakeValue\",\"Item_Name\":\"入量\",\"Item_Type\":1},{\"Code_Id\":\"YWGM2\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.AllergyInfo\",\"Item_Name\":\"药物过敏2\",\"Item_Type\":6},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Temperature.PhysicalValue\",\"Item_Name\":\"物理降温\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.IsAssistedBreathing\",\"Item_Name\":\"人工呼吸\",\"Item_Type\":5},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Heart.IsPacemaker\",\"Item_Name\":\"起搏心率\",\"Item_Type\":5},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.SurgeryDays\",\"Item_Name\":\"术后天数\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemValue1\",\"Item_Name\":\"备注1\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemValue2\",\"Item_Name\":\"备注2\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemValue3\",\"Item_Name\":\"备注3\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemValue4\",\"Item_Name\":\"备注4\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemCaption1\",\"Item_Name\":\"备注1标题\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemCaption2\",\"Item_Name\":\"备注2标题\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemCaption3\",\"Item_Name\":\"备注3标题\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemCaption4\",\"Item_Name\":\"备注4标题\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Temperature.IsCheckedTemperature\",\"Item_Name\":\"核实温度\",\"Item_Type\":5},{\"Code_Id\":\"nGw\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"Temperature.Way\",\"Item_Name\":\"肛温\",\"Item_Type\":5},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.BowelBeforEnema\",\"Item_Name\":\"输入量\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.BowelAfterEnema\",\"Item_Name\":\"痰量\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.NoWeightReason\",\"Item_Name\":\"体重\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.CatheterizationMarker\",\"Item_Name\":\"口腔白点\",\"Item_Type\":5},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.HeadCircumference\",\"Item_Name\":\"头围\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.BodyLength\",\"Item_Name\":\"月经\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.AbdominalCircumference\",\"Item_Name\":\"T管引流\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemValue7\",\"Item_Name\":\"备用10\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"TemperatureNmrs.DynamicItemCaption7\",\"Item_Name\":\"备注9\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\",\"Item_Id\":\"JarredScores\",\"Item_Name\":\"降低疼痛评分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"BradenScale.TotalScores\",\"Item_Name\":\"Braden评分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PipeSlippageRisk.TotalScores\",\"Item_Name\":\"管道滑脱评分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Breathe\",\"Item_Name\":\"呼吸\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PatientInfo.ContactAddress\",\"Item_Name\":\"联系地址\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PatientInfo.ContactPhone\",\"Item_Name\":\"联系人及电话\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Pulse\",\"Item_Name\":\"脉搏\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"FallRisk.TotalScores\",\"Item_Name\":\"Morse评分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Doc.EvaluationTime\",\"Item_Name\":\"评估时间\",\"Item_Type\":4},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"InHospital.Date\",\"Item_Name\":\"入院时间\",\"Item_Type\":4},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"InHospital.Diagnosis\",\"Item_Name\":\"入院诊断\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Doc.NurseSignature\",\"Item_Name\":\"护士签名\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PainScores.TotalScores\",\"Item_Name\":\"疼痛评分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Temperature.Value\",\"Item_Name\":\"体温\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"BloodPressure.BP\",\"Item_Name\":\"血压\",\"Item_Type\":1},{\"Code_Id\":\"A001\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"InHospital.Way\",\"Item_Name\":\"入院方式\",\"Item_Type\":1},{\"Code_Id\":\"A002\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PatientInfo.AllergicHistory\",\"Item_Name\":\"过敏史\",\"Item_Type\":1},{\"Code_Id\":\"A003\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PatientInfo.Obnubilation\",\"Item_Name\":\"神志\",\"Item_Type\":1},{\"Code_Id\":\"A004\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"GeneralInfo.Vision\",\"Item_Name\":\"视力\",\"Item_Type\":1},{\"Code_Id\":\"A005\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"SelfCareAbility.TotalScores\",\"Item_Name\":\"ADL评分\",\"Item_Type\":2},{\"Code_Id\":\"A009\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"GeneralInfo.Expression\",\"Item_Name\":\"表情\",\"Item_Type\":1},{\"Code_Id\":\"A010\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PatientInfo.MaritalStatus\",\"Item_Name\":\"婚姻状况\",\"Item_Type\":1},{\"Code_Id\":\"A011\",\"Control_Type\":\"1\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PatientInfo.PastHistory\",\"Item_Name\":\"既往史\",\"Item_Type\":1},{\"Code_Id\":\"A012\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PatientInfo.Nation\",\"Item_Name\":\"民族\",\"Item_Type\":1},{\"Code_Id\":\"A013\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PatientInfo.Occupation\",\"Item_Name\":\"职业\",\"Item_Type\":1},{\"Code_Id\":\"A014\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PatientInfo.Education\",\"Item_Name\":\"文化程度\",\"Item_Type\":1},{\"Code_Id\":\"A015\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"GeneralInfo.Hearing\",\"Item_Name\":\"听力\",\"Item_Type\":1},{\"Code_Id\":\"A016\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Communication.Mode\",\"Item_Name\":\"沟通方式\",\"Item_Type\":1},{\"Code_Id\":\"A017\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Communication.Understanding\",\"Item_Name\":\"理解能力\",\"Item_Type\":1},{\"Code_Id\":\"A018\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"OralCavity.Mucosa\",\"Item_Name\":\"口腔黏膜\",\"Item_Type\":1},{\"Code_Id\":\"A020\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"OralCavity.Denture\",\"Item_Name\":\"义齿\",\"Item_Type\":1},{\"Code_Id\":\"A020\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Sleep.DrugAssisted\",\"Item_Name\":\"药物辅助睡眠\",\"Item_Type\":1},{\"Code_Id\":\"A021\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"GeneralInfo.Skin\",\"Item_Name\":\"皮肤\",\"Item_Type\":1},{\"Code_Id\":\"A022\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"GeneralInfo.Diet\",\"Item_Name\":\"饮食\",\"Item_Type\":1},{\"Code_Id\":\"A023\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Sleep.Quality\",\"Item_Name\":\"睡眠\",\"Item_Type\":1},{\"Code_Id\":\"A024\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Hobby.Smoke\",\"Item_Name\":\"吸烟\",\"Item_Type\":1},{\"Code_Id\":\"A025\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Hobby.Drink\",\"Item_Name\":\"饮酒\",\"Item_Type\":1},{\"Code_Id\":\"A026\",\"Control_Type\":\"1\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Excretion.Micturition\",\"Item_Name\":\"排尿\",\"Item_Type\":1},{\"Code_Id\":\"A027\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"Excretion.Defecation\",\"Item_Name\":\"排便\",\"Item_Type\":1},{\"Code_Id\":\"A028\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"GeneralInfo.LimbsActivity\",\"Item_Name\":\"四肢活动\",\"Item_Type\":1},{\"Code_Id\":\"A029\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PsychologicalAssessment.Emotion\",\"Item_Name\":\"情绪\",\"Item_Type\":1},{\"Code_Id\":\"A030\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"PsychologicalAssessment.FamilySupport\",\"Item_Name\":\"家庭支持\",\"Item_Type\":1},{\"Code_Id\":\"A031\",\"Control_Type\":\"1\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"InHospital.Inform\",\"Item_Name\":\"住院告知\",\"Item_Type\":1},{\"Code_Id\":\"A071\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"TemperatureNmrs.Weight\",\"Item_Name\":\"体重\",\"Item_Type\":3},{\"Code_Id\":\"A072\",\"Control_Type\":\"0\",\"Doc_Id\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\",\"Item_Id\":\"TemperatureNmrs.NoWeightReason\",\"Item_Name\":\"体重未测原因\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"BradenScores.TotalScores\",\"Item_Name\":\"Braden评分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"PatientInfo.HistoryStatement\",\"Item_Name\":\"病史陈述者（与患者关系）\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"PipeSlippageRisk.TotalScores\",\"Item_Name\":\"管道滑脱评分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"Breathe\",\"Item_Name\":\"呼吸\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"PatientInfo.PastHistory\",\"Item_Name\":\"既往史\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"PatientInfo.ContactAddress\",\"Item_Name\":\"联系地址\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"PatientInfo.ContactPhone\",\"Item_Name\":\"联系人及电话\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"Pulse\",\"Item_Name\":\"脉搏\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"MorseScores.TotalScores\",\"Item_Name\":\"Morse评分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"Doc.EvaluationTime\",\"Item_Name\":\"评估时间\",\"Item_Type\":4},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"Doc.NurseSignature\",\"Item_Name\":\"护士签名\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"InHospital.Date\",\"Item_Name\":\"入院时间\",\"Item_Type\":4},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"InHospital.Diagnosis\",\"Item_Name\":\"入院诊断\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"PainScores.TotalScores\",\"Item_Name\":\"疼痛评分\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"Temperature.Value\",\"Item_Name\":\"体温\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"TemperatureNmrs.Weight\",\"Item_Name\":\"体重\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"BloodPressure.BP\",\"Item_Name\":\"血压\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"SelfCareAbility.TotalScores\",\"Item_Name\":\"ADL评分\",\"Item_Type\":2},{\"Code_Id\":\"A002\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"PatientInfo.AllergicHistory\",\"Item_Name\":\"过敏史\",\"Item_Type\":1},{\"Code_Id\":\"A003\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"PatientInfo.Obnubilation\",\"Item_Name\":\"神志\",\"Item_Type\":1},{\"Code_Id\":\"A009\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"GeneralInfo.Expression\",\"Item_Name\":\"表情\",\"Item_Type\":1},{\"Code_Id\":\"A012\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"PatientInfo.Nation\",\"Item_Name\":\"民族\",\"Item_Type\":1},{\"Code_Id\":\"A014\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"PatientInfo.Education\",\"Item_Name\":\"文化程度\",\"Item_Type\":1},{\"Code_Id\":\"A028\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"GeneralInfo.LimbsActivity\",\"Item_Name\":\"四肢活动\",\"Item_Type\":1},{\"Code_Id\":\"A031\",\"Control_Type\":\"1\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"InHospital.Inform\",\"Item_Name\":\"住院告知\",\"Item_Type\":1},{\"Code_Id\":\"A043\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"Communication.LanguageCompetence\",\"Item_Name\":\"语言能力\",\"Item_Type\":1},{\"Code_Id\":\"A044\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"BabyInformation.Fontanel\",\"Item_Name\":\"囟门\",\"Item_Type\":1},{\"Code_Id\":\"A045\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"OralCavity.Lips\",\"Item_Name\":\"口唇\",\"Item_Type\":1},{\"Code_Id\":\"A046\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"PatientInfo.EmotionalState\",\"Item_Name\":\"情绪状态\",\"Item_Type\":1},{\"Code_Id\":\"A063\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"OralCavity.Mucosa\",\"Item_Name\":\"口腔黏膜\",\"Item_Type\":1},{\"Code_Id\":\"A064\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"GeneralInfo.Skin\",\"Item_Name\":\"皮肤\",\"Item_Type\":1},{\"Code_Id\":\"A065\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"Excretion.Defecation\",\"Item_Name\":\"排便\",\"Item_Type\":1},{\"Code_Id\":\"A066\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"GeneralInfo.Diet\",\"Item_Name\":\"饮食\",\"Item_Type\":1},{\"Code_Id\":\"A067\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"Sleep.Quality\",\"Item_Name\":\"睡眠\",\"Item_Type\":1},{\"Code_Id\":\"A068\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"PatientInfo.FamilyAttitude\",\"Item_Name\":\"家属态度\",\"Item_Type\":1},{\"Code_Id\":\"A069\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"InHospital.Way\",\"Item_Name\":\"入院方式\",\"Item_Type\":1},{\"Code_Id\":\"A070\",\"Control_Type\":\"0\",\"Doc_Id\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\",\"Item_Id\":\"Excretion.Micturition\",\"Item_Name\":\"排尿\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"Breathe\",\"Item_Name\":\"呼吸\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PatientInfo.ContactAddress\",\"Item_Name\":\"联系地址\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PatientInfo.ContactPhone\",\"Item_Name\":\"联系人及电话\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"Pulse\",\"Item_Name\":\"脉搏\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PregnantInfo.LastMenstrualPeriod\",\"Item_Name\":\"末次月经\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"Doc.EvaluationTime\",\"Item_Name\":\"评估时间\",\"Item_Type\":4},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PregnantInfo.EmbryoAbnormality\",\"Item_Name\":\"胚胎异常描述\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"Doc.NurseSignature\",\"Item_Name\":\"护士签名\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"InHospital.Date\",\"Item_Name\":\"入院时间\",\"Item_Type\":4},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"InHospital.Diagnosis\",\"Item_Name\":\"入院诊断\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"BabyInformation.PositionOfTheFetus\",\"Item_Name\":\"胎位\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"Temperature.Value\",\"Item_Name\":\"体温\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PatientInfo.FetalHeart\",\"Item_Name\":\"胎心\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"BloodPressure.BP\",\"Item_Name\":\"血压\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PatientInfo.ExpectedDate\",\"Item_Name\":\"预产期\",\"Item_Type\":1},{\"Code_Id\":\"A001\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"InHospital.Way\",\"Item_Name\":\"入院方式\",\"Item_Type\":1},{\"Code_Id\":\"A002\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PatientInfo.AllergicHistory\",\"Item_Name\":\"过敏史\",\"Item_Type\":1},{\"Code_Id\":\"A003\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PatientInfo.Obnubilation\",\"Item_Name\":\"神志\",\"Item_Type\":1},{\"Code_Id\":\"A007\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"GeneralInfo.Edema\",\"Item_Name\":\"水肿\",\"Item_Type\":1},{\"Code_Id\":\"A008\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"GeneralInfo.Protein\",\"Item_Name\":\"蛋白质\",\"Item_Type\":1},{\"Code_Id\":\"A008\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"GeneralInfo.GlucoseInUrine\",\"Item_Name\":\"尿糖\",\"Item_Type\":1},{\"Code_Id\":\"A009\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"GeneralInfo.Expression\",\"Item_Name\":\"表情\",\"Item_Type\":1},{\"Code_Id\":\"A010\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PatientInfo.MaritalStatus\",\"Item_Name\":\"婚姻状况\",\"Item_Type\":1},{\"Code_Id\":\"A011\",\"Control_Type\":\"1\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PatientInfo.PastHistory\",\"Item_Name\":\"既往史\",\"Item_Type\":1},{\"Code_Id\":\"A012\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PatientInfo.Nation\",\"Item_Name\":\"民族\",\"Item_Type\":1},{\"Code_Id\":\"A013\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PatientInfo.Occupation\",\"Item_Name\":\"职业\",\"Item_Type\":1},{\"Code_Id\":\"A014\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PatientInfo.Education\",\"Item_Name\":\"文化程度\",\"Item_Type\":1},{\"Code_Id\":\"A020\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"RivanolAmnioticCavity.UC\",\"Item_Name\":\"宫缩\",\"Item_Type\":1},{\"Code_Id\":\"A020\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PregnantInfo.VaginalBleeding\",\"Item_Name\":\"阴道出血\",\"Item_Type\":1},{\"Code_Id\":\"A029\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PsychologicalAssessment.Emotion\",\"Item_Name\":\"情绪\",\"Item_Type\":1},{\"Code_Id\":\"A030\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PsychologicalAssessment.FamilySupport\",\"Item_Name\":\"家庭支持\",\"Item_Type\":1},{\"Code_Id\":\"A031\",\"Control_Type\":\"1\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"InHospital.Inform\",\"Item_Name\":\"住院告知\",\"Item_Type\":1},{\"Code_Id\":\"A032\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PatientInfo.PregnancyHistory\",\"Item_Name\":\"孕产史\",\"Item_Type\":1},{\"Code_Id\":\"A033\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PregnantInfo.BleedingPregnancy\",\"Item_Name\":\"孕期出血\",\"Item_Type\":1},{\"Code_Id\":\"A034\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PregnantInfo.MedicationPregnancy\",\"Item_Name\":\"孕期用药\",\"Item_Type\":1},{\"Code_Id\":\"A035\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PregnantInfo.BreastFeeding\",\"Item_Name\":\"母乳喂养知识\",\"Item_Type\":1},{\"Code_Id\":\"A036\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"BabyInformation.TheFetalMovement\",\"Item_Name\":\"胎动\",\"Item_Type\":1},{\"Code_Id\":\"A037\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"BabyInformation.Embryolemma\",\"Item_Name\":\"胎膜\",\"Item_Type\":1},{\"Code_Id\":\"A038\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PregnantInfo.UterineContraction\",\"Item_Name\":\"宫缩\",\"Item_Type\":1},{\"Code_Id\":\"A039\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"PregnantInfo.BreastDevelopment\",\"Item_Name\":\"乳房发育\",\"Item_Type\":1},{\"Code_Id\":\"A071\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"TemperatureNmrs.Weight\",\"Item_Name\":\"体重\",\"Item_Type\":3},{\"Code_Id\":\"A072\",\"Control_Type\":\"0\",\"Doc_Id\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\",\"Item_Id\":\"TemperatureNmrs.NoWeightReason\",\"Item_Name\":\"体重未测原因\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"PatientInfo.HistoryStatement\",\"Item_Name\":\"病史陈述者（与患者关系）\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.Breathing\",\"Item_Name\":\"呼吸\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.ContactNumber\",\"Item_Name\":\"联系电话\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"Pulse\",\"Item_Name\":\"脉搏\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.ParentName\",\"Item_Name\":\"家长姓名\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"Doc.EvaluationTime\",\"Item_Name\":\"评估时间\",\"Item_Type\":4},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"Doc.NurseSignature\",\"Item_Name\":\"护士签名\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"Doc.NurseSignatureOne\",\"Item_Name\":\"护士签名1\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"Doc.NurseSignatureTwo\",\"Item_Name\":\"护士签名2\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"InHospital.Date\",\"Item_Name\":\"入院时间\",\"Item_Type\":4},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"InHospital.Diagnosis\",\"Item_Name\":\"入院诊断\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.BodyHeight\",\"Item_Name\":\"身长\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"GeneralInfo.HeadCircumference\",\"Item_Name\":\"头围\",\"Item_Type\":2},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"Temperature.Value\",\"Item_Name\":\"体温\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"TemperatureNmrs.Weight\",\"Item_Name\":\"体重\",\"Item_Type\":3},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BloodPressure.BP\",\"Item_Name\":\"血压\",\"Item_Type\":1},{\"Code_Id\":\"\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"GeneralInfo.JuniorCollege\",\"Item_Name\":\"专科情况\",\"Item_Type\":1},{\"Code_Id\":\"A047\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"PregnantInfo.ModeOfProduction\",\"Item_Name\":\"生产方式\",\"Item_Type\":1},{\"Code_Id\":\"A048\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"PregnantInfo.FeedingPatterns\",\"Item_Name\":\"喂养方式\",\"Item_Type\":1},{\"Code_Id\":\"A049\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"GeneralInfo.Consciousness\",\"Item_Name\":\"意识\",\"Item_Type\":1},{\"Code_Id\":\"A050\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.Cry\",\"Item_Name\":\"哭声\",\"Item_Type\":1},{\"Code_Id\":\"A051\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.PhysicalActivity\",\"Item_Name\":\"肢体活动\",\"Item_Type\":1},{\"Code_Id\":\"A052\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.Foraging\",\"Item_Name\":\"觅食\",\"Item_Type\":1},{\"Code_Id\":\"A052\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.Swallow\",\"Item_Name\":\"吞咽\",\"Item_Type\":1},{\"Code_Id\":\"A052\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.HoldWithTheHand\",\"Item_Name\":\"握持\",\"Item_Type\":1},{\"Code_Id\":\"A052\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.Suck\",\"Item_Name\":\"吸吮\",\"Item_Type\":1},{\"Code_Id\":\"A052\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.Embrace\",\"Item_Name\":\"拥抱\",\"Item_Type\":1},{\"Code_Id\":\"A053\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.MuscleTension\",\"Item_Name\":\"肌张力\",\"Item_Type\":1},{\"Code_Id\":\"A054\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.Complexion\",\"Item_Name\":\"面色\",\"Item_Type\":1},{\"Code_Id\":\"A055\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"OralCavity.Mucosa\",\"Item_Name\":\"口腔黏膜\",\"Item_Type\":1},{\"Code_Id\":\"A056\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"GeneralInfo.Skin\",\"Item_Name\":\"皮肤\",\"Item_Type\":1},{\"Code_Id\":\"A057\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"\",\"Item_Name\":\"呼吸\",\"Item_Type\":1},{\"Code_Id\":\"A058\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"GeneralInfo.DigestiveSystem\",\"Item_Name\":\"消化系统\",\"Item_Type\":1},{\"Code_Id\":\"A059\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.UmbilicalCord\",\"Item_Name\":\"脐带\",\"Item_Type\":1},{\"Code_Id\":\"A060\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"BabyInformation.Umbilicus\",\"Item_Name\":\"脐周\",\"Item_Type\":1},{\"Code_Id\":\"A061\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"PregnantInfo.SituationOfProduction\",\"Item_Name\":\"\",\"Item_Type\":1},{\"Code_Id\":\"A062\",\"Control_Type\":\"0\",\"Doc_Id\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\",\"Item_Id\":\"GeneralInfo.Identity\",\"Item_Name\":\"身份确认方式\",\"Item_Type\":1}]");
                        saveDocumentDetailDic(result.getData());
                        break;
                    case 1006:
                        result.setData("[{\"Code_Id\":\"A001\",\"Opt_Code\":\"A001_0001\",\"Opt_Name\":\"步行\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A001\",\"Opt_Code\":\"A001_0002\",\"Opt_Name\":\"轮椅\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A001\",\"Opt_Code\":\"A001_0003\",\"Opt_Name\":\"平车\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A001\",\"Opt_Code\":\"A001_0004\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0003\",\"Opt_Name\":\"药物\",\"Parent_Opt\":\"A002_0002\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0004\",\"Opt_Name\":\"食物\",\"Parent_Opt\":\"A002_0002\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0005\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"A002_0002\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0001\",\"Opt_Name\":\"清楚\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0002\",\"Opt_Name\":\"嗜睡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0003\",\"Opt_Name\":\"神志模糊\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0004\",\"Opt_Name\":\"昏睡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0005\",\"Opt_Name\":\"浅昏迷\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0006\",\"Opt_Name\":\"深昏迷\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A004\",\"Opt_Code\":\"A004_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A004\",\"Opt_Code\":\"A004_0002\",\"Opt_Name\":\"失明\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A004\",\"Opt_Code\":\"A004_0003\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A005\",\"Opt_Code\":\"A005_0001\",\"Opt_Name\":\"自理(100分)\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A005\",\"Opt_Code\":\"A005_0002\",\"Opt_Name\":\"轻度依赖(60-90分)\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A005\",\"Opt_Code\":\"A005_0003\",\"Opt_Name\":\"中度依赖(41-59分)\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A005\",\"Opt_Code\":\"A005_0004\",\"Opt_Name\":\"重度依赖(0-40分)\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A009\",\"Opt_Code\":\"A009_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A009\",\"Opt_Code\":\"A009_0002\",\"Opt_Name\":\"淡漠\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A009\",\"Opt_Code\":\"A009_0003\",\"Opt_Name\":\"痛苦\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A010\",\"Opt_Code\":\"A010_0001\",\"Opt_Name\":\"未婚\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A010\",\"Opt_Code\":\"A010_0002\",\"Opt_Name\":\"已婚\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0001\",\"Opt_Name\":\"高血压\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0002\",\"Opt_Name\":\"心脏病\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0003\",\"Opt_Name\":\"糖尿病\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0004\",\"Opt_Name\":\"脑血管病\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0005\",\"Opt_Name\":\"手术史\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0006\",\"Opt_Name\":\"精神病\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0007\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A012\",\"Opt_Code\":\"A012_0001\",\"Opt_Name\":\"汉族\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A012\",\"Opt_Code\":\"A012_0002\",\"Opt_Name\":\"回族\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A012\",\"Opt_Code\":\"A012_0003\",\"Opt_Name\":\"满族\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0001\",\"Opt_Name\":\"工人\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0002\",\"Opt_Name\":\"干部\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0003\",\"Opt_Name\":\"农民\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0004\",\"Opt_Name\":\"职员\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0005\",\"Opt_Name\":\"学生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0006\",\"Opt_Name\":\"军人\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0007\",\"Opt_Name\":\"教师\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0008\",\"Opt_Name\":\"医务人员\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0009\",\"Opt_Name\":\"个体\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0010\",\"Opt_Name\":\"演员\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0011\",\"Opt_Name\":\"退休\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0012\",\"Opt_Name\":\"自由职业\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0013\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0014\",\"Opt_Name\":\"离休\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0001\",\"Opt_Name\":\"文盲\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0002\",\"Opt_Name\":\"小学\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0003\",\"Opt_Name\":\"中学\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0004\",\"Opt_Name\":\"高中\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0005\",\"Opt_Name\":\"大专\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0006\",\"Opt_Name\":\"大学本科\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0007\",\"Opt_Name\":\"硕士研究生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0008\",\"Opt_Name\":\"博士研究生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A015\",\"Opt_Code\":\"A015_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A015\",\"Opt_Code\":\"A015_0002\",\"Opt_Name\":\"重听\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A015\",\"Opt_Code\":\"A015_0003\",\"Opt_Name\":\"失聪\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A015\",\"Opt_Code\":\"A015_0004\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A016\",\"Opt_Code\":\"A016_0001\",\"Opt_Name\":\"语言\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A016\",\"Opt_Code\":\"A016_0002\",\"Opt_Name\":\"文字\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A016\",\"Opt_Code\":\"A016_0003\",\"Opt_Name\":\"手势\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A017\",\"Opt_Code\":\"A017_0001\",\"Opt_Name\":\"良好\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A017\",\"Opt_Code\":\"A017_0002\",\"Opt_Name\":\"一般\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A017\",\"Opt_Code\":\"A017_0003\",\"Opt_Name\":\"差\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A018\",\"Opt_Code\":\"A018_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A018\",\"Opt_Code\":\"A018_0002\",\"Opt_Name\":\"充血\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A018\",\"Opt_Code\":\"A018_0003\",\"Opt_Name\":\"破损\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A018\",\"Opt_Code\":\"A018_0004\",\"Opt_Name\":\"溃疡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A018\",\"Opt_Code\":\"A018_0005\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A020\",\"Opt_Code\":\"A020_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A020\",\"Opt_Code\":\"A020_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A020\",\"Opt_Code\":\"A020_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A020\",\"Opt_Code\":\"A020_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A021\",\"Opt_Code\":\"A021_0001\",\"Opt_Name\":\"完整\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A021\",\"Opt_Code\":\"A021_0002\",\"Opt_Name\":\"破损\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A021\",\"Opt_Code\":\"A021_0003\",\"Opt_Name\":\"压疮\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A021\",\"Opt_Code\":\"A021_0004\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A021\",\"Opt_Code\":\"A021_0005\",\"Opt_Name\":\"部位\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A021\",\"Opt_Code\":\"A021_0006\",\"Opt_Name\":\"范围\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0002\",\"Opt_Name\":\"咸\",\"Parent_Opt\":\"A022_0001\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0003\",\"Opt_Name\":\"甜\",\"Parent_Opt\":\"A022_0001\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0004\",\"Opt_Name\":\"辛辣\",\"Parent_Opt\":\"A022_0001\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0005\",\"Opt_Name\":\"油腻\",\"Parent_Opt\":\"A022_0001\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0006\",\"Opt_Name\":\"清淡\",\"Parent_Opt\":\"A022_0001\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0007\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"A022_0001\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0008\",\"Opt_Name\":\"忌食\",\"Parent_Opt\":\"A022_0001\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0009\",\"Opt_Name\":\"异常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0010\",\"Opt_Name\":\"食欲不振\",\"Parent_Opt\":\"A022_0009\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0011\",\"Opt_Name\":\"吞咽困难\",\"Parent_Opt\":\"A022_0009\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0012\",\"Opt_Name\":\"咀嚼困难\",\"Parent_Opt\":\"A022_0009\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0013\",\"Opt_Name\":\"恶心\",\"Parent_Opt\":\"A022_0009\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0014\",\"Opt_Name\":\"呕吐\",\"Parent_Opt\":\"A022_0009\"},{\"Code_Id\":\"A022\",\"Opt_Code\":\"A022_0015\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"A022_0009\"},{\"Code_Id\":\"A023\",\"Opt_Code\":\"A023_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A023\",\"Opt_Code\":\"A023_0002\",\"Opt_Name\":\"入睡困难\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A023\",\"Opt_Code\":\"A023_0003\",\"Opt_Name\":\"多梦\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A023\",\"Opt_Code\":\"A023_0004\",\"Opt_Name\":\"易醒\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A023\",\"Opt_Code\":\"A023_0005\",\"Opt_Name\":\"，每日睡眠{0}小时\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A024\",\"Opt_Code\":\"A024_0001\",\"Opt_Name\":\"否\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A024\",\"Opt_Code\":\"A024_0002\",\"Opt_Name\":\"是\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A024\",\"Opt_Code\":\"A024_0003\",\"Opt_Name\":\"{0}支\\/天\",\"Parent_Opt\":\"A024_0002\"},{\"Code_Id\":\"A025\",\"Opt_Code\":\"A025_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A025\",\"Opt_Code\":\"A025_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A025\",\"Opt_Code\":\"A025_0003\",\"Opt_Name\":\"{0}两\\/天\",\"Parent_Opt\":\"A025_0002\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0002\",\"Opt_Name\":\"失禁\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0003\",\"Opt_Name\":\"尿频\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0004\",\"Opt_Name\":\"尿急\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0005\",\"Opt_Name\":\"尿痛\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0006\",\"Opt_Name\":\"尿潴留\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0007\",\"Opt_Name\":\"留置尿管\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A026\",\"Opt_Code\":\"A026_0008\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0002\",\"Opt_Name\":\"失禁\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0003\",\"Opt_Name\":\"便秘\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0004\",\"Opt_Name\":\"黑便\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0005\",\"Opt_Name\":\"造口\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0006\",\"Opt_Name\":\"腹泻\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0007\",\"Opt_Name\":\"{0}次\\/日\",\"Parent_Opt\":\"A027_0006\"},{\"Code_Id\":\"A027\",\"Opt_Code\":\"A027_0009\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0001\",\"Opt_Name\":\"自如\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0002\",\"Opt_Name\":\"无力\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0003\",\"Opt_Name\":\"偏瘫\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0004\",\"Opt_Name\":\"截瘫\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0005\",\"Opt_Name\":\"全瘫\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0006\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0002\",\"Opt_Name\":\"兴奋\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0003\",\"Opt_Name\":\"焦虑\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0004\",\"Opt_Name\":\"恐惧\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0005\",\"Opt_Name\":\"易激动\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0006\",\"Opt_Name\":\"孤独\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0007\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A030\",\"Opt_Code\":\"A030_0001\",\"Opt_Name\":\"关心\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A030\",\"Opt_Code\":\"A030_0002\",\"Opt_Name\":\"不关心\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A030\",\"Opt_Code\":\"A030_0003\",\"Opt_Name\":\"过于关心\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A030\",\"Opt_Code\":\"A030_0004\",\"Opt_Name\":\"无人照顾\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0001\",\"Opt_Name\":\"床位医生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0002\",\"Opt_Name\":\"责任护士\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0003\",\"Opt_Name\":\"病房环境\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0004\",\"Opt_Name\":\"住院制度\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0005\",\"Opt_Name\":\"探视饮食制度\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0006\",\"Opt_Name\":\"安全告知\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0007\",\"Opt_Name\":\"饮食（喂养）指导\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0008\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A072\",\"Opt_Code\":\"A072_0001\",\"Opt_Name\":\"卧床\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A072\",\"Opt_Code\":\"A072_0002\",\"Opt_Name\":\"平车\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A072\",\"Opt_Code\":\"A072_0003\",\"Opt_Name\":\"轮椅\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0003\",\"Opt_Name\":\"药物\",\"Parent_Opt\":\"A002_0002\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0004\",\"Opt_Name\":\"食物\",\"Parent_Opt\":\"A002_0002\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0005\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"A002_0002\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0001\",\"Opt_Name\":\"清楚\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0002\",\"Opt_Name\":\"嗜睡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0003\",\"Opt_Name\":\"神志模糊\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0004\",\"Opt_Name\":\"昏睡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0005\",\"Opt_Name\":\"浅昏迷\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0006\",\"Opt_Name\":\"深昏迷\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A009\",\"Opt_Code\":\"A009_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A009\",\"Opt_Code\":\"A009_0002\",\"Opt_Name\":\"淡漠\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A009\",\"Opt_Code\":\"A009_0003\",\"Opt_Name\":\"痛苦\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A012\",\"Opt_Code\":\"A012_0001\",\"Opt_Name\":\"汉族\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A012\",\"Opt_Code\":\"A012_0002\",\"Opt_Name\":\"回族\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A012\",\"Opt_Code\":\"A012_0003\",\"Opt_Name\":\"满族\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0001\",\"Opt_Name\":\"文盲\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0002\",\"Opt_Name\":\"小学\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0003\",\"Opt_Name\":\"中学\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0004\",\"Opt_Name\":\"高中\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0005\",\"Opt_Name\":\"大专\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0006\",\"Opt_Name\":\"大学本科\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0007\",\"Opt_Name\":\"硕士研究生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0008\",\"Opt_Name\":\"博士研究生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0001\",\"Opt_Name\":\"自如\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0002\",\"Opt_Name\":\"无力\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0003\",\"Opt_Name\":\"偏瘫\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0004\",\"Opt_Name\":\"截瘫\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0005\",\"Opt_Name\":\"全瘫\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A028\",\"Opt_Code\":\"A028_0006\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0001\",\"Opt_Name\":\"床位医生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0002\",\"Opt_Name\":\"责任护士\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0003\",\"Opt_Name\":\"病房环境\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0004\",\"Opt_Name\":\"住院制度\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0005\",\"Opt_Name\":\"探视饮食制度\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0006\",\"Opt_Name\":\"安全告知\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0007\",\"Opt_Name\":\"饮食（喂养）指导\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0008\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A043\",\"Opt_Code\":\"A043_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A043\",\"Opt_Code\":\"A043_0002\",\"Opt_Name\":\"沟通障碍\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A043\",\"Opt_Code\":\"A043_0003\",\"Opt_Name\":\"发育未成熟\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A043\",\"Opt_Code\":\"A043_0004\",\"Opt_Name\":\"失语\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A044\",\"Opt_Code\":\"A044_0001\",\"Opt_Name\":\"已闭\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A044\",\"Opt_Code\":\"A044_0002\",\"Opt_Name\":\"未闭\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A044\",\"Opt_Code\":\"A044_0003\",\"Opt_Name\":\"平坦\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A044\",\"Opt_Code\":\"A044_0004\",\"Opt_Name\":\"凹陷\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A044\",\"Opt_Code\":\"A044_0005\",\"Opt_Name\":\"隆起\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A045\",\"Opt_Code\":\"A045_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A045\",\"Opt_Code\":\"A045_0002\",\"Opt_Name\":\"破损\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A045\",\"Opt_Code\":\"A045_0003\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A046\",\"Opt_Code\":\"A046_0001\",\"Opt_Name\":\"稳定\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A046\",\"Opt_Code\":\"A046_0002\",\"Opt_Name\":\"紧张\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A046\",\"Opt_Code\":\"A046_0003\",\"Opt_Name\":\"恐惧\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A046\",\"Opt_Code\":\"A046_0004\",\"Opt_Name\":\"抑郁\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A046\",\"Opt_Code\":\"A046_0005\",\"Opt_Name\":\"烦躁\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A046\",\"Opt_Code\":\"A046_0006\",\"Opt_Name\":\"哭闹\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A046\",\"Opt_Code\":\"A046_0007\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A063\",\"Opt_Code\":\"A063_0001\",\"Opt_Name\":\"完整\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A063\",\"Opt_Code\":\"A063_0002\",\"Opt_Name\":\"鹅口疮\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A063\",\"Opt_Code\":\"A063_0003\",\"Opt_Name\":\"溃疡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A063\",\"Opt_Code\":\"A063_0004\",\"Opt_Name\":\"疱疹\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A063\",\"Opt_Code\":\"A063_0005\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A064\",\"Opt_Code\":\"A064_0001\",\"Opt_Name\":\"完整\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A064\",\"Opt_Code\":\"A064_0002\",\"Opt_Name\":\"黄染\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A064\",\"Opt_Code\":\"A064_0003\",\"Opt_Name\":\"水肿\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A064\",\"Opt_Code\":\"A064_0004\",\"Opt_Name\":\"苍白\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A064\",\"Opt_Code\":\"A064_0005\",\"Opt_Name\":\"发绀\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A064\",\"Opt_Code\":\"A064_0006\",\"Opt_Name\":\"皮疹\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A064\",\"Opt_Code\":\"A064_0007\",\"Opt_Name\":\"破损\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"1\",\"Opt_Name\":\"入院\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"2\",\"Opt_Name\":\"出院\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"3\",\"Opt_Name\":\"转入\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"4\",\"Opt_Name\":\"手术\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"5\",\"Opt_Name\":\"分娩\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"6\",\"Opt_Name\":\"死亡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sXbsm\",\"Opt_Code\":\"1\",\"Opt_Name\":\"外出\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sXbsm\",\"Opt_Code\":\"2\",\"Opt_Name\":\"拒测\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sXbsm\",\"Opt_Code\":\"3\",\"Opt_Name\":\"请假\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"1\",\"Opt_Name\":\"入院\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"2\",\"Opt_Name\":\"出院\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"3\",\"Opt_Name\":\"转入\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"4\",\"Opt_Name\":\"手术\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"5\",\"Opt_Name\":\"分娩\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sSbsm\",\"Opt_Code\":\"6\",\"Opt_Name\":\"死亡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sXbsm\",\"Opt_Code\":\"1\",\"Opt_Name\":\"外出\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sXbsm\",\"Opt_Code\":\"2\",\"Opt_Name\":\"拒测\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sXbsm\",\"Opt_Code\":\"3\",\"Opt_Name\":\"请假\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"YWGM2\",\"Opt_Code\":\"1\",\"Opt_Name\":\"青霉素\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"YWGM2\",\"Opt_Code\":\"2\",\"Opt_Name\":\"阿莫西林\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"YWGM2\",\"Opt_Code\":\"3\",\"Opt_Name\":\"破伤风抗毒素\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"YWGM2\",\"Opt_Code\":\"4\",\"Opt_Name\":\"普鲁卡因\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"YWGM2\",\"Opt_Code\":\"5\",\"Opt_Name\":\"碘\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"YWGM2\",\"Opt_Code\":\"6\",\"Opt_Name\":\"酒精\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"YWGM2\",\"Opt_Code\":\"7\",\"Opt_Name\":\"头孢替胺\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"nGw\",\"Opt_Code\":\"1\",\"Opt_Name\":\"体温\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"nGw\",\"Opt_Code\":\"2\",\"Opt_Name\":\"腋温\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"nGw\",\"Opt_Code\":\"3\",\"Opt_Name\":\"肛温\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sYwgm\",\"Opt_Code\":\"1\",\"Opt_Name\":\"青霉素\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sYwgm\",\"Opt_Code\":\"2\",\"Opt_Name\":\"阿莫西林\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sYwgm\",\"Opt_Code\":\"3\",\"Opt_Name\":\"破伤风抗毒素\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sYwgm\",\"Opt_Code\":\"4\",\"Opt_Name\":\"普鲁卡因\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sYwgm\",\"Opt_Code\":\"5\",\"Opt_Name\":\"碘\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sYwgm\",\"Opt_Code\":\"6\",\"Opt_Name\":\"酒精\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"sYwgm\",\"Opt_Code\":\"7\",\"Opt_Name\":\"头孢替胺\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"nGw\",\"Opt_Code\":\"1\",\"Opt_Name\":\"体温\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"nGw\",\"Opt_Code\":\"2\",\"Opt_Name\":\"腋温\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"nGw\",\"Opt_Code\":\"3\",\"Opt_Name\":\"肛温\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A064\",\"Opt_Code\":\"A064_0008\",\"Opt_Name\":\"臀红\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A064\",\"Opt_Code\":\"A064_0009\",\"Opt_Name\":\"瘀斑\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A064\",\"Opt_Code\":\"A064_0010\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A065\",\"Opt_Code\":\"A065_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A065\",\"Opt_Code\":\"A065_0002\",\"Opt_Name\":\"失禁\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A065\",\"Opt_Code\":\"A065_0003\",\"Opt_Name\":\"便秘\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A065\",\"Opt_Code\":\"A065_0004\",\"Opt_Name\":\"便血\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A065\",\"Opt_Code\":\"A065_0005\",\"Opt_Name\":\"肠造瘘\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A065\",\"Opt_Code\":\"A065_0006\",\"Opt_Name\":\"腹泻\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A065\",\"Opt_Code\":\"A065_0007\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A066\",\"Opt_Code\":\"A066_0001\",\"Opt_Name\":\"母乳喂养\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A066\",\"Opt_Code\":\"A066_0002\",\"Opt_Name\":\"人工喂养\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A066\",\"Opt_Code\":\"A066_0003\",\"Opt_Name\":\"混合喂养\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A066\",\"Opt_Code\":\"A066_0004\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A067\",\"Opt_Code\":\"A067_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A067\",\"Opt_Code\":\"A067_0002\",\"Opt_Name\":\"易醒\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A067\",\"Opt_Code\":\"A067_0003\",\"Opt_Name\":\"盗汗\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A068\",\"Opt_Code\":\"A068_0001\",\"Opt_Name\":\"关心\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A068\",\"Opt_Code\":\"A068_0002\",\"Opt_Name\":\"不关心\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A068\",\"Opt_Code\":\"A068_0003\",\"Opt_Name\":\"过于关心\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A068\",\"Opt_Code\":\"A068_0004\",\"Opt_Name\":\"配合\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A068\",\"Opt_Code\":\"A068_0005\",\"Opt_Name\":\"不配合\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A069\",\"Opt_Code\":\"A069_0001\",\"Opt_Name\":\"步行\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A069\",\"Opt_Code\":\"A069_0002\",\"Opt_Name\":\"轮椅\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A069\",\"Opt_Code\":\"A069_0003\",\"Opt_Name\":\"平车\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A069\",\"Opt_Code\":\"A069_0004\",\"Opt_Name\":\"抱入\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A069\",\"Opt_Code\":\"A069_0005\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A070\",\"Opt_Code\":\"A070_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A070\",\"Opt_Code\":\"A070_0002\",\"Opt_Name\":\"失禁\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A070\",\"Opt_Code\":\"A070_0003\",\"Opt_Name\":\"尿频\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A070\",\"Opt_Code\":\"A070_0004\",\"Opt_Name\":\"尿急\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A070\",\"Opt_Code\":\"A070_0005\",\"Opt_Name\":\"尿痛\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A070\",\"Opt_Code\":\"A070_0006\",\"Opt_Name\":\"血尿\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A070\",\"Opt_Code\":\"A070_0007\",\"Opt_Name\":\"蛋白尿\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A070\",\"Opt_Code\":\"A070_0008\",\"Opt_Name\":\"尿潴留\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A070\",\"Opt_Code\":\"A070_0009\",\"Opt_Name\":\"留置尿管\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A070\",\"Opt_Code\":\"A070_0010\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A001\",\"Opt_Code\":\"A001_0001\",\"Opt_Name\":\"步行\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A001\",\"Opt_Code\":\"A001_0002\",\"Opt_Name\":\"轮椅\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A001\",\"Opt_Code\":\"A001_0003\",\"Opt_Name\":\"平车\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A001\",\"Opt_Code\":\"A001_0004\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0003\",\"Opt_Name\":\"药物\",\"Parent_Opt\":\"A002_0002\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0004\",\"Opt_Name\":\"食物\",\"Parent_Opt\":\"A002_0002\"},{\"Code_Id\":\"A002\",\"Opt_Code\":\"A002_0005\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"A002_0002\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0001\",\"Opt_Name\":\"清楚\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0002\",\"Opt_Name\":\"嗜睡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0003\",\"Opt_Name\":\"神志模糊\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0004\",\"Opt_Name\":\"昏睡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0005\",\"Opt_Name\":\"浅昏迷\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A003\",\"Opt_Code\":\"A003_0006\",\"Opt_Name\":\"深昏迷\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A007\",\"Opt_Code\":\"A007_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A007\",\"Opt_Code\":\"A007_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A007\",\"Opt_Code\":\"A007_0003\",\"Opt_Name\":\"(部位：\",\"Parent_Opt\":\"A007_0002\"},{\"Code_Id\":\"A007\",\"Opt_Code\":\"A007_0005\",\"Opt_Name\":\"水肿程度：{0}+\",\"Parent_Opt\":\"A007_0002\"},{\"Code_Id\":\"A007\",\"Opt_Code\":\"A007_0006\",\"Opt_Name\":\"++\",\"Parent_Opt\":\"A007_0002\"},{\"Code_Id\":\"A007\",\"Opt_Code\":\"A007_0007\",\"Opt_Name\":\"+++\",\"Parent_Opt\":\"A007_0002\"},{\"Code_Id\":\"A007\",\"Opt_Code\":\"A007_0008\",\"Opt_Name\":\"{0}++++）\",\"Parent_Opt\":\"A007_0002\"},{\"Code_Id\":\"A008\",\"Opt_Code\":\"A008_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A008\",\"Opt_Code\":\"A008_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A008\",\"Opt_Code\":\"A008_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A008\",\"Opt_Code\":\"A008_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A008\",\"Opt_Code\":\"A008_0004\",\"Opt_Name\":\"({0}+\",\"Parent_Opt\":\"A008_0002\"},{\"Code_Id\":\"A008\",\"Opt_Code\":\"A008_0004\",\"Opt_Name\":\"({0}+\",\"Parent_Opt\":\"A008_0002\"},{\"Code_Id\":\"A008\",\"Opt_Code\":\"A008_0005\",\"Opt_Name\":\"++\",\"Parent_Opt\":\"A008_0002\"},{\"Code_Id\":\"A008\",\"Opt_Code\":\"A008_0005\",\"Opt_Name\":\"++\",\"Parent_Opt\":\"A008_0002\"},{\"Code_Id\":\"A008\",\"Opt_Code\":\"A008_0006\",\"Opt_Name\":\"+++\",\"Parent_Opt\":\"A008_0002\"},{\"Code_Id\":\"A008\",\"Opt_Code\":\"A008_0006\",\"Opt_Name\":\"+++\",\"Parent_Opt\":\"A008_0002\"},{\"Code_Id\":\"A008\",\"Opt_Code\":\"A008_0007\",\"Opt_Name\":\"{0}++++）\",\"Parent_Opt\":\"A008_0002\"},{\"Code_Id\":\"A008\",\"Opt_Code\":\"A008_0007\",\"Opt_Name\":\"{0}++++）\",\"Parent_Opt\":\"A008_0002\"},{\"Code_Id\":\"A009\",\"Opt_Code\":\"A009_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A009\",\"Opt_Code\":\"A009_0002\",\"Opt_Name\":\"淡漠\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A009\",\"Opt_Code\":\"A009_0003\",\"Opt_Name\":\"痛苦\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A010\",\"Opt_Code\":\"A010_0001\",\"Opt_Name\":\"未婚\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A010\",\"Opt_Code\":\"A010_0002\",\"Opt_Name\":\"已婚\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0001\",\"Opt_Name\":\"高血压\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0002\",\"Opt_Name\":\"心脏病\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0003\",\"Opt_Name\":\"糖尿病\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0004\",\"Opt_Name\":\"脑血管病\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0005\",\"Opt_Name\":\"手术史\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0006\",\"Opt_Name\":\"精神病\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A011\",\"Opt_Code\":\"A011_0007\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A012\",\"Opt_Code\":\"A012_0001\",\"Opt_Name\":\"汉族\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A012\",\"Opt_Code\":\"A012_0002\",\"Opt_Name\":\"回族\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A012\",\"Opt_Code\":\"A012_0003\",\"Opt_Name\":\"满族\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0001\",\"Opt_Name\":\"工人\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0002\",\"Opt_Name\":\"干部\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0003\",\"Opt_Name\":\"农民\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0004\",\"Opt_Name\":\"职员\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0005\",\"Opt_Name\":\"学生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0006\",\"Opt_Name\":\"军人\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0007\",\"Opt_Name\":\"教师\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0008\",\"Opt_Name\":\"医务人员\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0009\",\"Opt_Name\":\"个体\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0010\",\"Opt_Name\":\"演员\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0011\",\"Opt_Name\":\"退休\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0012\",\"Opt_Name\":\"自由职业\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0013\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A013\",\"Opt_Code\":\"A013_0014\",\"Opt_Name\":\"离休\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0001\",\"Opt_Name\":\"文盲\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0002\",\"Opt_Name\":\"小学\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0003\",\"Opt_Name\":\"中学\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0004\",\"Opt_Name\":\"高中\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0005\",\"Opt_Name\":\"大专\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0006\",\"Opt_Name\":\"大学本科\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0007\",\"Opt_Name\":\"硕士研究生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A014\",\"Opt_Code\":\"A014_0008\",\"Opt_Name\":\"博士研究生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A020\",\"Opt_Code\":\"A020_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A020\",\"Opt_Code\":\"A020_0001\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A020\",\"Opt_Code\":\"A020_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A020\",\"Opt_Code\":\"A020_0002\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0002\",\"Opt_Name\":\"兴奋\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0003\",\"Opt_Name\":\"焦虑\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0004\",\"Opt_Name\":\"恐惧\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0005\",\"Opt_Name\":\"易激动\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0006\",\"Opt_Name\":\"孤独\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A029\",\"Opt_Code\":\"A029_0007\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A030\",\"Opt_Code\":\"A030_0001\",\"Opt_Name\":\"关心\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A030\",\"Opt_Code\":\"A030_0002\",\"Opt_Name\":\"不关心\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A030\",\"Opt_Code\":\"A030_0003\",\"Opt_Name\":\"过于关心\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A030\",\"Opt_Code\":\"A030_0004\",\"Opt_Name\":\"无人照顾\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0001\",\"Opt_Name\":\"床位医生\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0002\",\"Opt_Name\":\"责任护士\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0003\",\"Opt_Name\":\"病房环境\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0004\",\"Opt_Name\":\"住院制度\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0005\",\"Opt_Name\":\"探视饮食制度\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0006\",\"Opt_Name\":\"安全告知\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0007\",\"Opt_Name\":\"饮食（喂养）指导\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A031\",\"Opt_Code\":\"A031_0008\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A032\",\"Opt_Code\":\"A032_0001\",\"Opt_Name\":\"孕{0}次\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A032\",\"Opt_Code\":\"A032_0002\",\"Opt_Name\":\"产{0}次\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A032\",\"Opt_Code\":\"A032_0003\",\"Opt_Name\":\"人流{0}次\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A033\",\"Opt_Code\":\"A033_0001\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A033\",\"Opt_Code\":\"A033_0002\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A033\",\"Opt_Code\":\"A033_0003\",\"Opt_Name\":\"出血描述\",\"Parent_Opt\":\"A033_0001\"},{\"Code_Id\":\"A034\",\"Opt_Code\":\"A034_0001\",\"Opt_Name\":\"有\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A034\",\"Opt_Code\":\"A034_0002\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A034\",\"Opt_Code\":\"A034_0003\",\"Opt_Name\":\"药物名称\",\"Parent_Opt\":\"A034_0001\"},{\"Code_Id\":\"A035\",\"Opt_Code\":\"A035_0001\",\"Opt_Name\":\"掌握\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A035\",\"Opt_Code\":\"A035_0002\",\"Opt_Name\":\"了解\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A035\",\"Opt_Code\":\"A035_0003\",\"Opt_Name\":\"不知道\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A036\",\"Opt_Code\":\"A036_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A036\",\"Opt_Code\":\"A036_0002\",\"Opt_Name\":\"异常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A037\",\"Opt_Code\":\"A037_0001\",\"Opt_Name\":\"未破\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A037\",\"Opt_Code\":\"A037_0002\",\"Opt_Name\":\"已破\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A037\",\"Opt_Code\":\"A037_0003\",\"Opt_Name\":\"（羊水长度{0}）\",\"Parent_Opt\":\"A037_0002\"},{\"Code_Id\":\"A038\",\"Opt_Code\":\"A038_0001\",\"Opt_Name\":\"规律\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A038\",\"Opt_Code\":\"A038_0002\",\"Opt_Name\":\"不规律\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A039\",\"Opt_Code\":\"A039_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A039\",\"Opt_Code\":\"A039_0002\",\"Opt_Name\":\"异常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A039\",\"Opt_Code\":\"A039_0003\",\"Opt_Name\":\"乳头情况\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A072\",\"Opt_Code\":\"A072_0001\",\"Opt_Name\":\"卧床\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A072\",\"Opt_Code\":\"A072_0002\",\"Opt_Name\":\"平车\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A072\",\"Opt_Code\":\"A072_0003\",\"Opt_Name\":\"轮椅\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A047\",\"Opt_Code\":\"A047_0001\",\"Opt_Name\":\"顺产\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A047\",\"Opt_Code\":\"A047_0002\",\"Opt_Name\":\"助产\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A047\",\"Opt_Code\":\"A047_0003\",\"Opt_Name\":\"剖宫产\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A047\",\"Opt_Code\":\"A047_0004\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A048\",\"Opt_Code\":\"A048_0001\",\"Opt_Name\":\"母乳\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A048\",\"Opt_Code\":\"A048_0002\",\"Opt_Name\":\"配方奶\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A048\",\"Opt_Code\":\"A048_0003\",\"Opt_Name\":\"混合喂养\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A049\",\"Opt_Code\":\"A049_0001\",\"Opt_Name\":\"清醒\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A049\",\"Opt_Code\":\"A049_0002\",\"Opt_Name\":\"激惹\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A049\",\"Opt_Code\":\"A049_0003\",\"Opt_Name\":\"嗜睡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A049\",\"Opt_Code\":\"A049_0004\",\"Opt_Name\":\"迟钝\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A049\",\"Opt_Code\":\"A049_0005\",\"Opt_Name\":\"昏睡\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A049\",\"Opt_Code\":\"A049_0006\",\"Opt_Name\":\"昏迷\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A050\",\"Opt_Code\":\"A050_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A050\",\"Opt_Code\":\"A050_0002\",\"Opt_Name\":\"惊叫\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A050\",\"Opt_Code\":\"A050_0003\",\"Opt_Name\":\"微弱\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A051\",\"Opt_Code\":\"A051_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A051\",\"Opt_Code\":\"A051_0002\",\"Opt_Name\":\"抽搐\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A052\",\"Opt_Code\":\"A052_0001\",\"Opt_Name\":\"存在\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A052\",\"Opt_Code\":\"A052_0001\",\"Opt_Name\":\"存在\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A052\",\"Opt_Code\":\"A052_0001\",\"Opt_Name\":\"存在\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A052\",\"Opt_Code\":\"A052_0001\",\"Opt_Name\":\"存在\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A052\",\"Opt_Code\":\"A052_0001\",\"Opt_Name\":\"存在\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A052\",\"Opt_Code\":\"A052_0002\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A052\",\"Opt_Code\":\"A052_0002\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A052\",\"Opt_Code\":\"A052_0002\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A052\",\"Opt_Code\":\"A052_0002\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A052\",\"Opt_Code\":\"A052_0002\",\"Opt_Name\":\"无\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A053\",\"Opt_Code\":\"A053_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A053\",\"Opt_Code\":\"A053_0002\",\"Opt_Name\":\"高\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A053\",\"Opt_Code\":\"A053_0003\",\"Opt_Name\":\"低\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A054\",\"Opt_Code\":\"A054_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A054\",\"Opt_Code\":\"A054_0002\",\"Opt_Name\":\"潮红\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A054\",\"Opt_Code\":\"A054_0003\",\"Opt_Name\":\"灰暗\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A054\",\"Opt_Code\":\"A054_0004\",\"Opt_Name\":\"苍白\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A054\",\"Opt_Code\":\"A054_0005\",\"Opt_Name\":\"黄染\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A054\",\"Opt_Code\":\"A054_0006\",\"Opt_Name\":\"紫绀\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A054\",\"Opt_Code\":\"A054_0007\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A055\",\"Opt_Code\":\"A055_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A055\",\"Opt_Code\":\"A055_0002\",\"Opt_Name\":\"破溃\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A055\",\"Opt_Code\":\"A055_0003\",\"Opt_Name\":\"鹅口疮\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A056\",\"Opt_Code\":\"A056_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A056\",\"Opt_Code\":\"A056_0002\",\"Opt_Name\":\"潮红\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A056\",\"Opt_Code\":\"A056_0003\",\"Opt_Name\":\"干燥\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A056\",\"Opt_Code\":\"A056_0004\",\"Opt_Name\":\"苍白\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A056\",\"Opt_Code\":\"A056_0005\",\"Opt_Name\":\"发绀\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A056\",\"Opt_Code\":\"A056_0007\",\"Opt_Name\":\"黄染（{0}轻\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A056\",\"Opt_Code\":\"A056_0008\",\"Opt_Name\":\"中\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A056\",\"Opt_Code\":\"A056_0009\",\"Opt_Name\":\"重）\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A056\",\"Opt_Code\":\"A056_0010\",\"Opt_Name\":\"水肿\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A056\",\"Opt_Code\":\"A056_0011\",\"Opt_Name\":\"出血点\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A056\",\"Opt_Code\":\"A056_0012\",\"Opt_Name\":\"皮疹\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A057\",\"Opt_Code\":\"A057_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A057\",\"Opt_Code\":\"A057_0002\",\"Opt_Name\":\"稍促\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A057\",\"Opt_Code\":\"A057_0003\",\"Opt_Name\":\"困难\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A057\",\"Opt_Code\":\"A057_0004\",\"Opt_Name\":\"不规则\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A057\",\"Opt_Code\":\"A057_0005\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A058\",\"Opt_Code\":\"A058_0001\",\"Opt_Name\":\"正常\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A058\",\"Opt_Code\":\"A058_0002\",\"Opt_Name\":\"胎粪\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A058\",\"Opt_Code\":\"A058_0003\",\"Opt_Name\":\"腹胀\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A058\",\"Opt_Code\":\"A058_0004\",\"Opt_Name\":\"腹泻\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A058\",\"Opt_Code\":\"A058_0005\",\"Opt_Name\":\"呕吐\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A058\",\"Opt_Code\":\"A058_0006\",\"Opt_Name\":\"便秘\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A058\",\"Opt_Code\":\"A058_0007\",\"Opt_Name\":\"便血\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A059\",\"Opt_Code\":\"A059_0001\",\"Opt_Name\":\"未落\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A059\",\"Opt_Code\":\"A059_0002\",\"Opt_Name\":\"已落\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A060\",\"Opt_Code\":\"A060_0001\",\"Opt_Name\":\"干燥\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A060\",\"Opt_Code\":\"A060_0002\",\"Opt_Name\":\"红肿\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A060\",\"Opt_Code\":\"A060_0003\",\"Opt_Name\":\"其他\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A061\",\"Opt_Code\":\"A061_0001\",\"Opt_Name\":\"第{0}胎\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A061\",\"Opt_Code\":\"A061_0002\",\"Opt_Name\":\"{0}产\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A061\",\"Opt_Code\":\"A061_0004\",\"Opt_Name\":\"月足\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A061\",\"Opt_Code\":\"A061_0005\",\"Opt_Name\":\"早产\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A061\",\"Opt_Code\":\"A061_0006\",\"Opt_Name\":\"（{0}）周\",\"Parent_Opt\":\"A061_0005\"},{\"Code_Id\":\"A061\",\"Opt_Code\":\"A061_0008\",\"Opt_Name\":\"过期（{0}）周\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A061\",\"Opt_Code\":\"A061_0010\",\"Opt_Name\":\"双胎\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A062\",\"Opt_Code\":\"A062_0001\",\"Opt_Name\":\"双腕带\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A062\",\"Opt_Code\":\"A062_0002\",\"Opt_Name\":\"父母手指印\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A062\",\"Opt_Code\":\"A062_0003\",\"Opt_Name\":\"婴儿足印\",\"Parent_Opt\":\"\"},{\"Code_Id\":\"A062\",\"Opt_Code\":\"A062_0004\",\"Opt_Name\":\"家长身份\",\"Parent_Opt\":\"\"}]");
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
//        String serverUrl = NISUrl + "/ws/NRService";
        volleyTool.sendJsonRequest(1004, false, null, Request.Method.GET, requestMethod, serverUrl);
    }

    /**
     * 获取文书详细信息
     */
    private void getDocumentDetailDicFromWeb() {
      String requestMethod = "";
      String serverUrl = "http://www.baidu.com/";
//    	String requestMethod = "GetNursingDocumentMetaDataList";
//    	String serverUrl = NISUrl + "/ws/NRService";
        volleyTool.sendJsonRequest(1005, false, null, Request.Method.GET, requestMethod, serverUrl);
    }

    /**
     * 获取选项信息
     */
    private void getOptionDicFromWeb() {
      String requestMethod = "";
      String serverUrl = "http://www.baidu.com/";
//    	String requestMethod = "GetNursingDocumentCodeItemList";
//    	String serverUrl = NISUrl + "/ws/NRService";
        volleyTool.sendJsonRequest(1006, false, null, Request.Method.GET, requestMethod, serverUrl);
    }

    /**
     * 保存文书基本信息
     *
     * @param str
     */
    private void saveDocumentDic(String str) {
        str.replace(""," ");
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
     *
     * @param groupID
     * @param itemID
     */
    public void delete(int groupID, int itemID) {
        patPosition = groupID;
        listView.setSelection(groupID);
        businessDataInfoList.get(groupID).getWsDataList().remove(itemID);
        recordAdapter.updateList(businessDataInfoList);
    }

    /**
     * 初始化view
     */
    @SuppressLint("NewApi")
	private void initView() {
        timeText.setText(getDate());
        // 建立数据源
        String[] mItems = getResources().getStringArray(R.array.type);
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner.setAdapter(adapter);

        String[] mTempItems = getResources().getStringArray(R.array.temp);
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> tempAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mTempItems);
        tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        tempSpinner.setAdapter(tempAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!StringUtils.isEquals(typeList.get(i),selectedType)&&listView.getChildAt(0)!=null){
                    BaseToast.showToastNotRepeat(StandingRecordActivity.this,"请先保存现有记录单",2000);
                    spinner.setSelection(typeList.indexOf(selectedType));
                }else {
                    selectedType = typeList.get(i);
                    if (i==0){
                        tempLayout.setVisibility(View.VISIBLE);
                    }else {
                        tempLayout.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        initGLView();
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAll();
                finish();
            }
        });
        saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAll();
//                checkData();
                if (businessDataInfoList.size() == 0){
                    showTip("尚未录入任何数据！");
                    return;
                }
                for (BusinessDataInfo businessDataInfo : businessDataInfoList) {
                    if (StringUtils.isBlank(businessDataInfo.getBedNo())) {
                        showTip("尚有未确定床号患者！");
                        return;
                    } else if (StringUtils.isBlank(businessDataInfo.getPatName())) {
                        showTip(businessDataInfo.getBedNo() + "床患者信息错误，请检查数据！");
                        return;
                    }
                }
                saveRecordInfo();
//                if (StringUtils.isEquals(typeList.get(spinner.getSelectedItemPosition()), "体温单")) {
//                    saveRecordInfo(new ArrayList<FormCheck>());
//                }else {
//                     String formStr = IFlyNursing.getInstance().getFormStr();
//                    String formStr = "[{\"Name\":\"体温单\",\"Bldm\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\"},{\"Name\":\"新生儿体温单\",\"Bldm\":\"1DCFA477-6D8D-4C8B-B8CE-F846BBC615D4\"},{\"Name\":\"术前护理评估单\",\"Bldm\":\"333B2609-5504-4227-A520-793C3B95FD73\"},{\"Name\":\"术后护理评估单\",\"Bldm\":\"2B84147E-5824-473D-A84A-E21B166FAA42\"},{\"Name\":\"危重患者护理记录单\",\"Bldm\":\"E17CAE68-1C7F-4B78-B271-BC456787ED18\"},{\"Name\":\"血糖记录表\",\"Bldm\":\"1673A68E-3F32-4AEE-B2B3-83406699F538\"},{\"Name\":\"病人转科交接记录单\",\"Bldm\":\"E33F9E6A-DFF1-4057-B321-D73B70FB7799\"},{\"Name\":\"内科住院患者护理记录单\",\"Bldm\":\"A9B12E93-9606-4618-AC1E-EA7F22894478\"},{\"Name\":\"新入院评估单\",\"Bldm\":\"C2400BDC-B92F-489E-93A3-51E258B26F67\"},{\"Name\":\"(新)新入院评估单\",\"Bldm\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\"},{\"Name\":\"(新)产科入院评估单\",\"Bldm\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\"},{\"Name\":\"(新)儿科入院评估单\",\"Bldm\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\"},{\"Name\":\"(新)新生儿入院评估单\",\"Bldm\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\"}]";
//                    List<FormCheck> formCheckList = new Gson().fromJson(formStr, new TypeToken<List<FormCheck>>() {
//                    }.getType());
//                    CustomDialog customDialog = new CustomDialog(StandingRecordActivity.this, formCheckList);
//                    customDialog.show();
//                }

            }
        });
    }


    private Handler mHandler = new Handler();
    /**
     * 顶部语音计时显示
     */
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            topVoiceTime++;
            if (topVoiceTime < 10) {
                topVoiceTimeTextView.setText("0:0" + topVoiceTime);
            } else {
                topVoiceTimeTextView.setText("0:" + topVoiceTime);
            }
            mHandler.postDelayed(mRunnable, 1000);
        }
    };


    /**
     * 初始化GLView
     */
    private void initGLView(){

        glWaveFormView.init();
        miniWaveSurface.init();

        glWaveFormView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speech();
                glWaveFormView.setLongClickable(false);
            }
        });

        glWaveFormView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                topVoiceTime = 0;
                topVoiceTimeTextView.setText("0:00");
                mHandler.postDelayed(mRunnable, 1000);
                showTopSpeechLayout();
                glWaveFormView.longClickGLWV();
                speech();
                isLongClick = true;
                return true;
            }
        });

        glWaveFormView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (isLongClick) {
                            speech();
                        }
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 显示顶部语音动画
     */
    private void showTopSpeechLayout() {
        title.setVisibility(View.GONE);
        topVoice.setVisibility(View.VISIBLE);
    }


    /**
     * 隐藏顶部语音动画
     */
    private void hideTopSpeechLayout() {
        title.setVisibility(View.VISIBLE);
        topVoice.setVisibility(View.GONE);
        glWaveFormView.reset();
        isLongClick = false;
    }



    /**
     * 校对数据
     */
    private void checkData(){
        MappingDao mappingDao = new MappingDao(this);
        List<MappingInfo> mappingInfos = mappingDao.getMappingInfoList();
        for (MappingInfo mappingInfo : mappingInfos){
            DocumentDetailDic documentDetailDic = documentDetailDicDao.getDocumentDetailDic(mappingInfo.getValue());
            if (documentDetailDic ==null){
                Log.e("DATA",mappingInfo.getValue());
            }
        }
    }

    private void checkMap(){
        List<DocumentDetailDic> documentDetailDics = documentDetailDicDao.getItemDicList();
        MappingDao mappingDao = new MappingDao(this);
        for (DocumentDetailDic documentDetailDic : documentDetailDics){
            MappingInfo mappingInfo = mappingDao.getMappingDicByName(documentDetailDic.getItemName());
            if (mappingInfo ==null){
                Log.e("DATA",documentDetailDic.getItemName());
            }
        }
    }


    /**
     * 保存护理记录数据
     */
    public void saveRecordInfo() {
        String dateStr = timeText.getText().toString();
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            date = new Date();
        }
        int hours = date.getHours();
        int min = date.getMinutes();
        float currentTime = hours + min/60f;
        if (StringUtils.isEquals(typeList.get(spinner.getSelectedItemPosition()), "体温单")) {
            String[] times = IFlyNursing.getInstance().getTimes();
            for (String string : times) {
                if (Math.abs(currentTime - Integer.parseInt(string)) < 2) {
                    hours = Integer.parseInt(string);
                    break;
                }
            }
            //添加体温测量方式
            for (BusinessDataInfo businessDataInfo : businessDataInfoList) {
                WSData tempWsData = new WSData();
                tempWsData.setID("Temperature.Way");
                tempWsData.setName("体温测量方式");
                tempWsData.setValueCaption(tempSpinner.getSelectedItem().toString());
                businessDataInfo.getWsDataList().add(tempWsData);
            }
        }
        DocumentDic documentDic = documentDicDao.getDocumentDic(typeList.get(spinner.getSelectedItemPosition()));
        date.setHours(hours);
        date.setMinutes(0);
        date.setSeconds(0);
        for (BusinessDataInfo businessDataInfo : businessDataInfoList) {
            businessDataInfo.setDate(dateFormat.format(date));
            businessDataInfo.setNmrCode(documentDic.getNmrID());
            businessDataInfo.setNmrName(documentDic.getNmrName());
        }
        IFlyNursing.getInstance().setBusinessDataInfoList(businessDataInfoList);
        Intent intent = new Intent(this,SaveDataActivity.class);
        startActivity(intent);
//        String saveData = new Gson().toJson(businessDataInfoList);
//        String formData = new Gson().toJson(formCheckList);
//        Log.d("保存", formData + "    " + saveData);
//        IFlyNursing.getInstance().getNursingListener().onDataSavedListener(formData + "/n" + saveData);
//        finish();
//        //保存数据
//        StringBuffer bds = new StringBuffer();
//        for(FormCheck formCheck : formCheckList){
//        	bds.append(","+formCheck.getBldm());
//        }
//        iflyAction.saveData(saveData ,bds);
    }


    /**
     * 初始化数据
     */
    private void initData() {
    	//保存标准时间点
//    	String timestr = GlobalCache.getCache().getConfigByCode("TZ017", "");
//        IFlyNursing.getInstance().saveTimeInfo(timestr);
        //获取病人列表
//        String patientStr = getPaints();

        typeList = Arrays.asList(getResources().getStringArray(R.array.type));
        selectedType = typeList.get(0);
        String patientStr = "[{\"age\":\"30\",\"cwdm\":\"1\",\"hzxm\":\"黄旭珍\",\"patid\":\"8184\",\"sex\":\"女\",\"syxh\":\"8918\",\"yexh\":\"0\"},{\"age\":\"28\",\"cwdm\":\"2\",\"hzxm\":\"叶胜斌\",\"patid\":\"5053\",\"sex\":\"男\",\"syxh\":\"5583\",\"yexh\":\"0\"},{\"age\":\"28\",\"cwdm\":\"3\",\"hzxm\":\"陈素荣\",\"patid\":\"5041\",\"sex\":\"女\",\"syxh\":\"5572\",\"yexh\":\"0\"},{\"age\":\"26\",\"cwdm\":\"4\",\"hzxm\":\"孙元生\",\"patid\":\"6059\",\"sex\":\"男\",\"syxh\":\"6586\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"5\",\"hzxm\":\"李金贵\",\"patid\":\"5436\",\"sex\":\"女\",\"syxh\":\"5952\",\"yexh\":\"0\"},{\"age\":\"60\",\"cwdm\":\"6\",\"hzxm\":\"陶华荣\",\"patid\":\"5045\",\"sex\":\"女\",\"syxh\":\"5575\",\"yexh\":\"0\"},{\"age\":\"28\",\"cwdm\":\"7\",\"hzxm\":\"李中桥\",\"patid\":\"5044\",\"sex\":\"男\",\"syxh\":\"5574\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"8\",\"hzxm\":\"郭平安\",\"patid\":\"5418\",\"sex\":\"女\",\"syxh\":\"5935\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"9\",\"hzxm\":\"熊金明\",\"patid\":\"5437\",\"sex\":\"女\",\"syxh\":\"5953\",\"yexh\":\"0\"},{\"age\":\"26\",\"cwdm\":\"10\",\"hzxm\":\"胡建高\",\"patid\":\"5515\",\"sex\":\"男\",\"syxh\":\"6028\",\"yexh\":\"0\"},{\"age\":\"64\",\"cwdm\":\"11\",\"hzxm\":\"宋德厚\",\"patid\":\"5562\",\"sex\":\"男\",\"syxh\":\"6075\",\"yexh\":\"0\"},{\"age\":\"36\",\"cwdm\":\"12\",\"hzxm\":\"杨金辉\",\"patid\":\"8197\",\"sex\":\"女\",\"syxh\":\"8932\",\"yexh\":\"0\"},{\"age\":\"33\",\"cwdm\":\"13\",\"hzxm\":\"吴春林\",\"patid\":\"8186\",\"sex\":\"男\",\"syxh\":\"8920\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"14\",\"hzxm\":\"徐庆兰\",\"patid\":\"5587\",\"sex\":\"女\",\"syxh\":\"6100\",\"yexh\":\"0\"},{\"age\":\"23\",\"cwdm\":\"15\",\"hzxm\":\"丁伟\",\"patid\":\"8196\",\"sex\":\"男\",\"syxh\":\"8931\",\"yexh\":\"0\"},{\"age\":\"30\",\"cwdm\":\"16\",\"hzxm\":\"张世荣\",\"patid\":\"8191\",\"sex\":\"男\",\"syxh\":\"8925\",\"yexh\":\"0\"},{\"age\":\"33\",\"cwdm\":\"17\",\"hzxm\":\"张玉英\",\"patid\":\"5648\",\"sex\":\"女\",\"syxh\":\"6160\",\"yexh\":\"0\"},{\"age\":\"23\",\"cwdm\":\"18\",\"hzxm\":\"甘芳兰\",\"patid\":\"6664\",\"sex\":\"女\",\"syxh\":\"7190\",\"yexh\":\"0\"},{\"age\":\"26\",\"cwdm\":\"19\",\"hzxm\":\"李友翠\",\"patid\":\"6659\",\"sex\":\"女\",\"syxh\":\"7185\",\"yexh\":\"0\"},{\"age\":\"37\",\"cwdm\":\"20\",\"hzxm\":\"王婷荣\",\"patid\":\"6656\",\"sex\":\"女\",\"syxh\":\"7182\",\"yexh\":\"0\"},{\"age\":\"32\",\"cwdm\":\"21\",\"hzxm\":\"孙园园\",\"patid\":\"8278\",\"sex\":\"女\",\"syxh\":\"9005\",\"yexh\":\"0\"},{\"age\":\"47\",\"cwdm\":\"22\",\"hzxm\":\"王容\",\"patid\":\"8274\",\"sex\":\"女\",\"syxh\":\"9001\",\"yexh\":\"0\"},{\"age\":\"32\",\"cwdm\":\"23\",\"hzxm\":\"董广宇\",\"patid\":\"8942\",\"sex\":\"男\",\"syxh\":\"9753\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"24\",\"hzxm\":\"蔡丰英\",\"patid\":\"6657\",\"sex\":\"女\",\"syxh\":\"7183\",\"yexh\":\"0\"},{\"age\":\"23\",\"cwdm\":\"25\",\"hzxm\":\"胡玉清\",\"patid\":\"6658\",\"sex\":\"女\",\"syxh\":\"7184\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"26\",\"hzxm\":\"李丽娟\",\"patid\":\"6657\",\"sex\":\"女\",\"syxh\":\"7183\",\"yexh\":\"0\"},{\"age\":\"37\",\"cwdm\":\"27\",\"hzxm\":\"杨崔欣\",\"patid\":\"6656\",\"sex\":\"女\",\"syxh\":\"7182\",\"yexh\":\"0\"},{\"age\":\"23\",\"cwdm\":\"28\",\"hzxm\":\"王秋香\",\"patid\":\"6663\",\"sex\":\"女\",\"syxh\":\"7189\",\"yexh\":\"0\"},{\"age\":\"23\",\"cwdm\":\"29\",\"hzxm\":\"陈永年\",\"patid\":\"6663\",\"sex\":\"男\",\"syxh\":\"7189\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"30\",\"hzxm\":\"王吉莲\",\"patid\":\"5067\",\"sex\":\"女\",\"syxh\":\"5597\",\"yexh\":\"0\"},{\"age\":\"24\",\"cwdm\":\"31\",\"hzxm\":\"熊建江\",\"patid\":\"5420\",\"sex\":\"男\",\"syxh\":\"5937\",\"yexh\":\"0\"}]";
        List<PatientInfo> patientInfos = new Gson().fromJson(patientStr, new TypeToken<List<PatientInfo>>() {
        }.getType());
        patientInfoDao = new PatientInfoDao(StandingRecordActivity.this);
        patientInfoDao.deletePatientInfo();
        patientInfoDao.saveOrUpdatePaintInfoList(patientInfos);
//        Log.d("PATIENT", patientStr);
        businessDataInfoList = new ArrayList<BusinessDataInfo>();
//        businessDataInfoList.add(businessDataInfo);
        recordAdapter = new RecordAdapter(StandingRecordActivity.this, businessDataInfoList);
        listView.setAdapter(recordAdapter);
        if (businessDataInfoList.size() > 0) {
            patPosition = businessDataInfoList.size() - 1;
        }

    }

    /**获取病人列表*/
//    private String getPaints() {
//		List<CwCardInfo> paints = GlobalCache.getCache().getPatients();
//        List<IFLYPaint> iflyPaints = new ArrayList<IFLYPaint>();
//        for(CwCardInfo paint : paints){
//        	if (paint.isEmptyBed() || "2".equals(paint.getBrzt()) || "4".equals(paint.getBrzt())) {  //空床，出区病人
//        		continue;
//			}
//        	IFLYPaint iPaint = new IFLYPaint();
//        	iPaint.setAge( paint.getAge() );
//        	iPaint.setCwdm( paint.getCwdm() );
//        	iPaint.setHzxm( paint.getHzxm() );
//        	iPaint.setPatid( paint.getPatid() );
//        	iPaint.setSex( paint.getSex() );
//        	iPaint.setSyxh( paint.getSyxh() );
//        	iPaint.setYexh( paint.getYexh() );
//        	iflyPaints.add(iPaint);
//        }
//        String patientStr = new Gson().toJson(iflyPaints);
//		return patientStr;
//	}

    private void speech() {
        if (mIat.isListening()) {// 开始前检查状态
            mIat.stopListening();
//            Toast.makeText(RecordActivity.this,"停止录音",Toast.LENGTH_SHORT).show();
            if (isLongClick) {
                mHandler.removeCallbacks(mRunnable);
                miniWaveSurface.stopListening();
                hideTopSpeechLayout();
            } else {
                glWaveFormView.reset();
                glWaveFormView.stopListening();
            }
        } else {
            filePath = System.currentTimeMillis() + ".wav";
            speechHelper.setSavePath(filePath);
            voicePathList.add(filePath);
            ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("听写失败");
                if (isLongClick) {
                    miniWaveSurface.reset();
                    hideTopSpeechLayout();
                } else {
                    glWaveFormView.reset();
                }
            } else {

            }
        }

    }


    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//            showTip("开始说话");
            if (isLongClick) {
                miniWaveSurface.start();
            } else {
                glWaveFormView.start();
            }
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            showTip(error.getPlainDescription(false));
            if (isLongClick) {
                miniWaveSurface.reset();
                mHandler.removeCallbacks(mRunnable);
            } else {
                glWaveFormView.reset();
                glWaveFormView.setLongClickable(true);
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//            showTip("结束说话");
            mIat.startListening(mRecognizerListener);
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d("result", results.getResultString());
            printResult(results);
            if (isLast) {
                // TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
//            showTip("当前正在说话，音量大小：" + volume);
//            Log.d(TAG, "返回音频数据："+data.length);
            glWaveFormView.setVolume(volume);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };


    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        Log.d("result",text);
        ret = mTextUnderstander.understandText(text, mTextUnderstanderListener);
        if(ret != 0)
        {
//            showTip("语义理解失败");
        }
    }

    /**
     * 删除某一个患者
     * @param position
     */
    public void deletePosition(int position){
        patPosition = position - 1 >=0? position -1:0;
        if (patPosition>0){
            int height = getListViewItemHeight(patPosition);
            listView.setSelectionFromTop(position+1, height);
        }
    }



    private TextUnderstanderListener mTextUnderstanderListener = new TextUnderstanderListener() {

        @Override
        public void onResult(final UnderstanderResult result) {
            if (null != result) {
                try {

                    DataDealUtil dataDealUtil = new DataDealUtil(StandingRecordActivity.this,businessDataInfoList, patPosition,selectedType) {
                        @Override
                        public void onPatientSelected(int position) {
                            patPosition = position;
                            int height = getListViewItemHeight(patPosition);
                            listView.setSelectionFromTop(position+1, height);
//                            listView.setSelection(position);
                        }

                        @Override
                        public void onTypeSelected(String type) {
                            //切换种类
                            if (listView.getChildAt(0)!=null){
                                BaseToast.showToastNotRepeat(StandingRecordActivity.this,"请先保存现有记录单",2000);
                            }else {
                                businessDataInfoList.clear();
                                recordAdapter.updateList(businessDataInfoList);
                                selectedType = type;
                                spinner.setSelection(typeList.indexOf(type));
                            }
                        }

                        @Override
                        public void onError(int errorCode, String errorMsg) {
                            switch (errorCode){
                                case 1001:
                                    break;
                                case 1002:
                                    BaseToast.showToastNotRepeat(StandingRecordActivity.this,errorMsg,2000);
                                    break;
                            }
                        }


                        @Override
                        public void onRecordTime(String recordTime) {
                            timeText.setText(recordTime);
                        }
                    };
                    businessDataInfoList = dataDealUtil.transDataForBase(result.getResultString());
                    patPosition = dataDealUtil.getSelectPosition();
                    IFlyNursing.getInstance().setBusinessDataInfoList(businessDataInfoList);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (businessDataInfoList!=null&&businessDataInfoList.size()>0){
                    recordAdapter = new RecordAdapter(StandingRecordActivity.this, businessDataInfoList);
                    listView.setAdapter(recordAdapter);
                    setListViewSelecion();
                }

            } else {
                Log.d(TAG, "understander result:null");
                showTip("暂不支持您的说法");
            }
        }

        @Override
        public void onError(SpeechError error) {
            // 文本语义不能使用回调错误码14002，请确认您下载sdk时是否勾选语义场景和私有语义的发布
//            showTip("onError Code："	+ error.getErrorCode());

        }
    };

    /**
     * 获取listview的item的高度
     * @return
     */
    private int getListViewItemHeight(int position){
        if (recordAdapter == null) {
            return 0;
        }
        View listItem = recordAdapter.getView(position, null, listView);
        listItem.measure(0, 0);
        return listItem.getMeasuredHeight();
    }


    private void setListViewSelecion() {
//        listView.setSelection(patPosition);
        int height = getListViewItemHeight(patPosition);
        listView.setSelectionFromTop(patPosition, -height);
//        recordAdapter.setCount(patPosition);
    }


    private void showTip(String text) {
        BaseToast.showToastNotRepeat(StandingRecordActivity.this,text,2000);
    }

    /**
     * @return 得到基础时间  yyyy-MM-dd HH:mm:ss
     */
    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEAFULTFORMAT);
        return simpleDateFormat.format(new Date());
    }

    public void playVoice() {

        mediaplayerUtil.playSoundList(voicePathList, new MediaplayerUtil.MediaplayerPlayStateListener() {
            @Override
            public void startPlay() {

            }

            @Override
            public void stopPlay() {

            }
        });
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
                patPosition = count;
            } else {
                listView.setSelection(i);
                patPosition = i;
                showTip("该患者已录入，可直接录入信息");
            }


        }
    }

    /**
     * 停止所有录音
     */
    private void stopAll(){
        if (mIat.isListening()) {// 开始前检查状态
            mIat.stopListening();
//            Toast.makeText(RecordActivity.this,"停止录音",Toast.LENGTH_SHORT).show();
            if (isLongClick) {
                mHandler.removeCallbacks(mRunnable);
                miniWaveSurface.stopListening();
                hideTopSpeechLayout();
            } else {
                glWaveFormView.reset();
                glWaveFormView.stopListening();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAll();
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
