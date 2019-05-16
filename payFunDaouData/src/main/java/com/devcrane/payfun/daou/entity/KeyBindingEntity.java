package com.devcrane.payfun.daou.entity;

public class KeyBindingEntity {
	String f_ID;
	String f_CompanyNo;
	String f_MachineNo;
	String f_DeviceNo;
	String f_FirmwareVersion;
	String CREATE_DT;
	String CREATE_UID;
	String f_PinKsn;
	String f_TrackKsn;
	String f_EmvKsn;
	String f_Uid;
	String f_Csn;
	String f_ext1;
	String f_ext2;
	String f_ext3;
	
	public KeyBindingEntity() {
		super();
		this.f_ID = "";
		this.f_CompanyNo = "";
		this.f_MachineNo = "";
		this.f_DeviceNo = "";
		this.f_FirmwareVersion = "";
		this.CREATE_DT = "";
		this.CREATE_UID = "";
		this.f_PinKsn = "";
		this.f_TrackKsn = "";
		this.f_EmvKsn="";
		this.f_Uid = "";
		this.f_Csn = "";
		this.f_ext1 = "";
		this.f_ext2 = "";
		this.f_ext3 = "";
	}
	public KeyBindingEntity(String emptyVal) {
		super();
//		this.f_ID = "";
//		this.f_CompanyNo = "";
//		this.f_MachineNo = "";
//		this.f_DeviceNo = "";
//		this.f_FirmwareVersion = "";
//		this.CREATE_DT = "";
//		this.CREATE_UID = "";
//		this.f_PinKsn = "";
//		this.f_TrackKsn = "";
//		this.f_EmvKsn="";
//		this.f_Uid = "";
//		this.f_Csn = "";
//		this.f_ext1 = "";
//		this.f_ext2 = "";
//		this.f_ext3 = "";
	}
//	public KeyBindingEntity(KSNEntity ksnEntity) {
//		this.f_PinKsn = ksnEntity.getPinKsn();
//		this.f_TrackKsn = ksnEntity.getTrackKsn();
//		this.f_EmvKsn = ksnEntity.getEmvKsn();
//		this.f_Csn = ksnEntity.getCsn();
//		this.f_Uid = ksnEntity.getUid();
//	}
	public KeyBindingEntity(int create) {
		super();
		this.f_ID = "f_ID";
		this.f_CompanyNo = "f_CompanyNo";
		this.f_MachineNo = "f_MachineNo";
		this.f_DeviceNo = "f_DeviceNo";
		this.f_FirmwareVersion = "f_FirmwareVersion";
		this.CREATE_DT = "CREATE_DT";
		this.CREATE_UID = "CREATE_UID";
		this.f_PinKsn = "f_PinKsn";
		this.f_TrackKsn = "f_TrackKsn";
		this.f_EmvKsn = "f_EmvKsn";
		this.f_Uid = "f_Uid";
		this.f_Csn = "f_Csn";
		this.f_ext1 = "f_ext1";
		this.f_ext2 = "f_ext2";
		this.f_ext3 = "f_ext3";
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

	public String getF_MachineNo() {
		return f_MachineNo;
	}

	public void setF_MachineNo(String f_MachineNo) {
		this.f_MachineNo = f_MachineNo;
	}

	public String getF_DeviceNo() {
		return f_DeviceNo;
	}

	public void setF_DeviceNo(String f_DeviceNo) {
		this.f_DeviceNo = f_DeviceNo;
	}

	public String getF_FirmwareVersion() {
		return f_FirmwareVersion;
	}

	public void setF_FirmwareVersion(String f_FirmwareVersion) {
		this.f_FirmwareVersion = f_FirmwareVersion;
	}

	public String getCREATE_DT() {
		return CREATE_DT;
	}

	public void setCREATE_DT(String cREATE_DT) {
		CREATE_DT = cREATE_DT;
	}

	public String getCREATE_UID() {
		return CREATE_UID;
	}

	public void setCREATE_UID(String cREATE_UID) {
		CREATE_UID = cREATE_UID;
	}

	public String getF_PinKsn() {
		return f_PinKsn;
	}

	public void setF_PinKsn(String f_PinKsn) {
		this.f_PinKsn = f_PinKsn;
	}

	public String getF_TrackKsn() {
		return f_TrackKsn;
	}

	public void setF_TrackKsn(String f_TrackKsn) {
		this.f_TrackKsn = f_TrackKsn;
	}

	public String getF_EmvKsn() {
		return f_EmvKsn;
	}

	public void setF_EmvKsn(String f_EmvKsn) {
		this.f_EmvKsn = f_EmvKsn;
	}

	public String getF_Uid() {
		return f_Uid;
	}

	public void setF_Uid(String f_Uid) {
		this.f_Uid = f_Uid;
	}

	public String getF_Csn() {
		return f_Csn;
	}

	public void setF_Csn(String f_Csn) {
		this.f_Csn = f_Csn;
	}

	public String getF_ext1() {
		return f_ext1;
	}

	public void setF_ext1(String f_ext1) {
		this.f_ext1 = f_ext1;
	}

	public String getF_ext2() {
		return f_ext2;
	}

	public void setF_ext2(String f_ext2) {
		this.f_ext2 = f_ext2;
	}

	public String getF_ext3() {
		return f_ext3;
	}

	public void setF_ext3(String f_ext3) {
		this.f_ext3 = f_ext3;
	}

	@Override
	public String toString() {
		return "KeyBindingEntity [f_ID=" + f_ID + ", f_CompanyNo="
				+ f_CompanyNo + ", f_MachineNo=" + f_MachineNo
				+ ", f_DeviceNo=" + f_DeviceNo + ", f_FirmwareVersion="
				+ f_FirmwareVersion + ", CREATE_DT=" + CREATE_DT
				+ ", CREATE_UID=" + CREATE_UID + ", f_PinKsn=" + f_PinKsn
				+ ", f_TrackKsn=" + f_TrackKsn + ", f_EmvKsn=" + f_EmvKsn
				+ ", f_Uid=" + f_Uid + ", f_Csn=" + f_Csn + ", f_ext1="
				+ f_ext1 + ", f_ext2=" + f_ext2 + ", f_ext3=" + f_ext3 + "]";
	}
	
	

}
