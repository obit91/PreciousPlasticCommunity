package com.community.android.preciousplastic.fragments;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.community.android.preciousplastic.R;

public class FragmentTutorial extends BaseFragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Button profileBtn = null;
    private Button tradeBtn = null;
    private Button rankBtn = null;
    private Button trackHistBtn = null;
    private Button bazaarBtn = null;
    private Button workspacesBtn = null;
    private Button mapBtn = null;
    private Button mNextBtn = null;
    private Button mBackToTutBtn = null;
    private ImageView mTutImage = null;

    public FragmentTutorial() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSettings.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTutorial newInstance(String param1, String param2) {
        FragmentTutorial fragment = new FragmentTutorial();
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
    public void onStart() {
        super.onStart();

        if (getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        attachButtons(view);
        profileBtn.setOnClickListener(this);
        mBackToTutBtn.setVisibility(View.INVISIBLE);

        return view;
    }

    private void attachButtons(View view) {
        profileBtn = (Button) view.findViewById(R.id.faq_btn_recycle_plastic_for_points);
        tradeBtn = (Button) view.findViewById(R.id.faq_btn_trade_for_items);
        rankBtn =(Button)  view.findViewById(R.id.faq_btn_rank_progression);
        trackHistBtn =(Button)  view.findViewById(R.id.faq_btn_track_recycling);
        bazaarBtn = (Button) view.findViewById(R.id.faq_btn_bazaar_search);
        workspacesBtn = (Button) view.findViewById(R.id.faq_btn_explore_workspaces);
        mapBtn = (Button) view.findViewById(R.id.faq_btn_map_long_click);
        mBackToTutBtn = (Button) view.findViewById(R.id.faq_btn_to_faq);
        mNextBtn = (Button) view.findViewById(R.id.faq_btn_next);
        mTutImage = (ImageView) view.findViewById(R.id.faq_iv_current_tutorial_image);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.faq_btn_recycle_plastic_for_points:
                break;
            case R.id.faq_btn_trade_for_items:
                break;
            case R.id.faq_btn_rank_progression:
                break;
            case R.id.faq_btn_bazaar_search:
                break;
            case R.id.faq_btn_track_recycling:
                break;
            case R.id.faq_btn_explore_workspaces:
                break;
            case R.id.faq_btn_map_long_click:
                break;
            case R.id.faq_btn_next:
                break;
            case R.id.faq_btn_to_faq:
                break;
            default:
                break;
        }
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
