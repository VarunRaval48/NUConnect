package com.nirma.varunraval.nuconnect.message;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nirma.varunraval.nuconnect.body.BodyActivity;
import com.nirma.varunraval.nuconnect.login.LoginActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Varun on 8/6/2015.
 */
public class sendUpstreamMessage extends AsyncTask<Object, Void, Void>{

    ArrayList<String> reciepentList;
    JSONObject data;
    URL url;
    Resources resources;
    Context context;

    sendUpstreamMessage(){

    }

    public sendUpstreamMessage(ArrayList<String> reciepentList, JSONObject data, String url, Context context) throws MalformedURLException{
        this.reciepentList = new ArrayList<>();
        this.reciepentList = reciepentList;
        this.data = data;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.url = new URL("http://"+sharedPreferences.getString("NUConnect_ip", "192.168.1.6")+url);
        resources = Resources.getSystem();
        Log.i("Upstream", "In constructor");
        this.context = context;
    }

//    public void sendUpstream(){
//        Log.d("sendUpstream", "Before");
//        Object object[] = {reciepentList, data, url};
//        new sendUpstreamTask().execute(object);
//        Log.d("sendUpstream", "After");
//    }

    @Override
    protected Void doInBackground(Object[] params) {

        Log.i("In do in background", "");
        HttpClient httpClient = new DefaultHttpClient();

        JSONArray jsonArray = new JSONArray(reciepentList);
        try {
            Log.i("SendUpstream", "before httpPost");
            HttpPost httpPost = new HttpPost(url.toURI());

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            String json;
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("ids", jsonArray);
            jsonObject.accumulate("data", data);
            jsonObject.accumulate("action", "Instruct");
            jsonObject.accumulate("id", sharedPreferences.getString("NUConnect_email", null));
            jsonObject.accumulate("name", sharedPreferences.getString("NUConnect_username", null));

            json = jsonObject.toString();

            httpPost.setEntity(new StringEntity(json));

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.d("SendUpstream", "After httpPost");

            InputStream inputStream = httpResponse.getEntity().getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;StringBuffer result = new StringBuffer();

            Log.i("SendUpstream", "Before reading");
            while((line=br.readLine())!=null){
                result.append(line);
            }

            Log.i("Result from HTTPServer", result.toString());
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
//
//class sendUpstreamTask extends AsyncTask<Object, Object, Object>{
//
//    @Override
//    protected Object doInBackground(Object[] params) {
//
//        Log.d("In do in background", "");
//        HttpClient httpClient = new DefaultHttpClient();
//
//        ArrayList<String> reciepentList = (ArrayList)params[0];
//        String data = (String)params[1];
//        URL url = (URL)params[2];
//        try {
//            Log.d("SendUpstream", "before httpPost");
//            HttpPost httpPost = new HttpPost(url.toURI());
//
//            String json;
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.accumulate("ids", reciepentList.toArray());
//            jsonObject.accumulate("data", data);
//            jsonObject.accumulate("action", data);
//
//            json = jsonObject.toString();
//
//            httpPost.setEntity(new StringEntity(json));
//
//            httpPost.setHeader("Accept", "application/json");
//            httpPost.setHeader("Content-type", "application/json");
//
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//            Log.d("SendUpstream", "After httpPost");
//
//            InputStream inputStream = httpResponse.getEntity().getContent();
//            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//            String line;StringBuffer result= new StringBuffer();
//
//            Log.d("SendUpstream", "Before reading");
//            while((line=br.readLine())!=null){
//                result.append(line);
//            }
//
//            Log.d("Result from HTTPServer", result.toString());
//        }
//        catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        return null;
//    }
//}

