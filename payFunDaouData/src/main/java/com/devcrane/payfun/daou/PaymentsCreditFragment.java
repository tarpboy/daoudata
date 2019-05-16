package com.devcrane.payfun.daou;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bbpos.bbdevice.BBDeviceController;
import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.payfun.cardreader.EmvUtils;
import com.devcrane.payfun.daou.caller.ResPara;
import com.devcrane.payfun.daou.customkeypad.KeyboardUtil;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.EmvTcEntity;
import com.devcrane.payfun.daou.entity.EncPayInfo;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.DateHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.IUpdateUI;
import com.devcrane.payfun.daou.utility.KeypadHelper;
import com.devcrane.payfun.daou.utility.ObServerHelper;
import com.devcrane.payfun.daou.utility.PaymentTask;
import com.devcrane.payfun.daou.utility.VanHelper;
import com.devcrane.payfun.daou.van.CreditCard;
import com.devcrane.payfun.daou.van.DaouData;
import com.devcrane.payfun.daou.van.DaouDataContants;
import com.devcrane.payfun.daou.van.EmvTc;
import com.devcrane.payfun.daou.van.PaymentBase;

import java.lang.reflect.Method;
//import com.devcrane.payfun.cardreader.EmvReader;

public class PaymentsCreditFragment extends PaymentsFragment {

    Activity at;
    String sVanName;
    Spinner spDiviMonth;
    TextView tvSWModelName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        BHelper.db("PaymentsCreditFragment onCreateView");
        return inflater.inflate(R.layout.fragment_credit, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        BHelper.db("PaymentsCreditFragment onStart");

    }

    @Override
    public void onResume() {
        super.onResume();
        loadFromCaller();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        at = getActivity();
        BHelper.setTypeface(getView());
        checkEmvCard();
        initView();
        StaticData.creditSuccessWithEmv = false;
    }

    private void initView() {
        onInitView();
        spDiviMonth = (Spinner) at.findViewById(R.id.edCreditMonth);
        edTAmount = (EditText) at.findViewById(R.id.edCreditTAmount);
        tvSWModelName = (TextView) at.findViewById(R.id.tvSWModelName);
        tvSWModelName.setText(DaouDataContants.SWModelName);
        edTAmount.addTextChangedListener(tAmounWatcher);
//        edTAmount.setOnKeyListener(kl);
        disableShowSoftInput(edTAmount);
        edTAmount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showKeyboard();
                return false;
            }
        });

        receiptEntity = new ReceiptEntity();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.list_month, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDiviMonth.setAdapter(adapter);
        spDiviMonth.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    diviMonth = "00";
                } else {
                    diviMonth = spDiviMonth.getSelectedItem().toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
//        loadFromCaller();
    }

    @Override
    protected void loadFromCaller() {
        super.loadFromCaller();
        String value = edTAmount.getText().toString().replace(",", "");

        Double val =0.0;
        if(!value.equals(""))
            val = Double.parseDouble(value);
        BHelper.db("amount from caller:"+ value);
        keypadHelper.checkAmount(val);
        spDiviMonth.setSelection(Integer.valueOf(diviMonth));
    }

    @Override
    protected boolean validateCredit() {
        String tAmount = edTAmount.getText().toString().replace(",", "");
        if (comEntity == null) {
            showStatus(at.getString(R.string.please_select_company_first));
            return false;
        }
        if (tAmount == null || tAmount.length() < 0 || tAmount.equals("")) {
            showStatus(at.getString(R.string.please_input_total_amount));
            return false;
        }
        icAmount = tAmount;
        //case ic dont need check card value.
//		if (getCardNo().equals("") && !isReadyICPayment) {
//			makeToast(at.getString(R.string.please_input_card_value));
//			return false;
//		}
        return true;
    }

    private void doPaymentCredit() {
        updateDialogMsg(R.string.msg_pay_step_2_make_packet);
        String tAmount = edTAmount.getText().toString().replace(",", "");
        sVanName = comEntity.getF_VanName();
        String point = "0";
        if (!sPoint.equals("0")) {
            point = Helper.getPoint(sPoint, tAmount);
            tAmount = Helper.getTAmount(point, tAmount);
        }
        if (comEntity.getF_WithTax())
            receiptEntity = Helper.calWithTax(tAmount, "0", comEntity.getF_TaxRate(), comEntity.getF_ServiceTaxRate());
        else
            receiptEntity = Helper.calNoTax(tAmount, "0", comEntity.getF_TaxRate(), comEntity.getF_ServiceTaxRate());
        receiptEntity.setF_CouponDiscountRate(sPoint);
        receiptEntity.setF_CouponDiscountAmount(point);
        receiptEntity.setF_CouponID(sCouponID);
        if (payTypeSub.equals(StaticData.CREDIT_SUBTYPE_GIFT)
                || payTypeSub.equals(StaticData.CREDIT_SUBTYPE_ICC_SWIPE)
//                ||payTypeSub.equals(StaticData.CREDIT_SUBTYPE_BANK_CARD)
                )
            receiptEntity.setF_CardNo(bankCardData);
        else
            receiptEntity.setF_CardNo("");
        receiptEntity.setF_VanName(sVanName);
        receiptEntity.setF_CompanyNo(comEntity.getF_CompanyNo());
        receiptEntity.setF_MachineCode(comEntity.getF_MachineCode());
        receiptEntity.setF_TypeSub(payTypeSub);
        receiptEntity.setF_Type(StaticData.paymentTypeCredit);
        receiptEntity.setF_ReciptImage("");
        receiptEntity.setF_CardInputMethod(CardInputMethod);
        receiptEntity.setF_StaffName(AppHelper.getCurrentUserName());
        receiptEntity.setF_UserID(AppHelper.getCurrentUserID());
        receiptEntity.setF_RequestDate(DateHelper.getYYYYMMDD());//DateHelper.getCurrentDateFull()//
        receiptEntity.setF_Month(Helper.appenZeroNumber(diviMonth, 2));
        receiptEntity.setF_ApprovalCode("");
        resetCardNo();
        BHelper.db("receipt to pay:" + receiptEntity.toString());

        doSendVanServer(receiptEntity);
    }

    private String setReciptPayment(ReceiptEntity mReEntity) {
        String result = null;
        BHelper.db("Vanname:" + sVanName);
        paymentEmv = new CreditCard();
        payment = new CreditCard();
        String emvData = EmvUtils.getEmvData();
//        EmvUtils.showTlv(emvData);
//        String signData = AndroidBmpUtil_1Bit.makeImageInBase64(imageByte);
        String signData = "";
        if(imageByte!=null && imageByte.length>0){
            try {
                signData = new String(imageByte);
            }catch (Exception ex){
                ex.printStackTrace();
                signData="";
            }
        }
//        EncPayInfo encPayInfo = new EncPayInfo("", emvData, Base64Utils.base64Encode(imageByte));
        EncPayInfo encPayInfo = new EncPayInfo("", emvData, signData);

        if (mReEntity.getF_CardInputMethod().equals(DaouDataContants.VAL_WCC_IC))
            result = paymentEmv.payEmv(mReEntity, encPayInfo);
        else
            result = payment.pay(mReEntity, encPayInfo);

        isReceivedFromVan = true;
        if (!(mReEntity.getF_TypeSub().equals(StaticData.CREDIT_SUBTYPE_GIFT)
                || mReEntity.getF_TypeSub().equals(StaticData.CREDIT_SUBTYPE_ICC_SWIPE))) {
            isHaveToSendBack = true;


            //check networkResult to cancel
            if(DaouData.getNetworkResult().equals(DaouData.NETWORK_RESULT_NO_EOT)){
                BHelper.db("receiptEntity to cancel network 1:"+ receiptEntity.toString());
                return "";
            }

            Log.d("Payfun", "sendOnlineProcessResult: " + result);
            if (result != null && !result.equals("")) {
                if (StaticData.creditSuccessWithEmv) {
                    result = PaymentBase.makeEmvResponse(DaouData.cardType, result);
                    emvReader.sendOnlineProcessResult(result);
                } else {
                    result = PaymentBase.makeEmvResponseSimple();
                    BHelper.db("send simple:"+ result + " just writeback null");

                    emvReader.sendOnlineProcessResult(result);
                }


            } else {
//				emvReader.sendOnlineProcessResult("");
            }
            //reset emv data
//			EmvUtils.saveEmvData("");
        } else {

        }
        encPayInfo = null;
        mReEntity = null;
        return result;
    }

    void doSendVanServer(final ReceiptEntity mReEntity) {
        final String cardToCancel = mReEntity.getF_CardNo();
        StaticData.PaymentSuccess = StaticData.PaymentSuccess_Credit;
        new PaymentTask(at, R.string.msg_pay_step_3_ready_send_van, R.drawable.progress_ing) {

            @Override
            public String run() {
                this.updateCdialog(R.drawable.progress_sending);
//                this.updateMsg(at.getString(R.string.msg_pay_step_4_send_van));
                return setReciptPayment(mReEntity);
            }

            @Override
            public boolean res(String result) {
                BHelper.db("setReciptPayment:"+result);

                //Jonathan 171210 수정
                updateCDialog(R.drawable.progress_exiting);
//                updateDialogMsg(R.string.msg_pay_step_5_finishing);

                checkNetworkResult(DaouData.getNetworkResult());
                //check networkResult to cancel
                if(DaouData.getNetworkResult().equals(DaouData.NETWORK_RESULT_NO_EOT)){
                    BHelper.db("receiptEntity to cancel network 2 ===> dont need cancel:"+ mReEntity.toString());
//                    mReEntity.setF_CardNo(cardToCancel);
//                    mReEntity.setF_RequestDate(DateHelper.getCurrentDateFull());
//                    VanHelper.setVanCancel(mReEntity,"",true);
                    closeDialogDelay(5000);
                }else if (StaticData.isReadyShowReceipt() && result != null && result.length() > 0) {

                    if ((mReEntity.getF_TypeSub().equals(StaticData.CREDIT_SUBTYPE_GIFT)
                            || mReEntity.getF_TypeSub().equals(StaticData.CREDIT_SUBTYPE_ICC_SWIPE))) {
                        closeDialog();
                        AppHelper.removeDataWithKey(EmvReader.EMV_DATA);
                        MainActivity.setFragment(new ReceiptViewFragment());
                        showFallbackDlg(AppHelper.getVanMsg());
                    } else if (isHaveToSendBack && !StaticData.creditSuccessWithEmv) {
                        BHelper.db("dont need write back card 2");
//                      Have to wait from transtactionResult dont show receipt now
//                        closeDialog();
//                        AppHelper.removeDataWithKey(EmvReader.EMV_DATA);
//                        MainActivity.setFragment(new ReceiptViewFragment());
                    }

                } else {
                    if (!isHaveToSendBack)
                        closeDialogDelay(5000);
                }
                if (result == null || result.equals("")) {
                    resetToPayAgain(true);
                    showFallbackDlg(AppHelper.getVanMsg());
                    ResPara.returnFail(at);
//                    closeDialogDelay(5000);
//					BHelper.showToast(R.string.msg_request_payment_is_not_successful);
//                    BHelper.showToast(AppHelper.getVanMsg());
//                    showFallbackDlg(AppHelper.getVanMsg());

                }

//                showFallbackDlg(AppHelper.getVanMsg());
                ObServerHelper.processObserver(getActivity());
                boolean ret  = result != null;
                result = "";
                return ret;
            }
        };

    }
    void resetToPayAgainDelay(){
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                resetToPayAgain();
            }
        }, 3000);
    }
    @Override
    protected void cleanPaymentData() {

    }

    @Override
    protected void doConfirmPayment() {
        isIgnoreCheckDeclined =false;
        //prevent do again during waiting sendOlineProcessResult.
        if (!AppHelper.checkLastPayment()) {
            resetToPayAgain();
            closeDialog();
            return;
        }
        AppHelper.setLastPayment();

        if (payTypeSub.equals(StaticData.CREDIT_SUBTYPE_ICC)
                || payTypeSub.equals(StaticData.CREDIT_SUBTYPE_GIFT)
                || payTypeSub.equals(StaticData.CREDIT_SUBTYPE_ICC_SWIPE)
//				||payTypeSub.equals(StaticData.CREDIT_SUBTYPE_BANK_CARD)
                )
            doPaymentCredit();
        else
            doPaymentCredit();
//			doPaymentBankCard();
    }

    @Override
    protected void doTransactionResult(BBDeviceController.TransactionResult result) {
        // TODO Auto-generated method stub
        super.doTransactionResult(result);
        ReceiptEntity reciptEn = AppHelper.getReceipt();
        String reciptEnJson = VanHelper.payment(reciptEn);
        if (reciptEnJson != null) {
                StaticData.sResultPayment = reciptEnJson;
        }
        AppHelper.resetReceipt();

//        closeDialog();
        EmvTcEntity tcEntity = AppHelper.getEmvTcInfo();
        if(tcEntity!=null && !tcEntity.getApprovalNo().equals("") && tcEntity.getEmvOption().equals("Y")){
            DaouData daouData = new EmvTc(tcEntity);
            daouData.req(new TerminalInfo());
        }
        showFallbackDlg(AppHelper.getVanMsg());
        AppHelper.removeDataWithKey(EmvReader.EMV_DATA);
        if (StaticData.isReadyShowReceipt())
            MainActivity.setFragment(new ReceiptViewFragment());
    }
    Double lastAmount = 0.0;
    boolean checkUpdateAmount(Double value){
        boolean result = false;
        if((value>StaticData.SIGNATURE_AMOUNT_LIMIT && lastAmount< StaticData.SIGNATURE_AMOUNT_LIMIT)|| (value<StaticData.SIGNATURE_AMOUNT_LIMIT && lastAmount> StaticData.SIGNATURE_AMOUNT_LIMIT)){

            result =true;
        }
        
        lastAmount = value;
        return result;
    }
    KeypadHelper keypadHelper = new KeypadHelper(new IUpdateUI() {
        @Override
        public void setSignatureLayout(boolean isShow) {
            setDisplaySignature(isShow);
        }
    });
    String amountStr="";
    String displayText ="";
    Double val =0.0;
    View.OnKeyListener kl = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            BHelper.db("OnKeyListener:"+keyCode);
            if(event.getAction()==KeyEvent.ACTION_UP){
                switch (keyCode){
                    case KeyEvent.KEYCODE_DEL:
                        if(amountStr.length()>0){
                            amountStr = amountStr.substring(0,amountStr.length()-1);
                        }
                        BHelper.db("input str:"+ amountStr);
                        displayText = Helper.formatNumberExcel(amountStr);
                        edTAmount.setText(displayText);
                        edTAmount.setSelection(displayText.length());

                        if(!amountStr.equals(""))
                            val = Double.parseDouble(amountStr);
                        keypadHelper.checkAmount(val);
                        break;
                    case KeyEvent.KEYCODE_0:
                    case KeyEvent.KEYCODE_1:
                    case KeyEvent.KEYCODE_2:
                    case KeyEvent.KEYCODE_3:
                    case KeyEvent.KEYCODE_4:
                    case KeyEvent.KEYCODE_5:
                    case KeyEvent.KEYCODE_6:
                    case KeyEvent.KEYCODE_7:
                    case KeyEvent.KEYCODE_8:
                    case KeyEvent.KEYCODE_9:
                        if(amountStr.length()>8)
                            break;
                        amountStr = amountStr+ (char)event.getUnicodeChar();

                        displayText = Helper.formatNumberExcel(amountStr);
                        edTAmount.setText(displayText);
                        try{
                            edTAmount.setSelection(displayText.length());
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                        BHelper.db("input str:"+ amountStr);

                        if(!amountStr.equals(""))
                            val = Double.parseDouble(amountStr);
                        keypadHelper.checkAmount(val);
                        break;
                    default:
                        break;
                }
            }
            return true;
        }
    };
    private TextWatcher tAmounWatcher = new TextWatcher() {

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
            boolean b = s.toString().equals("");
            String value = edTAmount.getText().toString().replace(",", "");
            edTAmount.removeTextChangedListener(tAmounWatcher);
            String str = Helper.formatNumberExcel(value);
            if (str.equals("0"))
                str = "";
            edTAmount.setText(str);
            if (!b) {
                try {
                    edTAmount.setSelection(str.length());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            edTAmount.addTextChangedListener(tAmounWatcher);

            Double val =0.0;
            if(!value.equals(""))
                val = Double.parseDouble(value);
            keypadHelper.checkAmount(val);


//            if(checkUpdateAmount(val))
//                setDisplaySignature(value, false);

        }
    };
    private KeyboardUtil ku;

    private void showKeyboard() {
        BHelper.db("showKeyboard");
        if (ku == null) {
            ku = new KeyboardUtil(at, at, edTAmount);
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