package com.devcrane.payfun.daou.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.utility.BHelper;

public class DialogPhoneNumber extends Dialog {
	public Button btnOK, btnCancel;
	public TextView txtPhoneNumber;
	public TextView tvTitle;
	View v;

	public DialogPhoneNumber(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.screen_confirm);
		v = getWindow().getDecorView();
		v.setBackgroundResource(android.R.color.transparent);
		txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
		tvTitle = (TextView) findViewById(R.id.txtTitle);
		btnOK = (Button) findViewById(R.id.btnconfirmphoneNumber);
		btnCancel = (Button) findViewById(R.id.btncancelphoneNumber);
		BHelper.setTypeface(tvTitle);

	}

	public DialogPhoneNumber(Context context, int theme) {
		super(context, theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.screen_confirm);
		v = getWindow().getDecorView();
		v.setBackgroundResource(android.R.color.transparent);
		txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
		tvTitle = (TextView) findViewById(R.id.txtTitle);
		btnOK = (Button) findViewById(R.id.btnconfirmphoneNumber);
		btnCancel = (Button) findViewById(R.id.btncancelphoneNumber);
		BHelper.setTypeface(tvTitle);
	}

	public DialogPhoneNumber(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

}
