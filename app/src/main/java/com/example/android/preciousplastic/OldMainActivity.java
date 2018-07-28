package com.example.android.preciousplastic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import org.osmdroid.views.MapView;

public class OldMainActivity extends AppCompatActivity{

    private DBHandler dbHandler; // In charge of calls to app's DB
    private MapActivity mapActivity;
    private FirebaseAuth mAuth;

    private final String TAG = "main screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // start-up tools
//        mapActivity = new MapActivity(this, this);
    }

//    public void onOpenMapClick(View view){
//        if (mapActivity != null){
//            setContentView(R.layout.activity_map);
//            MapView mapView = (MapView) findViewById(R.id.activity_map);
//            mapActivity.buildMap(mapView);
//        }
//    }
}
