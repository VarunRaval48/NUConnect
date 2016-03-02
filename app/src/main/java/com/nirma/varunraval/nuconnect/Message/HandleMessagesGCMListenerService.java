package com.nirma.varunraval.nuconnect.message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.nirma.varunraval.nuconnect.database.MessagesDatabasedbHelper;
import com.nirma.varunraval.nuconnect.R;
import com.nirma.varunraval.nuconnect.SendIDToServer;
import com.nirma.varunraval.nuconnect.body.BodyActivity;
import com.nirma.varunraval.nuconnect.body.BodyFragmentHome;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Varun on 6/20/2015.
 */
public class HandleMessagesGCMListenerService extends GcmListenerService{

    CharSequence tickerText = "NUConnect Information", contentTitle = "NUConnect", contentText = "You got a message";
    int notificationID = 9, msg_id;
    Context context;
    String date, subject, msg_optional, time_to, time_from, venue, date_sent_on, from_id, from_name, msg_type;
    MessagesDatabasedbHelper messagesDatabasedbHelper;
    SharedPreferences.Editor editor;

    public void onMessageReceived(String from, Bundle data){

        Log.i("HandleMessage", "MessageReceived");
        Log.i("from", from);
        Log.i("HandleMsgService Data", data.toString());

        context = getApplicationContext();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(sharedPreferences.getString("NUConnect_email", null)!=null)
            sendNotification(from, data);
    }

    void sendNotification(String from, Bundle data){

        String username, email, login_type;

        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

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

        msg_type = data.getString("msg_type");

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject_cover = new JSONObject();

        try {
            if(msg_type.equals("Extra Lecture")){
                Log.i("HandleMSGmsg_id", "**"+data.getString("msg_id")+"**");
                msg_id = Integer.parseInt(data.getString("msg_id"));
                subject = data.getString("subject");
                date = data.getString("date");
                time_from = data.getString("time_from");
                time_to = data.getString("time_to");
                venue = data.getString("venue");
                from_id = data.getString("from_id");
                from_name = data.getString("from_name");
                msg_optional = data.getString("msg_optional");
                date_sent_on = data.getString("date_sent_on");
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

                getAllMessages();

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

    void setNotification_to_database(){
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

    public void getAllMessages(){

        int last_got_message;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        last_got_message = Integer.parseInt(sharedPreferences.getString("NUConnect_last_got_message", "0"));
        Log.i("update_sent", last_got_message+"");
        new GetAllMessages().execute(last_got_message);
    }

    class GetAllMessages extends AsyncTask<Integer, Void, ArrayList>{

        @Override
        protected ArrayList doInBackground(Integer... params) {

            String result="";
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String email = sharedPreferences.getString("NUConnect_email", null);

            List<NameValuePair> list = new ArrayList();
            list.add(new BasicNameValuePair("last_got_message", String.valueOf(params[0])));
            list.add(new BasicNameValuePair("email", email));

            messagesDatabasedbHelper = new MessagesDatabasedbHelper(context);
            String url = getResources().getString(R.string.server_url)+"getAllMessages.php";
            try {
                SendIDToServer sendIDToServer = new SendIDToServer(url, list, context);
                result = sendIDToServer.sendToken();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Log.i("BodyFragmentHomeMessage", result);

            if(!result.equals("0")) {
//            ArrayList<Integer> msg_ids_obtained = new ArrayList<>();
                ArrayList<String> msg_obtained = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jsonObject = null, data;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        data = jsonObject.getJSONObject("data");

                        msg_obtained.add(jsonObject.toString());
                        messagesDatabasedbHelper.insertRowAll(jsonObject.getInt("msg_id"), jsonObject.getString("from_id"), jsonObject.getString("from_name"),
                                data.getString("s"), data.getString("d"), data.getString("t_f"), data.getString("t_t"), data.getString("v"),
                                jsonObject.getString("msg_type"), data.getString("msg_optional"), jsonObject.getString("date_sent_on"));
                    }
                    if (jsonObject != null) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                        editor.putString("NUConnect_last_got_message", String.valueOf(jsonObject.getInt("msg_id")));
                        editor.commit();
                        Log.i("BodyFragment_lastMsg", jsonObject.getInt("msg_id") + "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return msg_obtained;
            }
            else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            super.onPostExecute(result);
            setNotification_to_database();
        }
    }
}
