package com.nirma.varunraval.nuconnect;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    static boolean isSignedIn = false;
    static int logInReqestCode = 99;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("MainAc", "Calling LoginClass");

        //fetch isSignedIn from memory or cache

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = sharedPreferences.getString("NUConnect_username", null);
        String email = sharedPreferences.getString("NUConnect_email", null);
        String login_type = sharedPreferences.getString("NUConnect_login_type", null);

        Log.i("Check", "Going to check username");
        if(username!=null){
            Log.i("username", username);
            isSignedIn = true;
        }
        else{
            isSignedIn = false;
        }

        mFragmentManager = getFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        Bundle arg = new Bundle();

        //TODO check access token with server
        if(isSignedIn){
            arg.putString("name", username);
            arg.putString("email", email);
            arg.putString("login_type", login_type);

            Intent in = new Intent(MainActivity.this, BodyActivity.class);
            in.putExtra("user_info", arg);
            startActivity(in);
        }
        else{
            Intent in = new Intent(MainActivity.this, WelcomeActivity.class);
//            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(in);
        }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
