package com.devcrane.android.lib.entity;

public class KSNEntity {
	String pinKsn;
	String trackKsn;
	String emvKsn;
	String uid;
	String csn;
	
	public KSNEntity(){
		pinKsn = trackKsn = emvKsn = uid = csn = "";
	}
	public KSNEntity(String pinKsn, String trackKsn, String emvKsn, String uid, String csn){
		this.pinKsn = pinKsn;
		this.trackKsn = trackKsn;
		this.emvKsn = emvKsn;
		this.uid = uid;
		this.csn =csn;
	}
	public String getPinKsn() {
		return pinKsn;
	}
	public void setPinKsn(String pinKsn) {
		this.pinKsn = pinKsn;
	}
	public String getTrackKsn() {
		return trackKsn;
	}
	public void setTrackKsn(String trackKsn) {
		this.trackKsn = trackKsn;
	}
	public String getEmvKsn() {
		return emvKsn;
	}
	public void setEmvKsn(String emvKsn) {
		this.emvKsn = emvKsn;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getCsn() {
		return csn;
	}
	public void setCsn(String csn) {
		this.csn = csn;
	}
	public String getSerial(){
		return "PF"+ (pinKsn.length()>15?pinKsn.substring(6, 14):"");
	}
	public String toString(){
		String ret="KSNEntity\n";
		ret+= "\n pinKsn: "+ pinKsn;
		ret+= "\n trackKsn: "+trackKsn;
		ret+= "\n emvKsn: "+emvKsn;
		ret+= "\n uid: "+uid;
		ret+= "\n csn: "+csn;
		return ret;
	}
	
	
	
}
