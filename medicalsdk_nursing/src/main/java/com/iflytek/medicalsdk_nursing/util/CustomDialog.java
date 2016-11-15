/*
 * Copyright @ 2015 com.iflytek.android
 * BZFamily 下午8:15:50
 * All right reserved.
 *
 */
package com.iflytek.medicalsdk_nursing.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.iflytek.medicalsdk_nursing.R;
import com.iflytek.medicalsdk_nursing.adapter.DialogCheckAdapter;
import com.iflytek.medicalsdk_nursing.domain.FormCheck;
import com.iflytek.medicalsdk_nursing.view.RecordActivity;

import java.util.List;


/**
 * create by zlchen
 */
public class CustomDialog extends Dialog{

	/**
	 * 显示一个按钮的布局和显示两个按钮的布局
	 */
	private LinearLayout doubleLayout;
	/**
	 * 一个确定按钮，左边按钮，右边按钮，显示的文字内容
	 */
	private TextView doubleCancle, doubleSure, tipContent;

	private ListView listView;

	private Context mContext;

	private View view;

	private List<FormCheck> formCheckList;

	private RecordActivity recordActivity;

	public CustomDialog(Context context, List<FormCheck> formChecks) {
		super(context);
		this.mContext = context;
		// 索引布局
		this.view = LayoutInflater.from(mContext).inflate(
				R.layout.custom_dialog, null);
		this.formCheckList = formChecks;
		this.recordActivity = (RecordActivity) context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(view);

		this.setCancelable(true);
		initView();
	}

	private void initView() {
//		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		this.setCancelable(false);
		tipContent = (TextView) view.findViewById(R.id.custom_dialog_tip);
		doubleSure = (TextView) view.findViewById(R.id.custom_dialog_double_sure);
		doubleCancle = (TextView) view.findViewById(R.id.custom_dialog_double_cancel);
		listView = (ListView) view.findViewById(R.id.custom_dialog_listView);
		final DialogCheckAdapter dialogCheckAdapter = new DialogCheckAdapter(mContext,formCheckList);
		listView.setAdapter(dialogCheckAdapter);
		doubleCancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				cancel();
			}
		});
		doubleSure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                cancel();
                recordActivity.saveRecordInfo(dialogCheckAdapter.getSelectedFormList());
			}
		});
	}

	public void cancel(){
		this.dismiss();
	}


}
