package com.devcrane.android.lib.entity;

import java.util.Hashtable;

import com.devcrane.android.lib.utility.BHelper;
import com.devcrane.android.lib.utility.VanFormatHelper;


public class KeyExchangeResultEntity {
	String randValue;
	String encRandValue;
	String ksn;
	public KeyExchangeResultEntity(){
		randValue = encRandValue = ksn = "";
	}
	public KeyExchangeResultEntity(String randValue, String encRandValue, String ksn){
		this.randValue = randValue;
		this.encRandValue = encRandValue;
		this.ksn = ksn;
	}
	public String getRandValue() {
		return randValue;
	}
	public void setRandValue(String randValue) {
		this.randValue = randValue;
	}
	public String getEncRandValue() {
		return encRandValue;
	}
	public void setEncRandValue(String encRandValue) {
		this.encRandValue = encRandValue;
	}
	public String getKsn() {
		return ksn;
	}
	public void setKsn(String ksn) {
		this.ksn = ksn;
	}
	public static KeyExchangeResultEntity parse(Hashtable<String, String> data){
		String respData = data.get("data");
		BHelper.db("KeyExchangeResultEntity data:"+ respData);
		VanFormatHelper vfHelper = new VanFormatHelper(respData);
		String randValue = respData;//vfHelper.HECSTR(32, 0);
		String encRandValue = vfHelper.HECSTR(512, 32);
//		byte[] ksnByte = Helper.hexStringToByteArray(vfHelper.HECSTR(20, 544));
//		BHelper.db("ksn size:"+ksnByte.length);
//		String ksn = new String(ksnByte);
		String ksn = vfHelper.HECSTR(20, 544);
		return new KeyExchangeResultEntity(randValue, encRandValue, ksn);
	}
	public static void testParseKeyExchangeResult(){
		Hashtable<String, String> data = new Hashtable<String,String>();
		String respData = "02E4953AC3D84CF33B6E31022E3053AF2B705840748DE1EAE623E42D4DD8ED77D2255F3D375F3F9F7603ECB0452F1F45A00C3747D2B4F0431445707260F8858EE9FE33352B780BFC8EF6EBE5ECA5D79FB5ACED0542EB5EEAD50384C187ECA5A71DE6B51DA31B11298B011C3B38D1BC5611C080E0E9D27F46DE07A8915169A68D5A9944FEBFDBBC1A17AA1DD295864913EE26AA5DDA03F3154804F98EC9D0FCFF5AEFC8C7DDBCB6578F7A038D0E128682A89574E60FCCEBE5AA52387120BD4D2DD5835917835F24937497F03BCC711A5D95087ACF245B173620BA2FE94C08A53782B61CA830F56B47059A89BEAD64CF12BC0DC272E6FC777CCAB1FAA83BA5A3D3580D5F11C6EFC58499DD0F5BD19948F04B41420015000100000C";
		data.put("data", respData);
		KeyExchangeResultEntity resultEntity = parse(data);
		BHelper.db(resultEntity.toString());
		
	}
	public String toString(){
		String ret="KeyExchangeResultEntity\n";
		ret+= "\n randValue: "+ randValue;
		ret+= "\n encRandValue: "+encRandValue;
		ret+= "\n ksn: "+ksn;
		return ret;
	}
}
