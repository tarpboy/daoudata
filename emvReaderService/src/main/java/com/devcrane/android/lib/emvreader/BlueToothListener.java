package com.devcrane.android.lib.emvreader;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * Created by Administrator on 10/5/2016.
 */
public interface BlueToothListener {
    void onBTReturnScanResults(List<BluetoothDevice> list);
    void onBTScanTimeout();
    void onBTScanStopped();
    void onBTConnected(BluetoothDevice bluetoothDevice);
    void onBTDisconnected();
}
