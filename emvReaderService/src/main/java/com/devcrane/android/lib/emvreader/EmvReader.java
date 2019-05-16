package com.devcrane.android.lib.emvreader;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.bbpos.bbdevice.BBDeviceController;
import com.bbpos.bbdevice.CAPK;
import com.bbpos.bbdevice.PayfunBBDeviceController;
import com.devcrane.android.lib.entity.KeyExchangeResultEntity;
import com.devcrane.android.lib.utility.BHelper;
import com.devcrane.android.lib.utility.Helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class EmvReader {
	
	public PayfunBBDeviceController emvSwipeController;
	public static final String ICCARD_TAG = "ICCARD";
	public static final String HW_MODEL_NAME = "HW_MODEL_NAME";
	public static final String DEVICE_PUBLIC_KEY_VERSION="DEVICE_PUBLIC_KEY_VERSION";
	public static final String HW_MODEL_NO = "HW_MODEL_NO";
	public static final String EMV_SERIAL_NO = "EMV_SERIAL_NO";
	public static final String EMV_DATA = "EMV_DATA";
	public static final String EMV_KSN = "EMV_KSN";
	public static final String EMV_FIRMWARE_VERS = "EMV_FIRMWARE_VERS";
	public static final String EMV_TAG_C3 = "c3";
	public static final String EMV_TAG_C4 = "c4";
	public static final String EMV_TAG_C7 = "c7";
	public static final String EMV_TAG_C8 = "c8";
	public static final String EMV_TAG_KSN_TRACK2 = "df8315";//"df8317";//
	public static final String EMV_TAG_POS_ENTRY_MODE_CODE		="9f39";
	public static final String EMV_TAG_ADDITIONAL_POS_INFOMATION		="9f40";
	public static final String EMV_TAG_CARD_SEQUENCE_NUMBER	="5f34";
	public static final String EMV_TAG_RETRIEVAL_REFERENCE_NUMBER	="";
	public static final String EMV_TAG_TERMINAL_CAPILITY_PROFILE	="9f33";
	public static final String EMV_TAG_TERMINAL_VERIFICATION_RESULTS	="95";
	public static final String EMV_TAG_UNPREDICTABLE_NUMBER	="9f37";
	public static final String EMV_TAG_VISA_DISCRETIONARY_DATA	="9f10";
	public static final String EMV_TAG_ISSUER_DISCRETIONARY_DATA	="";
	public static final String EMV_TAG_CRYPTOGRAM	="9f26";
	public static final String EMV_TAG_APPLICATION_TRANSACTON_COUNTER	="9f36";
	public static final String EMV_TAG_APPLICATION_INTERCHANGE_PROFILE	="82";
	public static final String EMV_TAG_CRYPTOGRAM_TRANSACTION_TYPE	="9c";
	public static final String EMV_TAG_TERMINAL_COUNTER_CODE	="9f1a";
	public static final String EMV_TAG_TERMINAL_TRANSACTION_DATE	="9a";
	public static final String EMV_TAG_CRYPTOGRAM_AMOUNT	="9f02";
	public static final String EMV_TAG_CRYPTOGRAM_CURRENCY_CODE	="5f2a";
	public static final String EMV_TAG_CRYPTOGRAM_CASHBACK	="9f03";
	public static final String EMV_TAG_CARD_TYPE ="9f06";//"4f";//
	public static final String EMV_TAG_CARD_TYPE_VAL_1_VISA_CREDIT_OR_DEBIT ="A0000000031010";
	public static final String EMV_TAG_CARD_TYPE_VAL_2_VISA_ELECTRON ="A0000000032010";
	public static final String EMV_TAG_CARD_TYPE_VAL_3_MASTERCARD_CREDIT_OR_DEBIT ="A0000000041010";
	public static final String EMV_TAG_CARD_TYPE_VAL_4_MASTERCARD_MAESTRO ="A0000000043060";
	public static final String EMV_TAG_CARD_TYPE_VAL_5_AMERICAN_EXPRESS ="A00000002501";
	public static final String EMV_TAG_CARD_TYPE_VAL_6_JCB ="A0000000651010";
	public static final String EMV_TAG_CARD_TYPE_VAL_7_DINER_CLUB_DISCOVER ="A0000001523010";
	public static final String EMV_TAG_CARD_TYPE_VAL_8_UNION_PAY_DEBIT ="A000000333010101";
	public static final String EMV_TAG_CARD_TYPE_VAL_9_UNION_PAY_CREDIT ="A000000333010102";
	public static final String EMV_TAG_CARD_TYPE_VAL_10_LOCAL_VISA ="D4100000011010";
	public static final String EMV_TAG_CARD_TYPE_VAL_11_LOCAL_MASTER ="D4100000012010";
	public static final String EMV_TAG_CARD_TYPE_VAL_12_LOCAL_DEBIT ="D4100000012020";
	public static final String EMV_TAG_CARD_TYPE_VAL_13_AMEX ="A00000002501";
	public static final String EMV_TAG_CARD_TYPE_VAL_14_DPAS ="A0000001523010";
	public static final String EMV_TAG_CARD_TYPE_VAL_15_CUPIC ="A0000003330101";
	
	
	public static final String CMD_GET_DEVICE_INFO ="GET_DEVICE_INFO";
	public static final String CMD_INTEGRITY_CHECK ="INTEGRITY_CHECK";
	public static final String CMD_NONE ="NONE";
	public static String cmdInUsed ="NONE";
	static boolean isBT = false;
	
	BBDeviceController.CheckCardMode checkCardMode;
	ArrayList<EmvReaderListener>	emvReaderListeners = null;
	ArrayList<IntegrityCheckListener>	integrityCheckListeners = null;
	ArrayList<DetectEmvListener>	detectEmvListers = null;
    ArrayList<BlueToothListener>	blueToothListeners = null;
	
	
	public static boolean isManualKeyBinding = false;
	boolean isForCancel = false;
	public String[] fids = new String[] {
    		"FID22",
    		"FID36",
    		"FID46",
    		"FID54",
    		"FID55",
    		"FID60",
			"FID61",
			"FID64",
			"FID65",
	};
	public static String fid;
	private Context at;
	ProgressDialog dialog;
    public static final int READER_TYPE_EARJACK = 1;
    public static final int READER_TYPE_BT = 0;
    public static void setIsBlueTooth(boolean value){
        isBT = value;
    }
    public static boolean getIsBlueTooth() {
        return isBT;
    }
	public static void setReaderType(int type){
        if(type==READER_TYPE_BT)
            isBT = true;
        else
            isBT = false;
    }
	public EmvReader(Context ctx){
		this.at = ctx;
//		if(isBT)
//			initWisepadReader();
//		else
			initEMVReader();
	}
//	void initWisepadReader(){
//		cmdInUsed = CMD_NONE;
//		if (wisePadController == null) {
//			MyWisePadControllerListener listener = new MyWisePadControllerListener(emvReaderListeners,integrityCheckListeners,detectEmvListers);
//			wisePadController = PayfunWisePadController.getInstance(this.at, listener);
//			PayfunWisePadController.setDebugLogEnabled(true);
//		}
//	}
	public void attachEmvReaderListener( EmvReaderListener listener ) {
		if( emvReaderListeners == null )
			emvReaderListeners = new ArrayList<EmvReaderListener>();
		BHelper.db("attachOnReadListener");
		BHelper.db("emvReaderListeners count 1:"+emvReaderListeners.size());
		for(int i=0;i<emvReaderListeners.size();i++){
			emvReaderListeners.remove(i);
		}
		BHelper.db("emvReaderListeners count 2:"+emvReaderListeners.size());
		emvReaderListeners.add(listener);
		BHelper.db("emvReaderListeners count 3:"+emvReaderListeners.size());
	}

    public void attachBlueToothListener( BlueToothListener listener ) {
        if( blueToothListeners == null )
            blueToothListeners = new ArrayList<BlueToothListener>();
        BHelper.db("attachOnReadListener");
        BHelper.db("blueToothListeners count 1:"+blueToothListeners.size());
        for(int i=0;i<blueToothListeners.size();i++){
            blueToothListeners.remove(i);
        }
        BHelper.db("blueToothListeners count 2:"+blueToothListeners.size());
        blueToothListeners.add(listener);
        BHelper.db("blueToothListeners count 3:"+blueToothListeners.size());
    }
	
	public void attachDetectEmvListener( DetectEmvListener listener ) {
		if( detectEmvListers == null )
			detectEmvListers = new ArrayList<DetectEmvListener>();
		BHelper.db("attachOnReadListener");
		BHelper.db("detectEmvListers count 1:"+detectEmvListers.size());
		for(int i=0;i<detectEmvListers.size();i++){
			detectEmvListers.remove(i);
		}
		BHelper.db("detectEmvListers count 2:"+detectEmvListers.size());
		detectEmvListers.add(listener);
		BHelper.db("detectEmvListers count 3:"+detectEmvListers.size());
	}
	
	public void attachIntegrityCheckListener( IntegrityCheckListener listener ) {
		if( integrityCheckListeners == null )
			integrityCheckListeners = new ArrayList<IntegrityCheckListener>();
		BHelper.db("attachOnReadListener");
		BHelper.db("integrityCheckListeners count 1:"+integrityCheckListeners.size());
		for(int i=0;i<integrityCheckListeners.size();i++){
			integrityCheckListeners.remove(i);
		}
		BHelper.db("integrityCheckListeners count 2:"+integrityCheckListeners.size());
		integrityCheckListeners.add(listener);
		BHelper.db("integrityCheckListeners count 3:"+integrityCheckListeners.size());
	}
	
	
	public void detachEmvReaderListener( EmvReaderListener listener ) {
		if( emvReaderListeners != null ){
			emvReaderListeners.remove(listener);
			BHelper.db("detachOnReadListener count:"+emvReaderListeners.size());
		}

	}

    public void detachBluetoothListener( BlueToothListener listener ) {
        if( blueToothListeners != null ){
			blueToothListeners.remove(listener);
			BHelper.db("detach blueToothListeners count:"+blueToothListeners.size());
		}

    }
	
	public void detachDetectEmvListener( DetectEmvListener listener ) {
		if( detectEmvListers != null ){
			detectEmvListers.remove(listener);
			BHelper.db("detachDetectEmvListener count:"+detectEmvListers.size());
		}

	}
	
	public void detachIntegrityCheckListener( IntegrityCheckListener listener ) {
		if( integrityCheckListeners != null ){
			integrityCheckListeners.remove(listener);
			BHelper.db("detachIntegrityCheckListener count:"+integrityCheckListeners.size());
		}

	}
	
	public BBDeviceController.CheckCardMode getCheckCardMode(){
		return this.checkCardMode;
	}
	public void setCheckCardMode(BBDeviceController.CheckCardMode checkCardMode){
		this.checkCardMode = checkCardMode;
	}
	public void integrityCheck(){
		BHelper.db("do integrityCheck");
		
		if(emvSwipeController!=null){
			if(cmdInUsed.equals(CMD_NONE)){
				emvSwipeController.integrityCheck();
				cmdInUsed = CMD_INTEGRITY_CHECK;
			}else
				BHelper.db("can not execute this command because that cmdInUsed:"+cmdInUsed);
		}
	}
	public static String getSettingPath(){
		String settingPath = BHelper.fileSDCard(EmvApplication.settingPath);
		String settingFile = "setting.txt";
		BHelper.db("setting path:"+settingPath + settingFile);
		return settingPath + settingFile;
	}
	void initEMVReader(){
		cmdInUsed = CMD_NONE;
        BHelper.db("initEMVReader");
		if (emvSwipeController == null) {
			emvSwipeController = PayfunBBDeviceController.getInstance(this.at, new MyEmvSwipeControllerListener1());
			PayfunBBDeviceController.setDebugLogEnabled(false);
            BHelper.db("PayfunBBDeviceController.isDebugLogEnabled(): "+ PayfunBBDeviceController.isDebugLogEnabled());
			emvSwipeController.setDetectAudioDevicePlugged(true);
		}
			
	}
	public boolean IsReaderReader(){
		return emvSwipeController != null;
	}
	public void setIsForCancel(boolean value){
		isForCancel = value;
	}
	public void startReaderDevice(){
		try {
			BHelper.db("start Audio in EmVReader library and wait for onReturnIntegrityCheckResult");		
//			Thread.sleep(500);
			
			if(!isForCancel){
//				Thread.sleep(500);
//				if(!isManualKeyBinding){
//					integrityCheck();
//				}else
//					getDeviceInfo();
			}else{
				//call check card for cancel
				BHelper.db("check card for cancel");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    public void connectBT(BluetoothDevice bluetoothDevice){
        if(emvSwipeController!=null){
            BHelper.db("connect device:"+bluetoothDevice.getName());
            emvSwipeController.connectBT(bluetoothDevice);
        }

    }
	public void disconnectBT(){
		if(emvSwipeController!=null){
			BHelper.db("disconnect device:");
			if(emvSwipeController.getConnectionMode().equals(BBDeviceController.ConnectionMode.BLUETOOTH))
				emvSwipeController.disconnectBT();
		}

	}
	public BBDeviceController.ConnectionMode getConnectionMode(){

		if(emvSwipeController!=null)
			return emvSwipeController.getConnectionMode();
		return BBDeviceController.ConnectionMode.NONE;
	}
    public void stopBTScan(){
        if(emvSwipeController!=null)
            emvSwipeController.stopBTScan();
    }
    public void startBTScan(String [] btName, int timout){
        if(emvSwipeController!=null)
            emvSwipeController.startBTScan(btName,timout);
    }
	public void startAutoConfig(){
		if(emvSwipeController!=null)
			emvSwipeController.startAudioAutoConfig();

//        String settingPath = getSettingPath();
//
//        //check file is empty or not.
//
//        try{
//            File file = new File(settingPath);
//            boolean empty = !file.exists() || file.length() == 0;
//            BHelper.db("isFile :" + empty);
//            if(empty == false) {
//                FileInputStream fis = new FileInputStream(settingPath);
//                byte[] temp = new byte[fis.available()];
//                fis.read(temp);
//                fis.close();
//                BHelper.db("temp :" + temp.hashCode());
//                if (emvSwipeController != null) {
//                    BHelper.db("setAutoConfig");
//                    emvSwipeController.setAudioAutoConfig(new String(temp));
//                }
//            }
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
	}
	public void startReader() throws IOException{
        if(isBT)
            return;
		try {
			BHelper.db("start Audio in EmVReader library and wait for onReturnIntegrityCheckResult");		
			Thread.sleep(500);
			if(emvSwipeController!=null)
				emvSwipeController.startAudio();
			else{
				BHelper.db("can not startAudio because emvSwipeController is null");
			}
			BHelper.db("set auto config from lib");
			String settingPath = getSettingPath();
			
			//check file is empty or not.
			File file = new File(settingPath);
			boolean empty = !file.exists() || file.length() == 0;
			BHelper.db("isFile :" + empty);
			if(empty == false) {
				FileInputStream fis = new FileInputStream(settingPath);
				byte[] temp = new byte[fis.available()];
				fis.read(temp);
				fis.close();
				BHelper.db("temp :" + temp.hashCode());
				if(emvSwipeController!=null){
					BHelper.db("setAutoConfig");
					emvSwipeController.setAudioAutoConfig(new String(temp));
				}
					
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void restartAudio(){
		stopAudio();
		try{
			Thread.sleep(500);
			startReader();
		}catch(Exception ex){
			ex.printStackTrace();
			
		}
	}

	public void stopAudio(){
		if(emvSwipeController!=null)
			if(emvSwipeController.getConnectionMode().equals(BBDeviceController.ConnectionMode.AUDIO))
			emvSwipeController.stopAudio();
	}
	
//	public void encryptData(String data){
//		if(emvSwipeController!=null)
//			emvSwipeController.encryptData(data);
//	}
	
	public void encryptDataWithSettings(String strData){
		Hashtable<String, Object> data = new Hashtable<String, Object>();
		data.put("data", strData);
//		data.put("encryptionMethod", EncryptionMethod.AES_CBC);
//		data.put("encryptionKeySource", EncryptionKeySource.BY_DEVICE_16_BYTES_RANDOM_NUMBER);
//		data.put("encryptionPaddingMethod", EncryptionPaddingMethod.PKCS7);
//		data.put("initialVector", "000102030405060708090A0B0C0D0E0F");
//		data.put("macLength", "8");

		BHelper.db("send cmd: encryptDataWithSettings");
		if(emvSwipeController!=null)
			emvSwipeController.encryptDataWithSettings(data);
		else{
			BHelper.db("can not send cmd encryptDataWithSettings to emvSwipeController because of controller is null");
		}

	}
	public void getKsn(){
		BHelper.db("start get Ksn");
//		if(emvSwipeController!=null)
//			emvSwipeController.getKsn();
		BHelper.db("wait for onReturnKsn");
	}
	public void getDeviceInfo(){
		BHelper.db("getDeviceInfo");
		try{
//			Thread.sleep(500);
			if(emvSwipeController!=null){
				if(cmdInUsed.equals(CMD_NONE)){
					emvSwipeController.getDeviceInfo();
					cmdInUsed = CMD_GET_DEVICE_INFO;
				}else
					BHelper.db("can not execute this command to emvSwipeController because that cmdInUsed:"+cmdInUsed);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
			
	}
	public void getEmvCardData(){
		try{
			Thread.sleep(500);
			if(emvSwipeController!=null)
				emvSwipeController.getEmvCardData();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	public void selectApplication(int index){
		if(emvSwipeController!=null)
			emvSwipeController.selectApplication(index);
	}
	public void setAutoConfig(String settings){
		if(emvSwipeController!=null)
			emvSwipeController.setAudioAutoConfig(settings);
	}
	public void setAutoConfig(){
		try {
			
			String settingPath = getSettingPath();
			BHelper.db("call setAutoConfig at: "+ settingPath);
			FileInputStream fis = new FileInputStream(settingPath);
			byte[] temp = new byte[fis.available()];
			fis.read(temp);
			fis.close();
			if(emvSwipeController!=null)
				emvSwipeController.setAudioAutoConfig(new String(temp));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public boolean setAmount(String amount, String cashbackAmount, String currencyCode, BBDeviceController.TransactionType transactionType){
		BBDeviceController.CurrencyCharacter [] currencyCharacter = {BBDeviceController.CurrencyCharacter.WON};
		if(emvSwipeController!=null)
			return emvSwipeController.setAmount(amount, cashbackAmount, currencyCode, transactionType,currencyCharacter);
       	return false;
	}

	public void cancelSelectApplication(){
		if(emvSwipeController!=null)
			emvSwipeController.cancelSelectApplication();
	}
//	public void resetEmvSwipeController(){
//		if(emvSwipeController!=null)
//			emvSwipeController.reset
//
//		BHelper.db("resetEmvSwipeController");
//	}
	public void cancelCheckCard(){
		if(emvSwipeController!=null)
			emvSwipeController.cancelCheckCard();

		BHelper.db("cancelCheckCard");
	}
	public void sendFinalConfirmResult(boolean isConfirmed){
		if(emvSwipeController!=null)
			emvSwipeController.sendFinalConfirmResult(isConfirmed);
		isConfirmed = false;
	}
	public void sendOnlineProcessResult(String tlv){
		if(emvSwipeController!=null)
			emvSwipeController.sendOnlineProcessResult(tlv);
		tlv = "";
	}
	public void keyExchnage(String base64){
		Hashtable<String, String> data = new Hashtable<String, String>();
		String vanID = "03";
		String dataVal = vanID +base64;
		data.put("data", dataVal);
		BHelper.db("keyExchnage:"+dataVal);
		base64 = dataVal = "";
		if(emvSwipeController!=null)
			emvSwipeController.keyExchange(data);
		data =null;
		BHelper.db("wait for onReturnKeyExchangeResult");

	}

	public void sendTerminalTime(String terminalTime){
		if(emvSwipeController!=null)
			emvSwipeController.sendTerminalTime(terminalTime);
        terminalTime = "";

	}
	
	public String formatHexString(String s){
		byte[] tmp = Helper.hexStringToByteArray(s);
		return Helper.byte2hex(tmp);
	}
	
	public void injectMasterKey(String encIpekValue , String ksn){
		//ksn = EmvUtils.increaseIdentifierKsn(ksn);
		Hashtable<String, String> data = new Hashtable<String, String>();
		data.put("keyManagementType", "1");
		String dataVal = "01"+ encIpekValue+ksn;
		data.put("data", dataVal);		
		if(emvSwipeController!=null)
			emvSwipeController.injectMasterKey(data);
        encIpekValue = ksn = dataVal = "";
        data = null;
		BHelper.db("wait for onReturnInjectMasterKeyResult");
	}
	public void injectMasterKey(String encIpekValue){
		//ksn = EmvUtils.increaseIdentifierKsn(ksn);

		Hashtable<String, String> data = new Hashtable<String, String>();
		data.put("keyManagementType", "01");
		String dataVal = "03"+ encIpekValue;
		data.put("data", dataVal);
		BHelper.db("====DATA TO INJECT MASTER KEY====");
		EmvUtils.showHashTable(data);
		if(emvSwipeController!=null)
			emvSwipeController.injectMasterKey(data);
        encIpekValue = dataVal = "";
        data = null;
		BHelper.db("wait for onReturnInjectMasterKeyResult");
	}
	public void checkCard(Hashtable<String, Object> data){
		BHelper.db("call to check card(IC PAYMENT)");
		checkCardMode = (BBDeviceController.CheckCardMode)data.get("checkCardMode");
		BHelper.db("checkCardMode:"+checkCardMode);
		if(emvSwipeController!=null)
			emvSwipeController.checkCard(data);
        data = null;
        BHelper.db("wait for onRequestOnlineRequest or onReturnCheckCardResult");
	}
	
	public void checkCard(Hashtable<String, Object> data,String fid){
		EmvReader.fid = fid;
		checkCard(data);
        data = null;
	}
	
	public void startEmv(Hashtable<String, Object> data){
		if(emvSwipeController!=null)
			emvSwipeController.startEmv(data);
        data = null;
	}
	
	public void releaseController(){
		if (emvSwipeController != null) {
			emvSwipeController.stopAudio();
            emvSwipeController.disconnectBT();
            emvSwipeController.releasePayfunBBDeviceController();
            emvSwipeController.releaseBBDeviceController();
			emvSwipeController = null;
		}

	}
	
//	public void encryptPin(String pin, String pan){
//	if(emvSwipeController!=null)
//		emvSwipeController.encryptPin(pin, pan);
//}
public void isDeviceHere(){
	if(emvSwipeController!=null)
		emvSwipeController.isDeviceHere();
}
	
//	public boolean isDevicePresent(){
//		if(emvSwipeController!=null)
//			return	emvSwipeController.isDevicePresent();
//		return false;
//	}
//	public void encryptData(String data){
//		if(emvSwipeController!=null)
//			emvSwipeController.encryptData(data);
//	}
	
//	public void getEmvCardNumber(){
//		if(emvSwipeController!=null)
//			emvSwipeController.getEmvCardNumber();
//		
//	}
	
	
//	public void checkCardForCancel(){
//		
//	}
	
	
	
//	public void sendApdu(){
//		//emvSwipeController.send
//	}
    public void stopConnection() {

		try {
			BHelper.db("stop connection....");
			if(emvSwipeController==null)
				return;

			BBDeviceController.ConnectionMode connectionMode = emvSwipeController.getConnectionMode();
			if (connectionMode == BBDeviceController.ConnectionMode.BLUETOOTH) {
				emvSwipeController.disconnectBT();
			} else if (connectionMode == BBDeviceController.ConnectionMode.AUDIO) {
				emvSwipeController.stopAudio();
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
    }
    //endregion
	
	//public class MyEmvSwipeControllerListener1 implements PayfunBBDeviceController.PayfunBBDeviceControllerListener {
	public class MyEmvSwipeControllerListener1 implements PayfunBBDeviceController.PayfunBBDeviceControllerListener {
		@Override
		public void onWaitingForCard(BBDeviceController.CheckCardMode checkCardMode) {
			BHelper.db("onWaitingForCard: "+checkCardMode);
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onWaitingForCard(checkCardMode);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}

		@Override
		public void onWaitingReprintOrPrintNext() {

		}

		@Override
		public void onBTReturnScanResults(List<BluetoothDevice> list) {
            if( blueToothListeners != null && blueToothListeners.size() > 0 ) {
                for( BlueToothListener mCallback : blueToothListeners ) {
                    try {
                        mCallback.onBTReturnScanResults(list);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
                for( DetectEmvListener mCallback : detectEmvListers ) {
                    try {
                        mCallback.onBTReturnScanResults(list);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
		}

		@Override
		public void onBTScanTimeout() {
            if( blueToothListeners != null && blueToothListeners.size() > 0 ) {
                for( BlueToothListener mCallback : blueToothListeners ) {
                    try {
                        mCallback.onBTScanTimeout();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
                for( DetectEmvListener mCallback : detectEmvListers ) {
                    try {
                        mCallback.onBTScanTimeout();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
		}

		@Override
		public void onBTScanStopped() {
            if( blueToothListeners != null && blueToothListeners.size() > 0 ) {
                for( BlueToothListener mCallback : blueToothListeners ) {
                    try {
                        mCallback.onBTScanStopped();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
                for( DetectEmvListener mCallback : detectEmvListers ) {
                    try {
                        mCallback.onBTScanStopped();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
		}

		@Override
		public void onBTConnected(BluetoothDevice bluetoothDevice) {
			cmdInUsed = CMD_NONE;
            if( blueToothListeners != null && blueToothListeners.size() > 0 ) {
                for( BlueToothListener mCallback : blueToothListeners ) {
                    try {
                        mCallback.onBTConnected(bluetoothDevice);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
                for( DetectEmvListener mCallback : detectEmvListers ) {
                    try {
                        mCallback.onBTConnected(bluetoothDevice);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
		}

		@Override
		public void onBTDisconnected() {
            if( blueToothListeners != null && blueToothListeners.size() > 0 ) {
                for( BlueToothListener mCallback : blueToothListeners ) {
                    try {
                        mCallback.onBTDisconnected();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
                for( DetectEmvListener mCallback : detectEmvListers ) {
                    try {
                        mCallback.onBTDisconnected();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
		}

		@Override
		public void onReturnCheckCardResult(BBDeviceController.CheckCardResult checkCardResult, Hashtable<String, String> decodeData) {
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onReturnCheckCardResult(checkCardResult, decodeData);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void onReturnCancelCheckCardResult(boolean isSuccess) {
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onReturnCancelCheckCardResult(isSuccess);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData) {
			cmdInUsed = CMD_NONE;
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onReturnDeviceInfo(deviceInfoData);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			//for detect
			if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
				for( DetectEmvListener mCallback : detectEmvListers ) {
					try {
						
						mCallback.onReturnDeviceInfo(deviceInfoData);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			//for integrity
			if( integrityCheckListeners != null && integrityCheckListeners.size() > 0 ) {
				for( IntegrityCheckListener mCallback : integrityCheckListeners ) {
					try {
						
						mCallback.onReturnDeviceInfo(deviceInfoData);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}

		}



		@Override
		public void onReturnCAPKLocation(String location) {
		}

		@Override
		public void onReturnUpdateCAPKResult(boolean isSuccess) {
		}

		@Override
		public void onReturnRemoveCAPKResult(boolean b) {

		}


		@Override
		public void onReturnEmvReportList(Hashtable<String, String> data) {
		}

		@Override
		public void onReturnEmvReport(String tlv) {
		}

		@Override
		public void onReturnDisableAccountSelectionResult(boolean b) {

		}

		@Override
		public void onReturnDisableInputAmountResult(boolean b) {

		}

		@Override
		public void onReturnPhoneNumber(BBDeviceController.PhoneEntryResult phoneEntryResult, String s) {

		}

		@Override
		public void onReturnTransactionResult(BBDeviceController.TransactionResult transResult) {
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onReturnTransactionResult(transResult);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		
		
		@Override
		public void onReturnBatchData(String tlv) {
			//reset. dont use anymore.
			tlv="";
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onReturnBatchData(tlv);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}

		@Override
		public void onReturnReversalData(String tlv) {
		}

		@Override
		public void onReturnAmountConfirmResult(boolean b) {

		}

		@Override
		public void onReturnPinEntryResult(BBDeviceController.PinEntryResult pinEntryResult, Hashtable<String, String> hashtable) {

		}

		@Override
		public void onReturnPrintResult(BBDeviceController.PrintResult printResult) {

		}

		@Override
		public void onReturnAccountSelectionResult(BBDeviceController.AccountSelectionResult accountSelectionResult, int i) {

		}

		@Override
		public void onReturnAmount(Hashtable<String, String> hashtable) {

		}

		@Override
		public void onReturnUpdateAIDResult(Hashtable<String, BBDeviceController.TerminalSettingStatus> hashtable) {

		}

		@Override
		public void onReturnUpdateGprsSettingsResult(boolean b, Hashtable<String, BBDeviceController.TerminalSettingStatus> hashtable) {

		}

		@Override
		public void onReturnPowerOnIccResult(boolean isSuccess, String ksn, String atr, int atrLength) {
			BHelper.db("onReturnPowerOnIccResult:"+isSuccess);
			
		}

		@Override
		public void onReturnPowerOffIccResult(boolean isSuccess) {

		}

		@Override
		public void onReturnApduResult(boolean b, Hashtable<String, Object> hashtable) {

		}

		@Override
		public void onReturnEmvCardDataResult(boolean isSuccess, String tlv) {
			BHelper.db("onReturnEmvCardDataResult:"+isSuccess);
//			if(isSuccess) {
				if (emvReaderListeners != null && emvReaderListeners.size() > 0) {
					for (EmvReaderListener mCallback : emvReaderListeners) {
						try {
							mCallback.onReturnEmvCardDataResult(isSuccess,tlv);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
//			}
		}

		@Override
		public void onReturnEmvCardNumber(boolean b, String s) {

		}

		@Override
		public void onReturnEncryptPinResult(boolean b, Hashtable<String, String> hashtable) {

		}

		@Override
		public void onReturnUpdateTerminalSettingResult(BBDeviceController.TerminalSettingStatus terminalSettingStatus) {
		}

		@Override
		public void onReturnUpdateWiFiSettingsResult(boolean b, Hashtable<String, BBDeviceController.TerminalSettingStatus> hashtable) {

		}

		@Override
		public void onReturnReadAIDResult(Hashtable<String, Object> hashtable) {

		}

		@Override
		public void onReturnReadGprsSettingsResult(boolean b, Hashtable<String, Object> hashtable) {

		}


		@Override
		public void onReturnReadTerminalSettingResult(Hashtable<String, Object> hashtable) {

		}

//		@Override
//		public void onReturnReadTerminalSettingResult(BBDeviceController.TerminalSettingStatus terminalSettingStatus, String s) {
//
//		}



		@Override
		public void onReturnReadWiFiSettingsResult(boolean b, Hashtable<String, Object> hashtable) {

		}

		@Override
		public void onReturnEnableAccountSelectionResult(boolean b) {

		}

		@Override
		public void onReturnEnableInputAmountResult(boolean b) {

		}

		@Override
		public void onReturnCAPKList(List<CAPK> list) {

		}

		@Override
		public void onRequestSelectApplication(ArrayList<String> appList) {
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onRequestSelectApplication(appList);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}

		@Override
		public void onRequestSetAmount() {
			BHelper.db("onRequestSetAmount");
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onRequestSetAmount();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void onRequestPinEntry(BBDeviceController.PinEntrySource pinEntrySource) {

		}

		@Override
		public void onRequestOnlineProcess(String tlv) {
//			BHelper.db("onRequestOnlineProcess: "+ tlv);
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onRequestOnlineProcess(tlv);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}

		@Override
		public void onRequestTerminalTime() {
			BHelper.db("onRequestTerminalTime");
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.requestTerminalTime();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}

		@Override
		public void onRequestDisplayText(BBDeviceController.DisplayText displayText) {
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onRequestDisplayText(displayText);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void onRequestDisplayAsterisk(int i) {

		}

		@Override
		public void onRequestDisplayLEDIndicator(BBDeviceController.ContactlessStatus contactlessStatus) {

		}

		@Override
		public void onRequestProduceAudioTone(BBDeviceController.ContactlessStatusTone contactlessStatusTone) {

		}

		@Override
		public void onRequestClearDisplay() {
		}

		@Override
		public void onRequestFinalConfirm() {
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onRequestFinalConfirm();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}

		@Override
		public void onRequestPrintData(int i, boolean b) {

		}

		@Override
		public void onPrintDataCancelled() {

		}

		@Override
		public void onPrintDataEnd() {

		}

		@Override
		public void onSessionInitialized() {

		}

		@Override
		public void onSessionError(BBDeviceController.SessionError sessionError, String s) {

		}

		@Override
		public void onNoAudioDeviceDetected() {
            if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
                for( EmvReaderListener mCallback : emvReaderListeners ) {
                    try {

                        mCallback.onNoDeviceDetected();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //for detect
            if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
                for( DetectEmvListener mCallback : detectEmvListers ) {
                    try {

                        mCallback.onNoDeviceDetected();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //for integrity
            if( integrityCheckListeners != null && integrityCheckListeners.size() > 0 ) {
                for( IntegrityCheckListener mCallback : integrityCheckListeners ) {
                    try {

                        mCallback.onNoDeviceDetected();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
		}

		@Override
		public void onAudioAutoConfigProgressUpdate(double percentage) {
			BHelper.db("onAutoConfigProgressUpdate");
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						
						mCallback.onAutoConfigProgressUpdate(percentage);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			//for detect
			if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
				for( DetectEmvListener mCallback : detectEmvListers ) {
					try {
						
						mCallback.onAutoConfigProgressUpdate(percentage);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			//for integrity
			if( integrityCheckListeners != null && integrityCheckListeners.size() > 0 ) {
				for( IntegrityCheckListener mCallback : integrityCheckListeners ) {
					try {
						
						mCallback.onAutoConfigProgressUpdate(percentage);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void onAudioAutoConfigCompleted(boolean isDefaultSettings, String autoConfigSettings) {
			
			try {
				String settingPath = getSettingPath();
				
				FileOutputStream fos = new FileOutputStream(settingPath, false);
				fos.write(autoConfigSettings.getBytes());
				fos.flush();
				fos.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			BHelper.db("onAutoConfigCompleted");
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						
						mCallback.onAutoConfigCompleted(isDefaultSettings,autoConfigSettings);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			//for detect
			if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
				for( DetectEmvListener mCallback : detectEmvListers ) {
					try {
						
						mCallback.onAutoConfigCompleted(isDefaultSettings,autoConfigSettings);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			//for integrity
			if( integrityCheckListeners != null && integrityCheckListeners.size() > 0 ) {
				for( IntegrityCheckListener mCallback : integrityCheckListeners ) {
					try {
						
						mCallback.onAutoConfigCompleted(isDefaultSettings,autoConfigSettings);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void onAudioAutoConfigError(BBDeviceController.AudioAutoConfigError autoConfigError) {
			BHelper.db("onAutoConfigError");
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						
						mCallback.onAutoConfigError(autoConfigError);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			//for detect
			if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
				for( DetectEmvListener mCallback : detectEmvListers ) {
					try {
						
						mCallback.onAutoConfigError(autoConfigError);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			//for integrity
			if( integrityCheckListeners != null && integrityCheckListeners.size() > 0 ) {
				for( IntegrityCheckListener mCallback : integrityCheckListeners ) {
					try {
						
						mCallback.onAutoConfigError(autoConfigError);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		//for apply config.


		@Override
		public void onBatteryLow(BBDeviceController.BatteryStatus batteryStatus) {
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						if(batteryStatus == batteryStatus.LOW) {
							mCallback.updateEmvBatteryLowUI();
							//showToast(at.getString(R.string.battery_low));
						} else if(batteryStatus == BBDeviceController.BatteryStatus.CRITICALLY_LOW) {
							mCallback.updateEmvBatteryCriticallyLowUI();
//							showToast(at.getString(R.string.battery_critically_low));
						}
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			
		}

		@Override
		public void onAudioDevicePlugged() {
			cmdInUsed = CMD_NONE;
			BHelper.db("onDevicePluged lib");
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						
						mCallback.onDevicePlugged();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			//for detect
			if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
				for( DetectEmvListener mCallback : detectEmvListers ) {
					try {
						
						mCallback.onDevicePlugged();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			//for integrity
			if( integrityCheckListeners != null && integrityCheckListeners.size() > 0 ) {
				for( IntegrityCheckListener mCallback : integrityCheckListeners ) {
					try {
						
						mCallback.onDevicePlugged();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
//			BHelper.db("trying start Audio");
//			emvSwipeController.startAudio();
			
		}

		@Override
		public void onAudioDeviceUnplugged() {
			cmdInUsed = CMD_NONE;
			BHelper.db("onDeviceUnplugged");
			if(emvReaderListeners!=null)
			BHelper.db("onDeviceUnplugged lib:"+  emvReaderListeners.size());
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						
						mCallback.onDeviceUnplugged();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			//for detect
			if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
				for( DetectEmvListener mCallback : detectEmvListers ) {
					try {
						mCallback.onDeviceUnplugged();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			//for integirty
			if( integrityCheckListeners != null && integrityCheckListeners.size() > 0 ) {
				for( IntegrityCheckListener mCallback : integrityCheckListeners ) {
					try {
						
						mCallback.onDeviceUnplugged();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void onDeviceHere(boolean isHere) {
			BHelper.db("onDeviceHere:"+ isHere);
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onDeviceHere(isHere);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			//for detect
			if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
				for( DetectEmvListener mCallback : detectEmvListers ) {
					try {
						mCallback.onDeviceHere(isHere);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			//for integirty
			if( integrityCheckListeners != null && integrityCheckListeners.size() > 0 ) {
				for( IntegrityCheckListener mCallback : integrityCheckListeners ) {
					try {
						
						mCallback.onDeviceHere(isHere);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}

		@Override
		public void onPowerDown() {

		}

		@Override
		public void onPowerButtonPressed() {

		}

		@Override
		public void onDeviceReset() {

		}

		@Override
		public void onEnterStandbyMode() {

		}

		@Override
		public void onReturnCAPKDetail(CAPK arg0) {
			// TODO Auto-generated method stub
			
		}



		@Override
		public void onError(BBDeviceController.Error errorState, String arg1) {
			// TODO Auto-generated method stub
			String error = "Error";
			
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onError(errorState);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
				for( DetectEmvListener mCallback : detectEmvListers ) {
					try {
						mCallback.onError(errorState);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			if( integrityCheckListeners != null && integrityCheckListeners.size() > 0 ) {
				for( IntegrityCheckListener mCallback : integrityCheckListeners ) {
					try {
						mCallback.onError(errorState);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			
			BHelper.db("Error:" + error.toString());
			
		}

		@Override
		public void onReturnEncryptDataResult(boolean arg0,
				Hashtable<String, String> arg1) {
			// TODO Auto-generated method stub
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onReturnEncryptDataResult(arg0, arg1);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void onReturnInjectSessionKeyResult(boolean b, Hashtable<String, String> hashtable) {

		}

		@Override
		public void onReturnInjectMasterKeyResult(boolean isSuccess, Hashtable<String, String> data) {
			String resultMsg = "";
			BHelper.db("Step 2.4 Receive callback onReturnInjectMasterKeyResult: "+ isSuccess);
			if(isSuccess){
				
			}else{
				resultMsg = data.get("errorMessage");
			}
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.injectMasterKeyCallback(resultMsg);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			if( integrityCheckListeners != null && integrityCheckListeners.size() > 0 ) {
				for( IntegrityCheckListener mCallback : integrityCheckListeners ) {
					try {
						mCallback.injectMasterKeyCallback(resultMsg);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}

		@Override
		public void onReturnKeyExchangeResult(boolean isSuccess,
				Hashtable<String, String> data) {
			BHelper.db("onReturnKeyExchangeResult");
			KeyExchangeResultEntity exchangeResultEntity;
			if (isSuccess) {
				exchangeResultEntity = KeyExchangeResultEntity.parse(data);
			}else{
				exchangeResultEntity = new KeyExchangeResultEntity();
				BHelper.db("errorMessage:"+data.get("errorMessage"));
			}
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.keyExchangeCallback(exchangeResultEntity);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			if( integrityCheckListeners != null && integrityCheckListeners.size() > 0 ) {
				for( IntegrityCheckListener mCallback : integrityCheckListeners ) {
					try {
						mCallback.keyExchangeCallback(exchangeResultEntity);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			
		}

		@Override
		public void onUsbConnected() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUsbDisconnected() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSerialConnected() {

		}

		@Override
		public void onSerialDisconnected() {

		}

		@Override
		public void onReturnIntegrityCheckResult(boolean arg0) {
			// TODO Auto-generated method stub
			cmdInUsed = CMD_NONE;
			if( emvReaderListeners != null && emvReaderListeners.size() > 0 ) {
				for( EmvReaderListener mCallback : emvReaderListeners ) {
					try {
						mCallback.onReturnIntegrityCheckResult(arg0);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			if( integrityCheckListeners != null && integrityCheckListeners.size() > 0 ) {
				for( IntegrityCheckListener mCallback : integrityCheckListeners ) {
					try {
						mCallback.onReturnIntegrityCheckResult(arg0);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}

			if( detectEmvListers != null && detectEmvListers.size() > 0 ) {
				for( DetectEmvListener mCallback : detectEmvListers ) {
					try {
						mCallback.onReturnIntegrityCheckResult(arg0);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			
		}


		@Override
		public void onReturnNfcDataExchangeResult(boolean arg0, Hashtable<String, String> arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReturnNfcDetectCardResult(BBDeviceController.NfcDetectCardResult nfcDetectCardResult, Hashtable<String, Object> hashtable) {

		}

		@Override
		public void onReturnControlLEDResult(boolean b, String s) {

		}

		@Override
		public void onReturnVasResult(BBDeviceController.VASResult vasResult, Hashtable<String, Object> hashtable) {

		}

		@Override
		public void onRequestStartEmv() {

		}

		@Override
		public void onDeviceDisplayingPrompt() {

		}

		@Override
		public void onRequestKeypadResponse() {

		}

		@Override
		public void onReturnDisplayPromptResult(BBDeviceController.DisplayPromptResult displayPromptResult) {

		}


		@Override
		public void onBarcodeReaderConnected() {

		}

		@Override
		public void onBarcodeReaderDisconnected() {

		}

		@Override
		public void onReturnBarcode(String s) {

		}

	}
	
}
