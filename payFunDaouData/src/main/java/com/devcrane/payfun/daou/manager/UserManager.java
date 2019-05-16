package com.devcrane.payfun.daou.manager;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;

import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.UserEntity;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.MyCursor;
import com.devcrane.payfun.daou.utility.MySoap;

public class UserManager {
	public static final String TABLE = "t_User";
	private static SQLiteDatabase db = AppHelper.getDatabase();
	private static UserEntity key = new UserEntity(0);

	public static int insert(UserEntity e) {
		int id = MySoap.userInsert(e);
		return id;
	}

	private static ContentValues getValues(UserEntity value) {
		ContentValues cv = new ContentValues();
		cv.put(key.getF_ID(), value.getF_ID());
		cv.put(key.getF_Email(), value.getF_Email());
		cv.put(key.getF_Password(), value.getF_Password());
		cv.put(key.getF_Name(), value.getF_Name());
		cv.put(key.getF_CompanyName(), value.getF_CompanyName());
		cv.put(key.getF_CompanyNo(), value.getF_CompanyNo());
		cv.put(key.getF_CompanyPhone(), value.getF_CompanyPhone());
		cv.put(key.getF_Address(), value.getF_Address());
		cv.put(key.getF_AddressDetail(), value.getF_AddressDetail());
		cv.put(key.getF_PartnerCode(), value.getF_PartnerCode());
		cv.put(key.getCREATE_UID(), value.getCREATE_UID());
		cv.put(key.getCREATE_DT(), value.getCREATE_DT());
		cv.put(key.getUPDATE_UID(), value.getUPDATE_UID());
		cv.put(key.getUPDATE_DT(), value.getUPDATE_DT());
		cv.put(key.getF_ParentID(), value.getF_ParentID());
		cv.put(key.getF_BranchId(), value.getF_BranchId());
		cv.put(key.getF_Status(), value.getF_Status());
		cv.put(key.getF_Mobile_NO(), value.getF_Mobile_NO());
		return cv;
	}
	
	public static String checkEmail(String email,String companyNo){
		int reSult = MySoap.getNumber(new String[] { "CheckEmail", email, companyNo });
		return String.valueOf(reSult);
	}
	public static String checkLogin(String pf_Email, String pf_Passwd) {
		pf_Passwd = BHelper.md5(pf_Passwd);
		String userID = null;
		List<UserEntity> list = MySoap.userGetLogin(pf_Email, pf_Passwd, AppHelper.prefGet(TABLE, ""));
		for (int i = 0; i < list.size(); i++) {
			UserEntity e = list.get(i);
			if (i == 0)
				AppHelper.prefSet(TABLE, e.getUPDATE_DT());
			db.replace(TABLE, null, getValues(e));
		}
		
		String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", 
				TABLE, key.getF_Email(), key.getF_Password());		
		list = getList(sql, new String[] { pf_Email, pf_Passwd });
		if (list.size() == 1){
			AppHelper.setCurrentUserName(list.get(0).getF_Name());
			return list.get(0).getF_ID();
		}
		return userID;
	}

	public static String checkLoginV1(Context ctx, String email, String password) {

		String phoneNo = AppHelper.getMyPhoneNumber(ctx);
		String deviceID = android.provider.Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);

		password = BHelper.md5(password);
		String userID = null;
		List<UserEntity> list = MySoap.userGetLoginV1(email, password, AppHelper.prefGet(TABLE, ""),phoneNo, deviceID);
		for (int i = 0; i < list.size(); i++) {
			UserEntity e = list.get(i);
//			if (i == 0)
				AppHelper.prefSet(TABLE, e.getUPDATE_DT());
			db.replace(TABLE, null, getValues(e));
			break;
		}

		String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?",
				TABLE, key.getF_Email(), key.getF_Password());
		list = getList(sql, new String[] { email, password });
		if (list.size() >= 1){
			AppHelper.setCurrentUserName(list.get(0).getF_Name());
			return list.get(0).getF_ID();
		}
		return userID;
	}

	public static String checkLoginV2(String phoneNo, String deviceID, String fcmToken) {

		String userID = null;
		List<UserEntity> list = MySoap.userGetLoginV2(phoneNo, deviceID, fcmToken);
		for (int i = 0; i < list.size(); i++) {
			UserEntity e = list.get(i);
//			if (i == 0)
//				AppHelper.prefSet(TABLE, e.getUPDATE_DT());
			db.replace(TABLE, null, getValues(e));
		}


		if (list.size() >= 1){
			AppHelper.setCurrentUserName(list.get(0).getF_Name());
			return list.get(0).getF_ID();
		}
		return userID;
	}


	public static String getName(String pf_Email, String pf_Passwd) {
		String userID = "";
		
		String sql = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?", 
				TABLE, key.getF_Email(), key.getF_Password());		
		List<UserEntity> list = getList(sql, new String[] { pf_Email, pf_Passwd });
		if (list.size() == 1)
			return list.get(0).getF_Name();
		return userID;
	}

	@SuppressWarnings("unchecked")
	private static List<UserEntity> getList(String sql, String[] selectionArgs) {
		return (List<UserEntity>) (List<?>) new MyCursor(db.rawQuery(sql, selectionArgs)) {
			@Override
			protected Object setCursor() {
				UserEntity e = new UserEntity();
				e.setF_ID(get(key.getF_ID()));
				e.setF_Email(get(key.getF_Email()));
				e.setF_Password(get(key.getF_Password()));
				e.setF_Name(get(key.getF_Name()));
				e.setF_CompanyName(get(key.getF_CompanyName()));
				e.setF_CompanyNo(get(key.getF_CompanyNo()));
				e.setF_CompanyPhone(get(key.getF_CompanyPhone()));
				e.setF_Address(get(key.getF_Address()));
				e.setF_AddressDetail(get(key.getF_AddressDetail()));
				e.setF_PartnerCode(get(key.getF_PartnerCode()));
				e.setCREATE_UID(get(key.getCREATE_UID()));
				e.setCREATE_DT(get(key.getCREATE_DT()));
				e.setUPDATE_UID(get(key.getUPDATE_UID()));
				e.setUPDATE_DT(get(key.getUPDATE_DT()));
				e.setF_ParentID(get(key.getF_ParentID()));
				e.setF_BranchId(get(key.getF_BranchId()));
				e.setF_Status(get(key.getF_Status()));
				e.setF_Mobile_NO(get(key.getF_Mobile_NO()));
				return e;
			}
		}.getList();
	}
	
	public static UserEntity getByUserID(String pf_ID) {
		BHelper.db("F_ID:"+pf_ID);
		String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE, 
				key.getF_ID());
		List<UserEntity> list = getList(sql, new String[] { pf_ID });
		if (list.size() == 1)
			return list.get(0);
		return null;
	}
	public static String getCodeConfirm(String phoneNumber){
		return MySoap.get(new String[] { "codeConfirm", phoneNumber });
	}
	
	public static String resetPassword(String email){
		return MySoap.get(new String[] { "resetPassword", email });
	}
}
