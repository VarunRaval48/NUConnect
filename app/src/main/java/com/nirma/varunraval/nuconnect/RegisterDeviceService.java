package com.nirma.varunraval.nuconnect;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
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
     * @param name Used to name the worker thread, important only for debugging.
     */

    @Override
    protected void onHandleIntent(Intent intent) {

        String token = null;

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

                URL url = new URL("http://" + LoginActivity.serverURL + "/nuconnect/setRegistrationID.php");

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url.toURI());

                httpPost.setEntity(new UrlEncodedFormEntity(list));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();

                InputStream inputStream = httpEntity.getContent();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuffer value = new StringBuffer();

                while((line=bufferedReader.readLine())!=null){
                    value.append(line);
                }

                Log.i("Response from Server", value.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
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
