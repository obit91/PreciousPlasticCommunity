package com.example.android.preciousplastic.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.utils.PPSession;

public abstract class BaseActivity extends AppCompatActivity {

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
            alertNoConnection();
        }
    };

    private void alertNoConnection() {
        /*
        TODO: Can't use this code for now.. need to fix a bug.
         */
/*        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle(R.string.app_name);
        builder.setMessage("There is no active connection.");
//            builder.setIcon(R.drawable.ic_launcher);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchToNoConnection();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();*/
        switchToNoConnection();
    }

    private void switchToNoConnection() {
        Intent noConnectionIntent = new Intent(this, NoConnectionActivity.class);
        noConnectionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(noConnectionIntent);
        finish();
    }
}
