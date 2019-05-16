package com.devcrane.payfun.daou;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bbpos.bbdevice.BBDeviceController;
import com.devcrane.android.lib.emvreader.DetectEmvListener;
import com.devcrane.android.lib.emvreader.EmvApplication;
import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.android.lib.emvreader.EmvReaderService;
import com.devcrane.payfun.cardreader.EmvUtils;
import com.devcrane.payfun.daou.caller.ParaConstant;
import com.devcrane.payfun.daou.caller.ReqPara;
import com.devcrane.payfun.daou.caller.ResPara;
import com.devcrane.payfun.daou.customkeypad.KeyboardUtil;
import com.devcrane.payfun.daou.data.PayFunDB;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.BTReaderInfo;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.KeyBindingEntity;
import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.entity.UserEntity;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.manager.NoticeManager;
import com.devcrane.payfun.daou.manager.UserManager;
import com.devcrane.payfun.daou.ui.CompanyAdapter;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.BHelper.DialogHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.MyTask;
import com.devcrane.payfun.daou.utility.MyTaskSetToast;
import com.devcrane.payfun.daou.utility.MyTaskStr;
import com.devcrane.payfun.daou.utility.MyTaskToast;
import com.devcrane.payfun.daou.utility.SoundSearcher;
import com.devcrane.payfun.daou.utility.StringTaskSetToast;
import com.devcrane.payfun.daou.van.DaouData;
import com.devcrane.payfun.daou.van.DaouDataContants;
import com.devcrane.payfun.daou.van.DaouDataHelper;
import com.devcrane.payfun.daou.van.OpenTerminal;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends FragmentActivity implements DetectEmvListener {
    private static FragmentActivity at;
//    static String UPDATE_UID, F_USERID, F_PASSWD;
    String userID="";
    UserEntity key = new UserEntity(0);
    static WifiManager wifiManager;
    public SlidingMenu mSlidingMenu;
    static int[] ids = {R.id.btnMRProfile, R.id.btnMRHome, R.id.btnMRCoupon, R.id.btnMRCredit,//
            R.id.btnMRHistory, R.id.btnMRCash, //
            R.id.btnMRMenuLeft, R.id.btnMRMenuRight,//
            R.id.menuMainHome, R.id.menuMainCancelList, R.id.menuMainCredit, R.id.menuMainCash};
    EmvApplication app;
    static EmvReader emvReader;
    boolean isCall = false;
    ProgressDialog dialog;
    Dialog pDialog;
    AlertDialog fallbackDlg;
    IntentFilter filter1;
    static final int SHOWING_DIALOG_LIMIT = 1;
    static int showingDialogCount = 0;
    public static boolean isBTReaderConnected = false;

    public static final int WAIT_FOR_BT_TURN_ON = 0;
    public static final int STOP_SEARCHING = 100;
    private int totalTime = 0;
    private static final int TOTAL_TIME_LIMIT = 30;
    private Handler mHandler = new MessageHandler();
    public static boolean isWaitTurnOnBT = false;
    public static boolean isRequiredWait = true;
    public static boolean isResumeOnMain = false;
    static {
        System.loadLibrary("daou-sign-jni");
    }
    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if (extras.containsKey("value")) {
                    BHelper.db("Value is:" + extras.get("value"));
                    EmvReaderService ers = app.getEmvReaderService();
                    if (ers != null) {
                        BHelper.db("initEmvResources");
                        emvReader = ers.getEmvReader();
                        attachService();
                    } else {
                        BHelper.db("getEmvReaderService is null");
                    }
                }
            }
        }
    };

    public static EmvReader getEmvReader() {
        return emvReader;
    }

    void attachService() {

        if (emvReader != null) {
            if (isCall) {
                emvReader.setIsForCancel(true);
            }
            emvReader.attachDetectEmvListener(MainActivity.this);
            if (EmvReader.getIsBlueTooth()) {
                connectBT();
                return;
            }
            if (Helper.isHeadsetConnected(at)) {

                try {
                    if (!isCall) {
                        showDialog();
                        Thread.sleep(4000);
                        emvReader.getDeviceInfo();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            BHelper.db("emvReader is null on MainActivity");
        }
    }

    void detachService() {
        unregisterReceiver(btReaderReceiver);
        if (emvReader != null) {
            emvReader.detachDetectEmvListener(MainActivity.this);
            BHelper.db("detachService from MainActivity ");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String fcmToken="";
        if (isPlayServiceAvailable(this, 0)) {
            fcmToken = FirebaseInstanceId.getInstance().getToken();
            BHelper.db("fcm_token:"+fcmToken);
            if(fcmToken!=null && !fcmToken.equals("")){
                AppHelper.setFcmToken(fcmToken);
                autoLogin();
            }
        }







        //ShortcutIcon();
        AppHelper.setAppSleep("false");
        setIsBlueTooth();
        BHelper.db("current Volumn:" + BHelper.getCurrentVolumn(this));
        AppHelper.setCurrentVolumn();
        BHelper.db("onCreate on MainActivity");
        setContentView(R.layout.content_frame);
        BHelper.setActivity(at = this);
        initSlidingMenu();
        app = (EmvApplication) getApplication();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        app.startEmvReaderService(MainActivity.class);
        BHelper.db("startEmveaderService.......1");
        filter1 = new IntentFilter("InitializationBroadcast");
        registerReceiver(myReceiver, filter1);
        registerBTReaderListener();

        checkDBBeforeRunApp();
        showingDialogCount = 0;

    }



    public boolean isPlayServiceAvailable(Context context, int requestCode) {
        boolean result = false;
        GoogleApiAvailability availabilityInstance = GoogleApiAvailability.getInstance();
        int statusCode = availabilityInstance.isGooglePlayServicesAvailable(context);
        if (statusCode == ConnectionResult.SUCCESS) {
            result = true;
        } else {
            if (availabilityInstance.isUserResolvableError(statusCode)) {
                availabilityInstance.getErrorDialog((Activity) context, statusCode, requestCode).show();
            } else {
                BHelper.db("This device is not supported.");
                finish();
            }
        }
        return result;
    }
    public static void setIsBlueTooth(){
        boolean isBT = false;
        if(AppHelper.getReaderType()== EmvReader.READER_TYPE_BT)
            isBT = true;
        EmvReader.setIsBlueTooth(isBT);
    }
    void registerBTReaderListener() {
        BHelper.db("registerBTReaderListener");
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(btReaderReceiver, filter1);
        this.registerReceiver(btReaderReceiver, filter2);
        this.registerReceiver(btReaderReceiver, filter3);
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumeOnMain = true;
        StaticData.GETCOUPON = false;
        setIsBlueTooth();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        BHelper.db("onResume on MainActivity");

        //Jonathan 171207 추가.
        if(!"".equals(AppHelper.getTID()))
        {
            callPayment();
            EmvUtils.setIsReadyIC(false);

            //check if user enable app after sleep
            if(AppHelper.getAppSleep().equals("true")) {
                connectDevice();
            }
        }



    }

    void connectDevice(){

//        BHelper.db("connection mode:"+emvReader.emvSwipeController.getConnectionMode().toString());
        BHelper.db("EmvReader.getIsBlueTooth():" + EmvReader.getIsBlueTooth());
        BHelper.db("isBTReaderConnected:" + isBTReaderConnected);
        if (EmvReader.getIsBlueTooth()) {
            if(!StaticData.isAtPaymentScreen)
                showDialog();
            if (!isBTReaderConnected) {
                connectBT();
            } else if(emvReader!=null && emvReader.getConnectionMode().equals(BBDeviceController.ConnectionMode.BLUETOOTH)){
                BHelper.db("call getDeviceInfo");
                if(!StaticData.isAtPaymentScreen)
                    emvReader.getDeviceInfo();
            }
        }else{
            if(emvReader!=null && emvReader.emvSwipeController!=null){
                String connectionMode = emvReader.emvSwipeController.getConnectionMode().toString();
                BHelper.db("connection mode2:"+connectionMode);
                if(connectionMode.equals(BBDeviceController.ConnectionMode.AUDIO.toString()))
                    return;
                if(connectionMode.equals(BBDeviceController.ConnectionMode.NONE.toString())
                        || connectionMode.equals(BBDeviceController.ConnectionMode.AUDIO)){
                    BHelper.db("start audio");
                    emvReader.restartAudio();
                }
            }
        }
    }
    boolean checkToStartService() {
        Intent intentCaller = getIntent();
        String isCalled = intentCaller.getStringExtra("isCalled");
        if (isCalled != null && isCalled.equals("true")) {
            isCall = true;
            StaticData.setToExit(false);
            StaticData.setIsCalled(true);
            return true;
        } else {
            return true;
        }
    }

    @Override
    protected void onPause() {
        BHelper.db("onPause");
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(KeyboardUtil.isShow()){
            KeyboardUtil.getInstance(this,this).hideKeyboard();
            return;
        }

        if (cfm(CancelSelectorFragment.class) || cfm(CancelDailyListFragment.class) || cfm(CouponFragment.class)//
                || cfm(PaymentsCashFragment.class) || cfm(PaymentsCreditFragment.class) || cfm(ProfileFragment.class) || cfm(CardReadingFragment.class)) {
            setFragment(new HomeFragment());
        } else if (cfm(UserFragment.class) || cfm(SignUpCompleteFM.class)) {
            setFragment(!UserFragment.bModify ? new SignUpFragment() : new ProfileFragment());
        } else if (cfm(SignUpFragment.class)) {
            setFragment(new LoginFragment());
        } else if (cfm(PaymentPeriodFM.class) || cfm(ConfigFragment.class) || //
                cfm(CompanyListFragment.class) || cfm(RegistervanNewFragment.class) || //
                cfm(ConfigPrintFM.class)) {
            setFragment(new ProfileFragment());
        } else if (cfm(DetailsCompanyFragment.class)) {
            setFragment(new CompanyListFragment());
        } else if (cfm(ReceiptCancelFragment.class)) {
            setFragment(!CancelDailyListFragment.isDailyChart ? new CancelListFragment() : new CancelDailyListFragment());
        } else if (cfm(CancelListFragment.class)) {
            setFragment(new CancelDailyListFragment());
        } else if (cfm(CouponDetailFragment.class)) {
            if (!CouponDetailFragment.sCanBarCode())
                setFragment(new CouponFragment());
        } else if (cfm(CancelCashFragment.class) || (cfm(CancelPaymentFragment.class))) {
            setFragment(new CancelSelectorFragment());
        } else if (cfm(ReceiptViewFragment.class)) {
            if (CancelDailyListFragment.isDailyChart)
                setFragment(new CancelDailyListFragment());
            else if (CancelListFragment.isChartList)
                setFragment(new CancelListFragment());
            else
                setFragment(new HomeFragment());
        } else {//if (BHelper.showBackExit())

            new AlertDialog.Builder(this).setTitle("앱을 종료하시겠습니까?").setCancelable(true).setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            AppHelper.setIsLogin(false);
                            EmvApplication app = (EmvApplication) getApplication();
                            app.stopApp();
                            System.exit(0);
                            System.exit(0);
                        }
                    }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }

    public static MainActivity getInstance() {
        return (MainActivity) at;
    }

    private boolean cfm(Class<?> pClass) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        return pClass.getName().equals(fragment.getClass().getName());
    }

    @Override
    protected void onStop() {
        BHelper.db("onStop on MainActivity");
        AppHelper.setAppSleep("true");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        BHelper.db("onDestroy");
        super.onDestroy();
        detachService();
        if (app != null)
            app.stopEmvReaderService();
    }

    void doCaller1CheckEmail(final String pf_Email, final String pf_Passwd, final String reqParaJson) {
        BHelper.db("doCaller1CheckEmail");
        new StringTaskSetToast(at) {

            @Override
            public String run() {
                return UserManager.checkEmail(pf_Email, " ");
            }

            @Override
            public boolean res(String result) {
                if (result.equals("-1")) {
                    showToast(at.getString(R.string.sqlite_error));
                    ResPara.returnFail(at);
                } else if (result.equals("0")) {
                    showToast(at.getString(R.string.msg_wrong_ID));
                    ResPara.returnFail(at);
                } else {
                    doCaller2CheckLogin(pf_Email, pf_Passwd, reqParaJson);
                }
                return false;
            }
        };
    }

    void doCaller2CheckLogin(final String pf_Email, final String pf_Passwd, final String reqParaJson) {
        BHelper.db("doCaller2CheckLogin");
        new MyTaskSetToast(at) {
            @Override
            public boolean run() {
                return (userID = UserManager.checkLoginV1(at,pf_Email, pf_Passwd)) != null;
            }

            @Override
            public boolean res(boolean result) {
                UserEntity key = new UserEntity(0);
                if (!result) {
                    AppHelper.prefSet(key.getF_Email(), "");
                    AppHelper.prefSet(key.getF_Password(), "");
                    ResPara.returnFail(at);
                } else {

                    AppHelper.prefSet(key.getF_Email(), "");
                    AppHelper.prefSet(key.getF_Password(), "");
                    AppHelper.setCurrentUserID(userID);

//                    AppHelper.prefSet(key.getF_ID(), F_USERID);
//                    UPDATE_UID = pf_Email;
//                    F_PASSWD = pf_Passwd;
                    doCaller3LoginSuccess(reqParaJson);
                }
                showToast(at.getString(R.string.sqlite_success), at.getString(R.string.msg_wrong_password));
                return true;
            }
        };
    }

    void autoLogin() {

        final String phoneNo = AppHelper.getMyPhoneNumber(this);
        final String deviceID = android.provider.Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        final String fcmToken = AppHelper.getFcmToken();
        BHelper.db("autoLogin");
        final String f_Passwd = AppHelper.prefGet(key.getF_Password(), "");

        //case user login before by old way then can not login this time. have to do check login in HomeFragment
        if(!AppHelper.getIsLogin() && !f_Passwd.isEmpty()){
            BHelper.db("Already login by old way. so have to update device info first");
            return;
        }

        new MyTaskSetToast(at) {
            @Override
            public boolean run() {
                return (userID = UserManager.checkLoginV2(phoneNo,deviceID,fcmToken)) != null;
            }

            @Override
            public boolean res(boolean result) {
                BHelper.db("F_USERID:"+userID);
                UserEntity key = new UserEntity(0);
                if (!result) {
                    AppHelper.prefSet(key.getF_Email(), "");
                    AppHelper.prefSet(key.getF_Password(), "");
                    ResPara.returnFail(at);
                } else {

                    AppHelper.prefSet(key.getF_Email(), "");
                    AppHelper.prefSet(key.getF_Password(), "");
                    AppHelper.setCurrentUserID(userID);

//                    AppHelper.prefSeBoolean(StaticData.Login, true);
                    AppHelper.setIsLogin(true);
                    if(AppHelper.getIsLogin()){
                        //check company in local
                        String userID = AppHelper.getCurrentUserID();
                        if(!CompanyManger.isExistCompanyLocal(userID)){
                            showDownloadConfirm();
                        }
                    }
                }
                return true;
            }
        };
    }

    void doCaller3LoginSuccess(final String reqParaJson) {
        BHelper.db("doCaller3LoginSuccess:" + userID);
        ReqPara reqPara = ReqPara.fromJsonString(reqParaJson);
        final String payType = reqPara.getPaymentType();
        final String transType = reqPara.getTransType();
        final String calledRegno = reqPara.getCompanyNo();
        final String machineCode = reqPara.getMachineNo();
        new MyTask(at) {

            @Override
            public boolean run() {
                AppHelper.prefSet(CompanyManger.TABLE, "");
                boolean isExist = CompanyManger.isExist(calledRegno);
                if (!isExist) {
                    CompanyManger.getCompanyByUserID(AppHelper.getCurrentUserID());
                } else {
                    BHelper.db("Dont need sync company data");
                }
                return true;
            }

            @Override
            public boolean res(boolean result) {
//                AppHelper.prefSeBoolean(StaticData.Login, true);
                AppHelper.setIsLogin(true);
                boolean isExist = CompanyManger.isExist(calledRegno, machineCode);
                if (!isExist) {
//                    downloadDaou(reqParaJson);
                    RegistervanNewFragment fragment = new RegistervanNewFragment();
                    Bundle args = new Bundle();
                    args.putString("isCalled", "true");
                    args.putString("reqParaJson", reqParaJson);
                    fragment.setArguments(args);
                    MainActivity.setFragment(fragment);

                    BHelper.db("Dont exist company. Try to download");
                    return false;
                } else {
                    BHelper.db("calledRegno:" + calledRegno);
                    doCaller4(calledRegno, machineCode, transType, payType, reqParaJson);
                }
                return false;
            }
        };
    }

    private CompanyEntity addCompany(CompanyEntity comE, String sTax, String vanName) {
        String sServiceTax = "0";
        comE.setF_UserID(AppHelper.getCurrentUserID());
        comE.setF_ServiceTaxRate(sServiceTax.equals("") ? "0" : sServiceTax);
        comE.setF_TaxRate(sTax.equals("") ? "0" : sTax);
        comE.setCREATE_UID(AppHelper.getCurrentUserID());
        comE.setUPDATE_UID(AppHelper.getUpdateUserID());
        comE.setF_PhoneNo(Helper.GetMyPhone(at));
        comE.setF_WithTax(true);
        comE.setF_VanName(vanName);
        return comE;
    }

    void downloadDaou(final String reqParaJson){
        BHelper.db("downloadCompany in MainActivity");
        ReqPara reqPara = ReqPara.fromJsonString(reqParaJson);
        final String sTax = reqPara.getTaxRate();
        final String vanName = StaticData.vanNameDaouData;
        final String companyNo = reqPara.getCompanyNo();
        final String machineNo = reqPara.getMachineNo();
        final String payType = reqPara.getPaymentType();
        final String transType = reqPara.getTransType();

        final DaouData daouData = new OpenTerminal();
        final TerminalInfo terminalInfo = new TerminalInfo();
        terminalInfo.setTerCompanyNo(companyNo);
        terminalInfo.setTerNumber(machineNo);
        new MyTaskStr(at){

            @Override
            public String[] run() {

                return daouData.req(terminalInfo);
            }

            @Override
            public boolean res(String[] result) {
                CompanyEntity comEntity;
                String publicKey ;
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
//                    ShowResult();
                    publicKey = result[15];
//					String data = DaouDataHelper.getData2KeyExchange(publicKey);
//					emvReader.injectMasterKey(data);
                    CompanyEntity companyEntity = addCompany(comEntity, sTax, vanName);
                    String f_idx = CompanyManger.insertCompanyJson(companyEntity);
                    if (f_idx != null && !f_idx.equals("")) {
                        companyEntity.setF_ID(f_idx);
                        CompanyManger.insertCompany(companyEntity);
                        doCaller4(companyNo, machineNo, transType, payType, reqParaJson);
                    } else {
                        BHelper.showToast("Company no is incorrect");
                        ResPara.returnFail(at);
                    }

                }else {
                    String msg =result[21];
                    BHelper.showToast(msg);
//                    BHelper.showToast("Company no is incorrect");
                    ResPara.returnFail(at);
//                    btnSaveCompany.setEnabled(false);
//                    btnSaveCompany.setSelected(true);
//                    btnKeyBindingRegister.setEnabled(false);
                }
                return false;
            }
        };
    }

    void downloadCompany(final String reqParaJson) {
        BHelper.db("downloadCompany");
        ReqPara reqPara = ReqPara.fromJsonString(reqParaJson);
        final String sTax = reqPara.getTaxRate();
        final String vanName = StaticData.vanNameDaouData;
        final String companyNo = reqPara.getCompanyNo();
        final String machineNo = reqPara.getMachineNo();
        final String payType = reqPara.getPaymentType();
        final String transType = reqPara.getTransType();

        //if don't have company or dont have machine code, have to go RegistervanFragment.
        //if finish RegisterVan and download company manually then have to go payment screen automatically

        RegistervanNewFragment fragment = new RegistervanNewFragment();
//        fragment.edCompanyNo.setText(companyNo);
//        fragment.edMachineCode.setText(machineNo);

        MainActivity.setFragment(fragment);
        /*
        new TaskDownload(at) {
            @Override
            public CompanyEntity run() {
                // TODO Auto-generated method stub
                CompanyEntity companyE = new CompanyEntity();
                //will be used for call app later
//				if(vanName.equals(StaticData.vanNameJTNet)){
//					companyE = JTNet.getVan(companyNo, machineNo);
//				}
                return companyE;
            }

            @Override
            public boolean res(CompanyEntity result) {
                String f_idx = "";

                if (result != null) {
                    CompanyEntity companyEntity = addCompany(result, sTax, vanName);
                    f_idx = CompanyManger.insertCompanyJson(companyEntity);
                    if (f_idx != null && !f_idx.equals("")) {
                        companyEntity.setF_ID(f_idx);
                        CompanyManger.insertCompany(companyEntity);
                        doCaller4(companyNo, machineNo, transType, payType, reqParaJson);
                    } else {
                        BHelper.showToast("Company no is incorrect");
                        ResPara.returnFail(at);
                    }
                } else {
                    BHelper.showToast("Company no is incorrect");
                    ResPara.returnFail(at);
                }
                return false;
            }

        };
        */
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
        CompanyEntity comE = CompanyManger.getCompany(calledRegno, machineNo);
        AppHelper.setCurrentVanID(comE.getF_ID());
        AppHelper.prefSet(StaticData.COMPANY_NO, comE.getF_CompanyNo());
        AppHelper.prefSet(StaticData.MACHINE_CODE, comE.getF_MachineCode());
        fragment.setArguments(args);
        MainActivity.setFragment(fragment);
        return false;
    }

    void callPayment() {
        final Intent intentCaller = getIntent();
        String isCalled = intentCaller.getStringExtra("isCalled");
        BHelper.db("isCalled:" + isCalled);
        StaticData.setToExit(false);
        if (isCalled != null && isCalled.equals("true")) {
            app.stopNotification();
            checkToStartService();
            StaticData.setIsCalled(true);
            final String reqParaJson = intentCaller.getStringExtra("reqParaJson");
            BHelper.db("reqParaJson:" + reqParaJson);
            ReqPara reqPara = ReqPara.fromJsonString(reqParaJson);
            final String userID = reqPara.getUserID();
            final String passWD = reqPara.getPassWD();
            BHelper.db("userID 0:" + userID);
            doCaller1CheckEmail(userID, passWD, reqParaJson);

        } else {
            StaticData.setIsCalled(false);
        }
    }

    private void checkDBBeforeRunApp() {
        new MyTaskToast(this, "로딩중...") {

            @Override
            public boolean run() {
                PayFunDB.InitializeDB(getBaseContext());
                if (!isCall) {
                    NoticeManager.getListWS();
                }
                return true;
            }

            @Override
            public boolean res(boolean result) {
                if (checkToStartService()) {

                    setFragment(new HomeFragment());
                }
                return true;
            }
        };
    }

    private void initSlidingMenu() {
        mSlidingMenu = new SlidingMenu(at);
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
        mSlidingMenu.setSlidingEnabled(false);
        // /* set menu right */
        for (int id : ids) {
            findViewById(id).setOnClickListener(onClickListener);
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        if (arg0 == PaymentPeriodFM.BUY_REQUEST_CODE) {
            PaymentPeriodFM.buyHelper.handleActivityResult(arg0, arg1, arg2);
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnMRProfile:
                    setFragment(new ProfileFragment());
                    break;
                case R.id.btnMRHome:

//                    if (EmvReader.getIsBlueTooth()) {
//                        showDialog();
//                        if (!isBTReaderConnected) {
//                            connectBT();
//                        } else {
//                            emvReader.getDeviceInfo();
//                        }
//                    }
                    if(!(EmvReader.getIsBlueTooth() && isBTReaderConnected))
                        connectDevice();

                    setFragment(new HomeFragment());
                    break;
                case R.id.menuMainHome:
                    if (!AppHelper.getIsLogin()){
                        BHelper.showToast(R.string.msg_not_login_yet);
                        break;
                    }
                    if(!(EmvReader.getIsBlueTooth() && isBTReaderConnected))
                        connectDevice();
                    setFragment(new HomeFragment());
                    break;
                case R.id.btnMRCredit:


                    Log.e("Jonathan1", isBTReaderConnected + "  " + Helper.isHeadsetConnected(at));

                    if (!isBTReaderConnected && !Helper.isHeadsetConnected(at))
                    {
//                        showRequestDevice();
//                        return;
                        BHelper.showToast("단말기 연결이 필요합니다.");
                    }


                    if (AppHelper.getIsLogin())
                        setFragment(new PaymentsCreditFragment());
                    else{
                        BHelper.showToast(R.string.msg_not_login_yet);
                    }
                    break;
                case R.id.menuMainCredit:


                    Log.e("Jonathan2", isBTReaderConnected + "  " + Helper.isHeadsetConnected(at));

                    if (!isBTReaderConnected && !Helper.isHeadsetConnected(at))
                    {
//                        showRequestDevice();
//                        return;
                        BHelper.showToast("단말기 연결이 필요합니다.");
                    }

                    if (AppHelper.getIsLogin())
                        setFragment(new PaymentsCreditFragment());
                    else{
                        BHelper.showToast(R.string.msg_not_login_yet);
                    }
                    break;
                case R.id.btnMRCash:
                    if (AppHelper.getIsLogin())
                        setFragment(new PaymentsCashFragment());
                    else{
                        BHelper.showToast(R.string.msg_not_login_yet);
                    }
                    break;
                case R.id.menuMainCash:
                    if (AppHelper.getIsLogin())
                        setFragment(new PaymentsCashFragment());
                    else{
                        BHelper.showToast(R.string.msg_not_login_yet);
                    }
                    break;
                case R.id.btnMRHistory:
                    CancelListFragment.isDaily = false;
                    if (AppHelper.getIsLogin())
                        setFragment(new CancelDailyListFragment());
                    else{
                        BHelper.showToast(R.string.msg_not_login_yet);
                    }
                    break;
                case R.id.menuMainCancelList:
                    CancelListFragment.isDaily = false;
                    if (AppHelper.getIsLogin())
                        setFragment(new CancelDailyListFragment());
                    else{
                        BHelper.showToast(R.string.msg_not_login_yet);
                    }
                    break;
                case R.id.btnMRCoupon:
                    BHelper.showToast(R.string.msg_unavailable_function);
 //                   if (StaticData.ISLOGIN)
 //                       setFragment(new CouponFragment());
 //                   else{
 //                       BHelper.showToast(R.string.msg_not_login_yet);
 //                   }
                    break;
                case R.id.btnMRMenuLeft:
                    mSlidingMenu.showMenu();
                    break;
                case R.id.btnMRMenuRight:
                    mSlidingMenu.showSecondaryMenu();
                    break;
                default:
                    break;
            }

        }
    };

    private Map<Integer, Fragment> getMapMR() {
        final Map<Integer, Fragment> map = new HashMap<Integer, Fragment>();
        map.put(R.id.btnMRProfile, new ProfileFragment());
        map.put(R.id.btnMRHome, new HomeFragment());
        map.put(R.id.btnMRCredit, new PaymentsCreditFragment());
        map.put(R.id.btnMRCash, new PaymentsCashFragment());
        map.put(R.id.btnMRHistory, new CancelDailyListFragment());
        map.put(R.id.btnMRCoupon, new CouponFragment());
        return map;
    }

    static final FragmentObject[] mListFragment = {new FragmentObject(ProfileFragment.class, R.string.title_profile),//
            new FragmentObject(PaymentsCashFragment.class, R.string.title_cash),//
            new FragmentObject(PaymentsCreditFragment.class, R.string.title_credit),//
            new FragmentObject(HistoryFragment.class, R.string.title_history),//
            new FragmentObject(CouponFragment.class, R.string.title_couponlist),//
            new FragmentObject(ExtendedFragment.class, R.string.title_extended),//
            new FragmentObject(LoginFragment.class, -1),//
            new FragmentObject(SignUpFragment.class, R.string.title_signup),//
            new FragmentObject(SignUpCompleteFM.class, R.string.title_signup_complete),//
            new FragmentObject(UserFragment.class, R.string.title_user_add),//
            new FragmentObject(RegistervanNewFragment.class, R.string.title_register_van),//
            new FragmentObject(CompanyListFragment.class, R.string.title_list_company),//
            new FragmentObject(PaymentPeriodFM.class, R.string.title_payment_period),//
            new FragmentObject(ConfigFragment.class, R.string.title_config),//
            new FragmentObject(HomeFragment.class, 0),//
            new FragmentObject(CancelListFragment.class, R.string.title_cancellist),//
            new FragmentObject(CancelSelectorFragment.class, R.string.title_cancel_selector),//
            new FragmentObject(CancelPaymentFragment.class, R.string.title_cancel_payment),//
            new FragmentObject(CancelCashFragment.class, R.string.title_cancel_cash),//
            new FragmentObject(ReceiptViewFragment.class, R.string.title_receipt),//
            new FragmentObject(ReceiptCancelFragment.class, R.string.title_receipt),//
            new FragmentObject(DetailsCompanyFragment.class, R.string.title_list_company),//
            new FragmentObject(CancelDailyListFragment.class, R.string.title_canceldailylist),};

    public static void lockMenu(boolean bLock) {
        BHelper.db("lockMenu:"+ bLock + " isCalled:"+StaticData.getIsCalled());
        if (!StaticData.getIsCalled()) {
            getInstance().mSlidingMenu.setSlidingEnabled(!bLock);
            getInstance().mSlidingMenu.setClickable(!bLock);
        }
        try {
            at.findViewById(R.id.btnMRMenuLeft).setVisibility(bLock ? View.INVISIBLE : View.VISIBLE);
            at.findViewById(R.id.layoutMenu).setVisibility(bLock ? View.GONE : View.VISIBLE);
            at.findViewById(R.id.btnMRMenuRight).setVisibility(bLock ? View.INVISIBLE : View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void lockMainMenu(boolean isLock) {
        if (!isLock || !StaticData.getIsCalled())
            return;
        for (int id : ids) {
            at.findViewById(id).setOnClickListener(null);
        }
    }

    public static void lockLeftRightMenu(boolean bLock) {
        getInstance().mSlidingMenu.setSlidingEnabled(!bLock);
        getInstance().mSlidingMenu.setClickable(!bLock);
        lockMainMenu(bLock);
    }

    public static void setFragment(final Fragment pFragment) {
        /* check fragment change */
        BHelper.db("setFragment:" + pFragment.getClass().getSimpleName());
        final Fragment fragment = at.getSupportFragmentManager().findFragmentById(R.id.content_frame);
        final String pName = pFragment.getClass().getName();
        if (fragment != null && pName.equals(fragment.getClass().getName())) {
            return;
        }

        lockMenu(false);
        if (getInstance().mSlidingMenu.isMenuShowing()) {
            getInstance().mSlidingMenu.toggle();
        }
        at.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        at.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, pFragment).commit();

		/* find title id */
        for (final FragmentObject e : mListFragment) {
            if (pName.equals(e.mClass.getName())) {
                setHeaderText(e.titleId);
                break;
            }
        }

		/* check selected button */
        final Map<Integer, Fragment> map = getInstance().getMapMR();
        for (final Integer id : map.keySet()) {
            final ImageButton ibtn = (ImageButton) at.findViewById(id);
            final boolean bSelected = pName.equals(map.get(id).getClass().getName());
            ibtn.setSelected(bSelected);
        }
        BHelper.db("end setFragment:" + pFragment.getClass().getSimpleName());
    }

    public static void setHeaderText(int titleId) {
        BHelper.db("setHeaderText");
		/* check no title */
        boolean bFullScreen = titleId == -1;
        View viewHeader = at.findViewById(R.id.viewHeader);
        viewHeader.setVisibility(bFullScreen ? View.GONE : View.VISIBLE);
        if (bFullScreen) {
            return;
        }

		/* set title */
        TextView tvHeaderTitle = (TextView) at.findViewById(R.id.tvHeaderTitle);
        BHelper.setTypeface(tvHeaderTitle);
        if (titleId == 0) {
            viewHeader.setBackgroundResource(R.drawable.bg_title_no_text);
            tvHeaderTitle.setText("");
        } else {
            viewHeader.setBackgroundResource(R.drawable.bg_title_text);
            tvHeaderTitle.setText(titleId);
        }
    }


    public static void setHeaderText(String titleId) {

        View viewHeader = at.findViewById(R.id.viewHeader);

		/* set title */
        TextView tvHeaderTitle = (TextView) at.findViewById(R.id.tvHeaderTitle);
        BHelper.setTypeface(tvHeaderTitle);

        viewHeader.setBackgroundResource(R.drawable.bg_title_text);
        tvHeaderTitle.setText(titleId);

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
    void setDefaultCompany(CompanyEntity comE){
        AppHelper.setCurrentVanID(comE.getF_ID());
        AppHelper.prefSet(StaticData.COMPANY_NO, comE.getF_CompanyNo());
        AppHelper.prefSet(StaticData.MACHINE_CODE, comE.getF_MachineCode());
        PaymentsCashFragment.setCompany(comE);
        CancelListFragment.setVantext(comE);
        CancelDailyListFragment.setVantext(comE);
        CouponDetailFragment.setCompany();
    }
    public void initMenuLeft() {
        final EditText txtSearch = (EditText) at.findViewById(R.id.txtSearch);


        final ImageButton btnSearch = (ImageButton) at.findViewById(R.id.buttonsearch);
        final ListView lvCompany = (ListView) at.findViewById(R.id.lvCompany);
        if (!AppHelper.getIsLogin()) {
            AppHelper.setCurrentUserID("");
//            AppHelper.prefSet(key.getF_ID(), "");
        }
        final List<CompanyEntity> objects = CompanyManger.getAllCompany(AppHelper.getCurrentUserID());


        String selectedVan = AppHelper.getCurrentVanID();
        if(objects!=null && objects.size()>0 && selectedVan.equals("")){
            setDefaultCompany(objects.get(objects.size()-1));
        }

        final CompanyAdapter adapter = new CompanyAdapter(at, objects, false);
        lvCompany.setAdapter(adapter);
        TextWatcher textSearch = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    lvCompany.setAdapter(adapter);
                    return;
                }
                List<CompanyEntity> list = new ArrayList<CompanyEntity>();
                for (CompanyEntity obj : objects) {
                    String keyword = s.toString();
                    if (SoundSearcher.matchString(obj.getF_CompanyName(), keyword) || obj.getF_CompanyNo().indexOf(keyword) > -1 || obj.getF_MachineCode().indexOf(keyword) > -1) {
                        list.add(obj);
                    }
                }
                lvCompany.setAdapter(new CompanyAdapter(at, list, true));

            }
        };
        txtSearch.addTextChangedListener(textSearch);
        lvCompany.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                adapter.setPosition(arg2);
                CompanyEntity comE = (CompanyEntity) lvCompany.getAdapter().getItem(arg2);
                AppHelper.setCurrentVanID(comE.getF_ID());
                AppHelper.prefSet(StaticData.COMPANY_NO, comE.getF_CompanyNo());
                AppHelper.prefSet(StaticData.MACHINE_CODE, comE.getF_MachineCode());
                PaymentsCashFragment.setCompany(comE);
                CancelListFragment.setVantext(comE);
                CancelDailyListFragment.setVantext(comE);
                CouponDetailFragment.setCompany();
            }
        });
        btnSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String s = txtSearch.getText().toString();
                List<CompanyEntity> listobjects = CompanyManger.getAllCompany(AppHelper.getCurrentUserID());
                if (s.toString().equals("")) {
                    lvCompany.setAdapter(new CompanyAdapter(at, listobjects, false));
                    return;
                }
                List<CompanyEntity> list = new ArrayList<CompanyEntity>();
                for (CompanyEntity obj : listobjects) {
                    String keyword = s.toString();
                    if (SoundSearcher.matchString(obj.getF_CompanyName(), keyword) || obj.getF_VanName().indexOf(keyword) > -1 || obj.getF_CompanyNo().indexOf(keyword) > -1 || obj.getF_MachineCode().indexOf(keyword) > -1) {
                        list.add(obj);
                    }
                }
                lvCompany.setAdapter(new CompanyAdapter(at, list, false));

            }
        });
    }


    @Override
    public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
        BHelper.db("onReturnDeviceInfo in MainActivity");
        closeMsg();
        EmvUtils.setIsReadyIC(true);
        // TODO Auto-generated method stub
        Set<String> keys = deviceInfoData.keySet();
        for (String key : keys) {
            BHelper.db(key + ":" + deviceInfoData.get(key));
        }

        String pinKsn = deviceInfoData.get("pinKsn") == null ? ""
                : deviceInfoData.get("pinKsn");
        String modelName = deviceInfoData.get("modelName") == null ? ""
                : deviceInfoData.get("modelName");
        EmvUtils.saveHWModelName(modelName);
        EmvUtils.saveKsn(pinKsn);

        String firmwareVersion = deviceInfoData.get("firmwareVersion") == null ? ""
                : deviceInfoData.get("firmwareVersion");
        String trackKsn = deviceInfoData.get("trackKsn") == null ? ""
                : deviceInfoData.get("trackKsn");
        String emvKsn = deviceInfoData.get("emvKsn") == null ? ""
                : deviceInfoData.get("emvKsn");
        String uid = deviceInfoData.get("uid") == null ? "" : deviceInfoData
                .get("uid");
        String csn = deviceInfoData.get("csn") == null ? "" : deviceInfoData
                .get("csn");

        String deviceSerial = EmvUtils.extractSerialNumber(pinKsn);
        String serialNumber = deviceInfoData.get("serialNumber")==null? DaouDataContants.VAL_PRODUCTION_SERIAL_NUMBER:deviceInfoData.get("serialNumber");
        // check to show Device name

        //save HwModelNo
        String modelNo = "";
        firmwareVersion = firmwareVersion.replace(".", "");
//		firmwareVersion = firmwareVersion.substring(0, 2) + firmwareVersion.substring(firmwareVersion.length()-2, firmwareVersion.length());
        if (firmwareVersion.length() >= 4)
            modelNo = firmwareVersion.substring(0, 4);
        EmvUtils.saveHwModelNo(modelNo);
        EmvUtils.saveHwSerialNumber(serialNumber);
        String publicKeyVersion = deviceInfoData.get("publicKeyVersion") == null? "" : deviceInfoData.get("publicKeyVersion");
        EmvUtils.savePublicKeyVersion(publicKeyVersion);
        EmvUtils.saveHwSerialNumber(serialNumber);

        String batteryLevel = "Battery: " + deviceInfoData.get("batteryPercentage") + " %";
        String hwModelInfo = EmvUtils.getHWModelName() + " " + EmvUtils.getHwModelNo();//JTNet.HWModelNo;
        String swModelInfo = DaouDataContants.SWModelName + " " + DaouDataContants.SWModelNo;

        ((TextView) findViewById(R.id.tvMenuRightBatteryInfo)).setText(batteryLevel);
        ((TextView) findViewById(R.id.tvMenuHWModelInfo)).setText(hwModelInfo);
        ((TextView) findViewById(R.id.tvMenuSWInfo)).setText(swModelInfo);


        //Set by Eric.
        KeyBindingEntity entity = new KeyBindingEntity("");
        entity.setF_Csn(csn);
        entity.setF_DeviceNo(deviceSerial);
        entity.setF_EmvKsn(emvKsn);
        entity.setF_FirmwareVersion(firmwareVersion);
        entity.setF_PinKsn(pinKsn);
        entity.setF_TrackKsn(trackKsn);
        entity.setF_Uid(uid);

        EmvUtils.saveEmvSerial(pinKsn);
        EmvUtils.saveKeyBinding(entity);
        KeyBindingEntity bindingEntity = EmvUtils.getKeyBinding(deviceSerial);

        //reset variables
        pinKsn = modelName = trackKsn =  emvKsn = uid = csn = batteryLevel = hwModelInfo = swModelInfo = firmwareVersion = deviceSerial = modelNo ="";
        deviceInfoData = null;

        // didn't select company
        if (bindingEntity == null) {
            return;
        }

    }


    void showRequestDevice(){

        new AlertDialog.Builder(this)
                .setTitle("단말기 연결이 필요합니다.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
//                        wifiManager.setWifiEnabled(false);
                        RegistervanNewFragment fragment = new RegistervanNewFragment();
                        MainActivity.setFragment(fragment);
                    }
                })
//                .setNegativeButton("아니오(cancel)", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//              }
//           })
                .show();
    }


    void showDownloadConfirm(){

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.msg_need_to_download_company_first))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
//                                                    wifiManager.setWifiEnabled(false);
                                                    RegistervanNewFragment fragment = new RegistervanNewFragment();
                                                    MainActivity.setFragment(fragment);
                                                }
                                    })
//                .setNegativeButton("아니오(cancel)", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//              }
//           })
            .show();
    }

    @Override
    public void onDeviceHere(boolean isHere) {
        // TODO Auto-generated method stub
        if (isHere) {
            emvReader.integrityCheck();
        } else if (!EmvReader.getIsBlueTooth() && Helper.isHeadsetConnected(at)) {
            emvReader.restartAudio();
            closeMsg();
            BHelper.showToast(R.string.msg_reconnect_device);
        } else {
            closeMsg();
            BHelper.showToast(R.string.msg_reconnect_device);
        }
        showingDialogCount = 0;
        isHere = false;
    }

    @Override
    public void onDevicePlugged() {
        // TODO Auto-generated method stub
        BHelper.db("current volumn:"+BHelper.getCurrentVolumn(this));

        if (emvReader != null && emvReader.emvSwipeController!=null && !StaticData.isAtPaymentScreen) {
            String connectionMode =emvReader.emvSwipeController.getConnectionMode().toString();
            BHelper.db("connectionMode:"+connectionMode);
            if(connectionMode.equals(BBDeviceController.ConnectionMode.AUDIO.toString())){
                try {
                    //dont restart audio for case kitkat 2017011
                    //                if(emvReader.emvSwipeController.getConnectionMode().equals(BBDeviceController.ConnectionMode.NONE))
                    //                    emvReader.restartAudio();
                    showDialog();
                    Thread.sleep(1000);
                    emvReader.isDeviceHere();
                    //				emvReader.getDeviceInfo();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else if(connectionMode.equals(BBDeviceController.ConnectionMode.NONE.toString())){

                try {
                    emvReader.startReader();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            emvReader.isDeviceHere();
                        }
                    }, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }

    }

    @Override
    public void onDeviceUnplugged() {
        BHelper.restoreVolumn(this);
        closeMsg();
        // TODO Auto-generated method stub
        ((TextView) findViewById(R.id.tvMenuRightBatteryInfo)).setText("");
        ((TextView) findViewById(R.id.tvMenuHWModelInfo)).setText("");
//        ((TextView)findViewById(R.id.tvMenuSWInfo)).setText("");
        EmvUtils.cleanDeviceValue();
    }

    @Override
    public void onNoDeviceDetected() {
        // TODO Auto-generated method stub
        closeMsg();
    }

    @Override
    public void onError(BBDeviceController.Error errorState) {
        // TODO Auto-generated method stub
        BHelper.db("onError on MainActivity");

        BHelper.db(EmvUtils.getEmvErrorString(this, errorState));
        if(isWaitTurnOnBT && EmvReader.getIsBlueTooth())
            return;
        switch (errorState){
            case COMM_LINK_UNINITIALIZED:
                if ( showingDialogCount < SHOWING_DIALOG_LIMIT) {
                    showingDialogCount += 1;
                    if (!EmvReader.getIsBlueTooth() && Helper.isHeadsetConnected(at)) {
                        emvReader.restartAudio();
                    }
                    closeMsg();
                    BHelper.showToast(R.string.msg_reconnect_device);
                }
                break;
            case COMM_ERROR:
                if ( showingDialogCount < SHOWING_DIALOG_LIMIT) {
                    showingDialogCount += 1;
                    if (!EmvReader.getIsBlueTooth() && Helper.isHeadsetConnected(at)) {
                        emvReader.restartAudio();
                    }
                    closeMsg();
                    BHelper.showToast(R.string.msg_reconnect_device);
                }
                break;
            case DEVICE_BUSY:
                String msg = at.getString(R.string.msg_reconnect_device);
                updateDialogMsg(msg);
                break;
            case FAIL_TO_START_AUDIO:
            case FAIL_TO_START_BT:
                isBTReaderConnected = false;
                closeMsg();
                BHelper.showToast(R.string.msg_reconnect_device);
                break;
            case INVALID_FUNCTION_IN_CURRENT_CONNECTION_MODE:
                emvReader.stopConnection();
            default:
                break;
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
        BHelper.db("autoConfigSettings:"+autoConfigSettings);
        if(autoConfigSettings!=null)
        emvReader.setAutoConfig();
        closeMsg();
        BHelper.db("auto config is completed");
        showToast(getString(R.string.msg_config_device_success));
    }

    @Override
    public void onAutoConfigError(BBDeviceController.AudioAutoConfigError autoConfigError) {
        // TODO Auto-generated method stub
        closeMsg();
        BHelper.db("auto config is error");
        showToast(getString(R.string.msg_config_device_failed));
    }

    @Override
    public void onAutoConfigProgressUpdate(double percentage) {
        // TODO Auto-generated method stub
        BHelper.db("config percent:" + (int) percentage);
        if (dialog != null && dialog.isShowing()) {
            updateDialogMsg(getString(R.string.msg_config_device_doing) + " " + (int) percentage + " %");
        }
    }

    @Override
    public void onReturnIntegrityCheckResult(boolean result) {
        BHelper.db("onReturnIntegrityCheckResult:" + result);
        Log.e("Jonathan", "Jonathan4");
        String logData = "INTERGRITY CHECK";
        logData += "\nResult:" + result;
        Helper.writeIntegrityLog(logData);
        logData = "";
        //171226 Jonathan 주석처리함
//        if (result == false || !result)
//            showFallbackDlg(getString(R.string.msgIntegrityCheckFail));
        emvReader.getDeviceInfo();
        result = false;
    }

    void showFallbackDlg(String msg) {
        fallbackDlg = new AlertDialog.Builder(at)
                .setTitle(R.string.emv_fallback_report)
                .setIcon(android.R.drawable.ic_dialog_alert).setMessage(msg)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();
    }

    public void updateDialogMsg(String msg) {
//		showStatus("");
        if (dialog != null && dialog.isShowing()) {
            dialog.setMessage(msg);
        }
    }

    public void showMsg(int rsid) {

        if (dialog == null || !dialog.isShowing()) {
            dialog = DialogHelper.makeDialog(rsid);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public void closeMsg() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void showToast(String message) {
        Toast.makeText(at, message, Toast.LENGTH_SHORT).show();
    }

    public void showDialog() {

        if (dialog == null || !dialog.isShowing()) {
            dialog = DialogHelper.makeDialog(R.string.msg_processing);
            dialog.setCancelable(false);
            dialog.show();
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

    //region WisePad

    static Dialog wiseDialog;

    public void dismissDialog() {
        if (wiseDialog != null) {
            wiseDialog.dismiss();
            wiseDialog = null;
        }
    }

    void connectBT() {

        if(emvReader.emvSwipeController!=null) {
            switch (emvReader.emvSwipeController.getConnectionMode()) {
                case NONE:
                    BHelper.db("mode is NONE so can connect BT device");
                    break;
                case BLUETOOTH:
                    BHelper.db("connection mode is BLUETOOTH");
//                    isBTReaderConnected = true;
                    return;
            }
        }



        if(dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }

        BTReaderInfo btReaderInfo = AppHelper.getBTReaderInfo();
        BHelper.db("connecting BT:" + btReaderInfo.getName() + ", " + btReaderInfo.getAddress());
        if (btReaderInfo!=null && !btReaderInfo.getName().equals("")) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(btReaderInfo.getAddress());
            try {
                emvReader.connectBT(device);
            } catch (Exception ex) {
                ex.printStackTrace();
                closeMsg();
                BHelper.showToast(R.string.msg_connect_bt_eror);
            }
        } else {
            closeMsg();
            BHelper.showToast(R.string.bluetooth_not_configured);
        }
    }

    @Override
    public void onBTReturnScanResults(List<BluetoothDevice> foundDevices) {

    }

    @Override
    public void onBTScanTimeout() {
        BHelper.db(getString(R.string.bluetooth_2_scan_timeout));
    }

    @Override
    public void onBTScanStopped() {
        BHelper.db(getString(R.string.bluetooth_2_scan_stopped));
    }

    @Override
    public void onBTConnected(BluetoothDevice bluetoothDevice) {
        BHelper.db(getString(R.string.bluetooth_connected) + ": " + bluetoothDevice.getAddress());
        isBTReaderConnected = true;
        isRequiredWait = true;
        totalTime = TOTAL_TIME_LIMIT;
        emvReader.integrityCheck();
    }

    @Override
    public void onBTDisconnected() {
        isBTReaderConnected = false;
        closeMsg();
        // TODO Auto-generated method stub
        ((TextView) findViewById(R.id.tvMenuRightBatteryInfo)).setText("");
        ((TextView) findViewById(R.id.tvMenuHWModelInfo)).setText("");
        BHelper.db(getString(R.string.bluetooth_disconnected));
        EmvUtils.cleanDeviceValue();
        if(!EmvReader.getIsBlueTooth()|| !isRequiredWait || isWaitTurnOnBT)
            return;

        waitTurnBT();

    }
    public void waitTurnBT(){
        isWaitTurnOnBT = true;
        mHandler.sendEmptyMessage(WAIT_FOR_BT_TURN_ON);
    }
    public void resetTotalWaitTime(){
        totalTime = TOTAL_TIME_LIMIT;
    }
    private final BroadcastReceiver btReaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            BTReaderInfo btReaderInfo = AppHelper.getBTReaderInfo();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Device is now connected
                BHelper.db("connected BT():" + device.getName());
                Log.e("Jonathan", "device.getName() :: " + device.getName());
                Log.e("Jonathan", "btReaderInfo.getName() :: " + device.getName());

                if (device.getName().contains(btReaderInfo.getName()) && !"".equals(btReaderInfo.getName())) {
                    isBTReaderConnected = true;
                    BHelper.showToast(R.string.bluetooth_connected);
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {


                //Device has disconnected
                BHelper.db("disconnected BT:" + device.getName());
                isBTReaderConnected = false;
                //show show toast for case Reader
                if(device.getName().contains("CHB10"))
                    BHelper.showToast(R.string.bluetooth_disconnected);
            }
        }
    };

    private void ShortcutIcon(){

        if(AppHelper.getCreateShortcut().equals("true"))
            return;

        Intent shortcutIntent = new Intent(getApplicationContext(), MainActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
        AppHelper.setCreateShortcut("true");
    }


    private class MessageHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WAIT_FOR_BT_TURN_ON:
                    BHelper.db("Will close dialog after wait 30s to insert card with paras Total: " + totalTime + "  isBTReaderConnected: " + isBTReaderConnected);
                    totalTime-=5;
                    if (totalTime > 0 && !isBTReaderConnected && EmvReader.getIsBlueTooth()) {
                        msg = obtainMessage(WAIT_FOR_BT_TURN_ON);
                        sendMessageDelayed(msg, 5000);
                        connectBT();
                        BHelper.db("Wait to close diaglog:" + totalTime);
                    } else if (totalTime <= 0 && !isBTReaderConnected  && EmvReader.getIsBlueTooth()) {
                        isWaitTurnOnBT = false;
                        closeMsg();
                    }

                    break;

                case STOP_SEARCHING:
                    isWaitTurnOnBT = false;
                    break;


                default:
                    break;
            }
        }
    }


}


