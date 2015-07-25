package com.nirma.varunraval.nuconnect;

import android.os.AsyncTask;

//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.tcp.XMPPTCPConnection;
//import org.jivesoftware.smack.ConnectionConfiguration;
//import org.jivesoftware.smack.SmackException;
//import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
//
//import java.io.IOException;
//
//import javax.net.ssl.SSLSocketFactory;


/**
 * Created by Varun on 6/28/2015.
 */
public class XmppConnection extends AsyncTask<Void, Void, Void>{

    XmppConnection(){

    }

    @Override
    protected Void doInBackground(Void... params) {

//        ConnectionConfiguration connectionConfiguration =

//
//        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
//
//        configBuilder.setUsernameAndPassword(R.string.sender_id+"@gcm.googleapis.com", String.valueOf(R.string.api_key));
//        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
//        configBuilder.setSendPresence(false);
//        configBuilder.setSocketFactory(SSLSocketFactory.getDefault());
//        configBuilder.setHost("gcm-preprod.googleapis.com:5236");
//
//        XMPPTCPConnection con = new XMPPTCPConnection(configBuilder.build());
//
//        try {
//            con.connect();
//            con.login();
//        } catch (SmackException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        }
//
        return null;
    }

}
