package com.devcrane.payfun.daou.entity;

public class UserBalanceEntity {
	private String f_ID;
	private String f_UserID;
	private String f_PayDate;
	private String f_ServiceStartDate;
	private String f_PurchaseMonthNo;
	private String f_ServiceBeExpiredDate;
	private String f_PurchaseAmount;
	private String CREATE_UID;
	private String CREATE_DT;
	private String UPDATE_UID;
	private String UPDATE_DT;
	
	public UserBalanceEntity(int key) {
		f_ID = "f_ID";
		f_UserID = "f_UserID";
		f_PayDate = "f_PayDate";
		f_ServiceStartDate = "f_ServiceStartDate";
		f_PurchaseMonthNo = "f_PurchaseMonthNo";
		f_ServiceBeExpiredDate = "f_ServiceBeExpiredDate";
		f_PurchaseAmount = "f_PurchaseAmount";
		CREATE_UID = "CREATE_UID";
		CREATE_DT = "CREATE_DT";
		UPDATE_UID = "UPDATE_UID";
		UPDATE_DT = "UPDATE_DT";
	}
	
	public UserBalanceEntity() {
		super();
	}

	public String getF_ID() {
		return f_ID;
	}

	public void setF_ID(String f_ID) {
		this.f_ID = f_ID;
	}

	public String getF_UserID() {
		return f_UserID;
	}

	public void setF_UserID(String f_UserID) {
		this.f_UserID = f_UserID;
	}

	public String getF_PayDate() {
		return f_PayDate;
	}

	public void setF_PayDate(String f_PayDate) {
		this.f_PayDate = f_PayDate;
	}

	public String getF_ServiceStartDate() {
		return f_ServiceStartDate;
	}

	public void setF_ServiceStartDate(String f_ServiceStartDate) {
		this.f_ServiceStartDate = f_ServiceStartDate;
	}

	public String getF_PurchaseMonthNo() {
		return f_PurchaseMonthNo;
	}

	public void setF_PurchaseMonthNo(String f_PurchaseMonthNo) {
		this.f_PurchaseMonthNo = f_PurchaseMonthNo;
	}

	public String getF_ServiceBeExpiredDate() {
		return f_ServiceBeExpiredDate;
	}

	public void setF_ServiceBeExpiredDate(String f_ServiceBeExpiredDate) {
		this.f_ServiceBeExpiredDate = f_ServiceBeExpiredDate;
	}

	public String getF_PurchaseAmount() {
		return f_PurchaseAmount;
	}

	public void setF_PurchaseAmount(String f_PurchaseAmount) {
		this.f_PurchaseAmount = f_PurchaseAmount;
	}

	public String getCREATE_UID() {
		return CREATE_UID;
	}

	public void setCREATE_UID(String cREATE_UID) {
		CREATE_UID = cREATE_UID;
	}

	public String getCREATE_DT() {
		return CREATE_DT;
	}

	public void setCREATE_DT(String cREATE_DT) {
		CREATE_DT = cREATE_DT;
	}

	public String getUPDATE_UID() {
		return UPDATE_UID;
	}

	public void setUPDATE_UID(String uPDATE_UID) {
		UPDATE_UID = uPDATE_UID;
	}

	public String getUPDATE_DT() {
		return UPDATE_DT;
	}

	public void setUPDATE_DT(String uPDATE_DT) {
		UPDATE_DT = uPDATE_DT;
	}
}
