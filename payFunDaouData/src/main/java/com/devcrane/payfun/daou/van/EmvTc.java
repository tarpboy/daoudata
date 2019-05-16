package com.devcrane.payfun.daou.van;

import com.devcrane.payfun.daou.entity.EmvTcEntity;
import com.devcrane.payfun.daou.utility.BHelper;

/**
 * Created by Administrator on 1/16/2017.
 */

public class EmvTc extends DaouData {
    EmvTcEntity tcEntity = new EmvTcEntity();
    @Override
    protected byte[] makeData() {
        byte[] buffer = new byte[4096];
        int pos = 0;
        int src_len;
        byte[] src_data;
        byte[] finalData = null;

        String transType = DaouDataContants.SEND_TC_ISSUER_SCRIPT_RESULT_REQ;
        String taskCode = DaouDataContants.TASK_SEND_TC_ISSUER_SCRIPT_RESULT;

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

        BHelper.db("packet'size:" + pos);
        for (int i = 3; i < 11; i++) {
            buffer[pos] = DaouDataContants.VAL_FS;
            pos++;
        }
//        #11 Aproval date N V8
        src_data = tcEntity.getApprovalDate().getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;
        showLog("Aproval date", new String(src_data));

//        #12 Aproval no N V12
        String approvalNo = tcEntity.getApprovalNo();
//        approvalNo = DaouDataHelper.appendChar(approvalNo, ' ', 12);
        src_data = approvalNo.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;
        showLog("Aproval no", new String(src_data));


//        #13 Transaction Unique Number AN 12
        src_data = DaouDataHelper.appendChar(tcEntity.getTransUniqueNo(),' ',12).getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;
        showLog("Transaction Unique Number AN 12", new String(src_data));



        BHelper.db("packet'size:" + pos);
        for (int i = 14; i < 18; i++) {
            buffer[pos] = DaouDataContants.VAL_FS;
            pos++;
        }

//        #18 TC
        src_data = tcEntity.getTc().getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos += src_len;
        buffer[pos] = DaouDataContants.VAL_FS;
        pos++;
        showLog("EMV TC & Issuer Script Result", new String(src_data));


        BHelper.db("packet'size:" + pos);
        for (int i = 19; i < 24; i++) {
            buffer[pos] = DaouDataContants.VAL_FS;
            pos++;
        }
        //#22
//        src_data = DaouDataHelper.appendChar("", ' ', 8).getBytes();
//        src_len = src_data.length;
//        System.arraycopy(src_data, 0, buffer, pos, src_len);
//        pos += src_len;
//        buffer[pos] = DaouDataContants.VAL_FS;
//        pos++;

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

        src_data = buffer = null;
        src_len = 0;
        return finalData;
    }

    public EmvTc(EmvTcEntity tcEntity){
        this.tcEntity = tcEntity;

    }
}
