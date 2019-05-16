package com.devcrane.payfun.daou.entity;

import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.van.DaouDataHelper;

/**
 * Created by Administrator on 2/9/2017.
 */

public class InCompleteDataEntity {
    String respCode ="";
    String transType ="";
    String taskCode="";
    String transDate="";
    String transUnique="";
    String approvalNo="";
    String pointApprovalNo="";
    String cardInfo="";//ANB
    String pointCardInfo="";//ABB

    public InCompleteDataEntity(){

    }
    public InCompleteDataEntity(String[] recv){
        this.respCode = recv[1];
        this.transType = recv[0].substring(7,11);
        this.taskCode =  recv[0].substring(11,13);

        String transDate = recv[3];
        transDate = transDate.substring(0,8);
        this.transDate = transDate;

        String approvalNo = recv[5];
        this.approvalNo = DaouDataHelper.appendChar(approvalNo,' ',12);

        String transUniqueNo = recv[4];
        this.transUnique = DaouDataHelper.appendChar(transUniqueNo,' ',12);

        pointApprovalNo = DaouDataHelper.appendChar("",' ',12);
        pointCardInfo = DaouDataHelper.appendChar("",' ',121);
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getTransUnique() {
        return transUnique;
    }

    public void setTransUnique(String transUnique) {
        this.transUnique = transUnique;
    }

    public String getApprovalNo() {
        return approvalNo;
    }

    public void setApprovalNo(String approvalNo) {
        this.approvalNo = approvalNo;
    }

    public String getPointApprovalNo() {
        return pointApprovalNo;
    }

    public void setPointApprovalNo(String pointApprovalNo) {
        this.pointApprovalNo = pointApprovalNo;
    }

    public String getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(String cardInfo) {
        this.cardInfo = cardInfo;
    }

    public String getPointCardInfo() {
        return pointCardInfo;
    }

    public void setPointCardInfo(String pointCardInfo) {
        this.pointCardInfo = pointCardInfo;
    }
    public byte[] makeIncompleteData(){
        String data = transType + taskCode + transDate + transUnique + approvalNo
                +pointApprovalNo + DaouDataHelper.appendChar(cardInfo,' ',121) + pointCardInfo;
        BHelper.db("makeIncompleteData:"+ data);
        BHelper.db("IncompleteDAta:"+ toString());
        return data.getBytes();
    }

    @Override
    public String toString() {
        return "InCompleteDataEntity{" +
                "respCode='" + respCode + '\'' +
                ", transType='" + transType + '\'' +
                ", taskCode='" + taskCode + '\'' +
                ", transDate='" + transDate + '\'' +
                ", transUnique='" + transUnique + '\'' +
                ", approvalNo='" + approvalNo + '\'' +
                ", pointApprovalNo='" + pointApprovalNo + '\'' +
                ", cardInfo='" + cardInfo + '\'' +
                ", pointCardInfo='" + pointCardInfo + '\'' +
                '}';
    }
}
