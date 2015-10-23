package com.nirma.varunraval.nuconnect.login_slider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.nirma.varunraval.nuconnect.R;
import com.nirma.varunraval.nuconnect.login.NextFragment;
import com.nirma.varunraval.nuconnect.login.RetryLoginFragment;

public class Login_Fragment extends Fragment {

    public static String login_type;
    Spinner spinner;
    FragmentManager fragmentManager;
    static Activity main_slider_activity;

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentLogINInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static Login_Fragment newInstance(String param1, String param2) {
        Login_Fragment fragment = new Login_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Login_Fragment() {
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
        View view = inflater.inflate(R.layout.fragment_login_main_slider, container, false);

        Log.i("View", "After setting view");


        Log.i("LoginFragment", "initialized spinner");
        spinner = (Spinner)view.findViewById(R.id.spinner);

        Button nextTempButton = (Button)view.findViewById(R.id.buttonTempNext);
        nextTempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed("temp_next");
            }
        });

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(main_slider_activity.getApplicationContext(), R.array.login_type, R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        Log.i("LoginFragment", "Setting spinner");
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                login_type = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ImageButton pick_next = (ImageButton)view.findViewById(R.id.button_next);
        pick_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFragmentLogINInteraction("pickUser");
            }
        });

//        mListener.onFragmentLogINInteraction("inflate_next");
//        inflateNext();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String name) {
        if (mListener != null) {
            mListener.onFragmentLogINInteraction(name);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentLogINInteractionListener) activity;

            main_slider_activity = activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentLogINInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentLogINInteraction(String name);
    }

    protected void inflateNext(){
        Fragment nextFragment = new NextFragment();
        fragmentManager = getChildFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.frameLaoyoutSpin, nextFragment).commit();
        fragmentManager.executePendingTransactions();
    }


}
