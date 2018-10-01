package com.community.android.preciousplastic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.community.android.preciousplastic.R;
import com.community.android.preciousplastic.db.entities.User;
import com.community.android.preciousplastic.utils.EventNotifier;
import com.community.android.preciousplastic.db.PointsType;
import com.community.android.preciousplastic.db.repositories.UserRepository;
import com.community.android.preciousplastic.utils.PPSession;
import com.community.android.preciousplastic.utils.ViewTools;
import com.google.android.gms.common.util.ArrayUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.community.android.preciousplastic.db.PointsType.TYPE_TOTAL;
import static com.community.android.preciousplastic.utils.ViewTools.isTextViewNull;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentPerformRecycle.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentPerformRecycle#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPerformRecycle extends BaseFragment implements View.OnClickListener
{
    private final String TAG = "FragmentMWOwner";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Spinner mTypeSpinner = null;
    private EditText mNicknameEditView = null;
    private EditText mWeightEditView = null;
    private TextView mScoreTextView = null;
    private Button mConfirmButton = null;

    private PointsType mType = null;
    private Double mWeight = null;
    private Double mScore = null;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentPerformRecycle()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMyWorkspaceOwner.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPerformRecycle newInstance(String param1, String param2)
    {
        FragmentPerformRecycle fragment = new FragmentPerformRecycle();
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
        View view = inflater.inflate(R.layout.fragment_perform_recycle, container, false);

        // update spinner
        mTypeSpinner = view.findViewById(R.id.mw_spinner_type);
        final List<PointsType> pointsTypes = new LinkedList<>(Arrays.asList(PointsType.values()));
        pointsTypes.remove(TYPE_TOTAL);
        ArrayAdapter<PointsType> pointsTypeArrayAdapter = new ArrayAdapter<PointsType>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                pointsTypes);
        mTypeSpinner.setAdapter(pointsTypeArrayAdapter);

        mConfirmButton = view.findViewById(R.id.trade_btn_confirm);

        // set TextView fields
        mNicknameEditView = view.findViewById(R.id.trade_et_nickname);
        mWeightEditView = view.findViewById(R.id.trade_et_points);
        mScoreTextView = view.findViewById(R.id.trade_tv_score);

        return view;
    }

    public void onStart() {
        super.onStart();

        // spinner listener
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeUpdated();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // button listener
        mConfirmButton.setOnClickListener(this);

        // weight listener
        mWeightEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                weightUpdated(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    /**
     * Upon clicking confirm, the input would be updated in the db.
     * @param view button view.
     */
    private void onConfirmClick(View view) {
        UserRepository userRepository = new UserRepository(getContext());
        User currentUser = PPSession.getCurrentUser();

        if (isTextViewNull(mNicknameEditView) || isTextViewNull(mWeightEditView)) {
            showToast("Please enter all required fields");
            return;
        }

//        if (mNicknameEditView.getText().toString().equals(currentUser.getNickname())) {
//            showToast("You can not give points to yourself.");
//            return;
//        }

        if (mScore <= 0) {
            Toast.makeText(getContext(), "Please fill a weight greater than zero.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!currentUser.getWorkspace().isShredderMachine()) {
            Toast.makeText(getContext(), "A shredder machine is a required for starting the recycling process.", Toast.LENGTH_SHORT).show();
            return;
        }

        String nickname = mNicknameEditView.getText().toString();
        WriteUserPoints eventNotifier = new WriteUserPoints();
        userRepository.updateUserPoints(nickname, mType, mScore, true, eventNotifier);
    }

    /**
     * On successful trade switch to success screen, else show a failure message.
     */
    private class WriteUserPoints extends EventNotifier {
        @Override
        public void onResponse(Object dataSnapshotObj){
            boolean success = (boolean)dataSnapshotObj;
            if (success) {
                PPSession.getHomeActivity().switchFragment(FragmentCompleteRecycle.class);
            } else {
                Toast.makeText(getActivity(), "Nickname does not exist.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Gets called upon updating the current plastic weight, parses the weight input.
     * @param newValue new weight inputted.
     */
    private void weightUpdated(String newValue) {
        if (ViewTools.isTextViewNull(mWeightEditView)) {
            resetScore();
            return;
        }

        mWeight = Double.parseDouble(newValue);

        if (mWeight <= 0) {
            resetScore();
            showToast("Weight must be greater than zero.");
            return;
        }
        calculateScore();
    }

    /**
     * Gets called upon choosing a type from the spinner and parses the item.
     */
    private void typeUpdated() {
        mType = (PointsType)mTypeSpinner.getSelectedItem();
        calculateScore();
    }

    /**
     * Resets the current plastic score and updates the UI accordingly.
     */
    private void resetScore() {
        mScore = 0.0;
        mScoreTextView.setText(String.valueOf(mScore));
    }

    /**
     * Calculates the current plastic score, and updates the UI accordingly.
     */
    private void calculateScore() {
        Log.i(TAG, "calculateScore: <weight, type>: <" + mWeight + "," + mType + ">");
        if (mWeight == null || mType == null) {
            return;
        }
        mScore = mWeight * mType.getPointValue();
        mScoreTextView.setText(String.valueOf(mScore));
    }

    /**
     * Shows the input message to the user.
     * @param msg message to show.
     */
    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.trade_btn_confirm):
                onConfirmClick(view);
                break;
            default:
                break;
        }
    }
}
