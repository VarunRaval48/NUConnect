package com.nirma.varunraval.nuconnect.body;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nirma.varunraval.nuconnect.R;

public class Settings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final EditText editText = (EditText) findViewById(R.id.editText_ip);
        Button button = (Button) findViewById(R.id.button_set_ip);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = editText.getText().toString();

                SharedPreferences.Editor sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                sharedPreferences.putString("NUConnect_ip", ip);
                sharedPreferences.commit();
            }
        });
    }

}
