package com.devcrane.payfun.daou;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devcrane.payfun.daou.caller.ParaConstant;
import com.devcrane.payfun.daou.caller.ReqPara;
import com.devcrane.payfun.daou.caller.ResPara;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.dialog.VanDLStep1Dialog;
import com.devcrane.payfun.daou.dialog.VanDLStep1DialogListener;
import com.devcrane.payfun.daou.dialog.VanDLStep2Dialog;
import com.devcrane.payfun.daou.dialog.VanDLStep2DialogListener;
import com.devcrane.payfun.daou.dialog.VanDLStep3Dialog;
import com.devcrane.payfun.daou.dialog.VanDLStep3DialogListener;
import com.devcrane.payfun.daou.dialog.VanDLStep4Dialog;
import com.devcrane.payfun.daou.dialog.VanDLStep4DialogListener;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.MyTaskStr;
import com.devcrane.payfun.daou.van.DaouData;
import com.devcrane.payfun.daou.van.DaouDataHelper;
import com.devcrane.payfun.daou.van.OpenTerminal;

/**
 * Created by admin on 7/25/17.
 */

public class RegistervanNewFragment extends Fragment {

    String companyNo, machineCode;
    CompanyEntity comEntity;
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registervan_new, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        showStep1();
    }

    void showStep1(){

        VanDLStep1Dialog dialog = new VanDLStep1Dialog(mContext, new VanDLStep1DialogListener() {
            @Override
            public void VanDLStep1DialogEvent(boolean isValid) {
                if(!isValid){
                    MainActivity.setFragment(new ProfileFragment());
                }else{
                    showStep2();
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    void showStep2(){

        VanDLStep2Dialog dialog = new VanDLStep2Dialog(mContext, new VanDLStep2DialogListener() {
            @Override
            public void VanDLStep2DialogEvent(String companyNo) {
                showStep3(companyNo);
            }

            @Override
            public void VanDLStep2DialogEvent(boolean isValid) {
                if(!isValid){
                    goBack();
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    void showStep3(String companyNo){
        VanDLStep3Dialog dialog = new VanDLStep3Dialog(mContext,companyNo, new VanDLStep3DialogListener() {
            @Override
            public void VanDLStep3DialogEvent(String companyNo, String machineCode, CompanyEntity companyEntity) {
                setCompanyInfo(companyNo,machineCode);
                showStep4(companyEntity);
            }

            @Override
            public void VanDLStep3DialogEvent(boolean isValid) {
                if(!isValid){
                    goBack();
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    void showStep4(CompanyEntity comEntity){
        VanDLStep4Dialog dialog = new VanDLStep4Dialog(mContext,comEntity, new VanDLStep4DialogListener() {
            @Override
            public void VanDLStep4DialogEvent(boolean isValid, String companyNo) {

                if(!isValid){
                    if(companyNo.equals(""))
                        goBack();
                    else
                        showStep3(companyNo);
                }else{
                    checkCaller();
                }

            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }


    void setCompanyInfo(String companyNo, String machineCode){
        this.companyNo = companyNo;
        this.machineCode = machineCode;
    }



    void checkCaller(){

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
        }else{
            goBack();
        }
    }

    void downloadDaou(final String comNo, String maCode){
        final DaouData daouData = new OpenTerminal();
        final TerminalInfo terminalInfo = new TerminalInfo();
        terminalInfo.setTerCompanyNo(comNo);
        terminalInfo.setTerNumber(maCode);
        new MyTaskStr(getActivity()){

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
                    showStep4(comEntity);
                }else {
                    String msg =result[21];
                    BHelper.showToast(msg);
                    goBack();
                }
                return false;
            }
        };
    }
    void goBack(){
        MainActivity.setFragment(new ProfileFragment());
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
                ResPara.returnFail(getActivity());
                return false;
            }
        } else if (transType.equals(ParaConstant.TRANS_TYPE_APPROVE)) {
            if (payType != null && payType.equals(ParaConstant.PAYMENT_TYPE_CREDIT)) {
                // do nothing.
            } else if (payType != null && payType.equals(ParaConstant.PAYMENT_TYPE_CASH)) {
                fragment = new PaymentsCashFragment();
            } else {
                BHelper.showToast("Payment type is incorrect");
                ResPara.returnFail(getActivity());
                return false;
            }
        } else {
            BHelper.showToast("Transaction type is incorrect");
            ResPara.returnFail(getActivity());
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
}
