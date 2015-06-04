package com.nirma.varunraval.nuconnect;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Varun on 5/19/2015.
 */
public class LoginActivity extends Activity{

    static final int REQUEST_CODE_PICK_ACCOUNT = 999;
    String email;
    String scope;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        scope = "audience:server:client_id:611036220045-sjstaa7r37ufc1t4q0iotb1otng8ktj2.apps.googleusercontent.com";

        if(!isDeviceOnline()){
            Toast.makeText(getApplicationContext(), "Connect to a network to Log in", Toast.LENGTH_LONG).show();
        }
        else{
            pickUserAccount();

        }
    }

    private void pickUserAccount(){
        String accountTypes[] = new String[]{"com.google","in.ac.nirmauni"};
        Intent in = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null, null, null, null);

        startActivityForResult(in, REQUEST_CODE_PICK_ACCOUNT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE_PICK_ACCOUNT){
            if(resultCode == RESULT_OK){
                email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                if(isDeviceOnline()){
                    new GetUsername(LoginActivity.this, email, scope).execute();
                }
            }
            else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "You must chose an account to Login", Toast.LENGTH_SHORT).show();
            }
        }
    }

    boolean isDeviceOnline(){
        ConnectivityManager comMng = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = comMng.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnected())
            return true;
        return false;
    }

}

class GetUsername extends AsyncTask{

    Activity act;
    String email;
    String scope;

    GetUsername(Activity act, String email, String scope){
        this.act = act;
        this.email = email;
        this.scope = scope;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        try {
            String token = fetchToken();
            Log.i("Token", token);

            URL url = new URL("https://www.googleapis.com/oauth2/v3/tokeninfo?alt=json&id_token="+token);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

//            HttpClient httpClient = new DefaultHttpClient();
//            HttpGet pageGet = new HttpGet(url.toURI());
//            org.apache.http.HttpResponse hResponse = httpClient.execute(pageGet);

            InputStream iStream = conn.getInputStream();

            BufferedReader read = new BufferedReader(new InputStreamReader(iStream));

            String line;
            StringBuffer val = new StringBuffer();

            while((line = read.readLine())!=null){
                val.append(line);
            }

            Log.i("Value :", val.toString());

            JSONObject reader = new JSONObject(val.toString());

            String em = (String)reader.get("email");

            Log.i("Email is", em);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected String fetchToken() throws IOException{
        try {
            return GoogleAuthUtil.getToken(act, email, scope);
        } catch (GoogleAuthException e) {
            e.printStackTrace();
        }
        return null;
    }
}
