package com.iflytek.medicalsdk_nursing;

import android.content.Context;
import android.content.Intent;

import com.iflytek.medicalsdk_nursing.base.IFlyNursing;

/**
 * @Title: com.iflytek.medicalsdk_nursing
 * @Copyright: IFlytek Co., Ltd. Copyright 16/10/9-上午10:03,  All rights reserved
 * @Description: TODO 护理语音助手实际业务处理;
 * @author: chenzhilei
 * @data: 16/10/9 上午10:03
 * @version: V1.0
 */
public class NursingSpeecher {

    private Context mContext;

    public NursingSpeecher(Context context){
        this.mContext  = context;
    }

    /**
     * 开始记录
     */
    public void startRecord(){
        Intent intent = new Intent(mContext,RecordActivity.class);
        mContext.startActivity(intent);
        IFlyNursing.getInstance().getNursingListener().onStartListener(true);
    }


    public void setNursingListener(NursingListener nursingListener){
        IFlyNursing.getInstance().setNursingListener(nursingListener);
    }


}
