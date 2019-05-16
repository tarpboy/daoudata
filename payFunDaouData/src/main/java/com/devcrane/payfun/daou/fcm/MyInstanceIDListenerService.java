package com.devcrane.payfun.daou.fcm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.devcrane.payfun.daou.utility.BHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by choheeyang on 2016. 12. 24..
 */

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {
    private static final String TAG = MyInstanceIDListenerService.class.getSimpleName();



    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        BHelper.db("FCM_TOKEN:"+token);

    }


}
