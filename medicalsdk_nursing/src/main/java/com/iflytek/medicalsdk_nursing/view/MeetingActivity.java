package com.iflytek.medicalsdk_nursing.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.android.framework.toast.BaseToast;
import com.iflytek.android.framework.volley.Request;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.medicalsdk_nursing.R;
import com.iflytek.medicalsdk_nursing.adapter.MeetingChatAdapter;
import com.iflytek.medicalsdk_nursing.domain.CreateInfo;
import com.iflytek.medicalsdk_nursing.domain.MessageInfo;
import com.iflytek.medicalsdk_nursing.net.ChatVolleyTool;
import com.iflytek.medicalsdk_nursing.net.SoapResult1;
import com.iflytek.medicalsdk_nursing.util.ChatCustomDialog;
import com.iflytek.medicalsdk_nursing.util.GLWaveformView;
import com.iflytek.medicalsdk_nursing.util.IatSpeechHelper;
import com.iflytek.medicalsdk_nursing.util.JsonParser;
import com.iflytek.medicalsdk_nursing.util.MiniWaveSurface;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.iflytek.android.framework.volley.VolleyLog.TAG;

public class MeetingActivity extends Activity {

    /**
     * 时间基础格式化
     */
    public static final String DEAFULTFORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 是否长按语音按钮
     */
    private boolean isLongClick = false;
    /**
     * 顶部语音时长
     */
    private int topVoiceTime = 0;

    /**
     * 底部语音按钮
     */
    private GLWaveformView glWaveFormView;
    /**
     * 顶部标题布局
     */
    private RelativeLayout titleLayout;
    /**
     * 房间标题(号)
     */
    private TextView mTitleText;
    /**
     * 顶部语音效果布局
     */
    private LinearLayout topVoice;
    /**
     * 顶部语音效果时间
     */
    private TextView topVoiceTimeTextView;
    /**
     * 顶部语音效果
     */
    private MiniWaveSurface miniWaveSurface;
    /**
     * 内容liseView
     */
    private ListView mMeetingListView;
    // 函数调用返回值
    private int ret = 0;

    // 语音听写对象
    private SpeechRecognizer mIat;

    // 语义理解对象（文本到语义）。
    private TextUnderstander mTextUnderstander;

    private IatSpeechHelper speechHelper;
    private List<MessageInfo> mChatList;
    private MeetingChatAdapter mChatAdapter;
    /**
     * 返回键
     */
    private LinearLayout mBackLayout;
    /**
     * 网络请求
     */
    private ChatVolleyTool mVolleyTool;
    /**
     * 销毁房间
     */
    private LinearLayout mCloseLayout;
    /**
     * 房主id
     */
    private String mUserId;
    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        //初始化布局
        initView();
        //初始化语音按钮布局
        initGLView();
        //初始化语音
        initSpeech();
        //初始化网络请求
        initVolley();
        //初始化listView
        initListView();
        //初始化数据
        initDate();
        mHandler.postDelayed(updateRunable, 5000);

    }


    private void updateUI() {
        if (mChatList.size() > 0) {
            int id = mChatList.get(mChatList.size() - 1).getId();
            getMessageFromNet(id + "");
        }
    }

    private void initVolley() {
        mVolleyTool = new ChatVolleyTool(this) {
            @Override
            public void getRequest(int msgWhat, SoapResult1 result) throws JSONException, Exception {
                switch (msgWhat) {
                    case 1001:
                        List<MessageInfo> messageInfoList = new Gson().fromJson(result.getData(), new TypeToken<List<MessageInfo>>() {
                        }.getType());
                        if (messageInfoList.size() > 0 && messageInfoList != null) {
                            mChatList.addAll(messageInfoList);
                            mChatAdapter.update(mChatList);
                            mMeetingListView.smoothScrollToPosition(mChatList.size());
                        }
                        break;
                    case 1002:
                        int id = mChatList.get(mChatList.size() - 1).getId();
                        getMessageFromNet(id + "");
                        break;
                    case 1003:
                        //关闭房间
                        if ("SUCCESS".equals(result.getData())) {
                            finish();
                            showTip("关闭会议");
                        }
                        break;
                }
            }

            @Override
            public void onNetUnConnected() {

            }

            @Override
            public void onErrorRequest(SoapResult1 result) throws Exception {
//                if ("当前用户没有创建会议室".equals(result.getResult())) {
//                    showTip("您不是房主,无法关闭房间");
//                    return;
//                }
                if ("会议已关闭".equals(result.getResult())) {
                    showTip(result.getResult());
                    finish();
                    return;
                }
                showTip(result.getResult());
            }
        };
    }

    private void initDate() {
    }

    /**
     * 初始化listview
     */
    private void initListView() {
        mChatList = new ArrayList<>();
        mChatAdapter = new MeetingChatAdapter(MeetingActivity.this, mChatList);
        mMeetingListView.setAdapter(mChatAdapter);
        getMessageFromNet("");
    }


    /**
     * 初始化
     */
    private void initView() {
        glWaveFormView = (GLWaveformView) findViewById(R.id.meeting_voice_image);
        titleLayout = (RelativeLayout) findViewById(R.id.meeting_title_layout);
        mTitleText = (TextView) findViewById(R.id.meeting_room_title);
        topVoice = (LinearLayout) findViewById(R.id.ll_top_voice);
        topVoiceTimeTextView = (TextView) findViewById(R.id.tv_top_voice_time);
        miniWaveSurface = (MiniWaveSurface) findViewById(R.id.mws_speech);
        mMeetingListView = (ListView) findViewById(R.id.meeting_listView);
        mBackLayout = (LinearLayout) findViewById(R.id.meeting_back);
        mCloseLayout = (LinearLayout) findViewById(R.id.close_meeting_room);
        mCloseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChatCustomDialog(MeetingActivity.this, "确定关闭此房间?", "确定", "取消") {
                    @Override
                    public void onSingleClick() {

                    }

                    @Override
                    public void onDoubleRightClick() {
                        stopAll();
                        //TODO 销毁房间
                        closeRoom();
                        dismiss();
                    }

                    @Override
                    public void onDoubleLeftClick() {
                        dismiss();
                    }
                }.show();
            }
        });
        mBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAll();
                onBackPressed();
            }
        });

        String create_room = getIntent().getStringExtra("CREATE_INFO");
        String chatCode = getIntent().getStringExtra("CHAT_CODE");
        String isUserOwner = getIntent().getStringExtra("IF_USER_OWNER");
        mUserId = getIntent().getStringExtra("CREATE_USER_ID");
        mUserName = getIntent().getStringExtra("CREATE_USER_NAME");
        CreateInfo createInfo = new Gson().fromJson(create_room, CreateInfo.class);
        if (getIntent().hasExtra("CREATE_INFO")) {
            mTitleText.setText(createInfo.getChatCode() + "");
        } else {
            mTitleText.setText(chatCode);
            if ("1".equals(isUserOwner)) {
                mCloseLayout.setVisibility(View.VISIBLE);
            } else {
                mCloseLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 初始化语音输入
     */
    private void initSpeech() {
        mTextUnderstander = TextUnderstander.createTextUnderstander(MeetingActivity.this, mTextUdrInitListener);
        speechHelper = new IatSpeechHelper(MeetingActivity.this);
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
                showTip("初始化失败,错误码：" + code);
            }
        }
    };


    private Handler mHandler = new Handler();
    Runnable updateRunable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            mHandler.postDelayed(updateRunable, 5000);
        }
    };
    /**
     * 顶部语音计时显示
     */
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            topVoiceTime++;
            if (topVoiceTime < 10) {
                topVoiceTimeTextView.setText("0:0" + topVoiceTime);
            } else if (topVoiceTime >= 10 && topVoiceTime < 60) {
                topVoiceTimeTextView.setText("0:" + topVoiceTime);
            } else if (topVoiceTime >= 60 && topVoiceTime < 70) {
                topVoiceTimeTextView.setText("1:0" + (topVoiceTime - 60));
            } else if (topVoiceTime >= 70 && topVoiceTime < 120) {
                topVoiceTimeTextView.setText("1:" + (topVoiceTime - 60));
            } else if (topVoiceTime >= 120 && topVoiceTime < 130) {
                topVoiceTimeTextView.setText("2:0" + (topVoiceTime - 120));
            } else if (topVoiceTime >= 130) {
                topVoiceTimeTextView.setText("2:" + (topVoiceTime - 120));
            }
            mHandler.postDelayed(mRunnable, 1000);
        }
    };


    /**
     * 初始化GLView
     */
    private void initGLView() {

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
        titleLayout.setVisibility(View.INVISIBLE);
        topVoice.setVisibility(View.VISIBLE);
    }


    /**
     * 隐藏顶部语音动画
     */
    private void hideTopSpeechLayout() {
        titleLayout.setVisibility(View.VISIBLE);
        topVoice.setVisibility(View.GONE);
        glWaveFormView.reset();
        isLongClick = false;
    }

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
//            filePath = System.currentTimeMillis() + ".wav";
//            speechHelper.setSavePath(filePath);
//            voicePathList.add(filePath);
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

    //听写的单条文字
    private StringBuffer voiceText;
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
            voiceText = new StringBuffer();
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
            printResult(results, isLast);
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

    /**
     * 打印语音文本
     *
     * @param results
     * @param isLast  是否是最后一句话
     */
    private void printResult(RecognizerResult results, boolean isLast) {
        String text = JsonParser.parseIatResult(results.getResultString());
        Log.d("result", text);
        voiceText.append(text);
        voiceText.append(",");
        //TODO 添加数据进listView
        if (isLast) {
            voiceText.replace(voiceText.length() - 2, voiceText.length(), "。");
//            mChatList.add(new ItemtInfo("小风", getDate(), voiceText.toString(), 0));
//            mChatAdapter.notifyDataSetChanged();
//            mMeetingListView.setSelection(mChatList.size());
            //提交数据到服务器
            sendMessageToNet(voiceText.toString());

        }
    }


    private void showTip(String text) {
        BaseToast.showToastNotRepeat(MeetingActivity.this, text, 2000);
    }

    /**
     * @return 得到基础时间  yyyy-MM-dd HH:mm:ss
     */
    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEAFULTFORMAT);
        return simpleDateFormat.format(new Date());
    }

    /**
     * 停止所有录音
     */
    private void stopAll() {
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
    protected void onDestroy() {
        mHandler.removeCallbacks(updateRunable);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //离开房间
        leaveRoom();
        super.onBackPressed();
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

//
//    private void init() {
//        String serverUrl = "http://192.168.58.32:18080/ChatServer/rest/chat/service";
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("methodName","creatRoom");
//            jsonObject.put("userId","xfsu9");
//            jsonObject.put("userName","苏笑风");
//            jsonObject.put("targetId","12345");
//            jsonObject.put("targetName","患者");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        mVolleyTool.sendJsonRequest(1001, false, jsonObject.toString(), Request.Method.POST, serverUrl);
//    }

    /**
     * 获取消息
     *
     * @param id
     */
    private void getMessageFromNet(String id) {
        String serverUrl = "http://192.168.58.32:18080/ChatServer/rest/chat/service";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("methodName", "findMessages");

            jsonObject.put("userId", mUserId);
            jsonObject.put("messageId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mVolleyTool.sendJsonRequest(1001, false, jsonObject.toString(), Request.Method.POST, serverUrl);

    }

    /**
     * 发送消息
     *
     * @param message
     */
    private void sendMessageToNet(String message) {
        String serverUrl = "http://192.168.58.32:18080/ChatServer/rest/chat/service";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("methodName", "sendMessage");
            jsonObject.put("userId", mUserId);
            jsonObject.put("userName", mUserName);
            jsonObject.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mVolleyTool.sendJsonRequest(1002, false, jsonObject.toString(), Request.Method.POST, serverUrl);

    }

    /**
     * 关闭房间
     */
    private void closeRoom() {
        String serverUrl = "http://192.168.58.32:18080/ChatServer/rest/chat/service";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("methodName", "closeRoom");
            jsonObject.put("userId", mUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mVolleyTool.sendJsonRequest(1003, false, jsonObject.toString(), Request.Method.POST, serverUrl);

    }

    /**
     * 离开房间
     */
    private void leaveRoom() {
        String serverUrl = "http://192.168.58.32:18080/ChatServer/rest/chat/service";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("methodName", "leaveRoom");
            jsonObject.put("userId", mUserId);
            jsonObject.put("userName", mUserName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mVolleyTool.sendJsonRequest(1004, false, jsonObject.toString(), Request.Method.POST, serverUrl);

    }


}
