package com.community.android.preciousplastic.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.community.android.preciousplastic.fragments.BaseFragment;

public class PermissionManager {

    public enum PERMISSIONS {
        REQUEST_PERMISSION_CAMERA_STATE(1),
        REQUEST_PERMISSION_WRITE_EXTERNAL(2),
        REQUEST_PERMISSION_FINE_LOCATION(3);

        private int value;

        PERMISSIONS(int value) {
            this.value = value;
        }

        public int getValue() { return value; }
    }

    private Activity activity;
    private PermissionResponseHandler permissionResponseHandlers;

    public <T extends BaseFragment & PermissionResponseHandler> PermissionManager(T fragment) {
        this.activity = fragment.getActivity();
        this.permissionResponseHandlers = fragment;
    }

    public <T extends Activity & PermissionResponseHandler> PermissionManager(T activity) {
        this.activity = activity;
        this.permissionResponseHandlers = activity;
    }

    private static final String TAG = "PERMISSION_MANAGER";

    /**
     * Shows an explanation to the user, why are we asking for permissions?
     * @param title title
     * @param message message
     * @param permission which permission we're requesting
     * @param permissionRequestCode the unique permission request code.
     */
    private void showExplanation(String title,
                                       String message,
                                       final String permission,
                                       final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    /**
     * Initiates a permission request popup.
     * @param permissionName name of the permission we desire.
     * @param permissionRequestCode unique permission code.
     */
    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permissionName}, permissionRequestCode);
    }

    /**
     * Requests permissions for requirements.
     * @param permissionToCheck which permission is requested.
     */
    public void showStatePermissions(PERMISSIONS permissionToCheck) {
        int permissionCheck;

        String permissionRequest = null;
        switch (permissionToCheck) {
            case REQUEST_PERMISSION_CAMERA_STATE:
                permissionRequest = Manifest.permission.CAMERA;
                break;
            case REQUEST_PERMISSION_WRITE_EXTERNAL:
                permissionRequest = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                break;
            case REQUEST_PERMISSION_FINE_LOCATION:
                permissionRequest = Manifest.permission.ACCESS_FINE_LOCATION;
                break;
        }

        permissionCheck = ContextCompat.checkSelfPermission(activity, permissionRequest);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionRequest)) {
                showExplanation("Permission Needed", "This page can not be used without granting the permission.", permissionRequest, permissionToCheck.value);
            } else {
                requestPermission(permissionRequest, permissionToCheck.value);
            }
        } else {
            permissionResponseHandlers.permissionGranted(permissionToCheck.value);
        }
    }
}
