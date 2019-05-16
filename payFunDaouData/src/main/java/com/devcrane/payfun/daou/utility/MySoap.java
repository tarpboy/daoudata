package com.devcrane.payfun.daou.utility;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.StrictMode;

import com.devcrane.payfun.daou.entity.UserBalanceEntity;
import com.devcrane.payfun.daou.entity.UserEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MySoap {
	private static final String NAMESPACE = "http://main.devcrane.com/";
//	private static final String URL = "http://dservice.payfun.kr:8080/PayFunWS?wsdl";
	private static final String URL = "http://jservice.payfun.kr:8080/PayFunWS?wsdl";
//	private static final String URL = "http://jservicetest.payfun.kr:8080/PayFunWS?wsdl";


	public static final String URL_IMG = "http://jadmin.payfun.kr:8080/resources/imagesUpload/";

	public static String get(String[] params) {
		try {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
			final String METHOD_NAME = params[0];
			final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			request.addProperty("arg0", "324");
			for (int i = 1; i < params.length; i++) {
				request.addProperty("arg" + i, params[i]);
				BHelper.db("arg" + i + ": " + params[i]);
			}
//			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//			envelope.setOutputSoapObject(request);
//			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
//			androidHttpTransport.call(SOAP_ACTION, envelope);
//			SoapPrimitive resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
//			String result = resultsRequestSOAP.toString();
//			BHelper.db(result);
			
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,180000);
//			Log.i("Dung", "SERVER:"+URLs);
//			allowAllSSL();
//			HttpsTransportSE androidHttpTransport = new HttpsTransportSE(URLs,portS,pathS,5000);
			ArrayList<HeaderProperty> headerPropertyArrayList = new ArrayList<HeaderProperty>();
			headerPropertyArrayList.add(new HeaderProperty("Connection", "close"));
			androidHttpTransport.call(SOAP_ACTION, envelope,headerPropertyArrayList);
			SoapPrimitive resultsRequestSOAP = (SoapPrimitive) envelope.getResponse();
			String result = resultsRequestSOAP.toString();
			androidHttpTransport.reset();
			BHelper.db("result: " + result);
			
			return result;
			
		} catch (Exception ex) {
			BHelper.ex(ex);
			return null;
		}
	}
	
	public static String getList(String[] params) {
		String json = get(params);
		return json == null ? "[]" : json;
	}

	public static int getNumber(String[] params) {
		try {
			String json = get(params);
			return json == null ? -1 : Integer.parseInt(json);
		} catch (Exception ex) {
			BHelper.ex(ex);
			return -1;
		}
	}
	
	public static String baseGet(String pTABLE_NAME) {
		String pUPDATE_DT = AppHelper.prefGet(pTABLE_NAME, "");
		return getList(new String[] { "BaseGet", pTABLE_NAME, pUPDATE_DT });
	}
	
	public static String getBeExpiredDate(String userID) {
		return getList(new String[] { "getBeExpiredDate", userID});
	}
	
	public static int userInsert(UserEntity e) {
		Type type = new TypeToken<UserEntity>(){}.getType();
		String json = new Gson().toJson(e, type);		
		return getNumber(new String[] { "UserInsert_2", json });
	}
	
	public static List<UserEntity> userGetLogin(String pf_Email, String pf_Passwd, String pUPDATE_DT) {
		Type type = new TypeToken<List<UserEntity>>() {}.getType();
		String json = getList(new String[] { "UserGetLogin", pf_Email, pf_Passwd, pUPDATE_DT });
		return new Gson().fromJson(json, type);
	}

	public static List<UserEntity> userGetLoginV1(String email, String password, String updateDT, String phoneNo, String deviceID) {
		Type type = new TypeToken<List<UserEntity>>() {}.getType();
		String json = getList(new String[] { "UserGetLoginV1", email, password, updateDT, phoneNo, deviceID });
		return new Gson().fromJson(json, type);
	}

	public static List<UserEntity> userGetLoginV2(String phoneNo, String deviceID, String fcmToken) {
		Type type = new TypeToken<List<UserEntity>>() {}.getType();
		String json = getList(new String[] { "UserGetLoginV2", phoneNo, deviceID, fcmToken });
		BHelper.db("userGetLoginV2:"+json);
		return new Gson().fromJson(json, type);
	}
	
	public static List<UserEntity> userGetRegister() {
		Type type = new TypeToken<List<UserEntity>>() {}.getType();
		String json = getList(new String[] { "UserGetRegister" });
		BHelper.db("UserGetRegister:"+json);
		return new Gson().fromJson(json, type);
	}
	
	public static int userBalanceInsert(UserBalanceEntity e) {
		Type type = new TypeToken<UserBalanceEntity>(){}.getType();
		String json = new Gson().toJson(e, type);		
		return getNumber(new String[] { "UserBalanceInsert", json });
	}
}
