package com.devcrane.payfun.daou.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bbpos.bbdevice.BBDeviceController;
import com.devcrane.android.lib.emvreader.BlueToothListener;
import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.android.lib.emvreader.IntegrityCheckListener;
import com.devcrane.android.lib.entity.KeyExchangeResultEntity;
import com.devcrane.payfun.daou.MainActivity;
import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.MyTaskStr;
import com.devcrane.payfun.daou.van.DaouData;
import com.devcrane.payfun.daou.van.DaouDataContants;
import com.devcrane.payfun.daou.van.DaouDataHelper;
import com.devcrane.payfun.daou.van.OpenTerminal;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 7/19/17.
 */

public class VanDLStep3Dialog extends Dialog implements View.OnClickListener , IntegrityCheckListener, BlueToothListener {
    final String TAG = getClass().getSimpleName();
    private Context mContext;
    VanDLStep3DialogListener onVanDLStep3DialogListener;
    CompanyEntity comEntity;
    String companyNo="";
    String machineCode="";
    private TextView tvStep;
    EmvReader emvReader;
    Button btnCancel;
    Button btnAccept;

    EditText edMachineCode;

    ProgressDialog dialog;

    RadioGroup rdgMachineDivision;
    RadioButton rdbGeneral;
    RadioButton rdbMultiple;
    String machineDivision = DaouDataContants.VAL_TERMINAL_DIVISION_GENERAL;

    public VanDLStep3Dialog(Context context, String companyNo, VanDLStep3DialogListener vanDLStep3DialogListener) {
        super(context);
        mContext = context;
        this.companyNo = companyNo;
        onVanDLStep3DialogListener = vanDLStep3DialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_vandl_step3);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initEmvResources();
        initView();
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

    void initEmvResources(){
        emvReader = MainActivity.getEmvReader();
    }

    void attachService(){

        if(emvReader!=null){
            emvReader.attachIntegrityCheckListener(VanDLStep3Dialog.this);
            emvReader.attachBlueToothListener(VanDLStep3Dialog.this);
        }
    }
    void detachService(){
        if(emvReader!=null){
            emvReader.detachIntegrityCheckListener(VanDLStep3Dialog.this);
            emvReader.detachBluetoothListener(VanDLStep3Dialog.this);
        }
    }

    void initView() {
        tvStep = (TextView)findViewById(R.id.tv_vandl_step);
        tvStep.setText(mContext.getString(R.string.vandl3_dialog_step));

        edMachineCode = (EditText) findViewById(R.id.txtResMachineCode);

        if(StaticData.IS_TEST){
            edMachineCode.setText(StaticData.JTNET_TEST_MACHINE_CODE);
        }
        edMachineCode.addTextChangedListener(mTextWatcher);
        btnCancel = (Button) findViewById(R.id.btn_dlg_cancel);
        btnAccept = (Button) findViewById(R.id.btn_dlg_accept);
        btnCancel.setOnClickListener(this);
        btnAccept.setOnClickListener(this);

        if(edMachineCode.getText().length()!=8){
            tongleButton(false);
        }else
            tongleButton(true);


        itnitMachineDivision();
    }

    void itnitMachineDivision(){
        rdgMachineDivision = (RadioGroup) findViewById(R.id.rdgMachineDivision);
        rdbGeneral = (RadioButton) rdgMachineDivision.findViewById(R.id.rdbDivisionGeneral);
        rdbMultiple = (RadioButton) rdgMachineDivision.findViewById(R.id.rdbDivisionMultiple);

        rdgMachineDivision.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String divisionType = ((RadioButton)radioGroup.findViewById(i)).getText().toString();
                String divisionTypeGeneral =mContext.getString(R.string.machine_division_general);
                if(divisionType.equals(divisionTypeGeneral))
                    machineDivision = DaouDataContants.VAL_TERMINAL_DIVISION_GENERAL;
                else
                    machineDivision = DaouDataContants.VAL_TERMINAL_DIVISION_MULTI_VENDOR;

                BHelper.db("machineDivision:"+ machineDivision);
            }
        });
    }

    void tongleButton(boolean isActive){

        if(isActive){
            btnAccept.setOnClickListener(this);
            btnAccept.setBackgroundResource(R.drawable.btn_dialog_shape_s);
            btnAccept.setTextColor(mContext.getResources().getColor(R.color.dl_dialog_active));

        }else{
            btnAccept.setOnClickListener(null);
            btnAccept.setBackgroundResource(R.drawable.btn_dialog_back_shape_s);
            btnAccept.setTextColor(mContext.getResources().getColor(R.color.dl_dialog_inactive));
        }
    }


    @Override
    public void onClick(View v) {
        BHelper.db("click");
        switch (v.getId()){
            case R.id.btn_dlg_cancel:
                onVanDLStep3DialogListener.VanDLStep3DialogEvent(false);
                dismiss();
                break;
            case R.id.btn_dlg_accept:
                BHelper.db("accept");
                machineCode = BHelper.requireTxt(edMachineCode, R.string.vandl3_dialog_content);

                if(machineCode.length()!=8){
                    BHelper.showToast(R.string.msg_vandl_dialog_machine_code_incorrect_length);
                    break;
                }

                getDeviceInfo();
//                downloadDaou(companyNo,machineCode);
                break;
        }
    }
    void getDeviceInfo(){
        showDialog(R.string.msg_processing);
        emvReader.getDeviceInfo();
    }

    void downloadDaou(String comNo, String maCode){
        final DaouData daouData = new OpenTerminal();
        final TerminalInfo terminalInfo = new TerminalInfo();
        terminalInfo.setTerCompanyNo(comNo);
        terminalInfo.setTerNumber(maCode);
        terminalInfo.setMachineDivision(machineDivision);
        new MyTaskStr((Activity)mContext){

            @Override
            public String[] run() {

                return daouData.req(terminalInfo);
            }

            @Override
            public boolean res(String[] result) {
                if(result[1].equals("0000")){
                    comEntity = DaouDataHelper.parseToCompany(result);
                    comEntity.setF_PhoneCode(machineDivision);
                    comEntity.setF_CompanyNo(terminalInfo.getTerCompanyNo());
                    comEntity.setF_MachineCode(terminalInfo.getTerNumber());
                    //Jonathan 171205 추가
                    AppHelper.setTID(terminalInfo.getTerNumber());


                    String vanInfo = result[9];
                    String vanIP = vanInfo.substring(24,39).trim();
                    String vanPort = vanInfo.substring(39,45).trim();
                    BHelper.db("vanIP:"+vanIP + ", vanPort:"+vanPort);
                    AppHelper.setVanIp(vanIP);
                    AppHelper.setVanPort(vanPort);
                    onVanDLStep3DialogListener.VanDLStep3DialogEvent(companyNo,machineCode,comEntity);
                    dismiss();
                }else {
                    String msg =result[21];
                    BHelper.showToast(msg);
                }
                return false;
            }
        };
    }


    @Override
    public void onBTReturnScanResults(List<BluetoothDevice> list) {

    }

    @Override
    public void onBTScanTimeout() {

    }

    @Override
    public void onBTScanStopped() {

    }

    @Override
    public void onBTConnected(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void onBTDisconnected() {
        onVanDLStep3DialogListener.VanDLStep3DialogEvent(false);
        dismiss();
    }

    @Override
    public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {

        BHelper.db("onReturnDeviceInfo on " + TAG);
        Set<String> keys = deviceInfoData.keySet();
        for(String key: keys){
            BHelper.db(key+":"+deviceInfoData.get(key));
        }

        closeDialog();
        downloadDaou(companyNo,machineCode);

    }

    @Override
    public void keyExchangeCallback(KeyExchangeResultEntity exchangeResultEntity) {

    }

    @Override
    public void injectMasterKeyCallback(String resultMsg) {

    }

    @Override
    public void onDeviceHere(boolean isHere) {

    }

    @Override
    public void onNoDeviceDetected() {

    }

    @Override
    public void onDeviceUnplugged() {
        onVanDLStep3DialogListener.VanDLStep3DialogEvent(false);
        dismiss();
    }

    @Override
    public void onDevicePlugged() {

    }

    @Override
    public void onReturnIntegrityCheckResult(boolean result) {

    }

    @Override
    public void onError(BBDeviceController.Error errorState) {
        closeDialog();

    }

    @Override
    public void onAutoConfigCompleted(boolean isDefaultSettings, String autoConfigSettings) {

    }

    @Override
    public void onAutoConfigError(BBDeviceController.AudioAutoConfigError autoConfigError) {

    }

    @Override
    public void onAutoConfigProgressUpdate(double percentage) {

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
//            if (!str.equals("")) {
//                str = (str.contains("-") ? str.replace("-", "") : str);
//                if (str.length() > 3 && str.length() < 6) {
//                    str = str.substring(0, 3) + "-" + str.substring(3);
//                } else if (str.length() >= 6) {
//                    str = str.substring(0, 3) + "-" + str.substring(3, 5) + "-" + str.substring(5);
//                }
//            }
            edMachineCode.removeTextChangedListener(this);
            edMachineCode.setText(str);
            edMachineCode.setSelection(str.length());
            edMachineCode.addTextChangedListener(this);
            if(str.length()==8){
                tongleButton(true);
            }else{
                tongleButton(false);
            }

        }
    };

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
}
