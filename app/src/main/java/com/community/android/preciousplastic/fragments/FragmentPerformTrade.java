package com.community.android.preciousplastic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.community.android.preciousplastic.R;
import com.community.android.preciousplastic.db.PointsType;
import com.community.android.preciousplastic.db.entities.User;
import com.community.android.preciousplastic.db.repositories.UserRepository;
import com.community.android.preciousplastic.utils.EventNotifier;
import com.community.android.preciousplastic.utils.PPSession;
import com.community.android.preciousplastic.utils.SharedResources;

import static com.community.android.preciousplastic.utils.ViewTools.isTextViewNull;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentPerformTrade.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentPerformTrade#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPerformTrade extends BaseFragment implements View.OnClickListener
{
    private final String TAG = "FragmentMWOwner";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText mNicknameEditView = null;
    private TextView mPointsTextView = null;
    private Button mConfirmButton = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentPerformTrade()
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
    public static FragmentPerformTrade newInstance(String param1, String param2)
    {
        FragmentPerformTrade fragment = new FragmentPerformTrade();
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
        View view = inflater.inflate(R.layout.fragment_perform_trade, container, false);

        mConfirmButton = view.findViewById(R.id.trade_btn_confirm);
        mNicknameEditView = view.findViewById(R.id.trade_et_nickname);
        mPointsTextView = view.findViewById(R.id.trade_et_points);

        return view;
    }

    public void onStart() {
        super.onStart();

        // button listener
        mConfirmButton.setOnClickListener(this);

        final Object bazarTrade = SharedResources.get("bazarTrade");
        if (bazarTrade != null) {
            SharedResources.remove("bazarTrade");
            String seller = (String)SharedResources.remove("name");
            Double price = (Double)SharedResources.remove("price");
            mNicknameEditView.setText(seller);
            mPointsTextView.setText(String.valueOf(price));
        }

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

        if (isTextViewNull(mNicknameEditView) || isTextViewNull(mPointsTextView)) {
            showToast("Please enter all required fields");
            return;
        }

        if (mNicknameEditView.getText().toString().equals(currentUser.getNickname())) {
            showToast("You can not trade points with yourself.");
            return;
        }

        String nickname = mNicknameEditView.getText().toString();
        WriteUserPoints eventNotifier = new WriteUserPoints();
        Double points = Double.valueOf(mPointsTextView.getText().toString());

        if (currentUser.getPoints().getTotalPurchasePoints() < points) {
            Toast.makeText(getContext(), "You don't have enough purchase to trade.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (points <= 0) {
            Toast.makeText(getContext(), "Please fill a number greater than zero.", Toast.LENGTH_SHORT).show();
            return;
        }

        userRepository.updateUserPoints(nickname, PointsType.TYPE_TOTAL, points, true, eventNotifier);
    }

    private void decrementPoints() {
        UserRepository userRepository = new UserRepository(getContext());
        RemoveUserPoints eventNotifier = new RemoveUserPoints();
        Double points = Double.valueOf(mPointsTextView.getText().toString());
        userRepository.updateUserPoints(PPSession.getNickname(), PointsType.TYPE_TOTAL, points, false, eventNotifier);
    }

    /**
     * On successful trade switch to success screen, else show a failure message.
     */
    private class WriteUserPoints extends EventNotifier {
        @Override
        public void onResponse(Object dataSnapshotObj){
            boolean success = (boolean)dataSnapshotObj;
            if (success) {
                decrementPoints();
            } else {
                Toast.makeText(getActivity(), "Nickname does not exist.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RemoveUserPoints extends EventNotifier {
        @Override
        public void onResponse(Object dataSnapshotObj){
            boolean success = (boolean)dataSnapshotObj;
            if (success) {
                PPSession.getHomeActivity().switchFragment(FragmentCompleteRecycle.class);
            } else {
                Toast.makeText(getActivity(), "Failed to decrement points.", Toast.LENGTH_SHORT).show();
            }
        }
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
