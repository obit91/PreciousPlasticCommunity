package com.example.android.preciousplastic.fragments;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.utils.PPSession;

public class FragmentCompleteRecycle extends BaseFragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView mCompleteTitle = null;
    private TextView mDescription = null;
    private Button mAgainButton = null;
    private Button mBackHome = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentCompleteRecycle() {
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
    public static FragmentCompleteRecycle newInstance(String param1, String param2) {
        FragmentCompleteRecycle fragment = new FragmentCompleteRecycle();
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

        mCompleteTitle.setText("Congratulations!");
        updateDescription();
    }

    /**
     * Creates and updates the text for the description field.
     */
    private void updateDescription() {
        StringBuilder sb = new StringBuilder();

        sb.append("Hooray!");
        sb.append("\n");
        sb.append("\n");
        sb.append("Every piece recycled brings us closer to a brighter, healthier and safer world.");
        sb.append("\n");
        sb.append("\n");
        sb.append("Thank you for choosing a better future for our planet and keep on recycling.");
        sb.append("\n");
        sb.append("\n");
        sb.append("\n");
        sb.append("\t\t\t\t\t\t").append("~KOY!");

        mDescription.setText(sb.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_complete_trade, container, false);

        mCompleteTitle = view.findViewById(R.id.trade_complete_tv_title);
        mDescription = view.findViewById(R.id.trade_complete_tv_description);
        mAgainButton = view.findViewById(R.id.trade_complete_btn_again);
        mAgainButton.setOnClickListener(this);
        mBackHome = view.findViewById(R.id.trade_complete_btn_home);
        mBackHome.setOnClickListener(this);

        return view;
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

    /**
     * Switches back to the trade screen.
     */
    private void performAnother() {
        PPSession.getHomeActivity().switchFragment(FragmentPerformRecycle.class);
    }

    /**
     * Switches back to the home screen.
     */
    private void backHome() {
        PPSession.getHomeActivity().switchFragment(FragmentHome.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trade_complete_btn_again:
                performAnother();
                break;
            case R.id.trade_complete_btn_home:
                backHome();
                break;
        }
    }
}
