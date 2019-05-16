package com.devcrane.android.lib.emvreader;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.devcrane.android.lib.utility.BHelper;

public class TLVParser {
	
	public static List<TLV> parse_old(String tlv) {
		try {
			return getTLVList(hexToByteArray(tlv));
		} catch(Exception e) {
			return null;
		}
	}
	public static List<TLV> parse(String tlv) {
		try {
			return getTLVList(tlv);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static List<TLV> getTLVList(String tlv){
		
		ArrayList<TLV> tlvList = new ArrayList<TLV>();
		String tag3bytes[] = {"df8315"};
		String tag2bytes[] = {"c282","5f20","5f24","5f25","5f2a",
				"5f30","5f34","9f01","9f02","9f03",
				"9f06","9f07","9f09","9f0d","9f0e",
				"9f0f","9f10","9f12","9f16","9f1a",
				"9f1c","9f1e","9f21","9f26","9f27",
				"9f33","9f34","9f35","9f36","9f37",
				"9f39","9f40","9f41","9f4e","9f53",
				"9f5b","9f1f","9f45","9f4c"};
		String tag1byte[] = {"4f","50","57","5a","82",
				"84","89","8a","8e","95",
				"99","9a","9b","9c",
				"c0","c1","c2","c3","c4",
				"c5","c6","c7","c8"};
		int index = 0;
		int tlvLen =tlv.length(); 
		BHelper.db("tlv len:"+ tlvLen);
		String lastChecked = "";
		boolean isDetected = false;
		
		while(index<tlvLen){
			String dataToCheck ;
			isDetected = false;
			if(tlvLen<index+6)
				break;
			dataToCheck = tlv.substring(index,index+6);
//			BHelper.db("dataToCheck:"+dataToCheck + "last checked:"+lastChecked );
			if(lastChecked!="" &&  dataToCheck.equals(lastChecked)){
//				BHelper.db("fail for check data:"+ dataToCheck);
				index+= 2;
				continue;
			}
			lastChecked = dataToCheck;
			
			
			
			//check tag 3 bytes
			for(int i=0;i<tag3bytes.length;i++){
				if(dataToCheck.contains(tag3bytes[i].toUpperCase()) || dataToCheck.contains(tag3bytes[i])){
					
					//found tag3byte.
					String tag, length, value;
					int tagLen = 0;
					tag = tag3bytes[i];
					length = tlv.substring(index+6,index+8);
					
					tagLen = getLengthInt(length);
					tagLen*=2;
					if(tagLen!=0 && tlvLen>=index+8+tagLen){
						value = tlv.substring(index+8,index+8+tagLen);
					}else{
//						BHelper.db("fail tag:"+tag3bytes[i] + "  for check data:"+ dataToCheck);
						
						index+=2;
						break;
					}
					
					TLV tlvEn = new TLV();
					tlvEn.tag = tag;
					tlvEn.length = length;
					tlvEn.value = value;
//					BHelper.db("tag found:"+ tag+ " len: "+ tagLen/2 + " value: "+ value);
					tlvList.add(tlvEn);
					index+=8+tagLen;
					isDetected = true;
					break;
				}
			}
			if(isDetected)
				continue;
			//check tag 2 bytes
			for(int j=0;j<tag2bytes.length;j++){
//				BHelper.db("check item :"+ tag2bytes[j]+ " at: "+ j);
				if(dataToCheck.contains(tag2bytes[j].toUpperCase())|| dataToCheck.contains(tag2bytes[j])){
					if(tag2bytes[j].equals("c282")){
//						BHelper.db("fail tag:"+tag2bytes[j] + "  for check data:"+ dataToCheck);
						index+=2;
						break;	
					}
					//found tag3byte.
//					index=tlv.indexOf(tag2bytes[j].toUpperCase());
					String tag, length, value;
					int tagLen = 0;
					tag = tag2bytes[j];
					length = tlv.substring(index+4,index+6);
					tagLen = getLengthInt(length);
					tagLen*=2;
					if(tagLen!=0 && tlvLen>=index+6+tagLen){
						value = tlv.substring(index+6,index+6+tagLen);
					}else{
//						BHelper.db("fail tag:"+tag2bytes[j] + "  for check data:"+ dataToCheck);
						index+=2;
						break;
					}
					
					TLV tlvEn = new TLV();
					tlvEn.tag = tag;
					tlvEn.length = length;
					tlvEn.value = value;
//					BHelper.db("tag found:"+ tag+ " len: "+ tagLen/2 + " value: "+ value);
					tlvList.add(tlvEn);
					index+=6+tagLen;
					isDetected = true;
					break;
				}
			}
			if(isDetected)
				continue;
			//check tag 1 bytes
			for(int k=0;k<tag1byte.length;k++){
//				BHelper.db("check item :"+ tag1byte[k]+ " at: "+ k);
				if(dataToCheck.startsWith(tag1byte[k].toUpperCase())){
					//found tag3byte.
					String tag, length, value;
					int tagLen = 0;
					tag = tag1byte[k];
					length = tlv.substring(index+2,index+4);
					tagLen = getLengthInt(length);
					tagLen*=2;
					if(tagLen!=0 && tlvLen>=index+4+tagLen){
						value = tlv.substring(index+4,index+4+tagLen);
					}else{
//						BHelper.db("fail tag:"+tag1byte[k] + "  for check data:"+ dataToCheck);
						index+=2;
						break;
					}
					
					TLV tlvEn = new TLV();
					tlvEn.tag = tag;
					tlvEn.length = length;
					tlvEn.value = value;
//					BHelper.db("tag found:"+ tag+ " len: "+ tagLen/2 + " value: "+ value);
					tlvList.add(tlvEn);
					index+=4+tagLen;
					isDetected = true;
					break;
				}
			}
		}
		BHelper.db("tlvList size:"+tlvList.size());
		return tlvList;
	}
	private static List<TLV> getTLVList(byte[] data) {
		int index = 0;
		
		ArrayList<TLV> tlvList = new ArrayList<TLV>();
		
		try {
			while(index < data.length) {
				
				byte[] tag;
				byte[] length;
				byte[] value;
				
				boolean isNested;
				if((data[index] & (byte)0x20) == (byte)(0x20)) {
					isNested = true;
				} else {
					isNested = false;
				}
				
				if((data[index] & (byte)0x1F) == (byte)(0x1F)) {
					int lastByte = index + 1;
					while((data[lastByte] & (byte)0x80) == (byte)0x80) {
						++lastByte;
					}
					tag = new byte[lastByte - index + 1];
					System.arraycopy(data, index, tag, 0, tag.length);
					index += tag.length;
				} else {
					tag = new byte[1];
					tag[0] = data[index];
					++index;
					
					if(tag[0] == 0x00) {
						break;
					}
				}
				
				if((data[index] & (byte)0x80) == (byte)(0x80)) {
					int n = (data[index] & (byte)0x7F) + 1;
					length = new byte[n];
					System.arraycopy(data, index, length, 0, length.length);
					index += length.length;
				} else {
					length = new byte[1];
					length[0] = data[index];
					++index;
				}
				
				int n = getLengthInt(length);
				value = new byte[n];
				System.arraycopy(data, index, value, 0, value.length);
				index += value.length;
				
				TLV tlv = new TLV();
				tlv.tag = toHexString(tag);
				tlv.length = toHexString(length);
				tlv.value = toHexString(value);
				tlv.isNested = isNested;
				
				if(isNested) {
					tlv.tlvList = getTLVList(value);
				}
				
				tlvList.add(tlv);
			}
		} catch (Exception e) {
		}
		
		return tlvList;
	}
	
	private static int getLengthInt(byte[] data) {
		if((data[0] & (byte)0x80) == (byte)(0x80)) {
			int n = data[0] & (byte)0x7F;
			int length = 0;
			
			for(int i = 1; (i < n + 1 && data.length>i); ++i) {
				length <<= 8; 
				length |= (data[i] & 0xFF);
			}
			return length;
		} else {
			return data[0] & 0xFF;
		}
	}
	private static int getLengthInt(String data) {
		try{
			return Integer.parseInt(data, 16);
		}catch(Exception ex){
			ex.printStackTrace();
			return 0;
		}
	}
	protected static byte[] hexToByteArray(String s) {
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
	
	protected static String toHexString(byte[] b) {
		String result = "";
		for (int i=0; i < b.length; i++) {
			result += Integer.toString( ( b[i] & 0xFF ) + 0x100, 16).substring( 1 );
		}
		return result;
	}
	
	protected static TLV searchTLV(List<TLV> tlvList, String targetTag) {
		for(int i = 0; i < tlvList.size(); ++i) {
			TLV tlv = tlvList.get(i);
			if(tlv.tag.equalsIgnoreCase(targetTag)) {
				return tlv;
			} else if(tlv.isNested) {
				TLV searchChild = searchTLV(tlv.tlvList, targetTag);
				if(searchChild != null) {
					return searchChild;
				}
			}
		}
		return null;
	}
}