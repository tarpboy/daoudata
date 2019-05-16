package com.devcrane.android.lib.emvreader;

import java.util.ArrayList;
import java.util.Hashtable;


import com.bbpos.bbdevice.BBDeviceController;
import com.devcrane.android.lib.entity.KSNEntity;
import com.devcrane.android.lib.entity.KeyExchangeResultEntity;

public interface EmvReaderListener {
	void updateEmvCardBalanceUI(String balance);
	void onReturnEmvCardDataResult(boolean isSuccess, String cardData);
	void onReturnEmvCardNumber(String cardNumber);
	void updateEmvBatteryLowUI();
	void updateEmvBatteryCriticallyLowUI();
	void updateEmvPinUI(String pinData);
	void updateEmvKsnUI(String ksnData);
	void updateEmvDeviceInfoUI(String deviceInfo);
	void requestPinEntry();
	void keyExchangeCallback(KeyExchangeResultEntity exchangeResultEntity);
	void injectMasterKeyCallback(String resultMsg);
	void ksnCallback(KSNEntity ksnEntity);
	void requestTerminalTime();
	abstract void onRequestSelectApplication(ArrayList<String> appList);
	void onRequestFinalConfirm();
	void onRequestSetAmount();
	void onRequestOnlineProcess(String tlv);
	void onError(BBDeviceController.Error errorState);
	void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData);
//	void onReturnApduResult(boolean isSuccess, String apdu, int apduLength);
//	void onReturnViposExchangeApduResult(String apdu);
//	void onReturnViposBatchExchangeApduResult(Hashtable<Integer, String> data);
	void onReturnTransactionResult(BBDeviceController.TransactionResult transResult);
	void onReturnCheckCardResult(BBDeviceController.CheckCardResult checkCardResult, Hashtable<String, String> decodeData);
	void onReturnCancelCheckCardResult(boolean isSuccess);
	void onReturnBatchData(String tlv);
	void onWaitingForCard(BBDeviceController.CheckCardMode checkCardMode);
	void onDeviceHere(boolean isHere);
	void onDeviceUnplugged();
	void onDevicePlugged();
	void onReturnIntegrityCheckResult(boolean result);
	void onNoDeviceDetected();
	void onAutoConfigCompleted(boolean isDefaultSettings, String autoConfigSettings);
	void onAutoConfigError(BBDeviceController.AudioAutoConfigError autoConfigError);
	void onAutoConfigProgressUpdate(double percentage);
	void onReturnEncryptDataResult(boolean result, Hashtable<String, String> encrytedData);
	void onRequestDisplayText(BBDeviceController.DisplayText displayText);
}
