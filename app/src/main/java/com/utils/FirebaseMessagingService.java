package com.utils;

/**
 * Created by hitesh.singh on 10/19/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.RemoteMessage;
import com.momskitchen.R;
import com.momskitchen.brandActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "StartingAndroid";
    private String message, tasteEncryptedID, activityType, reviewEncryptedID, postby;
    private AppPreferences preferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //preferences = AppPreferences.getPrefs(this);
        //It is optional
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Notification Message Body: " +remoteMessage.getData());

        String body = remoteMessage.getData().get("body");
        try {
            JSONObject obj = new JSONObject(body);
            Log.e("Title",""+obj);

            Log.e("OBJ",""+obj);
            message = obj.getString("message");
            activityType = obj.getString("activitytype");
            postby = obj.getString("postby");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Calling method to generate notification
        sendNotification("Moms Kitchen", message);
    }

    //This method is only generating push notification
    private void sendNotification(String title, String message ) {
        Log.e("Final message:","Message==="+message);

      Intent intent= new Intent(FirebaseMessagingService.this,brandActivity.class).putExtra("ID",""+postby).putExtra("NAME",""+activityType);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
       /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.mipmap.transparent_appicon);
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.app_icon);
        }*/
notificationBuilder.setSmallIcon(R.mipmap.app_icon);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(message);
        notificationBuilder .setAutoCancel(true);
        notificationBuilder .setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
