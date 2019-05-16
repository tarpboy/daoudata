package com.devcrane.payfun.daou.manager;

import java.lang.reflect.Type;
import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.devcrane.payfun.daou.entity.NoticeEntity;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.MyCursor;
import com.devcrane.payfun.daou.utility.MySoap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NoticeManager {
	public static final String TABLE = "t_Notice";
	private static SQLiteDatabase db = AppHelper.getDatabase();
	private static NoticeEntity key = new NoticeEntity(0);
	
	public static void getListWS () {
		Type type = new TypeToken<List<NoticeEntity>>() {}.getType();
		List<NoticeEntity> list = new Gson().fromJson(MySoap.baseGet(TABLE), type);
		for (int i = 0; i < list.size(); i++) {
			NoticeEntity value = list.get(i);
			ContentValues cv = new ContentValues();
			cv.put(key.getF_ID(), value.getF_ID());
			cv.put(key.getF_Type(), value.getF_Type());
			cv.put(key.getF_Titile(), value.getF_Titile());
			cv.put(key.getF_Content(), value.getF_Content());
			cv.put(key.getF_IsActive(), value.getF_IsActive());
			cv.put(key.getCREATE_UID(), value.getCREATE_UID());
			cv.put(key.getCREATE_DT(), value.getCREATE_DT());
			cv.put(key.getUPDATE_UID(), value.getUPDATE_UID());
			cv.put(key.getUPDATE_DT(), value.getUPDATE_DT());
			db.replace(TABLE, null, cv);
			if (i == 0) {
				AppHelper.prefSet(TABLE, value.getUPDATE_DT());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static List<NoticeEntity> getList(String sql, String[] selectionArgs) {
//		getListWS();
		return (List<NoticeEntity>) (List<?>) new MyCursor(db.rawQuery(sql, selectionArgs)) {
			@Override
			protected Object setCursor() {
				NoticeEntity e = new NoticeEntity();
				e.setF_ID(get(key.getF_ID()));
				e.setF_Type(get(key.getF_Type()));
				e.setF_Titile(get(key.getF_Titile()));
				e.setF_Content(get(key.getF_Content()));
				e.setF_IsActive(get(key.getF_IsActive()));
				e.setCREATE_UID(get(key.getCREATE_UID()));
				e.setCREATE_DT(get(key.getCREATE_DT()));
				e.setUPDATE_UID(get(key.getUPDATE_UID()));
				e.setUPDATE_DT(get(key.getUPDATE_DT()));
				return e;
			}
		}.getList();
	}
	
	public static List<NoticeEntity> get() {
		String sql = String.format(
				"SELECT * FROM %s WHERE %s = 'true' ORDER BY %s DESC", TABLE,
				key.getF_IsActive(), key.getUPDATE_DT());
		return getList(sql, null);
	}
}
