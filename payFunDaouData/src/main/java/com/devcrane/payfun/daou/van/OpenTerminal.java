package com.devcrane.payfun.daou.van;

import com.devcrane.payfun.daou.entity.SessionInfo;
import com.devcrane.payfun.daou.entity.TerminalInfo;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Base64Utils;

/**
 * Created by Administrator on 8/30/2016.
 */
public class OpenTerminal extends DaouData {

    @Override
    protected byte[] makeData() {
        byte[] buffer = new byte[4096];
        int pos = 0;
        int src_len;
        byte[] src_data;
        byte[] finalData = null;

        transType = DaouDataContants.DEVICE_OPENING_TRANSACTION_REQ;
        taskCode = DaouDataContants.TASK_DEVICE_OPENING_TRANSACTION;

        src_data = makeHeader(transType, taskCode);
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;

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

        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;

        src_data = terminalInfo.getTerCompanyNo().getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;
        showLog("company number", new String(src_data));

        //#5 area code
        src_data = DaouDataHelper.appendChar("031", '0', 3).getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;
        showLog("area code ", new String(src_data));

        for (int i = 6; i < 21; i++) {
            buffer[pos] = DaouDataContants.VAL_FS;
            pos++;
        }

//		#22
        src_data = DaouDataHelper.appendChar("", ' ', 8).getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;
        showLog("Main-Vendor Terminal Number", new String(src_data));
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
        src_len = src_data.length;
        System.arraycopy(src_data, 0, finalData, pos, src_len);
        pos += src_len;
        BHelper.db("sendPacket:" + new String(finalData));
        buffer = null;
        src_data = null;
        src_len = 0;
        return finalData;
    }

    @Override
    public String[] req(TerminalInfo para) {
        BHelper.db("req is overrided");

        String resp[] = super.req(para,AppHelper.getDownloadVanIp(),AppHelper.getDownloadVanPort());
        try {
            int headerLen = resp[0].length();

            String slipNo = resp[0].substring(headerLen - 4, headerLen);
            String publicKeyInfo = resp[15];
            byte[] publicKeyByte = Base64Utils.base64Decode(publicKeyInfo);
            byte[] seekIndexByte = new byte[2];
            System.arraycopy(publicKeyByte, 0, seekIndexByte, 0, 2);
            String seedIndex = new String(seekIndexByte);
            BHelper.db("slipNo:" + slipNo);
            BHelper.db("seedIndex:" + seedIndex);
            SessionInfo info = new SessionInfo(slipNo, seedIndex);
            AppHelper.setSessionInfo(info);
            headerLen = 0;
            slipNo = publicKeyInfo = seedIndex = "";
            publicKeyByte = seekIndexByte = null;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return resp;
    }
}
