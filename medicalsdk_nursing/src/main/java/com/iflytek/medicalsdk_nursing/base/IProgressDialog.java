package com.iflytek.medicalsdk_nursing.base;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * @Title: com.iflytek.medicalsdk_nursing.base
 * @Copyright: IFlytek Co., Ltd. Copyright 2016/10/31-上午10:44,  All rights reserved
 * @Description: TODO 请描述此文件是做什么的;
 * @author: chenzhilei
 * @data: 2016/10/31 上午10:44
 * @version: V1.0
 */

public class IProgressDialog extends ProgressDialog {


    public IProgressDialog(Context context) {
        super(context);
    }

    public IProgressDialog(Context context, int theme) {
        super(context, theme);
    }
}
