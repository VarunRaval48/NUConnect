package com.nirma.varunraval.nuconnect.body;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nirma.varunraval.nuconnect.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InformExtraLectureFragment.OnFragmentInformExtralectureInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InformExtraLectureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InformExtraLectureFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInformExtralectureInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InformExtraLectureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InformExtraLectureFragment newInstance(String param1, String param2) {
        InformExtraLectureFragment fragment = new InformExtraLectureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InformExtraLectureFragment() {
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
        View view = inflater.inflate(R.layout.fragment_inform_extra_lecture, container, false);

        Button informExtraLectureButton = (Button)view.findViewById(R.id.buttonExtralecture);

        informExtraLectureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            int msg_type = SelectReceipentFragment.msg_type;
            if(msg_type==0){
                mListener.onFragmentInformExtralectureInteraction(SelectReceipentFragment.reciepentListID[msg_type]);
            }
            else{
                Log.i("__InformExtra", SelectReceipentFragment.group_info+"");
                mListener.onFragmentInformExtralectureInteraction(SelectReceipentFragment.reciepentListID[msg_type], SelectReceipentFragment.group_info);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInformExtralectureInteractionListener) activity;
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
    public interface OnFragmentInformExtralectureInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInformExtralectureInteraction(ArrayList<Integer> receipentListID);
        public void onFragmentInformExtralectureInteraction(ArrayList<Integer> receipentListID, ArrayList<String> group_info);
    }

}
