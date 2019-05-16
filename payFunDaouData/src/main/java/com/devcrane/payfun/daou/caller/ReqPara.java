package com.devcrane.payfun.daou.caller;

import java.lang.reflect.Type;

import com.devcrane.payfun.daou.utility.JSonHelper;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * 
 * Request parameters for request paymnet
 */
public class ReqPara {
	/**
	 * UserID as email, used to login if need.
	 */
	String userID;
	/**
	 * Passwd used to login if need.
	 */
	String passWD;
	/**
	 * ex: JTNet;Smartro
	 */
	String vanName;
	/**
	 * company number
	 * <p>
	 * Length: 10
	 */
	String companyNo;
	/**
	 * Machine number
	 * <p>
	 * Length: 30
	 */
	String machineNo;
	/**
	 * Payment type
	 * <p>
	 * Value:credit;cash
	 */
	String paymentType;
	/**
	 * Transaction type
	 * <p>
	 * Length: 02
	 * <p>
	 * Value:
	 * <p>
	 * 01: Approve; 02: Cancel
	 */
	String transType;
	/**
	 * Tax rate
	 * <p>
	 * Length:3
	 * <p>
	 * Value
	 * ex: 5; 10,..
	 */
	String taxRate;
	/**
	 * Total Amount
	 * <p>
	 * Length: 9
	 * <p>
	 * Value
	 * ex: 1000; 1200,...
	 */
	String totalAmount;
	/**
	 * Divide month
	 * <p>
	 * Length: 2
	 * <p>
	 * Value
	 * ex: 1;2;10,...
	 */
	String divideMonth;
	/**
	 * Request date time
	 * <p>
	 * Length: 14
	 * <p>
	 * Format: yyyyMMddhhmmss
	 */
	String reqDT;
	/**
	 * Approval date time
	 * <p>
	 * Length: 14
	 * <p>
	 * Format: yyyyMMddhhmmss
	 */
	String approvalDT;
	/**
	 * Approval number (just for case cancel)
	 * <p>
	 * Length:30
	 * <p>
	 * Value
	 * ex: 131313
	 */
	String approvalNo;
	/**
	 * reserve field 1
	 * <p>
	 * Length:40
	 */
	String reserve1;
	/**
	 * reserve field 2
	 * <p>
	 * Length: 40
	 */
	String reserve2;
	
	public ReqPara(String userID, String passWD, String vanName, String companyNo, String machineNo, String paymentType, String transType, String taxRate,
			String totalAmount, String divideMonth, String reqDT, String approvalDT, String approvalNo, String reserve1,
			String reserve2) {
		super();
		this.userID = userID;
		this.passWD = passWD;
		this.vanName = vanName;
		this.companyNo = companyNo;
		this.machineNo = machineNo;
		this.paymentType = paymentType;
		this.transType = transType;
		this.taxRate = taxRate;
		this.totalAmount = totalAmount;
		this.divideMonth = divideMonth;
		this.reqDT = reqDT;
		this.approvalDT = approvalDT;
		this.approvalNo = approvalNo;
		this.reserve1 = reserve1;
		this.reserve2 = reserve2;
	}
	public ReqPara(){
		
	}
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getPassWD() {
		return passWD;
	}
	public void setPassWD(String passWD) {
		this.passWD = passWD;
	}
	public String getVanName() {
		return vanName;
	}
	public void setVanName(String vanName) {
		this.vanName = vanName;
	}
	public String getCompanyNo() {
		return companyNo;
	}
	public void setCompanyNo(String companyNo) {
		this.companyNo = companyNo;
	}
	public String getMachineNo() {
		return machineNo;
	}
	public void setMachineNo(String machineNo) {
		this.machineNo = machineNo;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
	}
	public String getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getDivideMonth() {
		return divideMonth;
	}
	public void setDivideMonth(String divideMonth) {
		this.divideMonth = divideMonth;
	}
	public String getReqDT() {
		return reqDT;
	}
	public void setReqDT(String reqDT) {
		this.reqDT = reqDT;
	}
	public String getApprovalDT() {
		return approvalDT;
	}
	public void setApprovalDT(String approvalDT) {
		this.approvalDT = approvalDT;
	}
	public String getApprovalNo() {
		return approvalNo;
	}
	public void setApprovalNo(String approvalNo) {
		this.approvalNo = approvalNo;
	}
	public String getReserve1() {
		return reserve1;
	}
	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}
	public String getReserve2() {
		return reserve2;
	}
	public void setReserve2(String reserve2) {
		this.reserve2 = reserve2;
	}
	
	@Override
	public String toString() {
		return "ReqPara [userID=" + userID + ", passWD=" + passWD + ", vanName=" + vanName + ", companyNo=" + companyNo
				+ ", machineNo=" + machineNo + ", paymentType=" + paymentType + ", transType=" + transType
				+ ", taxRate=" + taxRate + ", totalAmount=" + totalAmount + ", divideMonth=" + divideMonth + ", reqDT="
				+ reqDT + ", approvalDT=" + approvalDT + ", approvalNo=" + approvalNo + ", reserve1=" + reserve1
				+ ", reserve2=" + reserve2 + "]";
	}
	public static String toJsonString(ReqPara obj){
		return JSonHelper.serializerJson(obj);
	}
	public static ReqPara fromJsonString(String jsonData){
		Type type = new TypeToken<ReqPara>() {
		}.getType();
		return (ReqPara)JSonHelper.deserializerJson(jsonData, type);
	}
	
}
