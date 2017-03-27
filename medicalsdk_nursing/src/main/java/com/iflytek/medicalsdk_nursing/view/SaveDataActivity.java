package com.iflytek.medicalsdk_nursing.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.android.framework.toast.BaseToast;
import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.medicalsdk_nursing.R;
import com.iflytek.medicalsdk_nursing.adapter.SaveDataExpandListAdapter;
import com.iflytek.medicalsdk_nursing.base.IFlyNursing;
import com.iflytek.medicalsdk_nursing.domain.BusinessDataInfo;
import com.iflytek.medicalsdk_nursing.domain.FormCheck;
import com.iflytek.medicalsdk_nursing.util.CustomDialog;

import java.util.List;

/**
 * @Title: com.iflytek.medicalsdk_nursing.view
 * @Copyright: IFlytek Co., Ltd. Copyright 2017/1/9-下午2:06,  All rights reserved
 * @Description: TODO 保存数据界面;
 * @author: chenzhilei
 * @data: 2017/1/9 下午2:06
 * @version: V1.0
 */

public class SaveDataActivity extends Activity {
    /**
     * 时间
     */
    private TextView timeText;
    /**
     * 文书类型
     */
    private TextView typeText;
    /**
     * 全部设置
     */
    private TextView settingAllText;

    private ExpandableListView expandableListView;

    private List<BusinessDataInfo> businessDataInfoList;

    private List<FormCheck> formCheckList;

    private SaveDataExpandListAdapter adapter;

    private LinearLayout sureLayout,backLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_data);
        businessDataInfoList = IFlyNursing.getInstance().getBusinessDataInfoList();

        initView();
    }

    private void initView() {
        timeText = (TextView) findViewById(R.id.record_save_type_time);
        typeText = (TextView) findViewById(R.id.record_save_type_text);
        settingAllText = (TextView) findViewById(R.id.record_save_setting_all);
        sureLayout = (LinearLayout) findViewById(R.id.record_save_sure);
        backLayout = (LinearLayout) findViewById(R.id.record_save_back);
        expandableListView = (ExpandableListView) findViewById(R.id.record_save_expandableListView);
        adapter = new SaveDataExpandListAdapter(this,businessDataInfoList);
        expandableListView.setAdapter(adapter);
        timeText.setText(businessDataInfoList.get(0).getDate());
        typeText.setText(businessDataInfoList.get(0).getNmrName());
        settingAllText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String formStr = IFlyNursing.getInstance().getFormStr();
                formCheckList = new Gson().fromJson(formStr, new TypeToken<List<FormCheck>>() {
                }.getType());
                if(null!=formCheckList){
                    CustomDialog customDialog = new CustomDialog(SaveDataActivity.this, formCheckList,0,true);
                    customDialog.show();
                }else{
                    BaseToast.showToastNotRepeat(SaveDataActivity.this, "同步表单数据不存在",2000);
                }

            }
        });
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCLFS();
                IFlyNursing.getInstance().setBusinessDataInfoList(businessDataInfoList);
                finish();
            }
        });
        sureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //具体保存方法
                //。。。。具体卫宁构造数据
                String saveStr = new Gson().toJson(businessDataInfoList);
                Log.d("saveStr",saveStr);
            }
        });
    }

    @Override
    public void onBackPressed() {
        removeCLFS();
        super.onBackPressed();
    }

    /**
     * 去除体温测量方式
     * @return
     */
    private void removeCLFS(){
        for (BusinessDataInfo businessDataInfo:businessDataInfoList){
            if (StringUtils.isEquals(businessDataInfo.getNmrName(),"体温单")){
                int count = -1;
                for (int i = 0;i<businessDataInfo.getWsDataList().size();i++){
                    if (StringUtils.isEquals(businessDataInfo.getWsDataList().get(i).getName(),"体温测量方式")){
                        count = i;
                    }
                }
                if (count!= -1){
                    businessDataInfo.getWsDataList().remove(count);
                }
            }
        }
    }

    /**
     * 设置同步表单，根据标记
     * @param selectForms
     * @param position
     */
    public void setFormByPositon(List<FormCheck> selectForms,int position){
        businessDataInfoList.get(position).setFormList(selectForms);
        adapter.updateList(businessDataInfoList);
    }

    /**
     * 设置同步表单，所有的
     * @param selectForms
     */
    public void setFormAll(List<FormCheck> selectForms){
        for (BusinessDataInfo businessDataInfo:businessDataInfoList){
            businessDataInfo.setFormList(selectForms);
        }
        adapter.updateList(businessDataInfoList);
    }
}
