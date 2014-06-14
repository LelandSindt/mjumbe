package org.sindt.messenger;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.sindt.messenger.GcmBroadcastReceiver;
import org.sindt.messenger.MainActivity;
import org.sindt.messenger.R;


public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentServices");
    }

    public static final String TAG = "MSGR";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(extras.getString("m"));
                Log.i(TAG, "Message: " + extras.toString());

            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String ringtone = sharedPrefs.getString("notifications_new_message_ringtone", null);
        Boolean vibrate = sharedPrefs.getBoolean("notifications_new_message_vibrate", true);

        Log.i(TAG, "ringtone: " + ringtone);
        Log.i(TAG, "vibrate: " + vibrate.toString());



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Messenger")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setWhen(System.currentTimeMillis())
                .setContentText(msg);
        if (sharedPrefs.getBoolean("notifications_new_message", true)) {
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