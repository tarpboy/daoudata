package com.devcrane.payfun.daou.van;

import com.devcrane.payfun.daou.utility.BHelper;

/**
 * Created by Administrator on 8/30/2016.
 */
public class SecurityKeyDownload extends DaouData {

    public SecurityKeyDownload(String randomKey) {
        setTerminalRandomKey(randomKey);
    }

    String terminalRandomKey = "";

    public void setTerminalRandomKey(String randomKey) {
        this.terminalRandomKey = randomKey;
    }

    @Override
    protected byte[] makeData() {
        byte[] buffer = new byte[4096];
        int pos = 0;
        int src_len;
        byte[] src_data;
        byte[] finalData = null;

        String transType = DaouDataContants.TERMINAL_SECURITY_KEY_DOWNLOAD_REQ;
        String taskCode = DaouDataContants.TASK_TERMINAL_SECURITY_KEY_DOWNLOAD;

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
//		#3
        src_data = makeEncryptedInfo(DaouDataHelper.appendChar("", ' ', 16), DaouDataHelper.appendChar("", ' ', 16)).getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        showLog("encrypted information:", new String(src_data));
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;


//		#4
        src_data = getModuleInfo().getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        showLog("Module Information ", new String(src_data));
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;

        //#5 Terminal random key
        src_data = terminalRandomKey.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        showLog("Terminal random key", new String(src_data));
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;


        BHelper.db("packet'size:" + pos);
        for (int i = 6; i < 21; i++) {
            buffer[pos] = DaouDataContants.VAL_FS;
            pos++;
        }

        //#22
        src_data = DaouDataHelper.appendChar("", ' ', 8).getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;

        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;

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

        src_data = buffer = null;
        src_len = 0;
        return finalData;
    }
//    @Override
//    public String[] req(TerminalInfo para) {
//        BHelper.db("req is overrided in SecurityKeyDownload");
//        return super.req(para, VAN_IP_ADDRESS, VAN_PORT);
//    }
}
