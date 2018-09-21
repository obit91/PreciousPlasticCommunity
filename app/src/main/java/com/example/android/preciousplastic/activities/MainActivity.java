package com.example.android.preciousplastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.android.preciousplastic.connection.ConnectivityMonitor;
import com.example.android.preciousplastic.connection.OnTaskCompleted;
import com.example.android.preciousplastic.utils.Transitions.TransitionTypes;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.utils.PPSession;
import com.example.android.preciousplastic.utils.Transitions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.android.preciousplastic.connection.ConnectivityMonitor.isOnline;
import static com.example.android.preciousplastic.utils.PPGUIManager.updateGUI;
import static com.example.android.preciousplastic.utils.PPSession.setMainActivity;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";

    private static final boolean ACTIVE_USER = true;
    private static final boolean NO_ACTIVE_USER = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init firebase
        FirebaseApp.initializeApp(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: Starting a new flow.");
        setMainActivity(this);
        PPSession.setFirebaseDB(FirebaseDatabase.getInstance());
        PPSession.initSession(TransitionTypes.AUTO_SIGN_IN);
    }

    /**
     * Initiates firebase authentication.
     */
    public void fireBaseAuthInit(final TransitionTypes type) {
        FirebaseAuth.AuthStateListener authListener;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    if (currentUser.getDisplayName() != null) {
                        currentUserListener(currentUser.getDisplayName());
                        if (type == TransitionTypes.AUTO_SIGN_IN && !PPSession.isLoggingIn())
                            startApp(ACTIVE_USER);
                    } else {
                        Log.i(TAG, "fireBaseAuthInit: Created user, awaiting nickname update.");
                    }
                } else {
                    Log.i(TAG, "fireBaseAuthInit: New device (app started with no current user).");
                    startApp(NO_ACTIVE_USER);
                }
            }
        };
        firebaseAuth.addAuthStateListener(authListener);
        PPSession.setFirebaseAuth(firebaseAuth);
    }

    /**
     * Starts the app after retrieving the main user.
     * @param activeUser true if the user is already authenticated.
     */
    private void startApp(boolean activeUser) {
        if (activeUser) {
            TransitionTypes type = TransitionTypes.AUTO_SIGN_IN;
            Intent loadingActivity = new Intent(this, LoadingActivity.class);
            loadingActivity.putExtra(Transitions.TRANSITION_TYPE, type);
            loadingActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loadingActivity);
        } else {
            Intent welcomeActivity = new Intent(this, WelcomeActivity.class);
            welcomeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(welcomeActivity);
        }
    }

    /**
     * Listens for changes in the current user.
     */
    public void currentUserListener(final String nickname) {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PPSession.setCurrentUser(dataSnapshot.getValue(User.class));
                String msg = "currentUserListener: updating current user <%s>";
                Log.i(TAG, String.format(msg, nickname));
                updateGUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.i(TAG, "currentUserListener: current user disconnected.");
                finish();
            }
        };
        PPSession.getUsersTable().child(nickname).addValueEventListener(userListener);
    }
}
