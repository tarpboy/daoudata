package com.devcrane.payfun.daou;

import org.apache.commons.validator.EmailValidator;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.devcrane.payfun.daou.manager.UserManager;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.MyTaskToast;

public class ResetPasswordActivity  extends Activity {

	private EditText txtEmail;
	private Button btnSend,btnResetBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.screen_reset_password);
		initLayout();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
	void initLayout(){
		txtEmail = (EditText)findViewById(R.id.txtEmail);
		btnSend = (Button)findViewById(R.id.btnSend);
		btnResetBack = (Button)findViewById(R.id.btnResetBack);
		
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
					resetPass();
			}
		});
		btnResetBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				exitActivity();
			}
		});
	}

	
	void resetPass(){
		hideSoftKeyboard();
		BHelper.requireTxt(txtEmail, R.string.register_email_is_required);
		if(BHelper.mRequire)
			return;
		final String email = txtEmail.getText().toString();
		boolean res = EmailValidator.getInstance().isValid(email);
		if(!res){
			BHelper.showToast(R.string.email_invalid);
			return;
		}
		new MyTaskToast(this,"로딩중...") {
			
			@Override
			public boolean run() {
				// TODO Auto-generated method stub
				return UserManager.resetPassword(email).equals(email);
			}
			
			@Override
			public boolean res(boolean result) {
				// TODO Auto-generated method stub
				if(result){
					BHelper.showToast(R.string.already_sent_new_password);
					exitActivity();
				}
				else
					BHelper.showToast(R.string.new_password_sent_failed);
				return false;
			}
		};
		
		
		
	}
	void exitActivity(){
		this.finish();
	}
	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(txtEmail.getWindowToken(), 0);
	}
}
