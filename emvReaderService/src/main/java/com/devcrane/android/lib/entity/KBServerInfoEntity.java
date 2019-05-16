package com.devcrane.android.lib.entity;

import com.devcrane.android.lib.emvreader.JTNet;
import com.devcrane.android.lib.utility.VanFormatHelper;

public class KBServerInfoEntity {
	String respCode;
	String respMsg;
	String respDateTime;
	String respPublicKeyVers;
	String randValue;
	String hashValue;
	String signValue;
	
	public KBServerInfoEntity(){
		respCode = respMsg = respDateTime = respPublicKeyVers = randValue = hashValue = signValue = "";
	}
	public KBServerInfoEntity(String respCode, String respMsg, String respDateTime, String respPublicKeyVers, String randValue, String hashValue, String signValue){
		this.respCode = respCode;
		this.respMsg = respMsg;
		this.respDateTime = respDateTime;
		this.respPublicKeyVers = respPublicKeyVers;
		this.randValue = randValue;
		this.hashValue = hashValue;
		this.signValue = signValue;
	}
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespMsg() {
		return respMsg;
	}
	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}
	public String getRespDateTime() {
		return respDateTime;
	}
	public void setRespDateTime(String respDateTime) {
		this.respDateTime = respDateTime;
	}
	public String getRespPublicKeyVers() {
		return respPublicKeyVers;
	}
	public void setRespPublicKeyVers(String respPublicKeyVers) {
		this.respPublicKeyVers = respPublicKeyVers;
	}
	public String getRandValue() {
		return randValue;
	}
	public void setRandValue(String randValue) {
		this.randValue = randValue;
	}
	public String getHashValue() {
		return hashValue;
	}
	public void setHashValue(String hashValue) {
		this.hashValue = hashValue;
	}
	public String getSignValue() {
		return signValue;
	}
	public void setSignValue(String signValue) {
		this.signValue = signValue;
	}
	public static KBServerInfoEntity parse(String data){
		KBServerInfoEntity bindingServerInfo;
		if(JTNet.getBytesSize(data)<JTNet.SIZE_RES_KEYBINDING_SERVERINFO)
			return new KBServerInfoEntity();
		VanFormatHelper vfHelper = new VanFormatHelper(data);
		String respCode = vfHelper.HECSTR(4, 0);
		String respMsg = vfHelper.HECSTR(36, 4);
		String respDateTime = vfHelper.HECSTR(14, 40);
		String respPublicKeyVers = vfHelper.HECSTR(2, 54);
		String randValue = vfHelper.HECSTR(64, 56);
		String hashValue = vfHelper.HECSTR(64, 120);
		String signValue = vfHelper.HECSTR(512, 184);
		bindingServerInfo = new KBServerInfoEntity(respCode, respMsg, respDateTime, respPublicKeyVers, randValue, hashValue, signValue);
		return bindingServerInfo;
	}
	public String toString(){
		String ret="KBServerInfoEntity\n";
		ret+= "\n respCode: "+ respCode;
		ret+= "\n respMsg: "+respMsg;
		ret+= "\n respDateTime: "+respDateTime;
		ret+= "\n respPublicKeyVers: "+ respPublicKeyVers;
		ret+= "\n randValue: "+randValue;
		ret+= "\n hashValue"+ hashValue;
		ret+= "\n signValue: "+ signValue;
		return ret;
	}
	
}
