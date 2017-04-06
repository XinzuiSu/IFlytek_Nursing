/*
 * Copyright @ 2015 com.iflytek.android
 * BZFamily 下午8:15:50
 * All right reserved.
 *
 */
package com.iflytek.medicalsdk_nursing.util;

import android.app.Dialog;
import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.medicalsdk_nursing.R;

/**
 * create by zlchen
 */
public abstract class ChatCustomDialog {
	/**
	 * 要显示的dialog
	 */
	private Dialog dialog;
	/**
	 * 显示一个按钮的布局和显示两个按钮的布局
	 */
	private TextView singleSure;
	private LinearLayout doubleLayout;
	/**
	 * 一个确定按钮，左边按钮，右边按钮，显示的文字内容
	 */
	private TextView doubleCancle, doubleSure, tipContent;

	private ItemClickListener itemClickListener;

	/**
	 * 双按钮
	 * @param context
	 * @param tipTxt 标题
	 * @param rightText 右侧按钮文字
	 * @param leftText  左侧按钮文字
     */
	public ChatCustomDialog(Context context, String tipTxt, String rightText, String leftText) {
		itemClickListener = new ItemClickListener();
		initView(context);
		tipContent.setText(tipTxt);
		singleSure.setVisibility(View.GONE);
		doubleCancle.setText(leftText);
		doubleSure.setText(rightText);

	}

	/**
	 * 单按钮
	 * @param context
	 * @param tipText 标题
	 * @param singleText  按钮文字
     */
	public ChatCustomDialog(Context context, String tipText, String singleText){
		itemClickListener = new ItemClickListener();
		initView(context);
		doubleLayout.setVisibility(View.GONE);
		tipContent.setText(tipText);
		singleSure.setText(singleText);

	}

	/**
	 * 带特殊色彩的单按钮
	 * @param context
	 * @param tipText
	 * @param singleText
     */
	public ChatCustomDialog(Context context, SpannableString tipText, String singleText){
		itemClickListener = new ItemClickListener();
		initView(context);
		doubleLayout.setVisibility(View.GONE);
		tipContent.setText(tipText);
		singleSure.setText(singleText);

	}

	private void initView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.custom_dialog_chat, null);
		dialog = new Dialog(context);
		dialog.setCancelable(false);
		singleSure = (TextView) dialogView.findViewById(R.id.custom_dialog_single_sure);
		doubleLayout = (LinearLayout) dialogView.findViewById(R.id.custom_dialog_double);
		tipContent = (TextView) dialogView.findViewById(R.id.custom_dialog_tip);
		singleSure.setOnClickListener(itemClickListener);
		doubleSure = (TextView) dialogView.findViewById(R.id.custom_dialog_double_sure);
		doubleCancle = (TextView) dialogView.findViewById(R.id.custom_dialog_double_cancel);
		doubleCancle.setOnClickListener(itemClickListener);
		doubleSure.setOnClickListener(itemClickListener);
		dialog.setContentView(dialogView);
	}




	public class ItemClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.custom_dialog_single_sure) {
				onSingleClick();

			} else if (i == R.id.custom_dialog_double_sure) {
				onDoubleRightClick();

			} else if (i == R.id.custom_dialog_double_cancel) {
				onDoubleLeftClick();

			} else {
			}

		}
	}

	public abstract void onSingleClick();

	public abstract void onDoubleRightClick();

	public abstract void onDoubleLeftClick();


	public void show() {
		if (dialog!=null){
			dialog.show();
		}


	}

	public void dismiss() {
		if (dialog!=null){
			dialog.dismiss();
		}

	}

	public boolean isShowing(){
		return dialog.isShowing();
	}

}
