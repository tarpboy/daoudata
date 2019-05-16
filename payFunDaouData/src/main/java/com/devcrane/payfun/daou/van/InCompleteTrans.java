package com.devcrane.payfun.daou.van;

import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;

/**
 * Created by Administrator on 2/9/2017.
 */

public class InCompleteTrans extends DaouData{
    String reasonIncomplete = "1";
    byte[] inCompleteData;
    @Override
    protected byte[] makeData() {
        byte[] buffer = new byte[4096];
        int pos = 0;
        int src_len;
        byte[] src_data;
        byte[] finalData = null;

        String transType = DaouDataContants.INCOMPLETE_TRANSACTION_REQ;
        String taskCode = DaouDataContants.TASK_INCOMPLETE_TRANSACTION;

//      #1 header
        src_data = makeHeader(transType, taskCode);
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        BHelper.db("header:" + new String(src_data));
        BHelper.db("header size:" + src_len);

//		#2 terminal info
        src_data = makeTerminalInfo().getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;

//      #3 encrypt info
        src_data = makeEncryptedInfo(DaouDataHelper.appendChar("", ' ', 16), DaouDataHelper.appendChar("", ' ', 16)).getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;

        BHelper.db("packet'size:" + pos);
        for (int i = 4; i < 14; i++) {
            buffer[pos] = DaouDataContants.VAL_FS;
            pos++;
        }
//        #14 Reason for Incomplete AN V1
        src_data = reasonIncomplete.getBytes(); //NO_EOT
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;
        showLog("Reason for Incomplete", new String(src_data));

        BHelper.db("packet'size:" + pos);
        for (int i = 15; i < 24; i++) {
            buffer[pos] = DaouDataContants.VAL_FS;
            pos++;
        }

//        #24 incomplete data
//        src_data = Base64Utils.base64Encode(inCompleteData).getBytes();
        src_data = inCompleteData;
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        buffer[pos] = DaouDataContants.VAL_ETX;
        pos++;
        showLog("Incomplete Data", new String(src_data));

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
        BHelper.db("sendPacket:" + new String(finalData));
        return finalData;
    }
    public InCompleteTrans(byte[] inCompleteData){
        this.inCompleteData = inCompleteData;
    }
    public InCompleteTrans(byte[] inCompleteData, String reasonIncomplete){
        this.inCompleteData = inCompleteData;
        this.reasonIncomplete = reasonIncomplete;
    }

    @Override
    public String[] req(TerminalInfo terminalInfo) {
        String[] recv = super.req(terminalInfo);
        BHelper.db("========RESP DATA======");
        BHelper.db(getResp(recv));
        AppHelper.setVanMsg(recv[21]);
        return recv;

    }
}
