package com.nirma.varunraval.nuconnect;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectReceipentFragment.OnReceipentFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectReceipentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectReceipentFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1type;
    private String mParam2;

    static View view;

    Context context;

    public static ArrayList<Integer> receipentIndividualList = new ArrayList<>();
    public static ArrayList<Integer> receipentGroupList = new ArrayList<>();
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

            receipentIndividualList.add(R.id.autoCompleteTextViewIndv1);
            Button addReceipentIndButton = (Button)view.findViewById(R.id.buttonAddIndv);

            addReceipentIndButton.setOnClickListener(this);

        }//TODO Correct layout of group receipent (row size)
        else if(mParam1type.equals("Group")){
            view = inflater.inflate(R.layout.fragment_select_receipent_group, container, false);

            Spinner spinnerInformWhoTo = (Spinner)view.findViewById(R.id.spinnerInformWhoTo);
            ArrayAdapter<CharSequence> arrayAdapterWhoTo = ArrayAdapter.createFromResource(context, R.array.inform_who_to, R.layout.spinner_item);
            arrayAdapterWhoTo.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinnerInformWhoTo.setAdapter(arrayAdapterWhoTo);

            Spinner spinnerInformDivision = (Spinner)view.findViewById(R.id.spinnerInformDivision);
            ArrayAdapter<CharSequence> arrayAdapterDivision = ArrayAdapter.createFromResource(context, R.array.inform_division, R.layout.spinner_item);
            arrayAdapterDivision.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinnerInformDivision.setAdapter(arrayAdapterDivision);

            receipentGroupList.add(R.id.autoCompleteTextViewGroup);

            Button addReceipentGrpButton = (Button)view.findViewById(R.id.buttonAddGroup);

            addReceipentGrpButton.setOnClickListener(this);
        }



        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onReceipentFragmentInteraction(uri);
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

        receipentIndividualList.removeAll(receipentIndividualList);
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
        public void onReceipentFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {

        Log.i("onButtonPress", ""+v.getId());

        if(v.getId() == R.id.buttonAddIndv && canAddIndividual()){

            Log.i("onButtonPress", "In "+v.getId());

//            Resources res = context.getResources();
//            XmlPullParser parser = res.getXml()
//            AttributeSet attributeSet = res.getXml(R.);

            TableLayout tableLayout = (TableLayout)view.findViewById(R.id.tableLayoutIndividual);
//            TableRow newRow = new TableRow(context);
//            TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//            newRow.setLayoutParams(tableLayoutParams);
//
//            TableRow.LayoutParams rowLayoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//            AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(context);
//            autoCompleteTextView.setLayoutParams(rowLayoutParams);
//            autoCompleteTextView.setTextColor(getResources().getColor(R.color.wallet_holo_blue_light));
//            autoCompleteTextView.setId();

//            newRow.addView(autoCompleteTextView);
//
//            tableLayout.addView(newRow);

            LayoutInflater inflater = LayoutInflater.from(context);
            View rowView = inflater.inflate(R.layout.select_receipent_row, tableLayout);

            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)rowView.findViewById(R.id.autoCompleteViewIndReceipent);

            //TODO Add id randomly checking if view at there is present or not, and add all of them to database to retrieve
            //noinspection ResourceType
            while(view.findViewById(countRecInd)!=null){
                countRecInd++;
            }
            //noinspection ResourceType
            autoCompleteTextView.setId(countRecInd);

            receipentIndividualList.add(countRecInd);
        }
        else if(v.getId() == R.id.buttonAddGroup && canAddIndividual()){

            Log.i("onButtonPress", "In "+v.getId());

            TableLayout tableLayout = (TableLayout)view.findViewById(R.id.tableLayoutGroup);

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View rowView = layoutInflater.inflate(R.layout.select_receipent_row, tableLayout);

            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)rowView.findViewById(R.id.autoCompleteViewIndReceipent);

            while(view.findViewById(countRecGrp)!=null){
                countRecGrp++;
            }
            autoCompleteTextView.setId(countRecGrp);

            receipentGroupList.add(countRecGrp);
        }
    }

    boolean canAddIndividual(){

        return true;
    }


}
