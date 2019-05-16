package com.devcrane.payfun.daou.entity;

import com.devcrane.payfun.daou.utility.SecurityHelper;

import java.util.ArrayList;
import java.util.List;


public class ReceiptEntity {
	private String f_ID;
	private String f_VanName;
	private String f_CompanyNo;
	private String f_MachineCode;
	private String f_CardInputMethod;
	private String f_Type;
	private String f_TypeSub;
	private String f_CardNo;
	private String f_Amount;
	private String f_Month;
	private String f_Tax;
	private String f_Service;
	private String f_TotalAmount;
	private String f_CouponID;
	private String f_CouponDiscountRate;
	private String f_CouponDiscountAmount;
	private String f_RequestDate;
	private String f_ApprovalCode;
	private String f_uMobile;
	private String f_revStatus;
	private String f_revCode;
	private String f_revMessage;
	private String f_revCoCode;
	private String f_revSeller;
	private String f_revDate;
	private String f_revSellerName;
	private String f_TransUniqueCode;
	private String f_BuyerName;
	private String f_ReceiptLink;
	private String f_ReciptImage;
	private String f_UserID;
	private String f_StaffName;
	
	public ReceiptEntity() {
		super();
	}

	public ReceiptEntity(int create) {
		super();
		this.f_ID = "f_ID";
		this.f_VanName = "f_VanName";
		this.f_CompanyNo = "f_CompanyNo";
		this.f_MachineCode = "f_MachineCode";
		this.f_CardInputMethod = "f_CardInputMethod";
		this.f_Type = "f_Type";
		this.f_TypeSub = "f_TypeSub";
		this.f_CardNo = "f_CardNo";
		this.f_Amount = "f_Amount";
		this.f_Month = "f_Month";
		this.f_Tax = "f_Tax";
		this.f_Service = "f_Service";
		this.f_TotalAmount = "f_TotalAmount";
		this.f_CouponID = "f_CouponID";
		this.f_CouponDiscountRate = "f_CouponDiscountRate";
		this.f_CouponDiscountAmount = "f_CouponDiscountAmount";
		this.f_RequestDate = "f_RequestDate";
		this.f_ApprovalCode = "f_ApprovalCode";
		this.f_uMobile = "f_uMobile";
		this.f_revStatus = "f_revStatus";
		this.f_revCode = "f_revCode";
		this.f_revMessage = "f_revMessage";
		this.f_revCoCode = "f_revCoCode";
		this.f_revSeller = "f_revSeller";
		this.f_revDate = "f_revDate";
		this.f_revSellerName = "f_revSellerName";
		this.f_TransUniqueCode = "f_TransUniqueCode";
		this.f_BuyerName = "f_BuyerName";
		this.f_ReceiptLink = "f_ReceiptLink";
		this.f_ReciptImage = "f_ReciptImage";
		this.f_UserID = "f_UserID";
		this.f_StaffName = "f_StaffName";
	}

	public String getF_ID() {
		return f_ID;
	}

	public void setF_ID(String f_ID) {
		this.f_ID = f_ID;
	}

	public String getF_VanName() {
		return f_VanName;
	}

	public void setF_VanName(String f_VanName) {
		this.f_VanName = f_VanName;
	}

	public String getF_CompanyNo() {
		return f_CompanyNo;
	}

	public void setF_CompanyNo(String f_CompanyNo) {
		this.f_CompanyNo = f_CompanyNo;
	}

	public String getF_MachineCode() {
		return f_MachineCode;
	}

	public void setF_MachineCode(String f_MachineCode) {
		this.f_MachineCode = f_MachineCode;
	}

	public String getF_CardInputMethod() {
		return f_CardInputMethod;
	}

	public void setF_CardInputMethod(String f_CardInputMethod) {
		this.f_CardInputMethod = f_CardInputMethod;
	}

	public String getF_Type() {
		return f_Type;
	}

	public void setF_Type(String f_Type) {
		this.f_Type = f_Type;
	}

	public String getF_TypeSub() {
		return f_TypeSub;
	}

	public void setF_TypeSub(String f_TypeSub) {
		this.f_TypeSub = f_TypeSub;
	}

	public String getF_CardNo() {
		return f_CardNo;
	}

	public void setF_CardNo(String f_CardNo) {
		this.f_CardNo = f_CardNo;
	}

	public String getF_Amount() {
		return f_Amount;
	}

	public void setF_Amount(String f_Amount) {
		this.f_Amount = f_Amount;
	}

	public String getF_Month() {
		return f_Month;
	}

	public void setF_Month(String f_Month) {
		this.f_Month = f_Month;
	}

	public String getF_Tax() {
		return f_Tax;
	}

	public void setF_Tax(String f_Tax) {
		this.f_Tax = f_Tax;
	}

	public String getF_Service() {
		return f_Service;
	}

	public void setF_Service(String f_Service) {
		this.f_Service = f_Service;
	}

	public String getF_TotalAmount() {
		return f_TotalAmount;
	}

	public void setF_TotalAmount(String f_TotalAmount) {
		this.f_TotalAmount = f_TotalAmount;
	}

	public String getF_CouponID() {
		return f_CouponID;
	}

	public void setF_CouponID(String f_CouponID) {
		this.f_CouponID = f_CouponID;
	}

	public String getF_CouponDiscountRate() {
		return f_CouponDiscountRate;
	}

	public void setF_CouponDiscountRate(String f_CouponDiscountRate) {
		this.f_CouponDiscountRate = f_CouponDiscountRate;
	}

	public String getF_CouponDiscountAmount() {
		return f_CouponDiscountAmount;
	}

	public void setF_CouponDiscountAmount(String f_CouponDiscountAmount) {
		this.f_CouponDiscountAmount = f_CouponDiscountAmount;
	}

	public String getF_RequestDate() {
		return f_RequestDate;
	}

	public void setF_RequestDate(String f_RequestDate) {
		this.f_RequestDate = f_RequestDate;
	}

	public String getF_ApprovalCode() {
		return f_ApprovalCode;
	}

	public void setF_ApprovalCode(String f_ApprovalCode) {
		this.f_ApprovalCode = f_ApprovalCode;
	}

	public String getF_uMobile() {
		return f_uMobile;
	}

	public void setF_uMobile(String f_uMobile) {
		this.f_uMobile = f_uMobile;
	}

	public String getF_revStatus() {
		return f_revStatus;
	}

	public void setF_revStatus(String f_revStatus) {
		this.f_revStatus = f_revStatus;
	}

	public String getF_revCode() {
		return f_revCode;
	}

	public void setF_revCode(String f_revCode) {
		this.f_revCode = f_revCode;
	}

	public String getF_revMessage() {
		return f_revMessage;
	}

	public void setF_revMessage(String f_revMessage) {
		this.f_revMessage = f_revMessage;
	}

	public String getF_revCoCode() {
		return f_revCoCode;
	}

	public void setF_revCoCode(String f_revCoCode) {
		this.f_revCoCode = f_revCoCode;
	}

	public String getF_revSeller() {
		return f_revSeller;
	}

	public void setF_revSeller(String f_revSeller) {
		this.f_revSeller = f_revSeller;
	}

	public String getF_revDate() {
		return f_revDate;
	}

	public void setF_revDate(String f_revDate) {
		this.f_revDate = f_revDate;
	}

	public String getF_revSellerName() {
		return f_revSellerName;
	}

	public void setF_revSellerName(String f_revSellerName) {
		this.f_revSellerName = f_revSellerName;
	}

	public String getF_TransUniqueCode() {
		return f_TransUniqueCode;
	}

	public void setF_TransUniqueCode(String f_TransUniqueCode) {
		this.f_TransUniqueCode = f_TransUniqueCode;
	}

	public String getF_BuyerName() {
		return f_BuyerName;
	}

	public void setF_BuyerName(String f_BuyerName) {
		this.f_BuyerName = f_BuyerName;
	}

	public String getF_ReceiptLink() {
		return f_ReceiptLink;
	}

	public void setF_ReceiptLink(String f_ReceiptLink) {
		this.f_ReceiptLink = f_ReceiptLink;
	}

	public String getF_ReciptImage() {
		return f_ReciptImage;
	}

	public void setF_ReciptImage(String f_ReciptImage) {
		this.f_ReciptImage = f_ReciptImage;
	}

	public String getF_UserID() {
		return f_UserID;
	}

	public void setF_UserID(String f_UserID) {
		this.f_UserID = f_UserID;
	}

	public String getF_StaffName() {
		return f_StaffName;
	}

	public void setF_StaffName(String f_StaffName) {
		this.f_StaffName = f_StaffName;
	}

	@Override
	public String toString() {
		return "ReceiptEntity [f_ID=" + f_ID + ", f_VanName=" + f_VanName + ", f_CompanyNo=" + f_CompanyNo + ", f_MachineCode=" + f_MachineCode + ", f_CardInputMethod=" + f_CardInputMethod + ", f_Type=" + f_Type + ", f_TypeSub=" + f_TypeSub + ", f_CardNo=" + f_CardNo + ", f_Amount=" + f_Amount + ", f_Month=" + f_Month + ", f_Tax=" + f_Tax + ", f_Service=" + f_Service + ", f_TotalAmount=" + f_TotalAmount + ", f_CouponID=" + f_CouponID + ", f_CouponDiscountRate=" + f_CouponDiscountRate + ", f_CouponDiscountAmount=" + f_CouponDiscountAmount + ", f_RequestDate=" + f_RequestDate + ", f_ApprovalCode=" + f_ApprovalCode + ", f_uMobile=" + f_uMobile + ", f_revStatus=" + f_revStatus + ", f_revCode=" + f_revCode + ", f_revMessage=" + f_revMessage + ", f_revCoCode=" + f_revCoCode + ", f_revSeller=" + f_revSeller + ", f_revDate=" + f_revDate + ", f_revSellerName=" + f_revSellerName + ", f_TransUniqueCode=" + f_TransUniqueCode + ", f_BuyerName=" + f_BuyerName + ", f_ReceiptLink=" + f_ReceiptLink + ", f_ReciptImage=" + f_ReciptImage + ", f_UserID=" + f_UserID + ", f_StaffName=" + f_StaffName + "]";
	}
	public static ReceiptEntity encrypt(ReceiptEntity receiptEntity){
		receiptEntity.f_CardNo = SecurityHelper.encrypt(receiptEntity.f_CardNo);
		//receiptEntity.f_ApprovalCode = SecurityHelper.encrypt(receiptEntity.f_ApprovalCode);
		return receiptEntity;
	}
	
	public static ReceiptEntity decrypt(ReceiptEntity receiptEntity){
		receiptEntity.f_CardNo = SecurityHelper.decrypt(receiptEntity.f_CardNo);
		//receiptEntity.f_ApprovalCode = SecurityHelper.decrypt(receiptEntity.f_ApprovalCode);
		return receiptEntity;
	}
	
	public static List<ReceiptEntity> encryptList(List<ReceiptEntity> entities){
		List<ReceiptEntity> entities2 = new ArrayList<ReceiptEntity>();
		for(int i=0;i<entities.size();i++){
			ReceiptEntity receiptEntity  = encrypt(entities.get(i));
			entities2.add(receiptEntity);
		}
		return entities2;
	}
	
	public static List<ReceiptEntity> decryptList(List<ReceiptEntity> entities){
		List<ReceiptEntity> entities2 = new ArrayList<ReceiptEntity>();
		for(int i=0;i<entities.size();i++){
			ReceiptEntity receiptEntity  = decrypt(entities.get(i));
			entities2.add(receiptEntity);
		}
		return entities2;
	}
	
}
