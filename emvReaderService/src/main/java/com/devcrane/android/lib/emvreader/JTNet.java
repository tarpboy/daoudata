package com.devcrane.android.lib.emvreader;

import java.io.UnsupportedEncodingException;

public class JTNet {
	public static final int SIZE_RES_KEYBINDING = 44;
	public static final int SIZE_RES_KEYBINDING_SERVERINFO = 697;
	public static int getBytesSize(String data){
		int ret = 0;
		byte[] temp1 ;
		try {
			temp1 = data.getBytes("EUC-KR");
			ret = temp1.length;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
}
