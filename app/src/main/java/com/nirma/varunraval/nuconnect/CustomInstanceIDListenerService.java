package com.nirma.varunraval.nuconnect;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Varun on 6/21/2015.
 */
public class CustomInstanceIDListenerService extends InstanceIDListenerService{

    public void onTokenRefresh(){
        Intent intent = new Intent(this, RegisterDeviceService.class);
        startService(intent);
    }
}
