package com.devcrane.payfun.daou.caller;

import java.lang.reflect.Type;

import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.JSonHelper;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;

/**
 * 
 * Response para for payment
 *
 */
public class ResPara {
	
	/**
	 * company number
	 * <p>
	 * Length: 10
	 */
	String companyNo = "";
	/**
	 * Payment type
	 * <p>
	 * Value:credit;cash
	 */
	String paymentType = "";
	/**
	 * Transaction type
	 * <p>
	 * Length: 02
	 * <p>
	 * Value:
	 * <p>
	 * 01: Approve; 02: Cancel
	 */
	String transType = "";
	/**
	 * Transaction result
	 * <p>
	 * Lengh:2
	 * <p>
	 * Value:
	 * <p>
	 * 00: success; 99: fail
	 */
	String transResult = ParaConstant.TRANS_RESULT_FAIL;
	
	/**
	 * return message by VAN
	 * <p>
	 * Length: 100
	 */
	String message = "";
	/**
	 * Approval date time
	 * <p>
	 * Length: 14
	 * <p>
	 * Format: yyyyMMddhhmmss
	 */
	String approvalDT = "";
	/**
	 * Approval number (just for case cancel)
	 * <p>
	 * Length:30
	 * <p>
	 * Value
	 * ex: 131313
	 */
	String approvalNo = "";
	/**
	 * Divide month
	 * <p>
	 * Length: 2
	 * <p>
	 * Value
	 * ex: 1;2;10,...
	 */
	String divideMonth = "";
	
	/**
	 * Tax rate
	 * <p>
	 * Length:3
	 * <p>
	 * Value
	 * ex: 5%; 10%,..
	 */
	String taxRate = "";
	/**
	 * Payment amount
	 * <p>
	 * Length: 9
	 */
	String amount = "";
	/**
	 * Total Amount
	 * <p>
	 * Length: 9
	 * <p>
	 * Value
	 * ex: 1000; 1200,...
	 */
	String totalAmount = "";
	/**
	 * Card name
	 */
	String cardName = "";
	/**
	 * Card No
	 * <p>
	 * Masking Card number by VAN. (if Cash masking Cash number) XXXX-XXXX-XXXX-XXXX
	 */
	String cardNo = "";
	/**
	 * Card company number
	 * <p>
	 * Length: 30
	 */
	String cardCoNo = "";
	/**
	 * Bank company name
	 * <p>
	 * reserve field
	 * <p>
	 * Length: 30
	 */
	String bankName = "";
	/**
	 * Bank company number
	 * <p>
	 * reserve field
	 * <p>
	 * Length: 30
	 */
	String bankNo = "";
	/**
	 * reserve field 1
	 * <p>
	 * Length:40
	 */
	String reserve1 = "";
	/**
	 * reserve field 2
	 * <p>
	 * Length: 40
	 */
	String reserve2 = "";
	public ResPara(String companyNo, String paymentType, String transType, String transResult, String message,
			String approvalDT, String approvalNo, String divideMonth, String taxRate, String amount, String totalAmount,
			String cardName, String cardNo, String cardCoNo, String bankName, String bankNo, String reserve1,
			String reserve2) {
		super();
		this.companyNo = companyNo;
		this.paymentType = paymentType;
		this.transType = transType;
		this.transResult = transResult;
		this.message = message;
		this.approvalDT = approvalDT;
		this.approvalNo = approvalNo;
		this.divideMonth = divideMonth;
		this.taxRate = taxRate;
		this.amount = amount;
		this.totalAmount = totalAmount;
		this.cardName = cardName;
		this.cardNo = cardNo;
		this.cardCoNo = cardCoNo;
		this.bankName = bankName;
		this.bankNo = bankNo;
		this.reserve1 = reserve1;
		this.reserve2 = reserve2;
	}
	public ResPara(){
		
	}
	
	public String getCompanyNo() {
		return companyNo;
	}
	public void setCompanyNo(String companyNo) {
		this.companyNo = companyNo;
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
	public String getTransResult() {
		return transResult;
	}
	public void setTransResult(String transResult) {
		this.transResult = transResult;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
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
	public String getDivideMonth() {
		return divideMonth;
	}
	public void setDivideMonth(String divideMonth) {
		this.divideMonth = divideMonth;
	}
	public String getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getCardCoNo() {
		return cardCoNo;
	}
	public void setCardCoNo(String cardCoNo) {
		this.cardCoNo = cardCoNo;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
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
		return "ResPara [companyNo=" + companyNo + ", paymentType=" + paymentType + ", transType=" + transType
				+ ", transResult=" + transResult + ", message=" + message + ", approvalDT=" + approvalDT
				+ ", approvalNo=" + approvalNo + ", divideMonth=" + divideMonth + ", taxRate=" + taxRate + ", amount="
				+ amount + ", totalAmount=" + totalAmount + ", cardName=" + cardName + ", cardNo=" + cardNo
				+ ", cardCoNo=" + cardCoNo + ", bankName=" + bankName + ", bankNo=" + bankNo + ", reserve1=" + reserve1
				+ ", reserve2=" + reserve2 + "]";
	}
	public static String toJsonString(ResPara obj){
		return JSonHelper.serializerJson(obj);
	}
	public static ResPara fromJsonString(String jsonData){
		Type type = new TypeToken<ResPara>() {
		}.getType();
		return (ResPara)JSonHelper.deserializerJson(jsonData, type);
	}
	public static ResPara fromReceipt(ReceiptEntity receiptEntity){
		ResPara resPara = new ResPara();
		resPara.setAmount(receiptEntity.getF_Amount());
		resPara.setApprovalDT(receiptEntity.getF_revDate());
		resPara.setApprovalNo(receiptEntity.getF_ApprovalCode());
		resPara.setBankName("");
		resPara.setBankNo("");
		resPara.setCardCoNo(receiptEntity.getF_revCoCode());
		resPara.setCardName(receiptEntity.getF_revSeller());
		resPara.setCardNo(receiptEntity.getF_CardNo());
		resPara.setCompanyNo(receiptEntity.getF_CompanyNo());
		resPara.setDivideMonth(receiptEntity.getF_Month());
		resPara.setMessage(receiptEntity.getF_revMessage());
		resPara.setPaymentType(receiptEntity.getF_Type().toLowerCase());
		double taxRate = 100 * Double.parseDouble(receiptEntity.getF_Tax());
		taxRate = taxRate/Double.parseDouble(receiptEntity.getF_TotalAmount());
		resPara.setTaxRate(String.valueOf(Math.round(taxRate)));
		resPara.setTotalAmount(receiptEntity.getF_TotalAmount());
		resPara.setTransResult(ParaConstant.TRANS_RESULT_SUCCESS);
		resPara.setTransType(receiptEntity.getF_revStatus().equals("1")?ParaConstant.TRANS_TYPE_APPROVE:ParaConstant.TRANS_TYPE_CANCEL);
		return resPara;
	}
	static void saveToReturnFailToCallerApp(){
		ResPara resPara = new ResPara();
		AppHelper.setReturnToCaller(ResPara.toJsonString(resPara));
	}
	public static void returnFail(Activity at){
		saveToReturnFailToCallerApp();
		StaticData.checkToReturnCallerApp(at);
	}
}
