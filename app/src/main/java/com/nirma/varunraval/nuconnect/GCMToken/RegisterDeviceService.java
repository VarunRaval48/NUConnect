package com.nirma.varunraval.nuconnect.GCMToken;


import android.app.IntentService;
import android.content.Intent;
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

    @Override
    protected void onHandleIntent(Intent intent) {

        String value;

        String email = intent.getStringExtra("email");
        Log.i("In RegisterDevice Email", email);

        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            token = instanceID.getToken(getString(R.string.sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("Registration Id", token);
        //TODO Send Registrtion id to server

        if(token!=null) {
            try {
                List<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("reg_id", token));
                list.add(new BasicNameValuePair("email", email));
                list.add(new BasicNameValuePair("access_token", LoginActivity.nuconnect_accessToken));

                URL url = new URL(getResources().getString(R.string.server_url)+ "/setRegistrationID.php");

                SendIDToServer sendIDToServer = new SendIDToServer(url, list);
                value = sendIDToServer.sendToken();

                Log.i("Response from Server", value);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            Log.i("Error", "Error receiving token");
        }
    }
}
