package com.nirma.varunraval.nuconnect.GCMToken;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.nirma.varunraval.nuconnect.Login.LoginActivity;
import com.nirma.varunraval.nuconnect.R;
import com.nirma.varunraval.nuconnect.SendIDToServer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Varun on 6/20/2015.
 */
public class RegisterDeviceService extends IntentService {

    public RegisterDeviceService() {
        super("IntentService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *  Used to name the worker thread, important only for debugging.
     */

    static String token = null;

    public static void handleRefresh(Context context, String email){
        Intent intent = new Intent(context, RegisterDeviceService.class);
        intent.putExtra("email", email);
        intent.setAction("refresh");
        context.startService(intent);
    }

    public static void handleNew(Context context, String email){
        Intent intent = new Intent(context, RegisterDeviceService.class);
        intent.putExtra("email", email);
        intent.setAction("new");
        context.startService(intent);
    }

    public static void handleUpdate(Context context, String email, String nuconnect_accesstoken){
        Intent intent = new Intent(context, RegisterDeviceService.class);
        intent.putExtra("email", email);
        intent.setAction("update");
        intent.putExtra("access", nuconnect_accesstoken);
        context.startService(intent);
    }

    public static void handleConnect(Context context, String email){
        Log.i("RegisterDeviceService", "in connect");
        Intent intent = new Intent(context, RegisterDeviceService.class);
        intent.putExtra("email", email);
        intent.setAction("connect");
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            String action = intent.getAction();
            String value;

            String email = intent.getStringExtra("email");
            Log.i("In RegisterDevice Email", email);

            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("email", email));

            URL url = null;
            InstanceID instanceID = InstanceID.getInstance(this);

            //update access token
            if (action.equals("new")) {
                token = instanceID.getToken(getString(R.string.sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i("Registration Id", token);
                list.add(new BasicNameValuePair("reg_id", token));
                list.add(new BasicNameValuePair("access_token", LoginActivity.nuconnect_accessToken));
                url = new URL(getResources().getString(R.string.server_url) + "/setRegistrationID.php");
                if (token != null) {
                    sendToken(list, url);
                } else {
                    Log.i("Error", "Error receiving token");
                }
            }
            //Refresh reg id
            else if (action.equals("refresh")) {
                token = instanceID.getToken(getString(R.string.sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i("Registration Id", token);
                list.add(new BasicNameValuePair("reg_id", token));
                url = new URL(getResources().getString(R.string.server_url) + "/setRegistrationIDonly.php");
                if (token != null) {
                    sendToken(list, url);
                } else {
                    Log.i("Error", "Error receiving token");
                }
            }//during login
            else if (action.equals("update")) {
                list.add(new BasicNameValuePair("access_token", LoginActivity.nuconnect_accessToken));
                url = new URL(getResources().getString(R.string.server_url) + "/setaccesstoken.php");

                String nuconnect_accesstoken = intent.getStringExtra("access");
                LoginActivity.nuconnect_accessToken = nuconnect_accesstoken;

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.remove("NUConnect_accesstoken");
                editor.putString("NUConnect_accesstoken", nuconnect_accesstoken);
                editor.commit();

                sendToken(list, url);
            }//simple connect
            else if (action.equals("connect")) {
                url = new URL(getResources().getString(R.string.server_url) + "/setLoginTime.php");
                sendToken(list, url);
            }
            //TODO Send Registrtion id to server
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendToken(List<NameValuePair> list, URL url){
        String value;

        SendIDToServer sendIDToServer = new SendIDToServer(url, list);
        value = sendIDToServer.sendToken();

        Log.i("Response from Server", value);
    }
}
