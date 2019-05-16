package com.devcrane.android.lib.entity;

import com.devcrane.android.lib.emvreader.JTNet;
import com.devcrane.android.lib.utility.VanFormatHelper;


public class KBDUPTEntity {
	String responseCode;
	String responseMsg;
	int encryptedDataLen;
	String encryptedData;
	public KBDUPTEntity(){
		responseCode = responseMsg = encryptedData = "";
		encryptedDataLen  =0;
	}
	public KBDUPTEntity(String respCode, String respMsg, int encryptedDataLen, String encryptedData){
		this.responseCode = respCode;
		this.responseMsg = respMsg;
		this.encryptedDataLen = encryptedDataLen;
		this.encryptedData = encryptedData;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseMsg() {
		return responseMsg;
	}
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	public int getEncryptedDataLen() {
		return encryptedDataLen;
	}
	public void setEncryptedDataLen(int encryptedDataLen) {
		this.encryptedDataLen = encryptedDataLen;
	}
	public String getEncryptedData() {
		return encryptedData;
	}
	public void setEncryptedData(String encryptedData) {
		this.encryptedData = encryptedData;
	}
	public static KBDUPTEntity parse(String respData){
		if(JTNet.getBytesSize(respData)<JTNet.SIZE_RES_KEYBINDING)
			return new KBDUPTEntity();
		VanFormatHelper vfHelper = new VanFormatHelper(respData);
		String responseCode = vfHelper.HECSTR(4, 0);
		String responseMsg = vfHelper.HECSTR(36, 4);
		int encryptedDataLen = Integer.parseInt(vfHelper.HECSTR(4, 40));
		String encryptedData =  vfHelper.HECSTR(encryptedDataLen, 44);
		KBDUPTEntity bindingEntity=new KBDUPTEntity(responseCode,responseMsg,encryptedDataLen,encryptedData);
		return bindingEntity;
	}
	
	public String toString(){
		String ret="KBDUPTEntity\n";
		ret+= "\n responseCode: "+ responseCode;
		ret+= "\n responseMsg: "+responseMsg;
		ret+= "\n encryptedDataLen: "+encryptedDataLen;
		ret+= "\n encryptedData: "+ encryptedData;
		return ret;
	}
}
