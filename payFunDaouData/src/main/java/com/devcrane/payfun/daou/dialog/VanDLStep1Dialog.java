package com.devcrane.payfun.daou.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;
import com.bbpos.bbdevice.BBDeviceController;
import com.devcrane.android.lib.emvreader.BlueToothListener;
import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.android.lib.emvreader.IntegrityCheckListener;
import com.devcrane.android.lib.entity.KeyExchangeResultEntity;
import com.devcrane.payfun.cardreader.EmvUtils;
import com.devcrane.payfun.daou.MainActivity;
import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.entity.BTReaderInfo;
import com.devcrane.payfun.daou.entity.KeyBindingEntity;
import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.van.DaouData;
import com.devcrane.payfun.daou.van.DaouDataContants;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.devcrane.payfun.daou.MainActivity.WAIT_FOR_BT_TURN_ON;

/**
 * Created by admin on 7/19/17.
 */

public class VanDLStep1Dialog extends Dialog implements View.OnClickListener, IntegrityCheckListener, BlueToothListener {
    final String TAG = getClass().getSimpleName();
    private Context mContext;
    VanDLStep1DialogListener onVanDLStep1DialogListener;
    EmvReader emvReader;
    private TextView tvStep,tvMessage;
    private LinearLayout llBorderMini2, llBottomMini2, llBorderMinibt, llBottomMinibt;
    private ImageView ivCheckMini2, ivCheckMinibt, ivMini2,ivMiniBT;
    boolean isBT = false;
    ProgressDialog dialog;
    Dialog pDialog;
    boolean isDeviceReady =false;
    boolean isWaitForKeyBinding =false;
    Button btnCancel;
    Button btnAccept;
    Button btnAutoCofig;


    public VanDLStep1Dialog(Context context, VanDLStep1DialogListener vanDLStep1DialogListener) {
        super(context);
        mContext = context;
        onVanDLStep1DialogListener = vanDLStep1DialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_vandl_step1_inactive);
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
//        isDeviceReady = (!isBT && Helper.isHeadsetConnected(mContext)) || (isBT && MainActivity.isBTReaderConnected);
        isDeviceReady = Helper.isDeviceReady(mContext);
    }

    void attachService(){

        if(emvReader!=null){
            emvReader.attachIntegrityCheckListener(VanDLStep1Dialog.this);
            emvReader.attachBlueToothListener(VanDLStep1Dialog.this);
        }
        try {
            if(Helper.isHeadsetConnected(mContext)){

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showDialog();
                        emvReader.isDeviceHere();
                    }
                }, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void detachService(){
        if(emvReader!=null){
            emvReader.detachIntegrityCheckListener(VanDLStep1Dialog.this);
            emvReader.detachBluetoothListener(VanDLStep1Dialog.this);
        }
    }

    void initView(){

        tvMessage = (TextView)findViewById(R.id.tvMessage);

        tvStep = (TextView)findViewById(R.id.tv_vandl_step);
        tvStep.setText(mContext.getString(R.string.vandl1_dialog_step));

        ivMini2 = (ImageView)findViewById(R.id.iv_vandl_mini2);
        ivMiniBT = (ImageView)findViewById(R.id.iv_vandl_minibt);
        llBorderMini2 = (LinearLayout)findViewById(R.id.ll_vandl_mini2_border);
        llBottomMini2 = (LinearLayout)findViewById(R.id.ll_vandl_mini2_bottom);
        ivCheckMini2 = (ImageView)findViewById(R.id.iv_vandl_mini2_check);

        llBorderMinibt = (LinearLayout)findViewById(R.id.ll_vandl_minibt_border);
        llBottomMinibt = (LinearLayout)findViewById(R.id.ll_vandl_minibt_bottom);
        ivCheckMinibt = (ImageView)findViewById(R.id.iv_vandl_minibt_check);

        btnCancel = (Button)findViewById(R.id.btn_dlg_cancel);
        btnAccept = (Button)findViewById(R.id.btn_dlg_accept);
        btnAutoCofig = (Button)findViewById(R.id.btn_dlg_autoconfig);
        btnCancel.setOnClickListener(this);
        btnAccept.setOnClickListener(this);
        btnAutoCofig.setOnClickListener(this);

        tongleSelectDeviceListen(true);
        initDeviceSetting();
        checkButton();
        checkAutoConfigButton();

    }
    void tongleSelectDeviceListen(boolean isActive){
        if(isActive){
            ivMini2.setOnClickListener(this);
            ivMiniBT.setOnClickListener(this);
            llBorderMini2.setOnClickListener(this);
            llBorderMinibt.setOnClickListener(this);
        }else{
            ivMini2.setOnClickListener(null);
            ivMiniBT.setOnClickListener(null);
            llBorderMini2.setOnClickListener(null);
            llBorderMinibt.setOnClickListener(null);
        }
    }

    void inactive(){
        llBorderMini2.setBackgroundResource(R.drawable.border_step1_mini2_inactive);
        llBottomMini2.setBackgroundResource(R.drawable.border_btn_step1_mini2_inactive);
        ivCheckMini2.setImageResource(R.drawable.vandl_dialog_un_check);

        llBorderMinibt.setBackgroundResource(R.drawable.border_step1_minibt_inactive);
        llBottomMinibt.setBackgroundResource(R.drawable.border_btn_step1_minibt_inactive);
        ivCheckMinibt.setImageResource(R.drawable.vandl_dialog_un_check);
    }

    void activeMini2(){
        inactive();
        llBorderMini2.setBackgroundResource(R.drawable.border_step1_mini2_active);
        llBottomMini2.setBackgroundResource(R.drawable.border_btn_step1_mini2_active);
        ivCheckMini2.setImageResource(R.drawable.vandl_dialog_check_mini2);
        btnAutoCofig.setVisibility(View.VISIBLE);
    }

    void activeMinibt(){
        inactive();
        llBorderMinibt.setBackgroundResource(R.drawable.border_step1_minibt_active);
        llBottomMinibt.setBackgroundResource(R.drawable.border_btn_step1_minibt_active);
        ivCheckMinibt.setImageResource(R.drawable.vandl_dialog_check_minibt);
        btnAutoCofig.setVisibility(View.GONE);
    }
    void goBack(){
        onVanDLStep1DialogListener.VanDLStep1DialogEvent(false);
        dismiss();
    }
    @Override
    public void onClick(View v) {
        BHelper.db("click");
        switch (v.getId()){
            case R.id.btn_dlg_cancel:
                goBack();
                break;
            case R.id.btn_dlg_accept:
                BHelper.db("accept");
//                isDeviceReady = (!isBT && Helper.isHeadsetConnected(mContext)) || (isBT && MainActivity.isBTReaderConnected);
                isDeviceReady = Helper.isDeviceReady(mContext);
                if(!isDeviceReady){
                    BHelper.db("Device is not ready");
                    BHelper.showToast(R.string.device_not_ready);
                    startDevice();
                    break;
                }
                onVanDLStep1DialogListener.VanDLStep1DialogEvent(true);
                dismiss();
                break;
            case R.id.iv_vandl_mini2:
            case R.id.ll_vandl_mini2_border:
                tongleDevice(false);
                break;
            case R.id.iv_vandl_minibt:
            case R.id.ll_vandl_minibt_border:
                tongleDevice(true);
                break;
            case R.id.btn_dlg_autoconfig:
                showDialogProgress(R.string.msg_config_device_doing);
                emvReader.startAutoConfig();
                break;
        }
    }
    void initDeviceSetting(){
        if(AppHelper.getReaderType()== EmvReader.READER_TYPE_BT){
            isBT =true;
            activeMinibt();
            ((MainActivity)mContext).resetTotalWaitTime();
        }else{
            isBT = false;
            activeMini2();
        }

    }
    void tongleDevice(boolean isMiniBT){
        isBT = isMiniBT;
        emvReader.stopConnection();
        emvReader.emvSwipeController.disconnectBT();

        Log.e("Jonathan", "connection 1");
//        if(emvReader.emvSwipeController!=null) {
//
//
//            switch (emvReader.emvSwipeController.getConnectionMode()) {
//                case NONE:
//                    BHelper.db("mode is NONE so can connect BT device");
//                    break;
//                case BLUETOOTH:
//                    BHelper.db("connection mode is BLUETOOTH");
//                    if(!isBT)
//                    emvReader.emvSwipeController.disconnectBT();
//                    break;
//                case AUDIO:
//                    BHelper.db("connection mode is AUDIO");
//                    break;
//            }
//        }


        if(isMiniBT){

            activeMinibt();
            AppHelper.setReaderType(String.valueOf(EmvReader.READER_TYPE_BT));
            Log.e("Jonathan", "connection 2");

            //Jonathan 171125 추가
            final ArrayList<String> savedBtAddress = AppHelper.getStringArrayPref(mContext,"BT_ADDRS");
            final ArrayList<String> savedBtNames = AppHelper.getStringArrayPref(mContext,"BT_NAMES");
            Log.e("Jonathan", " aaa :: " + savedBtAddress.size());
            Log.e("Jonathan", " aaa1 :: " + savedBtAddress.toString());
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(mContext);
//            builderSingle.setIcon(R.drawable.ic_launcher);
            builderSingle.setTitle("어떤 기기와 연결하시겠습니까?");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_singlechoice);
            for(int i = 0 ; i < savedBtNames.size() ; i++)
            {
                arrayAdapter.add(savedBtNames.get(i).toString());
            }
            arrayAdapter.add("NEW 새로운 기기 연결");

            builderSingle.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(arrayAdapter.getItem(which).contains("NEW"))
                    {
                        promptForConnection();
                    }
                    else
                    {
                        //Jonathan 171124 수정
                        String setSelectedAddr = savedBtAddress.get(savedBtNames.indexOf(arrayAdapter.getItem(which)));
                        String setSelectedName = arrayAdapter.getItem(which);
                        BTReaderInfo btReaderInfo = new BTReaderInfo(setSelectedName,setSelectedAddr);
                        AppHelper.setBTReaderInfo(btReaderInfo);
                        showBTConfig();
                    }

                }
            });
            builderSingle.show();




//            showBTConfig();

        }else{
            activeMini2();
            AppHelper.setReaderType(String.valueOf(EmvReader.READER_TYPE_EARJACK));
        }
        isDeviceReady = false;
        EmvReader.setIsBlueTooth(isBT);
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

    void checkAutoConfigButton(){
        boolean isReady = Helper.isHeadsetConnected(mContext);
        boolean isBT = false;
        if(AppHelper.getReaderType()== EmvReader.READER_TYPE_BT)
            isBT = true;
        if(btnAutoCofig==null)
            return;
        if(!isBT){
            if(isReady){
                btnAutoCofig.setOnClickListener(this);
                btnAutoCofig.setBackgroundResource(R.drawable.btn_dialog_shape_s);
                btnAutoCofig.setTextColor(mContext.getResources().getColor(R.color.dl_dialog_active));
            }else{
                btnAutoCofig.setOnClickListener(null);
                btnAutoCofig.setBackgroundResource(R.drawable.btn_dialog_back_shape_s);
                btnAutoCofig.setTextColor(mContext.getResources().getColor(R.color.dl_dialog_inactive));
            }
        }else{
            btnAutoCofig.setOnClickListener(null);
            btnAutoCofig.setBackgroundResource(R.drawable.btn_dialog_back_shape_s);
            btnAutoCofig.setTextColor(mContext.getResources().getColor(R.color.dl_dialog_inactive));
        }
    }
    void checkButton(){
//        isDeviceReady = Helper.isHeadsetConnected(mContext) || MainActivity.isBTReaderConnected;
        isDeviceReady = Helper.isDeviceReady(mContext);
        if(isDeviceReady && !Helper.isHeadsetConnected(mContext)){
            tongleSelectDeviceListen(false);
            btnAccept.setOnClickListener(this);
            btnAccept.setBackgroundResource(R.drawable.btn_dialog_shape_s);
            btnAccept.setTextColor(mContext.getResources().getColor(R.color.dl_dialog_active));
            tvMessage.setText(R.string.msg_vandl_dialog_connected);
        }else{
            btnAccept.setOnClickListener(null);
            btnAccept.setBackgroundResource(R.drawable.btn_dialog_back_shape_s);
            btnAccept.setTextColor(mContext.getResources().getColor(R.color.dl_dialog_inactive));
            tvMessage.setText(R.string.msg_vandl_dialog_ask_connect);



        }
    }

    void checkButton(boolean noAudioDevice){

        if(!noAudioDevice){
            tongleSelectDeviceListen(false);
            btnAccept.setOnClickListener(this);
            btnAccept.setBackgroundResource(R.drawable.btn_dialog_shape_s);
            btnAccept.setTextColor(mContext.getResources().getColor(R.color.dl_dialog_active));
            tvMessage.setText(R.string.msg_vandl_dialog_connected);
        }else{
            btnAccept.setOnClickListener(null);
            btnAccept.setBackgroundResource(R.drawable.btn_dialog_back_shape_s);
            btnAccept.setTextColor(mContext.getResources().getColor(R.color.dl_dialog_inactive));
            tvMessage.setText(R.string.msg_vandl_dialog_ask_connect);



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
            BHelper.showToast(R.string.bluetooth_not_configured);
        }
    }

    void showBTConfig(){
        BTReaderInfo btReaderInfo = AppHelper.getBTReaderInfo();
        if (btReaderInfo==null || btReaderInfo.getName().equals("")){
            Log.e("Jonathan", "connection 3");

            if(AppHelper.getReaderType()== EmvReader.READER_TYPE_BT) {
                if (emvReader != null)
                    emvReader.stopConnection();
                try {
                    Thread.sleep(1000);
                    promptForConnection();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }else{
            Log.e("Jonathan", "connection 4");


            ((MainActivity)mContext).resetTotalWaitTime();
            ((MainActivity)mContext).waitTurnBT();
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
                    BHelper.db("added device: "+ list.get(i).getName());
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
    }

    @Override
    public void onBTDisconnected() {
        BHelper.db("BT disconnected in Register Van");
        isDeviceReady = false;
        tvMessage.setText(R.string.msg_vandl_dialog_ask_connect);
        closeDialog();
        onVanDLStep1DialogListener.VanDLStep1DialogEvent(false);

        //Jonathan 171125 추가
        ivMini2.setOnClickListener(this);
        ivMiniBT.setOnClickListener(this);
        llBorderMini2.setOnClickListener(this);
        llBorderMinibt.setOnClickListener(this);

        btnAccept.setOnClickListener(null);
        btnAccept.setBackgroundResource(R.drawable.btn_dialog_back_shape_s);
        btnAccept.setTextColor(mContext.getResources().getColor(R.color.dl_dialog_inactive));

    }


    @Override
    public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
        closeDialog();
        BHelper.db("onReturnDeviceInfo on "+ TAG);
        String firmwareVersion = deviceInfoData.get("firmwareVersion") == null? "" : deviceInfoData.get("firmwareVersion");
        String pinKsn = deviceInfoData.get("pinKsn") == null? "" : deviceInfoData.get("pinKsn");
        String serialNumber = deviceInfoData.get("serialNumber")==null? DaouDataContants.VAL_PRODUCTION_SERIAL_NUMBER:deviceInfoData.get("serialNumber");
        BHelper.db("set JTNet.SoftVer:"+firmwareVersion);

        Set<String> keys = deviceInfoData.keySet();
        for(String key: keys){
            BHelper.db(key+":"+deviceInfoData.get(key));
        }
        String modelName = deviceInfoData.get("modelName") == null? "" : deviceInfoData.get("modelName");
        EmvUtils.saveHWModelName(modelName);
        String publicKeyVersion = deviceInfoData.get("publicKeyVersion") == null? "" : deviceInfoData.get("publicKeyVersion");
        EmvUtils.savePublicKeyVersion(publicKeyVersion);
        EmvUtils.saveHwSerialNumber(serialNumber);
        EmvUtils.saveEmvSerial(pinKsn);
        EmvUtils.saveHwSerialNumber(serialNumber);

        firmwareVersion = pinKsn =  modelName  = "";

        if(EmvUtils.isValidHWModelName()){
            isDeviceReady =true;
            tvMessage.setText(R.string.msg_vandl_dialog_connected);
        }else{
            tvMessage.setText(R.string.msg_vandl_dialog_wrong_model);
        }

        checkButton(false);

    }

    @Override
    public void keyExchangeCallback(KeyExchangeResultEntity exchangeResultEntity) {

    }

    @Override
    public void injectMasterKeyCallback(String resultMsg) {

    }

    @Override
    public void onDeviceHere(boolean isHere) {
//        if(isHere){
//            BHelper.db("onDeviceHere on VanDlStep1:"+ isHere);
//            emvReader.getDeviceInfo();
//        }

    }

    @Override
    public void onNoDeviceDetected() {
        BHelper.db("onNoDeviceDetected on "+ TAG);
        checkButton(true);
        closeDialog();
    }

    @Override
    public void onDeviceUnplugged() {
        isDeviceReady = false;
        tvMessage.setText(R.string.msg_vandl_dialog_ask_connect);
        closeDialog();
        showFallbackDlg(mContext.getString(R.string.device_unplugged));
        goBack();
    }

    @Override
    public void onDevicePlugged() {
        // TODO Auto-generated method stub
        isDeviceReady =true;
        checkAutoConfigButton();
        showToast(mContext.getString(R.string.device_plugged));
    }
    @Override
    public void onReturnIntegrityCheckResult(boolean result) {

    }

    @Override
    public void onError(BBDeviceController.Error errorState) {
        closeDialog();

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
    void showDialog() {
        if (dialog == null || !dialog.isShowing()) {
            dialog = BHelper.DialogHelper.makeDialog(R.string.msg_processing);
            dialog.setCancelable(false);
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

    void showAskTouchAgainMsg(){
        tvMessage.setText(R.string.msg_vandl_dialog_ask_touch_to_connect_again);
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
    public void promptForConnection() {
        dismissDialog();
        wiseDialog = new Dialog(mContext);
        wiseDialog.setContentView(R.layout.connection_dialog);
        wiseDialog.setTitle(mContext.getString(R.string.connection));

        String[] connections = new String[1];
        connections[0] = "Bluetooth";
        Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
        final BluetoothDevice[] pairedDevices = new BluetoothDevice[pairedObjects.length];
        for (int i = 0; i < pairedObjects.length; ++i) {
            pairedDevices[i] = (BluetoothDevice) pairedObjects[i];
        }

        final ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1);
        for (int i = 0; i < pairedDevices.length; ++i) {
            String deviceName = pairedDevices[i].getName();
            if(deviceName.contains(mContext.getString(R.string.bt_device_prefix)))
                mArrayAdapter.add(deviceName);
        }

        dismissDialog();
        wiseDialog = new Dialog(mContext);
        wiseDialog.setContentView(R.layout.bluetooth_2_device_list_dialog);
        wiseDialog.setTitle(R.string.bluetooth_devices);

        ListView listView1 = (ListView) wiseDialog.findViewById(R.id.pairedDeviceList);
        listView1.setAdapter(mArrayAdapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BHelper.db("connecting..."+pairedDevices[position].getName());
                showAskTouchAgainMsg();
                BTReaderInfo btReaderInfo = new BTReaderInfo(pairedDevices[position].getName(),pairedDevices[position].getAddress());
//							emvReader.connectBT(pairedDevices[position]);

                //Jonathan 수정
                AppHelper.setBTReaderInfo(btReaderInfo);
                String btAddress = pairedDevices[position].getAddress();
                String btNames = pairedDevices[position].getName();
                ArrayList<String> savedBtAddress = new ArrayList<String>();
                ArrayList<String> savedBtNames = new ArrayList<String>();
                savedBtAddress =  AppHelper.getStringArrayPref(mContext,"BT_ADDRS");
                savedBtNames =  AppHelper.getStringArrayPref(mContext,"BT_NAMES");
                if(!savedBtNames.contains(btNames))
                {
                    savedBtAddress.add(btAddress);
                    savedBtNames.add(btNames);
                }
                else
                {
                    savedBtAddress.set(savedBtNames.indexOf(btNames) , btAddress);
                }
                Log.e("Jonathan", " bbb :: " + savedBtAddress.size());
                Log.e("Jonathan", " bbb1 :: " + savedBtAddress.toString());
                AppHelper.setStringArrayPref(mContext, "BT_ADDRS",savedBtAddress);
                AppHelper.setStringArrayPref(mContext, "BT_NAMES",savedBtNames);


                showBTConfig();


//                loadBTReaderInfo();
                dismissDialog();
            }

        });

        arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1);
        ListView listView2 = (ListView) wiseDialog.findViewById(R.id.discoveredDeviceList);            //연결가능한 리더기
        listView2.setAdapter(arrayAdapter);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvMessage.setText(R.string.msg_vandl_dialog_ask_touch_to_connect_again);
                BTReaderInfo btReaderInfo = new BTReaderInfo(foundDevices.get(position).getName(),foundDevices.get(position).getAddress());

                if(foundDevices.size() > 0)
                {
                    //Jonathan 180106 수정
                    AppHelper.setBTReaderInfo(btReaderInfo);
                    String btAddress = foundDevices.get(position).getAddress();
                    String btNames = foundDevices.get(position).getName();
                    ArrayList<String> savedBtAddress;
                    ArrayList<String> savedBtNames;
                    savedBtAddress =  AppHelper.getStringArrayPref(mContext,"BT_ADDRS");
                    savedBtNames =  AppHelper.getStringArrayPref(mContext,"BT_NAMES");
                    if(!savedBtNames.contains(btNames)) {
                        savedBtAddress.add(btAddress);
                        savedBtNames.add(btNames);
                    }
                    else {
                        savedBtAddress.set(savedBtNames.indexOf(btNames) , btAddress);
                    }
                    Log.e("Jonathan", " bbb :: " + savedBtAddress.size());
                    Log.e("Jonathan", " bbb1 :: " + savedBtAddress.toString());
                    AppHelper.setStringArrayPref(mContext, "BT_ADDRS",savedBtAddress);
                    AppHelper.setStringArrayPref(mContext, "BT_NAMES",savedBtNames);

                    showBTConfig();
                }
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
}
