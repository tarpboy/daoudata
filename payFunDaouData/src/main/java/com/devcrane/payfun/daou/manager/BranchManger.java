package com.devcrane.payfun.daou.manager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.devcrane.payfun.daou.entity.BranchEntity;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.JSonHelper;
import com.devcrane.payfun.daou.utility.MySoap;
import com.google.gson.reflect.TypeToken;

public class BranchManger {
//	private static SQLiteDatabase db = AppHelper.getDatabase();
//	private static BranchEntity com = new BranchEntity(0);

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

	public static String insertBranchJson(BranchEntity mBranchEntity) {
		Type type = new TypeToken<BranchEntity>() {
		}.getType();
		String json = JSonHelper.serializerJson(mBranchEntity, type);
		String[] params = { "insertBranch", json };
		return getValue(params);
	}

	public static String updateBranchJson(BranchEntity mBranchEntity) {
		Type type = new TypeToken<BranchEntity>() {
		}.getType();
		String json = JSonHelper.serializerJson(mBranchEntity, type);
		String[] params = { "updateBranch", json };
		return getValue(params);
	}

	@SuppressWarnings("unchecked")
	public static List<BranchEntity> getAllBranch() {
		Type type = new TypeToken<ArrayList<BranchEntity>>() {
		}.getType();
		try {
			String[] params = { "getAllBranch" };
			return (ArrayList<BranchEntity>) JSonHelper.deserializerJson2(getValue(params), type);
		} catch (Exception e) {
			BHelper.db("Error pase json");
			e.printStackTrace();
		}
		return null;
	}

}
