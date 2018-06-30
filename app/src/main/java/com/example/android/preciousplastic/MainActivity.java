package com.example.android.preciousplastic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DBHandler.DbResponse{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHandler dbHandler = new DBHandler(this, this);
        // example of inserting and authenticating a user!

        dbHandler.insertUser("Keren", "M", "KerenMeron2", "myEasyPassword",
                "keren.meron@mail.huji.ac.il", "Jerusalem", "Israel", "Israel");
        dbHandler.authenticate("KerenMeron2", "myEasyPassword");

    }

    /**
     * Handle any responses / notifications from the DbHandler!
     * Important because any calls to the db are done asynchronously.
     * @param taskType name of task performed
     * @param response
     *
     * TODO: either use the switch case for different db responses (e.g. query number of points per user)
     * TODO: OR make a method for each case (in the interface)
     *
     */

    @Override
    public void onDbResponse(String taskType, boolean response) {
        switch (taskType){
            case DBHandler.AUTHENTICATION:
                Toast.makeText(this, "Authenticated: " + String.valueOf(response), Toast.LENGTH_SHORT).show();
                Log.d("authentication", String.valueOf(response));
                break;
            default:
                break;
        }
    }
}
