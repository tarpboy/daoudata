package com.devcrane.payfun.daou.utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.devcrane.payfun.daou.R;


/**
 * Created by Administrator on 8/31/2016.
 */
public abstract class MyTaskDLVan extends AsyncTask<Void, Integer, String[]> {

    private Activity context;
    private ProgressDialog dialog;
    private boolean mResult = false;

    /**
     * Runs a callback in a background thread, and display a ProgressDialog until it's finished
     */
    public MyTaskDLVan(Activity context) {
        this.context = context;
        execute();
    }
    public MyTaskDLVan(Activity context, ProgressDialog dialog) {
        this.context = context;
        this.dialog = dialog;
        execute();
    }

    public abstract String[] run();

    public abstract boolean res(String[] result);

    @Override
	/* Runs on the UI thread */
    protected void onPreExecute() {
//        if (context != null) {
//            if(dialog == null) {
//                dialog = BHelper.DialogHelper.makeDialog(R.string.msg_processing);
//                dialog.setCancelable(false);
//            }
//            dialog.show();
//
//        }
        super.onPreExecute();
    }

    @Override
	/* Runs on a background thread */
    protected String[] doInBackground(Void... params) {
        try {
            return run();
        } catch (Exception ex) {
            BHelper.ex(ex);
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
    protected void onPostExecute(String[] result) {
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
            dialog = null;
            context = null;
        }
        super.onPostExecute(result);
    }

    protected void showToast(String success, String error) {
//		Toast.makeText(context, mResult ? success : error, Toast.LENGTH_SHORT).show();
    }
}
