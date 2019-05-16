package com.devcrane.android.lib.emvreader;

import java.util.Hashtable;

import com.bbpos.bbdevice.BBDeviceController;
import com.devcrane.android.lib.entity.KeyExchangeResultEntity;

public interface IntegrityCheckListener {
	
	void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData);
	void keyExchangeCallback(KeyExchangeResultEntity exchangeResultEntity);
	void injectMasterKeyCallback(String resultMsg);
	void onDeviceHere(boolean isHere);
	void onNoDeviceDetected();
	void onDeviceUnplugged();
	void onDevicePlugged();
	void onReturnIntegrityCheckResult(boolean result);
	void onError(BBDeviceController.Error errorState);
	void onAutoConfigCompleted(boolean isDefaultSettings, String autoConfigSettings);
	void onAutoConfigError(BBDeviceController.AudioAutoConfigError autoConfigError);
	void onAutoConfigProgressUpdate(double percentage);

}
