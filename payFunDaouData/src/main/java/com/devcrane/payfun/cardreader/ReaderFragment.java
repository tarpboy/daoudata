/***
 * Base class, used to payment.
 * This class implement interface EmvReaderListener to get data from Emv Device.
 * Init Reader library
 * 1.getDeviceInfo --> request get device info
 * 2.onReturnDeviceInfo --> return device info, then save data for keyBinding:
 * - csn
 * - deviceSerial
 * - emvKsn
 * - firmwareVersion
 * - pinKsn
 * - trackKsn
 * <p>
 * Payment process:
 * Start -->
 * 1. checkCard(app) --> check card
 * 2. onWaitingForCard(app) --> wait user insert card
 * 3. insertCard(user)
 * 4. onReturnCheckCardResult(app)--> return card type
 * 5. startEmv(app) --> get EmvData.
 * 6. onRequestOnlineProcess(app) --> device return EmvData.
 * 7. make packet and send request payment to JTNet (JTNet.paymentCard).
 * use EmvData to make packet
 * 8. JTNet return serviceCode
 * 9. sendOnlineProcessResult --> write back serviceCode to Card.
 * 10. End
 */

package com.devcrane.payfun.cardreader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bbpos.bbdevice.BBDeviceController;
import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.android.lib.emvreader.EmvReaderListener;
import com.devcrane.android.lib.entity.KSNEntity;
import com.devcrane.android.lib.entity.KeyExchangeResultEntity;
import com.devcrane.payfun.daou.CancelDailyListFragment;
import com.devcrane.payfun.daou.CancelPaymentFragment;
import com.devcrane.payfun.daou.CouponFragment;
import com.devcrane.payfun.daou.ExtendedFragment;
import com.devcrane.payfun.daou.HomeFragment;
import com.devcrane.payfun.daou.MainActivity;
import com.devcrane.payfun.daou.PaymentsCashFragment;
import com.devcrane.payfun.daou.PaymentsCreditFragment;
import com.devcrane.payfun.daou.ProfileFragment;
import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.caller.ResPara;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.InCompleteDataEntity;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.ui.SignatureView;
import com.devcrane.payfun.daou.utility.AndroidBmpUtil_1Bit;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Base64Utils;
import com.devcrane.payfun.daou.utility.CustomAlertDialog;
import com.devcrane.payfun.daou.utility.CustomDialog;
import com.devcrane.payfun.daou.utility.DaouSignHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.HexDump;
import com.devcrane.payfun.daou.utility.ObPaymentHelper;
import com.devcrane.payfun.daou.utility.SDcardHelper;
import com.devcrane.payfun.daou.utility.BHelper.DialogHelper;
import com.devcrane.payfun.daou.van.CardHelper;
import com.devcrane.payfun.daou.van.DaouData;
import com.devcrane.payfun.daou.van.DaouDataContants;
import com.devcrane.payfun.daou.van.InCompleteTrans;
import com.devcrane.payfun.daou.van.PaymentBase;


public class ReaderFragment extends Fragment implements EmvReaderListener {

    protected boolean isReadCard = true, isCancel = false,
            isReadyICPayment = false, isClickPay = false,
            isHaveToSendBack = false, isCash = false;
    protected String mTrack2 = "";
    protected EditText txtCard = null;
    protected TextView tvPercent = null;
    protected TextView tvStatus = null;
    protected Button btnReader = null;
    protected Button btnReadCard = null;
    protected Button btnNFC, btnKeyIN, btnMember, btnCoupon, btnCancel,
            btnConfirm;

    protected static String CardInputMethod = DaouDataContants.VAL_WCC_IC;
    protected String sPoint = "0";
    protected String sCouponID = "";
    protected boolean isKeyIn = false;
    protected boolean isGetCoupon = false;
    private EditText tvMonth, tvYear;
    ProgressDialog dialog;
    CustomDialog Cdialog;

    Dialog pDialog;
    AlertDialog fallbackDlg;
    com.devcrane.android.lib.entity.KBServerInfoEntity kbServerInfoEntity;
    com.devcrane.android.lib.entity.KBDUPTEntity kbduptEntity;

    private CustomAlertDialog CADialog;



    private ListView appListView;
    static boolean isUpdateKsn = false;
    protected String icAmount = "0";
    protected static FragmentActivity at;
    protected String payTypeSub = "";
    protected String bankCardData = "";
    protected String maskTrack2 = "";
    protected boolean isPlugined = false;
    protected boolean isAllowSwipeICC = false;
    protected boolean isStartAudioInResume = false;
    // signature
    public static final String TMP_SIGN = "tmp_sign.png";
    public static final String TMP_SIGN_SMT = "tmp_sign.bmp";
    protected LinearLayout mViewSign;
    protected SignatureView signView;
    protected String image;
    protected byte[] imageByte;


    public boolean isSwitchingActivity;

    //	EmvApplication app;
    protected EmvReader emvReader;
    protected ReceiptEntity receiptEntity;
    public static final int COUNT = 0;
    public static final int SHOW_PERCENT = 1;
    public static final int START_EMV = 2;
    public static final int WAIT_CARD_TIME = 7;
    private int Total = WAIT_CARD_TIME;//wait time card
    private final int TIMEOUT_START_EMV = 30;
    private Handler mHandler = new MessageHandler();
    boolean isInsertedCard = false;
    boolean isFinishedTrans = false;
    protected String encryptedKeyinCard = "";
    protected boolean isReceivedFromVan = false;

    void initEmvResources() {
        emvReader = MainActivity.getEmvReader();
    }

    void attachService() {
        if (emvReader != null) {
            BHelper.db("attachService");
            emvReader.attachEmvReaderListener(ReaderFragment.this);
        }
    }

    void detachService() {
        if (emvReader != null) {
            emvReader.detachEmvReaderListener(ReaderFragment.this);
        }
    }

    protected void initViewSignatureFragment() {
        mViewSign = (LinearLayout) getActivity().findViewById(R.id.viewSign);
        if (mViewSign != null){
            signView = new SignatureView(getActivity(), null);
            mViewSign.addView(signView);
        }

    }
    protected void setDisplaySignature(boolean isShow) {
        TextView mViewSignTitle = (TextView) at.findViewById(R.id.viewSignTitle);

        try {
            signView = new SignatureView(getActivity(), null);
            mViewSign.removeViewAt(0);
            mViewSign.addView(signView);

            if (!isShow) {
                // Set only target params:
                BHelper.db("signature is hide");
                if (mViewSign != null) {
                    mViewSign.setVisibility(View.INVISIBLE);
                }
                if(mViewSignTitle!=null)
                    mViewSignTitle.setVisibility(View.INVISIBLE);
                AppHelper.setNeedSignature(false);
            } else {
                BHelper.db("signature is showed");
                AppHelper.setNeedSignature(true);
                mViewSign.setVisibility(View.VISIBLE);
                mViewSignTitle.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void setDisplaySignature(String amount, boolean isFirstTime) {
        BHelper.db("check amount to show signature:" + amount);
        if (amount == null || amount.equals(""))
            amount = "0";
        Double val = Double.parseDouble(amount);
        TextView mViewSignTitle = (TextView) at.findViewById(R.id.viewSignTitle);

        try {
            signView = (SignatureView) mViewSign.getChildAt(0);
            signView.clear();
            if (val <= StaticData.SIGNATURE_AMOUNT_LIMIT) {
                // Set only target params:
                BHelper.db("signature is hide");
                if (mViewSign != null) {
                    mViewSign.setVisibility(View.INVISIBLE);
                }
                if(mViewSignTitle!=null)
                    mViewSignTitle.setVisibility(View.INVISIBLE);
                AppHelper.setNeedSignature(false);
            } else {
                BHelper.db("signature is showed");
                AppHelper.setNeedSignature(true);
                mViewSign.setVisibility(View.VISIBLE);
                mViewSignTitle.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    boolean checkSign() {
        if (isCash)
            return true;
        if (mViewSign == null)
            return true;
        AppHelper.resetBase64Signature();
        if (!AppHelper.getNeedSignature())
            return true;
        signView = (SignatureView) mViewSign.getChildAt(0);
        boolean isSigned = signView.isTouch;
        if (isSigned == false) {
            Toast.makeText(at, R.string.msg_requireSign, Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        mViewSign.setDrawingCacheEnabled(true);
        Bitmap source = signView.getBitmap();
        image = makeImage(source);
        imageByte = makeImageInByte(source);
//        BHelper.db("original signature: "+ HexDump.toHexString(imageByte));
//        BHelper.db("original signature size:"+ imageByte.length);

        imageByte = new DaouSignHelper().convertSignature(imageByte);


        Bitmap bmResize = Bitmap.createScaledBitmap(source, 300, 124, false);
        String bmpBase64 = SDcardHelper.BitmaptoBase64(bmResize);

        AppHelper.setBase64Signature(bmpBase64);
        Helper.saveBitmap(bmResize, TMP_SIGN);
        return isSigned;
    }


    byte[] makeImageInByte(Bitmap signView) {
        AndroidBmpUtil_1Bit bmpTool = new AndroidBmpUtil_1Bit();
        String bmpHex = bmpTool.bmpData_NoHeader(signView);
        byte[] rawData = HexDump.hexStringToByteArray(bmpHex);
//        SDcardHelper.createImage128_64_2(rawData,"svk_sign_5.bmp");
        return rawData;
    }

    String makeImage(Bitmap signView) {
        byte[] rawData = new byte[1024];
        rawData = SDcardHelper.getRawDataImage128_64(signView);
        return Base64Utils.base64Encode(rawData);
    }
    protected boolean isIgnoreCheckDeclined = false;
    protected boolean isWaitReturnTransactionResult = false;
    protected void resetToPayAgain(boolean isDecline) {
        isAllowSwipeICC = false;
        isIgnoreCheckDeclined = true;
        BHelper.db("resetToPayAgain and isIgnoreCheckDeclined:"+ isIgnoreCheckDeclined);
        if(ObPaymentHelper.ObStatus> ObPaymentHelper.PAYMENT_STEP_START_EMV){
            if(isDecline)
                emvReader.sendOnlineProcessResult(PaymentBase.makeEmvResponseDecline());
            else
                emvReader.sendOnlineProcessResult(null);
            isWaitReturnTransactionResult = true;
        } else{
            BHelper.db("Dont need sendOnlineProcessResult");
        }
        AppHelper.removeDataWithKey(EmvReader.EMV_DATA);
        AppHelper.resetEmvTcInfo();
        isClickPay = false;
        isReceivedFromVan = false;
        isHaveToSendBack = false;
        ObPaymentHelper.ObStatus = ObPaymentHelper.PAYMENT_STEP_PREPARE;
    }
    protected void resetToPayAgain() {
        resetToPayAgain(false);
    }

    protected void showStatus(String msg) {
        if (tvStatus != null)
            tvStatus.setText(msg);
    }

    protected void closeFallbackDlg() {
        if (fallbackDlg != null && fallbackDlg.isShowing()) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    fallbackDlg.dismiss();
                }

            }, 2000);
        }
    }
    protected void closeFallbackDlg2() {
        if (fallbackDlg != null && fallbackDlg.isShowing()) {
            fallbackDlg.dismiss();
        }
    }

    protected void showFallbackDlg(String msg) {
        if(msg==null || msg.equals(""))
            return;
        fallbackDlg = new AlertDialog.Builder(at)
                .setTitle(R.string.emv_fallback_report)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert).setMessage(msg)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();
    }
    protected void showFallbackDlg(int resID) {
        String msg = getString(resID);
        if(msg==null || msg.equals(""))
            return;
        fallbackDlg = new AlertDialog.Builder(at)
                .setTitle(R.string.emv_fallback_report)
                .setIcon(android.R.drawable.ic_dialog_alert).setMessage(msg)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();
    }


    protected void showAlertDialog(final BBDeviceController.CheckCardMode checkCardMode, int drawable_id)
    {
        CADialog = new CustomAlertDialog(at, drawable_id, new OnClickListener() {
            @Override
            public void onClick(View view) {

                BHelper.db("isDontCheck:"+isDontCheck);

                if(checkCardMode != null)
                {
                    if(isDontCheck){
                        showDialog();

                    }else{
                        checkCard(checkCardMode);
                    }

                }

                CADialog.dismiss();

            }
        });
        CADialog.show();
    }




    protected void showFallbackDlgWithConfirm(String msg,
                                              final BBDeviceController.CheckCardMode checkCardMode) {
        fallbackDlg = new AlertDialog.Builder(at)
                .setTitle(R.string.emv_fallback_report)
                .setIcon(android.R.drawable.ic_dialog_alert).setMessage(msg)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        BHelper.db("isDontCheck:"+isDontCheck);
//                        checkCard(checkCardMode);
                        if(isDontCheck){
                            showDialog();
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    checkCard(checkCardMode);
//                                }
//                            },2000);

                        }else{
                            checkCard(checkCardMode);
                        }
                    }
                }).show();
    }

    protected void showFallbackDlg(String title, String msg) {
        fallbackDlg = new AlertDialog.Builder(at).setTitle(title)
                .setIcon(android.R.drawable.ic_dialog_alert).setMessage(msg)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BHelper.db("onCreate on ReaderFragment");
        StaticData.isAtPaymentScreen = true;
        ObPaymentHelper.ObStatus = ObPaymentHelper.PAYMENT_STEP_PREPARE;
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        BHelper.db("onStart");
        at = getActivity();
        BHelper.setActivity(at);
        initViewSignatureFragment();
//        StaticData.isAtPaymentScreen = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        BHelper.db("onResume");
        setDisplaySignature("0",false);
        emvReader = MainActivity.getEmvReader();
        resetParas();
        EmvUtils.saveEmvData("");

        AppHelper.resetNeedSignature();
        AppHelper.resetVanMsg();
        bankCardData = "";
        isReadyICPayment = EmvUtils.getIsReadyIC();
        if (!AppHelper.getIsLogin() && !StaticData.KeyCodeBack) {
            MainActivity.setFragment(new HomeFragment());
        }
        if (isGetCoupon) {
            isGetCoupon = false;
            return;
        }
        if (StaticData.GET_NFC) {
            StaticData.GET_NFC = false;
            return;
        }

        if (isCancel) {
            doReset();
        }

        isPlugined = Helper.isHeadsetConnected(at);
        if (isPlugined || MainActivity.isBTReaderConnected) {
            isReadyICPayment = true;
            showCorrectDeviceStatus();
            isStartAudioInResume = true;
            if (emvReader != null) {
                emvReader.setIsForCancel(isCancel);
                emvReader.startReaderDevice();
                BHelper.db("emvReader.startReaderResume();");
            } else {
                BHelper.db("emvReader.is null");
                initEmvResources();
            }
            attachService();
            if (StaticData.getIsCalled()) {


//                showDialog();
//                emvReader.getDeviceInfo();
            }
        } else {
            initEmvResources();
            attachService();
            if (!CardInputMethod.equals(StaticData.CardInputMethodKeyIn))
                BHelper.showNeedDevice();
        }
        checkObStatus();
    }
    void checkObStatus(){
        BHelper.db("ObPaymentStatus:"+ ObPaymentHelper.ObStatus);
        switch (ObPaymentHelper.ObStatus){
            case ObPaymentHelper.PAYMENT_STEP_PREPARE:
            case ObPaymentHelper.PAYMENT_STEP_WAITING_CARD:
            case ObPaymentHelper.PAYMENT_STEP_START_EMV:
                resetToPayAgain();
                closeDialog();
                break;
            default:
                break;

        }
    }
    protected void resetParas() {
        maskTrack2 = "";
        isAllowSwipeICC = false;
        StaticData.bank_card_balance_amout = "";
        StaticData.sResultPayment = "";
        isReadyICPayment = false;
        isPlugined = false;
        isClickPay = false;
        isHaveToSendBack = false;
        isCash = false;
        Total = WAIT_CARD_TIME;
        encryptedKeyinCard = "";
    }

    protected void cleanPaymentData() {
        receiptEntity = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        detachService();

        MainActivity.isResumeOnMain = false;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        BHelper.db("onDestroy on ReaderFragment");
        closeDialog();
        StaticData.isAtPaymentScreen = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        BHelper.db("onStop on ReaderFragment");
//        closeDialog();
    }

    protected void checkEmvCard() {
        if (isCancel) {
            BHelper.db("checkEmvCard for cancel");
            isReadyICPayment = true;
            checkCard(BBDeviceController.CheckCardMode.SWIPE_OR_INSERT);
        }
    }

    protected void doTransactionResult(BBDeviceController.TransactionResult result) {

    }
    boolean isPassedWaitCard = false;
    public void checkCard(BBDeviceController.CheckCardMode checkCardMode) {
        checkCard(checkCardMode,R.string.msg_processing);
    }
    public void checkCard(BBDeviceController.CheckCardMode checkCardMode, int msgID) {
        String connectionMode =emvReader.emvSwipeController.getConnectionMode().toString();
        BHelper.db("connectionMode on payment screen:"+connectionMode);
        isPlugined = Helper.isHeadsetConnected(at);
        if((!EmvReader.getIsBlueTooth() && !isPlugined)||
                (EmvReader.getIsBlueTooth() && !MainActivity.isBTReaderConnected)){
            closeDialog();
            isClickPay = false;
//            showFallbackDlg(at.getString(R.string.msg_check_device_config));
            return;
        }


        if(isWaitReturnTransactionResult){
            showDialog(R.string.msg_wait_finish_pre_cmd);
            return;
        }

//        showDialog();
//        updateDialogMsg(msgID);
        showCDialog();
//        updateCDialog(R.drawable.progress_ing);

        BBDeviceController.CheckCardMode tmpCheckCardMode = checkCardMode;
        if(isCash)
            tmpCheckCardMode = BBDeviceController.CheckCardMode.SWIPE;
        isInsertedCard = false;
        Total = WAIT_CARD_TIME;
        BHelper.db("1.2 prepare data to check card");
        Hashtable<String, Object> data = new Hashtable<String, Object>();
        //
//		data.put("checkCardTimeout", "5000");//try change to 2000 to test
        if (isCancel) {
            BHelper.db("check card for cancel");
            data.put("checkCardTimeout", "5000");
        } else
            data.put("checkCardTimeout", "5000");
        data.put("checkCardMode", tmpCheckCardMode);
        String fid = emvReader.fids[6];
        if (fid.equals("FID46")) {
            data.put("randomNumber", "0123456789ABCDEF");
        } else if (fid.equals("FID61") && !EmvReader.getIsBlueTooth()) {
            data.put("orderID", "0123456789ABCDEF0123456789ABCDEF");
            data.put("randomNumber", "012345");
        } else if (fid.equals("FID65")) {
            // Note : The following encWorkingKey and workingKeyKcv should be
            // generated and given by the server.
            // Plain working key should never be transmitted through the mobile
            // application. Here is just an example to demonstrate how to
            // encrypt the working key can calculate the Kcv
            String encWorkingKey = EmvUtils.encrypt(
                    StaticData.FID65_WORKING_KEY_TEST,
                    StaticData.FID65_MASTER_KEY_TEST);
            String workingKeyKcv = EmvUtils.encrypt("0000000000000000",
                    StaticData.FID65_WORKING_KEY_TEST);
            data.put("encPinKey", encWorkingKey + workingKeyKcv);
            data.put("encDataKey", encWorkingKey + workingKeyKcv);
            data.put("encMacKey", encWorkingKey + workingKeyKcv);
            data.put("amount", "1.0");
        }
        BHelper.db("1.3 call emvReader  check card");
        emvReader.checkCard(data, fid);
//        int waitCancelCheckcardCounter = 8;
//        while (waitCancelCheckcardCounter>0){
//            waitCancelCheckcardCounter--;
//            BHelper.db("waitCancelCheckcardCounter:"+waitCancelCheckcardCounter);
//            if(!isDontCheck){
//                showDialog();
//                emvReader.checkCard(data, fid);
//                return;
//            }else{
//                if(!isPassedWaitCard && waitCancelCheckcardCounter==4){
//                    BHelper.db("try sendOnlineProcessResult before checkCard");
//                    emvReader.sendOnlineProcessResult(null);
//                }
//                try {
//                    BHelper.db("wait 1s for cancelCheckCard....");
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//            if(waitCancelCheckcardCounter<=0) {
//                isDontCheck = false;
//                emvReader.checkCard(data, fid);
//                return;
//            }
//
//        }

    }

    public void startEmvTrans(BBDeviceController.CheckCardMode checkCardMode, String fid) {
        ObPaymentHelper.ObStatus = ObPaymentHelper.PAYMENT_STEP_START_EMV;
        EmvReader.fid = fid;
        String terminalTime = new SimpleDateFormat("yyMMddHHmmss")
                .format(Calendar.getInstance().getTime());
        Hashtable<String, Object> data = new Hashtable<String, Object>();
        data.put("terminalTime", terminalTime);
        data.put("checkCardTimeout", "8000");
        data.put("setAmountTimeout", "8000");
        data.put("selectApplicationTimeout", "8000");
        data.put("finalConfirmTimeout", "8000");
        data.put("onlineProcessTimeout", "8000");
        data.put("pinEntryTimeout", "8000");
        data.put("emvOption", "START");
        data.put("checkCardMode", checkCardMode);
        data.put("encOnlineMessageTags", new String[]{"9F09"});
        data.put("encBatchDataTags", new String[]{"9F09"});
        data.put("encReversalDataTags", new String[]{"9F09"});


        //set Amount
        BBDeviceController.CurrencyCharacter[] currencyCharacter = new BBDeviceController.CurrencyCharacter[]
                { BBDeviceController.CurrencyCharacter.K, BBDeviceController.CurrencyCharacter.K, BBDeviceController.CurrencyCharacter.W };
        data.put("currencyCharacters", currencyCharacter);
        data.put("currencyCode", "410");
        data.put("amount",icAmount);
        data.put("cashbackAmount","0");
        data.put("transactionType",BBDeviceController.TransactionType.PAYMENT);


        BHelper.db("fid:" + EmvReader.fid);
        if (EmvReader.fid.equals("FID46")) {
            data.put("randomNumber", "0123456789ABCDEF");
        } else if (EmvReader.fid.equals("FID61") && !EmvReader.getIsBlueTooth()) {
            data.put("orderID", "0123456789ABCDEF0123456789ABCDEF");
            data.put("randomNumber", "012345");
        } else if (EmvReader.fid.equals("FID65")) {
            // Note : The following encWorkingKey and workingKeyKcv should be
            // generated and given by the server.
            // Plain working key should never be transmitted through the mobile
            // application. Here is just an example to demonstrate how to
            // encrypt the working key can calculate the Kcv
            String encWorkingKey = EmvUtils.encrypt(
                    StaticData.FID65_WORKING_KEY_TEST,
                    StaticData.FID65_MASTER_KEY_TEST);
            String workingKeyKcv = EmvUtils.encrypt("0000000000000000",
                    StaticData.FID65_WORKING_KEY_TEST);

            data.put("encPinKey", encWorkingKey + workingKeyKcv);
            data.put("encDataKey", encWorkingKey + workingKeyKcv);
            data.put("encMacKey", encWorkingKey + workingKeyKcv);
        }
        BHelper.db("start call emvSwipeController.startEmv");
        emvReader.startEmv(data);
        BHelper.db("wait for onRequestOnlineProcess");
        checkCardMode = null;
        fid = "";
    }

    protected void initCard() {
        at = getActivity();
        txtCard = (EditText) getView().findViewById(R.id.txtCard);
        btnNFC = (Button) at.findViewById(R.id.btnNFC);
        btnKeyIN = (Button) at.findViewById(R.id.btnKeyin);
        btnMember = (Button) at.findViewById(R.id.btnMember);
        btnCoupon = (Button) at.findViewById(R.id.btnCoupon);
        btnCancel = (Button) at.findViewById(R.id.btnCancel);
        btnConfirm = (Button) at.findViewById(R.id.btnConfirm);
        tvPercent = (TextView) at.findViewById(R.id.tvPercent);
        tvStatus = (TextView) at.findViewById(R.id.tvPaymentStatus);

        Button[] buttons = {btnNFC, btnKeyIN, btnCoupon, btnMember, btnCancel,
                btnConfirm, btnReader};
        if (btnReader != null)
            btnReader.setSelected(true);
        for (Button button : buttons) {
            if (button != null)
                button.setOnClickListener(onclick);
        }
    }

    protected String getKeyinCardNo() {
        return "";
    }

    protected String getCardNo() {
        String result = bankCardData;
        if (bankCardData.equals("")) {
            result = txtCard.getText().toString();
            result = result.replace("-","");
            CardInputMethod = DaouDataContants.VAL_WCC_KEYIN;
            if (!encryptedKeyinCard.equals(""))
                result = encryptedKeyinCard;
        }

        return result;
    }

    protected void resetCardNo() {
        mTrack2 = "";
    }

    protected OnClickListener onclick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnConfirm:
                    if (!checkSign())
                        return;
                    if (!isCash) {
                        if (!isReadyICPayment) {
                            BHelper.showToast(getString(R.string.device_not_ready));
                            return;
                        }
                    }
                    isPlugined = Helper.isHeadsetConnected(at);
                    if (!MainActivity.isBTReaderConnected && !isPlugined) {
                        BHelper.showToast(getString(R.string.device_not_ready));
                        return;
                    }

                    if (!validateCredit())
                        return;
                    // prevent click twice
                    if (isClickPay) {
                        BHelper.db("Already click pay. This action will be prevented");
                        return;
                    }
                    if(isCash)
                        showCDialog(R.drawable.progress_ing);
//                        showDialog(R.string.msg_pay_step_1_cash);
                    else
                        showCDialog(R.drawable.progress_please_read_ic);
//                        showDialog(R.string.msg_pay_step_1_reading_card);

                    isClickPay = true;

                    if (isCash) {


                        String cardNo = getKeyinCardNo();
                        BHelper.db("key-in card no:" + cardNo);
                        if (CardInputMethod == DaouDataContants.VAL_WCC_KEYIN) {
                        /*
						 *  1. encrypt card
						 *  2. call doConfirmPayment
						 */
                            cardNo = cardNo.replace("-", "").trim();
                            BHelper.db("key-in card no 2:" + cardNo);
//						cardNo+="=";
                            cardNo = HexDump.toHexString(cardNo.getBytes());
//
                            BHelper.db("key-in card no 3:" + cardNo);
                            emvReader.encryptDataWithSettings(cardNo);
                        } else {
                            doConfirmPayment();
                        }
                        return;
                    }

                    if (isCancel) {
                        //
                        if (payTypeSub
                                .equals(StaticData.CREDIT_SUBTYPE_ICC_SWIPE)
//							|| payTypeSub
//									.equals(StaticData.CREDIT_SUBTYPE_BANK_CARD)
                                || payTypeSub
                                .equals(StaticData.CREDIT_SUBTYPE_GIFT)) {
                            doConfirmPayment();
                        } else
                            startEmvTrans(emvReader.getCheckCardMode(),
                                    EmvReader.fid);
                    } else {
                        BHelper.db("1.1 Start check Card");
                        checkCard(BBDeviceController.CheckCardMode.SWIPE_OR_INSERT);
                    }
                    break;
                case R.id.btnCancel:
                    ResPara.returnFail(at);
                    MainActivity.setFragment(new HomeFragment());
                    break;
                case R.id.btnCoupon:
                    BHelper.showToast(R.string.msg_unavailable_function);
                    break;
                case R.id.btnMember:
                    BHelper.showToast(R.string.msg_unavailable_function);
                    break;
                case R.id.btnKeyin:
                    if (StaticData.IS_MAGNETIC_READER)
                        CardInputMethod = StaticData.CardInputMethodKeyIn;
                    StaticData.IS_NFC = false;
                    setEnableExpridate(true);
                    isKeyIn = true;
                    txtCard.setText("");
                    txtCard.setHint(R.string.hint_txtcard_keyin);
                    mTrack2 = "";
                    isReadCard = false;
                    setPress(v.getId());
                    doCard();
                    if (tvYear != null) {
                        tvYear.setText("78XX");
                    }
                    break;
                case R.id.btnNFC:
                    CardInputMethod = StaticData.CardInputMethodNFC;
                    setEnableExpridate(false);
                    txtCard.setHint(R.string.hint_txtcard_checkpass);
                    StaticData.IS_NFC = false;
                    isKeyIn = false;
                    isReadCard = true;
                    setPress(v.getId());
                    break;
                case R.id.btnReadCard:
                    if(MainActivity.isBTReaderConnected || isPlugined){
                        BHelper.db("read card");
                        isReadyICPayment = true;
                        bankCardData = "";
                        checkCard(BBDeviceController.CheckCardMode.SWIPE);
                    }else
                        BHelper.showToast(R.string.msg_have_to_connect_adio_or_turn_on_bluetooth);

                    break;
                default:
                    break;
            }

        }
    };

    private void setEnableExpridate(boolean enable) {
        if (tvMonth != null)
            tvMonth.setEnabled(enable);
        if (tvYear != null)
            tvYear.setEnabled(enable);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BHelper.db("onActivityResult.requestCode:" + requestCode);
        if (isGetCoupon) {
            MainActivity main = (MainActivity) at;
            main.initMenuLeft();
            ;
        }
        if (requestCode == StaticData.REQUEST_BARCODE) {
            if (resultCode == Activity.RESULT_OK) {
                String sResult = data
                        .getStringExtra(StaticData.RESULT_BARCODE);
                sCouponID = data.getStringExtra(StaticData.COUPON_ID);
                Scanner scanner = new Scanner(sResult);
                if (scanner.hasNextInt()) {
                    sPoint = sResult;
                } else {
                    sPoint = "0";
                }

            } else {
                sPoint = "0";
                try {

                    String sAction = data
                            .getStringExtra(StaticData.RESULT_ACTION);
                    BHelper.db("Action:" + sAction);
                    if (sAction.equals(StaticData.RightProfile)) {
                        MainActivity.setFragment(new ProfileFragment());
                    } else if (sAction.equals(StaticData.RightHome)) {
                        MainActivity.setFragment(new HomeFragment());
                    } else if (sAction.equals(StaticData.RightCredit)) {
                        MainActivity.setFragment(new PaymentsCreditFragment());
                    } else if (sAction.equals(StaticData.RightCash)) {
                        MainActivity.setFragment(new PaymentsCashFragment());
                    } else if (sAction.equals(StaticData.RightCancelList)) {
                        MainActivity.setFragment(new CancelDailyListFragment());
                    } else if (sAction.equals(StaticData.RightCoupon)) {
                        MainActivity.setFragment(new CouponFragment());
                    } else if (sAction.equals(StaticData.RightExtended)) {
                        MainActivity.setFragment(new ExtendedFragment());
                    } else if (sAction.equals(StaticData.MainHome)) {
                        MainActivity.setFragment(new HomeFragment());
                    } else if (sAction.equals(StaticData.MainCancel)) {
                        MainActivity.setFragment(new CancelPaymentFragment());
                    } else if (sAction.equals(StaticData.MainCancelList)) {
                        MainActivity.setFragment(new CancelDailyListFragment());
                    } else if (sAction.equals(StaticData.MainProfile)) {
                        MainActivity.setFragment(new ProfileFragment());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            sCouponID = "";
            sPoint = "0";
        }
        tvPercent.setText(sPoint + "%");
    }

    private void setPress(int id) {
        Button[] btns = {btnNFC, btnKeyIN, btnReader};
        for (Button button : btns) {
            if (button.getId() == id)
                button.setSelected(true);
            else
                button.setSelected(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isSwitchingActivity) {
            isSwitchingActivity = false;
        } else {

        }
    }

    protected void doConfirmPayment() {

    }

    protected boolean validateCredit() {
        return true;
    }

    protected void doCard() {

    }

    protected void doReset() {

    }

    public void dismissPDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    protected void showCDialog(int drawable) {
        if (Cdialog == null || !Cdialog.isShowing()) {
            Cdialog = new CustomDialog(getContext(), drawable);
            Cdialog.setCancelable(false);
            Cdialog.show();
        }
    }


    protected void showCDialog() {
        if (Cdialog == null || !Cdialog.isShowing()) {
            Cdialog = new CustomDialog(getContext(), R.drawable.progress_please_read_ic);
            Cdialog.setCancelable(false);
            Cdialog.show();
        }
    }


    protected void updateCDialog(int drawable) {
        if (Cdialog != null && Cdialog.isShowing()) {
            Cdialog.changeDrawable(drawable);
        }
    }



    protected void showDialog() {
        showStatus("");
        if (dialog == null || !dialog.isShowing()) {
            dialog = DialogHelper.makeDialog(R.string.msg_processing);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    protected void showDialog(int rsid) {
        showStatus("");
        if (dialog == null || !dialog.isShowing()) {
            dialog = DialogHelper.makeDialog(rsid);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    protected void showDialogProgress(int rsid) {
        showStatus("");
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

    protected void updateDialogMsg(String msg) {
        showStatus("");
        if (dialog != null && dialog.isShowing()) {
            dialog.setMessage(msg);
        }
    }
    protected void updateDialogMsg(int msgID) {
        showStatus("");
        if (dialog != null && dialog.isShowing()) {
            dialog.setMessage(at.getString(msgID));
        }
    }

    protected void resetDialogMsg() {
        if (dialog != null && dialog.isShowing()) {
            dialog.setProgressStyle(R.style.ProgressDialogCustom);
            dialog.setMessage(at.getString(R.string.msg_processing));
        }
    }
    public static int closeRequestCount = 0;
    protected void closeDialog() {
        closeRequestCount++;
        BHelper.db("closeDialog:"+closeRequestCount);
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        if (Cdialog != null) {
            Cdialog.dismiss();
            Cdialog = null;
        }

    }
    public static int closeRequestDelayCount = 0;
    protected void closeDialogDelay(final int time) {
        closeRequestDelayCount++;
        BHelper.db("request close dialog:"+ time);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                BHelper.db("closeDialogDelay:"+ time);
                BHelper.db("closeRequestCount:"+ closeRequestDelayCount);
                if(closeRequestDelayCount==1){
                    isClickPay = false;
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }


                    if (Cdialog != null) {
                        Cdialog.dismiss();
                        Cdialog = null;
                    }

                }
                closeRequestDelayCount--;

            }
        }, time);
    }

    private void showToast(String message) {
        Toast.makeText(at, message, Toast.LENGTH_SHORT).show();
    }

    // for EmvReaderInterface

    @Override
    public void onReturnEmvCardDataResult(boolean isSuccess, String cardData) {
        // TODO Auto-generated method stub
        BHelper.db("onReturnEmvCardDataResult: (isSuccess:"+isSuccess+ ", cardData:" + cardData+ ")");
        if(isSuccess){
            bankCardData = EmvUtils.extractMaskTrack2(cardData);
            if (isCancel) {
                callDoCard();
            }
        }else{
            BHelper.showToast(R.string.msg_check_emv_card_is_fail);
            closeDialog();
        }


        cardData = "";
    }

    void callDoCard() {
        try {
            Thread.sleep(2000);
            doCard();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onReturnEmvCardNumber(String cardNumber) {
        // TODO Auto-generated method stub
        BHelper.db("card Num:" + cardNumber);
        cardNumber = "";
    }

    @Override
    public void updateEmvBatteryLowUI() {
        showToast(at.getString(R.string.battery_low));
    }

    @Override
    public void updateEmvBatteryCriticallyLowUI() {
        dialog = ProgressDialog.show(at, null,
                at.getString(R.string.battery_critically_low), true);
        dialog.setCancelable(true);
    }

    @Override
    public void updateEmvCardBalanceUI(String balance) {
        // TODO Auto-generated method stub
        BHelper.db("balance:" + balance);
    }

    @Override
    public void updateEmvPinUI(String pinData) {
        // TODO Auto-generated method stub
        showToast(pinData);
    }

    @Override
    public void updateEmvKsnUI(String ksnData) {
        // TODO Auto-generated method stub
        showToast(ksnData);
    }

    @Override
    public void updateEmvDeviceInfoUI(String deviceInfo) {
        showToast(deviceInfo);
    }

    @Override
    public void requestPinEntry() {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyExchangeCallback(KeyExchangeResultEntity exchangeResultEntity) {
        BHelper.db("exchangeResultEntity:" + exchangeResultEntity.toString());
        BHelper.db("Step 1.3 call JTNet.requestKBDUPTKey");
        BHelper.db("Step 2.1 Wait JTNet response message");
        String ksn = exchangeResultEntity.getKsn();
        isUpdateKsn = true;
        BHelper.db(kbduptEntity.toString());
        BHelper.db("Update Ksn from device to injectMaterKey");
        // BHelper.db("save KsnEntity to Reader to use to injectMasterKey");
        isUpdateKsn = false;
        BHelper.db("Step 2.2 call injectMasterKey");
        emvReader.injectMasterKey(kbduptEntity.getEncryptedData(), ksn);
        BHelper.db("Step 2.3 Wait for callback onReturnInjectMasterKeyResult");
    }

    @Override
    public void injectMasterKeyCallback(String resultMsg) {
        // TODO Auto-generated method stub
        BHelper.db("injectMasterKeyCallback:" + resultMsg);
        BHelper.db("1.1 Start check Card");
        kbduptEntity = null;
        kbServerInfoEntity = null;
        checkCard(BBDeviceController.CheckCardMode.SWIPE_OR_INSERT);
    }

    @Override
    public void ksnCallback(KSNEntity ksnReturnEntity) {
        BHelper.db("dont request keyBinding....");
        checkCard(BBDeviceController.CheckCardMode.SWIPE_OR_INSERT);
    }

    @Override
    public void requestTerminalTime() {
        String terminalTime = new SimpleDateFormat("yyMMddHHmmss")
                .format(Calendar.getInstance().getTime());
        BHelper.db("send Terminal Time: " + terminalTime);
        emvReader.sendTerminalTime(terminalTime);

    }

    @Override
    public void onRequestSelectApplication(ArrayList<String> appList) {
        // TODO Auto-generated method stub
        dismissPDialog();
        BHelper.db("onRequestSelectApplication");
        pDialog = new Dialog(at);
        pDialog.setContentView(R.layout.application_dialog);
        pDialog.setTitle(R.string.please_select_app);
        String appListStr = "";
        String[] appNameList = new String[appList.size()];
        for (int i = 0; i < appNameList.length; ++i) {
            appNameList[i] = appList.get(i);
            appListStr += ";   " + appList.get(i);
        }
        BHelper.db("app list: " + appListStr);
        appListView = (ListView) pDialog.findViewById(R.id.appList);
        appListView.setAdapter(new ArrayAdapter<String>(at,
                android.R.layout.simple_list_item_1, appNameList));
        appListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                emvReader.selectApplication(position);
                BHelper.db("selected app code:" + position);
                dismissPDialog();
            }

        });

        pDialog.findViewById(R.id.cancelButton).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        emvReader.cancelSelectApplication();
                        dismissPDialog();
                    }
                });
        pDialog.show();
    }

    @Override
    public void onRequestFinalConfirm() {
        // TODO Auto-generated method stub
        emvReader.sendFinalConfirmResult(true);
        BHelper.db("sendFinalConfirmResult");
    }

    @Override
    public void onRequestSetAmount() {
        // TODO Auto-generated method stub

        boolean ret = emvReader.setAmount(icAmount, "0", "410",
                BBDeviceController.TransactionType.PAYMENT);
        BHelper.db("onRequestSetAmount:" + icAmount + "with result:" + ret);
    }

    @Override
    public void onRequestOnlineProcess(String tlv) {
        // TODO Auto-generated method stub
        isFinishedTrans = true;
        ObPaymentHelper.ObStatus = ObPaymentHelper.PAYMENT_STEP_SEND_VAN_REQUEST;
        EmvUtils.saveEmvData(tlv);
        tlv = null;
//        EmvUtils.showEmvDataManual();
        BHelper.db("onRequestOnlineProcess");
        doConfirmPayment();
    }

//    @Override
//    public void onReturnApduResult(boolean isSuccess, String apdu,
//                                   int apduLength) {
//        // TODO Auto-generated method stub
//    }

//    @Override
//    public void onReturnViposExchangeApduResult(String apdu) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onReturnViposBatchExchangeApduResult(
//            Hashtable<Integer, String> data) {
//        // TODO Auto-generated method stub
//
//    }
    void cancelInComplete(String reasonInComplete){
        BHelper.db("isReceivedFromVan:"+isReceivedFromVan);
        if(!isReceivedFromVan)
            return;
        InCompleteDataEntity dataEntity = AppHelper.getInCompleteData();
        DaouData daouData = new InCompleteTrans(dataEntity.makeIncompleteData(), reasonInComplete);
        String[] result = daouData.req(new TerminalInfo());
        AppHelper.setVanMsg(result[21]);
        showFallbackDlg(AppHelper.getVanMsg());
        isReceivedFromVan = false;

    }
    @Override
    public void onReturnTransactionResult(BBDeviceController.TransactionResult transResult) {

        BHelper.db("onReturnTransactionResult:" + transResult);
        BHelper.db("isWaitReturnTransactionResult:" + isWaitReturnTransactionResult);
        if(isWaitReturnTransactionResult){
            isWaitReturnTransactionResult = false;
            isClickPay = false;
            cleanPaymentData();
            closeDialog();
            return;
        }

        switch (transResult) {
            case NOT_ICC:
            case CARD_BLOCKED:
                closeDialog();
                String resultMsg = at.getString(R.string.transaction_card_blocked) + " "
                        + transResult.toString();
                //showFallbackDlg(resultMsg);
                isAllowSwipeICC = true;
                showFallbackDlgWithConfirm(resultMsg, BBDeviceController.CheckCardMode.SWIPE_OR_INSERT);
                //checkCard(BBDeviceController.CheckCardMode.SWIPE_OR_INSERT);
                break;
            case APPROVED:
                doTransactionResult(transResult);
                closeDialogDelay(1000);
                break;
            case TERMINATED:
                closeDialog();
                String resultMsgT = at.getString(R.string.transaction_result) + " "
                        + transResult.toString();
                isAllowSwipeICC = true;
                showFallbackDlgWithConfirm(resultMsgT, BBDeviceController.CheckCardMode.SWIPE);
                break;
            case ICC_CARD_REMOVED:
                try {
                    cancelInComplete(DaouDataContants.VAL_INCOMPLETE_REASON_WRITE_BACK_IC_CARD_REMOVED);
//                    VanHelper.setVanCancel(receiptEntity, "");
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                closeDialog();

                showAlertDialog(null, R.drawable.progress_remove);
//                showFallbackDlg(R.string.icc_card_removed);
                isClickPay = false;
                cleanPaymentData();
                break;
            case DECLINED:
                if(!isIgnoreCheckDeclined) {
                    cancelInComplete(DaouDataContants.VAL_INCOMPLETE_REASON_WRITE_BACK_DECLINE);
//                    VanHelper.setVanCancel(receiptEntity, "");
                }
//                doTransactionResult(transResult);

                // case DELICED do cancel receipt

                //			JTNet.cancelCardFallback(receiptEntity, image,JTNet.FALLBACK_DECLINE);

                closeDialogDelay(5000);
                //dont show DECLINE msg
//                showFallbackDlg(transResult.toString());
//                isClickPay = false;
                cleanPaymentData();
                break;
            default:
                closeDialog();
                isClickPay = false;
                cleanPaymentData();
                break;
        }
        transResult = null;
    }

    void showCheckCardResult(Hashtable<String, String> decodeData) {
        if (decodeData != null) {
            BHelper.db("showCheckCardResult");
            try {
                BHelper.showHashTable(decodeData);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        decodeData = null;
    }

    @Override
    public void onReturnCheckCardResult(BBDeviceController.CheckCardResult checkCardResult,
                                        Hashtable<String, String> decodeData) {
        closeDialog();

        Total = WAIT_CARD_TIME;
        isInsertedCard = true;
        String resultMsg = "";
        BHelper.db("onReturnCheckCardResult");
        BHelper.db("checkCardResult:" + checkCardResult);
        switch (checkCardResult) {

            case NOT_ICC:
                isAllowSwipeICC = true;
                resultMsg = at.getString(R.string.msg_check_card_result_is_not_icc);
                closeDialog();
                BBDeviceController.CheckCardMode checkCardMode;
                if (isCancel) {
                    checkCardMode = BBDeviceController.CheckCardMode.SWIPE_OR_INSERT;
                } else
                    checkCardMode = BBDeviceController.CheckCardMode.SWIPE;


                showAlertDialog(checkCardMode, R.drawable.progress_magnetic);
//                showFallbackDlgWithConfirm(resultMsg, checkCardMode);
                BHelper.db("run check card again for cancel in NOT_ICC");
                break;
            case NO_CARD:
                resultMsg = at.getString(R.string.msg_check_card_result_is_none);
                closeDialog();
                showFallbackDlg(resultMsg);
                break;
            case INSERTED_CARD:
                showCDialog(R.drawable.progress_ic_reading);
//                showDialog(R.string.msg_pay_step_1_reading_card);
                CardInputMethod = DaouDataContants.VAL_WCC_IC;
                payTypeSub = StaticData.CREDIT_SUBTYPE_ICC;
                BHelper.db("Step 2.1 startEmv");
                showCheckCardResult(decodeData);
                if (isCancel) {
                    BHelper.db("cancel in step ==> onReturnCheckCardResult");
                    BHelper.db("ICC: getEmvCardData: waiting for onReturnEmvCardDataResult");
                    emvReader.getEmvCardData();
                } else {
                    startEmvTrans(emvReader.getCheckCardMode(), EmvReader.fid);
                }
                break;
            case BAD_SWIPE:
                resultMsg = at
                        .getString(R.string.msg_check_card_result_is_bad_swipe);
                closeDialog();

                showAlertDialog(BBDeviceController.CheckCardMode.SWIPE, R.drawable.progress_retry);
//                showFallbackDlgWithConfirm(resultMsg, BBDeviceController.CheckCardMode.SWIPE);

//                showFallbackDlg(resultMsg);
//                closeFallbackDlg();
                // run check card again
                // if(isCancel){
                BHelper.db("run check card again for case  BAD_SWIPE");
//                checkCard(BBDeviceController.CheckCardMode.SWIPE);
                // }
                break;
            case MSR:
            case USE_ICC_CARD:
                showCDialog(R.drawable.progress_please_read_ic);
//                showDialog(R.string.msg_pay_step_1_reading_card);
                CardInputMethod = DaouDataContants.VAL_WCC_SWIPE;
                String serviceCode = decodeData.get("serviceCode");
                BHelper.db("serviceCode:" + serviceCode);

                payTypeSub = StaticData.CREDIT_SUBTYPE_GIFT;
                bankCardData = decodeData.get("data");
                if(bankCardData==null || bankCardData.equals("")){
                    closeDialog();
                    resultMsg = at.getString(R.string.msg_check_card_return_wrong_data);
                    showFallbackDlgWithConfirm(resultMsg, BBDeviceController.CheckCardMode.SWIPE);
                    return;
                }
                BHelper.db("encData:" + bankCardData);
                String maskedPAN = decodeData.get("maskedPAN");
                maskTrack2 = EmvUtils.formatMaskedTrack2(maskedPAN);
//                BHelper.showHashTable(decodeData);
                //cash payment
                if (isCash) {
                    bankCardData = new String(HexDump.hexStringToByteArray(bankCardData));
                    BHelper.db("card result for cash payment:");
                    CardInputMethod = StaticData.CardInputMethodMS;
                    txtCard.setText(maskTrack2);
                    closeDialog();
                    return;
                }

                //allow Swipe if serviceCode is 101
                if (serviceCode.startsWith("101"))
                    isAllowSwipeICC = true;

                if ((serviceCode.startsWith("2") || serviceCode.startsWith("6"))
                        && !isAllowSwipeICC && !isCancel) {
                    resultMsg = at.getString(R.string.msg_plz_insert_not_swipe);

                    showAlertDialog(BBDeviceController.CheckCardMode.SWIPE_OR_INSERT, R.drawable.progress_request);
//                    showFallbackDlgWithConfirm(resultMsg, BBDeviceController.CheckCardMode.SWIPE_OR_INSERT);
                    break;
                } else if ((serviceCode.startsWith("2") || serviceCode
                        .startsWith("6") || serviceCode.startsWith("101")) && isAllowSwipeICC) {
                    payTypeSub = StaticData.CREDIT_SUBTYPE_ICC_SWIPE;

                    //dont clean EMV for Fallback
//                    EmvUtils.saveEmvData("");
                } else if (CardHelper.isGiftCard(maskedPAN)) {
                    payTypeSub = StaticData.CREDIT_SUBTYPE_GIFT;
                    EmvUtils.saveEmvData("");
                }
                if(serviceCode==null || serviceCode.equals("") || serviceCode.equals("333")){
                    BHelper.db("can not get service code");
                    resultMsg = at.getString(R.string.msg_plz_insert_not_swipe);
                    showAlertDialog(BBDeviceController.CheckCardMode.SWIPE_OR_INSERT, R.drawable.progress_request);
//                    showFallbackDlgWithConfirm(resultMsg, BBDeviceController.CheckCardMode.SWIPE_OR_INSERT);
                    break;
                }
                BHelper.db("payTypeSub:" + payTypeSub);
                if (isCancel) {
                    if ((serviceCode.startsWith("2") || serviceCode.startsWith("6") || serviceCode.startsWith("101"))) {
                        payTypeSub = StaticData.CREDIT_SUBTYPE_ICC_SWIPE;
                        BHelper.db("payTypeSub:" + payTypeSub);
                        EmvUtils.saveEmvData("");
                    }
                    doCard();
                } else {
                    doConfirmPayment();
                }
                break;

            default:
                break;
        }
        checkCardResult =null;
        decodeData = null;
    }

    @Override
    public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
        BHelper.db("onReturnDeviceInfo on ReaderFragment");
        isReadyICPayment = true;

//        BHelper.showHashTable(deviceInfoData);
        // check to show Device name
        String modelName = deviceInfoData.get("modelName") == null ? ""
                : deviceInfoData.get("modelName");

        if (isPlugined) {
            showStatus(modelName
                    + at.getString(R.string.msg_correct_device));
        }
        modelName  = "";
        deviceInfoData = null;
        closeDialog();
    }

    void showCorrectDeviceStatus() {
        String pinKsn = EmvUtils.getKsn();
        String modelName = EmvUtils.getHWModelName();

        if (pinKsn != null && pinKsn.length() > 6
                && pinKsn.substring(2, 6).equals("5046"))
            showStatus(modelName
                    + at.getString(R.string.msg_correct_device));
    }

    @Override
    public void onReturnBatchData(String tlv) {
        BHelper.db("onReturnBatchData:" + tlv);
    }

    @Override
    public void onWaitingForCard(BBDeviceController.CheckCardMode checkCardMode) {
        ObPaymentHelper.ObStatus = ObPaymentHelper.PAYMENT_STEP_WAITING_CARD;
        // TODO Auto-generated method stub
        if(isDontCheck){
            isPassedWaitCard = true;
            BHelper.db("dont Wait card any more. user have to click to pay again");
//            isDontCheck = false;
//            isClickPay = false;
            emvReader.cancelCheckCard();
//            closeDialog();
            return;
        }
        String msg = "";
        //Jonathan 171210 수정
//        showDialog();
        showCDialog();
        mHandler.sendEmptyMessage(COUNT);
        BHelper.db("send noti to wait 10s");
        if (checkCardMode == BBDeviceController.CheckCardMode.INSERT)
            updateCDialog(R.drawable.progress_please_read_ic);
//            msg = at.getString(R.string.msg_please_insert_card);
        else if (checkCardMode == BBDeviceController.CheckCardMode.SWIPE)
            updateCDialog(R.drawable.progress_magnetic);
//            msg = at.getString(R.string.msg_please_swipe_card);
        else
            msg = at.getString(R.string.msg_please_swipe_or_insert_card);
//        updateDialogMsg(msg);

        msg = "";
        checkCardMode = null;
    }

    @Override
    public void onDeviceHere(boolean isHere) {
        // TODO Auto-generated method stub
        BHelper.db("onDeviceHere:" + isHere);
        isPlugined = Helper.isHeadsetConnected(at);
        if (isHere) {
            emvReader.getDeviceInfo();
        } else if (Helper.isHeadsetConnected(at)) {
            emvReader.restartAudio();
            closeDialog();
            BHelper.showToast(R.string.msg_reconnect_device);
        } else {
            closeDialog();
            BHelper.showToast(R.string.msg_reconnect_device);
        }
        isHere = false;
    }

    @Override
    public void onDeviceUnplugged() {
        BHelper.restoreVolumn(at);
        String connectionMode =emvReader.emvSwipeController.getConnectionMode().toString();
        BHelper.db("connectionMode on payment screen:"+connectionMode);
        if(!connectionMode.equals(BBDeviceController.ConnectionMode.AUDIO.toString()))
            return;
        BHelper.db("onDeviceUnplugged");
        isPlugined = false;
        closeDialog();
        showStatus("");
        showFallbackDlg(at.getString(R.string.device_unplugged));
        closeFallbackDlg();
        isStartAudioInResume = false;
        isReadyICPayment = false;
        Total = 0;
        EmvUtils.cleanDeviceValue();

    }

    @Override
    public void onDevicePlugged() {
        BHelper.db("onDevicePlugged");
        String connectionMode = emvReader.getConnectionMode().toString();
        BHelper.db("connectionMode on payment screen:"+connectionMode);
        if(!connectionMode.equals(BBDeviceController.ConnectionMode.AUDIO.toString()))
            return;
        showStatus(at.getString(R.string.device_plugged));
        showDialog();


        isPlugined = true;
        EmvReader.isManualKeyBinding = false;
        if (isCancel && !isCash) {
            checkEmvCard();
        } else {
            if (!isStartAudioInResume) {
                BHelper.db("onDevicePlugged with is Cancel:" + isCancel);
                emvReader.setIsForCancel(isCancel);
                emvReader.isDeviceHere();
            } else {
                emvReader.isDeviceHere();
            }
        }
        isStartAudioInResume = false;
    }

    @Override
    public void onReturnIntegrityCheckResult(boolean result) {
        // TODO Auto-generated method stub
        result = false;

    }

    @Override
    public void onNoDeviceDetected() {
        // TODO Auto-generated method stub
        closeDialog();
        showFallbackDlg(at.getString(R.string.no_device_detected));
        closeFallbackDlg();
    }

    @Override
    public void onError(BBDeviceController.Error errorState) {
        BHelper.db("onError on ReaderFragment"+ errorState.toString());
        isClickPay = false;
        closeDialog();
        if(MainActivity.isWaitTurnOnBT && !MainActivity.isBTReaderConnected)
            return;
        // TODO Auto-generated method stub
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (tvStatus != null)
            tvStatus.setText(EmvUtils.getEmvErrorString(at, errorState));
        switch (errorState){
            case COMM_ERROR:
            case COMM_LINK_UNINITIALIZED:
                showAutoConfigConfirm(getString(R.string.msg_config_device_ask));
                break;
            case DEVICE_BUSY:
                emvReader.sendOnlineProcessResult(null);
                break;
            case TIMEOUT:
                BHelper.showToast(R.string.device_reset);
                break;
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

                        BHelper.db("Start auto config and show dialog");
                        emvReader.startAutoConfig();
                    }
                }).show();
    }

    @Override
    public void onAutoConfigCompleted(boolean isDefaultSettings,
                                      String autoConfigSettings) {
        // TODO Auto-generated method stub
//        emvReader.setAutoConfig();
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
        BHelper.db("config percent:" + (int) percentage);
        if (dialog != null && dialog.isShowing()) {
            if (dialog != null && dialog.isShowing())
                updateDialogMsg(getString(R.string.msg_config_device_doing) + " " + (int) percentage + " %");
        }
    }

    @Override
    public void onReturnEncryptDataResult(boolean result, Hashtable<String, String> encrytedData) {
        if (result) {
            Set<String> keys = encrytedData.keySet();
            for (String key : keys) {
                BHelper.db("tag:" + key + " value: " + encrytedData.get(key) + " len:" + encrytedData.get(key).length());
            }

            if (encrytedData.containsKey("encData")) {
                encryptedKeyinCard = encrytedData.get("encData");
                BHelper.db("Track2 before base64:" + encryptedKeyinCard);
                encryptedKeyinCard = Base64Utils.base64Encode(HexDump.hexStringToByteArray(encryptedKeyinCard));
                doConfirmPayment();
            }
        } else if (encrytedData.containsKey("errorMessage")) {
            isClickPay = false;
            closeDialog();
            showFallbackDlg(encrytedData.get("errorMessage"));
        }
        result =false;
        encrytedData = null;

    }
    boolean isDontCheck = false;
    @Override
    public void onRequestDisplayText(BBDeviceController.DisplayText displayText) {
        switch (displayText){
            case INSERT_SWIPE_OR_TRY_ANOTHER_CARD:
//                closeDialog();
                isDontCheck = true;
                isAllowSwipeICC = true;
//                emvReader.setCheckCardMode(BBDeviceController.CheckCardMode.SWIPE);
                String resultMsg = at.getString(R.string.insert_or_swipe_card_or_tap_another_card);
                updateDialogMsg(resultMsg);
//                showFallbackDlg(resultMsg);
//                BBDeviceController.CheckCardMode checkCardMode;
//                if (isCancel) {
//                    checkCardMode = BBDeviceController.CheckCardMode.SWIPE_OR_INSERT;
//                } else
//                    checkCardMode = BBDeviceController.CheckCardMode.SWIPE;
//
//                showFallbackDlgWithConfirm(resultMsg, BBDeviceController.CheckCardMode.SWIPE);
                break;
        }
    }

    private class MessageHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COUNT:
                    BHelper.db("Will close dialog after wait 20s to insert card with paras Total: " + Total + "  isInsertedCard: " + isInsertedCard);
                    Total--;
                    if (Total > 0 && !isInsertedCard) {
                        msg = obtainMessage(COUNT);
                        sendMessageDelayed(msg, 1000);
                        BHelper.db("Wait to close diaglog:" + Total);
                    } else if (Total <= 0 && !isInsertedCard) {
                        emvReader.cancelCheckCard();
                        isClickPay = false;
                        closeDialogDelay(2000);
                    }
                    break;
                case SHOW_PERCENT:
                    Total--;
                    if (Total > 0) {
                        msg = obtainMessage(SHOW_PERCENT);
                        sendMessageDelayed(msg, 1000);
                        BHelper.db("show percent:" + (100 - Total * 5));
                        if (dialog != null && dialog.isShowing())
                            updateDialogMsg(getString(R.string.msg_config_device_doing) + " " + (100 - Total * 5) + " %");
//						dialog.setProgress(100- Total*5);
                    } else {
                        closeDialog();
                    }
                    break;
                case START_EMV:
                    Total--;
                    if (Total > 0 && !isFinishedTrans) {
                        msg = obtainMessage(START_EMV);
                        sendMessageDelayed(msg, 1000);
                        BHelper.db("show start emv:" + (100 - Total * 5));
                    } else if (Total <= 0 && !isFinishedTrans) {
//					BHelper.showToast(R.string.msg_icc_broken);
                        closeDialog();
                        String resultMsg = getString(R.string.msg_icc_broken);
                        isAllowSwipeICC = true;
                        BHelper.db("Broken ICC. Try ask swipe");
                        emvReader.sendOnlineProcessResult(null);
                        showFallbackDlgWithConfirm(resultMsg, BBDeviceController.CheckCardMode.SWIPE);

                    }
                    break;
            }
        }
    }

    @Override
    public void onReturnCancelCheckCardResult(boolean isSuccess) {
        // TODO Auto-generated method stub
        BHelper.db("onReturnCancelCheckCardResult:" + isSuccess);
        isSuccess = false;
        closeDialog();
        if(isDontCheck){
//            closeFallbackDlg2();
            isDontCheck = false;
            checkCard(BBDeviceController.CheckCardMode.SWIPE);
        }
    }
}