package com.devcrane.payfun.daou.manager;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.DateHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.JSonHelper;
import com.devcrane.payfun.daou.utility.MyCursor;
import com.devcrane.payfun.daou.utility.MySoap;
import com.devcrane.payfun.daou.utility.SecurityHelper;
import com.google.gson.reflect.TypeToken;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ReceiptManager {
	static ReceiptEntity recE = new ReceiptEntity(0);
	private static SQLiteDatabase db = AppHelper.getDatabase();

	public static String insertRecipt_Json(ReceiptEntity receiptEntity) {
		Type type = new TypeToken<ReceiptEntity>() {
		}.getType();
		
		//encrypt data before insert DB
		receiptEntity = ReceiptEntity.encrypt(receiptEntity);
		String json = JSonHelper.serializerJson(receiptEntity, type);
		String[] params = { "insertReceipt", json };
		json = "";
		return getValue(params);
	}
	public static String updateRecipt_Json(String f_idx) {
		Log.e("Jonathan", " f_idx :: " + f_idx);
		String[] params = { "updateReceipt", f_idx};
		Log.e("Jonathan", " params :: " + params);
		Log.e("Jonathan", " getValue(params) :: " + getValue(params));
		return getValue(params);
	}
	public static String updateReceiptBefore3Month(String f_UserId) {
		String[] params = { "updateReceiptBefore3Month", f_UserId};
		return getValue(params);
	}
	private static String getValue(String[] params) {
		String sResult = null;
		try {
			Object obj = MySoap.get(params);
			Log.e("Jonathan", " obj :: " + obj);
			if (obj != null) {
				sResult = (String) obj;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sResult;
	}

	public static boolean insertReceipt(ReceiptEntity receiptEntity) {
		BHelper.db("Receipt:"+receiptEntity.toString());
		//encrypt data before insert DB
		receiptEntity = ReceiptEntity.encrypt(receiptEntity);
		ContentValues values = new ContentValues();
		values.put(recE.getF_ID(), receiptEntity.getF_ID());
		values.put(recE.getF_Amount(), receiptEntity.getF_Amount());
		values.put(recE.getF_ApprovalCode(), receiptEntity.getF_ApprovalCode());
		values.put(recE.getF_BuyerName(), receiptEntity.getF_BuyerName());
		values.put(recE.getF_CardInputMethod(), receiptEntity.getF_CardInputMethod());
		values.put(recE.getF_CardNo(), receiptEntity.getF_CardNo());
		values.put(recE.getF_CompanyNo(), receiptEntity.getF_CompanyNo());
		values.put(recE.getF_CouponDiscountAmount(), receiptEntity.getF_CouponDiscountAmount());
		values.put(recE.getF_CouponDiscountRate(), receiptEntity.getF_CouponDiscountRate());
		values.put(recE.getF_CouponID(), receiptEntity.getF_CouponID());
		values.put(recE.getF_MachineCode(), receiptEntity.getF_MachineCode());
		values.put(recE.getF_Month(), receiptEntity.getF_Month());
		values.put(recE.getF_ReceiptLink(), receiptEntity.getF_ReceiptLink());
		values.put(recE.getF_ReciptImage(), receiptEntity.getF_ReciptImage());
		values.put(recE.getF_RequestDate(), receiptEntity.getF_RequestDate());
		values.put(recE.getF_revCoCode(), receiptEntity.getF_revCoCode());
		values.put(recE.getF_revCode(), receiptEntity.getF_revCode());
		values.put(recE.getF_revDate(), receiptEntity.getF_revDate());
		values.put(recE.getF_revMessage(), receiptEntity.getF_revMessage());
		values.put(recE.getF_revSeller(), receiptEntity.getF_revSeller());
		values.put(recE.getF_revSellerName(), receiptEntity.getF_revSellerName());
		values.put(recE.getF_revStatus(), receiptEntity.getF_revStatus());
		values.put(recE.getF_Service(), receiptEntity.getF_Service());
		values.put(recE.getF_StaffName(), receiptEntity.getF_StaffName());
		values.put(recE.getF_Tax(), receiptEntity.getF_Tax());
		values.put(recE.getF_TotalAmount(), receiptEntity.getF_TotalAmount());
		values.put(recE.getF_TransUniqueCode(), receiptEntity.getF_TransUniqueCode());
		values.put(recE.getF_Type(), receiptEntity.getF_Type());
		values.put(recE.getF_TypeSub(), receiptEntity.getF_TypeSub());
		values.put(recE.getF_uMobile(), receiptEntity.getF_uMobile());
		values.put(recE.getF_UserID(), receiptEntity.getF_UserID());
		values.put(recE.getF_VanName(), receiptEntity.getF_VanName());
		if (db.replace("t_Receipt", null, values) > 0)
			return true;
		else
			return false;
	}
	
	public static ReceiptEntity getReceiptByApprovalNo(String f_ApprovalNo) {
		String sqlQuery = String.format("SELECT * FROM t_Receipt WHERE %s = ?", recE.getF_ApprovalCode());
		List<ReceiptEntity> list = getAllReceipt(sqlQuery, new String[] { f_ApprovalNo });
		if (list.size() > 0){
			//decrypt data after get from DB
			ReceiptEntity receiptEntity =list.get(0); 
			return ReceiptEntity.decrypt(receiptEntity);
		}
			
		else
			return null;
	}
	public static ReceiptEntity getReceiptByID(String f_idx) {
		String sqlQuery = String.format("SELECT * FROM t_Receipt WHERE %s = ?", recE.getF_ID());
		List<ReceiptEntity> list = getAllReceipt(sqlQuery, new String[] { f_idx });
		if (list.size() > 0){
			//decrypt data after get from DB
			ReceiptEntity receiptEntity =list.get(0); 
			return ReceiptEntity.decrypt(receiptEntity);
		}
			
		else
			return null;
	}
	
	public static ReceiptEntity getReceiptByCardNo(String sCardNo,String cardInputMethod) {
		sCardNo = SecurityHelper.encrypt(sCardNo);
		String sqlQuery = String.format("SELECT * FROM t_Receipt WHERE %s = ? AND %s = ? AND %s = 1 ORDER BY %s DESC", //
				recE.getF_CardNo(),recE.getF_CardInputMethod(), recE.getF_revStatus(), recE.getF_RequestDate());
		List<ReceiptEntity> list = getAllReceipt(sqlQuery, new String[] { sCardNo,cardInputMethod });
		BHelper.db("Size:"+list.size());
		if(list.size()>0){
			//decrypt data after get from DB
			ReceiptEntity receiptEntity =list.get(0); 
			return ReceiptEntity.decrypt(receiptEntity);
		}
		else
			return null;
	}
	
	public static ReceiptEntity getReceiptByCardNo(String sCardNo) {
		sCardNo = SecurityHelper.encrypt(sCardNo);
		String sqlQuery = String.format("SELECT * FROM t_Receipt WHERE %s = ? AND %s = 1 ORDER BY %s DESC", //
				recE.getF_CardNo(), recE.getF_revStatus(), recE.getF_RequestDate());
		List<ReceiptEntity> list = getAllReceipt(sqlQuery, new String[] { sCardNo });
		BHelper.db("Size:"+list.size());
		if(list.size()>0){
			//decrypt data after get from DB
			ReceiptEntity receiptEntity =list.get(0); 
			return ReceiptEntity.decrypt(receiptEntity);
		}
		else
			return null;
	}
	public static List<ReceiptEntity> getCancelListByCardNo(String sCardNo) {
		sCardNo = SecurityHelper.encrypt(sCardNo);
		String sqlQuery = String.format("SELECT * FROM t_Receipt WHERE %s = ? AND %s = 1 ORDER BY %s DESC", //
				recE.getF_CardNo(), recE.getF_revStatus(), recE.getF_RequestDate());
		List<ReceiptEntity> list = getAllReceipt(sqlQuery, new String[] { sCardNo });
		BHelper.db("Size:"+list.size());
		if(list.size()>0){
			return list;
		}
		else
			return new ArrayList<ReceiptEntity>();
	}

	public static List<ReceiptEntity> getReceiptByDate(String sCompayNo, String sMachineCode, String sReqDate) {
		String[] params = { "getReceiptbydate", sCompayNo, sMachineCode, sReqDate };
		Type type = new TypeToken<List<ReceiptEntity>>() {
		}.getType();
		List<ReceiptEntity> entities =(List<ReceiptEntity>) JSonHelper.deserializerJson2(getValue(params), type);  
		return ReceiptEntity.decryptList(entities);
	}
	public static List<ReceiptEntity> getReceiptByMonth(String sCompayNo, String sMachineCode, String sMonth) {
		String[] params = { "getReceiptbymonth", sCompayNo, sMachineCode, sMonth };
		Type type = new TypeToken<List<ReceiptEntity>>() {
		}.getType();
		List<ReceiptEntity> entities =(List<ReceiptEntity>) JSonHelper.deserializerJson2(getValue(params), type);  
		return ReceiptEntity.decryptList(entities);
		
	}
	

	@SuppressWarnings("unchecked")
	public static List<ReceiptEntity> getAllReceipt(String sqlQuery, String[] selectionArgs) {
		return (List<ReceiptEntity>) (List<?>) new MyCursor(db.rawQuery(sqlQuery, selectionArgs)) {
			@Override
			protected Object setCursor() {
				ReceiptEntity receiptEntity = new ReceiptEntity();
				receiptEntity.setF_ID(get(recE.getF_ID()));
				receiptEntity.setF_Amount(get(recE.getF_Amount()));
				receiptEntity.setF_ApprovalCode(get(recE.getF_ApprovalCode()));
				receiptEntity.setF_BuyerName(get(recE.getF_BuyerName()));
				receiptEntity.setF_CardInputMethod(get(recE.getF_CardInputMethod()));
				receiptEntity.setF_CardNo(get(recE.getF_CardNo()));
				receiptEntity.setF_CompanyNo(get(recE.getF_CompanyNo()));
				receiptEntity.setF_CouponDiscountAmount(get(recE.getF_CouponDiscountAmount()));
				receiptEntity.setF_CouponDiscountRate(get(recE.getF_CouponDiscountRate()));
				receiptEntity.setF_CouponID(get(recE.getF_CouponID()));
				receiptEntity.setF_MachineCode(get(recE.getF_MachineCode()));
				receiptEntity.setF_Month(get(recE.getF_Month()));
				receiptEntity.setF_ReceiptLink(get(recE.getF_ReceiptLink()));
				receiptEntity.setF_ReciptImage(get(recE.getF_ReciptImage()));
				receiptEntity.setF_RequestDate(get(recE.getF_RequestDate()));
				receiptEntity.setF_revCoCode(get(recE.getF_revCoCode()));
				receiptEntity.setF_revCode(get(recE.getF_revCode()));
				receiptEntity.setF_revDate(get(recE.getF_revDate()));
				receiptEntity.setF_revMessage(get(recE.getF_revMessage()));
				receiptEntity.setF_revSeller(get(recE.getF_revSeller()));
				receiptEntity.setF_revSellerName(get(recE.getF_revSellerName()));
				receiptEntity.setF_revStatus(get(recE.getF_revStatus()));
				receiptEntity.setF_Service(get(recE.getF_Service()));
				receiptEntity.setF_StaffName(get(recE.getF_StaffName()));
				receiptEntity.setF_Tax(get(recE.getF_Tax()));
				receiptEntity.setF_TotalAmount(get(recE.getF_TotalAmount()));
				receiptEntity.setF_TransUniqueCode(get(recE.getF_TransUniqueCode()));
				receiptEntity.setF_Type(get(recE.getF_Type()));
				receiptEntity.setF_TypeSub(get(recE.getF_TypeSub()));
				receiptEntity.setF_uMobile(get(recE.getF_uMobile()));
				receiptEntity.setF_UserID(get(recE.getF_UserID()));
				receiptEntity.setF_VanName(get(recE.getF_VanName()));
				return ReceiptEntity.decrypt(receiptEntity);
			}
		}.getList();
	}
	
	public static boolean deleteBefore3Month() {
		try{
			String dateBefore3Month = DateHelper.getDateBeforeMonthNo(3);
			if (db.delete("t_Receipt", "f_RequestDate < ?", new String[] { dateBefore3Month }) > 0) {
				return true;
			}
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		
		return false;

	}
	
	public static ReceiptEntity getReceiptBefore3Month() {
		String dateBefore3Month = DateHelper.getDateBeforeMonthNo(3);
		BHelper.db("dateBefore6Month:"+dateBefore3Month);
		String sqlQuery = String.format("SELECT * FROM t_Receipt WHERE %s < ?", //
				recE.getF_RequestDate());
		List<ReceiptEntity> list = getAllReceipt(sqlQuery, new String[] { dateBefore3Month });
		BHelper.db("Size:"+list.size());
		if(list.size()>0){
			//decrypt data after get from DB
			ReceiptEntity receiptEntity =list.get(0); 
			return receiptEntity;
		}
		else
			return new ReceiptEntity();
	}
	
	public static void writeFileFromString(String filename, String base64) {
		String[] params = {"writeFileFromString", filename, base64 };
		getValue(params);
	}
	public static void uploadSign(String fileName){
		String fileCotent = AppHelper.getBase64Signature();
		if(fileCotent.equals(""))
			return;
		writeFileFromString(fileName,fileCotent);
		
	}
	public static void renameSignature(String receiptID){
		if(!AppHelper.getNeedSignature())
			return;
		String pathsFile = Helper.getExSD() + StaticData.TMP_SIGN;
		String pathsTemp = Helper.getExSD() + receiptID + ".png";
		File file = new File(pathsFile);
		File temp = new File(pathsTemp);
		if (file.exists()) {
			file.renameTo(temp);
		}
	}
	

}
