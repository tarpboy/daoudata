package com.devcrane.payfun.daou.manager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devcrane.payfun.daou.LoginFragment;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.JSonHelper;
import com.devcrane.payfun.daou.utility.MyCursor;
import com.devcrane.payfun.daou.utility.MySoap;
import com.google.gson.reflect.TypeToken;

public class CompanyManger {
	private static SQLiteDatabase db = AppHelper.getDatabase();
	private static CompanyEntity com = new CompanyEntity(0);
	public static final String TABLE = "t_Company";

	public static String insertCompanyJson(CompanyEntity comEntity) {
		Type type = new TypeToken<CompanyEntity>() {
		}.getType();
		String json = JSonHelper.serializerJson(comEntity, type);
		String[] params = { "insertCompany", json };
		return getValue(params);
	}

	private static String getValue(String[] params) {
		String sResult = null;
		try {
			Object obj = MySoap.get(params);
			if (obj != null) {
				sResult = (String) obj;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}

	public static boolean insertCompany(CompanyEntity comEntity) {
		ContentValues values = new ContentValues();
		values.put(com.getF_ID(), comEntity.getF_ID());
		values.put(com.getF_CompanyNo(), comEntity.getF_CompanyNo());
		values.put(com.getF_MachineCode(), comEntity.getF_MachineCode());
		values.put(com.getF_CompanyAddress(), comEntity.getF_CompanyAddress());
		values.put(com.getF_CompanyName(), comEntity.getF_CompanyName());
		values.put(com.getF_CompanyOwnerName(), comEntity.getF_CompanyOwnerName());
		values.put(com.getF_CompanyPhoneNo(), comEntity.getF_CompanyPhoneNo());
		values.put(com.getF_PhoneCode(), comEntity.getF_PhoneCode());
		values.put(com.getF_PhoneNo(), comEntity.getF_PhoneNo());
		values.put(com.getF_RegDate(), comEntity.getF_RegDate());
		values.put(com.getF_ResellerName(), comEntity.getF_ResellerName());
		values.put(com.getF_ResellerPhoneNo(), comEntity.getF_ResellerPhoneNo());
		values.put(com.getF_ServiceTaxRate(), comEntity.getF_ServiceTaxRate());
		values.put(com.getF_TaxRate(), comEntity.getF_TaxRate());
		values.put(com.getF_UserID(), comEntity.getF_UserID());
		values.put(com.getF_VanName(), comEntity.getF_VanName());
		values.put(com.getF_VanPhoneNo(), comEntity.getF_VanPhoneNo());
		values.put("f_WithTax", comEntity.getF_WithTax());
		values.put(com.getCREATE_UID(), comEntity.getCREATE_UID());
		values.put(com.getCREATE_DT(), comEntity.getCREATE_DT());
		values.put(com.getUPDATE_UID(), comEntity.getUPDATE_UID());
		values.put(com.getUPDATE_DT(), comEntity.getUPDATE_DT());
		if (db.replace("t_company", null, values) > 0)
			return true;
		else
			return false;
	}

//	private static List<CompanyEntity> getListCompanyJson(String userID, String pUpdate) {
//		BHelper.db("getCompanyByUser:" + userID +","+pUpdate);
//		Type type = new TypeToken<ArrayList<CompanyEntity>>() {
//		}.getType();
//		try {
//			String[] params = { "getCompanyByUser", userID, pUpdate };
//			return (ArrayList<CompanyEntity>) JSonHelper.deserializerJson2(getValue(params), type);
//		} catch (Exception e) {
//			BHelper.db("Error pase json");
//			e.printStackTrace();
//		}
//		return null;
//	}

	private static List<CompanyEntity> getListCompanyJson(String userID, String pUpdate) {
		BHelper.db("getCompanyByUser:" + userID +","+pUpdate);
		Type type = new TypeToken<ArrayList<CompanyEntity>>() {
		}.getType();
		try {
			String[] params = { "getCompanyByUser", userID, pUpdate };
			ArrayList<CompanyEntity> comList = (ArrayList<CompanyEntity>) JSonHelper.deserializerJson2(getValue(params), type);
			ArrayList<CompanyEntity> comListRet = new ArrayList<CompanyEntity>();
			for(CompanyEntity entity: comList){
				if(entity.getF_VanName().equals(StaticData.vanName))
					comListRet.add(entity);
			}
			return comListRet;
		} catch (Exception e) {
			BHelper.db("Error pase json");
			e.printStackTrace();
		}
		return null;
	}

	public static void getCompanyByUserID(String userID) {
		List<CompanyEntity> list = getListCompanyJson(userID, AppHelper.prefGet(TABLE, ""));
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				CompanyEntity comE = list.get(i);
				comE = list.get(i);
				insertCompany(comE);
				if(i==0)
					AppHelper.prefSet(TABLE, comE.getUPDATE_DT());
			}
		}
	}

	public static List<CompanyEntity> getAllCompany(String f_UserID) {
		String sqlQuery = String.format("SELECT * FROM t_company WHERE %s like ? and %s =? ", com.getF_UserID(), com.getF_VanName());
		return getListCompany(sqlQuery, new String[] { f_UserID,StaticData.vanNameDaouData});
	}
	public static boolean isExistCompanyLocal(String userID){
		List<CompanyEntity> list = getAllCompany(userID);
		if(list!=null && list.size()>0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public static List<CompanyEntity> getListCompany(String sqlQuery, String[] selectionArgs) {
		Cursor cur = null;
		try {
			cur = db.rawQuery(sqlQuery, selectionArgs);
		} catch (Exception e) {
			return new ArrayList<CompanyEntity>();
		}
		final List<CompanyEntity> list = (List<CompanyEntity>) (List<?>) new MyCursor(cur) {
			@Override
			protected Object setCursor() {
				CompanyEntity comE = new CompanyEntity();
				comE.setF_ID(get(com.getF_ID()));
				comE.setF_CompanyNo(get(com.getF_CompanyNo()));
				comE.setF_CompanyAddress(get(com.getF_CompanyAddress()));
				comE.setF_MachineCode(get(com.getF_MachineCode()));
				comE.setF_CompanyName(get(com.getF_CompanyName()));
				comE.setF_CompanyOwnerName(get(com.getF_CompanyOwnerName()));
				comE.setF_CompanyPhoneNo(get(com.getF_CompanyPhoneNo()));
				comE.setF_RegDate(get(com.getF_RegDate()));
				comE.setF_ResellerName(get(com.getF_ResellerName()));
				comE.setF_ResellerPhoneNo(get(com.getF_ResellerPhoneNo()));
				comE.setF_ServiceTaxRate(get(com.getF_ServiceTaxRate()));
				comE.setF_TaxRate(get(com.getF_TaxRate()));
				comE.setF_UserID(get(com.getF_UserID()));
				comE.setF_VanName(get(com.getF_VanName()));
				comE.setF_PhoneNo(get(com.getF_PhoneNo()));
				comE.setF_PhoneCode(get(com.getF_PhoneCode()));
				comE.setF_VanPhoneNo(get(com.getF_VanPhoneNo()));
				comE.setF_WithTax(get("f_WithTax").equals("1"));
				comE.setCREATE_DT(get(com.getCREATE_DT()));
				comE.setCREATE_UID(get(com.getCREATE_UID()));
				comE.setUPDATE_UID(get(com.getUPDATE_UID()));
				comE.setUPDATE_DT(get(com.getUPDATE_DT()));
				BHelper.db("comE:"+comE.toString());
				return comE;
			}
		}.getList();
		BHelper.db("list company:" + list.size());
		return list;
	}

	public static CompanyEntity getCompanyByID(String f_ID) {
		String sqlquery = String.format("SELECT * FROM t_company WHERE %s = ? and %s like ?", com.getF_ID(), com.getF_UserID());
		List<CompanyEntity> list = getListCompany(sqlquery, new String[] { f_ID, AppHelper.getCurrentUserID()});
		if (list.size() > 0)
			return list.get(0);
		else
			return null;
	}
	public static CompanyEntity getCompany(String f_ID) {
		String sqlquery = String.format("SELECT * FROM t_company WHERE %s = ?", com.getF_ID());
		List<CompanyEntity> list = getListCompany(sqlquery, new String[] { f_ID});
		if (list.size() > 0)
			return list.get(0);
		else
			return null;
	}
	public static CompanyEntity getCompanyByCompanyNo(String sCompanyNo) {
		String sqlquery = String.format("SELECT * FROM t_company WHERE %s = ?", com.getF_CompanyNo());
		List<CompanyEntity> list = getListCompany(sqlquery, new String[] { sCompanyNo });
		if (list.size() > 0)
			return list.get(0);
		else
			return new CompanyEntity();
	}
	public static CompanyEntity getCompany(String sCompanyNo, String machineCode) {
		String sqlquery = String.format("SELECT * FROM t_company WHERE %s = ? and %s = ?", com.getF_CompanyNo(), com.getF_MachineCode());
		List<CompanyEntity> list = getListCompany(sqlquery, new String[] { sCompanyNo,machineCode });
		if (list.size() > 0)
			return list.get(0);
		else
			return new CompanyEntity();
	}
	public static boolean isExist(String companyNo){
		boolean ret = false;
		String sqlquery = String.format("SELECT * FROM t_company WHERE %s = ?", com.getF_CompanyNo());
		List<CompanyEntity> list = getListCompany(sqlquery, new String[] { companyNo });
		if (list.size() > 0)
			ret =true;
		return ret;
	}
	public static boolean isExist(String companyNo, String machineNo){
		boolean ret = false;
		String sqlquery = String.format("SELECT * FROM t_company WHERE %s = ? and %s = ?", com.getF_CompanyNo(), com.getF_MachineCode());
		List<CompanyEntity> list = getListCompany(sqlquery, new String[] { companyNo ,machineNo});
		if (list.size() > 0)
			ret =true;
		return ret;
	}
	public static boolean delete_Company(String f_UserID) {
		if (f_UserID == null)
			return false;
		if (db.delete("t_company", "f_UserID = ?", new String[] { f_UserID }) > 0) {
			return true;
		}
		return false;

	}
	public static boolean delete_Company(String f_UserID, String  companyNo, String machineCode) {
		if (f_UserID == null)
			return false;
		if (db.delete("t_company", "f_UserID = ? and f_CompanyNo = ? and f_machineCode = ?", new String[] { f_UserID, companyNo, machineCode }) > 0) {
			return true;
		}
		return false;

	}
	
	public static String delete_CompanyJson(String userId,String companyNo,String machineNo){
		return MySoap.get(new String[] { "deleteCompany", userId, companyNo,machineNo });
	}
}
