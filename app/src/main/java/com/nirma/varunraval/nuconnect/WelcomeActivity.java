package com.nirma.varunraval.nuconnect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.nirma.varunraval.nuconnect.Login.LoginActivity;


public class WelcomeActivity extends Activity{

    ImageButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loginButton = (ImageButton) findViewById(R.id.loginMainButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startLoginIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(startLoginIntent);
            }
        });

    }
}
