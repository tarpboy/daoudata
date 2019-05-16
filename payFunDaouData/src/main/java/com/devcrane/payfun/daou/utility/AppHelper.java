package com.devcrane.payfun.daou.utility;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.devcrane.android.lib.emvreader.EmvApplication;
import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.data.PayFunDB;
import com.devcrane.payfun.daou.entity.BTReaderInfo;
import com.devcrane.payfun.daou.entity.EmvTcEntity;
import com.devcrane.payfun.daou.entity.InCompleteDataEntity;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.entity.SessionInfo;
import com.devcrane.payfun.daou.van.DaouData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.json.JSONArray;
import org.json.JSONException;

public class AppHelper extends EmvApplication {
	private static Context context;
	private static SQLiteDatabase database;

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		EmvApplication.APP_NAME = "JTNet";
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		BHelper.db("Application low memory!");
	}

	public static String getTitle(boolean isCashReceipt) {
		BHelper.db("isCashReceipt:"+isCashReceipt);
		if(isCashReceipt)
			return context.getString(R.string.title_receipt_cash);
		return context.getString(R.string.title_receipt);
	}
	private static SharedPreferences pref() {
		return context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
	}

	public static String prefGet(Object key, Object defValue) {
		String value = pref().getString(key.toString(), defValue.toString());
		BHelper.db(String.format("Pref get: %s, %s", key.toString(), value.toString()));
		return value;
	}

	public static String getSMTSignPath(){
		String path = context.getDir("", 0) + "/temp.bmp";
		return path;
	}

	public static void prefSet(Object key, Object value) {
		Editor edit = pref().edit();
		edit.putString(key.toString(), value.toString());
		BHelper.db(String.format("Pref set: %s, %s", key.toString(), value.toString()));
		edit.apply();
	}



	public static boolean prefGetBoolean(Object key, Boolean defValue) {
		boolean value = pref().getBoolean(key.toString(), defValue);
		return value;
	}
	public static void prefSeBoolean(Object key, Boolean value) {
		Editor edit = pref().edit();
		edit.putBoolean(key.toString(), value);
		edit.apply();
	}
	
	public static void setCallerReq(String value){
		prefSet("callerReqPara", value);
	}
	public static String getCallerReq(){
		String ret = String.valueOf(prefGet("callerReqPara", ""));
		return ret;
	}
	public static void resetCallerReq(){
		try{
			pref().edit().remove("callerReqPara").commit();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static void setCallerCancelRes(String value){
		prefSet("CallerCancelRes", value);
	}
	public static String getCallerCancelRes(){
		String ret = String.valueOf(prefGet("CallerCancelRes", ""));
		return ret;
	}
	public static void resetCallerCancelRes(){
		try{
			pref().edit().remove("CallerCancelRes").commit();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static void removeDataWithKey(String key){
		try{
			pref().edit().remove(key).commit();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void setNeedSignature(Boolean isNeed){
		String val="false";
		if(isNeed)
			val = "true";
		prefSet("PaymentNeedSignature", val);
	}
	public static boolean getNeedSignature(){
		String ret = String.valueOf(prefGet("PaymentNeedSignature", ""));
		if(ret.equals("true"))
			return true;
		return false;
	}
	public static void resetNeedSignature(){
		try{
			pref().edit().remove("PaymentNeedSignature").commit();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void setBase64Signature(String base64){
		prefSet("Base64Signature", base64);
	}
	public static String getBase64Signature(){
		String ret = String.valueOf(prefGet("Base64Signature", ""));
		
		return ret;
	}
	public static void resetBase64Signature(){
		try{
			pref().edit().remove("Base64Signature").commit();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void setLastPayment(){
//		String amount;
//		String card ="";
//		try{
//			card = EmvUtils.getTagFromEmvData("maskedPAN"); 
//			if(card==null || card.equals(""))
//				card="";
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		prefSet("LAST_PAYMENT_INFO", card+";"+amount+";"+DateHelper.getdateFull());
		prefSet("LAST_PAYMENT_INFO", DateHelper.getdateFull());
	}
	public static boolean checkLastPayment(){
//		String amount;
//		String card ="";
//		try{
//			card = EmvUtils.getTagFromEmvData("maskedPAN"); 
//			if(card==null || card.equals(""))
//				card="";
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
		
		String ret = String.valueOf(prefGet("LAST_PAYMENT_INFO", ""));
		if(ret.equals(""))
			return true;
//		String lastData[] = ret.split(";");
//		if(lastData.length<3)
//			return true;
//		
//		String lastCard = lastData[0];
//		String lastAmount = lastData[1];
//		BHelper.db("lastPayment:"+lastData[2]);
		Calendar currentC = Calendar.getInstance();
		Date currentDT = currentC.getTime();
		BHelper.db("currentPaymentDT:"+DateHelper.getdateFull());
//		
//		if(lastCard!= card || lastAmount!=amount)
//			return true;
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd a hh:mm:ss", DateHelper.sCountry);
		try {
//			Date lastDT = df.parse(lastData[2]);
			Date lastDT = df.parse(ret);
			
			Calendar lastC = Calendar.getInstance();
			lastC.setTime(lastDT);
			lastC.set(Calendar.SECOND, lastC.get(Calendar.SECOND) + 1);
			if(currentDT.after(lastC.getTime())){
				BHelper.db("VALID PAY REQUEST BECAUSE AFTER LAST PAYMENT MORE THAN 1(S)");
				return true;
			}
				
			else
				return false;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return false;
	}
	public static void resetLastPayment(){
		try{
			pref().edit().remove("LAST_PAYMENT_INFO").commit();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static void setVanMsg(String vanMsg){
		prefSet("VanMsg", vanMsg.trim());
	}
	public static String getVanMsg(){
		return String.valueOf(prefGet("VanMsg", ""));
		
	}
	public static void resetVanMsg(){
		try{
			pref().edit().remove("VanMsg").commit();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void setReturnToCaller(String value){
		prefSet("returnCallerAppData", value);
	}
	public static String getReturnToCaller(){
		String ret = String.valueOf(prefGet("returnCallerAppData", ""));
		return ret;
	}
	public static void resetReturnToCaller(){
		try{
			pref().edit().remove("returnCallerAppData").commit();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static SQLiteDatabase getDatabase() {
		if (database == null)
			if(PayFunDB.IsReadyDB())
				database = PayFunDB.mDB; 
		return database;
	}
	
	public static String getCompanyNo(){
		return prefGet(StaticData.COMPANY_NO, StaticData.JTNET_TEST_COMPANY_NO);
	}
	
	public static String getMachineCode(){
		return prefGet(StaticData.COMPANY_NO, StaticData.JTNET_TEST_MACHINE_CODE);
	}
	
	public static void setCurrentVolumn(){
		String value =String.valueOf(BHelper.getCurrentVolumn(context));
		prefSet("CURRENT_MEDIA_VOLUMN", value);
	}
	public static String getCurrentVolumn(){
		String ret = String.valueOf(prefGet("CURRENT_MEDIA_VOLUMN", "1"));
		return ret;
	}

	public static void setCreateShortcut(String value){
		prefSet("CREATE_SHORTCUT", value);
	}
	public static String getCreateShortcut(){
		String ret = String.valueOf(prefGet("CREATE_SHORTCUT", "false"));
		return ret;
	}

	public static void setAppSleep(String value){
		prefSet("APP_SLEEP", value);
	}
	public static String getAppSleep(){
		String ret = String.valueOf(prefGet("APP_SLEEP", "false"));
		return ret;
	}

	public static void setKeyBindingYear(){
		String currentYear = DateHelper.getYear();
			prefSet("KEY_BINDING_YEAR", currentYear);
	}

	public static String getKeyBindingYear(){
		String ret = String.valueOf(prefGet("KEY_BINDING_YEAR", "2016"));
		return ret;
	}



	public static void setSessionInfo(SessionInfo sessionInfo){
		Type type = new TypeToken<SessionInfo>(){}.getType();
		String info = new Gson().toJson(sessionInfo,type);
		prefSet("DAOU_DATA_SESSION_INFO", info);
	}
	public static SessionInfo getSessionInfo(){
		String tmpData =prefGet("DAOU_DATA_SESSION_INFO","");
		BHelper.db("sessionInfo gotten:"+tmpData);
		if(tmpData.equals(""))
			return new SessionInfo();
		Type type = new TypeToken<SessionInfo>(){}.getType();
		SessionInfo info =  (SessionInfo) new Gson().fromJson(tmpData,type);
		return info;
	}
	public static void resetSessionInfo(){
		Type type = new TypeToken<SessionInfo>(){}.getType();
		String info = new Gson().toJson(new SessionInfo(),type);
		prefSet("DAOU_DATA_SESSION_INFO",info);
	}

	public static void setReceipt(ReceiptEntity recipt){
		Type type = new TypeToken<ReceiptEntity>(){}.getType();
		String info = new Gson().toJson(recipt,type);
		prefSet("DAOU_DATA_RECEIPT_ENTITY", info);
	}
	public static ReceiptEntity getReceipt(){
		String tmpData =prefGet("DAOU_DATA_RECEIPT_ENTITY","");
		BHelper.db("receipt gotten:"+tmpData);
		if(tmpData.equals(""))
			return new ReceiptEntity();
		Type type = new TypeToken<ReceiptEntity>(){}.getType();
		ReceiptEntity info =  (ReceiptEntity) new Gson().fromJson(tmpData,type);
		return info;
	}
	public static void resetReceipt(){
		Type type = new TypeToken<ReceiptEntity>(){}.getType();
		String info = new Gson().toJson(new ReceiptEntity(),type);
		prefSet("DAOU_DATA_RECEIPT_ENTITY",info);
	}

	public static void setInCompleteData(InCompleteDataEntity incompleteData){
		Type type = new TypeToken<InCompleteDataEntity>(){}.getType();
		String info = new Gson().toJson(incompleteData,type);
		prefSet("DAOU_INCOMPLETE_DATA", info);
	}
	public static InCompleteDataEntity getInCompleteData(){
		String tmpData =prefGet("DAOU_INCOMPLETE_DATA","");
		BHelper.db("sessionInfo gotten:"+tmpData);
		if(tmpData.equals(""))
			return new InCompleteDataEntity();
		Type type = new TypeToken<InCompleteDataEntity>(){}.getType();
		InCompleteDataEntity info =  (InCompleteDataEntity) new Gson().fromJson(tmpData,type);
		return info;
	}
	public static void resetInCompleteData(){
		Type type = new TypeToken<InCompleteDataEntity>(){}.getType();
		String info = new Gson().toJson(new InCompleteDataEntity(),type);
		prefSet("DAOU_INCOMPLETE_DATA",info);
	}

	public static void setReaderType(String typeID){
		prefSet("READER_TYPE", typeID);
	}
	public static BTReaderInfo getBTReaderInfo(){
		String tmpData =prefGet("BT_READER_INFO","");
		BHelper.db("btReaderInfo gotten:"+tmpData);
		if(tmpData.equals(""))
			return new BTReaderInfo();
		Type type = new TypeToken<BTReaderInfo>(){}.getType();
		BTReaderInfo info =  (BTReaderInfo) new Gson().fromJson(tmpData,type);
		return info;
	}

	public static void setBTReaderInfo(BTReaderInfo btReader){
		Type type = new TypeToken<BTReaderInfo>(){}.getType();
		String info = new Gson().toJson(btReader,type);

		//Jonathan 수정 171122
		prefSet("BT_READER_INFO", info);
	}


	//Jonathan 추가 171122
	public static void setStringArrayPref(Context context, String key, ArrayList<String> values) {
		SharedPreferences prefs = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		JSONArray a = new JSONArray();
		for (int i = 0; i < values.size(); i++) {
			a.put(values.get(i));
		}
		if (!values.isEmpty()) {
			editor.putString(key, a.toString());
		} else {
			editor.putString(key, null);
		}
		editor.apply();
	}

	//Jonathan 추가 171122
	public static ArrayList<String> getStringArrayPref(Context context, String key) {

		SharedPreferences prefs = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		String json = prefs.getString(key, null);
		ArrayList<String> urls = new ArrayList<String>();
		if (json != null) {
			try {
				JSONArray a = new JSONArray(json);
				for (int i = 0; i < a.length(); i++) {
					String url = a.optString(i);
					urls.add(url);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return urls;


	}




	public static EmvTcEntity getEmvTcInfo(){
		String tmpData =prefGet("EMV_TC_INFO","");
		BHelper.db("emvTcInfo gotten:"+tmpData);
		if(tmpData.equals(""))
			return new EmvTcEntity();
		Type type = new TypeToken<EmvTcEntity>(){}.getType();
		EmvTcEntity info =  (EmvTcEntity) new Gson().fromJson(tmpData,type);
		return info;
	}
	public static void resetEmvTcInfo(){
		Type type = new TypeToken<EmvTcEntity>(){}.getType();
		String info = new Gson().toJson(new EmvTcEntity(),type);

		prefSet("EMV_TC_INFO", info);
	}
	public static void setEmvTcInfo(EmvTcEntity tcEntity){
		Type type = new TypeToken<EmvTcEntity>(){}.getType();
		String info = new Gson().toJson(tcEntity,type);

		prefSet("EMV_TC_INFO", info);
	}

	public static int getReaderType(){
		int ret = Integer.valueOf(prefGet("READER_TYPE", EmvReader.READER_TYPE_EARJACK));
		return ret;
	}
	public static final String VAN_IP_ADDRESS_KEY="VAN_IP_ADDRESS_KEY";
	public static final String VAN_PORT_KEY="VAN_PORT_KEY";

	public static final String DOWNLOAD_VAN_IP_ADDRESS_KEY="DOWNLOAD_VAN_IP_ADDRESS_KEY";
	public static final String DOWNLOAD_VAN_PORT_KEY="DOWNLOAD_VAN_PORT_KEY";

	public static final String APP_FCM_TOKEN="APP_FCM_TOKEN_KEY";


	public static final String DEVICE_TID="DEVICE_TID";



	//Jonathan 171205 추가
	public static void setTID(String value){
		prefSet(DEVICE_TID,value);
	}

	public static String getTID(){
		String TID = prefGet(DEVICE_TID,"");
		return TID;
	}



	public static void setVanIp(String value){
		prefSet(VAN_IP_ADDRESS_KEY,value);
	}

	public static int getVanPort(){
		String vanPort = prefGet(VAN_PORT_KEY,getDownloadVanPort());
		return Integer.valueOf(vanPort);
	}

	public static void setVanPort(String value){
		prefSet(VAN_PORT_KEY,value);
	}

	public static String getVanIp(){
		return prefGet(VAN_IP_ADDRESS_KEY,getDownloadVanIp());
	}


	/////
	public static void setDownloadVanIp(String value){
		prefSet(DOWNLOAD_VAN_IP_ADDRESS_KEY,value);
	}
	public static String getDownloadVanIp(){
		return prefGet(DOWNLOAD_VAN_IP_ADDRESS_KEY,DaouData.VAN_IP_ADDRESS);
	}
	public static int getDownloadVanPort(){
		String vanPort = prefGet(DOWNLOAD_VAN_PORT_KEY, DaouData.VAN_PORT);
		return Integer.valueOf(vanPort);
	}

	public static void setDownloadVanPort(String value){
		prefSet(DOWNLOAD_VAN_PORT_KEY,value);
	}


	public static String getFcmToken(){
		return prefGet(APP_FCM_TOKEN, "1");
	}

	public static void setFcmToken(String value){
		prefSet(APP_FCM_TOKEN,value);
	}




	public static String getMyPhoneNumber(Context context) {
		TelephonyManager telManager = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
		if(StaticData.IS_TEST){
			return StaticData.PHONE_NO_TEST;
		}
		String phoneNo = telManager.getLine1Number();
		if(phoneNo==null || phoneNo.isEmpty()){
			phoneNo = telManager.getSimSerialNumber();
		}
		return phoneNo;

	}

	public static void setCurrentUserID(String userID){
		prefSet("CURRENT_USER_ID", userID);
	}
	public static String getCurrentUserID(){
		String ret = String.valueOf(prefGet("CURRENT_USER_ID", ""));
		return ret;
	}

	public static void setUpdateUserID(String userID){
		prefSet("UPDATE_USER_ID", userID);
	}
	public static String getUpdateUserID(){
		String ret = String.valueOf(prefGet("UPDATE_USER_ID", ""));
		return ret;
	}
	static final String IS_LOGIN_KEY = "IS_LOGIN_KEY";
	public static void setIsLogin(boolean value){
		AppHelper.prefSeBoolean(IS_LOGIN_KEY, value);
	}
	public static boolean getIsLogin(){
		return AppHelper.prefGetBoolean(IS_LOGIN_KEY, false);
	}


	static final String IS_AUTO_LOGIN_KEY = "IS_AUTO_LOGIN_KEY";
	public static void setIsAutoLogin(boolean value){
		AppHelper.prefSeBoolean(IS_AUTO_LOGIN_KEY, value);
	}
	public static boolean getIsAutoLogin(){
		return AppHelper.prefGetBoolean(IS_AUTO_LOGIN_KEY, false);
	}
	static final String USERNAME ="UserNameLOGIN";
	public static void setCurrentUserName(String userName){
		prefSet(USERNAME, userName);
	}
	public static String getCurrentUserName(){
		String ret = String.valueOf(prefGet(USERNAME, ""));
		return ret;
	}
	final static String VANID = "VAN_ID";
	public static void setCurrentVanID(String vanID){
		prefSet(VANID, vanID);
	}
	public static String getCurrentVanID(){
		String ret = String.valueOf(prefGet(VANID, ""));
		return ret;
	}
}

class DBHelper extends SQLiteAssetHelper {
	private static final String DATABASE_NAME = "payfun.db";
	private static final int DATABASE_VERSION = 1;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
}