package com.devcrane.payfun.daou.manager;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.devcrane.payfun.daou.entity.UserBalanceEntity;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.MyCursor;
import com.devcrane.payfun.daou.utility.MySoap;
import com.devcrane.payfun.daou.utility.BHelper.CalendarHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class UserBalanceManager {
	public static final String TABLE = "t_User_Balance";
	private static SQLiteDatabase db = AppHelper.getDatabase();
	private static UserBalanceEntity key = new UserBalanceEntity(0);
	
	public static boolean insert(UserBalanceEntity e) {		
		int id = MySoap.userBalanceInsert(e);
		if (id > 0) {
			getListWS();
			return true;
		}
		return false;
	}
	
	public static void getListWS () {
		Type type = new TypeToken<List<UserBalanceEntity>>() {}.getType();
		List<UserBalanceEntity> list = new Gson().fromJson(MySoap.baseGet(TABLE), type);
		for (int i = 0; i < list.size(); i++) {
			UserBalanceEntity e = list.get(i);
			if (i == 0)
				AppHelper.prefSet(TABLE, e.getUPDATE_DT());
			db.replace(TABLE, null, getValues(e));
		}
	}

	private static ContentValues getValues(UserBalanceEntity value) {
		ContentValues cv = new ContentValues();
		cv.put(key.getF_ID(), value.getF_ID());
		cv.put(key.getF_UserID(), value.getF_UserID());
		cv.put(key.getF_PayDate(), value.getF_PayDate());
		cv.put(key.getF_ServiceStartDate(), value.getF_ServiceStartDate());
		cv.put(key.getF_PurchaseMonthNo(), value.getF_PurchaseMonthNo());
		cv.put(key.getF_ServiceBeExpiredDate(), value.getF_ServiceBeExpiredDate());
		cv.put(key.getF_PurchaseAmount(), value.getF_PurchaseAmount());
		cv.put(key.getCREATE_UID(), value.getCREATE_UID());
		cv.put(key.getCREATE_DT(), value.getCREATE_DT());
		cv.put(key.getUPDATE_UID(), value.getUPDATE_UID());
		cv.put(key.getUPDATE_DT(), value.getUPDATE_DT());
		return cv;
	}
	
	@SuppressWarnings("unchecked")
	private static List<UserBalanceEntity> getList(String sql, String[] selectionArgs) {
		getListWS();
		return (List<UserBalanceEntity>) (List<?>) new MyCursor(db.rawQuery(sql, selectionArgs)) {
			@Override
			protected Object setCursor() {
				UserBalanceEntity e = new UserBalanceEntity();
				e.setF_ID(get(key.getF_ID()));
				e.setF_UserID(get(key.getF_UserID()));
				e.setF_PayDate(get(key.getF_PayDate()));
				e.setF_ServiceStartDate(get(key.getF_ServiceStartDate()));
				e.setF_PurchaseMonthNo(get(key.getF_PurchaseMonthNo()));
				e.setF_ServiceBeExpiredDate(get(key.getF_ServiceBeExpiredDate()));
				e.setF_PurchaseAmount(get(key.getF_PurchaseAmount()));
				e.setCREATE_UID(get(key.getCREATE_UID()));
				e.setCREATE_DT(get(key.getCREATE_DT()));
				e.setUPDATE_UID(get(key.getUPDATE_UID()));
				e.setUPDATE_DT(get(key.getUPDATE_DT()));
				return e;
			}
		}.getList();
	}
	
	public static String getServiceStartDate(String pf_UserID) {
		String sql = String.format("SELECT * FROM %S WHERE %s = ? ORDER BY %s DESC LIMIT 1", TABLE, 
				key.getF_UserID(), key.getF_ServiceBeExpiredDate());
		List<UserBalanceEntity> list = getList(sql, new String[] { pf_UserID });		
		Calendar calendar = Calendar.getInstance();
		
		if (list.size() == 1) {
			String date = list.get(0).getF_ServiceBeExpiredDate();
			Calendar calendarNext = CalendarHelper.getCalendar(date);
			/* next date */
			if (calendarNext.after(calendar)) {
				calendarNext.set(Calendar.DATE, calendarNext.get(Calendar.DATE) + 1);
				return CalendarHelper.getDateTime(calendarNext);
			}
		}
		
		return CalendarHelper.getDateTime(calendar);
	}
}
