package com.example.android.preciousplastic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    DBHandler dbHandler;
    InternetHandler internetHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // example usage of DB
//        dbHandler = new DBHandler(this, this);
//        DbUsageExample();

        // example usage of web access
        internetHandler = new InternetHandler(this, this);
        internetHandler.getMapPins();
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
            case InternetHandler.GET_MAP_PINS:
                Log.d("get map pins", String.valueOf(response));
                break;
            default:
                break;
        }
    }
}
