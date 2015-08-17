package com.nirma.varunraval.nuconnect.message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.nirma.varunraval.nuconnect.body.BodyActivity;

/**
 * Created by Varun on 6/20/2015.
 */
public class HandleMessagesGCMListenerService extends GcmListenerService{

    CharSequence tickerText = "NUConnect Information", contentTitle = "NUConnect", contentText = "You got a message";
    int notificationID = 9;

    public void onMessageReceived(String from, Bundle data){

        Log.i("HandleMessage", "MesageReceived");
        Intent notificationIntent = new Intent(getApplicationContext(), BodyActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext())
                .setTicker(tickerText)
                .setContentIntent(pendingIntent)
                .setContentText(contentText)
                .setContentTitle(contentTitle)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationID, notificationBuilder.build());

    }

}
