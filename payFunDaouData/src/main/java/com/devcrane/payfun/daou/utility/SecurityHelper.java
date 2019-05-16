package com.devcrane.payfun.daou.utility;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SecurityHelper {
	private static final String key16 = "fsfyw!gDSj&*#*29";
	public static String encrypt(String value) {
		try {
			return value;
//			return	value.substring(0, 6) + "xxxxxx"+value.substring(13);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return value;
	}
	public static String encrypts(String value) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key16.getBytes("UTF-8"),"AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(value.getBytes());
			return Base64Utils.base64Encode(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public static String decrypt(String encrypted) {
		try {
			return encrypted;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return encrypted;
	}
	public static String decrypts(String encrypted) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key16.getBytes("UTF-8"),"AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE,skeySpec);
			byte[] original = cipher.doFinal(Base64Utils.base64Decode(encrypted));
			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
