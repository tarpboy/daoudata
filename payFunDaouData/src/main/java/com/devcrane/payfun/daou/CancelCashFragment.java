package com.devcrane.payfun.daou;

import java.util.Hashtable;

import org.jsoup.helper.StringUtil;

import com.bbpos.bbdevice.BBDeviceController;
import com.devcrane.payfun.daou.caller.ResPara;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.manager.ReceiptManager;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.ObServerHelper;
import com.devcrane.payfun.daou.utility.PaymentTask;
import com.devcrane.payfun.daou.utility.VanHelper;
import com.devcrane.payfun.daou.van.DaouData;
import com.devcrane.payfun.daou.van.DaouDataContants;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CancelCashFragment extends PaymentsFragment{
	Activity at;
	TextView txtTotal, txtApprovaNumber, txtReqDate;
	public static ReceiptEntity mReceipt;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_cancel_cash, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		at = getActivity();
		initView();
		loadFromCaller();
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isCash = true;
		
//		if(!AppDataStatic.getIsCalled()){
//			checkEmvCard();
//			BHelper.db("call checkEmvCard after returnDevice Info if caller");
//		}
			
	}

	public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
		// TODO Auto-generated method stub
		super.onReturnDeviceInfo(deviceInfoData);
//		if(AppDataStatic.getIsCalled()){
//			checkEmvCard();
//		}else{
//			closeDialog();
//		}
		closeDialog();
	}
	private void initView() {
		onInitView();
		mReceipt = null;
		isCancel = true;
		txtCard = (EditText) at.findViewById(R.id.txtCancelCard);
		txtCard.addTextChangedListener(searchCashReceipt);
		txtTotal = (TextView) at.findViewById(R.id.txtCancelTAmount);
		txtApprovaNumber = (TextView) at.findViewById(R.id.txtCancelApprovalnumber);
		txtReqDate = (TextView) at.findViewById(R.id.txtCancelReqDate);
		btnReadCard = (Button)at.findViewById(R.id.btnReadCard);
		btnReadCard.setOnClickListener(super.onclick);
		onSetCompany(null);
	}
	@Override
	public void onDevicePlugged() {
		// TODO Auto-generated method stub
//		super.onDevicePlugged();
		BHelper.db("onDevicePlugged in cancel cash");
		showDialog();
		emvReader.getDeviceInfo();
	}
	
	@Override
	protected void doConfirmPayment() {
		updateDialogMsg(R.string.msg_pay_step_2_make_packet);
		BHelper.db("ReceiptType:"+mReceipt.getF_Type());

		//if (mReceipt.getF_Type().equals(AppDataStatic.paymentTypeCash)) {
		new PaymentTask(at, R.string.msg_pay_step_3_ready_send_van) {

				@Override
				public String run() {
					try{

//						String cardNo = JTNet.makeTrack2(bankCardData);
						mReceipt.setF_CardNo(getCardNo());
						BHelper.db("receiptEntity in Cancel Cash:"+mReceipt.toString());
					}catch(Exception ex){
						ex.printStackTrace();
					}
					this.updateMsg(at.getString(R.string.msg_pay_step_4_send_van));
					return VanHelper.setVanCancel(mReceipt,getCardNo());
				}

				@Override
				public boolean res(String result) {
					//Jonathan 171210 수정
					updateCDialog(R.drawable.progress_exiting);
//					updateDialogMsg(R.string.msg_pay_step_5_finishing);
					checkNetworkResult(DaouData.getNetworkResult());
					BHelper.db("cancel result:"+result);
					if (result != null && !result.equals("")) {
						StaticData.sResultPayment = result;
						MainActivity.setFragment(new ReceiptViewFragment());
					}else{
						ResPara.returnFail(at);
						BHelper.showToast(AppHelper.getVanMsg());
					}
					showFallbackDlg(AppHelper.getVanMsg());
					ObServerHelper.processObserver(getActivity());
					resetCardNo();
					if(payTypeSub.equals(StaticData.CREDIT_SUBTYPE_ICC_SWIPE)
							||payTypeSub.equals(StaticData.CREDIT_SUBTYPE_GIFT)
//							||payTypeSub.equals(StaticData.CREDIT_SUBTYPE_BANK_CARD)
							){
					}else{
						emvReader.sendOnlineProcessResult(null);
					}
					
					closeDialog();
					return result != null;
				}
			};

	}
	void doSendVanServer(final ReceiptEntity mReEntity) {
		new PaymentTask(at) {

			@Override
			public String run() {
				
				return VanHelper.setVanCancel(mReEntity, mReEntity.getF_CardNo(),image);
			}

			@Override
			public boolean res(String result) {
				if (result != null) {
					StaticData.sResultPayment = result;
					MainActivity.setFragment(new ReceiptViewFragment());
				}
				ObServerHelper.processObserver(getActivity());
				return result != null;
			}
		};
	}
	@Override
	protected void doCard() {
		super.doCard();
		BHelper.db("getCardNo:"+bankCardData);
		if(StaticData.getIsCalled() && !approvalNoCancel.equals("")){
			mReceipt = ReceiptManager.getReceiptByApprovalNo(approvalNoCancel);	
			
		}else{
			if(bankCardData==null || bankCardData.equals("")){
				//do nothing
			}else{
				if((payTypeSub.equals(StaticData.CREDIT_SUBTYPE_ICC_SWIPE)
						||payTypeSub.equals(StaticData.CREDIT_SUBTYPE_GIFT)
//						||payTypeSub.equals(StaticData.CREDIT_SUBTYPE_BANK_CARD)
				)&& maskTrack2!=null && !maskTrack2.equals("")){
					mReceipt = ReceiptManager.getReceiptByCardNo(maskTrack2);
				}else{
					//case ICC
					char c = bankCardData.charAt(bankCardData.length()-1);
					boolean isDigit = (c >= '0' && c <= '9');
					if(!isDigit)
						bankCardData = bankCardData.substring(0, bankCardData.length()-1);
					mReceipt = ReceiptManager.getReceiptByCardNo(bankCardData);	
				}
			}
		}
		//check for case check card wrong way
		if (mReceipt != null && !mReceipt.getF_CardInputMethod().equals(CardInputMethod)){
			String resultMsg = at.getString(R.string.msg_check_card_result_wrong_way);
			resetCardNo();
			closeDialog();
			if(CardInputMethod.equals(DaouDataContants.VAL_WCC_IC))
				showFallbackDlgWithConfirm(resultMsg, BBDeviceController.CheckCardMode.SWIPE);
			else if(CardInputMethod.equals(DaouDataContants.VAL_WCC_SWIPE))
					showFallbackDlgWithConfirm(resultMsg, BBDeviceController.CheckCardMode.INSERT);
			mReceipt=null;
			return;
		}

		if (mReceipt == null||mReceipt.getF_revStatus().equals("0")){
			closeDialog();
			resetCardNo();
			BHelper.showToast(R.string.msg_search_recipt_failed);
			ResPara.returnFail(at);
			return;
		}
			
		BHelper.db("searched receipt:\n"+mReceipt.toString());
		setReceiptInfo(mReceipt);
		closeDialog();
		resetCardNo();
	}
	void setReceiptInfo(ReceiptEntity receiptEntity){
		if(receiptEntity!=null && receiptEntity.getF_Type().equals(StaticData.paymentTypeCash))
			isCash =true;
		icAmount = receiptEntity.getF_TotalAmount();
		txtTotal.setText(Helper.formatNumberExcel(receiptEntity.getF_TotalAmount()));
		txtReqDate.setText(receiptEntity.getF_RequestDate());
		txtApprovaNumber.setText(receiptEntity.getF_ApprovalCode());
	}
	@Override
	protected void doReset() {
		super.doReset();
		txtCard.setText("");
		txtApprovaNumber.setText("승인번호");
		txtReqDate.setText("거래일시");
		txtTotal.setText("0");
	}

	@Override
	protected boolean validateCredit() {
		// TODO Auto-generated method stub
		if (mReceipt == null)
			return false;
		return true;
	}
	private TextWatcher searchCashReceipt = new TextWatcher() {

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
			//search receipt
			if (s.toString().equals("")) {
				return;
			}
			
			String keyIn = s.toString();
			BHelper.db("keyIn:"+ keyIn);
			mReceipt = ReceiptManager.getReceiptByCardNo(keyIn);
			if(mReceipt!=null)
				setReceiptInfo(mReceipt);
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
}
