package com.community.android.preciousplastic.fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.community.android.preciousplastic.R;
import com.community.android.preciousplastic.activities.BaseActivity;
import com.community.android.preciousplastic.db.entities.User;
import com.community.android.preciousplastic.imgur.ImgurAsyncGenericTask;
import com.community.android.preciousplastic.imgur.ImgurData;
import com.community.android.preciousplastic.imgur.ImgurRequestsGenerator;
import com.community.android.preciousplastic.imgur.UploadNode;
import com.community.android.preciousplastic.imgur.UploadTask;
import com.community.android.preciousplastic.permissions.PermissionResponseHandler;
import com.community.android.preciousplastic.utils.ImageUtils;
import com.community.android.preciousplastic.utils.PPSession;
import com.community.android.preciousplastic.permissions.PermissionManager;
import com.community.android.preciousplastic.utils.ViewTools;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import static android.app.Activity.RESULT_OK;


public class FragmentUploadItem extends BaseFragment implements View.OnClickListener, UploadTask, PermissionResponseHandler {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String TAG = "UPLOAD_ITEM";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    private PermissionManager permissionManager;

    private Button mCaptureButton = null;
    private Button mChooseButton = null;
    private ImageView mUploadImage = null;
    private ProgressBar mProgressBar = null;
    private TextView mProgressText = null;
    private EditText mTitle = null;
    private EditText mDescription = null;
    private Button mUploadButton = null;
    private EditText mPrice = null;

    private Intent mChosenResult = null;

    // concurrentQueue to manage multi-threaded async uploads.
    private static Queue<UploadNode> mUploadQueue = new ConcurrentLinkedQueue<>();

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_PICK_IMAGE = 2;

    private static String mCurrentPhotoPath = null;

    private Set<View> uploadViews;

    public FragmentUploadItem() {
        // Required empty public constructor
    }

    public static FragmentUploadItem newInstance(String param1) {
        FragmentUploadItem fragment = new FragmentUploadItem();
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
        permissionManager = new PermissionManager(this);
        setPermissionResponseHandler(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_item, container, false);

        // set button listener
        mCaptureButton = (Button) view.findViewById(R.id.upload_btn_capture_image);
        mCaptureButton.setOnClickListener(this);
        mChooseButton = (Button) view.findViewById(R.id.upload_btn_choose_img);
        mChooseButton.setOnClickListener(this);


        // Upload fields will become visible only after choosing an image.
        uploadViews = new HashSet<>();
        mUploadButton = (Button) view.findViewById(R.id.upload_btn_upload);
        mUploadButton.setOnClickListener(this);
        mUploadButton.setClickable(false);
        mUploadButton.setVisibility(View.INVISIBLE);
        uploadViews.add(mUploadButton);
        mTitle = (EditText) view.findViewById(R.id.upload_et_title);
        mTitle.setVisibility(View.INVISIBLE);
        mTitle.setClickable(false);
        uploadViews.add(mTitle);
        mDescription = (EditText) view.findViewById(R.id.upload_et_description);
        mDescription.setVisibility(View.INVISIBLE);
        uploadViews.add(mDescription);
        mPrice = (EditText) view.findViewById(R.id.upload_et_price);
        mPrice.setVisibility(View.INVISIBLE);
        uploadViews.add(mPrice);

        // progress
        mUploadImage = (ImageView) view.findViewById(R.id.upload_iv_upload_img);
        mProgressBar = (ProgressBar) view.findViewById(R.id.upload_progress_bar);
        mProgressBar.getProgressDrawable().setColorFilter(
                Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
        mProgressText = (TextView) view.findViewById(R.id.upload_tv_progress);

        updateProgressGUI();
        return view;
    }

//     TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    /**
     * Updates the GUI with current image uploads.
     */
    private void updateProgressGUI() {
        int visibility;
        if (mUploadQueue.size() > 0) {
            // the user has left and returned to the fragment, update to the new views.
            if (PPSession.getCurrentFragment() != this) {
                final Fragment currentFragment = PPSession.getCurrentFragment();
                if (currentFragment.getClass() == FragmentUploadItem.class) {
                    FragmentUploadItem newFragment = (FragmentUploadItem)currentFragment;
                    mProgressBar = newFragment.getmProgressBar();
                    mProgressText = newFragment.getmProgressText();
                    mUploadImage = newFragment.getmUploadImage();
                    mUploadImage.setImageBitmap(mUploadQueue.peek().getBitmap());
                }
            }

            if (mProgressBar.getVisibility() == View.VISIBLE) {
                return;
            }
            visibility = View.VISIBLE;
            mUploadImage.setImageBitmap(mUploadQueue.peek().getBitmap());
        } else {
            visibility = View.INVISIBLE;
        }
        mProgressBar.setVisibility(visibility);
        mProgressText.setVisibility(visibility);
        mUploadImage.setVisibility(visibility);
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

    /**
     * Upon completing a complete image upload the image is then saved to the bazar under the user.
     * @param imgurData uploaded image.
     */
    @Override
    public void getResult(ImgurData imgurData) {
        final User currentUser = PPSession.getCurrentUser();
        final UploadNode uploadNode = mUploadQueue.poll();
        imgurData.setPrice(uploadNode.getPrice());
        currentUser.addBazarItem(imgurData);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public interface OnFragmentInteractionListener {
//         TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Trasnfers control to the image gallery and awaits user input.
     */
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }


    /**
     * Initiates the camera.
     */
    private void captureImage() {
        dispatchTakePictureIntent();
    }

    /**
     * Takes a picture using the camera.
     */
    private void dispatchTakePictureIntent() {

        final PackageManager packageManager = getActivity().getPackageManager();

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(getActivity(), "No camera available.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "startCamera: no camera available.");
            return;
        }
        permissionManager.showStatePermissions(PermissionManager.PERMISSIONS.REQUEST_PERMISSION_CAMERA_STATE);
    }

    /**
     * Uploads an image selected from the gallery.
     * @param data result returned from the camera intent.
     */
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

        // update upload queue with current image.
        UploadNode uploadNode = new UploadNode(chosenImage, Double.valueOf(mPrice.getText().toString()));
        mUploadQueue.add(uploadNode);
        final File tempFile = ImageUtils.generateTempFile(
                getContext(),
                chosenImage);

        final ImgurAsyncGenericTask imgurAsyncGenericTask = ImgurRequestsGenerator.generatePOST(this,
                        tempFile,
                        mTitle.getText().toString(),
                        mDescription.getText().toString());
        imgurAsyncGenericTask.execute();
    }

    /**
     * Handles intents output.
     * @param requestCode result status.
     * @param resultCode type of request we're receiving.
     * @param data result output.
     */
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
                mChosenResult = data;
                enableUploadGUI(true);
                break;
        }
    }

    /**
     * Adds the captured image to the gallery.
     */
    private void galleryAddPic() {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, mCurrentPhotoPath);

        getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * Creates an image file based on the image captured by camera.
     * @return returns a file containing the new image.
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = ImageUtils.generateFileName();
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

    /**
     * Called during the upload process.
     * @param percentage how much has been uploaded thus far.
     */
    @Override
    public void onProgressUpdate(int percentage) {
        updateProgressGUI();
        mProgressBar.setProgress(percentage);
        String progress = percentage + "%";
        mProgressText.setText(progress);
    }

    @Override
    public void onError() {
    }

    /**
     * Updates the GUI upon completion.
     */
    @Override
    public void onFinish() {
        mProgressBar.setVisibility(View.INVISIBLE);
        String done = "Upload complete!";
        mProgressText.setText(done);
    }

    public ImageView getmUploadImage() {
        return mUploadImage;
    }

    public ProgressBar getmProgressBar() {
        return mProgressBar;
    }

    public TextView getmProgressText() {
        return mProgressText;
    }

    /**
     * Assures that all upload fields have been filled with data and starts the upload.
     */
    private boolean initiateUpload() {
        boolean invalidInput = false;
        StringBuilder msg = new StringBuilder();
        msg.append("Please fill the following fields: ").append("\n");

        if (ViewTools.isTextViewNull(mTitle)) {
            msg.append("title").append("\n");
            mTitle.setHintTextColor(Color.RED);
            invalidInput = true;
        } else {
            mTitle.setHintTextColor(Color.BLACK);
        }

        if ( ViewTools.isTextViewNull(mDescription)) {
            msg.append("description").append("\n");
            mDescription.setHintTextColor(Color.RED);
            invalidInput = true;
        } else {
            mDescription.setHintTextColor(Color.BLACK);
        }

        if ( ViewTools.isTextViewNull(mPrice)) {
            msg.append("price").append("\n");
            mPrice.setHintTextColor(Color.RED);
            invalidInput = true;
        } else {
            mPrice.setHintTextColor(Color.BLACK);
        }

        if (invalidInput) {
            Toast.makeText(getActivity(), msg.toString().trim(), Toast.LENGTH_SHORT).show();
            return false;
        }

        uploadImage(mChosenResult);
        return true;
    }

    /**
     * Enables the upload fields.
     */
    private void enableUploadGUI(boolean enable) {
        int visibility = enable ? View.VISIBLE : View.INVISIBLE;
        for (View uploadView : uploadViews) {
            uploadView.setClickable(enable);
            uploadView.setVisibility(visibility);
        }
    }

    /**
     * Handles button clicks.
     * @param v chosen button.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.upload_btn_capture_image):
                captureImage();
                break;
            case(R.id.upload_btn_choose_img):
                chooseImage();
                break;
            case (R.id.upload_btn_upload):
                if (initiateUpload()) {
                    mTitle.setText(null);
                    mDescription.setText(null);
                    mPrice.setText(null);
                }
                break;
        }
    }

    @Override
    public void permissionGranted(int permission) {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionManager.showStatePermissions(PermissionManager.PERMISSIONS.REQUEST_PERMISSION_WRITE_EXTERNAL);
                return;
            }
        } else {
            return;
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final PackageManager packageManager = getActivity().getPackageManager();
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
                        "com.community.android.preciousplastic",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void setPermissionResponseHandler(PermissionResponseHandler permissionResponseHandler) {
        final BaseActivity activity = (BaseActivity) getActivity();
        activity.setPermissionResponseHandler(permissionResponseHandler);
    }

    @Override
    public void permissionDenied(int permissionCode) {

    }
}
