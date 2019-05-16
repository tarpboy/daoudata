package com.devcrane.payfun.daou.entity;

public class CouponEntity {

	private String f_ID;
	private String f_CouponName;
	private String f_CouponID;
	private String f_DiscountRate;
	private String f_IsActive;
	private String f_VanName;
	private String f_CompanyNo;
	private String f_MachineCode;
	private String CREATE_UID;
	private String CREATE_DT;
	private String UPDATE_UID;
	private String UPDATE_DT;
	
	public String getF_ID() {
		return f_ID;
	}

	public void setF_ID(String f_ID) {
		this.f_ID = f_ID;
	}

	public String getF_CouponName() {
		return f_CouponName;
	}

	public void setF_CouponName(String f_CouponName) {
		this.f_CouponName = f_CouponName;
	}

	public String getF_CouponID() {
		return f_CouponID;
	}

	public void setF_CouponID(String f_CouponID) {
		this.f_CouponID = f_CouponID;
	}

	public String getF_DiscountRate() {
		return f_DiscountRate;
	}

	public void setF_DiscountRate(String f_DiscountRate) {
		this.f_DiscountRate = f_DiscountRate;
	}

	public String getF_IsActive() {
		return f_IsActive;
	}

	public void setF_IsActive(String f_IsActive) {
		this.f_IsActive = f_IsActive;
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

	@Override
	public String toString() {
		return "CouponEntity [f_ID=" + f_ID + ", f_CouponName=" + f_CouponName + ", f_CouponID=" + f_CouponID + ", f_DiscountRate=" + f_DiscountRate + ", f_IsActive=" + f_IsActive + ", f_VanName=" + f_VanName + ", f_CompanyNo=" + f_CompanyNo + ", f_MachineCode=" + f_MachineCode + ", CREATE_UID=" + CREATE_UID + ", CREATE_DT=" + CREATE_DT + ", UPDATE_UID=" + UPDATE_UID + ", UPDATE_DT=" + UPDATE_DT + "]";
	}
}
