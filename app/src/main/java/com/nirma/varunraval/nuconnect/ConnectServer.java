package com.nirma.varunraval.nuconnect;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nirma.varunraval.nuconnect.login.LoginActivity;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ConnectServer extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.nirma.varunraval.nuconnect.action.FOO";
    private static final String ACTION_BAZ = "com.nirma.varunraval.nuconnect.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.nirma.varunraval.nuconnect.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.nirma.varunraval.nuconnect.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void initialize(Context context, String nuconnect_accesstoken, String email) {
        Intent intent = new Intent(context, ConnectServer.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, nuconnect_accesstoken);
        intent.putExtra(EXTRA_PARAM2, email);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
//    public static void startActionBaz(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, ConnectServer.class);
//        intent.setAction(ACTION_BAZ);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }

    public ConnectServer() {
        super("ConnectServer");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String nuconnect_accesstoken = intent.getStringExtra(EXTRA_PARAM1);
                final String email = intent.getStringExtra(EXTRA_PARAM2);
                giveResult(nuconnect_accesstoken, email);
            }
//            else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
        }
    }

    void giveResult(String nuconnect_accesstoken, String email){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.remove("NUConnect_accesstoken");
        editor.putString("NUConnect_accesstoken", nuconnect_accesstoken);
        editor.commit();

        LoginActivity.nuconnect_accessToken = nuconnect_accesstoken;
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("access_token", nuconnect_accesstoken));
        nameValuePairs.add(new BasicNameValuePair("email", email));
        try {
            URL url = new URL(getResources().getString(R.string.server_url)+"/setaccesstoken.php");
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url.toURI());

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            httpClient.execute(httpPost);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
