package com.devcrane.payfun.daou.van;

import com.devcrane.payfun.daou.entity.EncPayInfo;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.utility.BHelper;

/**
 * Created by Administrator on 8/30/2016.
 */
public class CashReceipt extends PaymentBase implements IPayment{
    static String TRANS_REQUEST = "1";
    static String TRANS_CANCEL = "A";
    String transTypeCash = "1";
    String cancelReason = "1";
    final static String TRANS_TYPE_CASH_1="소득공제";
    final static String TRANS_TYPE_CASH_2="사업자";
    final static String TRANS_TYPE_CASH_3="자진발급";

    public CashReceipt(){
        taskCode = DaouDataContants.TASK_CASH_RECEIPT;
    }

    public CashReceipt(String taskCode) {
        this.taskCode = taskCode;
    }
    @Override
    protected void setCardInfo(String cardInfo){
        BHelper.db("CashReceipt setCardInfo ==> Do nothing");
    }
    @Override
    public String pay(ReceiptEntity entity, EncPayInfo encPayInfo) {
        transType = DaouDataContants.CASH_RECEIPT_REQ;
        setTransTypeCash(entity);
        setVariable(entity,encPayInfo);
        return requestTrans(entity,makeDataPayment(null));
    }
    void setTransTypeCash(ReceiptEntity entity){
        switch (entity.getF_TypeSub()){
            case TRANS_TYPE_CASH_1:
                transTypeCash = "1";
                break;
            case TRANS_TYPE_CASH_2:
                transTypeCash = "2";
                break;
            case TRANS_TYPE_CASH_3:
                transTypeCash = "3";
                break;
            default:
                transTypeCash = "1";
                break;
        }
    }

    void setTransTypeCashCancel(ReceiptEntity entity){
        switch (entity.getF_TypeSub()){
            case TRANS_TYPE_CASH_1:
                transTypeCash = "A";
                break;
            case TRANS_TYPE_CASH_2:
                transTypeCash = "B";
                break;
            case TRANS_TYPE_CASH_3:
                transTypeCash = "E";
                break;
            default:
                transTypeCash = "A";
                break;
        }
    }

    @Override
    public String cancel(ReceiptEntity entity, EncPayInfo encPayInfo) {
        transType = DaouDataContants.CASH_RECEIPT_CANCEL_REQ;
        setTransTypeCashCancel(entity);
        setVariable(entity,encPayInfo);
        return requestTransCancel(entity,makeDataPayment(null));
    }

    @Override
    public byte[] makeDataPayment(ReceiptEntity entity) {
        byte[] buffer = new byte[4096];
        int pos=0;
        int src_len;
        byte[] src_data;
        byte[] finalData = null;

        src_data = makeHeader(transType, taskCode);
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos+=src_len;

        src_data = makeTerminalInfo().getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;

        src_data = makeEncryptedInfo(DaouDataHelper.appendChar("", ' ', 16), DaouDataHelper.appendChar("", ' ', 16)).getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos+=src_len;

        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
//        #4 Card information v121
        BHelper.db("WCC:"+wcc);
        cardInfo=wcc+cardInfo;
        src_data = cardInfo.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
        showLog("Card Info",new String(src_data));

//        #5 Point Card info V121
        src_data = pointCardInfo.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
        showLog("Point card Info",new String(src_data));


//        #6 Transaction type V2
        src_data = transTypeCash.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
        showLog("Transaction type",new String(src_data));

//        #7 Transaction amount N V12
        src_data = totalAmount.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
        showLog("Transaction amount",new String(src_data));

//        #8 Service amount N V12
        src_data = serviceAmount.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
        showLog("Service amount",new String(src_data));

//        #9 Tax amount N V12
        src_data = taxAmount.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
        showLog("Tax amount",new String(src_data));

//        #10 Taxable amount N V12
        src_data = amount.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
        showLog("Taxable amount",new String(src_data));

//        #11 Aproval date N V8
        src_data = approvalDate.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
        showLog("Aproval date",new String(src_data));

//        #12 Aproval no N V12
        src_data = approvalNo.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
        showLog("Aproval no",new String(src_data));

//        #13 Point aproval no N V12
        src_data = pointApprovalNo.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
        showLog("Point aproval no",new String(src_data));

//        #14 Reason for cancel
        src_data = cancelReason.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, buffer, pos, src_len);
        showLog("Reason for cancel",new String(src_data));
        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;

//        #15 Oil type info AN V82
//        oilTypeInfo = DaouDataHelper.appendChar("",' ',82);
//        src_data = oilTypeInfo.getBytes();
//        src_len = src_data.length;
//        System.arraycopy(src_data, 0, buffer, pos, src_len);
//        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
//        showLog("Oil type info",new String(src_data));

//        #16
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;

//        #17 Password ANB V26
//        passWord = DaouDataHelper.appendChar("",' ',26);
//        src_data = passWord.getBytes();
//        src_len = src_data.length;
//        System.arraycopy(src_data, 0, buffer, pos, src_len);
//        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
//        showLog("Password",new String(src_data));

//        #18 EmvData ANB V480

        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;

//        #19 Signature ANB V1300

        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
        showLog("Signature",new String(src_data));


//        #20 Exchange rate V100
//        src_data = exchangeRateInfo.getBytes();
//        src_len = src_data.length;
//        System.arraycopy(src_data, 0, buffer, pos, src_len);
//        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
//        showLog("Exchange rate",new String(src_data));

//        #21 Tax refund data AN V1024
//        src_data = taxRefundData.getBytes();
//        src_len = src_data.length;
//        System.arraycopy(src_data, 0, buffer, pos, src_len);
//        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
//        showLog("Tax refund data",new String(src_data));

//		#22
//        src_data = DaouDataHelper.appendChar("", ' ', 8).getBytes();
//        src_len = src_data.length;
//        System.arraycopy(src_data, 0, buffer, pos, src_len);
//        pos+=src_len;
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;
//        showLog("Main-Vendor Terminal Number",new String(src_data));


//        #23
        buffer[pos]= DaouDataContants.VAL_FS;
        pos++;

//      #24
        buffer[pos]= DaouDataContants.VAL_ETX;
        pos++;


        finalData = new byte[pos+4];
        System.arraycopy(buffer, 0, finalData, 0, pos);

        //Fill packet's len.
        String data_len = ""+(pos+4);
        data_len = DaouDataHelper.addCharBefore(data_len,'0',4);
        BHelper.db("data_len:"+data_len);
        src_data = data_len.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, finalData, 1, src_len);

        int crc = DaouDataHelper.calCRC(finalData, pos);

        src_data = HexDump.toByteArray((short)crc);
        String crc_str =HexDump.toHexString(src_data);
        BHelper.db("crc:"+crc_str);
        src_data  = crc_str.getBytes();
        src_len = src_data.length;
        System.arraycopy(src_data, 0, finalData, pos, src_len);
        pos+=src_len;
        BHelper.db("sendPacket:"+ new String(finalData));
        return finalData;
    }
}
