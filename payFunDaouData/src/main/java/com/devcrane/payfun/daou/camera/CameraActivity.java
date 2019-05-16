/*
 * Basic no frills app which integrates the ZBar barcode scanner with
 * the camera.
 * 
 * Created by lisah0 on 2012-02-24
 */
package com.devcrane.payfun.daou.camera;

import java.util.ArrayList;
import java.util.List;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.devcrane.payfun.daou.HomeFragment;
import com.devcrane.payfun.daou.LoginFragment;
import com.devcrane.payfun.daou.PaymentsCashFragment;
import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.manager.CouponManager;
import com.devcrane.payfun.daou.ui.CompanyAdapter;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.PaymentTask;
import com.devcrane.payfun.daou.utility.SoundSearcher;
import com.slidingmenu.lib.SlidingMenu;

/* Import ZBar Class files */

public class CameraActivity extends Activity {
	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private int requestedCameraId = -1;
	TextView tvResult;
	private Button btnCancel, btnConfirm;
	String sAction;
	boolean menuClick = false;
	public SlidingMenu mSlidingMenu;

	ImageScanner scanner;
	int[] ids = { R.id.btnMRProfile, R.id.btnMRHome, R.id.btnMRCoupon, R.id.btnMRCredit,//
			R.id.btnMRHistory, R.id.btnMRCash, R.id.btnMRMenuLeft, R.id.btnMRMenuRight,//
			R.id.menuMainHome,R.id.menuMainCredit,R.id.menuMainCash,R.id.menuMainCancelList};

	private boolean barcodeScanned = false;
	private boolean previewing = true;
	private String sResult;
	String sBarCode;
	FrameLayout preview;

	static {
		System.loadLibrary("iconv");
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	}

	@Override
	protected void onResume() {
		super.onResume();
		StaticData.KeyCodeBack = false;
		onInitView();
		
	}

	private void onInitView() {
		setContentView(R.layout.scan_barcode_layout);
		mSlidingMenu = new SlidingMenu(this);
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mSlidingMenu.setBehindOffset((int) (metrics.widthPixels * .4167));

		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		mSlidingMenu.setMenu(R.layout.content_frame_left);
		mSlidingMenu.setSecondaryMenu(R.layout.content_frame_right);
		mSlidingMenu.setSecondaryShadowDrawable(R.drawable.shadowright);
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
		for (int id : ids) {
			findViewById(id).setOnClickListener(onClickListener);
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
		preview = (FrameLayout) findViewById(R.id.cameraPreview);
		preview.addView(mPreview);
		tvResult = (TextView) findViewById(R.id.tvBarCodeResult);
		btnCancel = (Button) findViewById(R.id.btnBarcodeCancel);
		btnConfirm = (Button) findViewById(R.id.btnBarcodeConfirm);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!sCanBarCode()) {
					StaticData.KeyCodeBack = true;
					finish();
				}
			}
		});
		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StaticData.KeyCodeBack = true;
				if (sResult != null) {
					Intent returnIntent = new Intent();
					returnIntent.putExtra(StaticData.RESULT_BARCODE, sResult);
					returnIntent.putExtra(StaticData.COUPON_ID, sBarCode);
					setResult(RESULT_OK, returnIntent);
					finish();
				} else
					finish();

			}
		});
		
		initMenuLeft();
	}

	public void onPause() {
		super.onPause();
		releaseCamera();
		BHelper.db("KeyCodeBack:"+ StaticData.KeyCodeBack);
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
			tvResult.setText("");
			tvResult.setVisibility(View.GONE);
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
				mCamera.stopPreview();

				SymbolSet syms = scanner.getResults();
				sBarCode = "";
				for (Symbol sym : syms) {
					sBarCode = sym.getData();
					barcodeScanned = true;
				}
				if (!sBarCode.equals("")) {
					new PaymentTask(CameraActivity.this) {

						@Override
						public String run() {
							return CouponManager.getDisCountRateCoupon(sBarCode);
						}

						@Override
						public boolean res(String result) {
							if (result == null || result.equals("-1")) {
								sResult = "0";
								tvResult.setText("Can't find Coupon!");
							} else {
								tvResult.setText(result + " %");
								sResult = result;
							}
							return false;
						}
					};
				}
				tvResult.setVisibility(View.VISIBLE);
			}
		}
	};

	// Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			sAction = "";
			menuClick = false;
			switch (v.getId()) {
			case R.id.btnMRProfile:
				sAction = StaticData.RightProfile;
				break;
			case R.id.btnMRHome:
				sAction = StaticData.RightHome;
				break;
			case R.id.btnMRCredit:
				sAction = StaticData.RightCredit;
				break;
			case R.id.btnMRCash:
				sAction = StaticData.RightCash;
				break;
			case R.id.btnMRHistory:
				sAction = StaticData.RightCancelList;
				break;
			case R.id.btnMRCoupon:
				sAction = StaticData.RightCoupon;
				break;
			case R.id.btnMRMenuLeft:
				menuClick = true;
				mSlidingMenu.showMenu();
				break;
			case R.id.btnMRMenuRight:
				menuClick = true;
				mSlidingMenu.showSecondaryMenu();
				break;
			case R.id.menuMainHome:
				sAction = StaticData.RightHome;
				break;
			case R.id.menuMainCancelList:
				sAction = StaticData.MainCancelList;
				break;
			case R.id.menuMainCash:
				sAction = StaticData.RightCash;
				break;
			case R.id.menuMainCredit:
				sAction = StaticData.RightCredit;
				break;
			default:
				break;
			}
			if(!menuClick){
				StaticData.KeyCodeBack = true;
				Intent returnIntent = new Intent();
				returnIntent.putExtra(StaticData.RESULT_ACTION, sAction);
				setResult(RESULT_CANCELED, returnIntent);
				finish();
			}
		}
	};
	public void setEnableLayout(boolean enable){
		if(enable){
			preview.setVisibility(View.INVISIBLE);
		}else{
			preview.setVisibility(View.VISIBLE);
		}
	}
	public void initMenuLeft() {
		final EditText txtSearch = (EditText) findViewById(R.id.txtSearch);
		final ImageButton buttonSearch = (ImageButton) findViewById(R.id.buttonsearch);
		final ListView lvCompany = (ListView) findViewById(R.id.lvCompany);
		final List<CompanyEntity> objects = CompanyManger.getAllCompany(AppHelper.getCurrentUserID());
		final CompanyAdapter adapter = new CompanyAdapter(CameraActivity.this, objects, false);
		lvCompany.setAdapter(adapter);

		lvCompany.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				adapter.setPosition(arg2);
				CompanyEntity comE = (CompanyEntity) lvCompany.getAdapter().getItem(arg2);
				AppHelper.setCurrentVanID(comE.getF_ID());
				PaymentsCashFragment.setCompany(comE);
			}
		});
		buttonSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String s = txtSearch.getText().toString();
				if (s.toString().equals("")) {
					lvCompany.setAdapter(adapter);
					return;
				}
				List<CompanyEntity> list = new ArrayList<CompanyEntity>();
				for (CompanyEntity obj : objects) {
					String keyword = s.toString();
					if (SoundSearcher.matchString(obj.getF_CompanyOwnerName(), keyword) || obj.getF_CompanyNo().indexOf(keyword) > -1 || obj.getF_MachineCode().indexOf(keyword) > -1) {
						list.add(obj);
					}
				}
				lvCompany.setAdapter(new CompanyAdapter(CameraActivity.this, list, false));
			}
		});

		txtSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				if (s.toString().equals("")) {
					lvCompany.setAdapter(adapter);
					return;
				}
				List<CompanyEntity> list = new ArrayList<CompanyEntity>();
				for (CompanyEntity obj : objects) {
					String keyword = s.toString();
					if (SoundSearcher.matchString(obj.getF_CompanyOwnerName(), keyword) || obj.getF_CompanyNo().indexOf(keyword) > -1 || obj.getF_MachineCode().indexOf(keyword) > -1) {
						list.add(obj);
					}
				}
				lvCompany.setAdapter(new CompanyAdapter(CameraActivity.this, list, false));
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
	}
}
