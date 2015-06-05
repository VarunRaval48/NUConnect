package com.nirma.varunraval.nuconnect;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;

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
    String oAuthscopes;
    int REQUEST_AUTHORIZATION = 998, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 997;
    static boolean handling = false;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        scope = "audience:server:client_id:611036220045-sjstaa7r37ufc1t4q0iotb1otng8ktj2.apps.googleusercontent.com";
        oAuthscopes = "oauth2:"+"https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/plus.login";

        if(!isDeviceOnline()){
            Toast.makeText(getApplicationContext(), "Connect to a network to Log in", Toast.LENGTH_LONG).show();
        }
        else{
            Log.i("Picking", "Picking User Account");
            pickUserAccount();
        }
    }

    private void pickUserAccount(){
        String accountTypes[] = new String[]{"com.google"}; //com.google.android.legacyimap for all types of accounts
        Intent in = AccountPicker.newChooseAccountIntent(null, null, accountTypes, true, null, null, null, null);

        startActivityForResult(in, REQUEST_CODE_PICK_ACCOUNT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.i("Activity Result", "Request Code "+requestCode+" Result Code "+resultCode);
        if(requestCode == REQUEST_CODE_PICK_ACCOUNT){
            if(resultCode == RESULT_OK){
                email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                if(isDeviceOnline()){
                    new GetUsername(LoginActivity.this, email, scope, oAuthscopes).execute();
                }
            }
            else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "You must chose an account to Login", Toast.LENGTH_SHORT).show();
                pickUserAccount();
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

    public void handleException(final Exception e){
        handling = true;
        runOnUiThread(new Runnable() {
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            LoginActivity.this, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent, REQUEST_AUTHORIZATION);
                }
            }
        });
        handling = false;
    }
}

class GetUsername extends AsyncTask{

    LoginActivity act;
    String email;
    String scope;
    String oAuthscopes;

    GetUsername(LoginActivity act, String email, String scope, String oAuthScopes){
        this.act = act;
        this.email = email;
        this.scope = scope;
        this.oAuthscopes = oAuthScopes;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            String idToken = fetchIDToken();
            String accessToken = fetchAccessToken();

            while(act.handling){}

            Log.i("ID Token", idToken);
            Log.i("Access Token", accessToken);

            URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token="+accessToken);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

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

    protected String fetchIDToken() throws IOException{
        try {
            return GoogleAuthUtil.getToken(act, email, scope);
        }
        catch(GooglePlayServicesAvailabilityException e){
            act.handleException(e);
        }
        catch(UserRecoverableAuthException e){
            act.handleException(e);
        }
        catch (GoogleAuthException e) {
            e.printStackTrace();
        }
        return null;
    }
    protected String fetchAccessToken() throws IOException{
        try {
            return GoogleAuthUtil.getToken(act, email, oAuthscopes);
        }
        catch(GooglePlayServicesAvailabilityException e){
            act.handleException(e);
        }
        catch(UserRecoverableAuthException e){
            act.handleException(e);
        }
        catch (GoogleAuthException e) {
            e.printStackTrace();
        }
        return null;
    }
}