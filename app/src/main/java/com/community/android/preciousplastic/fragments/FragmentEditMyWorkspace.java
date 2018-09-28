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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.community.android.preciousplastic.R;
import com.community.android.preciousplastic.db.PointsType;
import com.community.android.preciousplastic.db.Workspace;
import com.community.android.preciousplastic.db.entities.User;
import com.community.android.preciousplastic.db.repositories.UserRepository;
import com.community.android.preciousplastic.utils.PPSession;
import com.community.android.preciousplastic.utils.ViewTools;

import java.util.HashSet;
import java.util.Set;

import static com.community.android.preciousplastic.utils.ViewTools.isTextViewNull;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentEditMyWorkspace.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentEditMyWorkspace#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentEditMyWorkspace extends BaseFragment implements View.OnClickListener
{

    private final String TAG = "FragmentMWOwner";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView mTitle = null;
    private TextView mDescription = null;
    private CheckBox mShredderCheckBox = null;
    private CheckBox mInjectionCheckBox = null;
    private CheckBox mExtrusionCheckBox = null;
    private CheckBox mCompressionCheckBox = null;
    private Button mUpdateWorkspace = null;
    private Button mGotoWorkspace = null;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentEditMyWorkspace()
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
    public static FragmentEditMyWorkspace newInstance(String param1, String param2)
    {
        FragmentEditMyWorkspace fragment = new FragmentEditMyWorkspace();
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
        View view = inflater.inflate(R.layout.fragment_edit_my_workspace, container, false);

        // setting listeners
        mUpdateWorkspace = (Button) view.findViewById(R.id.emw_btn_apply);
        mUpdateWorkspace.setOnClickListener(this);
        mGotoWorkspace = (Button) view.findViewById(R.id.emw_btn_goto_workspace);
        mGotoWorkspace.setOnClickListener(this);

        // gui access
        mTitle = (TextView) view.findViewById(R.id.emw_et_title);
        mDescription = (TextView) view.findViewById(R.id.emw_et_description);

        mShredderCheckBox = (CheckBox) view.findViewById(R.id.emw_checkbox_shredder);
        mInjectionCheckBox = (CheckBox) view.findViewById(R.id.emw_checkbox_injection);
        mExtrusionCheckBox = (CheckBox) view.findViewById(R.id.emw_checkbox_extrusion);
        mCompressionCheckBox = (CheckBox) view.findViewById(R.id.emw_checkbox_compression);

        return view;
    }

    public void onStart() {
        super.onStart();

        final Workspace workspace = PPSession.getCurrentUser().getWorkspace();
        mTitle.setText(workspace.getTitle());
        mDescription.setText(workspace.getDescription());
        mShredderCheckBox.setChecked(workspace.isShredderMachine());
        mInjectionCheckBox.setChecked(workspace.isInjectionMachine());
        mExtrusionCheckBox.setChecked(workspace.isExtrusionMachine());
        mCompressionCheckBox.setChecked(workspace.isCompressionMachine());
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
     * Shows the input message to the user.
     * @param msg message to show.
     */
    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the workspace and switches to workspace view.
     * @param view
     */
    private void onUpdateWorkspace(View view) {

        final User currentUser = PPSession.getCurrentUser();
        final Workspace workspace = currentUser.getWorkspace();

        if (!ViewTools.isTextViewNull(mTitle)) {
            workspace.setTitle(mTitle.getText().toString());
        }

        if (!ViewTools.isTextViewNull(mDescription)) {
            workspace.setDescription(mDescription.getText().toString());
        }

        workspace.setShredderMachine(mShredderCheckBox.isChecked());
        workspace.setInjectionMachine(mInjectionCheckBox.isChecked());
        workspace.setExtrusionMachine(mExtrusionCheckBox.isChecked());
        workspace.setCompressionMachine(mCompressionCheckBox.isChecked());

        currentUser.commitChanges();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.emw_btn_apply):
                onUpdateWorkspace(view);
                break;
            case (R.id.emw_btn_goto_workspace):
                PPSession.getHomeActivity().switchFragment(FragmentMyWorkspaceOwner.class);
                break;
            default:
                break;
        }
    }
}
