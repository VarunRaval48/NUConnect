package com.nirma.varunraval.nuconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * Created by Varun on 7/30/2015.
 */
public class SendIDToServer {

    public URL url;
    public List<NameValuePair> list;
    StringBuffer value;

    public SendIDToServer(String url, List<NameValuePair> list, Context context) throws MalformedURLException{
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.url = new URL("http://"+sharedPreferences.getString("NUConnect_ip", "192.168.1.6")+url);
        this.list = list;
        value = new StringBuffer();
    }

    public String sendToken(){
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url.toURI());
            httpPost.setEntity(new UrlEncodedFormEntity(list));

            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpEntity httpEntity = httpResponse.getEntity();

            InputStream inputStream = httpEntity.getContent();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while((line=bufferedReader.readLine())!=null){
                value.append(line);
            }
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return value.toString();
        }
    }
}
