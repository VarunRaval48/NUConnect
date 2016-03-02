package com.nirma.varunraval.nuconnect.body;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nirma.varunraval.nuconnect.database.MessagesDatabasedbHelper;
import com.nirma.varunraval.nuconnect.R;
import com.nirma.varunraval.nuconnect.SendIDToServer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BodyFragmentHome extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static Chat_ExtraLecture_ArrayAdapter arrayAdapter;
    private OnFragmentInteractionListener mListener;
    public static boolean fragmentAttached = false;

    SwipeRefreshLayout swipeRefreshLayout;
    MessagesDatabasedbHelper messagesDatabasedbHelper;

    Context context;

    static ListView listView;

    // TODO: Rename and change types and number of parameters
    public static BodyFragmentHome newInstance(String param1, String param2) {
        BodyFragmentHome fragment = new BodyFragmentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BodyFragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = getActivity().getApplicationContext();

        messagesDatabasedbHelper = new MessagesDatabasedbHelper(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_body_home, container, false);

//        TextView textView = (TextView)view.findViewById(R.id.default_message);
//        textView.setText("You have no New Notifications");

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh_home);

        listView = (ListView)view.findViewById(R.id.listView_notifications_extralecture);

        listView.setAdapter(arrayAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllMessages();
            }
        });

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        if(arrayAdapter.getCount()==0){
            loadItems_from_database(null);
        }

    }

    void loadItems_from_database(ArrayList newAdded){
        Log.i("LoadItensFromData", "Loading");

        new AsyncTask(){

            @Override
            protected ArrayList doInBackground(Object[] params) {
                return messagesDatabasedbHelper.getRowsAll((ArrayList) params[0]);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                arrayAdapter.addAll((ArrayList)o);
            }
        }.execute(newAdded);
    }

    public void getAllMessages(){

        int last_got_message;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        last_got_message = Integer.parseInt(sharedPreferences.getString("NUConnect_last_got_message", "0"));
        Log.i("update_sent", last_got_message+"");
        new GetAllMessages().execute(last_got_message);
    }

    class GetAllMessages extends AsyncTask<Integer, Void, ArrayList>{

        GetAllMessages(){

        }

        @Override
        protected ArrayList doInBackground(Integer... params) {

            String result="";
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String email = sharedPreferences.getString("NUConnect_email", null);

            List<NameValuePair> list = new ArrayList();
            list.add(new BasicNameValuePair("last_got_message", String.valueOf(params[0])));
            list.add(new BasicNameValuePair("email", email));

            String url = getResources().getString(R.string.server_url)+"getAllMessages.php";
            try {
                SendIDToServer sendIDToServer = new SendIDToServer(url, list, context);
                result = sendIDToServer.sendToken();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Log.i("BodyFragmentHomeMessage", result);

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
                        messagesDatabasedbHelper.insertRowAll(jsonObject.getInt("msg_id"), jsonObject.getString("from_id"), jsonObject.getString("from_name"),
                                data.getString("s"), data.getString("d"), data.getString("t_f"), data.getString("t_t"), data.getString("v"),
                                jsonObject.getString("msg_type"), data.getString("msg_optional"), jsonObject.getString("date_sent_on"));
                    }
                    if (jsonObject != null) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                        editor.putString("NUConnect_last_got_message", String.valueOf(jsonObject.getInt("msg_id")));
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
                arrayAdapter.addAll(result);
//            loadItems_fromdatabase(result);
            swipeRefreshLayout.setRefreshing(false);

        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    //TODO Remember to enable before use
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        arrayAdapter = new Chat_ExtraLecture_ArrayAdapter(activity.getApplicationContext(), "Home");

//        if(arrayAdapter.getCount()==0){
//            loadItems(activity);
//        }
//
//        fragmentAttached = true;
    }

    public static void move_at_last(){
        listView.setSelection(0);
    }

    private void loadItems(Activity activity){
        String data="";
        int c;
        try {
            File f = new File(activity.getFilesDir()+"/"+"NUConnect_chats_extralecture");
            if(f.exists()){
                FileInputStream fin = activity.openFileInput("NUConnect_chats_extralecture");
                while((c=fin.read())!=-1){
                    data += String.valueOf((char)c);
                }
                Log.i("BodyFragmentHome", data);

                String temp[] = data.split(",,");
                ArrayList<String> extra_lecture_list = new ArrayList<>(Arrays.asList(temp));

                arrayAdapter.addAll(extra_lecture_list);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        messagesDatabasedbHelper.close();
//        fragmentAttached = false;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
