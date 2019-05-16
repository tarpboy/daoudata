package com.devcrane.payfun.daou;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devcrane.payfun.daou.customkeypad.KeyboardUtil;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.manager.UserManager;
import com.devcrane.payfun.daou.ui.DialogPhoneNumber;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.PaymentTask;

import java.lang.reflect.Method;

public class ConfirmActivity extends Activity {
	public static final int COUNT = 0;
	private EditText codeInput, phoneInput;
	private Button btnConfirmCode, btnGetCode,btnBackToTop;
	private CheckBox cbTerm1, cbTerm2;
	private ImageView imageClearText;
	DialogPhoneNumber dialog;
	TextView txtTitleConfirm, txtCountConfirm;
	LinearLayout layoutTerm, layoutInput, layoutConfirm;
	public String CODECONFIRM;
	String phoneNumber;
	private int Total = 59;
	private Handler mHandler = new MessageHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_activity);
		BHelper.setActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initView();
		initEvent();

	}

	private void initView() {
		codeInput = (EditText) findViewById(R.id.txtCodeConfirm);
		phoneInput = (EditText) findViewById(R.id.txtPhoneNumberInput);
		btnConfirmCode = (Button) findViewById(R.id.btnCodeConfirm);
		imageClearText = (ImageView) findViewById(R.id.imgClearPhoneNumber);
		btnGetCode = (Button) findViewById(R.id.getCodeConfirm);
		cbTerm1 = (CheckBox) findViewById(R.id.cbterm1);
		cbTerm2 = (CheckBox) findViewById(R.id.cbterm2);
		txtTitleConfirm = (TextView) findViewById(R.id.titleConfirmCode);
		txtCountConfirm = (TextView) findViewById(R.id.titleCountConfirm);
		layoutTerm = (LinearLayout) findViewById(R.id.screenTerm);
		layoutInput = (LinearLayout) findViewById(R.id.screenInPut);
		layoutConfirm = (LinearLayout) findViewById(R.id.screenConfirmCode);
		btnBackToTop =(Button) findViewById(R.id.btnBackToTop);
	}

	private void initEvent() {
		cbTerm1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				cbTerm2.setEnabled(isChecked);

			}
		});
		cbTerm2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					layoutTerm.setVisibility(View.GONE);
					layoutInput.setVisibility(View.VISIBLE);
				}

			}
		});
		imageClearText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneInput.setText("");
			}
		});
		phoneInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				String str = s.toString();
				BHelper.db("input:"+ str);
				if (!str.equals("") && phoneNumber!=null && phoneNumber.length()<s.length()) {
					str = (str.contains("-") ? str.replace("-", "") : str);
					if (str.length() > 3 && str.length() < 7) {
						str = str.substring(0, 3) + "-" + str.substring(3);
					} else if (str.length() >= 7) {
						str = str.substring(0, 3) + "-" + str.substring(3, 7) + "-" + str.substring(7);
					}
				}
				phoneNumber = str;
				phoneInput.removeTextChangedListener(this);
				phoneInput.setText(str);
				phoneInput.setSelection(str.length());
				phoneInput.addTextChangedListener(this);

			}
		});
		disableShowSoftInput(phoneInput);
		phoneInput.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showKeyboard();
				return false;
			}
		});
        phoneInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    KeyboardUtil.getInstance(ConfirmActivity.this,ConfirmActivity.this).hideKeyboard();
                else{
                    //KeyboardUtil.hideSoftKeyboard(phoneInput, this);
                    disableShowSoftInput(phoneInput);
                }
            }
        });

        btnGetCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				phoneNumber = phoneInput.getText().toString();
				if (phoneNumber == null || phoneNumber.equals("")) {
					Toast.makeText(getBaseContext(), "휴대폰번호를 입력해주세요 !", Toast.LENGTH_LONG).show();
					return;
				}
				final String phoneSend = (phoneNumber.contains("-") ? phoneNumber.replace("-", "") : phoneNumber);
				
				if(phoneSend.length()!=11){
					BHelper.showToast(R.string.msg_phone_num_size_invalid);
					return;
				}
				dialog = new DialogPhoneNumber(ConfirmActivity.this);
				dialog.show();
				dialog.tvTitle.setTextColor(Color.BLACK);
				dialog.txtPhoneNumber.setText("+82 " + phoneNumber);
				dialog.txtPhoneNumber.setTextColor(Color.BLACK);
				dialog.btnOK.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						getCodeConfirm(phoneSend);
					}
				});
				dialog.btnCancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();

					}
				});

			}
		});

		btnConfirmCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String codeConfirmInput = codeInput.getText().toString();
				if (codeConfirmInput.equals(CODECONFIRM)) {
					hideSoftKeyboard();
					Intent returnIntent = new Intent();
					setResult(RESULT_OK, returnIntent);
					finish();
				} else {
					showFallbackDlgWithConfirm(getString(R.string.msg_confirm_code_incorrect));
//					Toast.makeText(getBaseContext(), "Wrong code!", Toast.LENGTH_LONG).show();
				}

			}
		});
		btnBackToTop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cancelAction();
			}
		});

	}
	void cancelAction(){
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}
	private void getCodeConfirm(final String phone) {

		new PaymentTask(ConfirmActivity.this) {

			@Override
			public String run() {
				return UserManager.getCodeConfirm(phone);
			}

			@Override
			public boolean res(String result) {
				if (!result.equals("-1")) {
					CODECONFIRM = result;
					AppHelper.prefSet(StaticData.PhoneNumber, phoneNumber);
					Total = 59;
					mHandler.sendEmptyMessage(COUNT);
					txtTitleConfirm.setText("+82 " + phoneNumber + "로 전송받은 4자리 인증번호를 입력해주세요.");
					layoutConfirm.setVisibility(View.VISIBLE);
					layoutInput.setVisibility(View.GONE);
					AlertDialog.Builder builder1 = new AlertDialog.Builder(ConfirmActivity.this);
					builder1.setTitle("ConfirmCode");
		            builder1.setMessage(result);
		            builder1.setCancelable(true);
		            builder1.setPositiveButton("OK",
		                    new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int id) {
		                    dialog.cancel();
		                }
		            });

		            AlertDialog alert11 = builder1.create();
//		            alert11.show();
				} else {
					Toast.makeText(getBaseContext(), getString(R.string.sqlite_error), Toast.LENGTH_LONG).show();
				}
				return false;
			}
		};

	}

	private class MessageHandler extends Handler {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case COUNT:
				Total--;
				txtCountConfirm.setText("인증번호가 " + Total + "초 이내에 도착합니다.");
				if (Total > 0) {
					msg = obtainMessage(COUNT);
					sendMessageDelayed(msg, 1000);
				}else{
					btnConfirmCode.setEnabled(false);
					cancelAction();
				}
				break;
			}
		}
	}

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(codeInput.getWindowToken(), 0);
	}
	
	protected void showFallbackDlgWithConfirm(String msg) {
		new AlertDialog.Builder(this)
				.setTitle(R.string.login_register)
				.setIcon(android.R.drawable.ic_dialog_alert).setMessage(msg)
				.setPositiveButton("예", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						//
					}
				}).show();
	}
	private KeyboardUtil ku;
	private void showKeyboard() {
		BHelper.db("showKeyboard");
		if (true) {
			ku = new KeyboardUtil(this, this, phoneInput);
			ku.setShowListener(new KeyboardUtil.onShowCloseListener() {
				@Override
				public void show() {

				}

				@Override
				public void onPush() {

				}

				@Override
				public void close() {

				}
			});
		}
		if (ku.showKeyboard()) {
			ku.startShow();
		}
	}

	public void disableShowSoftInput(EditText editText) {
		if (android.os.Build.VERSION.SDK_INT <= 10) {
			editText.setInputType(InputType.TYPE_NULL);
		} else {
			Class<EditText> cls = EditText.class;
			Method method;
			try {
				method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
				method.setAccessible(true);
				method.invoke(editText, false);
			} catch (Exception e) {
				// TODO: handle exception
			}

			try {
				method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
				method.setAccessible(true);
				method.invoke(editText, false);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
