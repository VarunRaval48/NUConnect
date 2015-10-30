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

import com.nirma.varunraval.nuconnect.Database.MessagesDatabasedbHelper;
import com.nirma.varunraval.nuconnect.R;
import com.nirma.varunraval.nuconnect.SendIDToServer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class BodyFragmentSentMessages extends Fragment {

    Context context;
    Activity activity;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    Chat_ExtraLecture_ArrayAdapter chat_extraLecture_arrayAdapter;
    MessagesDatabasedbHelper messagesDatabasedbHelper;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();

        messagesDatabasedbHelper = new MessagesDatabasedbHelper(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState){

        //TODO meaning of third parameter below
        View view = inflater.inflate(R.layout.fragment_body_sent_messages, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh_sent_messages);

        chat_extraLecture_arrayAdapter = new Chat_ExtraLecture_ArrayAdapter(context, "SentMessages");
        listView = (ListView)view.findViewById(R.id.listView_sent_messages);
        listView.setAdapter(chat_extraLecture_arrayAdapter);

        if(chat_extraLecture_arrayAdapter.getCount()==0){
            loadItems_fromdatabase(null);
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
        messagesDatabasedbHelper.close();
    }

    public void update_sent_message_fragment(){

        int last_sent_message;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        last_sent_message = Integer.parseInt(sharedPreferences.getString("NUConnect_last_sent_message", "0"));
        Log.i("update_sent", last_sent_message+"");
        new GetSentMessages().execute(last_sent_message);
    }

    private void loadItems_fromdatabase(ArrayList newAdded){

        Log.i("LoadItensFromData", "Loading");

        new AsyncTask(){

            @Override
            protected ArrayList doInBackground(Object[] params) {
                return messagesDatabasedbHelper.getRowsSent((ArrayList) params[0]);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                chat_extraLecture_arrayAdapter.addAll((ArrayList)o);
            }
        }.execute(newAdded);
    }

    class GetSentMessages extends AsyncTask<Integer, Void, ArrayList>{

        @Override
        protected ArrayList doInBackground(Integer... params) {

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

            Log.i("BodyFragmentSentMessage", result);

            if(!result.equals("0")) {
//            ArrayList<Integer> msg_ids_obtained = new ArrayList<>();
                ArrayList<String> msg_obtained = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jsonObject = null, data = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        data = jsonObject.getJSONObject("data");

                        msg_obtained.add(jsonObject.toString());
                        messagesDatabasedbHelper.insertRowSent(jsonObject.getInt("msg_id"), data.getString("s"), data.getString("d"),
                                data.getString("t_f"), data.getString("t_t"), data.getString("v"),
                                jsonObject.getString("msg_type"), data.getString("msg_optional"), jsonObject.getString("date_sent_on"));
                    }
                    if (jsonObject != null) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                        editor.putString("NUConnect_last_sent_message", String.valueOf(jsonObject.getInt("msg_id")));
                        editor.commit();
                        Log.i("BodyFragment_lastMsg", jsonObject.getInt("msg_id") + "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return msg_obtained;
            }
            else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            super.onPostExecute(result);

            if(result!=null)
                chat_extraLecture_arrayAdapter.addAll(result);
//            loadItems_fromdatabase(result);
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
