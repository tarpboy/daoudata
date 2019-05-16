package com.devcrane.payfun.daou.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bbpos.bbdevice.BBDeviceController;
import com.devcrane.android.lib.emvreader.BlueToothListener;
import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.android.lib.emvreader.IntegrityCheckListener;
import com.devcrane.android.lib.entity.KeyExchangeResultEntity;
import com.devcrane.payfun.daou.MainActivity;
import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.customkeypad.KeyboardUtil;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Helper;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by admin on 7/19/17.
 */

public class VanDLStep2Dialog extends Dialog implements View.OnClickListener, IntegrityCheckListener, BlueToothListener {

    final String TAG = getClass().getSimpleName();
    private Context mContext;
    VanDLStep2DialogListener onVanDLStep2DialogListener;
    private TextView tvStep;
    EditText edCompanyNo;
    EmvReader emvReader;
    Button btnCancel;
    Button btnAccept;

    public VanDLStep2Dialog(Context context, VanDLStep2DialogListener vanDLStep2DialogListener) {
        super(context);
        mContext = context;
        onVanDLStep2DialogListener = vanDLStep2DialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_vandl_step2);
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
            emvReader.attachIntegrityCheckListener(VanDLStep2Dialog.this);
            emvReader.attachBlueToothListener(VanDLStep2Dialog.this);
        }
    }
    void detachService(){
        if(emvReader!=null){
            emvReader.detachIntegrityCheckListener(VanDLStep2Dialog.this);
            emvReader.detachBluetoothListener(VanDLStep2Dialog.this);
        }
    }


    void initView(){
        tvStep = (TextView)findViewById(R.id.tv_vandl_step);
        tvStep.setText(mContext.getString(R.string.vandl2_dialog_step));

        edCompanyNo = (EditText) findViewById(R.id.txtResCompanyNo);
        if(StaticData.IS_TEST){
            edCompanyNo.setText(StaticData.JTNET_TEST_COMPANY_NO);
        }
        edCompanyNo.addTextChangedListener(mTextWatcher);
        btnCancel = (Button)findViewById(R.id.btn_dlg_cancel);
        btnAccept = (Button)findViewById(R.id.btn_dlg_accept);
        btnCancel.setOnClickListener(this);
        btnAccept.setOnClickListener(this);

        if(edCompanyNo.getText().length()!=12){
            tongleButton(false);
        }else
            tongleButton(true);




//        disableShowSoftInput(edCompanyNo);
//        edCompanyNo.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                KeyboardUtil.hideSoftKeyboard((Activity)mContext);
//                showKeyboard(edCompanyNo);
//                return false;
//            }
//        });
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

    private KeyboardUtil ku;
    private void showKeyboard(EditText editText) {
        BHelper.db("showKeyboard");
//		if (ku == null)
        if (true) {
            ku = new KeyboardUtil((Activity)mContext, mContext, editText);
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
            try {
                edCompanyNo.setSelection(str.length());
            }catch (Exception ex){
                ex.printStackTrace();
            }
            edCompanyNo.addTextChangedListener(this);
            if(str.length()==12){
                tongleButton(true);
            }else{
                tongleButton(false);
            }

        }
    };
    @Override
    public void onClick(View v) {
        BHelper.db("click");
        switch (v.getId()){
            case R.id.btn_dlg_cancel:
                onVanDLStep2DialogListener.VanDLStep2DialogEvent(false);
                dismiss();
                break;
            case R.id.btn_dlg_accept:
                BHelper.db("accept");
                String companyNO = BHelper.requireTxt(edCompanyNo, R.string.register_companyno_is_required);
                onVanDLStep2DialogListener.VanDLStep2DialogEvent(companyNO);
                dismiss();
                break;
        }
    }

    @Override
    public void onBTReturnScanResults(List<BluetoothDevice> list) {

    }

    @Override
    public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {

    }

    @Override
    public void onBTScanTimeout() {

    }

    @Override
    public void onBTScanStopped() {

    }

    @Override
    public void keyExchangeCallback(KeyExchangeResultEntity exchangeResultEntity) {

    }

    @Override
    public void onBTConnected(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void injectMasterKeyCallback(String resultMsg) {

    }

    @Override
    public void onBTDisconnected() {
        onVanDLStep2DialogListener.VanDLStep2DialogEvent(false);
        dismiss();
    }

    @Override
    public void onDeviceHere(boolean isHere) {

    }

    @Override
    public void onNoDeviceDetected() {

    }

    @Override
    public void onDeviceUnplugged() {
        onVanDLStep2DialogListener.VanDLStep2DialogEvent(false);
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


}
