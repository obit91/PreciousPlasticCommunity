package com.example.android.preciousplastic.fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.Workspace;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.imgur.ImgurAccessResponse;
import com.example.android.preciousplastic.imgur.ImgurAsyncGenericTask;
import com.example.android.preciousplastic.imgur.ImgurAsyncPostImage;
import com.example.android.preciousplastic.imgur.ImgurBazarItem;
import com.example.android.preciousplastic.imgur.ImgurData;
import com.example.android.preciousplastic.imgur.ImgurRequestsGenerator;
import com.example.android.preciousplastic.utils.PPSession;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class FragmentTest extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String TAG = "TESTING";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    private Button mTakePhoto = null;
    private Button mUploadButton = null;
    private Button mDeleteButton = null;
    private ImageView mImageTest = null;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_PICK_IMAGE = 2;

    private static final int REQUEST_PERMISSION_CAMERA_STATE = 1;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL = 2;
    private static String mCurrentPhotoPath = null;


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
        mTakePhoto = (Button) view.findViewById(R.id.test_btn_capture_image);
        mTakePhoto.setOnClickListener(this);
        mUploadButton = (Button) view.findViewById(R.id.test_btn_upload_img);
        mUploadButton.setOnClickListener(this);
        mDeleteButton = (Button) view.findViewById(R.id.test_btn_delete_img);
        mDeleteButton.setOnClickListener(this);

        mImageTest = (ImageView) view.findViewById(R.id.test_iv_upload_img);

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
            case(R.id.test_btn_capture_image):
                break;
            case(R.id.test_btn_upload_img):
                break;
            case(R.id.test_btn_delete_img):
                break;
        }
    }
}
