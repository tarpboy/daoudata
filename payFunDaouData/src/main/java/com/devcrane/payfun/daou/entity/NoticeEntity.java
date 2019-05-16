package com.devcrane.payfun.daou.entity;

public class NoticeEntity {
	private String f_ID; //int(11)
	private String f_Type; //varchar(10)
	private String f_Titile; //varchar(255)
	private String f_Content; //longtext
	private String f_IsActive; //bit(1)
	private String CREATE_UID; //varchar(50)
	private String CREATE_DT; //datetime
	private String UPDATE_UID; //varchar(50)
	private String UPDATE_DT; //datetime
	
	public NoticeEntity(int key) {
		super();
		this.f_ID = "f_ID";
		this.f_Type = "f_Type";
		this.f_Titile = "f_Titile";
		this.f_Content = "f_Content";
		this.f_IsActive = "f_IsActive";
		CREATE_UID = "CREATE_UID";
		CREATE_DT = "CREATE_DT";
		UPDATE_UID = "UPDATE_UID";
		UPDATE_DT = "UPDATE_DT";
	}
	
	public NoticeEntity() {
		super();
	}

	public String getF_ID() {
		return f_ID;
	}

	public void setF_ID(String f_ID) {
		this.f_ID = f_ID;
	}

	public String getF_Type() {
		return f_Type;
	}

	public void setF_Type(String f_Type) {
		this.f_Type = f_Type;
	}

	public String getF_Titile() {
		return f_Titile;
	}

	public void setF_Titile(String f_Titile) {
		this.f_Titile = f_Titile;
	}

	public String getF_Content() {
		return f_Content;
	}

	public void setF_Content(String f_Content) {
		this.f_Content = f_Content;
	}

	public String getF_IsActive() {
		return f_IsActive;
	}

	public void setF_IsActive(String f_IsActive) {
		this.f_IsActive = f_IsActive;
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
