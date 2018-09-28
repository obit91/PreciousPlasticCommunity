package com.community.android.preciousplastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.community.android.preciousplastic.R;
import com.community.android.preciousplastic.services.ConnectivityMonitorService;
import com.community.android.preciousplastic.utils.PPSession;

public class NoConnectionActivity extends BaseActivity {


    private TextView noConnectionText = null;
    private Button retryButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_no_connection);

        noConnectionText = (TextView) findViewById(R.id.no_connection_tv_title);
        String noConnection = "No active connection." + "\n" + "please connect and retry.";
        noConnectionText.setText(noConnection);

        retryButton = (Button) findViewById(R.id.no_connection_btn_retry);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityMonitorService.isOnline() == ConnectivityMonitorService.OnlineStatus.CONNECTED) {
                    startOver();
                } else {
                    printFail();
                }
            }
        });
    }

/* TODO: doesn't work for some reason, fix..
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "test123", Toast.LENGTH_SHORT).show();
        // minimizes activity on back press
        moveTaskToBack(true);
    }
*/

    /**
     * Starts the application from the start.
     */
    private void startOver() {
        Intent mainIntent = new Intent(this, WelcomeActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }

    private void printFail() {
        Toast.makeText(this, "No active internet connection.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
