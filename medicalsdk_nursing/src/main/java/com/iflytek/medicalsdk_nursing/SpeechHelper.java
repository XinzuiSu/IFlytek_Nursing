package com.iflytek.medicalsdk_nursing;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUnderstander;

/**
 * @Title: com.iflytek.medicalsdk_nursing
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/10/11-下午5:53,  All rights reserved
 * @Description: TODO 语音助手;
 * @author: chenzhilei
 * @data: 2016/10/11 下午5:53
 * @version: V1.0
 */
public class SpeechHelper {

    private SharedPreferences mSharedPreferences;

    private String TAG = "Speech";

    public static final String PREFER_NAME = "com.iflytek.setting";

    // 语义理解对象（语音到语义）。
    private SpeechUnderstander mSpeechUnderstander;

    private Context mContext;

    public SpeechHelper(Context context){
        this.mContext = context;
        mSharedPreferences = context.getSharedPreferences(PREFER_NAME, Activity.MODE_PRIVATE);
        mSpeechUnderstander = SpeechUnderstander.createUnderstander(context, mSpeechUdrInitListener);
        setParam();
    }


    /**
     * 初始化监听器（语音到语义）。
     */
    private InitListener mSpeechUdrInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "speechUnderstanderListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(mContext,"初始化失败,错误码：" + code,Toast.LENGTH_SHORT).show();
            }
        }
    };


    /**
     * 获取语义理解对象
     * @return
     */
    public SpeechUnderstander getmSpeechUnderstander() {
        return mSpeechUnderstander;
    }

    public void setParam(){
        String lang = mSharedPreferences.getString("understander_language_preference", "mandarin");
        if (lang.equals("en_us")) {
            // 设置语言
            mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "en_us");
        }else {
            // 设置语言
            mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, lang);
        }
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("understander_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("understander_vadeos_preference", "1000"));

        // 设置标点符号，默认：1（有标点）
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("understander_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/sud.wav");
    }





}
