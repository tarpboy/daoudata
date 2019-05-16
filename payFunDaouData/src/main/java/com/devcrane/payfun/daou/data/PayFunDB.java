package com.devcrane.payfun.daou.data;

import java.io.IOException;

import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.RootUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class PayFunDB {
	public static SQLiteDatabase mDB = null;
	public static String DBExtension = ".db";

	public static boolean IsReadyDB() {
		if (mDB != null) {
			if (mDB.isOpen() == true) // Database is already opened
			{
				return true;
			}
		}

		String fullDbPath = DatabaseHelper.DB_PATH + DatabaseHelper.DB_NAME + DBExtension;

		try {
			mDB = SQLiteDatabase.openDatabase(fullDbPath, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		} catch (Exception ex) {
			ex.printStackTrace();
			BHelper.db("There is no valid dictionary database " + DatabaseHelper.DB_NAME + " at path " + DatabaseHelper.DB_PATH);
			return false;
		}

		if (mDB == null) {
			return false;
		}
		BHelper.db("Database " + DatabaseHelper.DB_NAME + " is opened!");

		return true;

	}


	public static boolean InitializeDB(Context context) {
		boolean result = true;
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		try {
			if(!StaticData.IS_FOR_ROOTED &&  RootUtils.isDeviceRooted()){
				dbHelper.deleteDatabase();
				BHelper.db("db is deleted!");
			}else{
				dbHelper.createDataBase();
				IsReadyDB();
			}
			
		} catch (IOException iex) {
			result = false;
			throw new Error("Unable to create database");
		}

		return result;
	}
}
