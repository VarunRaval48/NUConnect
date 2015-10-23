package com.nirma.varunraval.nuconnect.body;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nirma.varunraval.nuconnect.R;
import com.nirma.varunraval.nuconnect.SendIDToServer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class BodyFragmentSentMessages extends Fragment {

    Context context;
    Activity activity;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    Chat_ExtraLecture_ArrayAdapter chat_extraLecture_arrayAdapter;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState){

        //TODO meaning of third parameter below
        View view = inflater.inflate(R.layout.fragment_body_sent_messages, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh_sent_messages);

        chat_extraLecture_arrayAdapter = new Chat_ExtraLecture_ArrayAdapter(context);
        listView = (ListView)view.findViewById(R.id.listView_sent_messages);
        listView.setAdapter(chat_extraLecture_arrayAdapter);

        if(chat_extraLecture_arrayAdapter.getCount()==0){
            loadItems();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update_sent_message_fragment();
            }
        });

        return view;
    }

    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.activity = activity;
    }

    public void onDetach(){
        super.onDetach();
    }

    public void update_sent_message_fragment(){

        int last_sent_message;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        last_sent_message = Integer.parseInt(sharedPreferences.getString("NUConnect_last_sent_message", "0"));
        new GetSentMessages().execute(last_sent_message);
    }

    private void appendToDatabase(){

    }

    private void loadItems(){

    }

    class GetSentMessages extends AsyncTask<Integer, Void, String>{

        @Override
        protected String doInBackground(Integer... params) {

            String result="";
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String email = sharedPreferences.getString("NUConnect_email", null);

            List<NameValuePair> list = new ArrayList();
            list.add(new BasicNameValuePair("last_sent_message", String.valueOf(params[0])));
            list.add(new BasicNameValuePair("email", email));

            String url = getResources().getString(R.string.server_url)+"getSentMessages.php";
            try {
                SendIDToServer sendIDToServer = new SendIDToServer(url, list, context);
                result = sendIDToServer.sendToken();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.i("BodyFragmentSentMessage", result);
            swipeRefreshLayout.setRefreshing(false);

        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.action_refresh:
                swipeRefreshLayout.setRefreshing(true);
                update_sent_message_fragment();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
