package com.iflytek.medicalsdk_nursing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.medicalsdk_nursing.R;
import com.iflytek.medicalsdk_nursing.domain.BusinessDataInfo;
import com.iflytek.medicalsdk_nursing.domain.FormCheck;
import com.iflytek.medicalsdk_nursing.domain.WSData;
import com.iflytek.medicalsdk_nursing.util.CustomDialog;

import java.util.List;

/**
 * @Title: com.iflytek.medicalsdk_nursing.adapter
 * @Copyright: IFlytek Co., Ltd. Copyright 2017/1/9-下午3:30,  All rights reserved
 * @Description: TODO 保存数据的adapter;
 * @author: chenzhilei
 * @data: 2017/1/9 下午3:30
 * @version: V1.0
 */

public class SaveDataExpandListAdapter extends BaseExpandableListAdapter {

    private List<BusinessDataInfo> businessDataInfoList;

    private Context mContext;

    public SaveDataExpandListAdapter(Context context, List<BusinessDataInfo> businessDataInfos){
        this.businessDataInfoList = businessDataInfos;
        this.mContext = context;
    }

    @Override
    public int getGroupCount() {
        return businessDataInfoList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return businessDataInfoList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return businessDataInfoList.get(i).getWsDataList();
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
        GroupViewHolder groupViewHolder;
        if (view ==null){
            groupViewHolder = new GroupViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_save_group_item,null);
            groupViewHolder.bedNum = (TextView) view.findViewById(R.id.save_group_bedNum);
            groupViewHolder.name = (TextView) view.findViewById(R.id.save_group_name);
            groupViewHolder.sex = (TextView) view.findViewById(R.id.save_group_sex);
            groupViewHolder.age = (TextView) view.findViewById(R.id.save_group_age);
            groupViewHolder.formText = (TextView) view.findViewById(R.id.save_group_formText);
            groupViewHolder.dataText = (TextView) view.findViewById(R.id.save_group_dataText);
            groupViewHolder.settingLayout = (LinearLayout) view.findViewById(R.id.save_group_setting);
            view.setTag(groupViewHolder);
        }else {
            groupViewHolder = (GroupViewHolder) view.getTag();
        }
        final BusinessDataInfo businessDataInfo = businessDataInfoList.get(i);
        groupViewHolder.bedNum.setText(businessDataInfo.getBedNo()+"床");
        groupViewHolder.name.setText(businessDataInfo.getPatName());
        groupViewHolder.sex.setText(businessDataInfo.getSex());
        groupViewHolder.age.setText(businessDataInfo.getAge()+"岁");
        String dataText="";
        if (businessDataInfo.getWsDataList()!=null&&businessDataInfo.getWsDataList().size()>0){
            for (WSData wsData:businessDataInfo.getWsDataList()){
                dataText+=wsData.getName()+" "+(StringUtils.isNotBlank(wsData.getExpandName())?wsData.getExpandName():"")+" "+ (StringUtils.isBlank(wsData.getValueCaption())?wsData.getValue():wsData.getValueCaption())+"、";
            }
            dataText = dataText.substring(0,dataText.length()-1);
        }else {
            dataText = "无";
        }
        groupViewHolder.dataText.setText(dataText);
        String formText = "";
        if (businessDataInfo.getFormList()!=null&&businessDataInfo.getFormList().size()>0){
            for (FormCheck formCheck:businessDataInfo.getFormList()){
                formText+=formCheck.getName()+"、";
            }
            formText = formText.substring(0,formText.length()-1);
        }else {
            formText = "无";
        }
        groupViewHolder.formText.setText(formText);
        groupViewHolder.settingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String formStr = "[{\"Name\":\"体温单\",\"Bldm\":\"F612A775-FCD7-483D-A2B9-7A1F2EECB40B\"},{\"Name\":\"新生儿体温单\",\"Bldm\":\"1DCFA477-6D8D-4C8B-B8CE-F846BBC615D4\"},{\"Name\":\"术前护理评估单\",\"Bldm\":\"333B2609-5504-4227-A520-793C3B95FD73\"},{\"Name\":\"术后护理评估单\",\"Bldm\":\"2B84147E-5824-473D-A84A-E21B166FAA42\"},{\"Name\":\"危重患者护理记录单\",\"Bldm\":\"E17CAE68-1C7F-4B78-B271-BC456787ED18\"},{\"Name\":\"血糖记录表\",\"Bldm\":\"1673A68E-3F32-4AEE-B2B3-83406699F538\"},{\"Name\":\"病人转科交接记录单\",\"Bldm\":\"E33F9E6A-DFF1-4057-B321-D73B70FB7799\"},{\"Name\":\"内科住院患者护理记录单\",\"Bldm\":\"A9B12E93-9606-4618-AC1E-EA7F22894478\"},{\"Name\":\"新入院评估单\",\"Bldm\":\"C2400BDC-B92F-489E-93A3-51E258B26F67\"},{\"Name\":\"(新)新入院评估单\",\"Bldm\":\"268f2ff1-25f9-4f67-ac46-fd0aa50f725a\"},{\"Name\":\"(新)产科入院评估单\",\"Bldm\":\"f26fa771-29c9-4cc0-81e7-2aca18b1a2e0\"},{\"Name\":\"(新)儿科入院评估单\",\"Bldm\":\"f816cb84-4c8c-430b-b76e-2735488ec9ef\"},{\"Name\":\"(新)新生儿入院评估单\",\"Bldm\":\"7526e6ac-01d6-41b2-8cfc-133742226bfb\"}]";
                List<FormCheck> formCheckList = new Gson().fromJson(formStr, new TypeToken<List<FormCheck>>() {
                }.getType());
                if (businessDataInfo.getFormList()!=null&& businessDataInfo.getFormList().size()>0){
                    for (FormCheck selectFormCheck : businessDataInfo.getFormList()){
                        for (FormCheck formCheck : formCheckList){
                            if (StringUtils.isEquals(formCheck.getBldm(),selectFormCheck.getBldm())){
                                formCheck.setCheck(true);
                            }
                        }
                    }
                }
                CustomDialog customDialog = new CustomDialog(mContext, formCheckList,i,false);
                customDialog.show();
            }
        });
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildViewHolder childViewHolder;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_save_child_item,null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.formText = (TextView) view.findViewById(R.id.save_child_formText);
            childViewHolder.dataText = (TextView) view.findViewById(R.id.save_child_dataText);
            view.setTag(childViewHolder);
        }else {
            childViewHolder = (ChildViewHolder) view.getTag();
        }
        BusinessDataInfo businessDataInfo = businessDataInfoList.get(i);
        String dataText="";
        if (businessDataInfo.getWsDataList()!=null&&businessDataInfo.getWsDataList().size()>0){
            for (WSData wsData:businessDataInfo.getWsDataList()){
                dataText+=wsData.getName()+" "+(StringUtils.isNotBlank(wsData.getExpandName())?wsData.getExpandName():"")+" "+ (StringUtils.isBlank(wsData.getValueCaption())?wsData.getValue():wsData.getValueCaption())+"\n";
            }
            dataText = dataText.substring(0,dataText.length()-1);
        }else {
            dataText = "无";
        }
        childViewHolder.dataText.setText(dataText);
        String formText = "";
        if (businessDataInfo.getFormList()!=null&&businessDataInfo.getFormList().size()>0){
            for (FormCheck formCheck:businessDataInfo.getFormList()){
                formText+=formCheck.getName()+"\n";
            }
            formText = formText.substring(0,formText.length()-1);
        }else {
            formText = "无";
        }
        childViewHolder.formText.setText(formText);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int i) {

    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long l, long l1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return 0;
    }

    public void updateList(List<BusinessDataInfo> businessDataInfos){
        this.businessDataInfoList = businessDataInfos;
        this.notifyDataSetChanged();
    }


    static class GroupViewHolder{
        /**
         * 床号
         */
        private TextView bedNum;
        /**
         * 姓名
         */
        private TextView name;
        /**
         * 性别
         */
        private TextView sex;
        /**
         * 年龄
         */
        private TextView age;
        /**
         * 表单数据
         */
        private TextView formText;
        /**
         * 记录数据
         */
        private TextView dataText;
        /**
         * 设置
         */
        private LinearLayout settingLayout;
    }


    static class ChildViewHolder{
        /**
         * 表单数据
         */
        private TextView formText;
        /**
         * 记录数据
         */
        private TextView dataText;
    }



}
