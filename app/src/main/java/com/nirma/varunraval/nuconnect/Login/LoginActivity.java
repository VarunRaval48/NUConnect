package com.nirma.varunraval.nuconnect.Login;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nirma.varunraval.nuconnect.Body.BodyActivity;
import com.nirma.varunraval.nuconnect.R;
import com.nirma.varunraval.nuconnect.GCMToken.RegisterDeviceService;
import com.nirma.varunraval.nuconnect.SendIDToServer;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Varun on 5/19/2015.
 */

//TODO Allow only nirmauni.ac.in accounts
//TODO Remember signed in activity

public class LoginActivity extends Activity implements RetryLoginFragment.OnFragmentRetryInteractionListener,
        NextFragment.OnNextFragmentInteractionListener {

    static final int REQUEST_CODE_PICK_ACCOUNT = 999;
    String email;
    String scope;
    String oAuthscopes;
    int REQUEST_AUTHORIZATION = 998, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 997;
    static boolean handling = false;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    FragmentManager fragmentManager;
    public static String nuconnect_accessToken;
    static String login_type = null;
    static int serverConnected=0;

//    public static String serverURL = "192.168.1.11:9000";


    public void setAccessToken(String accessToken){
        nuconnect_accessToken = accessToken;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Log.i("View", "After setting view");

        scope = "audience:server:client_id:611036220045-sjstaa7r37ufc1t4q0iotb1otng8ktj2.apps.googleusercontent.com";
        oAuthscopes = "oauth2:" + "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/plus.login";


        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.login_type, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        Button nextTempButton = (Button)findViewById(R.id.buttonTempNext);
        nextTempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte = new Intent(LoginActivity.this, BodyActivity.class);
                Bundle arg = new Bundle();

                arg.putString("email", "test");
                arg.putString("name", "test");
                arg.putBoolean("verified", true);
                arg.putString("login_type", "Faculty");

                inte.putExtra("user_info", arg);

                startActivity(inte);

                finish();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                login_type = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        inflateNext();

//        int retry=5;
//        if(isDeviceOnline()){
//            Log.i("Picking", "Picking User Account");
//            pickUserAccount();
//        }
//        else{
//            Log.i("Network", "Not able to connect");
//            Toast.makeText(getApplicationContext(), "Connect to a network to Log in Retrying in "+retry, Toast.LENGTH_SHORT).show();
//
//            Runnable r = new Runnable(){
//                public void run(){
//                    Toast.makeText(getApplicationContext(), "Retrying to Connect", Toast.LENGTH_SHORT).show();
//                }
//            };
//
//            while(!isDeviceOnline()){
//              Thread.sleep(retry*1000);
//                worker.schedule(r, retry, TimeUnit.SECONDS);
//                retry+=5;
//            }
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            pickUserAccount();
//        }
    }

    public void onPause(){
        super.onPause();
        Log.i("Checking", "in onPause");
    }

    public void pickUserAccount() {

        if (isDeviceOnline()) {
            Log.i("Picking", "Picking User Account");
            String accountTypes[] = new String[]{"com.google"}; //com.google.android.legacyimap for all types of accounts
            Intent in = AccountPicker.newChooseAccountIntent(null, null, accountTypes, true, null, null, null, null);

            startActivityForResult(in, REQUEST_CODE_PICK_ACCOUNT);
        } else {
            Log.i("Network", "Not able to connect");
            Toast.makeText(getApplicationContext(), "Connect to a network to Log in", Toast.LENGTH_SHORT).show();
            inflateRetry();
        }
    }

    protected void inflateRetry() {
        Fragment retryFragment = new RetryLoginFragment();
        fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.frameLaoyoutSpin, retryFragment).commit();
        fragmentManager.executePendingTransactions();
    }

    protected void inflateNext(){
        Fragment nextFragment = new NextFragment();
        fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.frameLaoyoutSpin, nextFragment).commit();
        fragmentManager.executePendingTransactions();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Activity Result", "Request Code " + requestCode + " Result Code " + resultCode);
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                if (!email.endsWith("nirmauni.ac.in")) {
                    Log.i("Toast", "Wrong Account");
                    Toast.makeText(this, "You must chose nirmauni account", Toast.LENGTH_SHORT).show();
                    inflateRetry();
                } else if (isDeviceOnline()) {
                    new GetUsername(LoginActivity.this, email, scope, oAuthscopes).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Network is not accessible. Please recheck.", Toast.LENGTH_SHORT).show();
                    inflateRetry();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You must chose an account to Login", Toast.LENGTH_SHORT).show();
                inflateRetry();
            }
        }
        else if(requestCode == REQUEST_AUTHORIZATION){
            if(resultCode == RESULT_OK){
                new GetUsername(LoginActivity.this, email, scope, oAuthscopes).execute();
            }
            else{
                inflateRetry();
            }
        }
        else if(resultCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR){
            //called when returning from GooglePlayServices Exception
            inflateRetry();
        }
    }

    boolean isDeviceOnline() {
        ConnectivityManager comMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = comMng.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected())
            return true;
        return false;
    }

    public void handleException(final Exception e) {
        handling = true;
        runOnUiThread(new Runnable() {
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            LoginActivity.this, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
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

    public void showSpinner(){
        runOnUiThread(new Runnable() {
            public void run() {
                Fragment fragment = new SpinnerFragment();

                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.frameLaoyoutSpin, fragment).commit();

                fragmentManager.executePendingTransactions();
            }
        });
    }

    public void setUsername(Bundle arg){

        Log.i("Checking", "Calling sendtoserver class");
        new SendTokenToServerClass(arg.getString("email"), arg).execute();
        //TODO send access token to server
    }

    @Override
    public void onNextFragmentInteraction() {
        pickUserAccount();
    }


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
                    URL url = new URL(getResources().getString(R.string.server_url) + "/checkServerConnection.php");

                    SendIDToServer sendIDToServer = new SendIDToServer(url, nameValuePairs);
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
            if(serverConnected == 1)
                validateAndGo(arg);
            else
                doOnUIInflate("retry", new IOException());
        }

    }

    void validateAndGo(Bundle arg) {

        if (arg.getBoolean("verified")) {

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putString("NUConnect_username", arg.getString("name"));
            editor.putString("NUConnect_email", arg.getString("email"));
            editor.putString("NUConnect_accesstoken", nuconnect_accessToken);
            editor.putString("NUConnect_login_type", login_type);
            editor.commit();

            Log.i("name", arg.getString("name"));

//            setUsername(getApplicationContext(), arg);

            arg.putString("login_type", login_type);
            Intent in = new Intent(LoginActivity.this, BodyActivity.class);
            in.putExtra("user_info", arg);

            RegisterDeviceService.handleNew(this, email);
//            Intent serviceIntent = new Intent(this, RegisterDeviceService.class);
//            serviceIntent.putExtra("email", arg.getString("email"));
//            startService(serviceIntent);

            startActivity(in);
        }

    }

    @Override
    public void onFragmentRetryInteraction() {
        pickUserAccount();
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

    public class GetUsername extends AsyncTask<Object, Void, Bundle> {

        LoginActivity act;
        String email;
        String scope;
        String oAuthscopes;
        List<NameValuePair> nameValuePairs;
        URL serverCheckuser;
        boolean waiting = false;

        String idToken = null;
        String accessToken = null;
//    ProgressDialog progressDialog;

        GetUsername(LoginActivity act, String email, String scope, String oAuthScopes) {
            this.act = act;
            this.email = email;
            this.scope = scope;
            this.oAuthscopes = oAuthScopes;

            nameValuePairs = new ArrayList<>();
//        progressDialog = new ProgressDialog(act.getApplicationContext());
        }


        protected void onPreExecute() {
            showSpinner();
//        this.progressDialog.setMessage("Please Wait...");
//        this.progressDialog.show();
        }

        @Override
        protected Bundle doInBackground(Object... params) {

            Bundle values = new Bundle();

            try {
//                idToken = fetchIDToken();
                accessToken = fetchAccessToken();

                Log.i("Before", "Before waiting " + handling);
//                while (handling) {
//                    Log.i("Before", "In Before waiting");
//                    waiting = true;
//                    wait();
//                }

                if (accessToken != null) {

//                    Log.i("ID Token", idToken);
                    Log.i("Access Token", accessToken);

                    setAccessToken(accessToken);
//                    nameValuePairs.add(new BasicNameValuePair("id_token", idToken));
//                    nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));

                    URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken);
                    StringBuffer val = returnJson(url);

//                    url = new URL("https://www.googleapis.com/oauth2/v1/tokeninfo?alt=json&id_token=" + idToken);
//                    StringBuffer valVerify = returnJson(url);

                    Log.i("Value :", val.toString());
//                    Log.i("Value Verified :", valVerify.toString());

                    //            serverCheckuser = new URL("http://192.168.1.6:9000/nuconnect/checkuser.php");
                    //            serverCheckuser = new URL("http://192.168.1.6:9000/nuconnect/checkuser.php?id_token="+idToken+"&access_token="+accessToken);
                    //
                    //            Log.i("Request", "Requesting server to get name");
                    //
                    //            StringBuffer valueVerify = validate_and_get(serverCheckuser);
                    //
                    //            Log.i("Value Verified :", valueVerify.toString());
                    //
                    //            JSONArray jArray = new JSONArray(valueVerify.toString());
                    //
                    JSONObject reader;
                    reader = new JSONObject(val.toString());
                    //            reader = jArray.getJSONObject(0);

                    values.putString("email", (String) reader.get("email"));
                    values.putString("name", (String) reader.get("name"));

//                    reader = new JSONObject(valVerify.toString());
                    //            reader = jArray.getJSONObject(1);
                    values.putBoolean("verified", reader.getBoolean("verified_email"));

                    //            Log.i("Email is", em);
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
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }

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
                act.handling = true;
                Log.i("Before", "Before calling handle Exception gPlay " + act.handling);
                act.handleException(e);
            } catch (UserRecoverableAuthException e) {
                act.handling = true;
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

//        HttpClient httpClient;
//        HttpPost httpPost;
//        HttpResponse httpResponse;
//        HttpEntity httpEntity = null;
//        try
//        {
//            httpClient = new DefaultHttpClient();
//            httpPost = new HttpPost(url.toURI());
//            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//            httpResponse = httpClient.execute(httpPost);
//            httpEntity = httpResponse.getEntity();
//        }
//        catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//
//        InputStream iStream = httpEntity.getContent();
//
//        BufferedReader read = new BufferedReader(new InputStreamReader(iStream,"iso-8859-1"),8);
//
//        String line;
//        StringBuffer val = new StringBuffer();
//
//        while((line = read.readLine())!=null){
//            val.append(line);
//        }

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
}