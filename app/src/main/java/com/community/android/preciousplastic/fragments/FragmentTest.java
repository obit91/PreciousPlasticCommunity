package com.community.android.preciousplastic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.community.android.preciousplastic.R;
import com.community.android.preciousplastic.db.Workspace;
import com.community.android.preciousplastic.db.entities.User;
import com.community.android.preciousplastic.imgur.ImgurBazarItem;
import com.community.android.preciousplastic.utils.PPSession;

import java.util.HashMap;
import java.util.Set;


public class FragmentTest extends BaseFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String TAG = "TESTING";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    private Button mTestButton1 = null;
    private Button mTestButton2 = null;
    private Button mTestButton3 = null;
    private ImageView mTestImage = null;

    public FragmentTest() {
        // Required empty public constructor
    }

    public static FragmentTest newInstance(String param1) {
        FragmentTest fragment = new FragmentTest();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        // set button listener
        mTestButton1 = (Button) view.findViewById(R.id.test_btn_1);
        mTestButton1.setOnClickListener(this);
        mTestButton2 = (Button) view.findViewById(R.id.test_btn_2);
        mTestButton2.setOnClickListener(this);
        mTestButton3 = (Button) view.findViewById(R.id.test_btn_3);
        mTestButton3.setOnClickListener(this);

        mTestImage = (ImageView) view.findViewById(R.id.test_iv_upload_img);

        return view;
    }

//     TODO: Rename method, update argument and hook method into UI event
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

    public interface OnFragmentInteractionListener {
//         TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.test_btn_1):
                removeAllItems();
                break;
            case(R.id.test_btn_2):
                break;
            case(R.id.test_btn_3):
                break;
        }
    }

    private void removeAllItems() {
        final User currentUser = PPSession.getCurrentUser();
        currentUser.removeAllItemsFromBazar();
    }
}
