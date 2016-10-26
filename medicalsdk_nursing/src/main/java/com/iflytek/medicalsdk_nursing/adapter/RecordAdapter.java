package com.iflytek.medicalsdk_nursing.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.medicalsdk_nursing.PatientsActivity;
import com.iflytek.medicalsdk_nursing.R;
import com.iflytek.medicalsdk_nursing.RecordActivity;
import com.iflytek.medicalsdk_nursing.domain.BusinessDataInfo;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by chenzhilei on 2016/10/19.
 */

public class RecordAdapter extends BaseAdapter{

    private List<BusinessDataInfo> businessDataInfoList;

    private Context mContext;

    private SimpleDateFormat simpleDateFormat;

    public RecordAdapter(Context context, List<BusinessDataInfo> businessDataInfos){
        this.businessDataInfoList = businessDataInfos;
        this.mContext = context;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public int getCount() {
        return businessDataInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return businessDataInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_record_instrument,null);
            viewHolder = new ViewHolder();
            viewHolder.bedNumberText = (TextView) convertView.findViewById(R.id.instrument_bedNumber);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.instrument_name);
            viewHolder.sexText = (TextView) convertView.findViewById(R.id.instrument_sex);
            viewHolder.ageText = (TextView) convertView.findViewById(R.id.instrument_age);
            viewHolder.voiceLayout = (LinearLayout) convertView.findViewById(R.id.instrument_voice);
            viewHolder.itemListView = (ListView) convertView.findViewById(R.id.instrument_listView);
            viewHolder.noDataText = (TextView) convertView.findViewById(R.id.instrument_nodata_text);
            viewHolder.patientLayout = (LinearLayout) convertView.findViewById(R.id.instrument_patient_layout);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.itemListView = (ListView) convertView.findViewById(R.id.instrument_listView);
        }
        BusinessDataInfo businessDataInfo = businessDataInfoList.get(position);
        viewHolder.bedNumberText.setText(businessDataInfo.getBedNo());
        if (StringUtils.isBlank(businessDataInfo.getPatName())){
            viewHolder.noDataText.setVisibility(View.VISIBLE);
            viewHolder.sexText.setVisibility(View.GONE);
            viewHolder.ageText.setVisibility(View.GONE);
            viewHolder.nameText.setVisibility(View.GONE);
            viewHolder.patientLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PatientsActivity.class);
                    intent.putExtra("POSITION",position);
                    ((RecordActivity)mContext).startActivityForResult(intent,1001);
                }
            });
        }else {
            viewHolder.nameText.setText(businessDataInfo.getPatName());
            viewHolder.sexText.setText(businessDataInfo.getSex());
            viewHolder.ageText.setText(businessDataInfo.getAge());
        }
        viewHolder.voiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        RecordContentAdapter recordContentAdapter = new RecordContentAdapter(mContext,businessDataInfo.getWsDataList());
        viewHolder.itemListView.setAdapter(recordContentAdapter);
        setListViewHeightBasedOnChildren(viewHolder.itemListView);
        return convertView;
    }



    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void updateList(List<BusinessDataInfo> businessDataInfos){
        this.businessDataInfoList = businessDataInfos;
        this.notifyDataSetChanged();
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
        /**
         * 无数据提示
         */
        private TextView noDataText;
        /**
         * 语音按钮
         */
        private LinearLayout voiceLayout;
        /**
         * 患者信息layout
         */
        private LinearLayout patientLayout;
        /**
         * 数据项list
         */
        private ListView itemListView;
    }
}
