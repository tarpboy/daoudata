package com.devcrane.payfun.daou;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bbpos.simplyprint.SimplyPrintController;
import com.bbpos.simplyprint.SimplyPrintController.BatteryStatus;
import com.bbpos.simplyprint.SimplyPrintController.Error;
import com.bbpos.simplyprint.SimplyPrintController.PrinterResult;
import com.bbpos.simplyprint.SimplyPrintController.SimplyPrintControllerListener;
import com.devcrane.payfun.daou.caller.ResPara;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.ui.DialogRecipt;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.ReceiptUtility;
import com.devcrane.payfun.daou.utility.SDcardHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zj.btsdk.BluetoothService;
import com.zj.btsdk.PrintPic;

import org.w3c.dom.Text;

public abstract class ReceiptFragment extends Fragment {
    protected Activity mActivity;
    protected View v1;
    protected View vReceiptReqDate2;
    protected View vReceiptCardNo3;
    protected View v4;
    protected View vReceiptApproval5;
    protected View v6;
    protected View vReceiptAmount7;
    protected View vReceiptTax8;
    protected View vReceiptPoint9;
    protected View vReceiptTotalAmount10;
    protected View vReceiptDivideMonth;
    protected View vReceiptDivideMonthN;

    protected View vReceiptBankBalanceTop;
    protected View vReceiptBankBalance;
    protected View v11;
    protected View vReceiptCompanyNo12;
    protected View vCompanyOwnerName13;
    protected View vCompanyPhone14;
    protected View vCompanyAddress15;
    protected Button btnForward;
    //	protected Button btnCancel;
    protected Button btnGotoDailyList;
    protected ReceiptEntity re;
    protected ImageView imgSign;
    protected LinearLayout viewSign;
    protected LinearLayout vReceiptBankBalanceLayout;
    BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    boolean isCredit;
    boolean bluetooth = false;
    private static final int REQUEST_ENABLE_BT = 2;
    protected DialogRecipt alertDialog;
    private String pastSign = "";
    private String rightV1 = "";
    private ProgressDialog dialog;
    // print
    protected LinearLayout linearRecipt;
    protected View v1N;
    protected View vReceiptReqDate2N;
    protected View vReceiptCardNo3N;
    protected View v4N;
    protected View vReceiptApproval5N;
    protected View v6N;
    protected View v11N;
    protected View vReceiptCompanyNo12N;
    protected View vCompanyOwnerName13N;
    protected View vCompanyAddress15N;
    protected View vReceiptReqCancelDateN;
    protected View vReceiptTypeSub;
    protected ImageView imgSignN;
    private SimplyPrintController controller;
    protected static List<BluetoothDevice> foundDevicesList;
    private CompanyEntity comE;

    protected void onStartReceiptFragment() {
        super.onStart();
        mActivity = MainActivity.getInstance();
        initView();
        initComponent();

    }

    private void initView() {
        v1 = mActivity.findViewById(R.id.v1);
        vReceiptReqDate2 = mActivity.findViewById(R.id.vReceiptReqDate2);
        vReceiptCardNo3 = mActivity.findViewById(R.id.vReceiptCardNo3);
        v4 = mActivity.findViewById(R.id.v4);
        vReceiptApproval5 = mActivity.findViewById(R.id.vReceiptApproval5);
        v6 = mActivity.findViewById(R.id.v6);
        vReceiptAmount7 = mActivity.findViewById(R.id.vReceiptAmount7);
        vReceiptTax8 = mActivity.findViewById(R.id.vReceiptTax8);
        vReceiptPoint9 = mActivity.findViewById(R.id.vReceiptPoint9);
        vReceiptTotalAmount10 = mActivity.findViewById(R.id.vReceiptTotalAmount10);
        vReceiptDivideMonth = mActivity.findViewById(R.id.vReceiptDivideMonth);
        vReceiptDivideMonthN = mActivity.findViewById(R.id.vReceiptDivideMonthN);
        vReceiptBankBalance = mActivity.findViewById(R.id.vReceiptBankBalance);
        v11 = mActivity.findViewById(R.id.v11);
        vReceiptCompanyNo12 = mActivity.findViewById(R.id.vReceiptCompanyNo12);
        vCompanyOwnerName13 = mActivity.findViewById(R.id.vCompanyOwnerName13);
        vCompanyPhone14 = mActivity.findViewById(R.id.vCompanyPhone14);
        vCompanyAddress15 = mActivity.findViewById(R.id.vCompanyAddress15);
        btnForward = (Button) mActivity.findViewById(R.id.btnForward);
//		btnCancel = (Button) mActivity.findViewById(R.id.btnCancel);
        btnGotoDailyList = (Button) mActivity.findViewById(R.id.btnGotoDaily);
        imgSign = (ImageView) mActivity.findViewById(R.id.imgSign);
        viewSign = (LinearLayout) mActivity.findViewById(R.id.viewSign);
        vReceiptBankBalanceTop = mActivity.findViewById(R.id.vReceiptBankBalanceTop);
        vReceiptBankBalanceLayout = (LinearLayout) mActivity.findViewById(R.id.vReceiptBankBalanceLayout);
        linearRecipt = (LinearLayout) mActivity.findViewById(R.id.vPrint);
        v1N = mActivity.findViewById(R.id.v1_new);
        vReceiptReqDate2N = mActivity.findViewById(R.id.vReceiptReqDate2_new);
        vReceiptCardNo3N = mActivity.findViewById(R.id.vReceiptCardNo3_new);
        v4N = mActivity.findViewById(R.id.v4_new);
        vReceiptApproval5N = mActivity.findViewById(R.id.vReceiptApproval5_new);
        v6N = mActivity.findViewById(R.id.v6_new);
        v11N = mActivity.findViewById(R.id.v11_new);
        vReceiptCompanyNo12N = mActivity.findViewById(R.id.vReceiptCompanyNo12_new);
        vCompanyOwnerName13N = mActivity.findViewById(R.id.vCompanyOwnerName13_new);
        vCompanyAddress15N = mActivity.findViewById(R.id.vCompanyAddress15_new);
        imgSignN = (ImageView) mActivity.findViewById(R.id.imgSign_new);
        vReceiptTypeSub = mActivity.findViewById(R.id.layoutTypeSub_new);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        returnCallerApp();
    }

    private void initComponent() {
        BHelper.setActivity(mActivity);
        setRecipt();

        btnForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doForwardReceipt();

            }
        });

        btnGotoDailyList.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MainActivity.setFragment(new CancelDailyListFragment());

            }
        });
        setEnableButton();
    }

    public void setRecipt() {
        try {
            final Type type = new TypeToken<ReceiptEntity>() {
            }.getType();
            re = new Gson().fromJson(StaticData.sResultPayment, type);
            //destroy value
            StaticData.sResultPayment = "";
            isCredit = re.getF_Type().equals("Credit");
            rightV1 = getString(R.string.title_credit);
            String titleReceipt = getString(R.string.title_receipt);
            if (!isCredit) {
                MainActivity.setHeaderText(R.string.title_receipt_cash);
                rightV1 = getString(R.string.title_cash);
                titleReceipt = getString(R.string.title_receipt_cash);
            }
            final CompanyEntity ce = CompanyManger.getCompanyByCompanyNo(re.getF_CompanyNo());
            comE = ce;
            String scr = "";
            BHelper.db("Receipt:" + re.toString());
            BHelper.db("ReStatus:" + re.getF_revStatus());
            boolean isCancel = re.getF_revStatus().equals("0");
            if (isCancel)
                scr = "-";
            setLeft(v1, re.getF_TypeSub());
            setRight(v1, rightV1);
            setRight(vReceiptReqDate2, re.getF_RequestDate());
            setRight(vReceiptCardNo3, Helper.formatCardNo(re.getF_CardNo()));
            setRight(v4, re.getF_revCoCode());
            setRight(vReceiptApproval5, re.getF_ApprovalCode().trim());
            // setRight(v6, titleReceipt);
            setRight(v6, re.getF_BuyerName());
            setRight(vReceiptAmount7, scr + Helper.formatNumberExcel(re.getF_Amount()));
            String tax = Helper.formatNumberExcel(re.getF_Tax());
            if (tax.equals("0"))
                tax = "0";
            else
                tax = scr + tax;
            setRight(vReceiptTax8, tax);
            String point = Helper.formatNumberExcel(re.getF_CouponDiscountAmount());
            if (point.equals("0"))
                point = "0";
            else
                point = scr + point;
            setRight(vReceiptPoint9, point);
            if (StaticData.bank_card_balance_amout.isEmpty() || StaticData.bank_card_balance_amout.equals("0")) {
                vReceiptBankBalanceTop.setVisibility(View.GONE);
                vReceiptBankBalanceLayout.setVisibility(View.GONE);
                BHelper.db("vReceiptBankBalanceTop.setVisibility(View.GONE)");
            } else {
                vReceiptBankBalanceTop.setVisibility(View.VISIBLE);
                vReceiptBankBalanceLayout.setVisibility(View.VISIBLE);
                BHelper.db("vReceiptBankBalanceTop.setVisibility(View.VISIBLE)");
                setRight(vReceiptBankBalance, Helper.formatNumberExcel(StaticData.bank_card_balance_amout));
            }
            setRight(vReceiptTotalAmount10, scr + Helper.formatNumberExcel(re.getF_TotalAmount()));
            String divideMonth = re.getF_Month();
            if (divideMonth.equals("00") || divideMonth.equals("0"))
                divideMonth = "일시불";
            else {
                divideMonth = divideMonth + " 개월";
            }
            setRight(vReceiptDivideMonth, divideMonth);
            setRight(vReceiptDivideMonthN, divideMonth);
            setRight(vReceiptCompanyNo12, Helper.formatCompanyNo(re.getF_CompanyNo()));
            setRight(vReceiptCompanyNo12N, Helper.formatCompanyNo(re.getF_CompanyNo()));
            if (ce != null) {
                setRight(v11, ce.getF_CompanyName());
                setRight(vCompanyOwnerName13, ce.getF_CompanyOwnerName());
                setRight(vCompanyPhone14, ce.getF_CompanyPhoneNo());
                setRight(vCompanyAddress15, ce.getF_CompanyAddress());
                // print
                setRight(v11N, ce.getF_CompanyName());
                // setRight(vCompanyOwnerName13N, ce.getF_CompanyOwnerName());
                setRight(vCompanyOwnerName13N, re.getF_BuyerName());
                setRight(vCompanyAddress15N, ce.getF_CompanyAddress());
            }

//			if (isCancel) {
//				btnCancel.setSelected(false);
//				btnCancel.setEnabled(false);
//			} else {
//				btnCancel.setSelected(true);
//				btnCancel.setEnabled(true);
//			}
            Double val = Double.parseDouble(re.getF_TotalAmount());
            if (isCredit && val > StaticData.SIGNATURE_AMOUNT_LIMIT) {
                String path = Helper.getExSD() + re.getF_ID() + ".png";

                File temp = new File(path);
                if (!temp.exists()) {
                    BHelper.bitmapFromURL(re.getF_ID() + ".png");
                }

                try {
                    pastSign = path;
                    viewSign.setVisibility(View.VISIBLE);
                    Bitmap bm = SDcardHelper.BitmapFromFilePath(path);
                    imgSign.setImageBitmap(bm);
                    if (imgSignN != null)
                        imgSignN.setImageBitmap(bm);
                    //delete signature after show in receipt
//                    SDcardHelper.deleteFile(path);
                    String pathsFile = Helper.getExSD() + StaticData.TMP_SIGN;
                    SDcardHelper.deleteFile(pathsFile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
            setRight(v1N, rightV1);
            setRight(vReceiptReqDate2N, re.getF_RequestDate());
            setRight(vReceiptCardNo3N, Helper.formatCardNo(re.getF_CardNo()));
            setRight(v4N, re.getF_revCoCode());
            setRight(vReceiptApproval5N, re.getF_ApprovalCode().trim());
            setRight(v6N, titleReceipt);
            setRight(vReceiptTypeSub, re.getF_TypeSub());
            setAmount(scr + re.getF_Amount());
            setTax(scr + re.getF_Tax());
            String service = re.getF_Service();
            if (!service.equals("0"))
                setService(scr + service);
            setTotal(scr + re.getF_TotalAmount());
            saveToReturnToCallerApp(re);

        } catch (Exception ex) {
            BHelper.ex(ex);
        }
    }

    void saveToReturnToCallerApp(ReceiptEntity receiptEntity) {
        ResPara resPara = ResPara.fromReceipt(receiptEntity);
        AppHelper.setReturnToCaller(ResPara.toJsonString(resPara));
    }

    void returnCallerApp() {
        StaticData.checkToReturnCallerApp(mActivity);

//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				AppDataStatic.checkToReturnCallerApp(mActivity);
//			}
//		}, 5000);
    }

    private void setRight(final View v, String str) {
        try {
            if (v == null)
                return;
            final TextView tv = v.findViewById(R.id.tvRight) != null ? (TextView) v.findViewById(R.id.tvRight) : null;

            final int[] ids = {R.id.vReceiptAmount7, R.id.vReceiptTax8, R.id.vReceiptPoint9, R.id.vReceiptTotalAmount10, R.id.vReceiptBankBalance,};
            for (int id : ids) {
                if (id == v.getId()) {

                    int uint = (id == R.id.vReceiptPoint9) ? R.string.recipt_unit_p : R.string.recipt_unit;
                    str += getString(uint);
                }
            }
            tv.setText(str);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void changeBitmapColor(Bitmap sourceBitmap, ImageView image, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);
        image.setImageBitmap(resultBitmap);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
    }

    private void setLeft(final View v, String str) {
        try {
            if (v == null)
                return;
            final TextView tv = v.findViewById(R.id.tvLeft) != null ? (TextView) v.findViewById(R.id.tvLeft) : null;

            final int[] ids = {R.id.vReceiptAmount7, R.id.vReceiptTax8, R.id.vReceiptPoint9, R.id.vReceiptTotalAmount10, R.id.vReceiptBankBalance,};
            for (int id : ids) {
                if (id == v.getId()) {

                    int uint = (id == R.id.vReceiptPoint9) ? R.string.recipt_unit_p : R.string.recipt_unit;
                    str += getString(uint);
                }
            }
            tv.setText(str);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private String getTextReciept(final View v) {
        TextView tvL = (TextView) v.findViewById(R.id.tvLeft);

        TextView tvR = (TextView) v.findViewById(R.id.tvRight);
        String value = tvL.getText().toString() + " : 		" + tvR.getText().toString();
        if (v.getId() == R.id.vCompanyAddress15)
            value = tvL.getText().toString() + "		" + tvR.getText().toString();
        return value + "\n";
    }

    protected abstract void doForwardReceipt();

    protected abstract void doCanceldReceipt();

    protected abstract void setEnableButton();

    protected void showPopup() {
        alertDialog = new DialogRecipt(mActivity);
        alertDialog.btnEmail.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String to = alertDialog.txtEmail.getText().toString().trim();
                if (to.equals("")) {
                    showAlert(mActivity, getActivity().getString(R.string.recipt_email_select_address));
                    alertDialog.dismiss();
                    return;
                }

                alertDialog.dismiss();
                StaticData.GETCOUPON = true;
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.recipt_email_title));
                email.setType("image/png");
                email.putExtra(Intent.EXTRA_TEXT, getEmailContentSale());
                email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(getBitmapFile()));
                mActivity.startActivity(Intent.createChooser(email, getString(R.string.recipt_email_choose_client)));
            }

            private String getEmailContentSale() {
                StringBuilder sb = new StringBuilder();
                return sb.toString();
            }

            private void showAlert(Context ct, String string) {
                AlertDialog.Builder ab = new AlertDialog.Builder(ct);
                ab.setMessage(Html.fromHtml("<b><font color=#ff0000> " + string + "</font></b><br>"));
                ab.setMessage(string);
                ab.setPositiveButton("  OK  ", null);
                AlertDialog dialog = ab.create();
                dialog.show();
            }
        });

        alertDialog.btnSMS.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String phone = alertDialog.txtSMS.getText().toString().trim();
                if (phone.equals("")) {
                    BHelper.showToast(R.string.recipt_phone_select);
                    alertDialog.dismiss();
                    return;
                }

                alertDialog.dismiss();
                StaticData.GETCOUPON = true;
                if (alertDialog.radSms.isChecked()) {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("sms_body", getSMSContentSale());
                    sendIntent.putExtra("address", phone);
                    sendIntent.putExtra("exit_on_sent", true);
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    try {
                        mActivity.startActivity(sendIntent);
                    } catch (Exception e) {
                        Toast.makeText(mActivity, "Can't send sms!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Intent sms = new Intent(Intent.ACTION_SEND);
                    sms.setType("image/png");
                    sms.putExtra("address", phone);
                    sms.putExtra("exit_on_sent", true);
                    sms.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(getBitmapFile()));
                    mActivity.startActivity(sms);
                }
            }
        });

        alertDialog.btnPrint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = ConfigPrintFM.getAddress();
                if (address.equals("")) {
                    BHelper.showToast(R.string.bt_unregister_mobile_printer_);
                    return;
                }
                printReceipt();
            }
        });

        alertDialog.show();
    }

    private File getBitmapFile() {
        final Bitmap tmpBitmap = Bitmap.createBitmap(linearRecipt.getWidth(), linearRecipt.getHeight(), Config.ARGB_8888);
        linearRecipt.draw(new Canvas(tmpBitmap));
        final File fileName = new File(BHelper.fileSDCard(null) + "email.png");
        BHelper.bitmapSave(tmpBitmap, fileName);
        return fileName;
    }

    private String getSMSContentSale() {
        StringBuilder sb = new StringBuilder();
        for (View view : getListView()) {
            if (view == null) {
                sb.append("----------------");
            } else {
                String left = ((TextView) view.findViewById(R.id.tvLeft)).getText().toString().trim();
                String right = ((TextView) view.findViewById(R.id.tvRight)).getText().toString().trim();

                sb.append(left + ": " + right);
            }
            sb.append("\n");
        }

        // String tmpReciptLink = "http://re.checkbill.kr/?nx=" + mReciptE.getF_idx();
        // sb.append("" + s(R.string.reciptLink) + "" + " : " + tmpReciptLink + "\n");
        return sb.toString();
    }

    private View[] getListView() {
        final View[] views = {v1, vReceiptReqDate2, vReceiptCardNo3, v4, vReceiptApproval5, v6, null, //
                vReceiptAmount7, vReceiptTax8, vReceiptPoint9, vReceiptTotalAmount10,vReceiptDivideMonth, null, //
                v11, vReceiptCompanyNo12, vCompanyOwnerName13, vCompanyPhone14, vCompanyAddress15,};
        return views;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            closeDialog();
                            if (!bluetooth) {
                                doPrint();
                            }
                            bluetooth = true;
                            BHelper.db("Connect successful");
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            BHelper.db("STATE_CONNECTING.....");
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            BHelper.db("STATE_NONE.....");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:
                    BHelper.db("Device connection was lost");
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    closeDialog();
                    BHelper.db("Unable to connect device");
                    try {
                        Toast.makeText(getActivity(), getString(R.string.msg_unable_to_connect_bluetooh_device), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    break;
            }
        }
    };

    class MySimplyPrintControllerListener implements SimplyPrintControllerListener {

        @Override
        public void onBTv2Detected() {
            appendStatus(getString(R.string.bluetooth_2_detected));
        }

        @Override
        public void onBTv2DeviceListRefresh(List<BluetoothDevice> foundDevices) {

        }

        @Override
        public void onBTv2Connected(BluetoothDevice bluetoothDevice) {
            bluetooth = true;
            appendStatus(getString(R.string.bluetooth_2_connected) + " " + bluetoothDevice.getAddress());
            doPrintSimply();
        }

        @Override
        public void onBTv2Disconnected() {
            appendStatus(getString(R.string.bluetooth_2_disconnected));
        }

        @Override
        public void onBTv2ScanStopped() {
            appendStatus(getString(R.string.bluetooth_2_scan_stopped));
        }

        @Override
        public void onBTv2ScanTimeout() {

            appendStatus(getString(R.string.bluetooth_2_scan_timeout));
        }

        @Override
        public void onBTv4DeviceListRefresh(List<BluetoothDevice> foundDevices) {
            BluetoothDevice blueToothDV = null;
            for (int i = 0; i < foundDevices.size(); ++i) {
                if (foundDevices.get(i).getAddress().equals(AppHelper.prefGet(StaticData.BlueToothADD, ""))) {
                    blueToothDV = foundDevices.get(i);
                    controller.connectBTv4(foundDevices.get(i));
                    break;
                }
            }
            if (blueToothDV == null) {
                appendStatus(getString(R.string.msg_unable_to_connect_bluetooh_device));
                closeDialog();
            }
        }

        @Override
        public void onBTv4Connected() {
            bluetooth = true;
            appendStatus(getString(R.string.bluetooth_4_connected));
            doPrintSimply();
        }

        @Override
        public void onBTv4Disconnected() {
            appendStatus(getString(R.string.bluetooth_4_disconnected));
            closeDialog();
        }

        @Override
        public void onBTv4ScanStopped() {
            appendStatus(getString(R.string.bluetooth_4_scan_stopped));
            closeDialog();
        }

        @Override
        public void onBTv4ScanTimeout() {
            appendStatus(getString(R.string.bluetooth_4_scan_timeout));
            closeDialog();
        }

        @Override
        public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoTable) {

        }

        @Override
        public void onReturnPrinterResult(PrinterResult printerResult) {
            if (printerResult == PrinterResult.SUCCESS) {
                appendStatus(getString(R.string.printer_command_success));
            } else if (printerResult == PrinterResult.NO_PAPER) {
                appendStatus(getString(R.string.no_paper));
            } else if (printerResult == PrinterResult.WRONG_CMD) {
                appendStatus(getString(R.string.wrong_printer_cmd));
            } else if (printerResult == PrinterResult.OVERHEAT) {
                appendStatus(getString(R.string.printer_overheat));
            }
            closeDialog();
        }

        @Override
        public void onReturnGetDarknessResult(int value) {
            appendStatus(getString(R.string.darkness) + value);
        }

        @Override
        public void onReturnSetDarknessResult(boolean isSuccess) {
            if (isSuccess) {
                appendStatus(getString(R.string.set_darkness_success));
            } else {
                appendStatus(getString(R.string.set_darkness_fail));
            }
        }

        @Override
        public void onRequestPrinterData(int index, boolean isReprint) {
            if (isReprint) {
                appendStatus(getString(R.string.request_reprint_data));
            } else {
                appendStatus(getString(R.string.request_printer_data));
            }
        }

        @Override
        public void onPrinterOperationEnd() {
            appendStatus(getString(R.string.printer_operation_end));
        }

        @Override
        public void onBatteryLow(BatteryStatus batteryStatus) {
            if (batteryStatus == BatteryStatus.LOW) {
                appendStatus(getString(R.string.battery_low));
            } else if (batteryStatus == BatteryStatus.CRITICALLY_LOW) {
                appendStatus(getString(R.string.battery_critically_low));
            }
        }

        @Override
        public void onBTv2DeviceNotFound() {
            appendStatus(getString(R.string.no_device_detected));
        }

        @Override
        public void onError(Error errorState) {
            if (errorState == Error.UNKNOWN) {
                appendStatus(getString(R.string.unknown_error));
            } else if (errorState == Error.CMD_NOT_AVAILABLE) {
                appendStatus(getString(R.string.command_not_available));
            } else if (errorState == Error.TIMEOUT) {
                appendStatus(getString(R.string.device_no_response));
            } else if (errorState == Error.DEVICE_BUSY) {
                appendStatus(getString(R.string.device_busy));
            } else if (errorState == Error.INPUT_OUT_OF_RANGE) {
                appendStatus(getString(R.string.out_of_range));
            } else if (errorState == Error.INPUT_INVALID) {
                appendStatus(getString(R.string.input_invalid));
            } else if (errorState == Error.CRC_ERROR) {
                appendStatus(getString(R.string.crc_error));
            } else if (errorState == Error.FAIL_TO_START_BTV2) {
                appendStatus(getString(R.string.fail_to_start_bluetooth));
            } else if (errorState == Error.COMM_LINK_UNINITIALIZED) {
                appendStatus(getString(R.string.comm_link_uninitialized));
            } else if (errorState == Error.BTV2_ALREADY_STARTED) {
                appendStatus(getString(R.string.bluetooth_already_started));
            }
            closeDialog();
        }

    }

    private void appendStatus(String s) {
        Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
    }

    private void printReceipt() {
        String blueToothSDK = AppHelper.prefGet(StaticData.BlueToothSDK, "1");
        if (blueToothSDK.equals("1")) {
            if (!bluetooth) {
                mService = new BluetoothService(mActivity, mHandler);
                if (mService.isAvailable() == false) {
                    Toast.makeText(mActivity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                } else {
                    if (mService.isBTopen() == false) {
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    } else {
                        showDialog();
                        con_dev = mService.getDevByMac(ConfigPrintFM.getAddress());
                        mService.connect(con_dev);
                    }
                }
            } else {
                doPrint();
            }
        } else {
            showDialog();
            if (!bluetooth) {
                controller = new SimplyPrintController(mActivity, new MySimplyPrintControllerListener());
                String[] DEVICE_NAMES = new String[]{AppHelper.prefGet(StaticData.BlueToothName, ""), "BTPTR", "SIMPLYP"};
                controller.scanBTv4(DEVICE_NAMES, 120);
            } else {
                doPrintSimply();
            }
        }
    }

    private void doPrintSimply() {
        List<byte[]> receipts = new ArrayList<byte[]>();
        receipts.add(ReceiptUtility.genReceipt(mActivity, re, pastSign, rightV1, comE));
        controller.startPrinting(receipts.size(), 120, 120);

    }

    private void doPrint() {
        String msg = "";
        msg = getTextReciept(v1);
        msg += getTextReciept(vReceiptReqDate2);
        msg += getTextReciept(vReceiptCardNo3);
        msg += getTextReciept(v4);
        msg += getTextReciept(vReceiptApproval5);
        msg += getTextReciept(v6);
        msg += getTextReciept(vReceiptAmount7);
        msg += getTextReciept(vReceiptTax8);
        msg += getTextReciept(vReceiptPoint9);
        msg += getTextReciept(vReceiptTotalAmount10);
        msg += getTextReciept(vReceiptDivideMonth);
        msg += getTextReciept(v11);
        msg += getTextReciept(vReceiptCompanyNo12);
        msg += getTextReciept(vCompanyOwnerName13);
        msg += getTextReciept(vCompanyPhone14);
        msg += getTextReciept(vCompanyAddress15);
        msg += "\n";

        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x21;
        cmd[2] |= 0x10;
        mService.write(cmd);
        mService.sendMessage(AppHelper.getTitle(!isCredit) + "\n\n", "EUC-KR");
        cmd[2] &= 0xEF;
        mService.write(cmd);
        mService.sendMessage(msg, "EUC-KR");
        printImage(pastSign);
    }

    private void printImage(String path) {
        byte[] sendData = null;
        PrintPic pg = new PrintPic();
        pg.initCanvas(384);
        pg.initPaint();
        pg.drawImage(0, 0, path);
        sendData = pg.printDraw();
        mService.write(sendData);
        mService.sendMessage("\n\n", "EUC-KR");
    }

    private void showDialog() {
        dialog = ProgressDialog.show(getActivity(), null, "Loading...", true);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void closeDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mService != null)
            mService.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            SDcardHelper.deleteFile(pastSign);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void setAmount(String amout) {
        char[] a = Helper.appendSpace(amout, 9);
        int[] ids = {R.id.loConten9, R.id.loConten8, R.id.loConten7, R.id.loConten6, R.id.loConten5, R.id.loConten4, R.id.loConten3, R.id.loConten2, R.id.loConten1};
        for (int i = 0; i < ids.length; i++) {
            View v = mActivity.findViewById(ids[i]);
            setRight(v, String.valueOf(a[i]));

        }
    }

    private void setTax(String tax) {
        char[] a = Helper.appendSpace(tax, 9);
        int[] ids = {R.id.loTaxConten9, R.id.loTaxConten8, R.id.loTaxConten7, R.id.loTaxConten6, R.id.loTaxConten5, R.id.loTaxConten4, R.id.loTaxConten3, R.id.loTaxConten2, R.id.loTaxConten1};
        for (int i = 0; i < ids.length; i++) {
            View v = mActivity.findViewById(ids[i]);
            setRight(v, String.valueOf(a[i]));

        }
    }

    private void setService(String service) {
        char[] a = Helper.appendSpace(service, 9);
        int[] ids = {R.id.loTipConten9, R.id.loTipConten8, R.id.loTipConten7, R.id.loTipConten6, R.id.loTipConten5, R.id.loTipConten4, R.id.loTipConten3, R.id.loTipConten2, R.id.loTipConten1};
        for (int i = 0; i < ids.length; i++) {
            View v = mActivity.findViewById(ids[i]);
            setRight(v, String.valueOf(a[i]));

        }
    }

    private void setTotal(String total) {
        char[] a = Helper.appendSpace(total, 9);
        int[] ids = {R.id.loTotalConten9, R.id.loTotalConten8, R.id.loTotalConten7, R.id.loTotalConten6, R.id.loTotalConten5, R.id.loTotalConten4, R.id.loTotalConten3, R.id.loTotalConten2, R.id.loTotalConten1};
        for (int i = 0; i < ids.length; i++) {
            View v = mActivity.findViewById(ids[i]);
            setRight(v, String.valueOf(a[i]));

        }
    }
}
