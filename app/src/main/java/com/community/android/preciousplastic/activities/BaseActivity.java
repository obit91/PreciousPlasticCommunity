package com.community.android.preciousplastic.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.community.android.preciousplastic.permissions.PermissionResponseHandler;
import com.community.android.preciousplastic.utils.PPSession;

public abstract class BaseActivity extends AppCompatActivity {

    PermissionResponseHandler permissionResponseHandler;
    private boolean isInForeground;
    private boolean shouldSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PPSession.setCurrentActivity(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter(PPSession.currentIntentKey));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            shouldSwitch = true;
            alertNoConnection();
        }
    };

    @Override
    protected void onResume()
    {
        super.onResume();
        isInForeground = false;
        if (shouldSwitch) {
            alertNoConnection();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        isInForeground = true;
    }

    private void alertNoConnection() {
        if (!isInForeground) {
            switchToNoConnection();
        }
    }

    private void switchToNoConnection() {
        shouldSwitch = false;
        Intent noConnectionIntent = new Intent(this, NoConnectionActivity.class);
        noConnectionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(noConnectionIntent);
        finish();
    }

    /**
     * Handles the results of a permission request from the user.
     * @param requestCode what type of request we're catching.
     * @param permissions permissions requested (we're only requesting one at a time).
     * @param grantResults results returned.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            permissionResponseHandler.permissionGranted(requestCode);
        } else {
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            permissionResponseHandler.permissionDenied(requestCode);
        }
    }

    public void setPermissionResponseHandler(PermissionResponseHandler permissionResponseHandler) {
        this.permissionResponseHandler = permissionResponseHandler;
    }
}
