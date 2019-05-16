package com.devcrane.payfun.daou.data;

import com.devcrane.android.lib.emvreader.EmvApplication;
import com.devcrane.payfun.cardreader.EmvUtils;
import com.devcrane.payfun.daou.caller.ParaConstant;
import com.devcrane.payfun.daou.caller.ReqPara;
import com.devcrane.payfun.daou.caller.ResPara;
import com.devcrane.payfun.daou.entity.UserEntity;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class StaticData extends Application {

	public static final String paymentTypeCredit = "Credit";
	public static final String CREDIT_SUBTYPE_ICC = "ICC";
	public static final String CREDIT_SUBTYPE_ICC_SWIPE = "ICC_SWIPE";
	public static final String CREDIT_SUBTYPE_ICC_UNION = "UNION";
	public static final String CREDIT_SUBTYPE_GIFT = "GIFT";

	public static final String paymentTypeCash = "Cash";
	public static final String vanNameDaouData = "DaouData";
	public static final String vanName = "DaouData";
	public static final String coCodeExample = "00-000-00000";
	public static final String cardNoExample = "XXXXXXXXXXXXXXXX";
//	public static final String Login = "Login";
	public static final String UseWifi = "UseWifi";
//	public static boolean ISLOGIN = false;
	public static boolean GETCOUPON = false;
	public static boolean GET_NFC = false;
	public static boolean IS_NFC = true;
	public static boolean IS_TEST = false;
	public static boolean IS_TEST_PROGRESS = true;
	public static boolean IS_ENABLE_LOG = true;
	public static boolean IS_FOR_ROOTED = false;
	public static final String PHONE_NO_TEST = "01038203609";
	public static final String PUSH_NOTIFICATION = "pushNotification";
	public static boolean appendNotificationMessages = true;
	public static final int NOTIFICATION_ID = 100;
	public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
	public static boolean IS_TEST_AUTO_LOGIN = 	true;

	
	public static boolean KeyCodeBack = true;
	public static final String BlueToothADD ="BlueToothADD";
	public static final String BlueToothName ="BlueToothName";
	public static final String BlueToothSDK ="BlueToothSDK";
	// //
	public static String sDay;
	public static String sResultPayment;
	public static boolean creditSuccessWithEmv = false;
	public static String sCompanyID = "0";
	public final static int REQUEST_BARCODE = 1;
	public final static String RESULT_BARCODE ="sResult";
	public final static String RESULT_ACTION ="ResultAction";
	public final static String COUPON_ID ="COUPON_ID";
	public final static String COUPON_JSON = "JSON";

	public final static int PAYMENT_REQUEST_SUCCESS = 1;
	public final static int PAYMENT_INSERT_REMOTE_SUCCESS = 2;
	public final static int PAYMENT_INSERT_LOCAL_SUCCESS = 4;

	public final static int CANCEL_REQUEST_SUCCESS = -2;
	public final static int CANCEL_UPDATE_REMOTE_SUCCESS = -3;
	public final static int CANCELCARD_SUCCESS = -4;
	public final static int CANCELCASH_SUCCESS = -5;
	public static int observerStatus = 0;
	public static boolean isAtPaymentScreen = false;
	// for payment
	public static String PaymentSuccess = "현금승인";
	public static String PaymentSuccess_Cash = "현금승인";
	public static String PaymentSuccess_Credit = "신용승인";
	public static String CancelCardSuccess = "거래승인취소";
	public static String CancelCashSuccess = "거래취소";
	public static String ErrorMessage = "";
	public static String Error = "결제진행에 문제가 있습니다. 다시 시도해 주십시요";
	public static String ErrorMessageDownLoad = "단말기 등록 실패";
	// for Cancel
	public static String RevMessage = "현금승인";

	// for Recipt
	public static String reciptLink = "http://re.payfun.kr/?nx=";
	// get regNo
	public final static String RegNo = "regNo";

	// get van f_ID
//	public final static String VANID = "VAN_ID";
	public final static String COMPANY_NO = "COMPANY_NO";
	public final static String MACHINE_CODE = "MACHINE_CODE";
	
	//CardInputMethod
	public final static String CardInputMethodNFC ="MS";
	public final static String CardInputMethodMS ="S";
	public final static String CardInputMethodKeyIn ="K";
	public final static String MainHome ="MainHome";
	public final static String MainCancelList ="MainCancelList";
	public final static String MainCancel ="MainCancel";
	public final static String MainProfile ="MainProfile";
	public final static String RightProfile ="RightProfile";
	public final static String RightHome ="RightHome";
	public final static String RightCredit ="RightCredit";
	public final static String RightCash ="RightCash";
	public final static String RightCancelList ="RightCancelList";
	public final static String RightCoupon ="RightCoupon";
	public final static String RightExtended ="RightExtende";
	public final static String CouponCancel ="CouponCancel";
	public final static String CouponOK ="CouponOK";
//	PhoneNumber
	public final static String PhoneNumber ="PhoneNumber";
	public final static int REQUEST_CONFIRM = 5;
	
//	MapView
	public final static int REQUEST_ADDRESS = 3;
	public final static String ADDRESS_RESULT ="ADDRESS_RESULT";
	public final static String DAUM_MAPS_ANDROID_APP_API_KEY = "034de1f0a4d4f4f75bc1f835bb25653e";
//	PostCode
	public final static int REQUEST_POSTCODE = 4;
	public final static String POSTCODE_RESULT ="POSTCODE_RESULT";
	
	
	//JTNet

	
	public final static String JTNET_TEST_COMPANY_NO= "124-21-37263";
	public final static String JTNET_TEST_MACHINE_CODE= "55000612";
	
	public static boolean IS_TEST_KEYBINDING = false;
	public static boolean IS_MAGNETIC_READER = false;
	
	public static String FID65_WORKING_KEY_TEST = "A1223344556677889900AABBCCDDEEFF";
	public static String FID65_MASTER_KEY_TEST = "0123456789ABCDEFFEDCBA9876543210";
	
	public static String bank_card_balance_amout="0";
	public static int TEXT_SIZE_TOAST = 20;
	public static int TEXT_SIZE_PROGRESS = 20;
	public static double SIGNATURE_AMOUNT_LIMIT = 50000;//50000;//
	
	//call from other app
	private static Boolean toExit =false;
	public static void setToExit(boolean value){
		toExit = value;
	}
	public static boolean getToExit(){
		return toExit;
	}
	private static Boolean isCalled =false;
	public static void setIsCalled(boolean value){
		isCalled = value;
	}
	public static boolean getIsCalled(){
		return isCalled;
	}
	public static final String TMP_SIGN = "tmp_sign.png";
	public static String KeyInVal="";

	public static boolean isReadyShowReceipt(){
		return StaticData.sResultPayment!=null && !StaticData.sResultPayment.equals("");
	}
	public static void checkToReturnCallerApp(Activity at) {
		BHelper.db("check to return data to caller app: " + StaticData.getIsCalled());
		
		if(!StaticData.getIsCalled())
			AppHelper.resetReturnToCaller();
		String retData =AppHelper.getReturnToCaller(); 
		BHelper.db("getReturnToCaller:"+retData);
		if(StaticData.getIsCalled() && retData!=""){
			
			
			ReqPara reqPara;
			try{
				reqPara = ReqPara.fromJsonString(AppHelper.getCallerReq());
				
				if(reqPara!=null){
					
					ResPara resPara = ResPara.fromJsonString(retData);
					resPara.setMessage(AppHelper.getVanMsg());
//					resPara.setCardNo(EmvUtils.makeMaskTrack2(resPara.getCardNo()));
					resPara.setCardNo(EmvUtils.formatMaskedTrack2(resPara.getCardNo()));
					resPara.setTaxRate(reqPara.getTaxRate());
					resPara.setDivideMonth(reqPara.getDivideMonth());
					resPara.setReserve1(reqPara.getReserve1());
					resPara.setReserve2(reqPara.getReserve2());
					if(reqPara.getTransType().equals(ParaConstant.TRANS_TYPE_CANCEL)){
						String cancelData = AppHelper.getCallerCancelRes();
						String approvalNo = cancelData.split(";")[0];
						String cancelDT = cancelData.split(";")[1];
						resPara.setApprovalNo(approvalNo);
						resPara.setApprovalDT(cancelDT);
					}
					retData = ResPara.toJsonString(resPara);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			Log.d("callApp","reset data after return data to caller app");
			AppHelper.resetReturnToCaller();
			UserEntity key = new UserEntity(0);
//			AppHelper.prefSet(key.getF_ID(), "");
			//broadcast result payment to call app can receive.
			Intent i = new Intent();
			i.setAction("com.devcrane.payfun.daou");
			i.putExtra("resParaJson", retData);
			at.sendBroadcast(i);
            resetPaymentData();
//			at.setResult(Activity.RESULT_OK,i);
			at.finish();
			//exit app
			EmvApplication app = (EmvApplication)at.getApplication();
			app.stopApp();
			System.exit(0);
			System.exit(0);
		}else{
            resetPaymentData();
        }

		
	}
    static void resetPaymentData(){
        sResultPayment="";

    }
	
}
