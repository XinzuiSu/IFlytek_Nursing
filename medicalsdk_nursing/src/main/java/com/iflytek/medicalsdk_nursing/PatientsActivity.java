package com.iflytek.medicalsdk_nursing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.medicalsdk_nursing.adapter.PatientListAdapter;
import com.iflytek.medicalsdk_nursing.dao.PatientInfoDao;
import com.iflytek.medicalsdk_nursing.domain.PatientInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: com.iflytek.medicalsdk_nursing
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/10/26-下午10:01,  All rights reserved
 * @Description: TODO 患者列表Activity;
 * @author: chenzhilei
 * @data: 2016/10/26 下午10:01
 * @version: V1.0
 */

public class PatientsActivity extends Activity{

    /**
     * 编辑框
     */
    private EditText editText;
    /**
     * 搜索文字按钮
     */
    private TextView searchText;
    /**
     * 患者listview
     */
    private ListView listView;

    private List<PatientInfo> patientInfos;

    private PatientInfoDao patientInfoDao;
    /**
     * 替换标记
     */
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients);
        position = getIntent().getIntExtra("POSITION",0);
        listView = (ListView) findViewById(R.id.patients_listView);
        editText = (EditText) findViewById(R.id.patients_search_edittext);
        searchText = (TextView) findViewById(R.id.patients_search_text);
        patientInfoDao = new PatientInfoDao(this);
        patientInfos = patientInfoDao.getPatientInfoList();
        listView.setAdapter(new PatientListAdapter(this,patientInfos));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("PATIENT_BEDNO",patientInfos.get(i).getHosBedNum());
                intent.putExtra("POSITION",position);
                setResult(1002,intent);
                finish();
            }
        });
        /**
         * 按下软键盘的回车键也能搜索
         */
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    //搜索病人列表
                    updatePatientList(editText.getText().toString());
                    //隐藏软键盘
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context
                            .INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(PatientsActivity.this.getCurrentFocus()
                                .getWindowToken(), 0);
                    }
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    searchText.setText("取消");
                    updatePatientList("");//刷新患者列表
                } else {
                    searchText.setText("搜索");
                }
            }
        });
        searchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isEquals(searchText.getText().toString(),"搜索")){
                    updatePatientList(editText.getText().toString());
                }else {
                    editText.clearFocus();
                }
            }
        });

    }


    public void updatePatientList(String searchText){
        if (StringUtils.isBlank(searchText)){
            patientInfos = patientInfoDao.getPatientInfoList();
        }else {
            patientInfos = patientInfoDao.getPatientInfoList(searchText);
            if (patientInfos == null){
                patientInfos = new ArrayList<>();
            }
        }
        listView.setAdapter(new PatientListAdapter(this,patientInfos));
    }
}
