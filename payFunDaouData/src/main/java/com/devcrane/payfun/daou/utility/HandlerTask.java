package com.devcrane.payfun.daou.utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * A small callback helper class to make running callbacks in threads easier
 */
public abstract class HandlerTask extends AsyncTask<Void, Integer, Void> {

	private Activity context;
	private ProgressDialog dialog;
	private boolean mResult = false;

	
	public HandlerTask(Activity context) {
		this.context = context;
		execute();
	}

	public abstract String run();

	public abstract boolean res();

	@Override
	/* Runs on the UI thread */
	protected void onPreExecute() {
		if (context != null) {
			dialog = ProgressDialog.show(context, null, "Loading...", true);
			dialog.setCancelable(false);
		}
		super.onPreExecute();
	}

	@Override
	/* Runs on a background thread */
	protected Void doInBackground(Void... params) {
		try {
			run();
			Message msg = new Message();
			msg.obj = "";
			TaskHandler.sendMessage(msg);
		} catch (Exception ex) {
			BHelper.ex(ex);
			return null;
		}
		return null;
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

	
	
	
	Handler TaskHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				res();
			} catch (Exception ex) {
				BHelper.ex(ex);
			} finally {
				if (dialog != null)
					dialog.dismiss();
				dialog = null;
				context = null;
			}
		}
	};


	protected void showToast(String success, String error) {
//		Toast.makeText(context, mResult ? success : error, Toast.LENGTH_SHORT).show();
	}
}
