package com.devcrane.payfun.daou.utility;

import com.devcrane.payfun.daou.entity.CompanyEntity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class TaskDownload extends AsyncTask<String, Void, CompanyEntity>{
	private Context context;
	private ProgressDialog dialog;
	private CompanyEntity mResult;
	public TaskDownload(Context context) {
		this.context = context;
		execute();
	}
	@Override
	protected CompanyEntity doInBackground(String... params) {
		try { 
			return run();
		} catch (Exception ex) {			
			BHelper.ex(ex);
			return null;
		}
		
	}
	public abstract CompanyEntity run();
	public abstract boolean res(CompanyEntity result);
	@Override
	/* Runs on the UI thread */
	protected void onPreExecute() {
		if (context != null) {
			dialog = ProgressDialog.show(context, null, "로딩중...", true);
			dialog.setCancelable(false);
		}
		super.onPreExecute();
	}
	@Override
	/* Runs on the UI thread */
	protected void onPostExecute(CompanyEntity result) {
		try {
			if (res(mResult = result))
				BHelper.db("연결 되었습니다");
//				BHelper.showToast(result ? R.string.sqlite_success : R.string.sqlite_error);
		} catch (Exception ex) {
			BHelper.ex(ex);
		} finally {
			if (dialog != null)
				dialog.dismiss();
			dialog = null;
			context = null;
		}		
		super.onPostExecute(result);
	}
	
}
