package com.example.android.preciousplastic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.EventNotifier;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.google.firebase.database.DataSnapshot;


public class FragmentTest extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String TAG = "TESTING";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    private Button mQueryButton = null;

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
        mQueryButton = (Button) view.findViewById(R.id.testing_btn_query_nicknames);
        mQueryButton.setOnClickListener(this);

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

    public interface OnFragmentInteractionListener {
//         TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.testing_btn_query_nicknames):
                queryNicknames(v);
                break;
        }
    }

    private void queryNicknames(View view) {
        UserRepository userRepository = new UserRepository(getContext());
        userRepository.isNicknameAvailable("a", new usersQueryNicknameEventNotifier());
        userRepository.isNicknameAvailable("fff", new usersQueryNicknameEventNotifier());
        int a = 5;
    }

    /**
     * Notifies true when the nickname is available.
     */
    private class usersQueryNicknameEventNotifier extends EventNotifier {
        @Override
        public void onResponse(Object dataSnapshotObj){
            DataSnapshot dataSnapshot;
            try {
                dataSnapshot = (DataSnapshot) dataSnapshotObj;
                if (dataSnapshot.getChildrenCount() == 0) {
                    System.out.println("Username is available");
                } else {
                    System.out.println("Username exists.");
                }
            } catch (ClassCastException e){
                onError(e.toString());
                return;
            }
        }
    }
}
