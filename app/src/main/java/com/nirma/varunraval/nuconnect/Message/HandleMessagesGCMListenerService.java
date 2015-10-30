package com.nirma.varunraval.nuconnect.message;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.nirma.varunraval.nuconnect.Database.MessagesDatabasedbHelper;
import com.nirma.varunraval.nuconnect.R;
import com.nirma.varunraval.nuconnect.body.BodyActivity;
import com.nirma.varunraval.nuconnect.body.BodyFragmentHome;
import com.nirma.varunraval.nuconnect.body.Chat_ExtraLecture_ArrayAdapter;

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
    Context context;

    public void onMessageReceived(String from, Bundle data){

        Log.i("HandleMessage", "MessageReceived");
        Log.i("from", from);
        Log.i("HandleMsgService Data", data.toString());

        context = getApplicationContext();

        sendNotification(from, data);
    }

    void sendNotification(String from, Bundle data){

        String username, email, login_type;

        final MessagesDatabasedbHelper messagesDatabasedbHelper = new MessagesDatabasedbHelper(context);
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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

        final String msg_type = data.getString("msg_type");

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject_cover = new JSONObject();

        try {
            if(msg_type.equals("Extra Lecture")){
                Log.i("HandleMSGmsg_id", "**"+data.getString("msg_id")+"**");
                final int msg_id = Integer.parseInt(data.getString("msg_id"));
                final String subject = data.getString("subject");
                final String date = data.getString("date");
                final String time_from = data.getString("time_from");
                final String time_to = data.getString("time_to");
                final String venue = data.getString("venue");
                final String from_id = data.getString("from_id");
                final String from_name = data.getString("from_name");
                final String msg_optional = data.getString("msg_optional");
                final String date_sent_on = data.getString("date_sent_on");
                Log.i("HandleGCM", "Message " + msg_optional);

                jsonObject.accumulate("s", subject);
                jsonObject.accumulate("d", date);
                jsonObject.accumulate("t_f", time_from);
                jsonObject.accumulate("t_t", time_to);
                jsonObject.accumulate("v", venue);
                jsonObject.accumulate("msg_optional", msg_optional);
                jsonObject_cover.accumulate("data", jsonObject);
                jsonObject_cover.accumulate("msg_type", msg_type);
                jsonObject_cover.accumulate("from_id", from_id);
                jsonObject_cover.accumulate("from_name", from_name);
                jsonObject_cover.accumulate("date_sent_on", date_sent_on);
                tickerText = "You have an Extra Lecture";
                contentText = "Extra Lecture of "+subject+" on "+date+" "+time_from+" "+time_to
                                +" at "+venue+" Click to see more";

                new AsyncTask(){
                    @Override
                    protected Object doInBackground(Object[] params) {
                        messagesDatabasedbHelper.insertRowAll(msg_id, from_id, from_name, subject, date, time_from, time_to,
                                venue, msg_type, msg_optional, date_sent_on);
                        return null;
                    }

                    protected void onPostExecute(Object result){
                        editor.putString("NUConnect_last_got_message", String.valueOf(msg_id));
                        Log.i("HandleMsgGCMListener", msg_id+"");
                        editor.commit();
                    }
                }.execute();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

//        String json_data = jsonObject_cover.toString()+",,";
//        Log.i("HandleGCM", json_data);
//        try {
//            File f = new File(getFilesDir()+"/"+"NUConnect_chats_extralecture");
//            FileOutputStream fos;
//            if(f.exists()){
//                fos = openFileOutput("NUConnect_chats_extralecture", Context.MODE_APPEND);
//            }
//            else{
//                fos = openFileOutput("NUConnect_chats_extralecture", Context.MODE_PRIVATE);
//            }
//            fos.write(json_data.getBytes());
//            fos.close();
//        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if(BodyFragmentHome.fragmentAttached){
            Log.i("HandleMessage", "Activity is on");
            BodyFragmentHome.arrayAdapter.add(jsonObject_cover.toString());
            BodyFragmentHome.move_at_last();
        }
        else {
            Log.i("HandleMessage", "Activity is off");
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
}
