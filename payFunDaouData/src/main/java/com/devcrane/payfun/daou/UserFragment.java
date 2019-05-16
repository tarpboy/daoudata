package com.devcrane.payfun.daou;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.EmailValidator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.devcrane.payfun.daou.customkeypad.KeyboardUtil;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.BranchEntity;
import com.devcrane.payfun.daou.entity.UserEntity;
import com.devcrane.payfun.daou.manager.BranchManger;
import com.devcrane.payfun.daou.manager.UserManager;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.MySoap;
import com.devcrane.payfun.daou.utility.MyTask;
import com.devcrane.payfun.daou.utility.MyTaskRegisterUser;
import com.devcrane.payfun.daou.utility.StringTaskSetToast;

public class UserFragment extends Fragment {
	public static int typeRegister;
	private List<UserEntity> listUser;
	public static boolean bModify = false;
	private Activity at;
	private CheckBox cbPrivacyPolicy;
	private View vSearch3;
	private EditText txtSearch;
	private EditText txtBranch;
	private EditText txtInfo;
	private EditText txtName123;
	private EditText txtEmail123;
	private EditText txtPasswd123;
	private EditText txtPasswdConfirm123;
	private EditText txtCompanyNo12;
	private EditText txtCompanyName1;
	private EditText txtCompanyPhone1;
	private EditText txtAddress1;
	private EditText txtAddressDetails1;
	private EditText txtPartnerCode12;
	private Button btnRegister;
	private Button btnCancel;
	private Button btnSearch;
	private Button btnSearchtBranch;
	private UserEntity mSearchUser;
	private UserEntity mEditUser;
	private LinearLayout mLinearBranchSearch;
	private LinearLayout mLinearRegisterSearch;
	private LinearLayout layoutView;
//	private LinearLayout layoutAddress;
	List<BranchEntity> listBranch;
	private String mBranchID;
	private int mUserIDInser;
	UserEntity key = new UserEntity(0);
//	private Button btnGetAddress;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_user, container, false);
		
		btnRegister = (Button) v.findViewById(R.id.btnRegister);
		btnCancel = (Button) v.findViewById(R.id.btnCancel);
		btnSearch = (Button) v.findViewById(R.id.btnSearchRegister);
		cbPrivacyPolicy = (CheckBox) v.findViewById(R.id.cbPrivacyPolicy);
		vSearch3 = v.findViewById(R.id.viewSearch3);
		txtSearch = (EditText) v.findViewById(R.id.txtSearchRegister);
		txtInfo = (EditText) v.findViewById(R.id.txtInfo);
		txtName123 = (EditText) v.findViewById(R.id.txtName123);
		txtEmail123 = (EditText) v.findViewById(R.id.txtEmail123);
		txtPasswd123 = (EditText) v.findViewById(R.id.txtPasswd123);
		txtPasswdConfirm123 = (EditText) v.findViewById(R.id.txtPasswdConfirm123);
		txtCompanyNo12 = (EditText) v.findViewById(R.id.txtCompanyNo12);
		txtCompanyName1 = (EditText) v.findViewById(R.id.txtCompanyName1);
		txtCompanyPhone1 = (EditText) v.findViewById(R.id.txtCompanyPhone1);
		txtAddress1 = (EditText) v.findViewById(R.id.txtAddress1);
		txtAddressDetails1 = (EditText) v.findViewById(R.id.txtAddressDetails1);
		txtPartnerCode12 = (EditText) v.findViewById(R.id.txtPartnerCode12);
		txtBranch = (EditText) v.findViewById(R.id.txtBranchID);
		btnSearchtBranch = (Button) v.findViewById(R.id.btnSearchBranchID);
		mLinearBranchSearch = (LinearLayout) v.findViewById(R.id.linearSearchBranch);
		mLinearRegisterSearch = (LinearLayout) v.findViewById(R.id.linearSearchRegister);
		layoutView = (LinearLayout) v.findViewById(R.id.layoutView);
//		layoutAddress = (LinearLayout) v.findViewById(R.id.linearAddress1);
//		btnGetAddress = (Button) v.findViewById(R.id.getAddress);
		

		String[] registers = getResources().getStringArray(R.array.register_arrays);
		mBranchID = "00001";
		if (typeRegister == R.id.btnSignup1) {
			bModify = false;
			btnCancel.setVisibility(View.GONE);
			btnRegister.setVisibility(View.INVISIBLE);
			btnRegister.setText(registers[0]);
			vSearch3.setVisibility(View.GONE);
//			txtPartnerCode12.setVisibility(View.GONE);
//			txtCompanyNo12.setVisibility(View.GONE);
			txtCompanyName1.setVisibility(View.GONE);
			txtCompanyPhone1.setVisibility(View.GONE);
			txtAddress1.setVisibility(View.GONE);
			txtAddressDetails1.setVisibility(View.GONE);
			txtPartnerCode12.setVisibility(View.GONE);
			mLinearBranchSearch.setVisibility(View.GONE);
//			layoutAddress.setVisibility(View.GONE);
		} else if (typeRegister == R.id.btnSignup2) {
			MainActivity.setHeaderText(R.string.title_no_string);
			bModify = false;
			btnCancel.setVisibility(View.GONE);
			btnRegister.setVisibility(View.INVISIBLE);
			btnRegister.setText(registers[1]);
			vSearch3.setVisibility(View.GONE);
			txtCompanyName1.setVisibility(View.GONE);
			txtCompanyPhone1.setVisibility(View.GONE);
			txtAddress1.setVisibility(View.GONE);
			txtAddressDetails1.setVisibility(View.GONE);
//			layoutAddress.setVisibility(View.GONE);
		} else if (typeRegister == R.id.btnSignup3) {
			bModify = false;
			MainActivity.setHeaderText(R.string.title_register_user);
			btnCancel.setVisibility(View.GONE);
			btnRegister.setVisibility(View.INVISIBLE);
			btnRegister.setText(registers[2]);
			txtCompanyNo12.setVisibility(View.GONE);
			txtCompanyName1.setVisibility(View.GONE);
			txtCompanyPhone1.setVisibility(View.GONE);
			txtAddress1.setVisibility(View.GONE);
			txtAddressDetails1.setVisibility(View.GONE);
			txtPartnerCode12.setVisibility(View.GONE);
			mLinearBranchSearch.setVisibility(View.GONE);
//			layoutAddress.setVisibility(View.GONE);
		} else if (bModify = typeRegister == R.id.btnUserModify) {
			// btnRegister.setText(registers[3]);
			txtPartnerCode12.setVisibility(View.GONE);
			mLinearBranchSearch.setVisibility(View.GONE);
			txtBranch.setVisibility(View.GONE);
			vSearch3.setVisibility(View.GONE);
			MainActivity.setHeaderText(R.string.title_user_edit);
		}
		BHelper.setTypeface(getView());
		initComponent();

		if (!bModify) {
			if (StaticData.IS_TEST) {
				txtName123.setText("qts");
				txtEmail123.setText("qts@payfunr.com");
				txtPasswd123.setText("qts1234");
				txtPasswdConfirm123.setText("qts1234");
				txtCompanyNo12.setText("31224600959");
				txtCompanyName1.setText("QTSoftware");
				txtCompanyPhone1.setText("0123456789");
				txtAddress1.setText("HCM City");
				txtAddressDetails1.setText("14 Tran Nao");
				txtPartnerCode12.setText("1204");
				txtBranch.setText("Test");
				mBranchID = "000000";
			}
		} else {
			txtEmail123.setEnabled(false);
			txtCompanyNo12.setEnabled(false);
			txtCompanyName1.setEnabled(false);
			cbPrivacyPolicy.setText(R.string.privacy_policy_edit);
			mEditUser = UserManager.getByUserID(AppHelper.getCurrentUserID());
			BHelper.db("mEditUser:"+mEditUser.toString());
			if (mEditUser != null) {
				txtName123.setText(mEditUser.getF_Name());
				txtEmail123.setText(mEditUser.getF_Email());
				txtPasswd123.setText("");
				txtPasswdConfirm123.setText("");
				txtCompanyNo12.setText(mEditUser.getF_CompanyNo());
				txtCompanyName1.setText(mEditUser.getF_CompanyName());
				txtCompanyPhone1.setText(mEditUser.getF_CompanyPhone());
				txtAddress1.setText(mEditUser.getF_Address());
				txtAddressDetails1.setText(mEditUser.getF_AddressDetail());
				txtPartnerCode12.setText(mEditUser.getF_PartnerCode());
				mBranchID = mEditUser.getF_BranchId();
			}
		}
		new MyTask(at) {

			@Override
			public boolean run() {
				listBranch = BranchManger.getAllBranch();
				return listBranch != null;
			}

			@Override
			public boolean res(boolean result) {

				return false;
			}
		};
		btnSearchtBranch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String mBranchName = txtBranch.getText().toString();
				for (BranchEntity mBEntity : listBranch) {
					if (mBranchName.equals(mBEntity.getF_BranchId())) {
						mBranchID = mBEntity.getF_BranchId();
						break;
					}
				}
				String message = getString(R.string.msg_not_exist_branch);
				if (mBranchID != "00001") {
					message = getString(R.string.msg_exist_branch);
				}
				Toast.makeText(at, message, Toast.LENGTH_LONG).show();
			}
		});

		return v;
	}


	@Override
	public void onStart() {
		super.onStart();
//		BHelper.db("onStart on UserFragment");
		BHelper.setActivity(at = getActivity());

	}

	private void initComponent() {
		// btnRegister.setVisibility(View.INVISIBLE);
		cbPrivacyPolicy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!bModify)
					btnRegister.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity main = (MainActivity) getActivity();
				main.onBackPressed();

			}
		});
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String companyNo = txtSearch.getText().toString();
				boolean isFound = false;
				if (listUser != null) {
					if (!companyNo.equals("")) {
						companyNo = (companyNo.contains("-") ? companyNo.replace("-", "") : companyNo);
						for (UserEntity muEntity : listUser) {
							if (muEntity.getF_CompanyNo().equals(companyNo)) {
								isFound = true;
								mSearchUser = muEntity;
								String str = String.format("%s: %s\n%s: %s\n%s: %s", txtCompanyName1.getHint(), mSearchUser.getF_CompanyName(), txtCompanyPhone1.getHint(), mSearchUser.getF_CompanyPhone(), txtAddress1.getHint(), mSearchUser.getF_Address());
								txtInfo.setText(str);
							}

						}
					}
				}
				if(!isFound){
					BHelper.showToast(R.string.msg_need_register_company_user_first);
					
				}
					
			}
		});
		txtCompanyNo12.addTextChangedListener(new TextWatcher() {

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
				if (!str.equals("")) {
					str = (str.contains("-") ? str.replace("-", "") : str);
					str = Helper.formatCompanyNoNew(str);
				}
				txtCompanyNo12.removeTextChangedListener(this);
				txtCompanyNo12.setText(str);
				txtCompanyNo12.setSelection(str.length());
				txtCompanyNo12.addTextChangedListener(this);

			}
		});
		disableShowSoftInput(txtCompanyNo12);
		txtCompanyNo12.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showKeyboard(txtCompanyNo12);
				return false;
			}
		});

		txtCompanyNo12.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
					KeyboardUtil.getInstance(at,at).hideKeyboard();
				else{
					KeyboardUtil.hideSoftKeyboard(txtCompanyNo12,at);
					disableShowSoftInput(txtCompanyNo12);
				}
			}
		});

		txtCompanyPhone1.addTextChangedListener(new TextWatcher() {

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
				if (!str.equals("")) {
					str = (str.contains("-") ? str.replace("-", "") : str);
					str = Helper.formatCompanyPhone(str);
				}
				txtCompanyPhone1.removeTextChangedListener(this);
				txtCompanyPhone1.setText(str);
				txtCompanyPhone1.setSelection(str.length());
				txtCompanyPhone1.addTextChangedListener(this);

			}
		});
		disableShowSoftInput(txtCompanyPhone1);
		txtCompanyPhone1.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showKeyboard(txtCompanyPhone1);
				return false;
			}
		});
		txtCompanyPhone1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
					KeyboardUtil.getInstance(at,at).hideKeyboard();
				else{
					KeyboardUtil.hideSoftKeyboard(txtCompanyPhone1,at);
					disableShowSoftInput(txtCompanyPhone1);
				}
			}
		});
		txtSearch.addTextChangedListener(new TextWatcher() {

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
				if (!str.equals("")) {
					str = (str.contains("-") ? str.replace("-", "") : str);
					str = Helper.formatCompanyNoNew(str);
				}
				txtSearch.removeTextChangedListener(this);
				txtSearch.setText(str);
				txtSearch.setSelection(str.length());
				txtSearch.addTextChangedListener(this);

			}
		});

		BHelper.requireBtn(btnRegister, new Runnable() {
			@Override
			public void run() {
				doRegister();
			}
		});
		txtBranch.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				mLinearBranchSearch.setSelected(hasFocus);

			}
		});
		txtSearch.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				mLinearRegisterSearch.setSelected(hasFocus);
			}
		});
	
//		btnGetAddress.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//				if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//					// Ask the user to enable GPS
//					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//					builder.setTitle("Location Manager");
//					builder.setMessage("Would you like to enable GPS?");
//					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//							startActivity(i);
//						}
//					});
//					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//
//						}
//					});
//					builder.create().show();
//				}else{
//					startActivityForResult(new Intent(getActivity(), MapActivity.class), StaticData.REQUEST_ADDRESS);
//				}
//
//
//			}
//		});
		 initSearch();

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		BHelper.db("onActivityResult for ");
		if (requestCode == StaticData.REQUEST_ADDRESS) {
			if (resultCode == getActivity().RESULT_OK) {
				String sResult = data.getStringExtra(StaticData.ADDRESS_RESULT);
				txtAddress1.setText(sResult);
			}
		}
	}

	private void initSearch() {
		if (typeRegister != R.id.btnSignup3) {
			return;
		}

		new MyTask(at) {
			private ArrayList<String> objects;

			@Override
			public boolean run() {
				listUser = MySoap.userGetRegister();
				return false;
			}

			@Override
			public boolean res(boolean result) {
				return false;
			}
		};
	}

	private void doRegister() {
		final UserEntity e = !bModify ? new UserEntity() : mEditUser;
		e.setF_Name(BHelper.requireTxt(txtName123, R.string.register_username_is_required));
		e.setF_Email(BHelper.requireTxt(txtEmail123, R.string.register_email_is_required));
		e.setF_Password(BHelper.requireTxt(txtPasswd123, R.string.register_password_is_required));
		String passwdConfirm = BHelper.requireTxt(txtPasswdConfirm123, R.string.register_password_retype_is_required);
		boolean res = EmailValidator.getInstance().isValid(e.getF_Email());
		if(!res){
			BHelper.showToast(R.string.email_invalid);
			return;
		}
		
		if (BHelper.mRequire)
			return;
		if (e.getF_Password().length() < 6) {
			BHelper.showToast(R.string.msg_minmum_password_register);
			return;
		}
		if (!passwdConfirm.equals(e.getF_Password())) {
			BHelper.showToast(R.string.sqlite_confirm);
			return;
		}
		e.setF_Password(BHelper.md5(passwdConfirm));

		if (mBranchID == null) {
			BHelper.showToast(R.string.sqlite_select_branch);
			return;
		}
		e.setF_BranchId(mBranchID);
		e.setF_Mobile_NO(AppHelper.prefGet(StaticData.PhoneNumber, ""));
//		if(e.getF_ParentID() == null)
//			e.setF_ParentID("-2");

		if (typeRegister == R.id.btnSignup1 || bModify) {
			e.setF_CompanyNo(BHelper.requireTxt(txtCompanyNo12, R.string.register_companyno_is_required));
//			if (!bModify)
//				e.setF_CompanyName(BHelper.requireTxt(txtCompanyName1, R.string.register_companyname_is_required));
//			e.setF_CompanyPhone(txtCompanyPhone1.getText().toString());
//			e.setF_Address(txtAddress1.getText().toString());
//			e.setF_AddressDetail(txtAddressDetails1.getText().toString());
//			 e.setF_PartnerCode(BHelper.requireTxt(txtPartnerCode12));
			if (bModify){
				e.setF_CompanyPhone(BHelper.requireTxt(txtCompanyPhone1, R.string.register_companyphone_is_required));
				e.setF_Address(BHelper.requireTxt(txtAddress1, R.string.register_address_is_required));
				e.setF_AddressDetail(BHelper.requireTxt(txtAddressDetails1, R.string.register_addressdetails_is_required));
				e.setUPDATE_UID(AppHelper.getUpdateUserID());
			}

		} else if (typeRegister == R.id.btnSignup2) {
			e.setF_CompanyNo(BHelper.requireTxt(txtCompanyNo12, R.string.register_companyno_is_required));
			e.setF_PartnerCode(BHelper.requireTxt(txtPartnerCode12, R.string.register_partnercode_is_required));
		} else if (typeRegister == R.id.btnSignup3) {
			if (mSearchUser == null) {
				BHelper.showToast(R.string.please_select_company_first);
				txtSearch.requestFocus();
				return;
			}
			e.setF_CompanyNo(mSearchUser.getF_CompanyNo());
			e.setF_CompanyName(mSearchUser.getF_CompanyName());
			e.setF_CompanyPhone(mSearchUser.getF_CompanyPhone());
			e.setF_Address(mSearchUser.getF_Address());
			e.setF_AddressDetail(mSearchUser.getF_AddressDetail());
			e.setF_ParentID(mSearchUser.getF_ID());
			e.setF_PartnerCode(mSearchUser.getF_PartnerCode());
		}

		if (BHelper.mRequire)
			return;
		if (bModify) {
			RegisterUser(e);
		} else {
			CheckEmail(e);
		}

	}

	private void CheckEmail(final UserEntity e) {
		new StringTaskSetToast(at) {

			@Override
			public String run() {
				String sCompanyNo = e.getF_CompanyNo().trim();
				if (typeRegister == R.id.btnSignup3) {
					sCompanyNo = " ";
				}
				return UserManager.checkEmail(e.getF_Email(), sCompanyNo);
			}

			@Override
			public boolean res(String result) {
				BHelper.db("check result:"+result);
				if (result.equals("-1"))
					showToast(getString(R.string.sqlite_error));
				else if (result.equals("1")) {
					showToast(getString(R.string.msg_email_exist));
				} else if (result.equals("2")) {
					showToast(getString(R.string.msg_companyno_exist));
				} else {
					RegisterUser(e);
				}
				return false;
			}
		};
	}

	private void RegisterUser(final UserEntity e) {
		new MyTaskRegisterUser(at) {
			@Override
			public boolean run() {
				mUserIDInser = UserManager.insert(e);
				boolean bResult = mUserIDInser > 0;
				/* sync user table */
				if (bResult && bModify)
					UserManager.checkLoginV1(at,AppHelper.prefGet(key.getF_Email(), ""), AppHelper.prefGet(key.getF_Password(), ""));
				return bResult;
			}

			@Override
			public boolean res(boolean result) {
				AppHelper.prefSet(key.getF_Email(), e.getF_Email());
				AppHelper.prefSet(key.getF_Password(), BHelper.requireTxt(txtPasswd123));
				if (result && !bModify) {
					doCheckLogin(e);
				}
				return true;
			}
		};
	}

	private void doCheckLogin(UserEntity e) {
		String userID = String.valueOf(mUserIDInser);
		AppHelper.setCurrentUserName(e.getF_Name());
		AppHelper.prefSet(key.getF_ID(), userID);
		AppHelper.setCurrentUserID(userID);
		AppHelper.setIsLogin(true);
		MainActivity.setFragment(new SignUpCompleteFM());
	}
	
	
	

	// PostCode
	



	

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(txtAddress1.getWindowToken(), 0);
	}

	private KeyboardUtil ku;

	private void showKeyboard(EditText editText) {
		BHelper.db("showKeyboard");
//		if (ku == null)
			if (true) {
			ku = new KeyboardUtil(at, at, editText);
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
