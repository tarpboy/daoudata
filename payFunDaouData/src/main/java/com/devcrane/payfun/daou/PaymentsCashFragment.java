package com.devcrane.payfun.daou;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.helper.StringUtil;

import com.devcrane.payfun.cardreader.EmvUtils;
import com.devcrane.payfun.daou.caller.ResPara;
import com.devcrane.payfun.daou.customkeypad.KeyboardUtil;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.EncPayInfo;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.DateHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.ObServerHelper;
import com.devcrane.payfun.daou.utility.PaymentTask;
import com.devcrane.payfun.daou.utility.VanHelper;
import com.devcrane.payfun.daou.van.CashReceipt;
import com.devcrane.payfun.daou.van.DaouData;
import com.devcrane.payfun.daou.van.DaouDataContants;

import java.lang.reflect.Method;


public class PaymentsCashFragment extends PaymentsFragment {

	ImageButton select1, select2, select3, select4, select5;
	private TextView tvAmount, tvServiceTax, tvSum, tvCoupon;
	static Activity at;
	ReceiptEntity receiptEntity;
	String sVanName;
	String sTypeSub = "현금매출";
	LinearLayout linear;
	boolean notRequest = false;
	boolean showRecipt = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_cash, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
//		checkEmvCard();

	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isCash = true;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		BHelper.setTypeface(getView());
		at = getActivity();
		initView();
	}

	private void initView() {
		onInitView();
		select1 = (ImageButton) at.findViewById(R.id.select1);
		select2 = (ImageButton) at.findViewById(R.id.select2);
		select3 = (ImageButton) at.findViewById(R.id.select3);
		select4 = (ImageButton) at.findViewById(R.id.select4);
		select5 = (ImageButton) at.findViewById(R.id.select5);
		select1.setOnClickListener(onclick);
		select2.setOnClickListener(onclick);
		select3.setOnClickListener(onclick);
		select4.setOnClickListener(onclick);
		select5.setOnClickListener(onclick);
		edTAmount = (EditText) at.findViewById(R.id.edCashTAmount);
//		edTAmount.setOnFocusChangeListener(focusChangeListener);
//		edTAmount.setOnKeyListener(kl);

		edTAmount.addTextChangedListener(tAmounWatcher);
		disableShowSoftInput(edTAmount);
		edTAmount.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showKeyboard(edTAmount);
				return false;
			}
		});
		disableShowSoftInput(txtCard);
//		txtCard.setOnFocusChangeListener(focusChangeListener);
		txtCard.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				showKeyboard(txtCard);
				return false;
			}
		});
		tvAmount = (TextView) at.findViewById(R.id.tvCashAmount);
		tvTax = (TextView) at.findViewById(R.id.tvCashTax);
		tvServiceTax = (TextView) at.findViewById(R.id.tvCashService);
		tvCoupon = (TextView) at.findViewById(R.id.tvCashCoupon);
		tvSum = (TextView) at.findViewById(R.id.tvCashSum);
		linear = (LinearLayout) at.findViewById(R.id.loCashService);
		select3.setSelected(true);
		sTypeSub="소득공제";
		btnReadCard = (Button)at.findViewById(R.id.btnReadCard);
//		btnReadCard.setOnClickListener(super.onclick);
		setReaderOnClick();
		MainActivity.setHeaderText(sTypeSub);
		loadFromCaller();
		//reset KeyinVal
	}

	View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			switch (v.getId()){
				case R.id.edCashTAmount:
				case R.id.txtCard:
					if(!hasFocus)
					KeyboardUtil.getInstance(at,at).hideKeyboard();
					break;
				default:
					break;
			}
		}
	};
	void setReaderOnClick(){
		btnReadCard.setBackgroundResource(R.drawable.button_confirm_selector);
		btnReadCard.setOnClickListener(super.onclick);
	}
	private OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			showRecipt = true;
			notRequest = false;
			txtCard.setHint(R.string.hint_txtcard_cash);
			try {
				if (edTAmount != null)
					edTAmount.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ImageButton[] selects = { select1, select2, select3, select4, select5 };
			for (ImageButton ra : selects) {
				if (v.getId() == ra.getId()) {
					ra.setSelected(true);
				} else
					ra.setSelected(false);
			}
			if (v.getId() == select1.getId()) {
				btnReadCard.setOnClickListener(null);
				btnReadCard.setBackgroundResource(R.drawable.button_cancel_selector);
				notRequest = true;
				showRecipt = false;
				txtCard.setHint(R.string.hint_txtcard_cash);
				sTypeSub = "현금매출";
				if(getCardNo().equals("0100001234")){
					mTrack2 = "";
					txtCard.setText("");
				}
			} else if (v.getId() == select2.getId()) {
				btnReadCard.setOnClickListener(null);
				btnReadCard.setBackgroundResource(R.drawable.button_cancel_selector);
				txtCard.setHint(R.string.hint_txtcard_cash);
				notRequest = true;
				showRecipt = true;
				sTypeSub = "일반영수증";
				if(getCardNo().equals("0100001234")){
					mTrack2 = "";
					txtCard.setText("");
				}
			} else if (v.getId() == select3.getId()) {
				setReaderOnClick();
				notRequest = false;
				showRecipt = true;
				sTypeSub = "소득공제";
				mTrack2 = "";
				if(getCardNo().equals("0100001234")){
					mTrack2 = "";
					txtCard.setText("");
				}
			} else if (v.getId() == select4.getId()) {
				setReaderOnClick();
				sTypeSub = "사업자";
				notRequest = false;
				showRecipt = true;
				if(getCardNo().equals("0100001234")){
					mTrack2 = "";
					txtCard.setText("");
				}
			} else if (v.getId() == select5.getId()) {
				setReaderOnClick();
				notRequest = false;
				showRecipt = true;
				sTypeSub = "자진발급";
				mTrack2 = "";
				txtCard.setText("0100001234");
			}else if(v.getId()==btnReader.getId()){
				BHelper.db("click by Read");
			}

			MainActivity.setHeaderText(sTypeSub);
		}
	};
	protected String getKeyinCardNo() {
		try{
			String keyin =txtCard.getText().toString();
			if(!keyin.equals("")&& StringUtil.isNumeric(keyin.replace("-", ""))){
				CardInputMethod = DaouDataContants.VAL_WCC_KEYIN;
				return keyin;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			return "";
		}
		return "";
	}
	private void doPaymentCash() {

		updateDialogMsg(R.string.msg_pay_step_2_make_packet);
		String tAmount = edTAmount.getText().toString().replace(",", "");

		StaticData.PaymentSuccess = StaticData.PaymentSuccess_Cash;
		sVanName = comEntity.getF_VanName();
		BHelper.db("Company:" + comEntity.toString());
		String point = "0";
		if (!sPoint.equals("0")) {
			point = Helper.getPoint(sPoint, tAmount);
			tAmount = Helper.getTAmount(point, tAmount);
		}
		if (comEntity.getF_WithTax())
			receiptEntity = Helper.calWithTax(tAmount, "0", comEntity.getF_TaxRate(), comEntity.getF_ServiceTaxRate());
		else
			receiptEntity = Helper.calNoTax(tAmount, "0", comEntity.getF_TaxRate(), comEntity.getF_ServiceTaxRate());
		receiptEntity.setF_CouponDiscountRate(sPoint);
		receiptEntity.setF_CouponDiscountAmount(point);
		receiptEntity.setF_CouponID(sCouponID);
		receiptEntity.setF_CardNo(getCardNo());

		receiptEntity.setF_VanName(sVanName);
		receiptEntity.setF_StaffName(AppHelper.getCurrentUserName());
		receiptEntity.setF_CompanyNo(comEntity.getF_CompanyNo());
		receiptEntity.setF_MachineCode(comEntity.getF_MachineCode());
		receiptEntity.setF_Month("00");
		if(sTypeSub.equals("사업자"))
			receiptEntity.setF_Month("01");
		receiptEntity.setF_TypeSub(sTypeSub);
		receiptEntity.setF_Type(StaticData.paymentTypeCash);
		receiptEntity.setF_ReciptImage("");
		receiptEntity.setF_CardInputMethod(CardInputMethod);
		receiptEntity.setF_UserID(AppHelper.getCurrentUserID());
		receiptEntity.setF_RequestDate(DateHelper.getYYYYMMDD());//effect to payment dont change this
		receiptEntity.setF_ApprovalCode("");
		if (notRequest) {
			receiptEntity.setF_ApprovalCode(DateHelper.getUnique10Num());
			receiptEntity.setF_revStatus("1");
//			receiptEntity.setF_BuyerName(sTypeSub);
			receiptEntity.setF_revDate(DateHelper.getCurrentDateFull());
		}

		receiptEntity.setF_BuyerName(sTypeSub);
		payment = new CashReceipt();
		BHelper.db("notRequest:"+ notRequest + "   isShowReceipt:"+showRecipt);
		new PaymentTask(at, R.string.msg_pay_step_3_ready_send_van) {
			String cardNoToCancel = receiptEntity.getF_CardNo();
			@Override
			public String run() {
				String result = null;
				//Jonathan 171210 수정
				this.updateMsg(at.getString(R.string.msg_pay_step_4_send_van));
//				this.sendingData();
				receiptEntity.setF_BuyerName(sTypeSub);
				if (notRequest) {
					BHelper.db("keyin:"+getKeyinCardNo());
					if(getKeyinCardNo()==null || getKeyinCardNo().equals("")){
						BHelper.showToast(R.string.msg_request_payment_is_not_successful);
						return "";
					}
					String cardNo = EmvUtils.formatMaskedTrack2(getKeyinCardNo());
					receiptEntity.setF_CardNo(cardNo);
					result = VanHelper.payment(receiptEntity,true);
				} else {
					if (sVanName.equals(StaticData.vanNameDaouData)) {
						BHelper.db("receiptEntity in Cash:"+receiptEntity.toString());
						StaticData.KeyInVal = getKeyinCardNo();
						result = payment.pay(receiptEntity,new EncPayInfo());
					}
				}
				resetCardNo();
				return result;
			}

			@Override
			public boolean res(String result) {
				BHelper.db("payment result:"+result);
				//Jonathan 171210 수정
				updateCDialog(R.drawable.progress_exiting);
//				updateDialogMsg(R.string.msg_pay_step_5_finishing);
				closeDialog();
				if(!notRequest) {
					checkNetworkResult(DaouData.getNetworkResult());
					if (DaouData.getNetworkResult().equals(DaouData.NETWORK_RESULT_NO_EOT)) {
						receiptEntity.setF_CardNo(cardNoToCancel);
						BHelper.db("receiptEntity to cancel network:" + receiptEntity.toString());
						VanHelper.setVanCancel(receiptEntity, "",true);
						return false;
					}
				}
				isClickPay = false;
				if (result != null && !result.equals("")) {
					BHelper.db("StaticData.isReadyShowReceipt():"+StaticData.isReadyShowReceipt());
					if (notRequest) {
						StaticData.sResultPayment = result;
					}
					if (StaticData.isReadyShowReceipt() && showRecipt) {

						MainActivity.setFragment(new ReceiptViewFragment());
					} else {
						makeToast(StaticData.PaymentSuccess_Cash);
					}
				}else{
//					BHelper.showToast(R.string.msg_request_payment_is_not_successful);
//					BHelper.showToast(AppHelper.getVanMsg());
//					showFallbackDlg(AppHelper.getVanMsg());
					ResPara.returnFail(at);
					resetToPayAgain();
				}
				showFallbackDlg(AppHelper.getVanMsg());
				ObServerHelper.processObserver(getActivity());
				return result != null;
			}
		};

	}

	private void setEnableButton(boolean enable) {
		btnReader.setEnabled(enable);
		btnNFC.setEnabled(enable);
		btnKeyIN.setEnabled(enable);
	}
	String amountStr="";
	String displayText ="";
	View.OnKeyListener kl = new View.OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if(event.getAction()==KeyEvent.ACTION_UP){
				switch (keyCode){
					case KeyEvent.KEYCODE_DEL:
						if(amountStr.length()>0){
							amountStr = amountStr.substring(0,amountStr.length()-1);
						}
						BHelper.db("input str:"+ amountStr);
						displayText = Helper.formatNumberExcel(amountStr);
						edTAmount.setText(displayText);
						edTAmount.setSelection(displayText.length());
						break;
					case KeyEvent.KEYCODE_0:
					case KeyEvent.KEYCODE_1:
					case KeyEvent.KEYCODE_2:
					case KeyEvent.KEYCODE_3:
					case KeyEvent.KEYCODE_4:
					case KeyEvent.KEYCODE_5:
					case KeyEvent.KEYCODE_6:
					case KeyEvent.KEYCODE_7:
					case KeyEvent.KEYCODE_8:
					case KeyEvent.KEYCODE_9:
						if(amountStr.length()>8)
							break;
						amountStr = amountStr+ (char)event.getUnicodeChar();

						displayText = Helper.formatNumberExcel(amountStr);
						edTAmount.setText(displayText);
						try{
							edTAmount.setSelection(displayText.length());
						}catch (Exception ex){
							ex.printStackTrace();
						}

						BHelper.db("input str:"+ amountStr);

						break;
					default:
						break;
				}
			}
			return false;
		}
	};
	private TextWatcher tAmounWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			boolean b = s.toString().equals("");
			String value = edTAmount.getText().toString().replace(",", "");
			edTAmount.removeTextChangedListener(tAmounWatcher);
			String str = Helper.formatNumberExcel(value);
			if(str.equals("0"))
				str = "";
			edTAmount.setText(str);
			if (!b) {
				try {
					edTAmount.setSelection(str.length());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			edTAmount.addTextChangedListener(tAmounWatcher);
		}
	};

	private void initValue() {
		linear.setVisibility(View.VISIBLE);
		if (receiptEntity == null)
			return;
		tvAmount.setText(Helper.formatNumberExcel(receiptEntity.getF_Amount()));
		tvServiceTax.setText(Helper.formatNumberExcel(receiptEntity.getF_Service()));
		tvTax.setText(Helper.formatNumberExcel(receiptEntity.getF_Tax()));
		tvCoupon.setText(Helper.formatNumberExcel(receiptEntity.getF_CouponDiscountAmount()));
		tvSum.setText(Helper.formatNumberExcel(receiptEntity.getF_TotalAmount()));

	}

	private void makeToast(String sMessage) {
		BHelper.showToast(sMessage);
//		Toast.makeText(at, sMessage, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void doCard() {

	}

	@Override
	protected void doReset() {

	}

	@Override
	protected void doConfirmPayment() {
		isIgnoreCheckDeclined =false;
		if(!AppHelper.checkLastPayment())
			return;
		AppHelper.setLastPayment();

		doPaymentCash();

	}

	@Override
	protected boolean validateCredit() {

		String tAmount = edTAmount.getText().toString().replace(",", "");
		if (comEntity == null) {
			makeToast(at.getString(R.string.please_select_company_first));
			return false;
		}
		if (tAmount == null || tAmount.length() < 0 || tAmount.equals("")) {
			makeToast(at.getString(R.string.please_input_total_amount));
			return false;
		}
		if (getCardNo().equals("")){// && !notRequest) {
			makeToast(at.getString(R.string.please_input_card_value));
			return false;
		}
		BHelper.db("CardInputMethod:"+CardInputMethod);
//		if(CardInputMethod.equals(AppDataStatic.CardInputMethodKeyIn)){
//			if(sTypeSub.equals("소득공제")|| sTypeSub.equals("사업자")){
//				BHelper.db("sTypeSub:"+sTypeSub);
//				BHelper.showToast(R.string.msg_this_type_not_available_with_key_in);
//				return false;
//			}
//		}
		return true;
	}

	private KeyboardUtil ku;

	private void showKeyboard(EditText editText) {
		BHelper.db("showKeyboard");
//		if (ku == null)
			if (true)
		{
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