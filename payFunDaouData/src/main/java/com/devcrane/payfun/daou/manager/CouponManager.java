package com.devcrane.payfun.daou.manager;

import java.lang.reflect.Type;
import java.util.List;

import com.devcrane.payfun.daou.entity.CouponEntity;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.JSonHelper;
import com.devcrane.payfun.daou.utility.MySoap;
import com.google.gson.reflect.TypeToken;

public class CouponManager {
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

	public static String insertCoupon(CouponEntity e) {
		Type type = new TypeToken<CouponEntity>() {
		}.getType();
		String json = JSonHelper.serializerJson(e, type);
		String[] params = { "insertCoupon", json };
		return getValue(params);
	}

	public static String updateCoupon(String sCouponID, String sUpdateID,String isActive) {
		String[] params = { "updateCoupon", sCouponID, sUpdateID,isActive};
		return getValue(params);
	}

	public static String getDisCountRateCoupon(String sCouponID) {
		String[] params = { "getDisCountRateCoupon", sCouponID };
		return getValue(params);
	}

	public static List<CouponEntity> getCouponByUser(String sUserID) {
		Type type = new TypeToken<List<CouponEntity>>() {
		}.getType();
		try {
			String[] params = { "getCouponByUser", sUserID };
			return (List<CouponEntity>) JSonHelper.deserializerJson2(getValue(params), type);
		} catch (Exception e) {
			BHelper.db("Error pase json");
			e.printStackTrace();
		}
		return null;
	}
}
