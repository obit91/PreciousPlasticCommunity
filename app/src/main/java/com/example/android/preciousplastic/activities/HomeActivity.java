package com.example.android.preciousplastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.PointsType;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.session.PPSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.views.MapView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "HOME_ACTIVITY";

    private FirebaseAuth mAuth = PPSession.getFirebaseAuth();
    private UserRepository mUserRepository;

    private TextView userTextView = null;
    private MapActivity mapActivity = null;

    private TextView pointsTextView = null;
    private TextView pointsTypeTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        userTextView = (TextView) findViewById(R.id.home_text_mail);
        mapActivity = new MapActivity(this, this);
        mUserRepository = new UserRepository(this);

        // setting listeners
        Button signOutButton = (Button)findViewById(R.id.button_sign_out);
        Button mapButton = (Button)findViewById(R.id.button_map);
        Button incrementButton = (Button)findViewById(R.id.button_increment);
        Button decrementButton = (Button)findViewById(R.id.button_decrement);
        signOutButton.setOnClickListener(this);
        mapButton.setOnClickListener(this);
        incrementButton.setOnClickListener(this);
        decrementButton.setOnClickListener(this);

        // points values
        pointsTextView = (TextView) findViewById(R.id.text_points_value);
        pointsTypeTextView = (TextView) findViewById(R.id.text_points_type);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String mail = currentUser.getEmail();
        userTextView.setText(mail);
    }

    /**
     * Logs out the current user.
     * @param view
     */
    public void onSignOutClick(View view){
        Toast.makeText(this, "sign out", Toast.LENGTH_SHORT).show();
        signOut();
    }

    /**
     * Signs out the current Firebase user.
     */
    private void signOut() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String mail = user.getEmail();
            mAuth.signOut();
            Log.d(TAG, mail + " Signed out.");
        }
        returnToSignIn();
    }

    /**
     * Switches activity to the sign-in intent (usually after sign out).
     */
    private void returnToSignIn() {
        Intent signInIntent = new Intent(this, SignInActivity.class);
        startActivity(signInIntent);
    }

    /**
     * Opens the map.
     * @param view
     */
    public void onOpenMapClick(View view){
        if (mapActivity != null){
            setContentView(R.layout.activity_map);
            MapView mapView = (MapView) findViewById(R.id.activity_map);
            mapActivity.buildMap(mapView);
        }
    }

    public void onIncrementClick(View view) {
        User user = PPSession.currentUser();
        int pointTypeInt = Integer.parseInt(pointsTypeTextView.getText().toString());
        int pointsValueInt = Integer.parseInt(pointsTextView.getText().toString());
        PointsType type = PointsType.getType(pointTypeInt);
        user.addPoints(type, pointsValueInt);
        mUserRepository.updateUser(user);
    }

    public void onDecrementClick(View view) {
        User user = PPSession.currentUser();
        int pointTypeInt = Integer.parseInt(pointsTypeTextView.getText().toString());
        int pointsValueInt = Integer.parseInt(pointsTextView.getText().toString());
        user.removePoints(PointsType.getType(pointTypeInt), pointsValueInt);
        mUserRepository.updateUser(user);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.button_sign_out):
                onSignOutClick(view);
                break;
            case (R.id.button_map):
                onOpenMapClick(view);
                break;
            case (R.id.button_increment):
                onIncrementClick(view);
                break;
            case (R.id.button_decrement):
                onDecrementClick(view);
                break;
        }
    }

}

