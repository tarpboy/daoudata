package com.devcrane.android.lib.emvreader;

import android.bluetooth.BluetoothDevice;

import java.util.Hashtable;
import java.util.List;

import com.bbpos.bbdevice.BBDeviceController;

public interface DetectEmvListener {
	
	void onReturnDeviceInfo(Hashtable<String, String> deviceInfoData);
	void onDeviceHere(boolean isHere);
	void onNoDeviceDetected();
	void onDevicePlugged();
	void onDeviceUnplugged();
	void onError(BBDeviceController.Error errorState);
	void onAutoConfigCompleted(boolean isDefaultSettings, String autoConfigSettings);
	void onAutoConfigError(BBDeviceController.AudioAutoConfigError autoConfigError);
	void onAutoConfigProgressUpdate(double percentage);
	void onReturnIntegrityCheckResult(boolean result);
	void onBTReturnScanResults(List<BluetoothDevice> list);
	void onBTScanTimeout();
	void onBTScanStopped();
	void onBTConnected(BluetoothDevice bluetoothDevice);
	void onBTDisconnected();

}
