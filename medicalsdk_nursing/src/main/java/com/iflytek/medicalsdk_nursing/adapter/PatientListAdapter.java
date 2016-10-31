package com.iflytek.medicalsdk_nursing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iflytek.medicalsdk_nursing.R;
import com.iflytek.medicalsdk_nursing.domain.PatientInfo;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by chenzhilei on 2016/10/19.
 */

public class PatientListAdapter extends BaseAdapter{

    private List<PatientInfo> patientInfoList;

    private Context mContext;

    private SimpleDateFormat simpleDateFormat;

    public PatientListAdapter(Context context, List<PatientInfo> patientInfos){
        this.patientInfoList = patientInfos;
        this.mContext = context;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public int getCount() {
        return patientInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return patientInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_patient,null);
            viewHolder = new ViewHolder();
            viewHolder.bedNumberText = (TextView) convertView.findViewById(R.id.instrument_bedNumber);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.instrument_name);
            viewHolder.sexText = (TextView) convertView.findViewById(R.id.instrument_sex);
            viewHolder.ageText = (TextView) convertView.findViewById(R.id.instrument_age);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PatientInfo patientInfo = patientInfoList.get(position);
        viewHolder.bedNumberText.setText(patientInfo.getHosBedNum()+"床");
        viewHolder.nameText.setText(patientInfo.getPatName());
        viewHolder.sexText.setText(patientInfo.getPatSex());
        viewHolder.ageText.setText(patientInfo.getPatBirth());
        return convertView;
    }

    static class ViewHolder{
        /**
         * 床号
         */
        private TextView bedNumberText;
        /**
         * 姓名
         */
        private TextView nameText;
        /**
         * 性别
         */
        private TextView sexText;
        /**
         * 年龄
         */
        private TextView ageText;
    }
}
