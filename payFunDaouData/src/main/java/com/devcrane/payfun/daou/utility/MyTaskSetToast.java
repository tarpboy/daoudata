package com.devcrane.payfun.daou.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * A small callback helper class to make running callbacks in threads easier
 */
public abstract class MyTaskSetToast extends AsyncTask<Void, Integer, Boolean> {

	private Context context;
	private ProgressDialog dialog;
	private boolean mResult;

	/**
	 * Runs a callback in a background thread, and display a ProgressDialog until it's finished
	 */
	public MyTaskSetToast(Context context) {
		this.context = context;
		execute();
	}

	public abstract boolean run();
	public abstract boolean res(boolean result);

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
	/* Runs on a background thread */
	protected Boolean doInBackground(Void... params) {
		try { 
			return run();
		} catch (Exception ex) {			
			BHelper.ex(ex);
			return false;
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if (dialog != null) {
			int per = (int) ((float) values[0] / values[1] * 100);
			String msg = "로딩중... ";
			msg += (per > 9 ? per : "0" + per) + "% [";
			int i = String.valueOf(values[0]).length();
			for (; i < String.valueOf(values[1]).length(); i++)
				msg += "0";
			msg += values[0] + "/" + values[1] + "]";
			dialog.setMessage(msg);
		}
		super.onProgressUpdate(values);
	}

	@Override
	/* Runs on the UI thread */
	protected void onPostExecute(Boolean result) {
		if (dialog != null)
			dialog.dismiss();
		try {
			res(mResult = result);
		} catch (Exception ex) {
			BHelper.ex(ex);
		} finally {
			dialog = null;
			context = null;
		}		
		super.onPostExecute(result);
	}
	
	protected void showToast(String success, String error) {
		if(!mResult){
			Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
		}else {
			if(success!=null)
				Toast.makeText(context, success, Toast.LENGTH_SHORT).show();
		}
			
			
	}
}
