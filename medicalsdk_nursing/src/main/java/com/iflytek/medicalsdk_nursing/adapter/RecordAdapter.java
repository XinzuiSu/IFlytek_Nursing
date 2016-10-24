package com.iflytek.medicalsdk_nursing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.iflytek.medicalsdk_nursing.R;
import com.iflytek.medicalsdk_nursing.domain.WSData;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by chenzhilei on 2016/10/19.
 */

public class RecordAdapter extends BaseAdapter{

    private List<WSData> WSDataList;

    private Context mContext;

    private SimpleDateFormat simpleDateFormat;

    public RecordAdapter(Context context, List<WSData> WSDatas){
        this.WSDataList = WSDatas;
        this.mContext = context;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public int getCount() {
        return WSDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return WSDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder;
//        if (convertView == null){
//            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_record_instrument,null);
//            viewHolder = new ViewHolder();
//            viewHolder.timeText = (TextView) convertView.findViewById(R.id.);
//            viewHolder.patientInfoText = (TextView) convertView.findViewById(R.id.instrument_patientInfo);
//            viewHolder.itemListView = (ListView) convertView.findViewById(R.id.instrument_listView);
//            convertView.setTag(viewHolder);
//        }else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        WSData WSData = WSDataList.get(position);
//        viewHolder.timeText.setText(WSData.getTime());
//        viewHolder.patientInfoText.setText(WSData.getPatientInfo().getBedNo()+"床         "+ WSData.getPatientInfo().getPatName());
////        RecordContentAdapter recordContentAdapter = new RecordContentAdapter(mContext,WSData.get)
        return convertView;
    }

    static class ViewHolder{
        private TextView bedNumberText;

        private TextView nameText;

        private ListView itemListView;
    }
}
