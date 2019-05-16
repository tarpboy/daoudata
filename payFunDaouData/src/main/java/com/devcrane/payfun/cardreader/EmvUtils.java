package com.devcrane.payfun.cardreader;

import android.app.Activity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.bbpos.bbdevice.PayfunBBDeviceController;
import com.devcrane.android.lib.emvreader.EmvReader;
import com.devcrane.android.lib.emvreader.TLV;
import com.devcrane.android.lib.emvreader.TLVParser;
import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.entity.CompanyEntity;
import com.devcrane.payfun.daou.entity.KeyBindingEntity;
import com.devcrane.payfun.daou.manager.CompanyManger;
import com.devcrane.payfun.daou.manager.KeyBindingManager;
import com.devcrane.payfun.daou.utility.AppHelper;
import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.Helper;
import com.devcrane.payfun.daou.van.DaouDataContants;
import com.devcrane.payfun.daou.van.DaouDataHelper;
import com.devcrane.payfun.daou.van.KSNEntity;

public class EmvUtils {
    public static String EMV_TEST = "";
    public final static String EMV_APPLICATION_CRYPTOGRAM = "EMV_APPLICATION_CRYPTOGRAM";

    private static byte[] hexToByteArray(String s) {
        if (s == null) {
            s = "";
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        for (int i = 0; i < s.length() - 1; i += 2) {
            String data = s.substring(i, i + 2);
            bout.write(Integer.parseInt(data, 16));
        }
        return bout.toByteArray();
    }

    public static Hashtable<String, String> decodeTlv(String tlv) {
        Hashtable<String, String> decodeTlv = PayfunBBDeviceController.decodeTlv(tlv);
        return decodeTlv;
    }

    public static List<TLV> decodeTlvToList(String tlv) {
        Hashtable<String, String> decodeTlv = decodeTlv(tlv);
        List<TLV> tlvs = new ArrayList<TLV>();
        Set<String> keys = decodeTlv.keySet();
        for (String key : keys) {
            TLV tlvItem = new TLV();
            tlvItem.tag = key;
            tlvItem.value = decodeTlv.get(key);
            tlvItem.length = String.valueOf(decodeTlv.get(key).length());
            tlvs.add(tlvItem);
        }
        return tlvs;
    }

    public static void showTlv(String tlv) {
        Hashtable<String, String> decodeTlv = PayfunBBDeviceController.decodeTlv(tlv);
        Set<String> keys = decodeTlv.keySet();
        for (String key : keys) {
            BHelper.db("tag:" + key + " value: " + decodeTlv.get(key) + " len:" + decodeTlv.get(key).length());
        }
    }

    public static void saveEmvData(String emvData) {
        AppHelper.prefSet(EmvReader.EMV_DATA, emvData);
    }

    public static void saveApplicationCryptogram(String data) {
        AppHelper.prefSet(EMV_APPLICATION_CRYPTOGRAM, data);
    }
    public static String getApplicationCryptogram() {
        return AppHelper.prefGet(EMV_APPLICATION_CRYPTOGRAM, "");
    }

    public static void saveKsn(KSNEntity ksnEntity) {
        BHelper.db("saveEmvSerial");
        AppHelper.prefSet(EmvReader.EMV_KSN, ksnEntity.getPinKsn());
    }

    public static void saveKsn(String ksn) {
        BHelper.db("saveEmvSerial");
        AppHelper.prefSet(EmvReader.EMV_KSN, ksn);
    }

    public static void saveKeyBinding(KeyBindingEntity bindingEntity) {
//		KSNEntity ksnEntity = ksnReturnEntity;

        String companyID = AppHelper.getCurrentVanID();
        BHelper.db("companyID:" + companyID);
        CompanyEntity comE = CompanyManger.getCompany(companyID);
        if (comE == null)
            return;
        bindingEntity.setF_CompanyNo(comE.getF_CompanyNo());
        bindingEntity.setF_MachineNo(comE.getF_MachineCode());
        BHelper.db("will be saved keyBinding:" + bindingEntity.toString());
        boolean ret = KeyBindingManager.insert(bindingEntity);
        BHelper.db("insert result:" + ret);

    }

    public static KeyBindingEntity getKeyBinding(String deviceNo) {
        String companyID = AppHelper.getCurrentVanID();
        String currentUserID = AppHelper.getCurrentUserID();

        if(!CompanyManger.isExistCompanyLocal(currentUserID)){
            BHelper.db("Need to download company first");
            return null;
        }

        if (CompanyManger.isExistCompanyLocal(currentUserID) && companyID.equals("")) {
            BHelper.showToast(R.string.please_select_company_first);
            return null;
        }

        BHelper.db("Get Key Binding");
        BHelper.db("companyID:" + companyID);
        CompanyEntity comE = CompanyManger.getCompany(companyID);
        KeyBindingEntity bindingEntity = KeyBindingManager.getKB(comE.getF_CompanyNo(), comE.getF_MachineCode(), deviceNo);
        return bindingEntity;
    }

    public static String extractSerialNumber(String pinKsn) {
        if (pinKsn != null && pinKsn.length() > 14) {
            return pinKsn.substring(6, 14);
        }
        return "";
    }
    public static void cleanDeviceValue(){
        AppHelper.removeDataWithKey(EmvReader.EMV_SERIAL_NO);
        AppHelper.removeDataWithKey(EmvReader.EMV_DATA);
        AppHelper.removeDataWithKey(EmvReader.HW_MODEL_NAME);
        AppHelper.removeDataWithKey(EmvReader.HW_MODEL_NO);
        AppHelper.removeDataWithKey(EmvReader.EMV_SERIAL_NO);
        AppHelper.removeDataWithKey(EmvReader.EMV_KSN);
        AppHelper.removeDataWithKey(EMV_APPLICATION_CRYPTOGRAM);
//        AppHelper.removeDataWithKey(StaticData.VANID);
    }
    public static void saveEmvSerial(String pinKsn) {
        BHelper.db("saveEmvSerial");
        AppHelper.prefSet(EmvReader.EMV_SERIAL_NO, getSerial(pinKsn));
    }

    public static String getSerial(String pinKsn) {
        return "PF" + (pinKsn.length() > 15 ? pinKsn.substring(6, 14) : "");
    }


    public static String getEmvSerialNo() {
        return AppHelper.prefGet(EmvReader.EMV_SERIAL_NO, "PF15120001");
    }

    public static String getEmvData() {
        return AppHelper.prefGet(EmvReader.EMV_DATA, EMV_TEST);
    }

    public static String getKsn() {
        return AppHelper.prefGet(EmvReader.EMV_KSN, "");
    }

    public static boolean getIsReadyIC() {
        String val = AppHelper.prefGet("IS_READY_IC", "");
        if (val.equals("true")) {
            return true;
        }
        return false;
    }

    public static void setIsReadyIC(boolean val) {
        if (val)
            AppHelper.prefSet("IS_READY_IC", "true");
        else {
            AppHelper.prefSet("IS_READY_IC", "false");
        }
    }


    public static void saveHWModelName(String swModelName) {
        BHelper.db("saveHWModelName");
        AppHelper.prefSet(EmvReader.HW_MODEL_NAME, swModelName);
    }

    public static String getHWModelName() {
        return AppHelper.prefGet(EmvReader.HW_MODEL_NAME, "DAOUPAYFUND0");
    }
    public static boolean isValidHWModelName(){
        String modelName = getHWModelName();
        if(modelName.contains("DAOUPAYFUN"))
            return true;
        return false;

    }

    public static void saveHwSerialNumber(String serial) {
        BHelper.db("saveHwSerialNumber");
        AppHelper.prefSet("PRODUCTION_SERIAL_NUMBER", serial);
    }

    public static String getHWSerialNumber() {
        return AppHelper.prefGet("PRODUCTION_SERIAL_NUMBER", DaouDataContants.VAL_PRODUCTION_SERIAL_NUMBER);
    }


    public static void savePublicKeyVersion(String publicKeyVer) {
        BHelper.db("savePublicKeyVersion");
        AppHelper.prefSet(EmvReader.DEVICE_PUBLIC_KEY_VERSION, publicKeyVer);
    }

    public static String getPublicKeyVersion() {
        return AppHelper.prefGet(EmvReader.DEVICE_PUBLIC_KEY_VERSION, "F5");
    }


    public static void saveHwModelNo(String hwModelNo) {
        BHelper.db("saveHwModelNo");
        AppHelper.prefSet(EmvReader.HW_MODEL_NO, hwModelNo);
    }

    public static String getHwModelNo() {
        return AppHelper.prefGet(EmvReader.HW_MODEL_NO, "1000");
    }


    public static void saveKsn(KeyBindingEntity bindingEntity) {
        boolean res = KeyBindingManager.insert(bindingEntity);
        BHelper.db("saveKsn DB: " + res);
    }

    public static String increaseIdentifierKsn(String ksn) {
        String ret = "";
        if (ksn != null && ksn.length() > 2) {
            String hex = ksn.substring(0, 2);
            Integer value = Integer.parseInt(hex, 16);
            value += 1;
            byte[] tmp = new byte[1];
            tmp[0] = value.byteValue();
            ret = EmvUtils.toHexString(tmp) + ksn.substring(2);
        }

        return ret;
    }

    public static String toHexString(byte[] b) {
        if (b == null) {
            return "null";
        }
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xFF) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static String encrypt(String data, String key) {
        if (key.length() == 16) {
            key += key.substring(0, 8);
        }
        byte[] d = hexToByteArray(data);
        byte[] k = hexToByteArray(key);

        SecretKey sk = new SecretKeySpec(k, "DESede");
        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, sk);
            byte[] enc = cipher.doFinal(d);
            return toHexString(enc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void showEmvData() {
        String tagValue = "";
        String emvData = EMV_TEST;
        List<TLV> tlvs = decodeTlvToList(emvData);
        for (int i = 0; i < tlvs.size(); i++) {
            int tmp = i + 1;
            BHelper.db("TLV no: " + tmp + " tag:" + tlvs.get(i).tag + " value:" + tlvs.get(i).value + " length:" + tlvs.get(i).length);
        }
    }

    public static void showEmvDataManual() {
        String tagValue = "";
        String emvData = EMV_TEST;
        List<TLV> tlvs = TLVParser.parse(emvData);
        for (int i = 0; i < tlvs.size(); i++) {
            int tmp = i + 1;
            BHelper.db("TLV no: " + tmp + " tag:" + tlvs.get(i).tag + " value:" + tlvs.get(i).value + " length:" + tlvs.get(i).length
                    + "\nascii val:" + new String(hexToByteArray(tlvs.get(i).value)));
        }
    }


    public static String getTagFromEmvData(String tagName) {
        String emvData = EmvUtils.getEmvData();
        return getTagFromEmvData(emvData, tagName);
    }

    public static String extractMaskTrack2(String emvData) {
        String track2Display = EmvUtils.getTagFromEmvData(emvData, EmvReader.EMV_TAG_C4);
        return formatMaskedTrack2(track2Display);
    }

    public static String formatMaskedTrack2(String maskedPAN) {
        String res = "";
        int maskedLen = maskedPAN.length();
        if(maskedLen<=6)
            return  maskedPAN;

        String part1 = "";
        String part2 = "";
        String part3 = "";
        int part1Len = 6;
        int part2Len = 6;
        int part3Len = 4;

        if (maskedLen > 11) {
            part1Len = 6;
        } else {
            part1Len = 3;
        }
        part1 = maskedPAN.substring(0, part1Len);
        part3 = maskedPAN.substring(maskedLen - 4, maskedLen);
        part2Len = maskedLen - (part1Len + part3Len);
        part2 = DaouDataHelper.appendChar(part1, '*', part1Len + part2Len);
        res = part2 + part3;
        return res;
    }

    public static String getTagFromEmvData(String emvData, String tagName) {
        BHelper.db("emvData:" + emvData);
        String tagValue = "";
        List<TLV> tlvs = TLVParser.parse(emvData);
        for (int i = 0; i < tlvs.size(); i++) {
            int tmp = i + 1;
            if (tlvs.get(i).tag.equals(tagName)) {
                tagValue = tlvs.get(i).value;
                tagValue = tagValue.toUpperCase();
                BHelper.db("selected tag : " + tagName + " value: " + tagValue);
                break;
            }
        }
        return tagValue;
    }

    public static void resetEmvData() {
        saveEmvData("");

    }

    public static String getTrack2DataBase64FromEncoded(String encryptedCardData) {
        byte[] encTrack2Byte = Helper.hexStringToByteArray(encryptedCardData);
        int cardDataLen = 0;

        String track2_base64 = new String(encTrack2Byte);
        BHelper.db("track2_base64 from device: " + track2_base64);
        String Track2Data = "";
        Track2Data += track2_base64;
        cardDataLen = Track2Data.length();
        Track2Data = String.valueOf(cardDataLen) + Track2Data;
        return Track2Data;
    }

    public static String getEmvErrorString(Activity at, PayfunBBDeviceController.Error errorState) {
        String error = "Error";
        if (errorState == PayfunBBDeviceController.Error.CMD_NOT_AVAILABLE) {
            error = (at.getString(R.string.command_not_available));
        } else if (errorState == PayfunBBDeviceController.Error.TIMEOUT) {
            error = (at.getString(R.string.device_no_response));
        } else if (errorState == PayfunBBDeviceController.Error.UNKNOWN) {
            error = (at.getString(R.string.unknown_error));
        } else if (errorState == PayfunBBDeviceController.Error.DEVICE_BUSY) {
            error = (at.getString(R.string.device_busy));
        } else if (errorState == PayfunBBDeviceController.Error.INPUT_OUT_OF_RANGE) {
            error = (at.getString(R.string.out_of_range));
        } else if (errorState == PayfunBBDeviceController.Error.INPUT_INVALID_FORMAT) {
            error = (at.getString(R.string.invalid_format));
        } else if (errorState == PayfunBBDeviceController.Error.INPUT_INVALID) {
            error = (at.getString(R.string.input_invalid));
        } else if (errorState == PayfunBBDeviceController.Error.CASHBACK_NOT_SUPPORTED) {
            error = (at.getString(R.string.cashback_not_supported));
        } else if (errorState == PayfunBBDeviceController.Error.CRC_ERROR) {
            error = (at.getString(R.string.crc_error));
        } else if (errorState == PayfunBBDeviceController.Error.COMM_ERROR) {
            error = (at.getString(R.string.comm_error));
        } else if (errorState == PayfunBBDeviceController.Error.VOLUME_WARNING_NOT_ACCEPTED) {
            error = (at.getString(R.string.volume_warning_not_accepted));
        } else if (errorState == PayfunBBDeviceController.Error.FAIL_TO_START_AUDIO) {
            error = (at.getString(R.string.fail_to_start_audio));
        } else if (errorState == PayfunBBDeviceController.Error.FAIL_TO_START_BT) {
            error = (at.getString(R.string.fail_to_start_bluetooth));
        }
        if (errorState == PayfunBBDeviceController.Error.COMM_LINK_UNINITIALIZED) {
            error = (at.getString(R.string.comm_link_uninitialized));
        }
        if (errorState == PayfunBBDeviceController.Error.INVALID_FUNCTION_IN_CURRENT_CONNECTION_MODE) {
            error = (at.getString(R.string.invalid_function_in_current_mode));
        }
        if (errorState == PayfunBBDeviceController.Error.USB_DEVICE_NOT_FOUND) {
            error = (at.getString(R.string.usb_device_not_found));
        }
        if (errorState == PayfunBBDeviceController.Error.USB_DEVICE_PERMISSION_DENIED) {
            error = (at.getString(R.string.usb_device_permission_denied));
        }
        return error;
    }


}
