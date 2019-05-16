package com.devcrane.payfun.daou.utility;

import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.utility.BHelper.DialogHelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * A small callback helper class to make running callbacks in threads easier
 */
public abstract class StringTaskSetToast extends AsyncTask<Void, Integer, String> {

	private Context context;
	private ProgressDialog dialog;

	/**
	 * Runs a callback in a background thread, and display a ProgressDialog until it's finished
	 */
	public StringTaskSetToast(Context context) {
		this.context = context;
		execute();
	}

	public abstract String run();
	public abstract boolean res(String result);

	@Override
	/* Runs on the UI thread */
	protected void onPreExecute() {
		if (context != null) {
//			dialog = ProgressDialog.show(context, null, "로딩중...", true);
			dialog = DialogHelper.makeDialog(R.string.msg_processing);
			dialog.setCancelable(false);
			dialog.show();
		}
		super.onPreExecute();
	}

	@Override
	/* Runs on a background thread */
	protected String doInBackground(Void... params) {
		try { 
			return run();
		} catch (Exception ex) {			
			BHelper.ex(ex);
			return "";
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
	protected void onPostExecute(String result) {
		if (dialog != null)
			dialog.dismiss();
		try {
			res(result);
		} catch (Exception ex) {
			BHelper.ex(ex);
		} finally {
			dialog = null;
			context = null;
		}		
		super.onPostExecute(result);
	}
	
	protected void showToast(String message) {
		BHelper.showToast(message);
	}
}
