package com.iflytek.medicalsdk_nursing;

/**
 * @Title: com.iflytek.medicalsdk_nursing
 * @Copyright: IFlytek Co., Ltd. Copyright 16/10/9-上午10:29,  All rights reserved
 * @Description: TODO 监听器;
 * @author: chenzhilei
 * @data: 16/10/9 上午10:29
 * @version: V1.0
 */
public interface NursingListener {

    void onStartListener(boolean success);

    void onDataSavedListener(String result);
}
