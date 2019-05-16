package com.devcrane.payfun.daou.utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.utility.BHelper.DialogHelper;

/**
 * A small callback helper class to make running callbacks in threads easier
 */
public abstract class PaymentTask extends AsyncTask<Void, Integer, String> {

	private Activity context;
	private ProgressDialog dialog;
	private boolean mResult = false;
	private int msgID;


	private CustomDialog Cdialog;
	private int mDrawable = 0;

	/**
	 * Runs a callback in a background thread, and display a ProgressDialog until it's finished
	 */
	public PaymentTask(Activity context) {
		this.context = context;
		msgID = R.string.msg_is_paying;
		execute();
	}
	public PaymentTask(Activity context, int msgID) {
		this.context = context;
		this.msgID = msgID;
		execute();
	}

	public PaymentTask(Activity context, int msgID, int mdrawable) {
		this.context = context;
		this.msgID = msgID;
		this.mDrawable = mdrawable;
		execute();
	}




	public abstract String run();

	public abstract boolean res(String result);

	@Override
	/* Runs on the UI thread */
	protected void onPreExecute() {
		if (context != null) {

			if(mDrawable != 0)
			{
				Cdialog = new CustomDialog(context, mDrawable);
				Cdialog.setCancelable(false);
				Cdialog.show();
			}
			else
			{
				BHelper.db("show PaymentTask dialog");
				dialog = DialogHelper.makeDialog(this.msgID);
				dialog.setCancelable(false);
				dialog.show();
			}


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
			if (dialog != null)
				dialog.dismiss();
			if (Cdialog != null)
				Cdialog.dismiss();
			return null;
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if (dialog != null) {
			int per = (int) ((float) values[0] / values[1] * 100);
			String msg = "로딩중...";
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
		try {
			res(result);
			if (result != null)
				mResult = true;
//			BHelper.showToast(mResult ? R.string.sqlite_success : R.string.sqlite_error);
		} catch (Exception ex) {
			BHelper.ex(ex);
		} finally {
			if (dialog != null)
				dialog.dismiss();
			if (Cdialog != null)
				Cdialog.dismiss();
			dialog = null;
			Cdialog = null;
			context = null;
		}
		super.onPostExecute(result);
	}
	protected void updateMsg(String msg){
		this.msgID = msgID;
		if (dialog != null && dialog.isShowing()) {
			dialog.setMessage(msg);
		}

	}


	protected void updateCdialog(int msg){
		this.msgID = msgID;
		if (Cdialog != null && Cdialog.isShowing()) {
			Cdialog.changeDrawable(msg);
		}

	}



	protected void showToast(String success, String error) {
//		Toast.makeText(context, mResult ? success : error, Toast.LENGTH_SHORT).show();
	}
}
