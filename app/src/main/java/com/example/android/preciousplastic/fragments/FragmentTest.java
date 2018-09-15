package com.example.android.preciousplastic.fragments;

import android.Manifest;
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


public class FragmentTest extends Fragment implements View.OnClickListener, ImgurAccessResponse<ImgurData> {
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

    @Override
    public void getResult(ImgurData imgurData) {
        final User currentUser = PPSession.getCurrentUser();
        currentUser.addBazarItem(imgurData);
    }

    public interface OnFragmentInteractionListener {
//         TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void removeImage() {
        final User currentUser = PPSession.getCurrentUser();
        final Workspace workspace = currentUser.getWorkspace();
        final HashMap<String, ImgurBazarItem> itemsOnSale = workspace.getItemsOnSale();
        for (String key : itemsOnSale.keySet()) {
            currentUser.removeBazarItem(itemsOnSale.get(key));
            break;
        }
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
            case(R.id.test_btn_delete_img):
                removeImage();
                break;
        }
    }

    private void chooseImage() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);

//        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        pickIntent.setType("image/*");
//
//        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

//        startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE);
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

    private File generateTempFile(Bitmap chosenImage) {
        // create a new file
        File imageFile = new File(getContext().getCacheDir(), generateFileName());

        //convert bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        chosenImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "generateTempFile: failed to create a temp file for gallery image.");
            return null;
        } catch (IOException e) {
            Log.e(TAG, "generateTempFile: failed to write image to temp file.");
            return null;
        }
        return imageFile;
    }

    private void uploadImage(Intent data) {

        if (data == null) {
            Log.e(TAG, "uploadImage: no file specified.");
            return;
        }

        Uri uri = data.getData();

//        File imageFile = new File(mCurrentPhotoPath);
        Bitmap chosenImage = null;
        try {
            chosenImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
        } catch (IOException e) {
            Log.e(TAG, "uploadImage: couldn't resolve chosen image URI to bitmap.");
            return;
        }

        mImageTest.setImageBitmap(chosenImage);

        final ImgurAsyncGenericTask imgurAsyncGenericTask = ImgurRequestsGenerator.generatePOST(this,
                generateTempFile(chosenImage), "test", "test123");
        imgurAsyncGenericTask.execute();
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
        String imageFileName = generateFileName();
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

    @NonNull
    private String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "JPEG_" + timeStamp + "_";
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
