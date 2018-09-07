package com.example.android.preciousplastic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.UserPoints;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.utils.PPSession;


public class FragmentProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView bin1 = null;
    private TextView bin2 = null;
    private TextView bin3 = null;
    private TextView bin4 = null;
    private TextView bin5 = null;
    private TextView bin6 = null;
    private TextView bin7 = null;
    private TextView binTotal = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentProfile newInstance(String param1, String param2) {
        FragmentProfile fragment = new FragmentProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        attachBins(view);
        updateBins(view);

        return view;
    }

    /**
     * Attaches the score bins on the screen to the corresponding fields.
     * @param view fragment view.
     */
    private void attachBins(View view) {
        bin1 = (TextView)view.findViewById(R.id.profile_tv_bin1);
        bin2 = (TextView)view.findViewById(R.id.profile_tv_bin2);
        bin3 = (TextView)view.findViewById(R.id.profile_tv_bin3);
        bin4 = (TextView)view.findViewById(R.id.profile_tv_bin4);
        bin5 = (TextView)view.findViewById(R.id.profile_tv_bin5);
        bin6 = (TextView)view.findViewById(R.id.profile_tv_bin6);
        bin7 = (TextView)view.findViewById(R.id.profile_tv_bin7);
        binTotal = (TextView)view.findViewById(R.id.profile_tv_total);
    }

    private void updateBins(View view) {
        User currentUser = PPSession.getCurrentUser();
        UserPoints points = currentUser.getPoints();
            bin1.setText(points.getType1AsString());
        bin2.setText(points.getType2AsString());
        bin3.setText(points.getType3AsString());
        bin4.setText(points.getType4AsString());
        bin5.setText(points.getType5AsString());
        bin6.setText(points.getType6AsString());
        bin7.setText(points.getType7AsString());
        binTotal.setText(points.getTotalPointsAsString());
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        }
        else
        {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
