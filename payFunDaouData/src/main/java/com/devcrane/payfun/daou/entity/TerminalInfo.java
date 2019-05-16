package com.devcrane.payfun.daou.entity;

import com.devcrane.payfun.cardreader.EmvUtils;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.van.DaouDataContants;
import com.devcrane.payfun.daou.van.DaouDataHelper;

/**
 * Created by qts1 on 9/22/16.
 */

public class TerminalInfo {
    String terNumber = DaouDataContants.VAL_TERMINAL_NUMBER;
    String terCompanyNo = DaouDataContants.VAL_COMPANY_NUMBER;
    String terProSerialNo = DaouDataContants.VAL_PRODUCTION_SERIAL_NUMBER;
    String terHwCert = DaouDataContants.VAL_HW_CERT_DEFAULT;
    String terSwCert = DaouDataContants.VAL_SW_CERT_DEFAULT;
    String eSign = DaouDataContants.VAL_ELECTRONIC_OTHERS;
    String modelCode = DaouDataContants.VAL_MODEL_CODE;
    String machineDivision = DaouDataContants.VAL_TERMINAL_DIVISION_GENERAL;

    public String getTerNumber() {
        return terNumber;
    }

    public void setTerNumber(String terNumber) {
        this.terNumber = terNumber;
    }

    public String getTerCompanyNo() {
        return terCompanyNo;
    }

    public void setTerCompanyNo(String terCompanyNo) {
        this.terCompanyNo = DaouDataHelper.cleanData(terCompanyNo);
    }

    public String getTerProSerialNo() {
        return terProSerialNo;
    }

    public void setTerProSerialNo(String terProSerialNo) {
        this.terProSerialNo = terProSerialNo;
    }

    public String getTerHwCert() {
        return EmvUtils.getHWModelName() + EmvUtils.getHwModelNo();
    }

    public void setTerHwCert(String terHwCert) {
        this.terHwCert = terHwCert;
    }

    public String getTerSwCert() {
        return DaouDataContants.SWModelName + DaouDataContants.SWModelNo;
    }

    public void setTerSwCert(String terSwCert) {
        this.terSwCert = terSwCert;
    }

    public String geteSign() {
        return eSign;
    }

    public void seteSign(String eSign) {
        this.eSign = eSign;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getMachineDivision() {
        return machineDivision;
    }

    public void setMachineDivision(String machineDivision) {
        this.machineDivision = machineDivision;
    }

    @Override
    public String toString() {
        return "TerminalInfo{" +
                "terNumber='" + terNumber + '\'' +
                ", terCompanyNo='" + terCompanyNo + '\'' +
                ", terProSerialNo='" + terProSerialNo + '\'' +
                ", terHwCert='" + terHwCert + '\'' +
                ", terSwCert='" + terSwCert + '\'' +
                ", eSign='" + eSign + '\'' +
                ", modelCode='" + modelCode + '\'' +
                ", machineDivision='" + machineDivision + '\'' +
                '}';
    }

    public TerminalInfo(){
        this.terNumber = DaouDataContants.VAL_TERMINAL_NUMBER;
        this.terCompanyNo = DaouDataContants.VAL_COMPANY_NUMBER;
        this.terProSerialNo = EmvUtils.getHWSerialNumber();
        this.terHwCert = DaouDataContants.VAL_HW_CERT_DEFAULT;
        this.terSwCert = DaouDataContants.VAL_SW_CERT_DEFAULT;
        this.eSign = DaouDataContants.VAL_ELECTRONIC_OTHERS;
        this.modelCode = DaouDataContants.VAL_MODEL_CODE;
        this.machineDivision = DaouDataContants.VAL_TERMINAL_DIVISION_GENERAL;

        CompanyEntity companyEntity = CompanyManger.getCompanyByID(AppHelper.getCurrentVanID());
        if(companyEntity!=null){
            this.terNumber = companyEntity.getF_MachineCode();
            this.terCompanyNo = companyEntity.getF_CompanyNo();
            this.machineDivision = (companyEntity.getF_PhoneCode()==null || companyEntity.getF_PhoneCode().equals("")) ? DaouDataContants.VAL_TERMINAL_DIVISION_GENERAL: companyEntity.getF_PhoneCode();
        }
    }
    public static TerminalInfo parseFromCompany(CompanyEntity entity){
        TerminalInfo info = new TerminalInfo();
        info.setTerProSerialNo(EmvUtils.getHWSerialNumber());
        info.setTerCompanyNo(entity.getF_CompanyNo());
        info.setTerNumber(entity.getF_MachineCode());
        info.setMachineDivision((entity.getF_PhoneCode()==null || entity.getF_PhoneCode().equals("")) ? DaouDataContants.VAL_TERMINAL_DIVISION_GENERAL: entity.getF_PhoneCode());
        return info;
    }
}
