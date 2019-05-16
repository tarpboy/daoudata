package com.devcrane.payfun.daou.entity;

public class CompanyEntity {
	private String f_ID;
	private String f_CompanyNo;
	private String f_MachineCode;
	private String f_PhoneNo;
	private String f_PhoneCode;
	private String f_CompanyName;
	private String f_CompanyAddress;
	private String f_CompanyPhoneNo;
	private String f_CompanyOwnerName;
	private String f_ResellerName;
	private String f_ResellerPhoneNo;
	private String f_VanName;
	private String f_VanPhoneNo;
	private String f_TaxRate;
	private boolean f_WithTax;
	private String f_ServiceTaxRate;
	private String f_RegDate;
	private String f_UserID;
	private String CREATE_UID;
	private String CREATE_DT;
	private String UPDATE_UID;
	private String UPDATE_DT;
	
	
	
	public CompanyEntity() {
		super();
	}
	public CompanyEntity(int create) {
		super();
		this.f_ID = "f_ID";
		this.f_CompanyNo = "f_CompanyNo";
		this.f_MachineCode = "f_MachineCode";
		this.f_PhoneNo = "f_PhoneNo";
		this.f_PhoneCode = "f_PhoneCode";
		this.f_CompanyName = "f_CompanyName";
		this.f_CompanyAddress = "f_CompanyAddress";
		this.f_CompanyPhoneNo = "f_CompanyPhoneNo";
		this.f_CompanyOwnerName = "f_CompanyOwnerName";
		this.f_ResellerName = "f_ResellerName";
		this.f_ResellerPhoneNo = "f_ResellerPhoneNo";
		this.f_VanName = "f_VanName";
		this.f_VanPhoneNo = "f_VanPhoneNo";
		this.f_TaxRate = "f_TaxRate";
//		this.f_WithTax = "f_WithTax";
		this.f_ServiceTaxRate = "f_ServiceTaxRate";
		this.f_RegDate = "f_RegDate";
		this.f_UserID = "f_UserID";
		CREATE_UID = "CREATE_UID";
		CREATE_DT = "CREATE_DT";
		UPDATE_UID = "UPDATE_UID";
		UPDATE_DT = "UPDATE_DT";
	}
	public String getF_ID() {
		return f_ID;
	}
	public void setF_ID(String f_ID) {
		this.f_ID = f_ID;
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
	public String getF_PhoneNo() {
		return f_PhoneNo;
	}
	public void setF_PhoneNo(String f_PhoneNo) {
		this.f_PhoneNo = f_PhoneNo;
	}
	public String getF_PhoneCode() {
		return f_PhoneCode;
	}
	public void setF_PhoneCode(String f_PhoneCode) {
		this.f_PhoneCode = f_PhoneCode;
	}
	public String getF_CompanyName() {
		return f_CompanyName;
	}
	public void setF_CompanyName(String f_CompanyName) {
		this.f_CompanyName = f_CompanyName;
	}
	public String getF_CompanyAddress() {
		return f_CompanyAddress;
	}
	public void setF_CompanyAddress(String f_CompanyAddress) {
		this.f_CompanyAddress = f_CompanyAddress;
	}
	public String getF_CompanyPhoneNo() {
		return f_CompanyPhoneNo;
	}
	public void setF_CompanyPhoneNo(String f_CompanyPhoneNo) {
		this.f_CompanyPhoneNo = f_CompanyPhoneNo;
	}
	public String getF_CompanyOwnerName() {
		return f_CompanyOwnerName;
	}
	public void setF_CompanyOwnerName(String f_CompanyOwnerName) {
		this.f_CompanyOwnerName = f_CompanyOwnerName;
	}
	public String getF_ResellerName() {
		return f_ResellerName;
	}
	public void setF_ResellerName(String f_ResellerName) {
		this.f_ResellerName = f_ResellerName;
	}
	public String getF_ResellerPhoneNo() {
		return f_ResellerPhoneNo;
	}
	public void setF_ResellerPhoneNo(String f_ResellerPhoneNo) {
		this.f_ResellerPhoneNo = f_ResellerPhoneNo;
	}
	public String getF_VanName() {
		return f_VanName;
	}
	public void setF_VanName(String f_VanName) {
		this.f_VanName = f_VanName;
	}
	public String getF_VanPhoneNo() {
		return f_VanPhoneNo;
	}
	public void setF_VanPhoneNo(String f_VanPhoneNo) {
		this.f_VanPhoneNo = f_VanPhoneNo;
	}
	public String getF_TaxRate() {
		return f_TaxRate;
	}
	public void setF_TaxRate(String f_TaxRate) {
		this.f_TaxRate = f_TaxRate;
	}
	public boolean getF_WithTax() {
		return f_WithTax;
	}
	public void setF_WithTax(boolean f_WithTax) {
		this.f_WithTax = f_WithTax;
	}
	public String getF_ServiceTaxRate() {
		return f_ServiceTaxRate;
	}
	public void setF_ServiceTaxRate(String f_ServiceTaxRate) {
		this.f_ServiceTaxRate = f_ServiceTaxRate;
	}
	public String getF_RegDate() {
		return f_RegDate;
	}
	public void setF_RegDate(String f_RegDate) {
		this.f_RegDate = f_RegDate;
	}
	public String getF_UserID() {
		return f_UserID;
	}
	public void setF_UserID(String f_UserID) {
		this.f_UserID = f_UserID;
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
		return "CompanyEntity [f_ID=" + f_ID + ", f_CompanyNo=" + f_CompanyNo + ", f_MachineCode=" + f_MachineCode + ", f_PhoneNo=" + f_PhoneNo + ", f_PhoneCode=" + f_PhoneCode + ", f_CompanyName=" + f_CompanyName + ", f_CompanyAddress=" + f_CompanyAddress + ", f_CompanyPhoneNo=" + f_CompanyPhoneNo + ", f_CompanyOwnerName=" + f_CompanyOwnerName + ", f_ResellerName=" + f_ResellerName + ", f_ResellerPhoneNo=" + f_ResellerPhoneNo + ", f_VanName=" + f_VanName + ", f_VanPhoneNo=" + f_VanPhoneNo + ", f_TaxRate=" + f_TaxRate + ", f_WithTax=" + f_WithTax + ", f_ServiceTaxRate=" + f_ServiceTaxRate + ", f_RegDate=" + f_RegDate + ", f_UserID=" + f_UserID + ", CREATE_UID=" + CREATE_UID + ", CREATE_DT=" + CREATE_DT + ", UPDATE_UID=" + UPDATE_UID + ", UPDATE_DT=" + UPDATE_DT + "]";
	}
}
