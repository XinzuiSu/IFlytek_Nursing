package com.iflytek.medicalsdk_nursing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.android.framework.util.StringUtils;
import com.iflytek.medicalsdk_nursing.R;
import com.iflytek.medicalsdk_nursing.domain.WSData;
import com.iflytek.medicalsdk_nursing.view.StandingRecordActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by chenzhilei on 2016/10/19.
 */

public class RecordContentAdapter extends BaseAdapter{

    private List<WSData> wsDataList;

    private Context mContext;

    private SimpleDateFormat simpleDateFormat;

    private int groupID;

    private StandingRecordActivity recordActivity;


    public RecordContentAdapter(Context context, List<WSData> wsDatas , int groupID){
        this.wsDataList = wsDatas;
        this.mContext = context;
        this.recordActivity = (StandingRecordActivity) context;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.groupID = groupID;
    }

    @Override
    public int getCount() {
        return wsDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return wsDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_record_detail,null);
            viewHolder = new ViewHolder();
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.detail_item_name);
            viewHolder.valueText = (TextView) convertView.findViewById(R.id.detail_item_value);
            viewHolder.deleteLayout = (LinearLayout) convertView.findViewById(R.id.detail_item_delete);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        WSData wsData = wsDataList.get(position);

        viewHolder.nameText.setText(wsData.getName());
        if (StringUtils.isNotBlank(wsData.getValueCaption())){
            viewHolder.valueText.setText(wsData.getValueCaption());
        }else {
            if (wsData.getValue().contains(":")){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH:mm:ss");
                Date date = null;
                try {
                    date = dateFormat.parse(wsData.getValue());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                viewHolder.valueText.setText(simpleDateFormat.format(date));
            }else {
                viewHolder.valueText.setText(wsData.getValue());
            }
        }
        viewHolder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordActivity.delete(groupID,position);
            }
        });
        return convertView;
    }

    static class ViewHolder{
        /**
         * 项目名称
         */
        private TextView nameText;
        /**
         * 值
         */
        private TextView valueText;
        /**
         * 删除
         */
        private LinearLayout deleteLayout;
    }
}
