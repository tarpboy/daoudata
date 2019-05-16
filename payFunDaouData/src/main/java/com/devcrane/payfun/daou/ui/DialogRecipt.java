package com.devcrane.payfun.daou.ui;

import com.devcrane.payfun.daou.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;


public class DialogRecipt extends Dialog {
	public RadioButton radSms;
	public EditText txtEmail;
	public Button btnEmail;
	public EditText txtSMS;
	public Button btnSMS;
	public Button btnPrint;
	View v;

	public DialogRecipt(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup_recipt);
		v = getWindow().getDecorView();
		v.setBackgroundResource(android.R.color.transparent);
		radSms = (RadioButton) findViewById(R.id.radSms);
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		btnEmail = (Button) findViewById(R.id.btnEmail);
		txtSMS = (EditText) findViewById(R.id.txtSMS);
		btnSMS = (Button) findViewById(R.id.btnSMS);
		btnPrint = (Button) findViewById(R.id.btnPrint);
	}

	public DialogRecipt(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public DialogRecipt(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

}
