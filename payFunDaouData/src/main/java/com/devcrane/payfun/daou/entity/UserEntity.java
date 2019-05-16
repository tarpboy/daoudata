package com.devcrane.payfun.daou.entity;

public class UserEntity {
	private String f_ID; //int(11)
	private String f_Email; //varchar(50)
	private String f_Password; //varchar(50)
	private String f_Name; //varchar(50)
	private String f_CompanyName; //varchar(255)
	private String f_CompanyNo; //varchar(255)
	private String f_CompanyPhone; //varchar(255)
	private String f_Address; //varchar(255)
	private String f_AddressDetail; //varchar(255)
	private String f_PartnerCode; //varchar(255)
	private String CREATE_UID; //varchar(50)
	private String CREATE_DT; //datetime
	private String UPDATE_UID; //varchar(50)
	private String UPDATE_DT; //datetime
	private String f_ParentID; //varchar(50)
	private String f_BranchId;
	private String f_Status;
	private String f_Mobile_NO;

	
	public UserEntity(int key) {
		this.f_ID = "f_ID";
		this.f_Email = "f_Email";
		this.f_Password = "f_Password";
		this.f_Name = "f_Name";
		this.f_CompanyName = "f_CompanyName";
		this.f_CompanyNo = "f_CompanyNo";
		this.f_CompanyPhone = "f_CompanyPhone";
		this.f_Address = "f_Address";
		this.f_AddressDetail = "f_AddressDetail";
		this.f_PartnerCode = "f_PartnerCode";
		CREATE_UID = "CREATE_UID";
		CREATE_DT = "CREATE_DT";
		UPDATE_UID = "UPDATE_UID";
		UPDATE_DT = "UPDATE_DT";
		this.f_ParentID = "f_ParentID";
		this.f_BranchId = "f_BranchId";
		this.f_Status = "f_Status";
		this.f_Mobile_NO = "f_Mobile_NO";
	}
	
	public UserEntity() {
		super();
	}

	public String getF_ID() {
		return f_ID;
	}
	public void setF_ID(String f_ID) {
		this.f_ID = f_ID;
	}
	public String getF_Email() {
		return f_Email;
	}
	public void setF_Email(String f_Email) {
		this.f_Email = f_Email;
	}
	public String getF_Password() {
		return f_Password;
	}
	public void setF_Password(String f_Password) {
		this.f_Password = f_Password;
	}
	public String getF_Name() {
		return f_Name;
	}
	public void setF_Name(String f_Name) {
		this.f_Name = f_Name;
	}
	public String getF_CompanyName() {
		return f_CompanyName;
	}
	public void setF_CompanyName(String f_CompanyName) {
		this.f_CompanyName = f_CompanyName;
	}
	public String getF_CompanyNo() {
		return f_CompanyNo;
	}
	public void setF_CompanyNo(String f_CompanyNo) {
		this.f_CompanyNo = f_CompanyNo;
	}
	public String getF_CompanyPhone() {
		return f_CompanyPhone;
	}
	public void setF_CompanyPhone(String f_CompanyPhone) {
		this.f_CompanyPhone = f_CompanyPhone;
	}
	public String getF_Address() {
		return f_Address;
	}
	public void setF_Address(String f_Address) {
		this.f_Address = f_Address;
	}
	public String getF_AddressDetail() {
		return f_AddressDetail;
	}
	public void setF_AddressDetail(String f_AddressDetail) {
		this.f_AddressDetail = f_AddressDetail;
	}
	public String getF_PartnerCode() {
		return f_PartnerCode;
	}
	public void setF_PartnerCode(String f_PartnerCode) {
		this.f_PartnerCode = f_PartnerCode;
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
	public String getF_ParentID() {
		return f_ParentID;
	}
	public void setF_ParentID(String f_ParentID) {
		this.f_ParentID = f_ParentID;
	}
	public String getF_BranchId() {
		return f_BranchId;
	}
	public void setF_BranchId(String f_BranchId) {
		this.f_BranchId = f_BranchId;
	}

	public String getF_Status() {
		return f_Status;
	}

	public void setF_Status(String f_Status) {
		this.f_Status = f_Status;
	}

	public String getF_Mobile_NO() {
		return f_Mobile_NO;
	}

	public void setF_Mobile_NO(String f_Mobile_NO) {
		this.f_Mobile_NO = f_Mobile_NO;
	}

	@Override
	public String toString() {
		return "UserEntity{" +
				"f_ID='" + f_ID + '\'' +
				", f_Email='" + f_Email + '\'' +
				", f_Password='" + f_Password + '\'' +
				", f_Name='" + f_Name + '\'' +
				", f_CompanyName='" + f_CompanyName + '\'' +
				", f_CompanyNo='" + f_CompanyNo + '\'' +
				", f_CompanyPhone='" + f_CompanyPhone + '\'' +
				", f_Address='" + f_Address + '\'' +
				", f_AddressDetail='" + f_AddressDetail + '\'' +
				", f_PartnerCode='" + f_PartnerCode + '\'' +
				", CREATE_UID='" + CREATE_UID + '\'' +
				", CREATE_DT='" + CREATE_DT + '\'' +
				", UPDATE_UID='" + UPDATE_UID + '\'' +
				", UPDATE_DT='" + UPDATE_DT + '\'' +
				", f_ParentID='" + f_ParentID + '\'' +
				", f_BranchId='" + f_BranchId + '\'' +
				", f_Status='" + f_Status + '\'' +
				", f_Mobile_NO='" + f_Mobile_NO + '\'' +
				'}';
	}
}
