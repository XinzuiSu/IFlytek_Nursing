package com.iflytek.medicalsdk_nursing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.iflytek.medicalsdk_nursing.R;
import com.iflytek.medicalsdk_nursing.domain.FormCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: com.iflytek.medicalsdk_nursing.adapter
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/11/11-上午9:35,  All rights reserved
 * @Description: TODO 确定弹出选中框adapter;
 * @author: chenzhilei
 * @data: 2016/11/11 上午9:35
 * @version: V1.0
 */

public class DialogCheckAdapter extends BaseAdapter{

    private List<FormCheck> formCheckList;

    private Context mContext;

    private List<FormCheck> selectedFormList;

    public DialogCheckAdapter(Context context, List<FormCheck> formChecks){
        this.mContext = context;
        this.formCheckList = formChecks;
        selectedFormList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return formCheckList.size();
    }

    @Override
    public Object getItem(int i) {
        return formCheckList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        CheckViewHolder checkViewHolder;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_form_check,null);
            checkViewHolder = new CheckViewHolder();
            checkViewHolder.formText = (TextView) view.findViewById(R.id.form_text);
            checkViewHolder.formCheck = (CheckBox) view.findViewById(R.id.form_check);
            view.setTag(checkViewHolder);
        }else {
            checkViewHolder = (CheckViewHolder) view.getTag();
        }
        checkViewHolder.formText.setText(formCheckList.get(i).getName());
        checkViewHolder.formCheck.setOnCheckedChangeListener(null);
        checkViewHolder.formCheck.setChecked(formCheckList.get(i).isCheck());
        checkViewHolder.formCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    formCheckList.get(i).setCheck(true);
                    selectedFormList.add(formCheckList.get(i));
                }else {
                    if (selectedFormList.contains(formCheckList.get(i))){
                        formCheckList.get(i).setCheck(false);
                        selectedFormList.remove(formCheckList.get(i));
                    }
                }

            }
        });
        return view;
    }

    /**
     * 获取选中的list
     * @return
     */
    public List<FormCheck> getSelectedFormList() {
        return selectedFormList;
    }

    /**
     * viewHolder
     */
    static class CheckViewHolder{
        //表单文本
        private TextView formText;
        //选中框
        private CheckBox formCheck;
    }
}
