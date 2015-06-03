package com.nirma.varunraval.nuconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Varun on 5/19/2015.
 */
public class LoginActivity extends Activity{

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ConnectivityManager comMng = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = comMng.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected()){
            Toast.makeText(getApplicationContext(), "Connect to a network to Log in", Toast.LENGTH_LONG).show();
        }

        Button logIn = (Button)findViewById(R.id.LogInbutton);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //here return data that is obtained using oAuth 2

                if(netInfo!=null && netInfo.isConnected()){
                    Intent data = new Intent();
                    data.putExtra("Result", "Returned from lgoin");
                }
                else{
                    Toast.makeText(getApplicationContext(), "Connect to a network to Log in", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
