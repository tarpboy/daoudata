package com.devcrane.payfun.daou.entity;

/**
 * Created by Administrator on 1/16/2017.
 */

public class EmvTcEntity {
    String approvalDate="";//N 8
    String approvalNo="";//AN V12
    String transUniqueNo="";//AN 12
    String tc="00";
    String emvOption="N";

    public EmvTcEntity(){
        this.approvalNo = this.approvalDate = this.transUniqueNo = "";
        this.tc="00";
        this.emvOption="N";
    }
    public  EmvTcEntity(String approvalDate, String approvalNo, String transUniqueNo, String tc, String emvOption){
        this.approvalDate = approvalDate;
        this.approvalNo = approvalNo;
        this.transUniqueNo = transUniqueNo;
        this.tc = tc;
        this.emvOption =emvOption;
    }

    public String getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(String approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getApprovalNo() {
        return approvalNo;
    }

    public void setApprovalNo(String approvalNo) {
        this.approvalNo = approvalNo;
    }

    public String getTransUniqueNo() {
        return transUniqueNo;
    }

    public void setTransUniqueNo(String transUniqueNo) {
        this.transUniqueNo = transUniqueNo;
    }

    public String getTc() {
        return tc;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public String getEmvOption() {
        return emvOption;
    }

    public void setEmvOption(String emvOption) {
        this.emvOption = emvOption;
    }

    @Override
    public String toString() {
        return "EmvTcEntity{" +
                "approvalDate='" + approvalDate + '\'' +
                ", approvalNo='" + approvalNo + '\'' +
                ", transUniqueNo='" + transUniqueNo + '\'' +
                ", tc='" + tc + '\'' +
                ", emvOption='" + emvOption + '\'' +
                '}';
    }
}
