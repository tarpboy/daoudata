package com.devcrane.payfun.daou.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.devcrane.payfun.daou.R;

/**
 * A small callback helper class to make running callbacks in threads easier
 */
public abstract class MyTaskRegisterUser extends AsyncTask<Void, Integer, Boolean> {

	private Context context;
	private ProgressDialog dialog;
	private boolean mResult;

	/**
	 * Runs a callback in a background thread, and display a ProgressDialog until it's finished
	 */
	public MyTaskRegisterUser(Context context) {
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
			String msg = "Loading... ";
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
		try {
			if (res(mResult = result))
				BHelper.showToast(result ? R.string.sqlite_success : R.string.msg_register_user_error);
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
	
	protected void showToast(String success, String error) {
		Toast.makeText(context, mResult ? success : error, Toast.LENGTH_SHORT).show();
	}
}
