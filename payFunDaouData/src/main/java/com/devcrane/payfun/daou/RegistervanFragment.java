package com.devcrane.payfun.daou;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bbpos.bbdevice.BBDeviceController;
import com.devcrane.android.lib.emvreader.BlueToothListener;
import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.android.lib.emvreader.IntegrityCheckListener;
import com.devcrane.android.lib.entity.KeyExchangeResultEntity;
import com.devcrane.payfun.cardreader.EmvUtils;
import com.devcrane.payfun.daou.caller.ParaConstant;
import com.devcrane.payfun.daou.caller.ReqPara;
import com.devcrane.payfun.daou.caller.ResPara;
import com.devcrane.payfun.daou.customkeypad.KeyboardUtil;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.dialog.CancelListDialog;
import com.devcrane.payfun.daou.dialog.CancelListDialogListener;
import com.devcrane.payfun.daou.dialog.VanDLStep1Dialog;
import com.devcrane.payfun.daou.dialog.VanDLStep1DialogListener;
import com.devcrane.payfun.daou.dialog.VanDLStep2Dialog;
import com.devcrane.payfun.daou.dialog.VanDLStep2DialogListener;
import com.devcrane.payfun.daou.dialog.VanDLStep3Dialog;
import com.devcrane.payfun.daou.dialog.VanDLStep3DialogListener;
import com.devcrane.payfun.daou.entity.BTReaderInfo;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.KeyBindingEntity;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.BTHelper;
import com.devcrane.payfun.daou.utility.Base64Utils;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.BHelper.DialogHelper;
import com.devcrane.payfun.daou.utility.MyTaskStr;
import com.devcrane.payfun.daou.van.DaouData;
import com.devcrane.payfun.daou.van.DaouDataContants;
import com.devcrane.payfun.daou.van.DaouDataHelper;
import com.devcrane.payfun.daou.van.HexDump;

import com.devcrane.payfun.daou.van.OpenTerminal;
import com.devcrane.payfun.daou.van.RenewalKey;
import com.devcrane.payfun.daou.van.SecurityCertificate;
import com.devcrane.payfun.daou.van.SecurityKeyDownload;

@SuppressLint("NewApi")
public class RegistervanFragment extends Fragment  implements IntegrityCheckListener,BlueToothListener {
	EditText edCompanyNo, edMachineCode, edTaxsetting, edServicechargeset, edPin ;

//	Spinner spReaderType;
	RadioGroup rdgReadeType;
	RadioButton rdbBT;
	RadioButton rdbEarjack;

	Button btnDownload, btnSaveCompany,btnKeyBindingRegister, btnReaderOnOff;
	CheckBox cbWithTax;
	Activity at;
	TextView tvVanName, tvComNo, tvComName, tvComaddress, tvComPhone,tvVanPhoneNo, tvResellerPhoneNo,tvBTReaderName;;
	LinearLayout loResult, llReaderConfig;
	private ProgressDialog diglog = null;
	private String vanName = StaticData.vanName;
	CompanyEntity comEntity;
//	EmvApplication app;
	EmvReader emvReader;
	String respPublicKeyVers="";
	ProgressDialog dialog;
	Dialog pDialog;
	AlertDialog fallbackDlg;
	static boolean isUpdateKsn = false;
	boolean isDeviceReady =false;
	DaouData daouData;
    TerminalInfo terInfo;
	FragmentActivity currentActivity;
	boolean isGetPublicKey = false;
	boolean isRenewal = false;
	String publicKey = "";
	void initEmvResources(){
		emvReader = MainActivity.getEmvReader();
	}
	
	void attachService(){

		if(emvReader!=null){
			emvReader.attachIntegrityCheckListener(RegistervanFragment.this);
            emvReader.attachBlueToothListener(RegistervanFragment.this);
		}
	}
	void detachService(){
		if(emvReader!=null){
			emvReader.detachIntegrityCheckListener(RegistervanFragment.this);
            emvReader.detachBluetoothListener(RegistervanFragment.this);
		}
	}

	public void dismissPDialog() {
    	if(pDialog != null) {
    		pDialog.dismiss();
    		pDialog = null;
    	}
    }
	
	protected void updateDialogMsg(String msg){
//		showStatus("");
		if(dialog!=null && dialog.isShowing()){
			dialog.setMessage(msg);
		}
	}

	protected void closeDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}
	private void showToast(String message) {
		Toast.makeText(at, message, Toast.LENGTH_SHORT).show();
	}
	protected void showFallbackDlg(String msg){
		new AlertDialog.Builder(at).setTitle(R.string.emv_fallback_report).setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(msg)
		.setPositiveButton("예", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		}).show();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_registervan, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		at = getActivity();
		emvReader = MainActivity.getEmvReader();
        currentActivity = getActivity();
		BHelper.setTypeface(getView());
		onInitView();
		onInitEvent();
		initEmvResources();

//        emvReader.stopConnection();
//		attachService();
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		BHelper.showNeedDevice();
//		isDeviceReady = Helper.isHeadsetConnected(at) || MainActivity.isBTReaderConnected;
		isDeviceReady = Helper.isDeviceReady(at);
        attachService();
		if (isDeviceReady) {

		}else{
			BHelper.showNeedDevice();
		}
	}



    @Override
    public void onStop() {
        super.onStop();
        BHelper.db("onStop in RegisterVan");
        detachService();
        closeDialog();
    }

    @Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
        BHelper.db("onPause in RegisterVan");

	}
	private void onInitView() {
		edCompanyNo = (EditText) at.findViewById(R.id.txtResCompanyNo);
		edMachineCode = (EditText) at.findViewById(R.id.txtResMachineCode);
		edTaxsetting = (EditText) at.findViewById(R.id.txtTaxsettings);
		edServicechargeset = (EditText) at.findViewById(R.id.txtServicechargeset);
		btnDownload = (Button) at.findViewById(R.id.btnDownload);
		btnSaveCompany = (Button) at.findViewById(R.id.btnSaveCompany);
		btnKeyBindingRegister = (Button) at.findViewById(R.id.btnKeyBindingRegister);
		btnReaderOnOff = (Button) at.findViewById(R.id.btnReaderOnOff);
		cbWithTax = (CheckBox) at.findViewById(R.id.cbWithTax);
		edPin = (EditText) at.findViewById(R.id.txtPin);
		tvVanName = (TextView) at.findViewById(R.id.txtComdetailVanName);
		tvComNo = (TextView) at.findViewById(R.id.txtComdetailComNo);
		tvComaddress = (TextView) at.findViewById(R.id.txtComdetailAddr);
		tvComPhone = (TextView) at.findViewById(R.id.txtComdetailComphone);
		tvComName = (TextView) at.findViewById(R.id.txtComdetailCoName);
		tvVanPhoneNo = (TextView) at.findViewById(R.id.txtComdetailVanTel);
		tvResellerPhoneNo = (TextView) at.findViewById(R.id.txtComdetailReselTel);
		loResult =(LinearLayout) at.findViewById(R.id.loRegisterResult);
//		spReaderType = (Spinner)at.findViewById(R.id.spReaderType);

		rdgReadeType = (RadioGroup) at.findViewById(R.id.rdgReadeType);
		rdbBT = (RadioButton) rdgReadeType.findViewById(R.id.rdbBluetooth);
		rdbEarjack = (RadioButton) rdgReadeType.findViewById(R.id.rdbEarjack);

		tvBTReaderName = (TextView)at.findViewById(R.id.tvBTReaderName);
		llReaderConfig = (LinearLayout)at.findViewById(R.id.llReaderConfig);
		initReaderType();
		loadDefaultReaderInfo();
//		loadBTReaderInfo();

	}

	private void onInitEvent() {

		edCompanyNo.addTextChangedListener(mTextWatcher);
		disableShowSoftInput(edCompanyNo);
		edCompanyNo.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				KeyboardUtil.hideSoftKeyboard(at);
				showKeyboard(edCompanyNo);
				return false;
			}
		});
		disableShowSoftInput(edMachineCode);
		edMachineCode.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				KeyboardUtil.hideSoftKeyboard(at);
				showKeyboard(edMachineCode);
				return false;
			}
		});
		disableShowSoftInput(edTaxsetting);
		edTaxsetting.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				KeyboardUtil.hideSoftKeyboard(at);
				showKeyboard(edTaxsetting);
				return false;
			}
		});
		disableShowSoftInput(edServicechargeset);
		edServicechargeset.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				KeyboardUtil.hideSoftKeyboard(at);
				showKeyboard(edServicechargeset);
				return false;
			}
		});

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.listvan, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		edPin.setVisibility(View.GONE);
		btnSaveCompany.setSelected(true);
		btnDownload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				downloadVan();
				if(edCompanyNo.getText().length()<12){
					BHelper.showToast(R.string.msg_company_no_machine_code_len);
					edCompanyNo.requestFocus();
					return;
				}
				if(edMachineCode.getText().length()<8){
					BHelper.showToast(R.string.msg_company_no_machine_code_len);
					edMachineCode.requestFocus();
					return;
				}
				if(!isDeviceReady){
					BHelper.db("Device is not ready");
					BHelper.showToast(R.string.device_not_ready);
					startDevice();
					isWaitForKeyBinding = true;
					return;
				}else {
					downloadDaou(at);
				}

			}
		});
		btnSaveCompany.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveCompany();
			}
		});
		btnKeyBindingRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				keyBinding();
//				renewalKey();
			}
		});
		
		if (StaticData.IS_TEST) {
			edCompanyNo.setText(StaticData.JTNET_TEST_COMPANY_NO);
			edMachineCode.setText(StaticData.JTNET_TEST_MACHINE_CODE);
		}

		//Load for caller
		String isCalled = "";
		String reqParaJson="";
		if(getArguments()!=null){
			isCalled = getArguments().getString("isCalled","");
			reqParaJson =getArguments().getString("reqParaJson","");
		}
		BHelper.db("isCalled:" + isCalled);
		BHelper.db("reqParaJson:"+reqParaJson);
		StaticData.setToExit(false);
		if (reqParaJson!=null && !reqParaJson.equals("") && isCalled != null && isCalled.equals("true")) {

			StaticData.setIsCalled(true);
			ReqPara reqPara = ReqPara.fromJsonString(reqParaJson);
			final String companyNo = reqPara.getCompanyNo();
			final String machineNo = reqPara.getMachineNo();
			edCompanyNo.setText(companyNo);
			edMachineCode.setText(machineNo);
			MainActivity.lockLeftRightMenu(true);
		}

		btnReaderOnOff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleReaderConfig();
			}
		});
		tvBTReaderName.setOnClickListener(clickListener);

	}

	void toggleReaderConfig(){
        BHelper.db("toggleReaderConfig:"+llReaderConfig.getVisibility());
		if(llReaderConfig.getVisibility()== View.GONE){
            showFallbackDlg(getString(R.string.msg_have_to_connect_adio_or_turn_on_bluetooth));
//            if(emvReader!=null)
//			    emvReader.stopConnection();
			MainActivity.isRequiredWait = false;
			llReaderConfig.setVisibility(View.VISIBLE);
		}else {
			llReaderConfig.setVisibility(View.GONE);
		}
		btnDownload.setEnabled(true);
		btnDownload.setSelected(false);
	}
	void startDevice(){
		if (emvReader!=null && emvReader.emvSwipeController!=null && emvReader.emvSwipeController.getConnectionMode().equals(BBDeviceController.ConnectionMode.NONE)){
//			attachService();
			if(AppHelper.getReaderType()== EmvReader.READER_TYPE_BT)
				connectBT();
			else{
                isDeviceReady = true;
                emvReader.restartAudio();
            }

		}
	}
	void connectBT() {
		BTReaderInfo btReaderInfo = AppHelper.getBTReaderInfo();

		BHelper.db("connecting BT:" + btReaderInfo.getName() + ", " + btReaderInfo.getAddress());
		if (btReaderInfo!=null && !btReaderInfo.getName().equals("")) {
			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(btReaderInfo.getAddress());
			try {
				emvReader.connectBT(device);
			} catch (Exception ex) {
				ex.printStackTrace();
				BHelper.showToast(R.string.msg_connect_bt_eror);
			}
		} else {
			BHelper.showToast(R.string.bluetooth_not_configured);
		}
	}
    boolean isWaitForKeyBinding =false;


	protected void renewalKey(){
		if(!isDeviceReady){
			BHelper.db("Device is not ready");
			BHelper.showToast(R.string.device_not_ready);
			startDevice();
			isWaitForKeyBinding = true;
			return;
		}
		showDialog(R.string.msg_keybinding_during);
		EmvReader.isManualKeyBinding = true;
		renewalRequset(publicKey);
	}

	protected void keyBinding() {
		if(!isDeviceReady){
			BHelper.db("Device is not ready");
			BHelper.showToast(R.string.device_not_ready);
            startDevice();
            isWaitForKeyBinding = true;
			return;
		}
		showDialog(R.string.msg_keybinding_during);
		EmvReader.isManualKeyBinding = true;
        certificateRequest();
	}

    private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			String str = s.toString();
			if (!str.equals("")) {
				str = (str.contains("-") ? str.replace("-", "") : str);
				if (str.length() > 3 && str.length() < 6) {
					str = str.substring(0, 3) + "-" + str.substring(3);
				} else if (str.length() >= 6) {
					str = str.substring(0, 3) + "-" + str.substring(3, 5) + "-" + str.substring(5);
				}
			}
			edCompanyNo.removeTextChangedListener(this);
			edCompanyNo.setText(str);
			edCompanyNo.setSelection(str.length());
			edCompanyNo.addTextChangedListener(this);

		}
	};

	void downloadDaou(Activity at){
		final DaouData daouData = new OpenTerminal();
		final TerminalInfo terminalInfo = new TerminalInfo();
		terminalInfo.setTerCompanyNo(edCompanyNo.getText().toString().trim());
		terminalInfo.setTerNumber(edMachineCode.getText().toString().trim());
		new MyTaskStr(at){

			@Override
			public String[] run() {

				return daouData.req(terminalInfo);
			}

			@Override
			public boolean res(String[] result) {
				if(result[1].equals("0000")){
					comEntity = DaouDataHelper.parseToCompany(result);
                    comEntity.setF_CompanyNo(terminalInfo.getTerCompanyNo());
                    comEntity.setF_MachineCode(terminalInfo.getTerNumber());
					String vanInfo = result[9];
					String vanIP = vanInfo.substring(24,39).trim();
					String vanPort = vanInfo.substring(39,45).trim();
					BHelper.db("vanIP:"+vanIP + ", vanPort:"+vanPort);
					AppHelper.setVanIp(vanIP);
					AppHelper.setVanPort(vanPort);
					ShowResult();
					publicKey = result[15];
//					String data = DaouDataHelper.getData2KeyExchange(publicKey);
//					emvReader.injectMasterKey(data);
				}else {
					String msg =result[21];
					BHelper.showToast(msg);
					btnSaveCompany.setEnabled(false);
					btnSaveCompany.setSelected(true);
					btnKeyBindingRegister.setEnabled(false);
				}
				return false;
			}
		};
	}


	void renewalRequset(String publicKey){
		terInfo = TerminalInfo.parseFromCompany(comEntity);
		new MyTaskStr(at){
			@Override
			public String[] run() {
				daouData = new RenewalKey();
				return daouData.req(terInfo);
			}

			@Override
			public boolean res(String[] result) {
				String base64 = result[15];
				String data = DaouDataHelper.getData2KeyExchange(base64);
				emvReader.keyExchnage(data);
				return false;
			}
		};

	}
    void certificateRequest(){
        terInfo = TerminalInfo.parseFromCompany(comEntity);
        new MyTaskStr(at){
            @Override
            public String[] run() {
                daouData = new SecurityCertificate();
                return daouData.req(terInfo);
            }

            @Override
            public boolean res(String[] result) {
                String base64 = result[4];
                String data = DaouDataHelper.getData2KeyExchange(base64);
                emvReader.keyExchnage(data);
                return false;
            }
        };


    }

    void securityKeyDownload(final String deviceRandomKey) {
        new MyTaskStr(at){

            @Override
            public String[] run() {
                daouData = new SecurityKeyDownload(deviceRandomKey);
                return daouData.req(new TerminalInfo());
            }

            @Override
            public boolean res(String[] result) {
                String base64Key = result[4];
                String mac  = result[5];

                byte[] hexKey = Base64Utils.base64Decode(base64Key);
                byte[] hexMac = Base64Utils.base64Decode(mac);

                String hexKeyStr = HexDump.toHexString(hexKey);
                String hexMacStr = HexDump.toHexString(hexMac);
                String data = hexKeyStr + hexMacStr;
                BHelper.db("hexData:"+ data);
                BHelper.db("Step 2.2 call injectMasterKey");
                BHelper.db("Step 2.3 Wait for callback onReturnInjectMasterKeyResult");
                emvReader.injectMasterKey(data);
                return false;
            }
        };
    }

    protected void showDialog(int rsid) {
//		showStatus("");
		if(dialog==null || !dialog.isShowing()){
//			dialog = new MyProgressDialog(at);
			dialog = DialogHelper.makeDialog(rsid);
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
			//dialog.setMessage(at.getString(rsid));
		}
	}
	protected void showDialogProgress(int rsid) {
		if (dialog == null || !dialog.isShowing()) {
			dialog = DialogHelper.makeDialog(rsid);
			dialog.setTitle(null);
			dialog.setMessage(at.getString(rsid));
			dialog.setCancelable(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setMax(100);
			dialog.setIndeterminate(false);
			dialog.show();
		}
	}
	void showDialog() {
		diglog = DialogHelper.makeDialog(R.string.log_conten);
		diglog.setTitle(getString(R.string.log_title));
		diglog.setMessage(getString(R.string.log_conten));
		diglog.setIndeterminate(true);
		diglog.setCancelable(false);
		diglog.show();
	}

	private void ShowResult() {
		btnSaveCompany.setEnabled(true);
		btnSaveCompany.setSelected(false);
		
		loResult.setVisibility(View.VISIBLE);
		tvComaddress.setText(comEntity.getF_CompanyAddress().trim());
		tvComName.setText(comEntity.getF_CompanyName().trim());
		tvComNo.setText(Helper.formatCompanyNo(comEntity.getF_CompanyNo()));
		tvVanName.setText(vanName);
		tvComPhone.setText(comEntity.getF_CompanyPhoneNo());
		tvVanPhoneNo.setText(comEntity.getF_VanPhoneNo());
		tvResellerPhoneNo.setText(comEntity.getF_ResellerPhoneNo());
		Toast.makeText(at, getString(R.string.msg_download_company_success), Toast.LENGTH_LONG).show();
	}

	private void saveCompany() {
		if (comEntity == null) {
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new TaskSaveCompany().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new TaskSaveCompany().execute();
		}
	}

	private void reset() {
//		edCompanyNo.setText("");
//		edMachineCode.setText("");
		tvComaddress.setText("");
		tvComName.setText("");
		tvComNo.setText("");
		tvVanName.setText("");
		tvComPhone.setText("");
		btnSaveCompany.setEnabled(false);
		btnSaveCompany.setSelected(true);
		btnKeyBindingRegister.setEnabled(false);
		loResult.setVisibility(View.GONE);
	}

	boolean doCaller4(String calledRegno, String machineNo, String transType, String payType, String reqParaJson) {
		BHelper.db("doCaller4");

		//save ReqPara to use to return
		AppHelper.resetCallerReq();
		AppHelper.resetCallerCancelRes();
		AppHelper.setCallerReq(reqParaJson);
		PaymentsFragment fragment = new PaymentsCreditFragment();

		if (transType.equals(ParaConstant.TRANS_TYPE_CANCEL)) {
			if (payType != null && payType.equals(ParaConstant.PAYMENT_TYPE_CREDIT)) {
				fragment = new CancelPaymentFragment();
			} else if (payType != null && payType.equals(ParaConstant.PAYMENT_TYPE_CASH)) {
				fragment = new CancelCashFragment();
			} else {
				BHelper.showToast("Payment type is incorrect");
				ResPara.returnFail(at);
				return false;
			}
		} else if (transType.equals(ParaConstant.TRANS_TYPE_APPROVE)) {
			if (payType != null && payType.equals(ParaConstant.PAYMENT_TYPE_CREDIT)) {
				// do nothing.
			} else if (payType != null && payType.equals(ParaConstant.PAYMENT_TYPE_CASH)) {
				fragment = new PaymentsCashFragment();
			} else {
				BHelper.showToast("Payment type is incorrect");
				ResPara.returnFail(at);
				return false;
			}
		} else {
			BHelper.showToast("Transaction type is incorrect");
			ResPara.returnFail(at);
			return false;
		}
		Bundle args = new Bundle();
		args.putString("isCalled", "true");
		args.putString("reqParaJson", reqParaJson);
//		CompanyEntity comE = CompanyManger.getCompany(calledRegno, machineNo);
//		AppHelper.prefSet(StaticData.VANID, comE.getF_ID());
		AppHelper.prefSet(StaticData.COMPANY_NO, calledRegno);
		AppHelper.prefSet(StaticData.MACHINE_CODE, machineNo);
		fragment.setArguments(args);
		MainActivity.setFragment(fragment);
		return false;
	}

	public class TaskSaveCompany extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog();
		}

		@Override
		protected String doInBackground(Void... params) {
			CompanyEntity com = addCompany(comEntity);
            return CompanyManger.insertCompanyJson(com);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				comEntity.setF_ID(result);
				if (CompanyManger.insertCompany(comEntity)) {
					AppHelper.setCurrentVanID(result);
					Toast.makeText(at, getString(R.string.msg_insert_company_success), Toast.LENGTH_LONG).show();
					MainActivity main = (MainActivity) getActivity();
					main.initMenuLeft();
					reset();
					btnKeyBindingRegister.setEnabled(true);

					//check for caller
					String isCalled = "";
					String reqParaJson="";
					if(getArguments()!=null){
						isCalled = getArguments().getString("isCalled","");
						reqParaJson =getArguments().getString("reqParaJson","");
					}
					BHelper.db("isCalled:" + isCalled);
					BHelper.db("reqParaJson:"+reqParaJson);
					StaticData.setToExit(false);
					if (reqParaJson!=null && !reqParaJson.equals("") && isCalled != null && isCalled.equals("true")) {

						StaticData.setIsCalled(true);
						ReqPara reqPara = ReqPara.fromJsonString(reqParaJson);
						final String vanName = StaticData.vanNameDaouData;
						final String companyNo = reqPara.getCompanyNo();
						final String machineNo = reqPara.getMachineNo();
						final String payType = reqPara.getPaymentType();
						final String transType = reqPara.getTransType();
						doCaller4(companyNo,machineNo,transType,payType,reqParaJson);
					}

				} else
					Toast.makeText(at, getString(R.string.msg_insert_company_failed), Toast.LENGTH_LONG).show();
			} else
				Toast.makeText(at,getString(R.string.msg_insert_company_to_server_failed), Toast.LENGTH_LONG).show();
			diglog.dismiss();
		}
	}

	private CompanyEntity addCompany(CompanyEntity comE) {
		String sServiceTax = edServicechargeset.getText().toString();
		String sTax = edTaxsetting.getText().toString();
		comE.setF_UserID(AppHelper.getCurrentUserID());
		comE.setF_ServiceTaxRate(sServiceTax.equals("") ? "0" : sServiceTax);
		comE.setF_TaxRate(sTax.equals("") ? "0" : sTax);
		comE.setCREATE_UID(AppHelper.getCurrentUserID());
		comE.setUPDATE_UID(AppHelper.getUpdateUserID());
		comE.setF_PhoneNo(Helper.GetMyPhone(at));
		if (cbWithTax.isChecked())
			comE.setF_WithTax(true);
		else
			comE.setF_WithTax(false);
		comE.setF_VanName(vanName);
		return comE;
	}

	@Override
	public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
		// TODO Auto-generated method stub
BHelper.db("------------onReturnDeviceInfo in Integrity Check-------------");
		
		isDeviceReady =true;
		String firmwareVersion = deviceInfoData.get("firmwareVersion") == null? "" : deviceInfoData.get("firmwareVersion");
		String pinKsn = deviceInfoData.get("pinKsn") == null? "" : deviceInfoData.get("pinKsn");
		String trackKsn = deviceInfoData.get("trackKsn") == null? "" : deviceInfoData.get("trackKsn");
		String emvKsn = deviceInfoData.get("emvKsn") == null? "" : deviceInfoData.get("emvKsn");
		String uid = deviceInfoData.get("uid") == null? "" : deviceInfoData.get("uid");
		String csn = deviceInfoData.get("csn") == null? "" : deviceInfoData.get("csn");
		String serialNumber = deviceInfoData.get("serialNumber")==null? DaouDataContants.VAL_PRODUCTION_SERIAL_NUMBER:deviceInfoData.get("serialNumber");
		BHelper.db("set JTNet.SoftVer:"+firmwareVersion);

		Set<String> keys = deviceInfoData.keySet();
        for(String key: keys){
        	BHelper.db(key+":"+deviceInfoData.get(key));
        }
        String modelName = deviceInfoData.get("modelName") == null? "" : deviceInfoData.get("modelName");
        com.devcrane.payfun.cardreader.EmvUtils.saveHWModelName(modelName);
		String publicKeyVersion = deviceInfoData.get("publicKeyVersion") == null? "" : deviceInfoData.get("publicKeyVersion");
		EmvUtils.savePublicKeyVersion(publicKeyVersion);
		EmvUtils.saveHwSerialNumber(serialNumber);
//		BHelper.db(content);
		String deviceSerial = EmvUtils.extractSerialNumber(pinKsn);
		KeyBindingEntity bindingEntity = com.devcrane.payfun.cardreader.EmvUtils.getKeyBinding(deviceSerial);
		//didn't select company
		if(bindingEntity ==null){
			return;
		}
		EmvUtils.saveEmvSerial(pinKsn);
		EmvUtils.saveHwSerialNumber(serialNumber);
		BHelper.db("saved bindingEntity:"+bindingEntity.toString());
		BHelper.db("EmvUtils.getEmvFirmwareVer():"+bindingEntity.getF_FirmwareVersion());
		BHelper.db("EmvUtils.getKsn():"+bindingEntity.getF_PinKsn());
		String currentSerial = EmvUtils.extractSerialNumber(bindingEntity.getF_PinKsn());
		BHelper.db("deviceSerial:"+deviceSerial);
		BHelper.db("currentSerial:"+currentSerial);
//		if(!bindingEntity.getF_FirmwareVersion().equals(firmwareVersion) 
//				||  bindingEntity.getF_PinKsn().equals("")
//				|| !deviceSerial.equals(currentSerial)){
			try {
//				EmvUtils.saveEmvFirmwareVer(firmwareVersion);//kiem tra lai gia tri ksn trong device return va ksn	
				
				Thread.sleep(1000);
				KeyBindingEntity entity = new KeyBindingEntity("");
				entity.setF_Csn(csn);
				entity.setF_DeviceNo(deviceSerial);
				entity.setF_EmvKsn(emvKsn);
				entity.setF_FirmwareVersion(firmwareVersion);
				entity.setF_PinKsn(pinKsn);
				entity.setF_TrackKsn(trackKsn);
				entity.setF_Uid(uid);
				com.devcrane.payfun.cardreader.EmvUtils.saveKeyBinding(entity);
				entity = null;
				emvReader.getKsn();
				updateDialogMsg(getString(R.string.msg_keybinding_during) + " 20%");

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			bindingEntity = null;
			firmwareVersion = pinKsn = trackKsn = emvKsn =uid = csn = deviceSerial = modelName = currentSerial = "";
	
	}

//	@Override
//	public void ksnCallback(KSNEntity ksnReturnEntity) {
//
//		if(!isUpdateKsn){
//			BHelper.db("save KsnEntity to Reader to use to requestKeyBinding from DaouData");
//			updateDialogMsg(getString(R.string.msg_keybinding_during) + " 40%");
//		}else{
//
//		}
//	}
	
	@Override
	public void keyExchangeCallback(
			KeyExchangeResultEntity exchangeResultEntity) {
		BHelper.db("exchangeResultEntity:"+exchangeResultEntity.toString());
//		if(!isRenewal){
//			isRenewal = true;
//			keyBinding();
//			return;
//		}
		BHelper.db("Step 1.3 call DaouData securityKeyDownload");
		BHelper.db("Step 2.1 Wait DaouData response message");
        BHelper.db("keyExchangeCallback:"+exchangeResultEntity.toString());
        String hexRandom = exchangeResultEntity.getRandValue();
        String base64Random = Base64Utils.base64Encode(HexDump.hexStringToByteArray(hexRandom));

		updateDialogMsg(getString(R.string.msg_keybinding_during) + " 80%");
		if(exchangeResultEntity.getEncRandValue()==null){
			dismissPDialog();
			BHelper.showToast(R.string.msg_keybiding_error);
			return;
		}
        securityKeyDownload(base64Random);

		isUpdateKsn = true;

		BHelper.db("Update Ksn from device to injectMaterKey");
//		emvReader.getKsn();
//		BHelper.db("save KsnEntity to Reader to use to injectMasterKey");
		isUpdateKsn = false;
	}

    @Override
	public void injectMasterKeyCallback(String resultMsg) {
//		if(!isGetPublicKey){
//			isGetPublicKey = true;
//			return;
//		}

		if(resultMsg==null || resultMsg.equals("")){
			updateDialogMsg(getString(R.string.msg_keybinding_during) + " 100%");
			BHelper.showToast(R.string.msg_key_binding_success);
			AppHelper.setKeyBindingYear();
		}
		// TODO Auto-generated method stub
		BHelper.db("injectMasterKeyCallback:"+ resultMsg);
		closeDialog();
		
		BHelper.showToast(R.string.msg_keybiding_success);
		
//		BHelper.db("1.1 Start check Card");
//		checkCard(CheckCardMode.INSERT);
//		isReadyICPayment = false;
	}

	@Override
	public void onReturnIntegrityCheckResult(boolean result) {
		// TODO Auto-generated method stub
		BHelper.db("MANUAL: onReturnIntegrityCheckResult:"+result);
		Log.e("Jonathan", "Jonathan1");
		String logData = "INTERGRITY CHECK";
		logData+="\nResult:"+result;
		Helper.writeIntegrityLog(logData);
		logData = "";
		if(result == true) {
			BHelper.showToast(R.string.emv_integrity_check_success);
		}else{
			showFallbackDlg(at.getString(R.string.msgIntegrityCheckFail));
		}
		emvReader.getDeviceInfo();
	}

	@Override
	public void onDeviceHere(boolean isHere) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeviceUnplugged() {
		BHelper.restoreVolumn(at);
		isDeviceReady = false;
		closeDialog();
		showFallbackDlg(at.getString(R.string.device_unplugged));
	}

	@Override
	public void onDevicePlugged() {
		// TODO Auto-generated method stub
		isDeviceReady =true;
		showToast(at.getString(R.string.device_plugged));
	}

	

	@Override
	public void onNoDeviceDetected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(BBDeviceController.Error errorState) {
		// TODO Auto-generated method stub

		if(MainActivity.isWaitTurnOnBT && !MainActivity.isBTReaderConnected)
			return;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeDialog();
		showToast(EmvUtils.getEmvErrorString(at, errorState));
		if(errorState.equals(BBDeviceController.Error.DEVICE_BUSY)){
			emvReader.sendOnlineProcessResult(null);
		}
		else if(errorState.equals(BBDeviceController.Error.COMM_LINK_UNINITIALIZED)||errorState.equals(BBDeviceController.Error.UNKNOWN)||errorState.equals(BBDeviceController.Error.COMM_ERROR)){
			if(AppHelper.getReaderType()!= EmvReader.READER_TYPE_BT)
				showAutoConfigConfirm(getString(R.string.msg_config_device_ask));
		}
		
	}

	protected void showAutoConfigConfirm(String msg) {
		fallbackDlg = new AlertDialog.Builder(at)
				.setTitle(R.string.emv_fallback_report)
				.setIcon(android.R.drawable.ic_dialog_alert).setMessage(msg)
				.setPositiveButton("예", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						showDialogProgress(R.string.msg_config_device_doing);
						emvReader.startAutoConfig();
					}
				}).show();
	}

	@Override
	public void onAutoConfigCompleted(boolean isDefaultSettings,
			String autoConfigSettings) {
		// TODO Auto-generated method stub
		emvReader.setAutoConfig();
		closeDialog();
		BHelper.db("auto config is completed");
		showToast(getString(R.string.msg_config_device_success));
	}

	@Override
	public void onAutoConfigError(BBDeviceController.AudioAutoConfigError autoConfigError) {
		// TODO Auto-generated method stub
		closeDialog();
		BHelper.db("auto config is error");
		showToast(getString(R.string.msg_config_device_failed));
	}

	@Override
	public void onAutoConfigProgressUpdate(double percentage) {
		// TODO Auto-generated method stub
		BHelper.db("config percent:"+ (int)percentage);
		if(dialog!=null && dialog.isShowing()){
			updateDialogMsg(getString(R.string.msg_config_device_doing)+ " " +(int)percentage+ " %");
		}
	}
	void loadDefaultReaderInfo(){
		BTReaderInfo btReaderInfo = AppHelper.getBTReaderInfo();
		if(btReaderInfo.getName().equals("")){
			BTHelper.savePairedBT(currentActivity);
		}
		loadBTReaderInfo();

	}
	void initReaderType(){
		rdgReadeType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				String readerType = ((RadioButton)radioGroup.findViewById(i)).getText().toString();
				String readerTypeBT =getString(R.string.reader_type_bluetooth);

				if(readerType.equals(readerTypeBT)){
					AppHelper.setReaderType(String.valueOf(EmvReader.READER_TYPE_BT));
				}else{
					AppHelper.setReaderType(String.valueOf(EmvReader.READER_TYPE_EARJACK));
				}
				BHelper.db("checked:"+ i+ readerType + "    "+ readerTypeBT);
                isDeviceReady = false;
				boolean isBT = false;
				if(AppHelper.getReaderType()== EmvReader.READER_TYPE_BT){
					isBT = true;
				}

				EmvReader.setIsBlueTooth(isBT);

			}
		});
		if(AppHelper.getReaderType()== EmvReader.READER_TYPE_BT)
			rdbBT.setChecked(true);
		else
			rdbEarjack.setChecked(true);
	}

	//region WisePad
	static ArrayAdapter<String> arrayAdapter;
	static List<BluetoothDevice> foundDevices;
	static Dialog wiseDialog;
	static final String[] DEVICE_NAMES = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	public void dismissDialog() {
		if (wiseDialog != null) {
			wiseDialog.dismiss();
			wiseDialog = null;
		}
	}
	void loadBTReaderInfo(){
		BTReaderInfo btReaderInfo = AppHelper.getBTReaderInfo();
		tvBTReaderName.setText(btReaderInfo.getName());
	}
	View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.tvBTReaderName:
					if(AppHelper.getReaderType()== EmvReader.READER_TYPE_BT){
                        if(emvReader!=null)
                            emvReader.stopConnection();
                        try{
                            Thread.sleep(1000);
                            promptForConnection();
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }

					break;
				default:
					break;
			}
		}
	};
	public void promptForConnection() {
		dismissDialog();
		wiseDialog = new Dialog(currentActivity);
		wiseDialog.setContentView(R.layout.connection_dialog);
		wiseDialog.setTitle(this.getString(R.string.connection));

		String[] connections = new String[1];
		connections[0] = "Bluetooth";
//		connections[1] = "Audio";

//		ListView listView = (ListView) wiseDialog.findViewById(R.id.connectionList);
//		listView.setAdapter(new ArrayAdapter<String>(currentActivity, android.R.layout.simple_list_item_1, connections));
//		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				dismissDialog();
//				if (position == 0) {
					Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
					final BluetoothDevice[] pairedDevices = new BluetoothDevice[pairedObjects.length];
					for (int i = 0; i < pairedObjects.length; ++i) {
						pairedDevices[i] = (BluetoothDevice) pairedObjects[i];
					}

					final ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(currentActivity, android.R.layout.simple_list_item_1);
					for (int i = 0; i < pairedDevices.length; ++i) {
						String deviceName = pairedDevices[i].getName();
						if(deviceName.contains(getString(R.string.bt_device_prefix)))
							mArrayAdapter.add(deviceName);
					}

					dismissDialog();
					wiseDialog = new Dialog(currentActivity);
					wiseDialog.setContentView(R.layout.bluetooth_2_device_list_dialog);
					wiseDialog.setTitle(R.string.bluetooth_devices);

					ListView listView1 = (ListView) wiseDialog.findViewById(R.id.pairedDeviceList);
					listView1.setAdapter(mArrayAdapter);
					listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							BHelper.db("connecting..."+pairedDevices[position].getName());
							BTReaderInfo btReaderInfo = new BTReaderInfo(pairedDevices[position].getName(),pairedDevices[position].getAddress());
//							emvReader.connectBT(pairedDevices[position]);
							AppHelper.setBTReaderInfo(btReaderInfo);
							loadBTReaderInfo();
							dismissDialog();
						}

					});

					arrayAdapter = new ArrayAdapter<String>(currentActivity, android.R.layout.simple_list_item_1);
					ListView listView2 = (ListView) wiseDialog.findViewById(R.id.discoveredDeviceList);
					listView2.setAdapter(arrayAdapter);
					listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							BHelper.db("connecting..."+ getString(R.string.connecting_bluetooth));
//                            statusEditText.setText(at.getString(R.string.connecting_bluetooth));
//							emvReader.connectBT(foundDevices.get(position));
							BTReaderInfo btReaderInfo = new BTReaderInfo(foundDevices.get(position).getName(),foundDevices.get(position).getAddress());
//							emvReader.connectBT(pairedDevices[position]);
							AppHelper.setBTReaderInfo(btReaderInfo);

							loadBTReaderInfo();
							dismissDialog();
						}

					});

					wiseDialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
                            BHelper.db("emvReader.stopBTScan()");
							emvReader.stopBTScan();
							dismissDialog();
						}
					});
					wiseDialog.setCancelable(false);
					wiseDialog.show();
                    BHelper.db("emvReader.startBTScan()");
					emvReader.startBTScan(DEVICE_NAMES, 120);
//				}
//                else if (position == 1) {
//					emvReader.startAudio();
//				}
//			}
//
//		});

		wiseDialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
                BHelper.db("emvReader.stopBTScan()");
                emvReader.stopBTScan();
				dismissDialog();
			}
		});

		wiseDialog.show();
	}

	@Override
	public void onBTReturnScanResults(List<BluetoothDevice> list) {
		BHelper.db("onBTReturnScanResults in Config: "+ list.size());
		foundDevices = new LinkedList<BluetoothDevice>();
		if (arrayAdapter != null) {
			arrayAdapter.clear();
			for (int i = 0; i < list.size(); ++i) {
				BHelper.db("found device: "+ list.get(i).getName());
				String deviceName = list.get(i).getName();
				if(deviceName.contains(getString(R.string.bt_device_prefix))){
					arrayAdapter.add(deviceName);
					foundDevices.add(list.get(i));
				}
			}
			arrayAdapter.notifyDataSetChanged();
		}else {
			BHelper.db("arrayAdapter is null");
		}
	}

	@Override
	public void onBTScanTimeout() {

	}

	@Override
	public void onBTScanStopped() {

	}

	@Override
	public void onBTConnected(BluetoothDevice bluetoothDevice) {
        BHelper.db("BT connected in Register Van");
        isDeviceReady = true;
        if(isWaitForKeyBinding){
            showDialog(R.string.msg_keybinding_during);
            EmvReader.isManualKeyBinding = true;
            certificateRequest();
            isWaitForKeyBinding = false;
        }
	}

	@Override
	public void onBTDisconnected() {
        BHelper.db("BT disconnected in Register Van");
        isDeviceReady = false;
        closeDialog();
	}

	private KeyboardUtil ku;

	private void showKeyboard(EditText editText) {
		BHelper.db("showKeyboard");
//		if (ku == null)
		if (true) {
			ku = new KeyboardUtil(at, at, editText);
			ku.setShowListener(new KeyboardUtil.onShowCloseListener() {
				@Override
				public void show() {

				}

				@Override
				public void onPush() {

				}

				@Override
				public void close() {

				}
			});
		}
		if (ku.showKeyboard()) {
			ku.startShow();
		}
	}

	public void disableShowSoftInput(EditText editText) {
		if (android.os.Build.VERSION.SDK_INT <= 10) {
			editText.setInputType(InputType.TYPE_NULL);
		} else {
			Class<EditText> cls = EditText.class;
			Method method;
			try {
				method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
				method.setAccessible(true);
				method.invoke(editText, false);
			} catch (Exception e) {
				// TODO: handle exception
			}

			try {
				method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
				method.setAccessible(true);
				method.invoke(editText, false);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
