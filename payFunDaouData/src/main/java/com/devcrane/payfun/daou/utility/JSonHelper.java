package com.devcrane.payfun.daou.utility;

import java.lang.reflect.Type;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSonHelper {
	static final String json_Tag = "Json-Tag";

	public static String serializerJson(Object jsonObject, Type typeObject) {
		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.create();
		String jsonData = "";
		try {
			jsonData = gson.toJson(jsonObject, typeObject);
		} catch (Exception e) {
			jsonData = "";
			BHelper.db(json_Tag+": "+  e.toString());
		}
		return jsonData;
	}

	public static String serializerJson(Object jsonObject) {
		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.create();
		String jsonData = "";
		try {
			jsonData = gson.toJson(jsonObject);
		} catch (Exception e) {
			jsonData = "";
			BHelper.db(json_Tag+": " + e.toString());
		}
		return jsonData;
	}

	public static Object deserializerJson(String jsonData, Type typeData) {
		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.create();
		JSONObject j;
		Object obj = null;
		try {
			j = new JSONObject(jsonData);
			obj = gson.fromJson(j.toString(), typeData);
		} catch (Exception e) {
			obj = null;
			BHelper.db(json_Tag+": "+ e.toString());
		}
		return obj;
	}

	public static Object deserializerJson2(String jsonData, Type typeData) {
		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.create();

		Object obj = null;
		try {
			obj = gson.fromJson(jsonData, typeData);
		} catch (Exception e) {
			obj = null;
			BHelper.db(json_Tag+": "+ e.toString());
		}
		return obj;
	}
}
