package com.devcrane.payfun.daou.ui;


import com.devcrane.payfun.daou.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class MyProgressDialog extends ProgressDialog{

	TextView tvMessage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_progress_dialog);
		tvMessage = (TextView)findViewById(R.id.tv_custom_progress_content);
	}
	@Override
	public void setMessage(CharSequence message) {
		// TODO Auto-generated method stub
//		super.setMessage(message);
		tvMessage.setText(message);
	}
//	public void setMessage(String msg){
//		tvMessage.setText(msg);
//	}
	public MyProgressDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	

}
