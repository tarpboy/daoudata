package com.devcrane.android.lib.emvreader;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.R;
import android.R.bool;


import com.bbpos.bbdevice.BBDeviceController;
import com.devcrane.android.lib.utility.BHelper;
import com.devcrane.android.lib.utility.Helper;

public class EmvUtils {
	public static String	EMV_TEST = "C408457972FFFFFF5069C28201884F07A0000000031010500B56495341204352454449545A08457972439854506982025C008407A00000000310108E1000000000000000001E03420341031F00950502000080009A031601089B02E8009C01005F20164E53574C2F494B4B4B484653202020202020202020205F24031906305F25031408015F2A0204105F300202015F3401019F01060000000000019F02060000000015009F03060000000000009F0607A00000000310109F0702FF009F0902008C9F0D05F050BC88009F0E0500000000009F0F05F070BC98009F100706020A03A020009F120F4B4D435F564953415F4352454449549F160F3132333435363738393031323334359F1A0204109F1C0831313232333334349F1E0831323334353637389F21031454159F2608E64E88774DA763049F2701809F33036028C89F34031E03009F3501219F360201F59F370479A2B9B69F3901059F40056E0000A0019F4104000000379F4E0850462D35313030529F1F1C313235343620202020202020202020202030303938313030303030309F4502DAC5C70A08504615120001000025C830AF9B375528692B2B6470401FA4942D694C7DBB7F47519545727E3D87D124A84BBEFB911F32EB56D047BF888A17D74DBADF8315504346424746524941415141414A612B624E31556F615373725A484241483653554C576C4D6662742F5231475652584A2B505966524A4B684C76767552487A4C72567442487634694B4639644E75673D3D4F07A00000000310105A08457972439854506982025C008407A0000000031010950502000080009A031601089B02E8009C01005F20164E53574C2F494B4B4B484653202020202020202020205F24031906305F2A0204105F3401019F02060000000015009F03060000000000009F0607A00000000310109F0702FF009F0902008C9F0D05F050BC88009F0E0500000000009F0F05F070BC98009F100706020A03A020009F120F4B4D435F564953415F4352454449549F1A0204109F1C0831313232333334349F2608E64E88774DA763049F2701809F33036028C89F360201F59F370479A2B9B69F3901059F4E0850462D3531303052";
	private static byte[] hexToByteArray(String s) {
		if(s == null) {
			s = "";
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		for(int i = 0; i < s.length() - 1; i += 2) {
			String data = s.substring(i, i + 2);
			bout.write(Integer.parseInt(data, 16));
		}
		return bout.toByteArray();
	}
	
	public static Hashtable<String, String> decodeTlv(String tlv){
		Hashtable<String, String> decodeTlv = BBDeviceController.decodeTlv(tlv);
		return decodeTlv;
	}
	public static List<TLV> decodeTlvToList(String tlv){
		Hashtable<String, String> decodeTlv = decodeTlv(tlv);
		List<TLV> tlvs = new ArrayList<TLV>();
		Set<String> keys = decodeTlv.keySet();
		for(String key:keys){
			TLV tlvItem = new TLV();
			tlvItem.tag = key;
			tlvItem.value = decodeTlv.get(key);
			tlvItem.length = String.valueOf(decodeTlv.get(key).length());
			tlvs.add(tlvItem);
		}
		return tlvs;
	}
	public static void showTlv(String  tlv){
		Hashtable<String, String> decodeTlv =BBDeviceController.decodeTlv(tlv);
		Set<String> keys = decodeTlv.keySet();
		for(String key:keys){
			BHelper.db("tag:"+ key + " value: "+ decodeTlv.get(key)+ " len:"+decodeTlv.get(key).length());
		}
	}
	
	
	
	
	
	public static String extractSerialNumber(String pinKsn){
		if(pinKsn!=null && pinKsn.length()>14){
			return pinKsn.substring(6, 14);
		}
		return "";
	}
	
	
	
	
	public static String increaseIdentifierKsn(String ksn){
		String ret="";
		if(ksn!=null && ksn.length()>2){
			String hex = ksn.substring(0, 2);
			Integer value = Integer.parseInt(hex, 16);  
			value+=1;
			byte[] tmp = new byte[1];
			tmp[0] = value.byteValue();
			ret = EmvUtils.toHexString(tmp)+ ksn.substring(2);
		}
		
		return ret;
	}
    public static String toHexString(byte[] b) {
		if(b == null) {
			return "null";
		}
		String result = "";
		for (int i=0; i < b.length; i++) {
			result += Integer.toString( ( b[i] & 0xFF ) + 0x100, 16).substring( 1 );
		}
		return result;
	}
    
    public static String encrypt(String data, String key) {
    	if(key.length() == 16) {
    		key += key.substring(0, 8);
    	}
    	byte[] d = hexToByteArray(data);
    	byte[] k = hexToByteArray(key);
    	
    	SecretKey sk = new SecretKeySpec(k, "DESede");
    	try {
    		Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
    		cipher.init(Cipher.ENCRYPT_MODE, sk);
			byte[] enc = cipher.doFinal(d);
			return toHexString(enc);
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	
    	return null;
    }
    public static void showEmvData(){
		String tagValue ="";
		String emvData = EMV_TEST;
		List<TLV> tlvs = decodeTlvToList(emvData);
		for(int i=0;i<tlvs.size();i++){
			int tmp = i+1;
			BHelper.db("TLV no: "+ tmp+ " tag:"+tlvs.get(i).tag+ " value:"+ tlvs.get(i).value + " length:" + tlvs.get(i).length);
		}
	}
    
    public static void showEmvDataManual(){
		String tagValue ="";
		String emvData = EMV_TEST;
		List<TLV> tlvs = TLVParser.parse(emvData);
		for(int i=0;i<tlvs.size();i++){
			int tmp = i+1;
			BHelper.db("TLV no: "+ tmp+ " tag:"+tlvs.get(i).tag+ " value:"+ tlvs.get(i).value + " length:" + tlvs.get(i).length
					+ "\nascii val:"+new String(hexToByteArray( tlvs.get(i).value)));
		}
	}
    
    
    public static String extractMaskTrack2(String emvData){
		String track2Display = EmvUtils.getTagFromEmvData(emvData,EmvReader.EMV_TAG_C4);
		String maskTrack2 = track2Display.substring(0, 6);
		maskTrack2+="******";
		maskTrack2+=track2Display.substring(12);
		return maskTrack2;
    }
    public static String makeMaskTrack2(String maskPAN){
    	String maskTrack2 ="";
    	if(maskPAN!=null && maskPAN.length()>=6)
    		maskTrack2 = maskPAN.substring(0, 6);
    	
		maskTrack2+="******";
    	if(maskPAN.length()>=12)
			maskTrack2+=maskPAN.substring(12,maskPAN.length());
		else if(maskPAN.length()>=9){
			int pos = maskPAN.length();
			maskTrack2 = maskPAN.substring(pos-4,pos);
			pos -=8;
			maskTrack2 = maskPAN.substring(0,pos)+ "****"+ maskTrack2;
		}
		return maskTrack2;
    }
    public static String getTagFromEmvData(String emvData, String tagName){
		BHelper.db("emvData:"+emvData);
		String tagValue ="";
		List<TLV> tlvs = TLVParser.parse(emvData);
		for(int i=0;i<tlvs.size();i++){
			int tmp = i+1;
			if(tlvs.get(i).tag.equals(tagName)){
				tagValue = tlvs.get(i).value;
				tagValue = tagValue.toUpperCase();
				BHelper.db("selected tag : "+ tagName+ " value: "+ tagValue);
				break;
			}
		}
		return tagValue;
	}
    public static String getTrack2DataBase64FromEncoded(String encryptedCardData){
    	byte[] encTrack2Byte = Helper.hexStringToByteArray(encryptedCardData);
		int cardDataLen = 0;
		
    	String track2_base64 =new String(encTrack2Byte);
		BHelper.db("track2_base64 from device: "+track2_base64);
		String Track2Data="";
		Track2Data+=track2_base64;
		cardDataLen = Track2Data.length();
		Track2Data = String.valueOf(cardDataLen)+Track2Data;
		return Track2Data;
    }
	public static  void showHashTable(Hashtable<String, String> data){
		Set<String> keys = data.keySet();
		for(String key: keys){
			BHelper.db(key+":"+data.get(key));
		}
	}
}
