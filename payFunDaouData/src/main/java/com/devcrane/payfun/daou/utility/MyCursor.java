package com.devcrane.payfun.daou.utility;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public abstract class MyCursor {
	protected Cursor c;
	protected List<Object> mList;

	public MyCursor(Cursor c) {
		this.c = c;
		mList = new ArrayList<Object>();
		
		c.moveToFirst();
		while (!c.isAfterLast()) {
			mList.add(setCursor());
			c.moveToNext();
		}
		c.close();
	}

	protected abstract Object setCursor();

	protected String get(Object columnName) {
		return c.getString(c.getColumnIndex(columnName.toString()));
	}

	public List<Object> getList() {
		return mList;
	}
}