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
        String formStr = "[{\"Name\":\"体温单\",\"Bldm\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\"},{\"Name\":\"新生儿体温单\",\"Bldm\":\"1DCFA477-6D8D-4C8B-B8CE-F846BBC615D4\"},{\"Name\":\"术前护理评估单\",\"Bldm\":\"333B2609-5504-4227-A520-793C3B95FD73\"},{\"Name\":\"术后护理评估单\",\"Bldm\":\"2B84147E-5824-473D-A84A-E21B166FAA42\"},{\"Name\":\"危重患者护理记录单\",\"Bldm\":\"E17CAE68-1C7F-4B78-B271-BC456787ED18\"},{\"Name\":\"血糖记录表\",\"Bldm\":\"1673A68E-3F32-4AEE-B2B3-83406699F538\"},{\"Name\":\"病人转科交接记录单\",\"Bldm\":\"E33F9E6A-DFF1-4057-B321-D73B70FB7799\"},{\"Name\":\"内科住院患者护理记录单\",\"Bldm\":\"A9B12E93-9606-4618-AC1E-EA7F22894478\"},{\"Name\":\"新入院评估单\",\"Bldm\":\"C2400BDC-B92F-489E-93A3-51E258B26F67\"},{\"Name\":\"(新)新入院评估单\",\"Bldm\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\"},{\"Name\":\"(新)产科入院评估单\",\"Bldm\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\"},{\"Name\":\"(新)儿科入院评估单\",\"Bldm\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\"},{\"Name\":\"(新)新生儿入院评估单\",\"Bldm\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\"}]";
        formCheckList = new Gson().fromJson(formStr, new TypeToken<List<FormCheck>>() {
        }.getType());
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
                CustomDialog customDialog = new CustomDialog(SaveDataActivity.this, formCheckList,0,true);
                customDialog.show();
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
