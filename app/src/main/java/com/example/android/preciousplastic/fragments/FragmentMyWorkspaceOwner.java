package com.example.android.preciousplastic.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ViewUtils;
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

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.PointsType;
import com.example.android.preciousplastic.db.Workspace;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.utils.PPSession;
import com.example.android.preciousplastic.utils.ViewTools;

import static com.example.android.preciousplastic.utils.ViewTools.isTextViewNull;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMyWorkspaceOwner.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMyWorkspaceOwner#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMyWorkspaceOwner extends BaseFragment implements View.OnClickListener {

    private final String TAG = "FragmentMWOwner";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button mEdit = null;
    private Button mUploadItem = null;
    private TextView mTitle = null;
    private TextView mDescription = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentMyWorkspaceOwner() {
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
    public static FragmentMyWorkspaceOwner newInstance(String param1, String param2) {
        FragmentMyWorkspaceOwner fragment = new FragmentMyWorkspaceOwner();
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
        View view = inflater.inflate(R.layout.fragment_my_workspace_owner, container, false);

        mEdit = (Button) view.findViewById(R.id.mw_btn_edit);
        mUploadItem = (Button) view.findViewById(R.id.mw_btn_upload);
        mTitle = (TextView) view.findViewById(R.id.mw_tv_workspace_title);
        mDescription = (TextView) view.findViewById(R.id.mw_tv_workspace_description);

        mUploadItem.setOnClickListener(this);
        mEdit.setOnClickListener(this);

        return view;
    }

    public void onStart() {
        super.onStart();

        final User currentUser = PPSession.getCurrentUser();
        final Workspace workspace = currentUser.getWorkspace();

        final String title = workspace.getTitle();
        final String description = workspace.getDescription();

        if (title == null || title.equals("") || description == null || description.equals("")) {
            mTitle.setVisibility(View.INVISIBLE);
            mUploadItem.setVisibility(View.INVISIBLE);
        } else {
            mTitle.setText(workspace.getTitle());
            mDescription.setText(workspace.getDescription());

            mTitle.setVisibility(View.VISIBLE);
            mUploadItem.setVisibility(View.VISIBLE);
        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Shows the input message to the user.
     *
     * @param msg message to show.
     */
    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Switches fragment to the upload item fragment.
     *
     * @param view
     */
    private void onUploadClick(View view) {
        PPSession.getHomeActivity().switchFragment(FragmentUploadItem.class);
    }

    /**
     * Switches fragment to the edit workspace fragment.
     *
     * @param view
     */
    private void onEditClick(View view) {
        PPSession.getHomeActivity().switchFragment(FragmentEditMyWorkspace.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.mw_btn_upload):
                onUploadClick(view);
                break;
            case (R.id.mw_btn_edit):
                onEditClick(view);
                break;
            default:
                break;
        }
    }
}
