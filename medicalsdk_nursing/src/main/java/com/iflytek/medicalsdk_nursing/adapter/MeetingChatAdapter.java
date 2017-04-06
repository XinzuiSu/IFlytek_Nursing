package com.iflytek.medicalsdk_nursing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.medicalsdk_nursing.R;
import com.iflytek.medicalsdk_nursing.domain.MessageInfo;
import com.iflytek.medicalsdk_nursing.view.MeetingActivity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by suxiaofeng on 2017/3/28.
 */
public class MeetingChatAdapter extends BaseAdapter {
    private List<MessageInfo> chatList;
    private Context mContext;

    public MeetingChatAdapter(MeetingActivity meetingActivity, List<MessageInfo> chatList) {
        this.chatList = chatList;
        this.mContext = meetingActivity;
    }

    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        MessageInfo info = chatList.get(position);
        return Integer.parseInt(info.getUserFlag());
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int mySelf = getItemViewType(position);
        ViewHolder holder = null;
        View convertView1,convertView2;
        if (0 == mySelf) {
            convertView1 = convertView;
            if(convertView1==null){
                convertView1= inflater.inflate(R.layout.chatting_item_to,null);
                holder=new ViewHolder();
                holder.time=(TextView) convertView1.findViewById(R.id.item_chat_time);
                holder.name=(TextView)convertView1.findViewById(R.id.item_chat_name);
                holder.content=(TextView)convertView1.findViewById(R.id.item_chat_content);
//                holder.chatImage=(ImageView)convertView1.findViewById(R.id.chatting_avatar_iv);
                convertView1.setTag(holder);
            }else{
                holder=(ViewHolder) convertView1.getTag();
            }
            convertView=convertView1;
        }else if (1 == mySelf){
            convertView2 = convertView;
            if(convertView2==null){
                convertView2=inflater.inflate(R.layout.chatting_item_from, null);
                holder=new ViewHolder();
                holder.time=(TextView) convertView2.findViewById(R.id.item_chat_time);
                holder.name=(TextView)convertView2.findViewById(R.id.item_chat_name);
                holder.content=(TextView)convertView2.findViewById(R.id.item_chat_content);
//                holder.chatImage=(ImageView)convertView2.findViewById(R.id.chatting_avatar_iv);
                convertView2.setTag(holder);
            }else{
                holder=(ViewHolder) convertView2.getTag();
            }
            convertView=convertView2;
        }
        holder.name.setText(chatList.get(position).getUserName());
        holder.time.setText(getDate(chatList.get(position).getCreatTime()));
        if ("1".equals(chatList.get(position).getMessageFlag())) {
            holder.content.setText("系统消息: "+chatList.get(position).getContent());
        } else {
            holder.content.setText(chatList.get(position).getContent());
        }

        return convertView;
    }

    private String getDate(long creatTime) {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy/dd/MM HH:mm:ss");
        return sdf.format(new Date(creatTime));
    }

    public void update(List<MessageInfo> chatList) {
        this.chatList = chatList;
        this.notifyDataSetChanged();
    }

    private class ViewHolder{
        TextView time;
        TextView name;
        TextView content;
        ImageView chatImage;

    }
}
