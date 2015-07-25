package com.nirma.varunraval.nuconnect;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Varun on 6/20/2015.
 */
public class HandleMessagesGCMListenerService extends GcmListenerService{

    CharSequence tickerText = "NUConnect Information", contentTitle = "NUConnect", contentText = "You go a message";
    int notificationID = 9;

    public void onMessageReceived(String from, Bundle data){

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
