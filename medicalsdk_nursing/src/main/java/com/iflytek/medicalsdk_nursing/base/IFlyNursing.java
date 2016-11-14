package com.iflytek.medicalsdk_nursing.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.android.framework.db.DbHelper;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.medicalsdk_nursing.dao.DocumentDetailDicDao;
import com.iflytek.medicalsdk_nursing.dao.DocumentDicDao;
import com.iflytek.medicalsdk_nursing.dao.OptionDicDao;
import com.iflytek.medicalsdk_nursing.dao.PatientInfoDao;
import com.iflytek.medicalsdk_nursing.domain.DocumentDetailDic;
import com.iflytek.medicalsdk_nursing.domain.DocumentDic;
import com.iflytek.medicalsdk_nursing.domain.OptionDic;
import com.iflytek.medicalsdk_nursing.domain.PatientInfo;
import com.iflytek.medicalsdk_nursing.domain.UserInfo;
import com.iflytek.medicalsdk_nursing.util.NursingListener;

import java.util.List;

/**
 * @Title: com.iflytek.medicalsdk_nursing
 * @Copyright: IFlytek Co., Ltd. Copyright 16/10/8-上午8:53,  All rights reserved
 * @Description: TODO 讯飞护理SDK;
 * @author: chenzhilei
 * @data: 16/10/8 上午8:53
 * @version: V1.0
 */
public class IFlyNursing {


    private AudioManager mAudioManager;

    private DbHelper dbHelper;

    private static IFlyNursing instance = null;

    private Context mContext;

    private Gson gson;

    private NursingListener nursingListener;

    private String[] times;

    private String formStr;

    public NursingListener getNursingListener() {
        return nursingListener;
    }

    public void setNursingListener(NursingListener nursingListener) {
        this.nursingListener = nursingListener;
    }

    /**
     * 单例化初始化sdk
     *
     * @return
     */
    public static IFlyNursing getInstance() {
        if (instance == null) {
            instance = new IFlyNursing();
        }
        return instance;
    }

    /**
     * 初始化sdk
     *
     * @param context
     * @param appID
     */
    public void initSDK(Context context, String appID) {
        this.mContext = context;
        init(context);
        saveTimeInfo("");
    }


    /**
     * 存储用户信息
     *
     * @param userInfoStr
     */
    public void saveUserInfo(String userInfoStr) {
        gson = new Gson();
        UserInfo userInfo = gson.fromJson(userInfoStr, UserInfo.class);
    }

    public String[] getTimes() {
        return times;
    }

    /**
     * 存储患者信息
     *
     * @param patientStr
     */
    public void savePatientInfo(String patientStr) {
        PatientInfoDao patientInfoDao = new PatientInfoDao(mContext);
        gson = new Gson();
        List<PatientInfo> patientInfoList = gson.fromJson(patientStr, new TypeToken<List<PatientInfo>>() {
        }.getType());
        patientInfoDao.saveOrUpdatePaintInfoList(patientInfoList);
    }

    public String getFormStr() {
        return formStr;
    }

    /**
     * 存储form表单数据
     *
     * @param formStr
     */
    public void saveFormInfo(String formStr) {
        this.formStr = formStr;
    }

    /**
     * 保存文书信息
     *
     * @param documentStr
     */
    public void saveDocumentInfo(String documentStr) {

        DocumentDicDao documentDicDao = new DocumentDicDao(mContext);
        gson = new Gson();
        List<DocumentDic> documentDicList = gson.fromJson(documentStr, new TypeToken<List<DocumentDic>>() {
        }.getType());
        documentDicDao.saveOrUpdateDocumentDicList(documentDicList);
    }

    /**
     * 保存文书详细信息
     *
     * @param documentDetailStr
     */
    public void saveDocumentDetailInfo(String documentDetailStr) {
        DocumentDetailDicDao documentDetailDicDao = new DocumentDetailDicDao(mContext);
        gson = new Gson();
        List<DocumentDetailDic> documentDetailDicList = gson.fromJson(documentDetailStr, new TypeToken<List<DocumentDetailDic>>() {
        }.getType());
        documentDetailDicDao.saveOrUpdateDocumentDetailDicList(documentDetailDicList);
    }

    public void saveTimeInfo(String timeStr) {
        timeStr = "3,7,11,15,19,23";
        times = timeStr.split(",");
    }

    /**
     * 保存选项信息
     *
     * @param optionStr
     */
    public void saveOptionInfo(String optionStr) {
        OptionDicDao optionDicDao = new OptionDicDao(mContext);
        gson = new Gson();
        List<OptionDic> optionDics = gson.fromJson(optionStr, new TypeToken<List<OptionDic>>() {
        }.getType());
        optionDicDao.saveOrUpdateOptionDicList(optionDics);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        openBluetooth(context);
//        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=58008545" + ",server_url= http://bj.voicecloud.cn/index.htm");

        SpeechUtility.createUtility(context, SpeechConstant.APPID + "=58008545" + ",server_url= http://116.213.69.199:1028/index.htm");
        initDataBase(context);
    }


    /**
     * 初始化数据库
     */
    private void initDataBase(Context context) {
        dbHelper = new DbHelper(context);
        dbHelper.init("IFLY_NURSING", 1);
    }


    public DbHelper getDbHelper() {
        return dbHelper;
    }

    /**
     * 开启蓝牙连接
     */
    private void openBluetooth(Context context) {

        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (!mAudioManager.isBluetoothScoAvailableOffCall()) {
            Log.d("BluetoothTest", "系统不支持蓝牙录音&quot");
            return;
        }
        //蓝牙录音的关键，启动SCO连接，耳机话筒才起作用
        mAudioManager.startBluetoothSco();
        //蓝牙SCO连接建立需要时间，连接建立后会发出ACTION_SCO_AUDIO_STATE_CHANGED消息，通过接收该消息而进入后续逻辑。
        //也有可能此时SCO已经建立，则不会收到上述消息，可以startBluetoothSco()前先stopBluetoothSco()
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
                    mAudioManager.setBluetoothScoOn(true);  //打开SCO
                    context.unregisterReceiver(this);  //别遗漏
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


}
