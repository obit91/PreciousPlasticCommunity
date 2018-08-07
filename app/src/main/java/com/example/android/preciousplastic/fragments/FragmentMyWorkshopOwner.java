package com.example.android.preciousplastic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.PointsType;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.utils.PPSession;

import static com.example.android.preciousplastic.utils.ViewTools.isTextViewNull;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMyWorkshopOwner.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMyWorkshopOwner#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMyWorkshopOwner extends Fragment implements View.OnClickListener
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Spinner mTypeSpinner = null;
    private TextView mNicknameTextView = null;
    private TextView mScoreTextView = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentMyWorkshopOwner()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMyWorkshopOwner.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMyWorkshopOwner newInstance(String param1, String param2)
    {
        FragmentMyWorkshopOwner fragment = new FragmentMyWorkshopOwner();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_my_workshop_owner, container, false);

        // update spinner
        mTypeSpinner = (Spinner) view.findViewById(R.id.mw_spinner_type);
        ArrayAdapter<PointsType> pointsTypeArrayAdapter = new ArrayAdapter<PointsType>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                PointsType.values());
        mTypeSpinner.setAdapter(pointsTypeArrayAdapter);

        // set button listener
        Button mConfirmButton = (Button) view.findViewById(R.id.mw_btn_confirm);
        mConfirmButton.setOnClickListener(this);

        // set TextView fields
        mNicknameTextView = (TextView)view.findViewById(R.id.mw_et_nickname);
        mScoreTextView = (TextView)view.findViewById(R.id.mw_et_score);

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
     * Upon clicking confirm, the input would be updated in the db.
     * @param view button view.
     */
    private void onConfirmClick(View view) {
        UserRepository userRepository = new UserRepository(getContext());

        if (isTextViewNull(mNicknameTextView) || isTextViewNull(mScoreTextView)) {
            Toast.makeText(getContext(), "Please enter all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String nickname = mNicknameTextView.getText().toString();
        PointsType type = (PointsType)mTypeSpinner.getSelectedItem();
        double score = Double.parseDouble(mScoreTextView.getText().toString());

        userRepository.updateUserPoints(nickname, type, score);
        PPSession.getHomeActivity().onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.mw_btn_confirm):
                onConfirmClick(view);
                break;
        }
    }
}
