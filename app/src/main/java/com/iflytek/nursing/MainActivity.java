package com.iflytek.nursing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iflytek.android.framework.toast.BaseToast;
import com.iflytek.android.framework.volley.Request;
import com.iflytek.medicalsdk_nursing.base.IFlyNursing;
import com.iflytek.medicalsdk_nursing.domain.CreateInfo;
import com.iflytek.medicalsdk_nursing.net.ChatVolleyTool;
import com.iflytek.medicalsdk_nursing.net.SoapResult1;
import com.iflytek.medicalsdk_nursing.util.NursingListener;
import com.iflytek.medicalsdk_nursing.util.NursingSpeecher;
import com.iflytek.medicalsdk_nursing.view.MeetingActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button recordButton;
    private NursingSpeecher nursingSpeecher;

    private TextView resultText;
    private Button mNewMeeting;
    private Button mAddMeeting;

    private static String USERID = "sxf36";
    private static String USERNAME = "苏笑风";

    /**
     * 网络请求
     */
    private ChatVolleyTool mVolleyTool;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultText = (TextView) findViewById(R.id.main_text);

        initVolley();
        String patientStr = "";
        //传递患者数据
        IFlyNursing.getInstance().savePatientInfo(patientStr);

        String documentStr = "";
        IFlyNursing.getInstance().saveDocumentInfo(documentStr);

        String documentDetailStr = "";
        IFlyNursing.getInstance().saveDocumentDetailInfo(documentDetailStr);

        String optionStr = "";
        IFlyNursing.getInstance().saveOptionInfo(optionStr);


        nursingSpeecher = new NursingSpeecher(this);
        nursingSpeecher.setNursingListener(new NursingListener() {
            @Override
            public void onStartListener(boolean success) {

            }

            @Override
            public void onDataSavedListener(String result) {
                resultText.setText(result);
            }
        });
        recordButton = (Button) findViewById(R.id.main_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nursingSpeecher.startRecord();
            }
        });

        mNewMeeting = (Button) findViewById(R.id.bt_new_room);
        mAddMeeting = (Button) findViewById(R.id.bt_add_room);

        mNewMeeting.setOnClickListener(this);
        mAddMeeting.setOnClickListener(this);
    }

    private void initVolley() {
        mVolleyTool = new ChatVolleyTool(this) {
            @Override
            public void getRequest(int msgWhat, SoapResult1 result) throws JSONException, Exception {
                switch (msgWhat) {
                    case 1001:
                        CreateInfo createInfo = new Gson().fromJson(result.getData(), CreateInfo.class);
                        Intent newIntent = new Intent(MainActivity.this, MeetingActivity.class);
                        newIntent.putExtra("CREATE_INFO",result.getData());
                        newIntent.putExtra("CREATE_USER_ID",USERID);
                        newIntent.putExtra("CREATE_USER_NAME",USERNAME);
                        startActivity(newIntent);
                        break;
                    case 1002:
                        CreateInfo addInfo = new Gson().fromJson(result.getData(), CreateInfo.class);
                        String createrChatId = addInfo.getCreater();
                        Intent addIntent = new Intent(MainActivity.this, MeetingActivity.class);
                        addIntent.putExtra("CHAT_CODE",mEditText.getText().toString());
                        addIntent.putExtra("CREATE_USER_ID",USERID);
                        addIntent.putExtra("CREATE_USER_NAME",USERNAME);
                        if (createrChatId.equals(USERID)) {
                            addIntent.putExtra("IF_USER_OWNER", "1");
                        } else {
                            addIntent.putExtra("IF_USER_OWNER", "2");
                        }
                        startActivity(addIntent);
                        break;
                }
            }

            @Override
            public void onNetUnConnected() {

            }

            @Override
            public void onErrorRequest(SoapResult1 result) throws Exception {
                showTip(result.getResult());
                if ("会议口令错误，无法获取会议信息".equals(result.getResult())) {
                    return;
                }
                //TODO 初始化
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_new_room:
                createRoom();
                break;
            case R.id.bt_add_room:
                mEditText = new EditText(this);
                new AlertDialog.Builder(this)
                        .setTitle("请输入会议口令")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(mEditText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                joinRoom(mEditText.getText().toString());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                break;
        }
    }

    /**
     * 加入房间
     * @param roomCode
     */
    private void joinRoom(String roomCode) {
        String serverUrl = "http://192.168.58.32:18080/ChatServer/rest/chat/service";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("methodName", "joinRoom");
            jsonObject.put("userId", USERID);
            jsonObject.put("userName", USERNAME);
            jsonObject.put("roomCode", roomCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mVolleyTool.sendJsonRequest(1002, false, jsonObject.toString(), Request.Method.POST, serverUrl);

    }

    /**
     * 创建房间
     */
    private void createRoom() {

        String serverUrl = "http://192.168.58.32:18080/ChatServer/rest/chat/service";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("methodName", "creatRoom");
            jsonObject.put("userId", USERID);
            jsonObject.put("userName", USERNAME);
            jsonObject.put("targetId", "12345");
            jsonObject.put("targetName", "患者");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mVolleyTool.sendJsonRequest(1001, false, jsonObject.toString(), Request.Method.POST, serverUrl);

    }

    private void showTip(String text) {
        BaseToast.showToastNotRepeat(MainActivity.this, text, 2000);
    }
}

