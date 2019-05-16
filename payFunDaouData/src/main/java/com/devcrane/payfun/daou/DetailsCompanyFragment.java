package com.devcrane.payfun.daou;

import java.util.Hashtable;
import java.util.Set;


import com.bbpos.bbdevice.BBDeviceController;
import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.android.lib.emvreader.IntegrityCheckListener;
import com.devcrane.android.lib.entity.KeyExchangeResultEntity;
import com.devcrane.payfun.cardreader.EmvUtils;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.BHelper.DialogHelper;
import com.devcrane.payfun.daou.utility.Base64Utils;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.MyTaskStr;
import com.devcrane.payfun.daou.utility.PaymentTask;
import com.devcrane.payfun.daou.van.DaouData;
import com.devcrane.payfun.daou.van.DaouDataContants;
import com.devcrane.payfun.daou.van.DaouDataHelper;
import com.devcrane.payfun.daou.van.HexDump;
import com.devcrane.payfun.daou.van.SecurityCertificate;
import com.devcrane.payfun.daou.van.SecurityKeyDownload;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class DetailsCompanyFragment extends Fragment implements IntegrityCheckListener{
	TextView tvVanName, tvComNo, tvComName, tvComaddress, tvComPhone;
	CheckBox cbWithtax;
	Button btnSaveCompany, btnKeyBinding,btnIntegrityCheck,btnIntegrityLog,btnDeleteCompany,btnReaderConfig;
	EditText edTaxSetting, edServiceSetting;
	LinearLayout locomdetail0;

	Activity at;
	CompanyEntity comEntity;
	
	ProgressDialog dialog;
	Dialog pDialog;
	AlertDialog fallbackDlg;
	static boolean isUpdateKsn = false;
	EmvReader emvReader;

	boolean isDeviceReady = false;
	static final int SHOWING_DIALOG_LIMIT = 1;
	static int showingDialogCount = 0;
	TerminalInfo terInfo;
	DaouData daouData;
	void initEmvResources(){
		emvReader = MainActivity.getEmvReader();
	}
	
	void attachService(){

		if(emvReader!=null){
			emvReader.attachIntegrityCheckListener(DetailsCompanyFragment.this);
		}
	}
	void detachService(){
		if(emvReader!=null){
			emvReader.detachIntegrityCheckListener(DetailsCompanyFragment.this);
			
		}
	}

	public void dismissPDialog() {
    	if(pDialog != null) {
    		pDialog.dismiss();
    		pDialog = null;
    	}
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
		return inflater.inflate(R.layout.fragment_company_details, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
//		MainActivity.lockMenu(true);
		at = getActivity();
		showingDialogCount = 0;
		BHelper.setTypeface(getView());
		onInitView();
		initEmvResources();
		attachService();
		
		
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		if(EmvReader.getIsBlueTooth()){
//			isDeviceReady = true;
//			return;
//		}

//		isDeviceReady = Helper.isHeadsetConnected(at)|| MainActivity.isBTReaderConnected;
		isDeviceReady = Helper.isDeviceReady(at);
		if (isDeviceReady ) {
			
		}else{
			BHelper.showNeedDevice();
		}

	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		detachService();
		closeDialog();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		detachService();
		closeDialog();
	}
	private void onInitView() {
		tvVanName = (TextView) at.findViewById(R.id.txtComdetailVanName);
		tvComNo = (TextView) at.findViewById(R.id.txtComdetailComNo);
		tvComaddress = (TextView) at.findViewById(R.id.txtComdetailAddr);
		tvComPhone = (TextView) at.findViewById(R.id.txtComdetailComphone);
		tvComName = (TextView) at.findViewById(R.id.txtComdetailCoName);
		edServiceSetting = (EditText) at.findViewById(R.id.edComdetailServicechargeset);
		edTaxSetting = (EditText) at.findViewById(R.id.edComdetailTaxsettings);
		cbWithtax = (CheckBox) at.findViewById(R.id.cbComdetailWithTax);
		locomdetail0 = (LinearLayout)at.findViewById(R.id.locomdetail0);
		
		btnKeyBinding = (Button) at.findViewById(R.id.btnKeyBinding);
		btnDeleteCompany = (Button) at.findViewById(R.id.btnDeleteCompany);
		btnSaveCompany = (Button) at.findViewById(R.id.btnComdetailSaveCompany);
		btnIntegrityCheck = (Button)at.findViewById(R.id.btnIntegrityCheck);
		btnIntegrityLog = (Button)at.findViewById(R.id.btnIntegrityLog);
		btnReaderConfig = (Button)at.findViewById(R.id.btnReaderConfig);
		btnKeyBinding.setOnClickListener(onclick);
		btnSaveCompany.setOnClickListener(onclick);
		btnIntegrityCheck.setOnClickListener(onclick);
		btnIntegrityLog.setOnClickListener(onclick);
		btnDeleteCompany.setOnClickListener(onclick);
		btnReaderConfig.setOnClickListener(onclick);
		onInitData();
	}
	
	OnClickListener onclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnKeyBinding:
				doKeyBinding();
				break;
			case R.id.btnDeleteCompany:
				doDeleteCompany();
				break;
			case R.id.btnComdetailSaveCompany:
				doSaveCompany();
				break;
			case R.id.btnIntegrityCheck:
				intergrityCheck();
				break;
			case R.id.btnIntegrityLog:
				doShowInterityLog();
				break;
			case R.id.btnReaderConfig:
				if(AppHelper.getReaderType()!= EmvReader.READER_TYPE_BT) {
					startAutoConfig();
				}else{
					BHelper.showToast(R.string.msg_notEarJack_function);
				}
				break;
			default:
				break;
			}
			
		}
	};
	
	protected void doDeleteCompany() {
		comEntity.setF_UserID(AppHelper.getCurrentUserID());
		new PaymentTask(at) {
			
			@Override
			public String run() {
				// TODO Auto-generated method stub
				return CompanyManger.delete_CompanyJson(comEntity.getF_UserID(), comEntity.getF_CompanyNo(), comEntity.getF_MachineCode());
			}
			
			@Override
			public boolean res(String result) {
				// TODO Auto-generated method stub
				if(result.equals(comEntity.getF_UserID())){
					CompanyManger.delete_Company(comEntity.getF_UserID(),comEntity.getF_CompanyNo(), comEntity.getF_MachineCode());
//					Toast.makeText(at, , Toast.LENGTH_LONG).show();
					BHelper.showToast(R.string.msg_remove_company_success);
					MainActivity main = (MainActivity) getActivity();
					main.initMenuLeft();
					main.onBackPressed();
				}else
					BHelper.showToast(R.string.msg_remove_company_unsuccess);
//					Toast.makeText(at, "Remove company unsuccess!", Toast.LENGTH_LONG).show();
				return true;
			}
		};
		
				
		
	}

	protected void doShowInterityLog() {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater= LayoutInflater.from(at);
		View view=inflater.inflate(R.layout.popup_integrity_log, null);

		TextView textview=(TextView)view.findViewById(R.id.txtMsg);
		textview.setText(Helper.readIntegrityLog());
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(at);  
		alertDialog.setTitle(R.string.popup_title_integrity_log);  
		//alertDialog.setMessage("Here is a really long message.");
		alertDialog.setView(view);
		alertDialog.setPositiveButton("OK", null);  
		AlertDialog alert = alertDialog.create();
		alert.show();
	}
	void intergrityCheck(){
		if(!isDeviceReady){
			BHelper.showToast(R.string.device_not_ready);
			return;
		}
		emvReader.integrityCheck();
	}
	protected void doKeyBinding() {
		// TODO Auto-generated method stub
		
		if(!isDeviceReady){
			BHelper.showToast(R.string.device_not_ready);
			return;
		}
		showDialog(R.string.msg_keybinding_during);
		EmvReader.isManualKeyBinding = true;
		BHelper.db("------------------doKeyBinding---------");
		BHelper.db("------------------doKeyBinding step1: GetDeviceInfo---------");
		emvReader.getDeviceInfo();
//        certificateRequest();
		
		
	}

	private void onInitData() {
		comEntity = CompanyManger.getCompanyByID(StaticData.sCompanyID);
		if (comEntity == null)
			MainActivity.setFragment(new CompanyListFragment());
		tvComaddress.setText(comEntity.getF_CompanyAddress().trim());
		tvComName.setText(comEntity.getF_CompanyName().trim());
		tvComNo.setText(Helper.formatCompanyNo(comEntity.getF_CompanyNo()));
		tvVanName.setText(comEntity.getF_VanName());
		tvComPhone.setText(comEntity.getF_CompanyPhoneNo());
		if (comEntity.getF_WithTax())
			cbWithtax.setChecked(true);
		else
			cbWithtax.setChecked(false);
		edServiceSetting.setText(comEntity.getF_ServiceTaxRate());
		edTaxSetting.setText(comEntity.getF_TaxRate());
	}

	void doSaveCompany() {
		if (cbWithtax.isChecked())
			comEntity.setF_WithTax(true);
		else
			comEntity.setF_WithTax(false);
		String sServiceTax = edServiceSetting.getText().toString();
		String sTax = edTaxSetting.getText().toString();
		comEntity.setF_UserID(AppHelper.getCurrentUserID());
		comEntity.setF_ServiceTaxRate(sServiceTax.equals("") ? "0" : sServiceTax);
		comEntity.setF_TaxRate(sTax.equals("") ? "0" : sTax);
		comEntity.setUPDATE_UID(AppHelper.getUpdateUserID());
		new PaymentTask(at) {

			@Override
			public String run() {
				return CompanyManger.insertCompanyJson(comEntity);
			}

			@Override
			public boolean res(String result) {
				if (result != null) {
					comEntity.setF_ID(result);
					if (CompanyManger.insertCompany(comEntity)) {
						MainActivity main = (MainActivity) getActivity();
						main.initMenuLeft();
						Toast.makeText(at, "Update company success!", Toast.LENGTH_LONG).show();
					} else
						Toast.makeText(at, "Update company failed!", Toast.LENGTH_LONG).show();
				} else
					Toast.makeText(at, "Update company to server failed! ", Toast.LENGTH_LONG).show();
				return false;
			}
		};
	}
	@Override
	public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
		
		BHelper.db("------------onReturnDeviceInfo in Integrity Check-------------");
		if(!EmvReader.isManualKeyBinding)
			return;
		
		isDeviceReady = true;
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
//		BHelper.db(content);
		String deviceSerial = EmvUtils.extractSerialNumber(pinKsn);

		String publicKeyVersion = deviceInfoData.get("publicKeyVersion") == null? "" : deviceInfoData.get("publicKeyVersion");
		EmvUtils.savePublicKeyVersion(publicKeyVersion);
		EmvUtils.saveHwSerialNumber(serialNumber);

		certificateRequest();

//		KeyBindingEntity bindingEntity = com.devcrane.payfun.cardreader.EmvUtils.getKeyBinding(deviceSerial);
		//didn't select company
//		if(bindingEntity ==null){
//			return;
//		}
//
//
//
//		EmvUtils.saveEmvSerial(pinKsn);
//		BHelper.db("saved bindingEntity:"+bindingEntity.toString());
//		BHelper.db("EmvUtils.getEmvFirmwareVer():"+bindingEntity.getF_FirmwareVersion());
//		BHelper.db("EmvUtils.getKsn():"+bindingEntity.getF_PinKsn());
//		String currentSerial = EmvUtils.extractSerialNumber(bindingEntity.getF_PinKsn());
//		BHelper.db("deviceSerial:"+deviceSerial);
//		BHelper.db("currentSerial:"+currentSerial);
////		if(!bindingEntity.getF_FirmwareVersion().equals(firmwareVersion)
////				||  bindingEntity.getF_PinKsn().equals("")
////				|| !deviceSerial.equals(currentSerial)){
//			try {
////				EmvUtils.saveEmvFirmwareVer(firmwareVersion);//kiem tra lai gia tri ksn trong device return va ksn
//				Thread.sleep(1000);
//				KeyBindingEntity entity = new KeyBindingEntity("");
//				entity.setF_Csn(csn);
//				entity.setF_DeviceNo(deviceSerial);
//				entity.setF_EmvKsn(emvKsn);
//				entity.setF_FirmwareVersion(firmwareVersion);
//				entity.setF_PinKsn(pinKsn);
//				entity.setF_TrackKsn(trackKsn);
//				entity.setF_Uid(uid);
//				com.devcrane.payfun.cardreader.EmvUtils.saveKeyBinding(entity);
//				entity = null;
//				emvReader.getKsn();
//				updateDialogMsg(getString(R.string.msg_keybinding_during) + " 20%");
//
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			bindingEntity = null;
			firmwareVersion = pinKsn = trackKsn = emvKsn =uid = csn = deviceSerial = modelName = "";//currentSerial = "";
	}
	@Override
	public void onDeviceUnplugged() {
		BHelper.restoreVolumn(at);
		closeDialog();
		showFallbackDlg(at.getString(R.string.device_unplugged));
		isDeviceReady = false;
	}

	@Override
	public void onDevicePlugged() {
		// TODO Auto-generated method stub
		showToast(at.getString(R.string.device_plugged));
		isDeviceReady = true;
		showingDialogCount = 0;
	}

	
	@Override
	public void onDeviceHere(boolean isHere) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyExchangeCallback(
			KeyExchangeResultEntity exchangeResultEntity) {
        BHelper.db("exchangeResultEntity:"+exchangeResultEntity.toString());
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

        isUpdateKsn = false;
	}
	
	@Override
	public void injectMasterKeyCallback(String resultMsg) {
        if(resultMsg==null || resultMsg.equals("")){
            updateDialogMsg(getString(R.string.msg_keybinding_during) + " 100%");
            BHelper.showToast(R.string.msg_key_binding_success);
			AppHelper.setKeyBindingYear();
        }
        // TODO Auto-generated method stub
        BHelper.db("injectMasterKeyCallback:"+ resultMsg);
        closeDialog();

        BHelper.showToast(R.string.msg_keybiding_success);
	}

	@Override
	public void onReturnIntegrityCheckResult(boolean result) {
		// TODO Auto-generated method stub
		BHelper.db("MANUAL: onReturnIntegrityCheckResult:"+result);
		Log.e("Jonathan", "Jonathan2");
		String logData = "INTERGRITY CHECK";
		logData+="\nResult:"+result;
		Helper.writeIntegrityLog(logData);

		if(result == true)
			BHelper.showToast(R.string.emv_integrity_check_success);
		else{
			showFallbackDlg(at.getString(R.string.emv_integrity_check_fail));
		}
//		emvReader.getDeviceInfo();
	}

	@Override
	public void onNoDeviceDetected() {
		// TODO Auto-generated method stub
		closeDialog();
		showFallbackDlg(at.getString(R.string.no_device_detected));
	}
	@Override
	public void onError(BBDeviceController.Error errorState) {
		BHelper.db("onError on DetailCompanyFragment");
		// TODO Auto-generated method stub

		closeDialog();
		if(MainActivity.isWaitTurnOnBT && !MainActivity.isBTReaderConnected)
			return;
		BHelper.showToast(R.string.msg_reconnect_device);
		if(errorState.equals(BBDeviceController.Error.COMM_LINK_UNINITIALIZED) && showingDialogCount<SHOWING_DIALOG_LIMIT){
			showingDialogCount +=1;
			if( Helper.isHeadsetConnected(at) && !EmvReader.getIsBlueTooth()){
				emvReader.restartAudio();
				
			}
		}
//		else{
//			BHelper.showToast(R.string.msg_reconnect_device);
//		}
		
	}

	void startAutoConfig(){
		if(!isDeviceReady){
			BHelper.showToast(R.string.device_not_ready);
			return;
		}
		if(emvReader!=null && emvReader.emvSwipeController!=null) {
			showDialog(R.string.auto_configuring);
			emvReader.startAutoConfig();
			//showAutoConfigConfirm();
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
				})
				.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				})
				.show();
	}
	@Override
	public void onAutoConfigCompleted(boolean isDefaultSettings,
			String autoConfigSettings) {
		// TODO Auto-generated method stub
//		emvReader.setAutoConfig();
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
//			dialog.setProgress((int)percentage);
		
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
				base64Key = mac = "";
				return false;
			}
		};
	}

}
