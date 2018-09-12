package com.example.android.preciousplastic.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.android.preciousplastic.imgur.ImgurAccessResponse;
import com.example.android.preciousplastic.imgur.ImgurAsyncPostImage;
import com.example.android.preciousplastic.imgur.ImgurData;
import com.google.android.gms.common.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class FragmentTest extends Fragment implements View.OnClickListener, ImgurAccessResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String TAG = "TESTING";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    private Button mTakePhoto = null;
    private Button mUploadButton = null;
    private ImageView mImageTest = null;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_PICK_IMAGE = 2;

    private static final int REQUEST_PERMISSION_CAMERA_STATE = 1;
    private static String mCurrentPhotoPath = null;

    public static final String PREFIX = "stream2file";
    public static final String SUFFIX = ".tmp";


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

        mImageTest = (ImageView) view.findViewById(R.id.test_iv_upload_img);

        return view;
    }

//     TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public static File stream2file (InputStream in) throws IOException {
        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copyStream(in, out);
        }
        return tempFile;
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
    public void getResult(ImgurData asyncResult) {
        System.out.println("yoyoyo");
        ImgurData data = asyncResult;
    }

    public interface OnFragmentInteractionListener {
//         TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.test_btn_capture_image):
                captureImage();
                break;
            case(R.id.test_btn_upload_img):
                chooseImage();
                break;
        }
    }

    private void chooseImage() {

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE);
    }


    private void captureImage() {
        dispatchTakePictureIntent();
    }

    private void showStatePermissions() {
        int permissionCheck = 0;

        permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.CAMERA, REQUEST_PERMISSION_CAMERA_STATE);
            } else {
                requestPermission(Manifest.permission.CAMERA, REQUEST_PERMISSION_CAMERA_STATE);
            }
        } else {
            Log.d(TAG,"CameraPermission: Permission (already) Granted!");
        }
    }

    private void dispatchTakePictureIntent() {

        final PackageManager packageManager = getActivity().getPackageManager();

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(getActivity(), "No camera available.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "startCamera: no camera available.");
            return;
        } else {
            showStatePermissions();
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(getActivity(), "Error occurred during image capture..", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "startCamera: " + ex.getMessage());
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        getActivity(),
                        "com.example.android.preciousplastic",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void uploadImage(Intent data) {

        if (data == null) {
            Log.e(TAG, "uploadImage: no file specified.");
            return;
        }

        File imageFile = new File(mCurrentPhotoPath);

        ImgurAsyncPostImage request = new ImgurAsyncPostImage();
        request.delegate = FragmentTest.this;
        request.title = "test";
        request.description = "test123";
        request.imageFile = imageFile;
        request.execute();

        Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

        mImageTest.setImageBitmap(myBitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            Log.e(TAG, String.format("onActivityResult: failure <id: %d>", resultCode));
            return;
        }

        switch (requestCode) {
            case(REQUEST_TAKE_PHOTO):
                galleryAddPic();
                break;
            case(REQUEST_PICK_IMAGE):
                uploadImage(data);
                break;
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CAMERA_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{permissionName}, permissionRequestCode);
    }
}
