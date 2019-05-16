package com.devcrane.payfun.daou.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bbpos.bbdevice.BBDeviceController;
import com.devcrane.android.lib.emvreader.BlueToothListener;
import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.android.lib.emvreader.IntegrityCheckListener;
import com.devcrane.android.lib.entity.KeyExchangeResultEntity;
import com.devcrane.payfun.cardreader.EmvUtils;
import com.devcrane.payfun.daou.LoginFragment;
import com.devcrane.payfun.daou.MainActivity;
import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.RegistervanFragment;
import com.devcrane.payfun.daou.caller.ReqPara;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.BTReaderInfo;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.KeyBindingEntity;
import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Base64Utils;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.MyTaskDLVan;
import com.devcrane.payfun.daou.utility.MyTaskStr;
import com.devcrane.payfun.daou.van.DaouData;
import com.devcrane.payfun.daou.van.DaouDataContants;
import com.devcrane.payfun.daou.van.DaouDataHelper;
import com.devcrane.payfun.daou.van.HexDump;
import com.devcrane.payfun.daou.van.SecurityCertificate;
import com.devcrane.payfun.daou.van.SecurityKeyDownload;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 7/19/17.
 */

public class VanDLStep4Dialog extends Dialog implements View.OnClickListener, IntegrityCheckListener, BlueToothListener {
    final String TAG = getClass().getSimpleName();
    private Context mContext;
    VanDLStep4DialogListener onVanDLStep4DialogListener;
    CompanyEntity comEntity;
    ProgressDialog dialog;
    Dialog pDialog;
    boolean isDeviceReady =false;
    EmvReader emvReader;
    DaouData daouData;
    TerminalInfo terInfo;
    boolean isWaitForKeyBinding =false;
    private TextView tvStep;

    EditText edMachineCode;
    public VanDLStep4Dialog(Context context, CompanyEntity comEntity, VanDLStep4DialogListener vanDLStep4DialogListener) {
        super(context);
        mContext = context;
        this.comEntity = comEntity;
        onVanDLStep4DialogListener = vanDLStep4DialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_vandl_step4);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initView();
        initEmvResources();
    }

    @Override
    protected void onStart() {
        super.onStart();

        attachService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        detachService();
    }


    void initView(){
        tvStep = (TextView)findViewById(R.id.tv_vandl_step);
        tvStep.setText(mContext.getString(R.string.vandl4_dialog_step));

        Button btnCancel = (Button) findViewById(R.id.btn_dlg_cancel);
        Button btnAccept = (Button) findViewById(R.id.btn_dlg_accept);
        btnCancel.setOnClickListener(this);
        btnAccept.setOnClickListener(this);
        btnCancel.setText(R.string.vandl_dialog_btn_back);
        btnAccept.setText(R.string.vandl_dialog_btn_finish);
        loadData();

    }
    void loadData(){
        TextView tvVanName = (TextView)findViewById(R.id.tvVanName);
        TextView tvCompanyNo = (TextView)findViewById(R.id.tvCompanyNo);
        TextView tvCompanyName = (TextView)findViewById(R.id.tvCompanyName);
        TextView tvAddress = (TextView)findViewById(R.id.tvAddress);
        TextView tvPhoneNo = (TextView)findViewById(R.id.tvPhoneNo);
        TextView tvRetailTel = (TextView)findViewById(R.id.tvRetailTel);
        TextView tvVanTel = (TextView)findViewById(R.id.tvVanTel);

        tvAddress.setText(comEntity.getF_CompanyAddress().trim());
        tvCompanyName.setText(comEntity.getF_CompanyName().trim());
        tvCompanyNo.setText(Helper.formatCompanyNo(comEntity.getF_CompanyNo()));
        tvVanName.setText(StaticData.vanName);
        tvPhoneNo.setText(comEntity.getF_CompanyPhoneNo());
        tvVanTel.setText(comEntity.getF_VanPhoneNo());
        tvRetailTel.setText(comEntity.getF_ResellerPhoneNo());

    }

    @Override
    public void onClick(View v) {
        BHelper.db("click");
        switch (v.getId()){
            case R.id.btn_dlg_cancel:
                onVanDLStep4DialogListener.VanDLStep4DialogEvent(false,comEntity.getF_CompanyNo());
                dismiss();
                break;
            case R.id.btn_dlg_accept:
                BHelper.db("accept");
                saveCompany();
                break;
        }
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
    private CompanyEntity addCompany(CompanyEntity comE) {
        String sServiceTax = "0";
        String sTax = "10";
        comE.setF_UserID(AppHelper.getCurrentUserID());
        comE.setF_ServiceTaxRate(sServiceTax.equals("") ? "0" : sServiceTax);
        comE.setF_TaxRate(sTax.equals("") ? "0" : sTax);
        comE.setCREATE_UID(AppHelper.getCurrentUserID());
        comE.setUPDATE_UID(AppHelper.getUpdateUserID());
        comE.setF_PhoneNo(Helper.GetMyPhone((Activity)mContext));

        boolean isWithTax = true;
        if (isWithTax)
            comE.setF_WithTax(true);
        else
            comE.setF_WithTax(false);
        comE.setF_VanName(StaticData.vanName);
        return comE;
    }

    protected void keyBinding() {
        if(!isDeviceReady){
            BHelper.db("Device is not ready");
            BHelper.showToast(R.string.device_not_ready);
            startDevice();
//            isWaitForKeyBinding = true;
            return;
        }
        showDialog(R.string.msg_keybinding_during);
        EmvReader.isManualKeyBinding = true;
        certificateRequest();
    }
    void certificateRequest(){
        terInfo = TerminalInfo.parseFromCompany(comEntity);

        showDialog(R.string.msg_keybinding_during);

        new MyTaskDLVan((Activity)mContext){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                updateDialogMsg(mContext.getString(R.string.msg_keybinding_during)+" 20%");
            }

            @Override
            public String[] run() {
                daouData = new SecurityCertificate();
                return daouData.req(terInfo);
            }

            @Override
            public boolean res(String[] result) {
                BHelper.db("keyExchange");
                String base64 = result[4];
                String data = DaouDataHelper.getData2KeyExchange(base64);
                emvReader.keyExchnage(data);
                updateDialogMsg(mContext.getString(R.string.msg_keybinding_during)+" 40%");
                return false;
            }
        };


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
    void initEmvResources(){
        emvReader = MainActivity.getEmvReader();
//        isDeviceReady = Helper.isHeadsetConnected(mContext) || MainActivity.isBTReaderConnected;
        isDeviceReady = Helper.isDeviceReady(mContext);
    }

    void attachService(){

        if(emvReader!=null){
            emvReader.attachIntegrityCheckListener(VanDLStep4Dialog.this);
            emvReader.attachBlueToothListener(VanDLStep4Dialog.this);
        }
    }
    void detachService(){
        if(emvReader!=null){
            emvReader.detachIntegrityCheckListener(VanDLStep4Dialog.this);
            emvReader.detachBluetoothListener(VanDLStep4Dialog.this);
        }
    }

    private void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
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
                if(deviceName.contains(mContext.getString(R.string.bt_device_prefix))){
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
        onVanDLStep4DialogListener.VanDLStep4DialogEvent(false,"");
        dismiss();
    }

    @Override
    public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
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
            updateDialogMsg(mContext.getString(R.string.msg_keybinding_during) + " 20%");

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        bindingEntity = null;
        firmwareVersion = pinKsn = trackKsn = emvKsn =uid = csn = deviceSerial = modelName = currentSerial = "";

    }

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

        updateDialogMsg(mContext.getString(R.string.msg_keybinding_during) + " 40%");
        if(exchangeResultEntity.getEncRandValue()==null){
            dismissPDialog();
            BHelper.showToast(R.string.msg_keybiding_error);
            return;
        }
        securityKeyDownload(base64Random,dialog);

//        isUpdateKsn = true;
//
//        BHelper.db("Update Ksn from device to injectMaterKey");
////		emvReader.getKsn();
////		BHelper.db("save KsnEntity to Reader to use to injectMasterKey");
//        isUpdateKsn = false;
    }

    @Override
    public void injectMasterKeyCallback(String resultMsg) {

        if(resultMsg==null || resultMsg.equals("")){
            updateDialogMsg(mContext.getString(R.string.msg_keybinding_during) + " 100%");
            BHelper.showToast(R.string.msg_key_binding_success);
            AppHelper.setKeyBindingYear();
        }
        // TODO Auto-generated method stub
        BHelper.db("injectMasterKeyCallback:"+ resultMsg);
        closeDialog();

        BHelper.showToast(R.string.msg_keybiding_success);
        onVanDLStep4DialogListener.VanDLStep4DialogEvent(true, comEntity.getF_CompanyNo());
        dismiss();
    }

    @Override
    public void onReturnIntegrityCheckResult(boolean result) {
        // TODO Auto-generated method stub
        BHelper.db("MANUAL: onReturnIntegrityCheckResult:"+result);
        Log.e("Jonathan", "Jonathan3");
        String logData = "INTERGRITY CHECK";
        logData+="\nResult:"+result;
        Helper.writeIntegrityLog(logData);
        logData = "";
        if(result == true) {
            BHelper.showToast(R.string.emv_integrity_check_success);
        }else{
            showFallbackDlg(mContext.getString(R.string.msgIntegrityCheckFail));
        }
        emvReader.getDeviceInfo();
    }

    @Override
    public void onDeviceHere(boolean isHere) {

    }

    @Override
    public void onNoDeviceDetected() {

    }

    @Override
    public void onDeviceUnplugged() {
        BHelper.restoreVolumn(mContext);
        isDeviceReady = false;
        closeDialog();
        showFallbackDlg(mContext.getString(R.string.device_unplugged));
        onVanDLStep4DialogListener.VanDLStep4DialogEvent(false,"");
        dismiss();
    }

    @Override
    public void onDevicePlugged() {
        // TODO Auto-generated method stub
        isDeviceReady =true;
        showToast(mContext.getString(R.string.device_plugged));
    }

    @Override
    public void onError(BBDeviceController.Error errorState) {

    }

    @Override
    public void onAutoConfigCompleted(boolean isDefaultSettings,
                                      String autoConfigSettings) {
        // TODO Auto-generated method stub
//        emvReader.setAutoConfig();
        closeDialog();
        BHelper.db("auto config is completed");
        showToast(mContext.getString(R.string.msg_config_device_success));
    }


    @Override
    public void onAutoConfigError(BBDeviceController.AudioAutoConfigError autoConfigError) {
        // TODO Auto-generated method stub
        closeDialog();
        BHelper.db("auto config is error");
        showToast(mContext.getString(R.string.msg_config_device_failed));
    }

    @Override
    public void onAutoConfigProgressUpdate(double percentage) {
        // TODO Auto-generated method stub
        BHelper.db("config percent:"+ (int)percentage);
        if(dialog!=null && dialog.isShowing()){
            updateDialogMsg(mContext.getString(R.string.msg_config_device_doing)+ " " +(int)percentage+ " %");
        }
    }

    public class TaskSaveCompany extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(R.string.msg_processing);
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
                    Toast.makeText(mContext, mContext.getString(R.string.msg_insert_company_success), Toast.LENGTH_LONG).show();
                    MainActivity main = (MainActivity) mContext;
                    main.initMenuLeft();


                    //check for caller
                    String isCalled = "";
                    String reqParaJson="";
//                    if(getArguments()!=null){
//                        isCalled = getArguments().getString("isCalled","");
//                        reqParaJson =getArguments().getString("reqParaJson","");
//                    }
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
//                        doCaller4(companyNo,machineNo,transType,payType,reqParaJson);
                    }

                    keyBinding();

                } else {
                    dialog.dismiss();
                    Toast.makeText(mContext, mContext.getString(R.string.msg_insert_company_failed), Toast.LENGTH_LONG).show();
                }
            } else{
                dialog.dismiss();
                Toast.makeText(mContext,mContext.getString(R.string.msg_insert_company_to_server_failed), Toast.LENGTH_LONG).show();
            }


        }
    }
    protected void showDialog(int rsid) {
//		showStatus("");
        if(dialog==null || !dialog.isShowing()){
//			dialog = new MyProgressDialog(at);
            dialog = BHelper.DialogHelper.makeDialog(rsid);
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
            //dialog.setMessage(at.getString(rsid));
        }
    }
    protected void showDialogProgress(int rsid) {
        if (dialog == null || !dialog.isShowing()) {
            dialog = BHelper.DialogHelper.makeDialog(rsid);
            dialog.setTitle(null);
            dialog.setMessage(mContext.getString(rsid));
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMax(100);
            dialog.setIndeterminate(false);
            dialog.show();
        }
    }
//    void showDialog() {
//        dialog = BHelper.DialogHelper.makeDialog(R.string.log_conten);
//        dialog.setTitle(mContext.getString(R.string.log_title));
//        dialog.setMessage(mContext.getString(R.string.log_conten));
//        dialog.setIndeterminate(true);
//        dialog.setCancelable(false);
//        dialog.show();
//    }
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
    public void dismissPDialog() {
        if(pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    protected void showFallbackDlg(String msg){
        new AlertDialog.Builder(mContext).setTitle(R.string.emv_fallback_report).setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(msg)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();
    }
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

    void securityKeyDownload(final String deviceRandomKey, final ProgressDialog progressDialog) {
        new MyTaskDLVan((Activity) mContext){


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                updateDialogMsg(mContext.getString(R.string.msg_keybinding_during)+" 60%");
            }

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
                updateDialogMsg(mContext.getString(R.string.msg_keybinding_during)+" 80%");
                return false;
            }
        };
    }
}
