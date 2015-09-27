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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

        JSONObject jsonObject = new JSONObject();

        try {
            if(inform_type.equals("Extra Lecture")){
                String subject = data.getString("subject");
                String date = data.getString("date");
                String time_from = data.getString("time_from");
                String time_to = data.getString("time_to");
                String venue = data.getString("venue");
                String from_id = data.getString("from_id");
                String name = data.getString("from_name");
                String message = data.getString("msg_optional");

                jsonObject.accumulate("subject",subject);
                jsonObject.accumulate("date", date);
                jsonObject.accumulate("time_from", time_from);
                jsonObject.accumulate("time_to", time_to);
                jsonObject.accumulate("venue", venue);
                jsonObject.accumulate("id", from_id);
                jsonObject.accumulate("name", name);
                jsonObject.accumulate("message", message);

                tickerText = "You have an Extra Lecture";
                contentText = "Extra Lecture of "+subject+" on "+date+" "+time_from+" "+time_to
                                +" at "+venue+" Click to see more";
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        String json_data = jsonObject.toString()+",,";

        try {
            File f = new File(getFilesDir()+"/"+"NUConnect_chats_extralecture");
            FileOutputStream fos;
            if(f.exists()){
                fos = openFileOutput("NUConnect_chats_extralecture", Context.MODE_APPEND);
            }
            else{
                fos = openFileOutput("NUConnect_chats_extralecture", Context.MODE_PRIVATE);
            }
            fos.write(json_data.getBytes());
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
