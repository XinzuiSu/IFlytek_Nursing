package com.iflytek.medicalsdk_nursing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iflytek.medicalsdk_nursing.R;
import com.iflytek.medicalsdk_nursing.dao.MappingDao;
import com.iflytek.medicalsdk_nursing.domain.MappingInfo;
import com.iflytek.medicalsdk_nursing.domain.WSData;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by chenzhilei on 2016/10/19.
 */

public class RecordContentAdapter extends BaseAdapter{

    private List<WSData> wsDataList;

    private Context mContext;

    private SimpleDateFormat simpleDateFormat;

    private MappingDao mappingDao;

    public RecordContentAdapter(Context context, List<WSData> wsDatas){
        this.wsDataList = wsDatas;
        this.mContext = context;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mappingDao = new MappingDao(mContext);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_record_detail,null);
            viewHolder = new ViewHolder();
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.detail_item_name);
            viewHolder.valueText = (TextView) convertView.findViewById(R.id.detail_item_value);
            viewHolder.unitText = (TextView) convertView.findViewById(R.id.detail_item_unit);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        WSData wsData = wsDataList.get(position);
        MappingInfo mappingInfo = mappingDao.getMappingDic(wsData.getWsName());
        if (mappingInfo!=null){
            viewHolder.nameText.setText(mappingInfo.getValue());
        }else {
            viewHolder.nameText.setText(wsData.getWsName());
        }
        viewHolder.valueText.setText(wsData.getWsValue());
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
         * 单位
         */
        private TextView unitText;
    }
}
