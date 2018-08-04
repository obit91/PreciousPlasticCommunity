package com.example.android.preciousplastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.android.preciousplastic.activities.WelcomeActivity;
import com.example.android.preciousplastic.db.DBConstants;
import com.example.android.preciousplastic.session.PPSession;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity{

    private final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        PPSession.setFirebaseAuth(FirebaseAuth.getInstance());
        PPSession.setFirebaseDB(FirebaseDatabase.getInstance());
//        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent welcomeActivity = new Intent(this, WelcomeActivity.class);
        startActivity(welcomeActivity);
    }

    /**
     * Handle any responses / notifications from the DbHandler!
     * Important because any calls to the db are done asynchronously.
     * @param taskType name of task performed
     * @param response content of response
     */

    public void onServerResponse(String taskType, String response) {
        switch (taskType){
            case DBConstants.AUTHENTICATION:
                Toast.makeText(this, "Authenticated: " + response, Toast.LENGTH_SHORT).show();
                Log.d("authentication", response);
                break;
            default:
                break;
        }
    }
}
