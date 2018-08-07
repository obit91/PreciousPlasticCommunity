package com.example.android.preciousplastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.DBConstants;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.utils.PPSession;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";

    private DatabaseReference mUsersTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init firebase
        FirebaseApp.initializeApp(this);
        PPSession.setFirebaseAuth(FirebaseAuth.getInstance());
        PPSession.setFirebaseDB(FirebaseDatabase.getInstance());
        mUsersTable = PPSession.getFirebaseDB().getReference(DBConstants.USERS_COLLECTION);
    }

    @Override
    public void onStart() {
        super.onStart();

        // init current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserListener(currentUser.getDisplayName());
        } else {
            Log.i(TAG, "onStart: New device (app started with no current user).");
            startApp();
        }
    }

    /**
     * Starts the app after retrieving the main user.
     */
    private void startApp() {
        Intent welcomeActivity = new Intent(this, WelcomeActivity.class);
        startActivity(welcomeActivity);
    }

    /**
     * Listens for changes in the current user.
     */
    public void currentUserListener(final String nickname) {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (PPSession.getCurrentUser() == null) {
                    PPSession.setCurrentUser(dataSnapshot.getValue(User.class));
                    String msg = "currentUserListener: listening to current user <%s>";
                    Log.i(TAG, String.format(msg, nickname));
                    startApp();
                } else {
                    PPSession.setCurrentUser(dataSnapshot.getValue(User.class));
                    String msg = "currentUserListener: updating current user <%s>";
                    Log.i(TAG, String.format(msg, nickname));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.i(TAG, "currentUserListener: current user disconnected.");
                finish();
            }
        };
        mUsersTable.child(nickname).addValueEventListener(userListener);
    }
}
