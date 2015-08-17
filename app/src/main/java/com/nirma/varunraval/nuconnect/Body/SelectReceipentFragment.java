package com.nirma.varunraval.nuconnect.body;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.nirma.varunraval.nuconnect.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectReceipentFragment.OnReceipentFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectReceipentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectReceipentFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1type;
    private String mParam2;

    static View view;

    Context context;

    public static ArrayList<Integer>[] reciepentListID = new ArrayList[2];
    public static int msg_type=0; // 0 for Indivisual 1 for group
    static int countRecInd=1, countRecGrp=101;

    private OnReceipentFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1type Parameter 1.
     * @return A new instance of fragment SelectReceipentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectReceipentFragment newInstance(String param1type) {
        SelectReceipentFragment fragment = new SelectReceipentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1type);
        fragment.setArguments(args);
        return fragment;
    }

    public SelectReceipentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1type = getArguments().getString(ARG_PARAM1);
        }

        for(int i=0; i<2; i++){
            reciepentListID[i] = new ArrayList<>();
        }

        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_select_receipent_individual, container, false);;

        Log.i("params:", mParam1type);
        if(mParam1type.equals("Individual")) {
            view = inflater.inflate(R.layout.fragment_select_receipent_individual, container, false);

            reciepentListID[0].add(R.id.autoCompleteTextViewIndv1);
            msg_type = 0;
            Button addReceipentIndButton = (Button)view.findViewById(R.id.buttonAddIndv);

            addReceipentIndButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonPressed("view", "AutoCompleteTextViewInd");
                }
            });

        }//TODO Correct layout of group receipent (row size)
        else if(mParam1type.equals("Group")){
            view = inflater.inflate(R.layout.fragment_select_receipent_group, container, false);

            Spinner spinnerInformWhoToYear = (Spinner)view.findViewById(R.id.spinnerInformWhoToYear);
            ArrayAdapter<CharSequence> arrayAdapterWhoToYear = ArrayAdapter.createFromResource(context, R.array.inform_who_to_year, R.layout.spinner_item);
            arrayAdapterWhoToYear.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinnerInformWhoToYear.setAdapter(arrayAdapterWhoToYear);

            Spinner spinnerInformWhoTo = (Spinner)view.findViewById(R.id.spinnerInformWhoTo);
            ArrayAdapter<CharSequence> arrayAdapterWhoTo = ArrayAdapter.createFromResource(context, R.array.inform_who_to, R.layout.spinner_item);
            arrayAdapterWhoTo.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinnerInformWhoTo.setAdapter(arrayAdapterWhoTo);

            Spinner spinnerInformDivision = (Spinner)view.findViewById(R.id.spinnerInformDivision);
            ArrayAdapter<CharSequence> arrayAdapterDivision = ArrayAdapter.createFromResource(context, R.array.inform_division, R.layout.spinner_item);
            arrayAdapterDivision.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinnerInformDivision.setAdapter(arrayAdapterDivision);

            reciepentListID[1].add(R.id.autoCompleteTextViewGroup);
            msg_type = 1;

            Button addReceipentGrpButton = (Button)view.findViewById(R.id.buttonAddGroup);

            addReceipentGrpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonPressed("view", "AutoCompleteTextViewGrp");
                }
            });
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String type, String fragmentType) {
        if (mListener != null) {
            mListener.onReceipentFragmentInteraction(type, fragmentType);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnReceipentFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        reciepentListID[0].removeAll(reciepentListID[0]);
        reciepentListID[1].removeAll(reciepentListID[1]);
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
    public interface OnReceipentFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onReceipentFragmentInteraction(String type, String fragmentType);
    }

    static boolean canAddIndividual(){

        return true;
    }


}
