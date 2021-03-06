package com.community.android.preciousplastic.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.community.android.preciousplastic.R;
import com.community.android.preciousplastic.db.UserPoints;
import com.community.android.preciousplastic.db.UserRank;
import com.community.android.preciousplastic.db.entities.User;
import com.community.android.preciousplastic.utils.PPSession;


public class FragmentProfile extends BaseFragment implements View.OnClickListener {
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
    private TextView mTitle = null;
    private TextView currentXP = null;
    private TextView nextRankXP = null;
    private ProgressBar progressBar = null;
    private TextView percentDone = null;

    private TextView mNickname = null;

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

        attachFields(view);

        updateGUI(view);

        return view;
    }

    /**
     * Attaches the score bins on the screen to the corresponding fields.
     * @param view fragment view.
     */
    private void attachFields(View view) {
        bin1 = view.findViewById(R.id.profile_tv_bin1);
        bin2 = view.findViewById(R.id.profile_tv_bin2);
        bin3 = view.findViewById(R.id.profile_tv_bin3);
        bin4 = view.findViewById(R.id.profile_tv_bin4);
        bin5 = view.findViewById(R.id.profile_tv_bin5);
        bin6 = view.findViewById(R.id.profile_tv_bin6);
        bin7 = view.findViewById(R.id.profile_tv_bin7);
        binTotal = view.findViewById(R.id.profile_tv_total);
        mTitle = view.findViewById(R.id.profile_tv_title);
        currentXP = view.findViewById(R.id.profile_tv_currentXP);
        nextRankXP = view.findViewById(R.id.profile_tv_expToNextLvl);
        progressBar = view.findViewById(R.id.profile_progressBar_plasticExp);
        percentDone = view.findViewById(R.id.profile_tv_percent);
        mNickname = view.findViewById(R.id.profile_tv_nickname);
    }

    @Override
    public void updateGUI(View view) {
        User currentUser = PPSession.getCurrentUser();
        UserPoints points = currentUser.getPoints();
        bin1.setText(points.getType1AsString());
        bin2.setText(points.getType2AsString());
        bin3.setText(points.getType3AsString());
        bin4.setText(points.getType4AsString());
        bin5.setText(points.getType5AsString());
        bin6.setText(points.getType6AsString());
        bin7.setText(points.getType7AsString());
        binTotal.setText(points.getTotalPurchasePointsAsString());

        UserRank rank = currentUser.getRank();
        String title = rank.getTitle();
        mTitle.setText(title);
        mNickname.setText(PPSession.getNickname());

        currentXP.setText(String.valueOf(points.getPointsSum()));
        nextRankXP.setText(String.valueOf(rank.getRequiredExp()));

        int progress = getProgress(points, rank);
        progressBar.getProgressDrawable().setColorFilter(
                Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgress(progress);
        String percentage = (progress < 100 ? progress : "100") + "%";
        percentDone.setText(percentage);
    }

    private int getProgress(UserPoints points, UserRank rank) {
        int from;
        int to;
        UserRank prevRank = rank.getPrevRank();
        if (rank == prevRank) {
            from = 0;
        } else {
            from = prevRank.getRequiredExp();
        }
        to = rank.getRequiredExp();
        return (int)((points.getPointsSum() - from) / (to - from) * 100);
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

    @Override
    public boolean onBackPressed() {
        return false;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }
}
