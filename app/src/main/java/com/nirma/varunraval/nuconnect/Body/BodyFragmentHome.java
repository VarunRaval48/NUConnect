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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BodyFragmentHome.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BodyFragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BodyFragmentHome extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Chat_ExtraLecture_ArrayAdapter arrayAdapter;
    private OnFragmentInteractionListener mListener;

    ListView listView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BodyFragmentHome.
     */
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

        Log.i("BodyFragmentHome", "ArrayAdapter NULL "+(arrayAdapter==null));
        Log.i("BodyFragmentHome", "ListView NULL "+(listView==null));
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
    }

    private void loadItems(Activity activity){
        String data="";
        int c;
        try {
            FileInputStream fin = activity.openFileInput("NUConnect_chats_extralecture");
            while((c=fin.read())!=-1){
                data += String.valueOf((char)c);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("BodyFragmentHome", data);

        String temp[] = data.split(",,");
        ArrayList<String> extra_lecture_list = new ArrayList<>(Arrays.asList(temp));

        arrayAdapter.addAll(extra_lecture_list);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
