package org.sindt.messenger;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;


public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;


    public GcmIntentService() {
        super("GcmIntentServices");
    }

    public static final String TAG = "MSGR";
    Messages messages;



    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        messages = new Messages(this);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                sendNotification(extras.getString("message"), extras.getString("messageType", "alert"));
                messages.insertMessage(extras.getString("message", "message is missing"), extras.getString("messageType", "alert"), "message", System.currentTimeMillis());
                Log.i(TAG, "Message: " + extras.toString());

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }



    private void sendNotification(String msg, String msgType) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String ringtone = sharedPrefs.getString("notifications_new_message_ringtone", null);
        Boolean vibrate = sharedPrefs.getBoolean("notifications_new_message_vibrate", true);
        Boolean alert = true;

        alert = sharedPrefs.getBoolean("notifications_alert_message", true);

        if (msgType.equals("info")) {
            if (sharedPrefs.getBoolean("notifications_info_message", true)) {
                alert = true;
            } else {
                alert = false;
            }
        }

        Log.i(TAG, "alert: " + alert.toString());
        Log.i(TAG, "ringtone: " + ringtone);
        Log.i(TAG, "vibrate: " + vibrate.toString());
        Log.i(TAG, "message type: " + msgType);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Messenger")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setWhen(System.currentTimeMillis())
                .setContentText(msg);
        if (alert.equals(true)) {
            if (vibrate) {
                mBuilder.setVibrate(new long[]{500, 500, 500, 500});
            }
            mBuilder.setSound(Uri.parse(ringtone));
        }

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        //Log.i(TAG, "sendNotification: " + msg);


    }
}