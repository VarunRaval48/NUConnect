package com.nirma.varunraval.nuconnect.GCMToken;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.nirma.varunraval.nuconnect.GCMToken.RegisterDeviceService;
import com.nirma.varunraval.nuconnect.Login.LoginActivity;

/**
 * Created by Varun on 6/21/2015.
 */
public class CustomInstanceIDListenerService extends InstanceIDListenerService{

    public void onTokenRefresh(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        RegisterDeviceService.handleRefresh(getApplicationContext(), sharedPreferences.getString("email", null));
//        Intent intent = new Intent(this, RegisterDeviceService.class);
//        startService(intent);
    }
}
