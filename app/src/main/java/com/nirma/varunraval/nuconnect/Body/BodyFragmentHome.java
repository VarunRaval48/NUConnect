package com.nirma.varunraval.nuconnect.body;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nirma.varunraval.nuconnect.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class BodyFragmentHome extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    static Chat_ExtraLecture_ArrayAdapter arrayAdapter;
    private OnFragmentInteractionListener mListener;
    public static boolean fragmentAttached = false;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_body_home, container, false);

//        TextView textView = (TextView)view.findViewById(R.id.default_message);
//        textView.setText("You have no New Notifications");

        listView = (ListView)view.findViewById(R.id.listView_notifications_extralecture);

        listView.setAdapter(arrayAdapter);

        return view;
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

        arrayAdapter = new Chat_ExtraLecture_ArrayAdapter(activity.getApplicationContext());

        if(arrayAdapter.getCount()==0){
            loadItems(activity);
        }

        fragmentAttached = true;
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
        fragmentAttached = false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
