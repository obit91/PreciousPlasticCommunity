package com.example.android.preciousplastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.PointTypes;
import com.example.android.preciousplastic.db.UserPoints;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.session.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.osmdroid.views.MapView;

public class HomeActivity extends AppCompatActivity {

    private final String TAG = "HOME_ACTIVITY";

    private FirebaseAuth mAuth = Session.getFirebaseAuth();
    private UserRepository mUserRepository;

    private TextView userTextView = null;
    private MapActivity mapActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        userTextView = (TextView) findViewById(R.id.home_text_mail);
        mapActivity = new MapActivity(this, this);
        mUserRepository = new UserRepository(this);
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

    public void onUpdateScore2(View view) {
        User user = Session.currentUser();
        user.addPoints(PointTypes.TYPE_1, 1);
        mUserRepository.updateUser(user);
    }
}

