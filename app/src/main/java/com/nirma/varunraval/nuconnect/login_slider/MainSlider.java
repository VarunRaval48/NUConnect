package com.nirma.varunraval.nuconnect.login_slider;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nirma.varunraval.nuconnect.MainActivity;
import com.nirma.varunraval.nuconnect.R;
import com.nirma.varunraval.nuconnect.SendIDToServer;
import com.nirma.varunraval.nuconnect.body.BodyActivity;
import com.nirma.varunraval.nuconnect.gcmtoken.RegisterDeviceService;
import com.nirma.varunraval.nuconnect.login.NextFragment;
import com.nirma.varunraval.nuconnect.login.RetryLoginFragment;
import com.nirma.varunraval.nuconnect.login.SpinnerFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainSlider extends FragmentActivity implements Login_Fragment.OnFragmentLogINInteractionListener,
        NextFragment.OnNextFragmentInteractionListener, RetryLoginFragment.OnFragmentRetryInteractionListener{

    private static final int MAX_PAGES = 2;
    FragmentManager fragmentManager;
    static final int REQUEST_CODE_PICK_ACCOUNT = 999, REQUEST_AUTHORIZATION = 998, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 997;
    String email, scope, oAuthscopes, nuconnect_accessToken, email_initials, name, login_type;
    static boolean handling = false;
    private int serverConnected;
    private ViewPager mpager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_slider);

        mpager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new ScreenSlidePageAdapter(getSupportFragmentManager());
        scope = "audience:server:client_id:611036220045-sjstaa7r37ufc1t4q0iotb1otng8ktj2.apps.googleusercontent.com";
        oAuthscopes = "oauth2:" + "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/plus.login";

        mpager.setAdapter(mPagerAdapter);
//        new setAdapterTask().execute();

        //TODO add setOnPageChangeListener

    }

    private class setAdapterTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        protected  void onPostExecute(Void result){
            mpager.setAdapter(mPagerAdapter);
        }
    }


    protected void onPostResume(){
        super.onPostResume();

        if(inflate_retry){
            inflateRetry();
        }
        if(inflate_spinner){
            showSpinner();
        }
        inflate_spinner = false;
        inflate_retry = false;
    }

    @Override
    public void onFragmentLogINInteraction(String name) {

        if(name.equals("temp_next")){
            Intent inte = new Intent(MainSlider.this, BodyActivity.class);
            Bundle arg = new Bundle();

            arg.putString("email", "test");
            arg.putString("name", "test");
            arg.putBoolean("verified", true);
            arg.putString("login_type", "Faculty");

            inte.putExtra("user_info", arg);

            startActivity(inte);

            finish();
        }
//        else if(name.equals("inflate_next")){
//            inflateNext();
//        }
        else if(name.equals("pickUser")){
            pickUserAccount();
        }
    }

    protected void inflateNext(){
        Fragment nextFragment = new NextFragment();
        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.frameLaoyoutSpin, nextFragment).commit();
        fragmentManager.executePendingTransactions();
    }

    @Override
    public void onNextFragmentInteraction() {
        pickUserAccount();
    }

    public void pickUserAccount() {

        if (isDeviceOnline()) {
            Log.i("Picking", "Picking User Account");
            String accountTypes[] = new String[]{"com.google"}; //com.google.android.legacyimap for all types of accounts
            Intent in = AccountPicker.newChooseAccountIntent(null, null, accountTypes, true, null, null, null, null);

            startActivityForResult(in, REQUEST_CODE_PICK_ACCOUNT);
        }
        else {
            Log.i("Network", "Not able to connect");
            Toast.makeText(getApplicationContext(), "Connect to a network to Log in", Toast.LENGTH_SHORT).show();
            inflateRetry();
        }
    }

    protected void inflateRetry() {
        Fragment retryFragment = RetryLoginFragment.newInstance("retry");
        fragmentManager = this.getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.frameLaoyoutSpin, retryFragment).commit();
        fragmentManager.executePendingTransactions();
    }

    boolean inflate_retry = false;
    boolean inflate_spinner = false;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Activity Result", "Request Code " + requestCode + " Result Code " + resultCode);
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                Log.i("MainSlider", email);
                if (!email.endsWith("nirmauni.ac.in") && false) {
                    Log.i("Toast", "Wrong Account");
                    Toast.makeText(this, "You must chose nirmauni account", Toast.LENGTH_SHORT).show();
//                    inflateRetry();
                    inflate_retry = true;
                } else if (isDeviceOnline()) {
                    new GetUsername(MainSlider.this, email, scope, oAuthscopes).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Network is not accessible. Please recheck.", Toast.LENGTH_SHORT).show();
//                    inflateRetry();
                    inflate_retry = true;
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You must chose an account to Login", Toast.LENGTH_SHORT).show();
//                inflateRetry();
                inflate_retry = true;
            }
        }
        else if(requestCode == REQUEST_AUTHORIZATION){
            if(resultCode == RESULT_OK){
                new GetUsername(this, email, scope, oAuthscopes).execute();
            }
            else{
//                inflateRetry();
                inflate_retry = true;
            }
        }
        else if(resultCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR){
            //called when returning from GooglePlayServices Exception
//            inflateRetry();
            inflate_retry = true;
        }
    }

    private class ScreenSlidePageAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePageAdapter(FragmentManager fm){
            super(fm);
        }

        public Fragment getItem(int position){
            Log.i("MainSlider", "Position: "+position);
            if(position==0){
                return new Welcome_Fragment();
            }
            else {
                return new Login_Fragment();
            }
        }

        public int getCount(){
            return MAX_PAGES;
        }
    }

    ProgressDialog progressDialog;
    public void showSpinner(){
        runOnUiThread(new Runnable() {
            public void run() {
                inflate_spinner = true;
                Fragment fragment = RetryLoginFragment.newInstance("spinner");

                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.frameLaoyoutSpin, fragment).commitAllowingStateLoss();

                fragmentManager.executePendingTransactions();

//                progressDialog = new ProgressDialog(getApplicationContext());
//                progressDialog.setTitle("Please Wait");
//                progressDialog.setMessage("Wait");
//                progressDialog.setCancelable(false);
//                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                progressDialog.show();
            }
        });
    }

    @Override
    public void onFragmentRetryInteraction() {
        pickUserAccount();
    }
    public boolean isDeviceOnline() {
        ConnectivityManager comMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = comMng.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected())
            return true;
        return false;
    }

    public void doOnUIInflate(final String typeOfFragment,final Exception exceptionType){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(exceptionType instanceof IOException){
                    Toast.makeText(getApplicationContext(), "Network is not accessible. Please recheck.", Toast.LENGTH_SHORT).show();
                }
                if(typeOfFragment.equals("retry")){
                    inflateRetry();
                }
            }
        });
    }

    public void setUsername(Bundle arg){

        Log.i("Checking", "Calling sendtoserver class");
        new SendTokenToServerClass(arg.getString("email"), arg).execute();
        //TODO send access token to server
    }
    //To check access token and weather device is connected to server or not
    public class SendTokenToServerClass extends AsyncTask<Object, Void, Void> {

        String email;
        Bundle arg;
        String line;
        String value;

        SendTokenToServerClass(String email, Bundle arg) {
            this.email = email;
            this.arg = arg;
        }

        @Override
        protected Void doInBackground(Object... params) {
            Log.i("Calling sendtokenmethod", "In do in Background");
            try {

                if (!nuconnect_accessToken.equals(null)) {

                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("access_token", nuconnect_accessToken));
//                    nameValuePairs.add(new BasicNameValuePair("email", email));
                    String url = (getResources().getString(R.string.server_url) + "checkServerConnection.php");

                    SendIDToServer sendIDToServer = new SendIDToServer(url, nameValuePairs, getApplicationContext());
                    value = sendIDToServer.sendToken();

                    Log.i("InSendTokenToServerJson", value.toString());
                    JSONObject reader = new JSONObject(value.toString());

                    serverConnected = reader.getInt("success");

                    Log.i("Returned from Server", reader.getString("message"));
                }

            }catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (IOException e) {
                doOnUIInflate("retry", e);
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void params){
//            progressDialog.dismiss();
            if(serverConnected == 1)
                validateAndGo(arg);
            else
                doOnUIInflate("retry", new IOException());
        }
    }

    void validateAndGo(Bundle arg) {

        if (arg.getBoolean("verified")) {

            login_type = Login_Fragment.login_type;

            email_initials = arg.getString("email").split("@")[0];
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putString("NUConnect_username", arg.getString("name"));
            editor.putString("NUConnect_email", email_initials);
            editor.putString("NUConnect_accesstoken", nuconnect_accessToken);
            editor.putString("NUConnect_login_type", login_type);
            editor.commit();

            Log.i("MainSlider_name_email", arg.getString("name")+" "+email_initials);

//            setUsername(getApplicationContext(), arg);

            arg.putString("login_type", login_type);
            Intent in = new Intent(MainSlider.this, BodyActivity.class);
            in.putExtra("user_info", arg);
            in.putExtra("intent_type", "new_login");

            RegisterDeviceService.handleNew(this, email_initials);
//            Intent serviceIntent = new Intent(this, RegisterDeviceService.class);
//            serviceIntent.putExtra("email", arg.getString("email"));
//            startService(serviceIntent);
            Log.i("MainSlider", arg.toString());
            startActivity(in);
        }

    }

    public void setAccessToken(String accessToken){
        nuconnect_accessToken = accessToken;
    }

    public class GetUsername extends AsyncTask<Object, Void, Bundle> {

        MainSlider act;
        String email;
        String scope;
        String oAuthscopes;
        List<NameValuePair> nameValuePairs;
        URL serverCheckuser;
        boolean waiting = false;

        String idToken = null;
        String accessToken = null;
//    ProgressDialog progressDialog;

        GetUsername(MainSlider act, String email, String scope, String oAuthScopes) {
            this.act = act;
            this.email = email;
            this.scope = scope;
            this.oAuthscopes = oAuthScopes;

            nameValuePairs = new ArrayList<>();
        }


        protected void onPreExecute() {
            showSpinner();
        }

        @Override
        protected Bundle doInBackground(Object... params) {

            Bundle values = null;

            try {
//                idToken = fetchIDToken();
                accessToken = fetchAccessToken();

                Log.i("Before", "Before waiting " + handling);

                if (accessToken != null) {

//                    Log.i("ID Token", idToken);
                    Log.i("Access Token", accessToken);

                    setAccessToken(accessToken);

                    URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken);
                    StringBuffer val = returnJson(url);

//                    url = new URL("https://www.googleapis.com/oauth2/v1/tokeninfo?alt=json&id_token=" + idToken);
//                    StringBuffer valVerify = returnJson(url);

                    Log.i("Value :", val.toString());
//                    Log.i("Value Verified :", valVerify.toString());

                    JSONObject reader;
                    reader = new JSONObject(val.toString());
                    //            reader = jArray.getJSONObject(0);

                    values = new Bundle();

                    name = (String) reader.get("name");
                    values.putString("email", (String) reader.get("email"));
                    values.putString("name", name);

//                    reader = new JSONObject(valVerify.toString());
                    //            reader = jArray.getJSONObject(1);
                    values.putBoolean("verified", reader.getBoolean("verified_email"));

                }
                else {
//                    Toast.makeText(act.getApplicationContext(), "Login Error", Toast.LENGTH_SHORT).show();
//                    inflateRetry();
                }
            } catch (IOException e) {
                doOnUIInflate("retry", e);
                e.printStackTrace();
            } catch (JSONException e) {
                doOnUIInflate("retry", e);
                e.printStackTrace();
            }
            return values;
        }

        protected void onProgressUpdate(Void... values) {
        }

        protected void onPostExecute(Bundle result) {

//        if(progressDialog.isShowing()){
//            progressDialog.dismiss();
//        }
//            inflateRetry();
//            validateAndGo(result);
            if(result!=null)
                setUsername(result);
        }

        protected String fetchIDToken() throws IOException {
            try {
                return GoogleAuthUtil.getToken(act, email, scope);
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

        protected String fetchAccessToken() throws IOException {
            try {
                return GoogleAuthUtil.getToken(act, email, oAuthscopes);
            } catch (GooglePlayServicesAvailabilityException e) {
                handling = true;
                Log.i("Before", "Before calling handle Exception gPlay " + handling);
                act.handleException(e);
            } catch (UserRecoverableAuthException e) {
                handling = true;
                Log.i("Before", "Before calling handle Exception recoverable");
                act.handleException(e);
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }
            return null;
        }

        StringBuffer returnJson(URL url) throws IOException {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            InputStream iStream = conn.getInputStream();

            BufferedReader read = new BufferedReader(new InputStreamReader(iStream));

            String line;
            StringBuffer val = new StringBuffer();

            while ((line = read.readLine()) != null) {
                val.append(line);
            }

            return val;
        }

        StringBuffer validate_and_get(URL url) throws IOException {

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            InputStream iStream = conn.getInputStream();

            BufferedReader read = new BufferedReader(new InputStreamReader(iStream));

            String line;
            StringBuffer val = new StringBuffer();

            while ((line = read.readLine()) != null) {
                val.append(line);
            }

            return val;

        }
    }

    public void handleException(final Exception e) {
        handling = true;
        runOnUiThread(new Runnable() {
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            MainSlider.this, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
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

}
