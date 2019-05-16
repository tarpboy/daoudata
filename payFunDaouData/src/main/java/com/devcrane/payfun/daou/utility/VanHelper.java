package com.devcrane.payfun.daou.utility;

import android.util.Log;

import java.lang.reflect.Type;

import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.payfun.cardreader.EmvUtils;
import com.devcrane.payfun.daou.LoginFragment;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.EncPayInfo;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.manager.CouponManager;
import com.devcrane.payfun.daou.manager.ReceiptManager;
import com.devcrane.payfun.daou.van.CreditCard;
import com.devcrane.payfun.daou.van.DaouDataContants;
import com.devcrane.payfun.daou.van.IPayment;
import com.devcrane.payfun.daou.van.IPaymentEmv;
import com.devcrane.payfun.daou.van.CashReceipt;
import com.google.gson.reflect.TypeToken;

public class VanHelper {

	public static String payment(ReceiptEntity reciptEn,boolean isNoRequest) {
		String jsonData = "";
		reciptEn.setF_revStatus("1");
		StaticData.observerStatus += StaticData.PAYMENT_REQUEST_SUCCESS;
		String cardNo = reciptEn.getF_CardNo();
		reciptEn.setF_revMessage(StaticData.PaymentSuccess);
		if(reciptEn.getF_Type().equals(StaticData.paymentTypeCash))
			reciptEn.setF_BuyerName(reciptEn.getF_TypeSub());
//		if (!reciptEn.getF_CardNo().equals("") && reciptEn.getF_CardNo().length() >= 16)
//			reciptEn.setF_CardNo(cardNo.substring(0, 16));
//

		String formattedCardNo = EmvUtils.formatMaskedTrack2(cardNo);
		BHelper.db("receipt before payment:"+reciptEn.toString());
		if(isNoRequest)
			formattedCardNo = reciptEn.getF_CardNo();
		reciptEn.setF_CardNo(formattedCardNo);

		if (reciptEn.getF_CouponID()!=null && !reciptEn.getF_CouponID().equals(""))
			CouponManager.updateCoupon(reciptEn.getF_CouponID(), AppHelper.getUpdateUserID(), "0");
		String f_idx = ReceiptManager.insertRecipt_Json(reciptEn);
		if (!f_idx.equals("0")) {
			StaticData.observerStatus += StaticData.PAYMENT_INSERT_REMOTE_SUCCESS;
			reciptEn.setF_ID(f_idx);
			reciptEn.setF_ReceiptLink(StaticData.reciptLink + f_idx);
			if (reciptEn.getF_Type().equals(StaticData.paymentTypeCredit)) {
				//check to upload signature.
				ReceiptManager.uploadSign(f_idx+".png");
				ReceiptManager.renameSignature(f_idx);

			}
			reciptEn.setF_CardNo(formattedCardNo);
			if(reciptEn.getF_CardInputMethod().equals(StaticData.CardInputMethodKeyIn)){
				reciptEn.setF_CardNo(StaticData.KeyInVal);
				StaticData.KeyInVal = "";
			}
			BHelper.db("localReceipt:"+reciptEn.toString());
			BHelper.db("VanHelper-CardNo:" + f_idx);
			if (ReceiptManager.insertReceipt(reciptEn)) {
				StaticData.observerStatus += StaticData.PAYMENT_INSERT_LOCAL_SUCCESS;
				reciptEn.setF_revStatus("1");
				Type type = new TypeToken<ReceiptEntity>() {
				}.getType();
				jsonData = JSonHelper.serializerJson(reciptEn, type);
			}
		} else {
			reciptEn.setF_ID("0");
//			reciptEn.setF_CardNo(cardNo);
			reciptEn.setF_CardNo(formattedCardNo);
			if (ReceiptManager.insertReceipt(reciptEn)) {
				StaticData.observerStatus += StaticData.PAYMENT_INSERT_LOCAL_SUCCESS;
				reciptEn.setF_revStatus("1");
				Type type = new TypeToken<ReceiptEntity>() {
				}.getType();
				jsonData = JSonHelper.serializerJson(reciptEn, type);
			}
		}
		BHelper.db("JsonReciptPayment Success:" + jsonData);
		return jsonData;
	}
	public static String payment(ReceiptEntity reciptEn) {
		return payment(reciptEn, false);
	}
	public static String cancel(ReceiptEntity reciptEntity) {
		boolean isCash = false;
		if(reciptEntity.getF_Type().equals(StaticData.paymentTypeCash))
			isCash = true;
		return  cancel(reciptEntity,isCash);

	}
	public static String cancel(ReceiptEntity reciptEntity, boolean isCash) {

		String jsonData = "";
		reciptEntity.setF_revStatus("0");
		if (!reciptEntity.getF_CouponID().equals(""))
			CouponManager.updateCoupon(reciptEntity.getF_CouponID(), AppHelper.getUpdateUserID(), "1");
		StaticData.observerStatus += StaticData.CANCEL_REQUEST_SUCCESS;
		if (isCash)
			reciptEntity.setF_revMessage(StaticData.CancelCashSuccess);
		else
			reciptEntity.setF_revMessage(StaticData.CancelCardSuccess);
		BHelper.db("cancel locally for: "+reciptEntity.toString());

		String delReciptJson = ReceiptManager.updateRecipt_Json(reciptEntity.getF_ID());

		Log.e("Jonathan", "Jonathan delReciptJson: " + delReciptJson + "  getF_ID :  " + reciptEntity.getF_ID());

		if (Integer.parseInt(delReciptJson) == 1) {
			StaticData.observerStatus += StaticData.CANCEL_UPDATE_REMOTE_SUCCESS;
			if (ReceiptManager.insertReceipt(reciptEntity)) {
				if (isCash)
					StaticData.observerStatus += StaticData.CANCELCASH_SUCCESS;
				else
					StaticData.observerStatus += StaticData.CANCELCARD_SUCCESS;
				Type type = new TypeToken<ReceiptEntity>() {
				}.getType();
				jsonData = JSonHelper.serializerJson(reciptEntity, type);
			}
		}

		Log.e("Jonathan", "Jonathan reciptEntity getF_revStatus : " + reciptEntity.getF_revStatus());

		return jsonData;
	}

	public static String cancelFallback(ReceiptEntity reciptEntity, boolean isCash) {
		String jsonData = "";
		reciptEntity.setF_revStatus("0");
		if (!reciptEntity.getF_CouponID().equals(""))
			CouponManager.updateCoupon(reciptEntity.getF_CouponID(), AppHelper.getUpdateUserID(), "1");
		StaticData.observerStatus += StaticData.CANCEL_REQUEST_SUCCESS;
		if (isCash)
			reciptEntity.setF_revMessage(StaticData.CancelCashSuccess);
		else
			reciptEntity.setF_revMessage(StaticData.CancelCardSuccess);
		String delReciptJson = ReceiptManager.updateRecipt_Json(reciptEntity.getF_ID());
		if (Integer.parseInt(delReciptJson) == 1) {
			StaticData.observerStatus += StaticData.CANCEL_UPDATE_REMOTE_SUCCESS;
			if (ReceiptManager.insertReceipt(reciptEntity)) {
				if (isCash)
					StaticData.observerStatus += StaticData.CANCELCASH_SUCCESS;
				else
					StaticData.observerStatus += StaticData.CANCELCARD_SUCCESS;
				Type type = new TypeToken<ReceiptEntity>() {
				}.getType();
				jsonData = JSonHelper.serializerJson(reciptEntity, type);
			}
		}
		return jsonData;
	}

	public static String adjustCardNo(String cardNo) {
		if (cardNo.length() == 16) {
			cardNo += "=";
		}
		int cardNoLength = cardNo.length();
		for (int i = 0; i < 37 - cardNoLength; i++) {
			cardNo += " ";
		}
		return cardNo;
	}

	public static void setRequestMessageError(String message, int start, int lenghth) {
		String errorMessage = "";
		if (start > 0)
			errorMessage = Helper.getStringKRByLength(message, start, lenghth);
		else
			errorMessage = message;
		if (errorMessage == null || errorMessage.equals("")) {
			errorMessage = StaticData.Error;
		}
		StaticData.ErrorMessage = errorMessage;
		StaticData.observerStatus = -2;
	}

	public static void setRequestMessageError(String message) {

		StaticData.ErrorMessage = message;
		StaticData.observerStatus = -2;
	}
	public static String setVanCancel(ReceiptEntity mReciptE, String cardNo){
		return setVanCancel(mReciptE, cardNo,"",false);
	}
	public static String setVanCancel(ReceiptEntity mReciptE, String cardNo,boolean isNoEOT) {
		return setVanCancel(mReciptE, cardNo,"",isNoEOT);
	}
	public static String setVanCancel(ReceiptEntity mReciptE, String cardNo, String signature){
		return setVanCancel(mReciptE, cardNo,signature,false);
	}
	public static String setVanCancel(ReceiptEntity mReciptE, String cardNo, String signature, boolean isNoEOT) {
		IPaymentEmv paymentEmv;
		IPayment payment;
		String sResult = null;
		String vanName = mReciptE.getF_VanName();
		String typeRecipt = mReciptE.getF_Type();
		String mSign = signature;//mReciptE.getF_ReciptImage();
		mReciptE.setF_RequestDate(DateHelper.formatCancelDateDaouData(mReciptE.getF_RequestDate()));
		BHelper.db("do cancel in step ==> setVanCancel");
		if (vanName.equals(StaticData.vanNameDaouData)) {
			if (typeRecipt.equals(StaticData.paymentTypeCash)){
				if(isNoEOT)
					payment = new CashReceipt(DaouDataContants.TASK_NO_EOT);
				else{
					payment = new CashReceipt();
				}
				sResult = payment.cancel(mReciptE, new EncPayInfo());
			} else if(typeRecipt.equals(StaticData.paymentTypeCredit)){
				if(isNoEOT){
					paymentEmv = new CreditCard(DaouDataContants.TASK_NO_EOT);
					payment = new CreditCard(DaouDataContants.TASK_NO_EOT);
				}else{
					paymentEmv = new CreditCard();
					payment = new CreditCard();
				}

				String emvData = EmvUtils.getEmvData();
				EmvUtils.showTlv(emvData);
				EncPayInfo encPayInfo = new EncPayInfo("",emvData,mSign);
				switch (mReciptE.getF_CardInputMethod()){
					case DaouDataContants.VAL_WCC_IC:
						if(mReciptE.getF_TypeSub().equals(StaticData.CREDIT_SUBTYPE_ICC_UNION)){
							sResult = paymentEmv.cancelEmv(mReciptE, encPayInfo);
						}else
							sResult = paymentEmv.cancelEmv(mReciptE, encPayInfo);
						break;
					default:
						sResult = payment.cancel(mReciptE,encPayInfo);
						break;
				}
			}
		}
		signature = mSign = "";
		mReciptE = null;
		AppHelper.removeDataWithKey(EmvReader.EMV_DATA);
		return sResult;
	}
	public static String appenNumberZero(String sNumber, int leng) {
		for (int i = sNumber.length(); i < leng; i++) {
			sNumber = "0" + sNumber;
		}
		return sNumber;
	}
}
