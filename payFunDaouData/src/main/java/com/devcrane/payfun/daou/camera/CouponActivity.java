package com.devcrane.payfun.daou.camera;

import java.lang.reflect.Type;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.devcrane.payfun.daou.HomeFragment;
import com.devcrane.payfun.daou.LoginFragment;
import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.CouponEntity;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.manager.CouponManager;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.PaymentTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CouponActivity extends Activity {
	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private int requestedCameraId = -1;
	TextView tvResult;
	TextView tvComNo, tvComOwerName, tvMachineCode, tvVanName, tvComphone;
	CompanyEntity comEntity;
	EditText edCouponRate, edCouponName;
	Button btnSave;

	ImageScanner scanner;

	private boolean barcodeScanned = false;
	private boolean previewing = true;
	private String sBarCodeValue = "";

	static {
		System.loadLibrary("iconv");
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StaticData.KeyCodeBack = false;
		setContentView(R.layout.view_coupon);
		openCamera();
		setCompany();
	}

	private void openCamera() {
		autoFocusHandler = new Handler();

		Intent intent = getIntent();
		if (intent != null) {
			if (intent.hasExtra(Intents.Scan.CAMERA_ID)) {
				int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1);
				if (cameraId >= 0) {
					requestedCameraId = cameraId;
				}
			}
		}

		mCamera = getCameraInstance();
		Camera theCamera = mCamera;
		if (theCamera == null) {

			if (requestedCameraId >= 0) {
				theCamera = OpenCameraInterface.open(requestedCameraId);
			} else {
				theCamera = OpenCameraInterface.open();
			}
			mCamera = theCamera;
		}

		/* Instance barcode scanner */
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);
		mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
		FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
		preview.addView(mPreview);
		initView();
	}

	private void initView() {
		tvResult = (TextView) findViewById(R.id.tvBarCodeResult);
		tvComNo = (TextView) findViewById(R.id.tvCompanyNo);
		tvMachineCode = (TextView) findViewById(R.id.tvCompanyMachineCode);
		tvVanName = (TextView) findViewById(R.id.tvVanName);
		tvComOwerName = (TextView) findViewById(R.id.tvCompanyOwerName);
		edCouponRate = (EditText) findViewById(R.id.edCouponSave);
		edCouponName = (EditText) findViewById(R.id.edCouponsperson);
		btnSave = (Button) findViewById(R.id.btnSaveCoupon);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doSaveCoupon();
			}
		});
		Intent inten = getIntent();
		if (inten == null)
			return;
		Type type = new TypeToken<CouponEntity>() {
		}.getType();
		String json = inten.getStringExtra(StaticData.COUPON_JSON);
		CouponEntity mCouEntity = new Gson().fromJson(json, type);
		if (mCouEntity != null) {
			tvResult.setText(mCouEntity.getF_CouponID());
			edCouponName.setText(mCouEntity.getF_CouponName());
			edCouponName.setSelection(mCouEntity.getF_CouponName().length());
			edCouponRate.setText(mCouEntity.getF_DiscountRate());
			sBarCodeValue = mCouEntity.getF_CouponID();
		}
	}

	private void setCompany() {
		comEntity = CompanyManger.getCompanyByID(AppHelper.getCurrentVanID());
		if (comEntity == null)
			return;
		tvComNo.setText(Helper.formatCompanyNo(comEntity.getF_CompanyNo()));
		tvMachineCode.setText(comEntity.getF_MachineCode());
		tvComOwerName.setText(comEntity.getF_CompanyOwnerName());
		tvVanName.setText(comEntity.getF_VanName());
		tvComphone.setText(comEntity.getF_CompanyPhoneNo());
	}

	public void onPause() {
		super.onPause();
		releaseCamera();
		if (!StaticData.KeyCodeBack){
			AppHelper.setIsLogin(false);
			AppHelper.setCurrentUserID("");
			HomeFragment.btnLogin.setImageResource(R.drawable.lock);
		}
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (sCanBarCode()) {
				return false;
			} else {
				StaticData.KeyCodeBack = true;
				return super.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean sCanBarCode() {
		if (barcodeScanned) {
			barcodeScanned = false;
			tvResult.setText("BarCode");
			sBarCodeValue = "";
			edCouponName.setText("");
			edCouponRate.setText("");
			mCamera.setPreviewCallback(previewCb);
			mCamera.startPreview();
			previewing = true;
			mCamera.autoFocus(autoFocusCB);
			return true;
		} else
			return false;
	}

	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
		}
		return c;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	private CouponEntity setCoupon() {
		CouponEntity co = new CouponEntity();
		co.setF_MachineCode(comEntity.getF_MachineCode());
		co.setF_CompanyNo(comEntity.getF_CompanyNo());
		co.setF_CouponName(edCouponName.getText().toString());
		co.setF_DiscountRate(edCouponRate.getText().toString());
		co.setF_CouponID(sBarCodeValue);
		co.setF_VanName(comEntity.getF_VanName());
		co.setCREATE_UID(AppHelper.getCurrentUserID());
		co.setUPDATE_UID(AppHelper.getUpdateUserID());
		return co;
	}

	private void doSaveCoupon() {
		if (comEntity == null)
			return;
		if (sBarCodeValue.equals(""))
			return;
		if (edCouponName.getText().toString().length() == 0)
			return;
		if (edCouponRate.getText().toString().length() == 0)
			return;
		new PaymentTask(CouponActivity.this) {

			@Override
			public String run() {
				return CouponManager.insertCoupon(setCoupon());
			}

			@Override
			public boolean res(String result) {
				if (result != null)
					Toast.makeText(getBaseContext(), R.string.sqlite_success, Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getBaseContext(), R.string.sqlite_error, Toast.LENGTH_SHORT).show();
				sCanBarCode();
				return false;
			}
		};
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
				previewing = false;
				mCamera.setPreviewCallback(null);
				// mCamera.stopPreview();

				SymbolSet syms = scanner.getResults();
				for (Symbol sym : syms) {
					tvResult.setText(sym.getData());
					sBarCodeValue = sym.getData();
					barcodeScanned = true;
				}
				// tvResult.setVisibility(View.VISIBLE);
			}
		}
	};

	// Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};
}