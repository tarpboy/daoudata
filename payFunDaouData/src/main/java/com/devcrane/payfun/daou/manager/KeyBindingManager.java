package com.devcrane.payfun.daou.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devcrane.payfun.daou.entity.KeyBindingEntity;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.MyCursor;

public class KeyBindingManager {
	private static SQLiteDatabase db = AppHelper.getDatabase();
	private static KeyBindingEntity com = new KeyBindingEntity(0);
	public static final String TABLE = "t_KeyBinding";
	
	public static boolean insert(KeyBindingEntity bindingEntity) {
		ContentValues values = new ContentValues();
		values.put(com.getF_ID(), bindingEntity.getF_ID());
		values.put(com.getF_CompanyNo(), bindingEntity.getF_CompanyNo());
		values.put(com.getF_MachineNo(), bindingEntity.getF_MachineNo());
		values.put(com.getF_DeviceNo(), bindingEntity.getF_DeviceNo());
		values.put(com.getF_FirmwareVersion(), bindingEntity.getF_FirmwareVersion());
		values.put(com.getF_PinKsn(), bindingEntity.getF_PinKsn());
		values.put(com.getF_TrackKsn(), bindingEntity.getF_TrackKsn());
		values.put(com.getF_EmvKsn(), bindingEntity.getF_EmvKsn());
		values.put(com.getF_Uid(), bindingEntity.getF_Uid());
		values.put(com.getF_Csn(), bindingEntity.getF_Csn());
		values.put(com.getF_ext1(), bindingEntity.getF_ext1());
		values.put(com.getF_ext2(), bindingEntity.getF_ext2());
		values.put(com.getF_ext3(), bindingEntity.getF_ext3());
		values.put(com.getCREATE_UID(), bindingEntity.getCREATE_UID());
		values.put(com.getCREATE_DT(), bindingEntity.getCREATE_DT());
		if (db.replace("t_KeyBinding", null, values) > 0)
			return true;
		else
			return false;
	}
	
	public static KeyBindingEntity getKB(String f_CompanyNo, String f_MachineNo, String f_DeviceNo) {
//		String sqlquery = String.format("SELECT * FROM t_KeyBinding WHERE %s = ? and %s = ? and %s = ? order by f_ID desc ",com.getF_CompanyNo(), com.getF_MachineNo(), com.getF_DeviceNo());
		String sqlquery = String.format("SELECT * FROM t_KeyBinding WHERE %s = ? order by f_ID desc ", com.getF_DeviceNo());
//		List<KeyBindingEntity> list = getListKB(sqlquery, new String[] {f_CompanyNo, f_MachineNo, f_DeviceNo });
		List<KeyBindingEntity> list = getListKB(sqlquery, new String[] {f_DeviceNo });
		if (list.size() > 0)
			return list.get(0);
		else
			return new KeyBindingEntity();
	}
	public static List<KeyBindingEntity> getListKB(String sqlQuery, String[] selectionArgs) {
		Cursor cur = null;
		try {
			cur = db.rawQuery(sqlQuery, selectionArgs);
		} catch (Exception e) {
			return new ArrayList<KeyBindingEntity>();
		}
		@SuppressWarnings("unchecked")
		final List<KeyBindingEntity> list = (List<KeyBindingEntity>) (List<?>) new MyCursor(cur) {
			@Override
			protected Object setCursor() {
				KeyBindingEntity comE = new KeyBindingEntity();
				comE.setF_ID(get(com.getF_ID()));
				comE.setF_CompanyNo(get(com.getF_CompanyNo()));
				comE.setF_MachineNo(get(com.getF_MachineNo()));
				comE.setF_DeviceNo(get(com.getF_DeviceNo()));
				comE.setF_FirmwareVersion(get(com.getF_FirmwareVersion()));
				comE.setF_PinKsn(get(com.getF_PinKsn()));
				comE.setF_TrackKsn(get(com.getF_TrackKsn()));
				comE.setF_EmvKsn(get(com.getF_EmvKsn()));
				comE.setF_Uid(get(com.getF_Uid()));
				comE.setF_Csn(get(com.getF_Csn()));
				comE.setCREATE_DT(get(com.getCREATE_DT()));
				comE.setCREATE_UID(get(com.getCREATE_UID()));
				comE.setF_ext1(get(com.getF_ext1()));
				comE.setF_ext2(get(com.getF_ext2()));
				comE.setF_ext3(get(com.getF_ext3()));
				return comE;
			}
		}.getList();
		BHelper.db("list KeyBindingEntity:" + list.size());
		return list;
	}
//	public static void testInsert(){
//		String pinKsn= "07504615110006000002";
//		String trackKsn= "07504615110006000002";
//		String emvKsn = "07504615110006000002";
//		String uid = "3B003A001051343036363435";
//		String csn =  "07504615110006000002000000000000";
//		KSNEntity ksnEntity = new KSNEntity(pinKsn, trackKsn, emvKsn, uid, csn);
//		EmvUtils.saveKeyBinding(ksnEntity);
//	}
	
}
