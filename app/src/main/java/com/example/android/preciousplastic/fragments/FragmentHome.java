package com.example.android.preciousplastic.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.utils.PPSession;

public class FragmentHome extends BaseFragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageButton bazaarBtn;
    private ImageButton tradeBtn;
    private ImageButton profileBtn;
    private ImageButton mapBtn;

    private TextView mUserName;
    private TextView mTitle;
    private TextView mEmail = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentHome()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(String param1, String param2)
    {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private void SetupHubButtonListeners() {

        if (bazaarBtn == null || mapBtn == null || profileBtn == null || tradeBtn == null) {
            return;
        }
        //Setup btns backgrounds
        int backgroundAlphaVal = 255; //Max 255
        bazaarBtn.setBackgroundColor(Color.WHITE);
        bazaarBtn.getBackground().setAlpha(backgroundAlphaVal);
        mapBtn.setBackgroundColor(Color.WHITE);
        mapBtn.getBackground().setAlpha(backgroundAlphaVal);
        profileBtn.setBackgroundColor(Color.WHITE);
        profileBtn.getBackground().setAlpha(backgroundAlphaVal);
        tradeBtn.setBackgroundColor(Color.WHITE);
        tradeBtn.getBackground().setAlpha(backgroundAlphaVal);

        // Set up the buttons' listeners
        bazaarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PPSession.getHomeActivity().switchFragment(FragmentBazaar.class);
            }
        });
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PPSession.getHomeActivity().switchFragment(FragmentMap.class);
            }
        });
        tradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PPSession.getHomeActivity().switchFragment(FragmentPerformTrade.class);
            }
        });
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PPSession.getHomeActivity().switchFragment(FragmentProfile.class);
            }
        });
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
            mUserName = (TextView) view.findViewById(R.id.hub_tv_nickname);
        mTitle = (TextView) view.findViewById(R.id.hub_tv_title);
        bazaarBtn = (ImageButton) view.findViewById(R.id.hub_ib_bazaar);
        mapBtn = (ImageButton) view.findViewById(R.id.hub_ib_map);
        profileBtn = (ImageButton) view.findViewById(R.id.hub_ib_profile);
        tradeBtn = (ImageButton) view.findViewById(R.id.hub_ib_recycle);

        mUserName.setText(PPSession.getNickname());
        mTitle.setText(PPSession.getCurrentUser().getRank().getTitle());

        SetupHubButtonListeners();
        return view;
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
}
