package com.celex.rider.Firebase_Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.celex.rider.AllActivitys.MainActivity;
import com.celex.rider.CodeClasses.Variables;
import com.celex.rider.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public Context context;

    String title, message, action_type, sender, st_title, order, st_image, st_order_id, st_store_id, st_user_id, receiver;
    Handler messageHandler = new Handler();

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        if (token != null && token.length() > 6)
            getSharedPreferences("_", MODE_PRIVATE).edit().putString("fcm_token", token).apply();

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("body");
            sender = remoteMessage.getData().get("sender");
            receiver = remoteMessage.getData().get("receiver");
            action_type = remoteMessage.getData().get("type");
            this.context = context;


            Map<String, String> body = remoteMessage.getData();
            JSONObject json = new JSONObject(body);

            Log.d("Rider_log", "json for noti : " + json.toString());


            if (action_type.contains("chat")) {
                try {

                    JSONObject sender_Obj = new JSONObject(sender);

                    st_image = sender_Obj.getString("image");
                    st_user_id = sender_Obj.getString("id");
                    if (order != null) {

                        JSONObject orderobj = new JSONObject(order);
                        st_order_id = orderobj.getString("id");
                        st_store_id = orderobj.getString("store_id");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String reciver_id = Variables.userDetails_pref.getString(Variables.chat_reciever_id, "");
                if (!reciver_id.equals(st_user_id)) {
                    showNotification(this, title, message);
                }
            } else {
                if (title != null && title.contains("new order")) {
                    sendBroadcast(new Intent("Active"));
                }
            }
        }


        if (title != null) {
            if (title.contains("new order request")) {
                showNotification(this, title, message);
                sendBroadcast(new Intent("Active"));
            }
        }

    }

    public void showNotification(Context context, String title, String body) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        Intent myintent = new Intent(context, MainActivity.class);
        Bundle args = new Bundle();

        args.putString("senderid", Variables.userDetails_pref.getString(Variables.id, ""));
        args.putString("Receiverid", st_user_id);
        args.putString("Receiver_name", st_title);
        args.putString("Receiver_pic", st_image);
        args.putString("Receiver_order_id", st_order_id);
        args.putString("Receiver_store_id", st_store_id);
        args.putString("type", action_type);
        args.putString("pushnotification", "yes");
        args.putString("sender", sender);
        args.putString("order", order);

        Log.d("onMessageReceived", "args" + args.toString());

        myintent.putExtras(args);
        myintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        int randomPIN = (int) (Math.random() * 9000) + 1000;
        PendingIntent conIntent = PendingIntent.getActivity(context, randomPIN, myintent, PendingIntent.FLAG_UPDATE_CURRENT);

        // chatFragment(st_user_id ,st_title ,st_image );

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText(message).setAutoCancel(true).setSound(soundUri)
                .setContentIntent(conIntent);

        notificationManager.notify(notificationId, mBuilder.build());


    }


    public void displayError(final String errorText) {
        Runnable doDisplayError = new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), errorText, Toast.LENGTH_LONG).show();
            }
        };
        messageHandler.post(doDisplayError);
    }

}
