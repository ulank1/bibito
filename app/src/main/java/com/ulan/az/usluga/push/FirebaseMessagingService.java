package com.ulan.az.usluga.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.ulan.az.usluga.Profile.MyForumActivity;
import com.ulan.az.usluga.Profile.MyOrderActivity;
import com.ulan.az.usluga.R;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;



public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService  {
    private static final String TAG = "FirebaseMessagingServic";
    String click;
    int id;
    String type, name;
    int customerProperty=1;
    public FirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        //Log.e("JSON_OBJECT", object.toString());

        int id = 0;
        String  name = null;
        String title = null;

        try {
            id = object.getInt("id");
            name = object.getString("title");
            title = object.getString("body");
        }catch (Exception e){

        }

        sendNotification2(name,id,title);


    }

    @Override
    public void onDeletedMessages() {

    }

    private void sendNotification(String name,int customerProperty) {

        /*Intent intent = null;
        intent = new Intent(this,MapActivity.class);
//        }else if(clickAction.equals("video")){
//            intent = new Intent(this, CertaiVideoActivity.class);
//        }else if(clickAction.equals("audio")){
//            intent = new Intent(this, PushAudioActivity.class);
//            intent.putExtra("bundle",1);
//            intent.putExtra("name",name);
//        }
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("message",name);
        intent.putExtra("typecustomerProperty",customerProperty);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                PendingIntent.FLAG_ONE_SHOT);*/

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(customerProperty+"")
                .setContentText(name)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
        //.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendNotification2(String name,int customerProperty,String title) {

        Intent intent = null;
        if (customerProperty==1) {
            intent = new Intent(this, MyOrderActivity.class);
        }else {
            intent = new Intent(this, MyForumActivity.class);

        }

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("message", name);
           // intent.putExtra("extra1", 1);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0  /*Request code*/, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(name + "")
                    .setContentText(title)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
