package com.nirma.varunraval.nuconnect;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nirma.varunraval.nuconnect.body.BodyActivity;
import com.nirma.varunraval.nuconnect.body.Settings;
import com.nirma.varunraval.nuconnect.gcmtoken.RegisterDeviceService;
import com.nirma.varunraval.nuconnect.login_slider.MainSlider;

import java.io.IOException;


public class MainActivity extends Activity {

    static boolean isSignedIn = false;
    static int logInReqestCode = 99;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    static String email, nuconnect_accesstoken, username, login_type;
    boolean handling = false;
    int REQUEST_AUTHORIZATION = 998, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 997;
    final int PLAY_SERVICES_RESOLUTION_REQUEST=990;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("MainAc", "Calling LoginClass");

        //fetch isSignedIn from memory or cache

        boolean check = checkPlayServices();
        Log.i("GooglePlayServices", ""+check);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = sharedPreferences.getString("NUConnect_username", null);
        email = sharedPreferences.getString("NUConnect_email", null);
        Log.i("Main Activity", email+" ");
        nuconnect_accesstoken = sharedPreferences.getString("NUConnect_accesstoken", null);
        login_type = sharedPreferences.getString("NUConnect_login_type", null);

        Log.i("Check", "Going to check username");
        if(username!=null){
            Log.i("username", username);
            isSignedIn = true;
        }
        else{
            isSignedIn = false;
        }

//        mFragmentManager = getFragmentManager();
//        mFragmentTransaction = mFragmentManager.beginTransaction();

        Bundle arg = new Bundle();

        //TODO check access token with server
        if(isSignedIn){
//            arg.putString("name", username);
//            arg.putString("email", email);
//            arg.putString("login_type", login_type);
//
//            Intent in = new Intent(MainActivity.this, BodyActivity.class);
//            in.putExtra("user_info", arg);
//            startActivity(in);
            new FetchToken().execute();
        }
        else{
            Intent in = new Intent(MainActivity.this, MainSlider.class);
//            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(in);
        }
    }

    public void onResume(){
        super.onResume();
        boolean check = checkPlayServices();
        Log.i("GooglePlayServices", "" + check);
    }
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();
        }
        return true;
    }

    void giveResult(boolean update){
        if(update){
//            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
//            editor.remove("NUConnect_accesstoken");
//            editor.putString("NUConnect_accesstoken", nuconnect_accesstoken);
//            editor.commit();
            RegisterDeviceService.handleUpdate(getApplicationContext(), email, nuconnect_accesstoken);
//            ConnectServer connectServer = new ConnectServer();
//            connectServer.initialize(getApplicationContext(), nuconnect_accesstoken, email);
        }
        else{
            RegisterDeviceService.handleConnect(getApplicationContext(), email);
        }
        Bundle arg = new Bundle();
        arg.putString("name", username);
        arg.putString("email", email);
        arg.putString("login_type", login_type);

        Intent in = new Intent(MainActivity.this, BodyActivity.class);
        in.putExtra("intent_type", "resuming");
        in.putExtra("user_info", arg);
        startActivity(in);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent in = new Intent(this, Settings.class);
            startActivity(in);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void handleException(final Exception e) {
        handling = true;
        runOnUiThread(new Runnable() {
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            MainActivity.this, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    Log.i("Check First", "Going to handle");
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent, REQUEST_AUTHORIZATION);
                }
            }
        });
        handling = false;
//        notify();
    }

    public class FetchToken extends AsyncTask<Object, Void, Void>{

        String oAuthscopes = "oauth2:" + "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/plus.login";
        String access_token="";
        boolean update = false;
//        MainActivity man;
//        String email;

        FetchToken(){
//            this.man = man;
//            this.email = email;
        }

        @Override
        protected Void doInBackground(Object... params) {

            try {
                access_token = fetchAccessToken();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void params){
            if(!access_token.equals(MainActivity.nuconnect_accesstoken)){
                nuconnect_accesstoken = access_token;
                update = true;
            }
            giveResult(update);
        }

        protected String fetchAccessToken() throws IOException {
            try {
                return GoogleAuthUtil.getToken(getApplicationContext(), email+"@nirmauni.ac.in", oAuthscopes);
            } catch (GooglePlayServicesAvailabilityException e) {
                handling = true;
                Log.i("Before", "Before calling handle Exception gPlay " + handling);
                handleException(e);
            } catch (UserRecoverableAuthException e) {
                handling = true;
                Log.i("Before", "Before calling handle Exception recoverable");
                handleException(e);
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
