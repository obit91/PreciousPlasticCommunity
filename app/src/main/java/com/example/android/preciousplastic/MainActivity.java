package com.example.android.preciousplastic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;


public class MainActivity extends AppCompatActivity{

    private DBHandler dbHandler; // In charge of calls to app's DB

    private final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        dbHandler = new DBHandler(this, this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void DbUsageExample(){
        // example of inserting and authenticating a user!
        dbHandler.insertUser("Keren", "M", "KerenMeron2", "myEasyPassword",
                "keren.meron@mail.huji.ac.il", "Jerusalem", "Israel", "Israel");
        dbHandler.authenticate("KerenMeron2", "myEasyPassword");
    }

    /**
     * Handle any responses / notifications from the DbHandler!
     * Important because any calls to the db are done asynchronously.
     * @param taskType name of task performed
     * @param response content of response
     */

    public void onServerResponse(String taskType, String response) {
        switch (taskType){
            case DBHandler.AUTHENTICATION:
                Toast.makeText(this, "Authenticated: " + response, Toast.LENGTH_SHORT).show();
                Log.d("authentication", response);
                break;
            default:
                break;
        }
    }

    public void onStartClick(View view){
        Intent signInIntent = new Intent(this, SignInActivity.class);
        startActivity(signInIntent);
    }
}
