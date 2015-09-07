package com.nirma.varunraval.nuconnect.gcmtoken;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Varun on 6/21/2015.
 */
public class CustomInstanceIDListenerService extends InstanceIDListenerService{

    public void onTokenRefresh(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        RegisterDeviceService.handleRefresh(getApplicationContext(), sharedPreferences.getString("NUConnect_email", null));
//        Intent intent = new Intent(this, RegisterDeviceService.class);
//        startService(intent);
    }
}
