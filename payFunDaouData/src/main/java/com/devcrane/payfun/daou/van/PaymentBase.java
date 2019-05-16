package com.devcrane.payfun.daou.van;

import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.android.lib.emvreader.TLV;
import com.devcrane.android.lib.emvreader.TLVParser;
import com.devcrane.payfun.cardreader.EmvUtils;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.EmvTcEntity;
import com.devcrane.payfun.daou.entity.EncPayInfo;
import com.devcrane.payfun.daou.entity.InCompleteDataEntity;
import com.devcrane.payfun.daou.entity.ReceiptEntity;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Base64Utils;
import com.devcrane.payfun.daou.utility.DateHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.utility.VanHelper;

import java.util.List;

/**
 * Created by Administrator on 8/30/2016.
 */
public class PaymentBase extends DaouData {

    protected String cardInfo = "";
    protected String pointCardInfo = "";
    protected String payType;
    protected String totalAmount;
    protected String serviceAmount;
    protected String taxAmount;
    protected String amount;
    protected String approvalDate;
    protected String approvalNo;
    protected String pointApprovalNo;
    protected String divideMonth;
    protected String wcc = "";
    protected String subType = "";
    String passWord = "";
    String emvData;
    String signData;


    @Override
    protected byte[] makeData() {
        return new byte[0];
    }

    protected void setVariable(ReceiptEntity entity, EncPayInfo encPayInfo) {
        setEncData(encPayInfo);
        setVariable(entity);
        setCardInfo(encPayInfo.getEmvData(), entity);
    }

    void setVariable(ReceiptEntity entity) {
        this.payType = entity.getF_Type();
        this.totalAmount = DaouDataHelper.addCharBefore(entity.getF_TotalAmount(), '0', 12);
        this.amount = DaouDataHelper.addCharBefore(entity.getF_Amount(), '0', 12);
        this.serviceAmount = DaouDataHelper.addCharBefore(entity.getF_Service(), '0', 12);
        this.taxAmount = DaouDataHelper.addCharBefore(entity.getF_Tax(), '0', 12);
        this.divideMonth = entity.getF_Month();
        this.approvalDate = entity.getF_RequestDate();
        this.approvalNo = DaouDataHelper.addCharBefore(entity.getF_ApprovalCode(), '0', 8);
        this.pointApprovalNo = DaouDataHelper.addCharBefore("", '0', 12);
        this.wcc = entity.getF_CardInputMethod();
        this.cardInfo = entity.getF_CardNo();
        this.subType = entity.getF_TypeSub();
    }

    protected void setCardInfo(String emvData, ReceiptEntity receiptEntity) {
        this.cardInfo = receiptEntity.getF_CardNo();
        setCardInfo(emvData);
    }

    protected void setCardInfo(String emvData) {
        String track2 = EmvUtils.getTagFromEmvData(emvData, EmvReader.EMV_TAG_KSN_TRACK2);
        byte[] encTrack2Byte;
        if (emvData == null || emvData.equals("")) {
            track2 = this.cardInfo;
        }

        encTrack2Byte = Helper.hexStringToByteArray(track2);
        String track2_base64 = new String(encTrack2Byte);
        this.cardInfo = track2_base64;
    }
    protected String makeFallback(String amount, String fallbackReason){
        byte[] tmpBuff = new byte[30];
        String cardTypeCode = DaouDataContants.VAL_CARD_TYPE_DEFAULT;
        String emvDataLen = "    ";
        byte[] posEntryModeCode = {(byte)0x90, 0x10};

        byte[] cardSequenceNumber = new byte[1];
        String retrievalReferenceNumber = "            ";
        byte[] addiontionPosInfomationByte = {0x04, 0x05, 0x00, 0x00, 0x10};

        String emvData = EmvUtils.getEmvData();
        BHelper.db("emvData:" + emvData);

        List<TLV> tlvs = TLVParser.parse(emvData);

        for (int i = 0; i < tlvs.size(); i++) {
            switch (tlvs.get(i).tag) {
                case EmvReader.EMV_TAG_CARD_SEQUENCE_NUMBER:
                    cardSequenceNumber = Helper.hexStringToByteArray(tlvs.get(i).value);
                    break;
                case EmvReader.EMV_TAG_CARD_TYPE:
                    cardTypeCode = DaouDataHelper.getCardType(tlvs.get(i).value.toUpperCase());
                    cardType = cardTypeCode;
                    break;
//			case EmvReader.EMV_TAG_PAN:
////				pan = Helper.hexStringToByteArray(tlvs.get(i).value);
//				break;
                case EmvReader.EMV_TAG_POS_ENTRY_MODE_CODE:
                    posEntryModeCode[0] = Helper.hexStringToByteArray(tlvs.get(i).value)[0];
                    posEntryModeCode[1] = 0x10;
                    break;
                default:
                    break;
            }
        }
        byte[] pan = {0x00};
        long myrand = (long) Math.floor(Math.random() * 900000) + 1000000;
        long check1 = Long.valueOf((terminalInfo.getTerNumber() + 2));//.substring(0, 8));//
        long checkCode1 = (myrand + check1) % 10;

        pan = cardSequenceNumber;

        long checkCode2 = (Long.valueOf(amount.trim()) + DaouDataHelper.getLong(pan)) % 10;

        retrievalReferenceNumber = String.valueOf(DateHelper.getCurrJulianDate()).substring(0, 4);
        retrievalReferenceNumber += DateHelper.getCurrenthhmmss();
        retrievalReferenceNumber += String.valueOf(checkCode1);
        retrievalReferenceNumber += String.valueOf(checkCode2);

        byte[] emvPart1 = (cardTypeCode).getBytes();
        byte[] emvPart2 = new byte[4];//emvDataLen
        byte[] emvPart3 = new byte[15];
        byte[] emvPart4 = addiontionPosInfomationByte;
        byte[] emvPart5 = fallbackReason.getBytes();




        //make part3
        System.arraycopy(posEntryModeCode, 0, emvPart3, 0, 2);
        System.arraycopy(cardSequenceNumber, 0, emvPart3, 2, 1);
        System.arraycopy(retrievalReferenceNumber.getBytes(), 0, emvPart3, 3, 12);

        int emvLen = 0;
        int tmpLen = 0;
        System.arraycopy(emvPart1, 0, tmpBuff, 0, emvPart1.length);
        tmpLen += emvPart1.length;
        System.arraycopy(emvPart2, 0, tmpBuff, tmpLen, emvPart2.length);
        tmpLen += emvPart2.length;
        System.arraycopy(emvPart3, 0, tmpBuff, tmpLen, emvPart3.length);
        tmpLen += emvPart3.length;
        System.arraycopy(emvPart4, 0, tmpBuff, tmpLen, emvPart4.length);
        tmpLen += emvPart4.length;
        System.arraycopy(emvPart5, 0, tmpBuff, tmpLen, emvPart5.length);
        tmpLen += emvPart5.length;

        emvLen = tmpLen - (emvPart1.length + emvPart2.length);
        emvDataLen = String.format("%04d", emvLen);

        System.arraycopy(emvDataLen.getBytes(), 0, tmpBuff, emvPart1.length, 4);
        byte[] emvDataFin = new byte[tmpLen];
        System.arraycopy(tmpBuff, 0, emvDataFin, 0, tmpLen);
        BHelper.db(Helper.byte2hex2(emvDataFin));
        String tmpEmvLog = "";
        tmpEmvLog += "cardTypeCode:" + cardTypeCode + "\n";
        tmpEmvLog += "emvDataLen:" + tmpLen + "\n";
        tmpEmvLog += "posEntryMode:" + Helper.byte2hex(posEntryModeCode) + "\n";
        tmpEmvLog += "cardSequenceNumber:" + Helper.byte2hex(cardSequenceNumber) + "\n";
        tmpEmvLog += "retrievalReferenceNumber:" +retrievalReferenceNumber+ "\n";
        tmpEmvLog += "addiontionPosInfomation:" + Helper.byte2hex(addiontionPosInfomationByte) + "\n";
        tmpEmvLog += "fallbackReason:" + fallbackReason + "\n";
        BHelper.db("Fallback:" + tmpEmvLog);

        emvPart1 =
                emvPart2 =
                        emvPart3 =
                                emvPart4 = emvPart5 = null;
        emvLen = tmpLen = 0;
        retrievalReferenceNumber = "";
        return Base64Utils.base64Encode(emvDataFin);
    }
    protected String makeEmvData(String amount) {
        byte[] tmpBuff = new byte[480];
        String password = "";//"00";//
        String cardTypeCode = DaouDataContants.VAL_CARD_TYPE_DEFAULT;
        String emvDataLen = "    ";
        byte[] posEntryModeCode = new byte[2];

        byte[] cardSequenceNumber = new byte[1];
        byte[] addiontionPosInfomation = new byte[10];
        byte[] addiontionPosInfomationByte = {0x04, 0x05, 0x00, 0x00, 0x10};

        String retrievalReferenceNumber = "            ";
        byte[] pan = {0x00};


        byte[] terminalCapilityProfile = new byte[3];
        byte[] terminalVerificationResults = new byte[5];
        byte[] unpredictableNumber = new byte[8];
        byte[] visaDiscretionanryData = {0x00};

        byte[] issuerDiscretionaryData = {0x00};
        byte[] cryptoGram = new byte[8];
        byte[] applicationTransactionCounter = new byte[2];
        byte[] applicationInterchangeProfile = new byte[2];
        byte[] cryptoGramTransactionType = new byte[1];
        byte[] terminalCountryCode = new byte[2];
        byte[] terminalTransactionDate = new byte[3];
        byte[] cryptoGramAmount = new byte[6];
        byte[] cryptoGramCurrencyCode = new byte[2];
        byte[] cryptoGramCashback = new byte[6];//2 old

        String emvData = EmvUtils.getEmvData();
        BHelper.db("emvData:" + emvData);

        List<TLV> tlvs = TLVParser.parse(emvData);

        for (int i = 0; i < tlvs.size(); i++) {
            switch (tlvs.get(i).tag) {
                case EmvReader.EMV_TAG_CARD_SEQUENCE_NUMBER:
                    cardSequenceNumber = Helper.hexStringToByteArray(tlvs.get(i).value);
                    break;
                case EmvReader.EMV_TAG_CARD_TYPE:
                    cardTypeCode = DaouDataHelper.getCardType(tlvs.get(i).value.toUpperCase());
                    //use to check response emv.
                    BHelper.db("cardTypeCode:" + cardTypeCode);
                    cardType = cardTypeCode;
                    break;
//			case EmvReader.EMV_TAG_PAN:
////				pan = Helper.hexStringToByteArray(tlvs.get(i).value);
//				break;
                case EmvReader.EMV_TAG_POS_ENTRY_MODE_CODE:
                    posEntryModeCode[0] = Helper.hexStringToByteArray(tlvs.get(i).value)[0];
                    posEntryModeCode[1] = 0x10;
                    break;
                case EmvReader.EMV_TAG_ADDITIONAL_POS_INFOMATION:
                    addiontionPosInfomation = Helper.hexStringToByteArray(tlvs.get(i).value);
                    BHelper.db("additionalPosInfo:" + tlvs.get(i).value);
                    BHelper.db("addiontionPosInfomation:" + Helper.byte2hex2(addiontionPosInfomation));
                    break;
                case EmvReader.EMV_TAG_TERMINAL_CAPILITY_PROFILE:
                    terminalCapilityProfile = Helper.hexStringToByteArray(tlvs.get(i).value);
                    break;
                case EmvReader.EMV_TAG_TERMINAL_VERIFICATION_RESULTS:
                    terminalVerificationResults = Helper.hexStringToByteArray(tlvs.get(i).value);
                    break;
                case EmvReader.EMV_TAG_UNPREDICTABLE_NUMBER:
                    unpredictableNumber = Helper.hexStringToByteArray(tlvs.get(i).value);
                    break;
                case EmvReader.EMV_TAG_VISA_DISCRETIONARY_DATA:
                    if (cardTypeCode.equals("V")) {
                        BHelper.db("EMV_TAG_VISA_DISCRETIONARY_DATA:" + tlvs.get(i).value);
                        visaDiscretionanryData = Helper.hexStringToByteArray(tlvs.get(i).value);
                    }
                    break;
                case EmvReader.EMV_TAG_ISSUER_DISCRETIONARY_DATA:
                    BHelper.db("EMV_TAG_ISSUER_DISCRETIONARY_DATA:" + tlvs.get(i).value);
                    issuerDiscretionaryData = tlvs.get(i).value.getBytes();
                    break;
                case EmvReader.EMV_TAG_CRYPTOGRAM:
                    cryptoGram = Helper.hexStringToByteArray(tlvs.get(i).value);
                    EmvUtils.saveApplicationCryptogram(tlvs.get(i).value);
                    break;
                case EmvReader.EMV_TAG_APPLICATION_TRANSACTON_COUNTER:
                    applicationTransactionCounter = Helper.hexStringToByteArray(tlvs.get(i).value);
                    break;
                case EmvReader.EMV_TAG_APPLICATION_INTERCHANGE_PROFILE:
                    applicationInterchangeProfile = Helper.hexStringToByteArray(tlvs.get(i).value);
                    break;
                case EmvReader.EMV_TAG_CRYPTOGRAM_TRANSACTION_TYPE:
                    cryptoGramTransactionType = Helper.hexStringToByteArray(tlvs.get(i).value);
                    break;
                case EmvReader.EMV_TAG_TERMINAL_COUNTER_CODE:
                    terminalCountryCode = Helper.hexStringToByteArray(tlvs.get(i).value);
                    break;
                case EmvReader.EMV_TAG_TERMINAL_TRANSACTION_DATE:
                    terminalTransactionDate = Helper.hexStringToByteArray(tlvs.get(i).value);
                    break;
                case EmvReader.EMV_TAG_CRYPTOGRAM_AMOUNT:
                    cryptoGramAmount = Helper.hexStringToByteArray(tlvs.get(i).value);
                    break;
                case EmvReader.EMV_TAG_CRYPTOGRAM_CURRENCY_CODE:
                    cryptoGramCurrencyCode = Helper.hexStringToByteArray(tlvs.get(i).value);
                    break;
                case EmvReader.EMV_TAG_CRYPTOGRAM_CASHBACK:
                    cryptoGramCashback = Helper.hexStringToByteArray(tlvs.get(i).value);
                    break;
            }

        }
        long myrand = (long) Math.floor(Math.random() * 900000) + 1000000;
        long check1 = Long.valueOf((terminalInfo.getTerNumber() + 2));//.substring(0, 8));//
        long checkCode1 = (myrand + check1) % 10;

        pan = cardSequenceNumber;

        long checkCode2 = (Long.valueOf(amount.trim()) + DaouDataHelper.getLong(pan)) % 10;

        retrievalReferenceNumber = String.valueOf(DateHelper.getCurrJulianDate()).substring(0, 4);
        retrievalReferenceNumber += DateHelper.getCurrenthhmmss();
        retrievalReferenceNumber += String.valueOf(checkCode1);
        retrievalReferenceNumber += String.valueOf(checkCode2);

        byte[] emvPart1 = (password + cardTypeCode).getBytes();
        byte[] emvPart2 = new byte[4];//emvDataLen
        byte[] emvPart3 = new byte[15];
        byte[] emvPart4 = addiontionPosInfomationByte;

        System.arraycopy(posEntryModeCode, 0, emvPart3, 0, 2);
        System.arraycopy(cardSequenceNumber, 0, emvPart3, 2, 1);
        System.arraycopy(retrievalReferenceNumber.getBytes(), 0, emvPart3, 3, 12);

        int emvLen = 0;

        int tmpLen = 0;
        System.arraycopy(emvPart1, 0, tmpBuff, 0, emvPart1.length);
        tmpLen += emvPart1.length;
        System.arraycopy(emvPart2, 0, tmpBuff, tmpLen, emvPart2.length);
        tmpLen += emvPart2.length;
        System.arraycopy(emvPart3, 0, tmpBuff, tmpLen, emvPart3.length);
        tmpLen += emvPart3.length;
        System.arraycopy(emvPart4, 0, tmpBuff, tmpLen, emvPart4.length);
        tmpLen += emvPart4.length;

        System.arraycopy(terminalCapilityProfile, 0, tmpBuff, tmpLen, terminalCapilityProfile.length);
        tmpLen += terminalCapilityProfile.length;

        System.arraycopy(terminalVerificationResults, 0, tmpBuff, tmpLen, terminalVerificationResults.length);
        tmpLen += terminalVerificationResults.length;

        System.arraycopy(unpredictableNumber, 0, tmpBuff, tmpLen, unpredictableNumber.length);
        tmpLen += unpredictableNumber.length;

        System.arraycopy(visaDiscretionanryData, 0, tmpBuff, tmpLen, visaDiscretionanryData.length);
        tmpLen += visaDiscretionanryData.length;

        System.arraycopy(issuerDiscretionaryData, 0, tmpBuff, tmpLen, issuerDiscretionaryData.length);
        tmpLen += issuerDiscretionaryData.length;

        System.arraycopy(cryptoGram, 0, tmpBuff, tmpLen, cryptoGram.length);
        tmpLen += cryptoGram.length;

        System.arraycopy(applicationTransactionCounter, 0, tmpBuff, tmpLen, applicationTransactionCounter.length);
        tmpLen += applicationTransactionCounter.length;

        System.arraycopy(applicationInterchangeProfile, 0, tmpBuff, tmpLen, applicationInterchangeProfile.length);
        tmpLen += applicationInterchangeProfile.length;

        System.arraycopy(cryptoGramTransactionType, 0, tmpBuff, tmpLen, cryptoGramTransactionType.length);
        tmpLen += cryptoGramTransactionType.length;

        System.arraycopy(terminalCountryCode, 0, tmpBuff, tmpLen, terminalCountryCode.length);
        tmpLen += terminalCountryCode.length;

        System.arraycopy(terminalTransactionDate, 0, tmpBuff, tmpLen, terminalTransactionDate.length);
        tmpLen += terminalTransactionDate.length;

        System.arraycopy(cryptoGramAmount, 0, tmpBuff, tmpLen, cryptoGramAmount.length);
        tmpLen += cryptoGramAmount.length;

        System.arraycopy(cryptoGramCurrencyCode, 0, tmpBuff, tmpLen, cryptoGramCurrencyCode.length);
        tmpLen += cryptoGramCurrencyCode.length;
        BHelper.db("len:" + tmpLen);

        System.arraycopy(cryptoGramCashback, 0, tmpBuff, tmpLen, cryptoGramCashback.length);
        tmpLen += cryptoGramCashback.length;

        //other chip data.
        String otherChipData = "";
        String tag9F27, tag9F10, tag9F03, tag9F34, tag9F35, tag9F1E, tag84, tag9F09, tag9F41, tag9F53, tag4F;
        tag9F27 = tag9F10 = tag9F03 = tag9F34 = tag9F35 = tag9F1E = tag84 = tag9F09 = tag9F41 = tag9F53 = tag4F = "";

        tag9F27 = DaouDataHelper.makeTagWithLen("9F27", 1);
        tag9F10 = DaouDataHelper.makeTagWithLen("9F10", 32);
        tag9F03 = DaouDataHelper.makeTagWithLen("9F03", 6);
        tag9F34 = DaouDataHelper.makeTagWithLen("9F34", 3);
        tag9F35 = DaouDataHelper.makeTagWithLen("9F35", 1);
        tag9F1E = DaouDataHelper.makeTagWithLen("9F1E", 8);
        tag84 = DaouDataHelper.makeTagWithLen("84", 16);
        tag9F09 = DaouDataHelper.makeTagWithLen("9F09", 2);
        tag9F41 = DaouDataHelper.makeTagWithLen("9F41", 4);
        tag9F53 = DaouDataHelper.makeTagWithLen("9F53", 1);
        tag4F = DaouDataHelper.makeTagWithLen("4F", 16);

        for (int i = 0; i < tlvs.size(); i++) {
            TLV tlv = tlvs.get(i);
            switch (tlv.tag.toUpperCase()) {
                case "9F27":
                    tag9F27 = tlv.tag + tlv.length + tlv.value;
                    break;
                case "9F10":
                    tag9F10 = tlv.tag + tlv.length + tlv.value;
                    break;
                case "9F03":
                    tag9F03 = tlv.tag + tlv.length + tlv.value;
                    break;
                case "9F34":
                    tag9F34 = tlv.tag + tlv.length + tlv.value;
                    break;
                case "9F35":
                    tag9F35 = tlv.tag + tlv.length + tlv.value;
                    break;
                case "9F1E":
                    tag9F1E = tlv.tag + tlv.length + tlv.value;
                    break;
                case "84":
                    tag84 = tlv.tag + tlv.length + tlv.value;
                    break;
                case "9F09":
                    tag9F09 = tlv.tag + tlv.length + tlv.value;
                    break;
                case "9F41":
                    tag9F41 = tlv.tag + tlv.length + tlv.value;
                    break;
                case "9F53":
                    tag9F53 = tlv.tag + tlv.length + tlv.value;
                    break;
                case "4F":
                    tag4F = tlv.tag + tlv.length + tlv.value;
                    break;
                default:
                    break;
            }
        }

        otherChipData = tag9F27 + tag9F10 + tag9F03 + tag9F34 + tag9F35 + tag9F1E + tag84 + tag9F09 + tag9F41 + tag9F53;
        if (cardTypeCode.equals("J"))
            otherChipData += tag4F;
        BHelper.db("Other Chip Data");
        EmvUtils.showTlv(otherChipData);
        //add more data for case visa

        if (cardTypeCode.equals("M")
                || cardTypeCode.equals("A")
                || cardTypeCode.equals("D")
                || cardTypeCode.equals("C")
                || cardTypeCode.equals("P")
                || cardTypeCode.equals("J")) {
            BHelper.db("otherChipData:" + otherChipData.toUpperCase());
            byte[] otherChipDataByte = Helper.hexStringToByteArray(otherChipData.toUpperCase());
            System.arraycopy(otherChipDataByte, 0, tmpBuff, tmpLen, otherChipDataByte.length);
            tmpLen += otherChipDataByte.length;
            otherChipDataByte = null;
            BHelper.db("len:" + tmpLen);
        } else if (cardTypeCode.equals("V"))
            BHelper.db("dont send otherChipData case V:" + otherChipData.toUpperCase());
        BHelper.db("len final:" + tmpLen);

        emvLen = tmpLen - (emvPart1.length + emvPart2.length);
        emvDataLen = String.format("%04d", emvLen);

        System.arraycopy(emvDataLen.getBytes(), 0, tmpBuff, emvPart1.length, 4);

        byte[] emvDataFin = new byte[tmpLen];
        System.arraycopy(tmpBuff, 0, emvDataFin, 0, tmpLen);
        String tmpEmvLog = "";
        tmpEmvLog += "password:" + password + "\n";
        tmpEmvLog += "cardTypeCode :" + cardTypeCode + "\n";
        tmpEmvLog += "emvDataLen :" + emvDataLen + "\n";
        tmpEmvLog += "posEntryModeCode :" + Helper.byte2hex2(posEntryModeCode) + "\n";
        tmpEmvLog += "cardSequenceNumber :" + Helper.byte2hex2(cardSequenceNumber) + "\n";
        tmpEmvLog += "retrievalReferenceNumber :" + retrievalReferenceNumber + "\n";
        tmpEmvLog += "terminalCapilityProfile :" + Helper.byte2hex2(terminalCapilityProfile) + "\n";
        tmpEmvLog += "TerminalVerificationResults :" + Helper.byte2hex2(terminalVerificationResults) + "\n";
        tmpEmvLog += "unpredictableNumber :" + Helper.byte2hex2(unpredictableNumber) + "\n";
        tmpEmvLog += "visaDiscretionanryData :" + Helper.byte2hex2(visaDiscretionanryData) + "\n";
        tmpEmvLog += "issuerDiscretionaryData :" + Helper.byte2hex2(issuerDiscretionaryData) + "\n";
        tmpEmvLog += "cryptoGram :" + Helper.byte2hex2(cryptoGram) + "\n";
        tmpEmvLog += "applicationTransactionCounter :" + Helper.byte2hex2(applicationTransactionCounter) + "\n";
        tmpEmvLog += "applicationInterchangeProfile :" + Helper.byte2hex2(applicationInterchangeProfile) + "\n";
        tmpEmvLog += "cryptoGramTransactionType :" + Helper.byte2hex2(cryptoGramTransactionType) + "\n";
        tmpEmvLog += "terminalCountryCode :" + Helper.byte2hex2(terminalCountryCode) + "\n";
        tmpEmvLog += "terminalTransactionDate :" + Helper.byte2hex2(terminalTransactionDate) + "\n";
        tmpEmvLog += "cryptoGramAmount :" + Helper.byte2hex2(cryptoGramAmount) + "\n";
        tmpEmvLog += "cryptoGramCurrenceCode :" + Helper.byte2hex2(cryptoGramCurrencyCode) + "\n";
        tmpEmvLog += "cryptoGramCashback :" + Helper.byte2hex2(cryptoGramCashback) + "\n";
        BHelper.db("++++++++ EMV DATA +++++++++++");
        BHelper.db(tmpEmvLog);
        BHelper.db(Helper.byte2hex2(emvDataFin));
        emvData = tag9F27 = tag9F27 = tag9F10 = tag9F03 = tag9F34 = tag9F35 = tag9F1E = tag84 = tag9F09 = tag9F41 = tag9F53 = tag4F  = otherChipData = tmpEmvLog = "";
        posEntryModeCode
                = cardSequenceNumber
                = addiontionPosInfomation
                = addiontionPosInfomationByte
                = pan
                = terminalCapilityProfile
                = terminalVerificationResults
                = unpredictableNumber
                = visaDiscretionanryData
                = issuerDiscretionaryData
                = cryptoGram
                = applicationTransactionCounter
                = applicationInterchangeProfile
                = cryptoGramTransactionType
                = terminalCountryCode
                = terminalTransactionDate
                = cryptoGramAmount
                = cryptoGramCurrencyCode
                = cryptoGramCashback = null;

        emvPart1 =
                emvPart2 =
                        emvPart3 =
                                emvPart4 = null;
        emvLen = tmpLen = 0;
        retrievalReferenceNumber = "";
        return Base64Utils.base64Encode(emvDataFin);
    }

    protected ReceiptEntity parseResp(String[] resp, ReceiptEntity reqEntity) {
        reqEntity.setF_ApprovalCode(resp[5]);//approval number
        reqEntity.setF_BuyerName(DaouDataHelper.getSubString(resp[7], 4));
        reqEntity.setF_CardNo(DaouDataHelper.cleanData(resp[10]));
        reqEntity.setF_revMessage(resp[22]);
        String revDate = DateHelper.formatRevDateDaouData(resp[3]);
        reqEntity.setF_RequestDate(revDate);
        reqEntity.setF_revDate(revDate);
        StaticData.bank_card_balance_amout = resp[12];
        return reqEntity;

    }

    void setEncData(EncPayInfo encData) {
        this.emvData = encData.getEmvData();
        this.signData = encData.getSignData();
        this.passWord = encData.getPassWord();
    }

    protected String requestTrans(ReceiptEntity reqEntity, byte[] data) {
        String[] recv = sendMsg(data);
        BHelper.db("========RESP DATA======");
        switch (DaouData.getNetworkResult()){
            case DaouData.NETWORK_RESULT_DLE:
            case DaouData.NETWORK_RESULT_SOCKET_ERROR:
                AppHelper.setVanMsg("");
                return "";
            default:
                break;
        }

        BHelper.db(getResp(recv));
        String respCode = recv[1];
        String emvResp = recv[17];
        String emvOption = "";
        if (emvResp != null && !emvResp.equals("")) {
            emvResp = new String(Base64Utils.base64Decode(emvResp));
            emvOption = emvResp.substring(0, 1);
        }

        BHelper.db("empResp:" + emvResp);
        ReceiptEntity reciptEn = null;
        AppHelper.setVanMsg(recv[21]);
        InCompleteDataEntity entity = new InCompleteDataEntity(recv);
        InCompleteDataEntity entity1 = AppHelper.getInCompleteData();
        entity.setCardInfo(entity1.getCardInfo());
        AppHelper.setInCompleteData(entity);

        if(DaouData.getNetworkResult().equals(DaouData.NETWORK_RESULT_NO_EOT)){

            if(respCode.equals(DaouDataContants.VAL_RESP_CODE_SUCCESS))
                AppHelper.setVanMsg("");
            return "";
        }else if (respCode.equals(DaouDataContants.VAL_RESP_CODE_SUCCESS)) {

            if(taskCode.equals(DaouDataContants.TASK_NO_EOT)){
                BHelper.db("TASK_NO_EOT");
                return "";
            }
            if(reqEntity.getF_TotalAmount().equals("1")){
                AppHelper.setVanMsg(recv[21]+ " "+ recv[22] + " " + recv[23]);
                return "";
            }
            reciptEn = parseResp(recv, reqEntity);
            AppHelper.setReceipt(reciptEn);
//            String reciptEnJson = VanHelper.payment(reciptEn);

            BHelper.db("emvOption:" + emvOption);
            //check to write back card
            if (emvOption != null && emvOption.equals("Y"))
                StaticData.creditSuccessWithEmv = true;
            else
                StaticData.creditSuccessWithEmv = false;

            //Send back TC to VAN
            if(reciptEn.getF_Type().equals(StaticData.paymentTypeCredit) && (reciptEn.getF_TypeSub().equals(StaticData.CREDIT_SUBTYPE_ICC)||reciptEn.getF_TypeSub().equals(StaticData.CREDIT_SUBTYPE_ICC_UNION))){

                if(emvOption==null || emvOption.trim().equals(""))
                    emvOption = "N";
                String transDate = recv[3];
                transDate = transDate.substring(0,8);
                String approvalNo = reciptEn.getF_ApprovalCode();
                String transUniqueNo = recv[4];
                String tc = getEmvRespTC(recv[17]);

                EmvTcEntity tcEntity = new EmvTcEntity(transDate, approvalNo, transUniqueNo, tc,emvOption);
                AppHelper.setEmvTcInfo(tcEntity);
            }else{
                String reciptEnJson = VanHelper.payment(reciptEn);
                if (reciptEnJson != null) {
                    StaticData.sResultPayment = reciptEnJson;
                }
            }

//            if (reciptEnJson != null) {
//                StaticData.sResultPayment = reciptEnJson;
                emvResp = recv[17];
                BHelper.db("send back Emv from VAN: " + emvResp);
                if (emvResp == null || emvResp.trim().equals(""))
                    emvResp = "8A023030";
//                reciptEnJson = "";
                reciptEn = null;
                return emvResp;
//            }

        } else {
            AppHelper.setVanMsg(recv[21]);
        }
        return "";
    }

    private String getEmvRespTC(String emvResp) {
        String tcData="";
        byte[] emvPart1 = new byte[4];//emvData length
        byte[] emvPart2 = new byte[8];//application cryptogram
        byte[] emvPart3 = new byte[1];
        byte[] emvPart4 = new byte[20];

        if (emvResp.length() < 10)
            return "00";
        try {
            byte[] emv = Base64Utils.base64Decode(emvResp);
            int counter = 0;
            BHelper.db("emv:" + Helper.byte2hex(emv));
            byte[] len = new byte[2];
            System.arraycopy(emv, 3, len, 0, 2);
            counter += 5;

            int addRespDataLen = emv[5];

            byte[] addRespDataByte = new byte[addRespDataLen];
            counter += 1;
            System.arraycopy(emv, counter, addRespDataByte, 0, addRespDataLen);
            BHelper.db("addRespDataByte in getEmvRespTC:" + Helper.byte2hex(addRespDataByte));
            counter += addRespDataLen;

            byte[] issuerAuthData = new byte[10];

            System.arraycopy(emv, counter, issuerAuthData, 0, 10);
            String issuerAuthDataStr = Helper.byte2hex(issuerAuthData);
            BHelper.db("issuerAuthData in getEmvRespTC:" + Helper.byte2hex(issuerAuthData));

            byte[] respCode = new byte[2];
            System.arraycopy(issuerAuthData, 8, respCode, 0, 2);
            String respCodeStr = Helper.byte2hex(respCode);
            BHelper.db("respCode in getEmvRespTC:" + respCodeStr);
            counter += 10;
            int issuerScriptLen = emv[counter];
            int tcLen = 0;
            tcData = EmvUtils.getApplicationCryptogram();
            emvPart2 = Helper.hexStringToByteArray(EmvUtils.getApplicationCryptogram());
            tcLen += 8;

            if (issuerScriptLen > 0) {
                byte[] issuerScript = new byte[issuerScriptLen];
                System.arraycopy(emv, counter, issuerScript, 0, issuerScriptLen);
                String issuerScriptStr = Helper.byte2hex(issuerScript);
                tcData += String.valueOf(issuerScriptLen) + issuerScriptStr;
                tcLen += 1 + issuerScriptLen;
                emvPart3[0] = emv[counter];
                System.arraycopy(issuerScript,0,emvPart4,0,issuerScriptLen);

            }else{
                emvPart3[0]=0x00;
                tcData+="00";
                tcLen+=1;
            }
            tcData = String.format("%04d", tcLen) + tcData;
            String emvDataLen = String.format("%04d", tcLen);
            System.arraycopy(emvDataLen.getBytes(), 0, emvPart1, 0, 4);
            int i = 0;
            byte[] finalBuff = new byte[tcLen+4];
            System.arraycopy(emvPart1,0,finalBuff,i,4);

            i+=4;
            System.arraycopy(emvPart2,0,finalBuff,i,8);
            i+=8;
            System.arraycopy(emvPart3,0,finalBuff,i,1);
            i+=1;
            if (issuerScriptLen > 0) {
                System.arraycopy(emvPart4,0,finalBuff,i,issuerScriptLen);
            }
            BHelper.db("getEmvRespTC:"+ HexDump.toHexString(finalBuff));
            return Base64Utils.base64Encode(finalBuff);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    protected String requestTransCancel(ReceiptEntity reqEntity, byte[] data) {
        String[] recv = sendMsg(data);
        BHelper.db("========RESP DATA======");
        switch (DaouData.getNetworkResult()){
            case DaouData.NETWORK_RESULT_DLE:
            case DaouData.NETWORK_RESULT_SOCKET_ERROR:
                AppHelper.setVanMsg("");
                return "";
            default:
                break;
        }

        BHelper.db(getResp(recv));
        String respCode = recv[1];
        String emvResp = recv[17];
        String emvOption = "";
        if (emvResp != null && !emvResp.equals("")) {
            emvResp = new String(Base64Utils.base64Decode(emvResp));
            emvOption = emvResp.substring(0, 1);
        }

        BHelper.db("respCode:" + respCode);
        ReceiptEntity reciptEn = null;
        InCompleteDataEntity entity = new InCompleteDataEntity(recv);
        InCompleteDataEntity entity1 = AppHelper.getInCompleteData();
        entity.setCardInfo(entity1.getCardInfo());
        AppHelper.setInCompleteData(entity);

        if(DaouData.getNetworkResult().equals(DaouData.NETWORK_RESULT_NO_EOT)) {
            if(respCode.equals(DaouDataContants.VAL_RESP_CODE_SUCCESS))
                AppHelper.setVanMsg("");
            return "";
        }
        BHelper.db("receipt to cancel:" + reqEntity.toString());
        AppHelper.setVanMsg(recv[21]);
        if (respCode.equals(DaouDataContants.VAL_RESP_CODE_SUCCESS)) {

            if(taskCode.equals(DaouDataContants.TASK_NO_EOT)){
                BHelper.db("TASK_NO_EOT");
                return "";
            }
            if(reqEntity.getF_TotalAmount().equals("1")){
                AppHelper.setVanMsg(recv[21]+ " "+ recv[22] + " " + recv[23]);
                return "";
            }
            reciptEn = parseResp(recv, reqEntity);
            recv = null;
            return VanHelper.cancel(reciptEn);
        } else {
            AppHelper.setVanMsg(recv[21]);
        }
        recv = null;
        return "";
    }

    public static String makeEmvResponse(String respBase64) {
        String cardType = "";
        return makeEmvResponse(cardType, respBase64);
    }

    public static String makeEmvResponseSimple() {
       return "8A023030";
    }
    public static String makeEmvResponseDecline() {
        return "8A023035";
    }

    public static String makeEmvResponse(String cardType, String respBase64) {
        String ret = "";
        if (respBase64.length() < 10)
            return "";
        try {
            ret = "8A02";
            byte[] emv = Base64Utils.base64Decode(respBase64);
            int counter = 0;
            BHelper.db("emv:" + Helper.byte2hex(emv));
            byte[] len = new byte[2];
            System.arraycopy(emv, 3, len, 0, 2);
            counter += 5;

            int addRespDataLen = emv[5];

            byte[] addRespDataByte = new byte[addRespDataLen];
            counter += 1;
            System.arraycopy(emv, counter, addRespDataByte, 0, addRespDataLen);
            BHelper.db("addRespDataByte:" + Helper.byte2hex(addRespDataByte));
            counter += addRespDataLen;

            byte[] issuerAuthData = new byte[10];

            System.arraycopy(emv, counter, issuerAuthData, 0, 10);
            String issuerAuthDataStr = Helper.byte2hex(issuerAuthData);
            BHelper.db("issuerAuthData:" + Helper.byte2hex(issuerAuthData));

            byte[] respCode = new byte[2];
            System.arraycopy(issuerAuthData, 8, respCode, 0, 2);
            String respCodeStr = Helper.byte2hex(respCode);
            BHelper.db("respCode:" + respCodeStr);
            counter+=10;
            int issuerScriptLen = emv[counter];
            int tcLen = 0;
            String tcData =Helper.byte2hex("00".getBytes());
            tcLen+=8;

            if(issuerScriptLen>0){
                byte[] issuerScript = new byte[issuerScriptLen];
                System.arraycopy(emv, counter, issuerScript, 0, issuerScriptLen);
                String issuerScriptStr =  Helper.byte2hex(issuerScript);
                tcData+=String.valueOf(issuerScriptLen)+issuerScriptStr;
                tcLen+=issuerScriptLen;
            }
            tcData = String.format("%04d", tcLen)+ tcData;


//            if (cardType.equals("M"))
            if (true){
                respCodeStr = "3030";
                respCodeStr += "910A" + issuerAuthDataStr;
                BHelper.db("respCode convert to:" + respCodeStr);
            }
            ret += respCodeStr;
            counter = 0;
            emv = len = addRespDataByte = issuerAuthData = respCode = null;
            issuerAuthDataStr ="";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }
}
