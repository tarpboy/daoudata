package com.devcrane.payfun.daou.utility;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.entity.BTReaderInfo;

/**
 * Created by qts2 on 12/19/16.
 */
public class BTHelper {
    public static void savePairedBT(Context ct){
        Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
        final BluetoothDevice[] pairedDevices = new BluetoothDevice[pairedObjects.length];
        for (int i = 0; i < pairedObjects.length; ++i) {
            pairedDevices[i] = (BluetoothDevice) pairedObjects[i];
            String btPrefix = ct.getString(R.string.bt_device_prefix);
            if(pairedDevices[i].getName().contains(btPrefix)){
                BTReaderInfo btReaderInfo = new BTReaderInfo(pairedDevices[i].getName(),pairedDevices[i].getAddress());
                AppHelper.setBTReaderInfo(btReaderInfo);
                return;
            }
        }

    }
}
