package com.devcrane.payfun.daou.entity;

public class BranchEntity {
	private String f_BranchId;
	private String f_BranchName;
	private String f_Status;
	public BranchEntity(int create) {
		this.f_BranchId = "f_BranchId";
		this.f_BranchName = "f_BranchName";
		this.f_Status = "f_Status";
	}
	public String getF_BranchId() {
		return f_BranchId;
	}
	public void setF_BranchId(String f_BranchId) {
		this.f_BranchId = f_BranchId;
	}
	public String getF_BranchName() {
		return f_BranchName;
	}
	public void setF_BranchName(String f_BranchName) {
		this.f_BranchName = f_BranchName;
	}
	public String getF_Status() {
		return f_Status;
	}
	public void setF_Status(String f_Status) {
		this.f_Status = f_Status;
	}
	@Override
	public String toString() {
		return "BranchEntity [f_BranchId=" + f_BranchId + ", f_BranchName=" + f_BranchName + ", f_Status=" + f_Status + "]";
	}
}
