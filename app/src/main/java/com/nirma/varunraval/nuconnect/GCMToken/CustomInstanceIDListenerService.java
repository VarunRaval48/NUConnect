package com.nirma.varunraval.nuconnect.GCMToken;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.nirma.varunraval.nuconnect.GCMToken.RegisterDeviceService;

/**
 * Created by Varun on 6/21/2015.
 */
public class CustomInstanceIDListenerService extends InstanceIDListenerService{

    public void onTokenRefresh(){
        Intent intent = new Intent(this, RegisterDeviceService.class);
        startService(intent);
    }
}
