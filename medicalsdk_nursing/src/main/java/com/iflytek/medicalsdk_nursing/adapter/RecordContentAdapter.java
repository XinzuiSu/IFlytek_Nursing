package com.iflytek.medicalsdk_nursing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iflytek.medicalsdk_nursing.R;
import com.iflytek.medicalsdk_nursing.dao.DocumentDetailDicDao;
import com.iflytek.medicalsdk_nursing.domain.DocumentDetailDic;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/10/19.
 */

public class RecordContentAdapter extends BaseAdapter{

    private List<DocumentDetailDic> instrumentDicList;

    private Context mContext;

    private SimpleDateFormat simpleDateFormat;

    public RecordContentAdapter(Context context, List<DocumentDetailDic> instrumentDics){
        this.instrumentDicList = instrumentDics;
        this.mContext = context;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public int getCount() {
        return instrumentDicList.size();
    }

    @Override
    public Object getItem(int position) {
        return instrumentDicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder;
//        if (convertView == null){
//            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_record_detail,null);
//            viewHolder = new ViewHolder();
//            viewHolder.typeText = (TextView) convertView.findViewById(R.id.detail_type);
//            viewHolder.itemNameText = (TextView) convertView.findViewById(R.id.detail_item_name);
//            viewHolder.itemValueText = (TextView) convertView.findViewById(R.id.detail_item_value);
//            convertView.setTag(viewHolder);
//        }else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        InstrumentDic instrumentDic = instrumentDicList.get(position);
//        viewHolder.typeText.setText(instrumentDic.getInstruName());
//        viewHolder.itemNameText.setText(instrumentDic.getProjectName());
//        DocumentDetailDicDao documentDetailDicDao = new DocumentDetailDicDao(mContext);
//        //假设“1”为选择项，其他为数值
//        if (instrumentDic.getType() == "1"){
//            ItemDic itemDic = documentDetailDicDao.getItemDic(instrumentDic.getValue());
//            if (itemDic!=null){
//                viewHolder.itemValueText.setText(itemDic.getCheckName());
//            }else {
//                viewHolder.itemValueText.setText(instrumentDic.getValue());
//            }
//        }else {
//            //展示数值加单位
//            viewHolder.itemValueText.setText(instrumentDic.getValue()+instrumentDic.getUnit());
//        }
        return convertView;
    }

    static class ViewHolder{
        private TextView typeText;

        private TextView itemNameText;

        private TextView itemValueText;
    }
}
