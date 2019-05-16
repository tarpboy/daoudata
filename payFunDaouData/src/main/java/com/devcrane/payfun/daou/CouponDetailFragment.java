package com.devcrane.payfun.daou;

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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devcrane.payfun.daou.camera.CameraPreview;
import com.devcrane.payfun.daou.camera.Intents;
import com.devcrane.payfun.daou.camera.OpenCameraInterface;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.CouponEntity;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.manager.CouponManager;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.PaymentTask;

public class CouponDetailFragment extends Fragment {
	private static Camera mCamera;
	private CameraPreview mPreview;
	private static Handler autoFocusHandler;
	private int requestedCameraId = -1;
	static TextView tvResult;
	static TextView tvComNo;
	static TextView tvComOwerName;
	static TextView tvMachineCode;
	static TextView tvVanName;
	static CompanyEntity comEntity;
	static EditText edCouponRate;
	static EditText edCouponName;
	Button btnSave;
	Activity at;

	static ImageScanner scanner;

	private static boolean barcodeScanned = false;
	private static boolean previewing = true;
	private static String sBarCodeValue = "";

//	static {
//		System.loadLibrary("iconv");
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.view_coupon, container, false);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		at = getActivity();
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		initView();
	}
	public void onResume() {
		super.onResume();
//		openCamera();
		setCompany();
	}

	private void openCamera() {
		autoFocusHandler = new Handler();

		Intent intent = at.getIntent();
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
		mPreview = new CameraPreview(at, mCamera, previewCb, autoFocusCB);
		FrameLayout preview = (FrameLayout) at.findViewById(R.id.cameraPreview);
		preview.addView(mPreview);
		initView();
	}

	private void initView() {
		tvResult = (TextView) at.findViewById(R.id.tvBarCodeResult);
		tvComNo = (TextView) at.findViewById(R.id.tvCompanyNo);
		tvMachineCode = (TextView) at.findViewById(R.id.tvCompanyMachineCode);
		tvVanName = (TextView) at.findViewById(R.id.tvVanName);
		tvComOwerName = (TextView) at.findViewById(R.id.tvCompanyOwerName);
		edCouponRate = (EditText) at.findViewById(R.id.edCouponSave);
		edCouponName = (EditText) at.findViewById(R.id.edCouponsperson);
		btnSave = (Button) at.findViewById(R.id.btnSaveCoupon);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doSaveCoupon();
			}
		});
		CouponEntity mCouEntity = CouponFragment.mCouponEntity;
		if (mCouEntity != null) {
			tvResult.setText(mCouEntity.getF_CouponID());
			edCouponName.setText(mCouEntity.getF_CouponName());
			edCouponName.setSelection(mCouEntity.getF_CouponName().length());
			edCouponRate.setText(mCouEntity.getF_DiscountRate());
			sBarCodeValue = mCouEntity.getF_CouponID();
		}
	}

	public static void setCompany() {
		comEntity = CompanyManger.getCompanyByID(AppHelper.getCurrentVanID());
		if (comEntity == null)
			return;
		BHelper.db("comEntity:"+comEntity.toString());
		try {
			if (tvComNo != null)
				tvComNo.setText(Helper.cutString(comEntity.getF_CompanyName(), 16));
			if (tvMachineCode != null)
				tvMachineCode.setText(Helper.formatCompanyNo(comEntity.getF_CompanyNo()));
			if (tvComOwerName != null)
				tvComOwerName.setText(comEntity.getF_CompanyOwnerName());
			if (tvVanName != null)
				tvVanName.setText(comEntity.getF_VanName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean sCanBarCode() {
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

	@Override
	public void onPause() {
		super.onPause();
		releaseCamera();
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
		new PaymentTask(at) {

			@Override
			public String run() {
				return CouponManager.insertCoupon(setCoupon());
			}

			@Override
			public boolean res(String result) {
				if (result != null)
					Toast.makeText(at, R.string.sqlite_success, Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(at, R.string.sqlite_error, Toast.LENGTH_SHORT).show();
				sCanBarCode();
				return false;
			}
		};
	}

	private static Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	static PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
				previewing = false;
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();

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
	static AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};
}
