package com.devcrane.payfun.daou.van;

import com.devcrane.payfun.daou.utility.BHelper;

/**
 * Created by Administrator on 8/30/2016.
 */
public class RenewalKey extends DaouData {
    String publicKeyInfo="";//anb176

    @Override
    protected byte[] makeData() {
        byte[] buffer = new byte[4096];
        int pos = 0;
        int src_len;
        byte[] src_data;
        byte[] finalData = null;
        transType = DaouDataContants.RENEWAL_OF_DEVICE_ENCRYPT_KEY_REQ;
        taskCode = DaouDataContants.TASK_RENEWAL_OF_DEVICE_ENCRYPT_KEY;
        src_data = makeHeader(transType, taskCode);
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        BHelper.db("header:" + new String(src_data));
        BHelper.db("header size:" + src_len);

        src_data = makeTerminalInfo().getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;

        src_data = makeEncryptedInfo(DaouDataHelper.appendChar("", ' ', 16), DaouDataHelper.appendChar("", ' ', 16)).getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        showLog("encrypted information:", new String(src_data));
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;

//		#4
        src_data = keyInfo.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        showLog("Card Session Key Information ", new String(src_data));
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;

//        //#5 PIN Session Key Information
//        src_data = keyInfo.getBytes();
//        src_len = src_data.length;
//        System.arraycopy(src_data, 0, buffer, pos, src_len);
//        pos += src_len;
//        showLog("PIN Session Key Information", new String(src_data));
//        buffer[pos] = DaouDataContants.VAL_FS;
//        pos++;
//
////		#6 Signature Session Key Information
//
//        src_data = keyInfo.getBytes();
//        src_len = src_data.length;
//        System.arraycopy(src_data, 0, buffer, pos, src_len);
//        pos += src_len;
//        showLog("Signature Session Key Information", new String(src_data));
//        buffer[pos] = DaouDataContants.VAL_FS;
//        pos++;

//        BHelper.db("packet'size:" + pos);

        for (int i = 5; i < 23; i++) {
            buffer[pos] = DaouDataContants.VAL_FS;
            pos++;
        }

//        //#22
//        src_data = DaouDataHelper.appendChar("", ' ', 8).getBytes();
//        src_len = src_data.length;
//        System.arraycopy(src_data, 0, buffer, pos, src_len);
//        pos += src_len;
//        buffer[pos] = DaouDataContants.VAL_FS;
//        pos++;
//
//        buffer[pos] = DaouDataContants.VAL_FS;
//        pos++;

        buffer[pos] = DaouDataContants.VAL_ETX;
        pos++;

        finalData = new byte[pos + 4];
        System.arraycopy(buffer, 0, finalData, 0, pos);

        //Fill packet's len.
        String data_len = "" + (pos + 4);
        data_len = DaouDataHelper.addCharBefore(data_len, '0', 4);
        BHelper.db("data_len:" + data_len);
        src_data = data_len.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, finalData, 1, src_len);

        int crc = DaouDataHelper.calCRC(finalData, pos);

        src_data = HexDump.toByteArray((short) crc);
        String crc_str = HexDump.toHexString(src_data);
        BHelper.db("crc:" + crc_str);
        src_data = crc_str.getBytes();
        BHelper.db("crc 2:" + HexDump.toHexString(src_data));
        src_len = src_data.length;
        System.arraycopy(src_data, 0, finalData, pos, src_len);
        pos += src_len;
        src_data = null;
        src_len = 0;
        return finalData;
    }
}
