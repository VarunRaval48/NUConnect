package com.nirma.varunraval.nuconnect.Body;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.nirma.varunraval.nuconnect.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BodyFragmentInform.OnFragmentInformInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BodyFragmentInform#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BodyFragmentInform extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Context context;

    private OnFragmentInformInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BodyFragmentInform.
     */
    // TODO: Rename and change types and number of parameters
    public static BodyFragmentInform newInstance(String param1, String param2) {
        BodyFragmentInform fragment = new BodyFragmentInform();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BodyFragmentInform() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_body_inform, container, false);


        //TODO maintain context on orientation change
        Spinner spinnerInformWhatTo = (Spinner)view.findViewById(R.id.spinnerInformWhatTo);
        ArrayAdapter<CharSequence> arrayAdapterWhatTo = ArrayAdapter.createFromResource(context, R.array.inform_what_to, R.layout.spinner_item);
        arrayAdapterWhatTo.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerInformWhatTo.setAdapter(arrayAdapterWhatTo);

        Spinner spinnerInformWhoToType = (Spinner)view.findViewById(R.id.spinnerInformWhoToType);
        ArrayAdapter<CharSequence> arrayAdapterWhoToType = ArrayAdapter.createFromResource(context, R.array.inform_who_to_type, R.layout.spinner_item);
        arrayAdapterWhoToType.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerInformWhoToType.setAdapter(arrayAdapterWhoToType);

//        Spinner spinnerInformDivision = (Spinner)view.findViewById(R.id.spinnerInformDivision);
//        ArrayAdapter<CharSequence> arrayAdapterDivision = ArrayAdapter.createFromResource(context, R.array.inform_division, R.layout.spinner_item);
//        arrayAdapterDivision.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//        spinnerInformDivision.setAdapter(arrayAdapterDivision);


        spinnerInformWhatTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mListener.onFragmentInformInteraction("fragment", parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerInformWhoToType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mListener.onFragmentInformInteraction("fragment", parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInformInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInformInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInformInteractionListener");
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
    public interface OnFragmentInformInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInformInteraction(String Type, String fragmentType);
    }

}
