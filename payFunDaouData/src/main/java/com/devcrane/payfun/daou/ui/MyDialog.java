package com.devcrane.payfun.daou.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.utility.BHelper;

public class MyDialog extends Dialog {
	public Button btnOK;
	public TextView tvContent,tvTitle;
	View v;

	public MyDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_notice);
		v = getWindow().getDecorView();
		v.setBackgroundResource(android.R.color.transparent);
		tvContent = (TextView) findViewById(R.id.tvNoticeContent);
		tvTitle = (TextView) findViewById(R.id.tvNoticeTitle);
		btnOK = (Button) findViewById(R.id.btnNoticeOk);
		BHelper.setTypeface(tvTitle);
	}

	public MyDialog(Context context, int theme) {
		super(context, theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_notice);
		v = getWindow().getDecorView();
		v.setBackgroundResource(android.R.color.transparent);
		btnOK = (Button) findViewById(R.id.btnNoticeOk);
		tvContent = (TextView) findViewById(R.id.tvNoticeContent);
		tvTitle = (TextView) findViewById(R.id.tvNoticeTitle);
		BHelper.setTypeface(tvTitle);
	}

	public MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

}
