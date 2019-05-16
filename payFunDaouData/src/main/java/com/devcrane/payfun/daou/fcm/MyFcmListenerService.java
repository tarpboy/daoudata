package com.devcrane.payfun.daou.fcm;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.devcrane.android.lib.emvreader.EmvApplication;
import com.devcrane.payfun.daou.MainActivity;
import com.devcrane.payfun.daou.R;
import com.devcrane.payfun.daou.data.StaticData;
import com.devcrane.payfun.daou.utility.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Map;


/**
 * FCM 메시지 서비스.
 */

public class MyFcmListenerService extends FirebaseMessagingService{

    private static final String TAG = MyFcmListenerService.class.getSimpleName();

    /**
     * 구글서버에서 메시지가 도착시 실행되는 이벤트.
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Context act = getApplication().getApplicationContext();
        // 앱이 종료되었거나 백그라운드에서 실행시 알림을 보낸다.
        if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//            if (isPreventTime()) return;
            sendPushNotification(remoteMessage);
        }else{
            // 앱이 실행중이면 채팅창이 현재 실행중이면 알림을 보내지 않고 채팅창에 바로 메시지를 보낸다.
            if(act != null && act.getClass().getSimpleName().startsWith("ChatRoomActivity")) {
                Map<String, String> data = remoteMessage.getData();
                // App is in foreground, Broadcast the push message
                Intent intent = new Intent(StaticData.PUSH_NOTIFICATION);
//                intent.putExtra("type", StaticData.PUSH_TYPE_CHATROOM);
                intent.putExtra("content", data.get("msg"));
                intent.putExtra("sender", data.get("sender"));
                intent.putExtra("imgurl", data.get("imgurl"));
                intent.putExtra("rdate", data.get("rdate"));
                intent.putExtra("roomIdx", data.get("room_idx"));
                intent.putExtra("chatIdx", data.get("chat_idx"));
                intent.putExtra("receiver", data.get("sender"));

                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }else{
                // 앱이 실행중이라도 채팅창이 실행중이지 않으면 알림을 보낸다.
//                if (isPreventTime()) return;
                sendPushNotification(remoteMessage);
            }

        }

    }

    /**
     * 메시지 객체내에 있는 데이터를 추출해서 단말에 보낸다.
     * @param message
     */
    private void sendPushNotification(RemoteMessage message) {
        String from = message.getFrom();
        Map<String, String> data = message.getData();
        String receiver = "";
        String title = "";
        String content = "";
        String chatRoomIdx = "0";

        receiver = data.get("sender");
        title = data.get("title");
        content = data.get("msg");

        if (content != null && content.length() > 20) {
            content += content.substring(0, 20) + "...";
        }

        chatRoomIdx = data.get("room_idx");

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("roomIdx", chatRoomIdx);
        intent.putExtra("chatIdx", "0");
        intent.putExtra("receiver", receiver);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

//        boolean isSound = Config.getSound(this,false);
//        boolean isVibrate = Config.getVibrate(this,false);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher) )
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

//        if(isSound)
            notificationBuilder.setSound(defaultSoundUri);
//        if(isVibrate)
            notificationBuilder.setVibrate(new long[] { 1000, 1000 });

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
        wakelock.acquire(5000);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//        String event = "refresh_chat_message";
//        JobEventBus.post(event);
    }


}
