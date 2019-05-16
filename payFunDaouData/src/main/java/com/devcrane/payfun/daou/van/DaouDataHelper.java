package com.devcrane.payfun.daou.van;

import android.app.Activity;

import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Base64Utils;
import com.devcrane.payfun.daou.utility.MyTaskStr;

import java.io.UnsupportedEncodingException;

public class DaouDataHelper {
    public static String appendChar(String strInput, char c, int targetLen) {
        if (strInput == null)
            strInput = "";
        String ret = strInput;
        for (int j = ret.length(); j < targetLen; j++) ret = ret + c;
        return ret;
    }

    public static String addCharBefore(String strInput, char c, int targetLen) {
        if (strInput == null)
            strInput = "";
        String ret = strInput;
        for (int j = ret.length(); j < targetLen; j++) ret = c + ret;
        return ret;
    }

    static int getBytesSize(String data) {
        int ret = 0;
        byte[] temp1;
        try {
            temp1 = data.getBytes("EUC-KR");
            ret = temp1.length;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }

    public static int calCRC(byte[] data, int len) {
        int crc = 0;
        int pos = 0;

        while (--len >= 0) {
            crc = crc ^ (int) data[pos++] << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) > 0)
                    crc = crc << 1 ^ 0x1021;
                else
                    crc = crc << 1;
            }
        }
        return crc & 0xFFFF;
    }

    public static String getData2KeyExchange(String data) {
        byte[] decode = Base64Utils.base64Decode(data);
        String hexa = HexDump.toHexString(decode);
        BHelper.db("decoded:" + hexa);
        return hexa;
    }

    public static String makeTagWithLen(String tagName, int len) {
        String res = "";
        for (int i = 0; i < len; i++) {
            res += "00";
        }
        res = com.devcrane.payfun.daou.utility.HexDump.toHexString((byte) len) + res;
        res = tagName + res;
        return res;
    }

    public static long getLong(byte[] bytes) {

        long value = 0;
        try {
            for (int i = 0; i < bytes.length; i++) {
                value += ((long) bytes[i] & 0xffL) << (8 * i);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public static String getCardType(String tag4F) {
        String cases[] = {EmvReader.EMV_TAG_CARD_TYPE_VAL_1_VISA_CREDIT_OR_DEBIT,
                EmvReader.EMV_TAG_CARD_TYPE_VAL_2_VISA_ELECTRON,
                EmvReader.EMV_TAG_CARD_TYPE_VAL_3_MASTERCARD_CREDIT_OR_DEBIT,
                EmvReader.EMV_TAG_CARD_TYPE_VAL_4_MASTERCARD_MAESTRO,
                EmvReader.EMV_TAG_CARD_TYPE_VAL_5_AMERICAN_EXPRESS,
                EmvReader.EMV_TAG_CARD_TYPE_VAL_6_JCB,
                EmvReader.EMV_TAG_CARD_TYPE_VAL_7_DINER_CLUB_DISCOVER,
                EmvReader.EMV_TAG_CARD_TYPE_VAL_8_UNION_PAY_DEBIT,
                EmvReader.EMV_TAG_CARD_TYPE_VAL_9_UNION_PAY_CREDIT,
                EmvReader.EMV_TAG_CARD_TYPE_VAL_10_LOCAL_VISA,
                EmvReader.EMV_TAG_CARD_TYPE_VAL_11_LOCAL_MASTER,
                EmvReader.EMV_TAG_CARD_TYPE_VAL_12_LOCAL_DEBIT,
                EmvReader.EMV_TAG_CARD_TYPE_VAL_15_CUPIC};

        String ret = DaouDataContants.VAL_CARD_TYPE_DEFAULT;
        int i = 0;
        for (i = 0; i < cases.length; i++) {
            if (tag4F.contains(cases[i]))
                break;
        }
        i += 1;

        switch (i) {
            case 1:
            case 2:
            case 10:
                ret = "V";
                break;
            case 3:
            case 4:
            case 11:
            case 12:
            case 15:
                ret = "M";
                break;
            case 6:
                ret = "J";//before default is M for this
                break;
            case 5:
                ret = "A";
                break;
            case 7:
                ret = "D";
                break;
            case 8:
            case 9:
                ret = "C";
                break;
            default:
                break;
        }
        return ret;
    }

    public static void openTerminal(Activity at, final TerminalInfo terminalInfo) {
        final DaouData daouData = new OpenTerminal();
        new MyTaskStr(at) {

            @Override
            public String[] run() {

                return daouData.req(terminalInfo);
            }

            @Override
            public boolean res(String[] result) {
                return false;
            }
        };
    }

    public static String cleanData(String data) {
        if (data == null || data.equals(""))
            return "";
        return data.replace("-", "");
    }

    public static CompanyEntity parseToCompany(String[] resp) {
        CompanyEntity entity = new CompanyEntity();
        try {
            entity.setF_ID(resp[1]);
            entity.setF_VanName(StaticData.vanName);
            entity.setF_CompanyName(resp[4]);
            entity.setF_CompanyAddress(resp[5]);
            entity.setF_CompanyPhoneNo(resp[6]);
            entity.setF_CompanyOwnerName(resp[7]);
            entity.setF_VanPhoneNo(resp[8].substring(0, 14));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return entity;
    }

    public static String getSubString(String data, int pos) {
        if (data.length() > pos) {
            return data.substring(pos);
        }
        return data;
    }
}
