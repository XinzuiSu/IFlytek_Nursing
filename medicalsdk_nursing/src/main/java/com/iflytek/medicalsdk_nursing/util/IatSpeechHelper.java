package com.iflytek.medicalsdk_nursing.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;

/**
 * @Title: com.iflytek.medicalsdk_nursing
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/10/11-下午5:53,  All rights reserved
 * @Description: TODO 语音听写助手;
 * @author: chenzhilei
 * @data: 2016/10/11 下午5:53
 * @version: V1.0
 */
public class IatSpeechHelper {

    private SharedPreferences mSharedPreferences;

    private String TAG = "Speech";

    public static final String PREFER_NAME = "com.iflytek.setting";

    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    // 语音听写对象
    private SpeechRecognizer mIat;

    private Context mContext;

    public IatSpeechHelper(Context context){
        this.mContext = context;
        mSharedPreferences = context.getSharedPreferences(PREFER_NAME, Activity.MODE_PRIVATE);
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
        setParam();
    }


    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(mContext,"初始化失败,错误码：" + code,Toast.LENGTH_SHORT).show();
            }
        }
    };


    /**
     * 获取语义理解对象
     * @return
     */
    public SpeechRecognizer getmIat() {
        return mIat;
    }


    /**
     * 设置参数
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIat.setParameter(SpeechConstant.DOMAIN, "jdsearch");
            mIat.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "4000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }


//    /**
//     * 参数设置
//     *
//     * @return
//     */
//    public void setParam() {
//        String lang = mSharedPreferences.getString("understander_language_preference", "mandarin");
//        if (lang.equals("en_us")) {
//            // 设置语言
//            mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "en_us");
//        } else {
//            // 设置语言
//            mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//            // 设置语言区域
//            mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, lang);
//            mSpeechUnderstander.setParameter(SpeechConstant.DOMAIN, "jdsearch");
//            mSpeechUnderstander.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
//
//        }
//        //mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
//        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
//        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString
//                ("understander_vadbos_preference", "4000"));
//
//        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
//        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString
//                ("understander_vadeos_preference", "2000"));
//
//        // 设置标点符号，默认：1（有标点）
//        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString
//                ("understander_punc_preference", "1"));
//
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() +
//                "/msc/sud.wav");
//
//
//    }

    /**
     * 设置语义存储目录
     * @param filepath
     */
    public void setSavePath(String filepath){
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() +
                "/iflytek/"+filepath);
    }





}
