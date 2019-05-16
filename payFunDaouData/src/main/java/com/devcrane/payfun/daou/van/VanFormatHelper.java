package com.devcrane.payfun.daou.van;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 8/17/2016.
 */
public class VanFormatHelper {
    byte[] HECSTR_PTR;

    public VanFormatHelper(String data) {
        try {
            HECSTR_PTR = data.getBytes("EUC-KR");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String HECSTR(int cnt, int offset) {
        if (HECSTR_PTR != null && HECSTR_PTR.length >= cnt + offset) {
            try {
                String str = new String(HECSTR_PTR, offset, cnt, "EUC-KR");
                return str.trim();
            } catch (Exception e) {
            }
        }
        return "";
    }
}
