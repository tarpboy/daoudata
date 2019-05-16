package com.devcrane.payfun.daou.entity;

/**
 * Created by admin on 6/2/17.
 */

public class CancelEntity {
    String amount;
    String cardNo;
    String rDate;
    String companyNo;
    String machineCode;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getrDate() {
        return rDate;
    }

    public void setrDate(String rDate) {
        this.rDate = rDate;
    }

    public String getCompanyNo() {
        return companyNo;
    }

    public void setCompanyNo(String companyNo) {
        this.companyNo = companyNo;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    @Override
    public String toString() {
        return "CancelEntity{" +
                "amount='" + amount + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", rDate='" + rDate + '\'' +
                ", companyNo='" + companyNo + '\'' +
                ", machineCode='" + machineCode + '\'' +
                '}';
    }
}
