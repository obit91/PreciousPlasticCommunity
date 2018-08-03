package com.example.android.preciousplastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.DBConstants;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.session.Session;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity{

    private final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        Session.setFirebaseAuth(FirebaseAuth.getInstance());
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onStartClick(View view){
        Intent signInIntent = new Intent(this, SignInActivity.class);
        startActivity(signInIntent);
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
