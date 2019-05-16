package com.devcrane.payfun.daou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.UserEntity;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.manager.UserManager;
import com.devcrane.payfun.daou.ui.DialogPhoneNumber;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.MyTask;
import com.devcrane.payfun.daou.utility.MyTaskSetToast;
import com.devcrane.payfun.daou.utility.StringTaskSetToast;

public class LoginFragment extends Fragment {
//	public static String UPDATE_UID, F_USERID, F_PASSWD;
	private Activity at;
	private EditText txtUserEmail;
	private EditText txtPasswd;
	private CheckBox cbPasswd;
	private Button btnLogin;
	private Button btnSignUp;
	private Button btnFind;
	DialogPhoneNumber dialog;
	String userID;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_login, container, false);
		txtUserEmail = (EditText) v.findViewById(R.id.txtUserEmail);
		txtPasswd = (EditText) v.findViewById(R.id.txtPasswd);
		cbPasswd = (CheckBox) v.findViewById(R.id.cbPasswd);
		btnLogin = (Button) v.findViewById(R.id.btnLogin);		
		btnSignUp = (Button) v.findViewById(R.id.btnSignUp);
		btnFind = (Button) v.findViewById(R.id.btnfind);
//		btnCancel = (Button) v.findViewById(R.id.btnLoginCancel);
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		BHelper.setActivity(at = getActivity());
		BHelper.setTypeface(getView());
		initComponent();
		
		if (StaticData.IS_TEST) {
			txtUserEmail.setText("qts@payfun.com");
			txtPasswd.setText("123456");
		}
	}

	private void initComponent() {
		BHelper.requireBtn(btnLogin, new Runnable() {
			@Override
			public void run() {
				hideKeyboard();
				String pf_Email = BHelper.requireTxt(txtUserEmail,R.string.register_email_is_required);
				String pf_Passwd = BHelper.requireTxt(txtPasswd,R.string.register_password_is_required);
				if (BHelper.mRequire) {
					return;
				}
				doCheckEmail(pf_Email, pf_Passwd);
			}
		});
		btnSignUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainActivity.setFragment(new SignUpFragment());
			}
		});
		btnFind.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				startActivity(new Intent(getActivity(), ResetPasswordActivity.class));
//				
			}
		});
		doLoginSave();
	}
	
	
	
	public void doLoginSave() {
		UserEntity key = new UserEntity(0);
		String f_Passwd = AppHelper.prefGet(key.getF_Password(), "");
		if (!f_Passwd.equals("")) {
			String f_Email = AppHelper.prefGet(key.getF_Email(), "");
			txtUserEmail.setText(f_Email);
			txtPasswd.setText(f_Passwd);
		}
	}

	private void doLoginCheck(final String pf_Email, final String pf_Passwd) {
		new MyTaskSetToast(at) {
			@Override
			public boolean run() {
				BHelper.db("Login: 2. doLoginCheck");
				return (userID = UserManager.checkLoginV1(at,pf_Email, pf_Passwd)) != null;
			}
			@Override
			public boolean res(boolean result) {
				UserEntity key = new UserEntity(0);
				if (!result) {
					AppHelper.prefSet(key.getF_Email(), "");
					AppHelper.prefSet(key.getF_Password(), "");
				} else {					
					if (cbPasswd.isChecked()) {
						AppHelper.prefSet(key.getF_Email(), pf_Email);
						AppHelper.prefSet(key.getF_Password(), pf_Passwd);
					}else{
						AppHelper.prefSet(key.getF_Email(), "");
						AppHelper.prefSet(key.getF_Password(),"");
					}
					AppHelper.setCurrentUserID(userID);
					AppHelper.setUpdateUserID(pf_Email);
					doLoginSuccess();
				}
				showToast(getString(R.string.sqlite_success), getString(R.string.msg_wrong_password));
				return true;
			}
		};
	}
	private void doCheckEmail(final String pf_Email, final String pf_Passwd){
		new StringTaskSetToast(at) {
			
			@Override
			public String run() {
				BHelper.db("Login: 1. do check email");
				return UserManager.checkEmail(pf_Email," ");
			}
			
			@Override
			public boolean res(String result) {
				BHelper.db("Login: 1. result:"+result);
				if(result.equals("-1"))
					showToast(getString(R.string.sqlite_error));
				else if(result.equals("0")){
					showToast(getString(R.string.msg_wrong_ID));
					
				}else{
					doLoginCheck(pf_Email, pf_Passwd);
				}
				return false;
			}
		};
	}
	private void hideKeyboard() {   
	    // Check if no view has focus:
	    View view = getActivity().getCurrentFocus();
	    if (view != null) {
	        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}

	private void doLoginSuccess() {
		
		new MyTask(at) {
			
			@Override
			public boolean run() {
				BHelper.db("Login: 3. getCompanyByUserID:"+userID);
				CompanyManger.getCompanyByUserID(AppHelper.getCurrentUserID());
				return true;
			}
			
			@Override
			public boolean res(boolean result) {
				AppHelper.setIsLogin(true);
				String name = AppHelper.getCurrentUserName();
				HomeFragment.tvUserName.setText(name.equals("") ? "" : name + "님");
				((MainActivity) getActivity()).initMenuLeft();
				MainActivity.setFragment(new HomeFragment());
				return false;
			}
		};
	}
}
