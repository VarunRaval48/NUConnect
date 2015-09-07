package com.nirma.varunraval.nuconnect.message;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.nirma.varunraval.nuconnect.R;
import com.nirma.varunraval.nuconnect.body.BodyActivity;

/**
 * Created by Varun on 6/20/2015.
 */
public class HandleMessagesGCMListenerService extends GcmListenerService{

    CharSequence tickerText = "NUConnect Information", contentTitle = "NUConnect", contentText = "You got a message";
    int notificationID = 9;

    public void onMessageReceived(String from, Bundle data){

        Log.i("HandleMessage", "MessageReceived");
        Log.i("from", from);
        Log.i("HandleMsgService Data", data.toString());

        sendNotification(from, data);
    }

    void sendNotification(String from, Bundle data){

        String username, email, login_type;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = sharedPreferences.getString("NUConnect_username", null);
        email = sharedPreferences.getString("NUConnect_email", null);
        login_type = sharedPreferences.getString("NUConnect_login_type", null);

        Log.i("HandleGCMListener", "Sending Notification");
        Intent notificationIntent = new Intent(getApplicationContext(), BodyActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        notificationIntent.putExtra("intent_type", "notification");

        Bundle arg = new Bundle();
        arg.putString("email", email);
        arg.putString("name", username);
        arg.putString("login_type", login_type);
        notificationIntent.putExtra("user_info", arg);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        String inform_type = data.getString("inform_type");
        if(inform_type.equals("Extra Lecture")){
            tickerText = "You have an Extra Lecure";
            contentText = "Extra Lecture on "+data.getString("date")+" "+data.getString("time_from")+" "+data.getString("time_to")
                            +" at "+data.getString("venue")+" Click to see more";
        }

        Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext())
                .setTicker(tickerText)
                .setContentIntent(pendingIntent)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(contentTitle)
                .setAutoCancel(true)
                .setAutoCancel(true)
                .setStyle(new Notification.BigTextStyle().bigText(contentText));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationID, notificationBuilder.build());
        Log.i("HandleGCMListener", "Notification Sent");
    }
}
