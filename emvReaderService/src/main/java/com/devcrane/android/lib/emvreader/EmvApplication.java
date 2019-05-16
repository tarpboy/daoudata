package com.devcrane.android.lib.emvreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.devcrane.android.lib.utility.BHelper;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class EmvApplication extends Application {

	public final static String SETTING_NAME = "payfun_data";
	public final static String SETTING_DECODE_CONFIGURED = "reader_configured";
	public final static String SETTING_DECODE_VER = "encoder_config_ver";
	public final static String SETTING_DECODE_MODE = "encoder_mode";
	public final static String SETTING_DECODE_MIN_VAL = "encoder_min_val";
	public final static String SETTING_DECODE_CAL_VAL = "encoder_cal_val";
	public static String APP_NAME = "";
	public static String TAG = "EmvReader";
	public static final String settingPath = "EmvReader";
	public Boolean isServiceRun = false;
	EmvReader emvReader;
	private UncaughtExceptionHandler appExHandler;
	static NotificationManager notiManager;
	public static final boolean IS_ENABLE_LOG = false;
	public void onCreate() {
		super.onCreate();
		appExHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
		BHelper.setContext(getApplicationContext());
	}

	

	public String formatNumberStringFrom(String source, boolean masking) {
		String ret = null;
		String str = source.replaceAll("-", "");
		if (str.length() == 10) {
			if ("01".equals(str.substring(0, 2))) {
				ret = String.format("%s-%s-%s", str.substring(0, 3),
						masking ? "***" : str.substring(3, 3 + 3),
						str.substring(6, 6 + 4));
			} else {
				ret = String.format("%s-%s-%s", str.substring(0, 3),
						masking ? "**" : str.substring(3, 3 + 2),
						str.substring(5, 5 + 5));
			}
		} else if (str.length() == 11) {
			if ("01".equals(str.substring(0, 2))) {
				ret = String.format("%s-%s-%s", str.substring(0, 3),
						masking ? "****" : str.substring(3, 3 + 4),
						str.substring(7, 7 + 4));
			}
		} else if (str.length() == 13) {
			ret = String.format("%s-%s", str.substring(0, 6),
					masking ? "*******" : str.substring(6, 6 + 7));
		} else if (str.length() == 16) {
			ret = String.format("%s-%s-%s-%s", str.substring(0, 4),
					str.substring(4, 4 + 4),
					masking ? "****" : str.substring(8, 8 + 4),
					str.substring(12, 12 + 4));
		} else if (str.length() == 18) {
			ret = String.format("%s-%s-%s-%s", str.substring(0, 4),
					str.substring(4, 4 + 4),
					masking ? "****" : str.substring(8, 8 + 4),
					str.substring(12, 12 + 6));
		}

		if (ret != null)
			return ret;

		return str;
	}
	
	private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {

			try {
				if (emvReaderService != null && emvReaderServiceIntent != null) {
					stopService(emvReaderServiceIntent);

				}
				unbindService(emvReaderConnection);
//				Log.d(TAG, "stop notify");
				NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notiManager.cancel(1);

				// write log if error
				String tmpMessage = "";
				for (StackTraceElement iterable_element : ex.getStackTrace()) {
					tmpMessage += "\n" + iterable_element.toString();
				}
				writeLogFile(tmpMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
			appExHandler.uncaughtException(thread, ex);
		}
	};
	public boolean internetIsConnected() {
		try {

			ConnectivityManager net = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			if (net != null) {
				NetworkInfo netInfo = net.getActiveNetworkInfo();
				return netInfo != null && netInfo.isAvailable()
						&& netInfo.isConnected();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean isServiceRuning() {
		return isServiceRun;
	}

	protected ServiceConnection emvReaderConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			emvReaderService = ((EmvReaderService.EmvReaderServiceBinder) binder).getService();
			BHelper.db("onServiceConnected");
			emvReader = emvReaderService.getEmvReader();
			try {
				//try start reader after connected service
				emvReader.startReader();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent intent = new Intent();
			intent.setAction("InitializationBroadcast");

			intent.putExtra("value", 1000);
			BHelper.db("sendBroadcast");
			sendBroadcast(intent);
		}

		public void onServiceDisconnected(ComponentName className) {
			BHelper.db("onServiceDisconnected");
			emvReaderService = null;
		}
	};

	protected Intent emvReaderServiceIntent = null;
	protected EmvReaderService emvReaderService = null;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public boolean startEmvReaderService(Class<?> cls) {
		Log.d(TAG,"startEmvReaderService.......");
		emvReaderServiceIntent = new Intent(this, EmvReaderService.class);
		if (!bindService(emvReaderServiceIntent, emvReaderConnection,
				Context.BIND_AUTO_CREATE)) {
			return false;
		}
		isServiceRun = true;
		notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Context context = this;
		String expandedText = "Application is running,..";
		String expandedTitle = APP_NAME;
		Intent intent = new Intent(context, cls);
		intent.setAction("android.intent.action.MAIN");

		PendingIntent launchIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		
		Notification noti = 
		new Notification.Builder(this)
        .setContentTitle("Payfun")
        .setContentText("Application is running,..")
        .setContentIntent(launchIntent)
        .setSmallIcon(R.drawable.ic_launcher)
        .build();



		noti.flags |= Notification.FLAG_ONGOING_EVENT
				| Notification.FLAG_NO_CLEAR;

		notiManager.notify(1, noti);
		Log.d(TAG,"startEmvReaderService.......done");
		return true;
	}

	public void stopEmvReaderService() {
		try {
			if (emvReaderService != null && emvReaderServiceIntent != null) {
				stopService(emvReaderServiceIntent);
				emvReaderService.releaseEmvReader();
			}
			unbindService(emvReaderConnection);
			BHelper.db("stopEmvReaderService");
		} catch (Exception e) {
			e.printStackTrace();
		}
		isServiceRun = false;
	}
	public void stopApp(){
		try {
			stopEmvReaderService();
			BHelper.db("stop notify");
//			notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			notiManager.cancel(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		isServiceRun = false;
	}
	
	public void stopNotification(){
		try {
			notiManager.cancel(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EmvReaderService getEmvReaderService() {
		return emvReaderService;
	}

	// write error log to sdcard
	public void writeLogFile(String logData) {
		try {
			String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(Calendar.getInstance().getTime());
			logData = timeStamp + logData + "\n*********\n";
			String filepath = Environment.getExternalStorageDirectory()
					.getPath();
			File file = new File(filepath, "PayFunLog");
			if (!file.exists()) {
				file.mkdirs();
			}
			String fullFileName = file.getAbsolutePath() + "/error.log";
			File myFile = new File(fullFileName);
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(logData);
			myOutWriter.close();
			fOut.close();
		} catch (Exception e) {

		}
	}
}
