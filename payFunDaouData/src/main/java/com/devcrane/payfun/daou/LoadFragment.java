package com.devcrane.payfun.daou;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.payfun.daou.data.PayFunDB;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.UserEntity;
import com.devcrane.payfun.daou.manager.NoticeManager;
import com.devcrane.payfun.daou.manager.ReceiptManager;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Base64Utils;
import com.devcrane.payfun.daou.utility.DateHelper;
import com.devcrane.payfun.daou.utility.HexDump;
import com.devcrane.payfun.daou.utility.MyTaskToast;
import com.devcrane.payfun.daou.utility.RootUtils;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.jsoup.Jsoup;

import java.util.ArrayList;

public class LoadFragment extends Activity {
	String package_name = "";

	boolean isPermission = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_load);


		TedPermission.with(this)
				.setPermissionListener(permissionlistener)
				.setDeniedMessage("권한을 설정하지 않으면, 앱을 사용 할 수 없습니다.\n\n권한설정을 먼저 해주세요. [설정] > [앱] > [권한]")
				.setPermissions
				(
						Manifest.permission.ACCESS_FINE_LOCATION,
//						android.Manifest.permission.READ_CONTACTS,
						android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
						android.Manifest.permission.READ_PHONE_STATE,
						android.Manifest.permission.RECORD_AUDIO
//						android.Manifest.permission.CAMERA,
//						Manifest.permission.SEND_SMS
				)
				.check();


//		int PERMISSION_ALL = 1;
//		String[] PERMISSIONS = { android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
//
//		if(!hasPermissions(this, PERMISSIONS)){
//			ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
//		}



		package_name = getApplicationInfo().packageName;
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		int readerTyppe = AppHelper.getReaderType();
		BHelper.db("readerTyppe:"+readerTyppe);
		boolean isBT = false;
		if(readerTyppe == EmvReader.READER_TYPE_BT)
			isBT = true;
		EmvReader.setIsBlueTooth(isBT);
	}




	public static boolean hasPermissions(Context context, String... permissions) {
		Log.e("Jonathan", "11111");
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
			Log.e("Jonathan", "2222");
			for (String permission : permissions) {
				Log.e("Jonathan", "3333 :: " + permission);
				if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
					Log.e("Jonathan", "4444");
					return false;
				}
			}
		}
		return true;
	}


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(LoadFragment.this, "권한이 설정 되었습니다", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(LoadFragment.this, "권한이 설정되지 않았습니다.\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }


    };



	@Override
	protected void onResume() {
		super.onResume();
//		List<UserEntity> list = MySoap.userGetLoginV2("1","1","1");
//		for(UserEntity entity :list){
//			BHelper.db(entity.toString());
//		}

//		BHelper.db("sessionInfo:"+AppHelper.getSessionInfo().toString());
//		testKeyin();



//		String encCardData = "4456424746674941416741414239754B37516E523655364869326451544133593676614B5238385A4B71793474334A53305379306450787731624536332F74366D42674955756D687974473570413D3D";
//		BHelper.db("base64 card:"+ JTNet.makeTrack2(encCardData));
//		if(!AppDataStatic.checkAntiVirus(getApplicationContext())){
//			showAntiVirusDialog(R.string.msg_no_antivirus);
//
//		}
//		else
//        if(!CertHelper.cert(this)){
//            showCertDialog();
//        }else
		if (RootUtils.isDeviceRooted()){
			showAntiVirusDialog(R.string.msg_device_is_rooted);
		}else{

				new MyTaskToast(this,"로딩중...") {

					@Override
					public boolean run() {
						return web_update();
					}

					@Override
					public boolean res(boolean result) {
						removeReceiptBefore3Month();
						if (result) {
							showDialog();
						} else {
							if(!isPermission)
							{
								isPermission = true;
								checkKeyBinding();
							}
						}
						return false;
					}
				};


		}
	}
	void removeReceiptBefore3Month(){
		try{
			BHelper.db("Receipt before 3 month:"+ReceiptManager.getReceiptBefore3Month());
			ReceiptManager.deleteBefore3Month();
			UserEntity key = new UserEntity(0);
			String f_UserID = AppHelper.getCurrentUserID();
			BHelper.db("f_UserID:"+f_UserID);
			if(!f_UserID.equals("")){
				BHelper.db("update 3 month:"+ReceiptManager.updateReceiptBefore3Month(f_UserID));
			}
			BHelper.db("Receipt before 3 month (after delete):"+ReceiptManager.getReceiptBefore3Month());	
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	private void showAntiVirusDialog(int resId) {

		Log.e("Jonathan","업데이트 진행??");

		new AlertDialog.Builder(this)
		.setTitle(getString(R.string.msg_app_updated_title))
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(resId)
		.setPositiveButton("예", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
			}
		}).show();

	}
    private void showCertDialog() {

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.msg_cert_title))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.msg_cert_content)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                }).show();

    }
	private void showDialog() {

		Log.e("Jonathan","업데이트 진행??");



		new AlertDialog.Builder(this).setTitle(getString(R.string.msg_app_updated_title)).setCancelable(false).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton("예", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + package_name)));
			}
		}).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				checkKeyBinding();
			}
		}).show();

	}

	void checkKeyBinding(){
		String keySavedYear = AppHelper.getKeyBindingYear();
		String currentYear = DateHelper.getYear();
		BHelper.db("keySavedYear:"+ keySavedYear + ", currentYear:"+currentYear);
		if(keySavedYear.equals(currentYear)){
			getNotice();
		}else{
			new AlertDialog.Builder(this)
					.setCancelable(false)
					.setTitle(getString(R.string.msg_key_updated_title))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("예", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							getNotice();
						}
					}).show();
		}
	}
	private void getNotice() {
		new MyTaskToast(this,"로딩중...") {

			@Override
			public boolean run() {
				PayFunDB.InitializeDB(getBaseContext());
				// AppHelper.getDatabase();
				NoticeManager.getListWS();
				return true;
			}

			@Override
			public boolean res(boolean result) {
				final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				boolean wifiEnabled = wifiManager.isWifiEnabled();
				boolean useWifi = AppHelper.prefGetBoolean(StaticData.UseWifi, false);
				if (wifiEnabled && !useWifi) {
					new AlertDialog.Builder(LoadFragment.this).setTitle(getString(R.string.msg_turn_off_wifi)).setCancelable(false).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton("예", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							wifiManager.setWifiEnabled(false);
							gotoMain();
						}
					}).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							gotoMain();
						}
					}).show();

				} else {
					gotoMain();
				}
				return false;
			}
		};
	}
	private void gotoMain(){
		Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
//		mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//		mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(mainIntent);
		finish();
	}

	private boolean web_update() {
		try {
			BHelper.db("try init LocalDB");
			PayFunDB.InitializeDB(getBaseContext());
			String curVersion = getApplication().getPackageManager().getPackageInfo(package_name, 0).versionName;
			String newVersion = curVersion;
			newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + package_name + "&hl=en").timeout(30000).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6").referrer("http://www.google.com").get().select("div[itemprop=softwareVersion]").first().ownText();
			BHelper.db("NewVersion:" + newVersion);
			return (value(curVersion) < value(newVersion)) ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private long value(String string) {
		string = string.trim();
		if (string.contains(".")) {
			final int index = string.lastIndexOf(".");
			return value(string.substring(0, index)) * 100 + value(string.substring(index + 1));
		} else {
			return Long.valueOf(string);
		}
	}
	void testKeyin(){
		String ksn = "215046160200020000DC";
		String encryptedData="AFCD448AE7B4A1260688CBE867541CF5";
		byte[] tmpKsn = HexDump.hexStringToByteArray(ksn);
		byte[] tmpCardData = HexDump.hexStringToByteArray(encryptedData);
//		byte[] tmpCardData = encryptedData.getBytes();
		byte[] cardInBytes = new byte[100];
		int count = 0;
		System.arraycopy(tmpKsn, 0, cardInBytes, count, tmpKsn.length);
		count+=tmpKsn.length;
		System.arraycopy(tmpCardData, 0, cardInBytes, count, tmpCardData.length);
		count+=tmpCardData.length;
		byte[] finalCardData = new byte[count];
		System.arraycopy(cardInBytes, 0, finalCardData, 0, count);
		String cardValue = Base64Utils.base64Encode(finalCardData);
		String cardValue2 = Base64Utils.base64Encode(HexDump.hexStringToByteArray(ksn+encryptedData));
		BHelper.db("ksn:"+ksn);
		BHelper.db("ksn base64:"+Base64Utils.base64Encode(tmpKsn));
		BHelper.db("encryptedData:"+encryptedData);
		BHelper.db("encryptedData base64:"+Base64Utils.base64Encode(tmpCardData));
		BHelper.db("finalCardData:"+ HexDump.toHexString(finalCardData));
		BHelper.db("base64 card:"+cardValue);
		BHelper.db("base64 card 2:"+cardValue2);
		
	}

}
