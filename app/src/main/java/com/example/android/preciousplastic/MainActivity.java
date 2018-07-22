package com.example.android.preciousplastic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.osmdroid.views.MapView;

public class MainActivity extends AppCompatActivity{

    DBHandler dbHandler;        // In charge of calls to app's DB
    MapActivity mapActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // start-up tools
        mapActivity = new MapActivity(this, this);
        dbHandler = new DBHandler(this, this);

        //DbUsageExample();  // example usage of DB

    }

    public void onOpenMapClick(View view){
        if (mapActivity != null){
            setContentView(R.layout.activity_map);
            MapView mapView = (MapView) findViewById(R.id.activity_map);
            mapActivity.buildMap(mapView);
        }
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
}
