package com.iflytek.nursing;

import android.app.Application;

import com.iflytek.medicalsdk_nursing.base.IFlyNursing;

/**
 * @Title: com.iflytek.nursing
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/10/17-上午11:18,  All rights reserved
 * @Description: TODO 请描述此文件是做什么的;
 * @author: chenzhilei
 * @data: 2016/10/17 上午11:18
 * @version: V1.0
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        IFlyNursing.getInstance().initSDK(this,"123456");
    }
}
