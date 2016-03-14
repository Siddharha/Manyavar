package com.wbct.etab.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.wbct.etab.R;
import com.wbct.etab.activity.MenuActivity;
import com.wbct.etab.utils.GcmBroadcastReceiver;
import com.wbct.etab.utils.Pref;

/**
 * Created by BLUEHORSE 123 on 2/24/2016.
 */
public class GCMNotificationIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private Pref _pref;
    public static final String TAG = "GCMIntentService";

    public GCMNotificationIntentService() {
        super("GCMNotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        _pref = new Pref(this);
        String messageType = gcm.getMessageType(intent);
        Log.e("test recieve", "------------------------------ Catch");

        if (!extras.isEmpty()) {
            Log.i(TAG, "Received: " + extras.toString());

            _pref.saveSyncValue(false);
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {

                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                sendNotification("Deleted messages on server: "
                        + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {

                for (int i = 0; i < 3; i++) {
                    Log.i(TAG,
                            "Working... " + (i + 1) + "/5 @ "
                                    + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }

                }

                if(extras.containsKey("msg")){
                    if(!extras.get("msg").equals("") ){
                        sendNotification(""+extras.get("msg"));
                    }
                }

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        Log.d(TAG, "Preparing to send notification...: " + msg);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent in = new Intent(this, MenuActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        in.putExtra("Notification", msg);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                in, 0);

        int numMessages = 0;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.mipmap.app_icon)
                .setContentTitle("Manyavar")
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setAutoCancel(true)
                .setContentText(msg)
                .setNumber(++numMessages);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d(TAG, "Notification sent successfully.");
    }
}
