package com.devcrane.payfun.daou;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Hashtable;

import com.bbpos.bbdevice.BBDeviceController;
import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.payfun.daou.caller.ResPara;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.dialog.CancelListDialog;
import com.devcrane.payfun.daou.dialog.CancelListDialogListener;
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
import com.devcrane.payfun.daou.van.PaymentBase;

public class CancelPaymentFragment extends PaymentsFragment {
	Activity at;
	TextView txtTotal, txtApprovaNumber, txtReqDate;
	Button btnReadCardCancel;
	public static ReceiptEntity mReceipt;
	boolean isCallCheckCard = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_cancel_credit, container, false);
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
		
		if(!StaticData.getIsCalled() && !MainActivity.isResumeOnMain){
			String connectionMode =emvReader.emvSwipeController.getConnectionMode().toString();
			if((!EmvReader.getIsBlueTooth() && !connectionMode.equals(BBDeviceController.ConnectionMode.AUDIO.toString()))||
					(EmvReader.getIsBlueTooth() && !MainActivity.isBTReaderConnected)){
				BHelper.db("device not ready:" + connectionMode);
				return;
			}

			checkEmvCard();
			BHelper.db("call checkEmvCard after returnDevice Info if caller");
		}
			
	}
	@Override
	public void onDevicePlugged() {
		// TODO Auto-generated method stub
//		super.onDevicePlugged();
		showDialog();
		emvReader.getDeviceInfo();
	}



	public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
		// TODO Auto-generated method stub
		super.onReturnDeviceInfo(deviceInfoData);
		closeDialog();
		//if(AppDataStatic.getIsCalled()){
			checkEmvCard();
		//}
	}
	View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.btnReadCardCancel:
					doReset();
					resetParas();
					checkEmvCard();
					break;
				default:
					break;
			}
		}
	};

	private void initView() {
		onInitView();
		mReceipt = null;
		isCancel = true;
		txtTotal = (TextView) at.findViewById(R.id.txtCancelTAmount);
		txtApprovaNumber = (TextView) at.findViewById(R.id.txtCancelApprovalnumber);
		txtReqDate = (TextView) at.findViewById(R.id.txtCancelReqDate);
		btnReadCardCancel = (Button)at.findViewById(R.id.btnReadCardCancel);
		btnReadCardCancel.setOnClickListener(listener);
		onSetCompany(null);
	}

	@Override
	protected void doConfirmPayment() {
		updateDialogMsg(R.string.msg_pay_step_2_make_packet);
		BHelper.db("ReceiptType:"+mReceipt.getF_Type());
		//if (mReceipt.getF_Type().equals(AppDataStatic.paymentTypeCash)) {
		new PaymentTask(at, R.string.msg_pay_step_3_ready_send_van, R.drawable.progress_ing) {

				@Override
				public String run() {
					try{
						BHelper.db("bankCardData:"+bankCardData);
						String cardNo = bankCardData;
						mReceipt.setF_CardNo(cardNo);
					}catch(Exception ex){
						ex.printStackTrace();
					}
					//Jonathan 171210 수정
					this.updateCdialog(R.drawable.progress_sending);
//					this.updateMsg(at.getString(R.string.msg_pay_step_4_send_van));
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
						ReceiptManager.uploadSign(mReceipt.getF_ID()+".png");
						ReceiptManager.renameSignature(mReceipt.getF_ID());
						MainActivity.setFragment(new ReceiptViewFragment());
					}else{
						ResPara.returnFail(at);
//						BHelper.showToast(AppHelper.getVanMsg());
					}
					BHelper.showToast(AppHelper.getVanMsg());
					ObServerHelper.processObserver(getActivity());
					resetCardNo();
					if(payTypeSub.equals(StaticData.CREDIT_SUBTYPE_ICC_SWIPE)
							||payTypeSub.equals(StaticData.CREDIT_SUBTYPE_GIFT)
//							||payTypeSub.equals(StaticData.CREDIT_SUBTYPE_BANK_CARD)
							){
					}else{
						emvReader.sendOnlineProcessResult(PaymentBase.makeEmvResponseDecline());
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
		closeDialog();
		BHelper.db("getCardNo:"+bankCardData);
		if(StaticData.getIsCalled() && !approvalNoCancel.equals("")){
			mReceipt = ReceiptManager.getReceiptByApprovalNo(approvalNoCancel);
			loadSearchedReceipt(mReceipt);
		}else{
			if(bankCardData==null || bankCardData.equals("")){
				//do nothing
			}else{


				if((payTypeSub.equals(StaticData.CREDIT_SUBTYPE_ICC_SWIPE)
						||payTypeSub.equals(StaticData.CREDIT_SUBTYPE_GIFT)
//						||payTypeSub.equals(StaticData.CREDIT_SUBTYPE_BANK_CARD)
				)&& maskTrack2!=null && !maskTrack2.equals("")){
//					mReceipt = ReceiptManager.getReceiptByCardNo(maskTrack2);
					showCancelList(maskTrack2);
				}else{
					//case ICC
					char c = bankCardData.charAt(bankCardData.length()-1);
					boolean isDigit = (c >= '0' && c <= '9');
					if(!isDigit)
						bankCardData = bankCardData.substring(0, bankCardData.length()-1);
//					mReceipt = ReceiptManager.getReceiptByCardNo(bankCardData);
					showCancelList(bankCardData);
				}

			}
		}

	}

	void loadSearchedReceipt(ReceiptEntity entity){
		mReceipt = entity;
		//check for case check card wrong way
		if (mReceipt != null && !mReceipt.getF_CardInputMethod().equals(CardInputMethod)){
			BHelper.db("check card wrong way");
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
		if(mReceipt!=null && mReceipt.getF_Type().equals(StaticData.paymentTypeCash))
			isCash =true;

		icAmount = mReceipt.getF_TotalAmount();
		txtTotal.setText(Helper.formatNumberExcel(mReceipt.getF_TotalAmount()));
		txtReqDate.setText(mReceipt.getF_RequestDate());
		txtApprovaNumber.setText(mReceipt.getF_ApprovalCode());
		closeDialog();
		resetCardNo();
		setDisplaySignature(mReceipt.getF_TotalAmount(),true);
	}

	void showCancelList(String cardNo){
		BHelper.db("cardno for search:"+cardNo);
		CancelListDialog dialog = new CancelListDialog(getActivity(), cardNo, new CancelListDialogListener() {
			@Override
			public void CancelListDialogEvent(ReceiptEntity entity) {
				loadSearchedReceipt(entity);
			}
		});
		dialog.setCancelable(false);
		dialog.show();
	}
	@Override
	protected void doReset() {
		super.doReset();
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

}
