package com.ulan.az.usluga.push;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.ulan.az.usluga.Profile.FavoritesActivity;
import com.ulan.az.usluga.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Dastan on 06.06.2017.
 */

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, FavoritesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notifycation = new NotificationCompat.Builder(this);
        notifycation.setContentTitle("Level");
        notifycation.setContentText(remoteMessage.getNotification().getBody());
        notifycation.setAutoCancel(true);
        notifycation.setSmallIcon(R.mipmap.ic_launcher);
        notifycation.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifycation.build());
        super.onMessageReceived(remoteMessage);
    }
}
