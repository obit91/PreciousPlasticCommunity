package com.example.android.preciousplastic.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.DBConstants;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.utils.PPSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth = PPSession.getFirebaseAuth();

    private static final String TAG = "WELCOME_ACTIVITY";

    private UserRepository userRepo;

    private TextView emailTextView = null;
    private TextView passwordTextView = null;

    private DatabaseReference mUsersTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_welcome);

        // setting listeners
        Button registerButton = (Button) findViewById(R.id.welcome_btn_register);
        Button signInButton = (Button) findViewById(R.id.welcome_btn_sign_in);
        registerButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);

        // db access
        userRepo = new UserRepository(this);
        mUsersTable = PPSession.getFirebaseDB().getReference(DBConstants.USERS_COLLECTION);

        // gui access
        emailTextView = (TextView) findViewById(R.id.welcome_text_email);
        passwordTextView = (TextView) findViewById(R.id.welcome_text_password);
        //TODO: fix missing checkbox owner
//        ownerCheckBox = (CheckBox)findViewById(R.id.checkbox_owner);
    }

    @Override
    public void onStart() {
        super.onStart();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loggedIn(currentUser.getDisplayName());
        }
    }

    public void onSignInClick(View view) {
        Toast.makeText(this, "sign in", Toast.LENGTH_SHORT).show();
        loginUser(emailTextView.getText().toString(), passwordTextView.getText().toString());
    }

    public void onRegisterClick(View view) {
        Toast.makeText(this, "register", Toast.LENGTH_SHORT).show();
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        //TODO: fix missing owner checkbox
//        boolean owner = ownerCheckBox.isChecked();
        String nickname = email.substring(0, email.indexOf("@"));
        createUser(email, password, nickname, false);
    }

    public void onSignOutClick(View view) {
        Toast.makeText(this, "sign out", Toast.LENGTH_SHORT).show();
        signOut();
    }

    /**
     * Create new document in 'users' collection in database.
     * Document will have unique identifier of param nickName.
     */
    public void insertUser(FirebaseUser firebaseUser, final String nickname, boolean owner) {
        User user = new User(firebaseUser, nickname, owner);
        mUsersTable.child(nickname).setValue(user, new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error == null) {
                    Log.i(TAG, "insertUser: created " + nickname);
                    loggedIn(nickname);
                } else {
                    Log.e(TAG, "insertUser: " + error.getMessage());
                }
            }
        });
    }

    /**
     * Creates a new user for authentication purposes & updates the users table in the db.
     *
     * @param email    desired user email.
     * @param password desired user password.
     */
    void createUser(String email, String password, final String nickname, final boolean owner) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest
                                    .Builder()
                                    .setDisplayName(nickname)
                                    .build();
                            user.updateProfile(profileUpdates);
                            insertUser(user, nickname, owner);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(WelcomeActivity.this, "Authentication failed.\n" + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    /**
     * Logs an existing user to his account.
     *
     * @param email    user email.
     * @param password user password.
     */
    void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(TAG, "signInWithEmail:success");
                            loggedIn(mAuth.getCurrentUser().getDisplayName());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(WelcomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Logs out the currently connected user.
     */
    private void signOut() {
        String mail = mAuth.getCurrentUser().getEmail();
        mAuth.signOut();
        Log.i(TAG, mail + " Signed out.");
    }

    /**
     * Switches activity to the home intent (upon login).
     */
    void loggedIn(final String nickname) {
        userRepo.updateLastLogin(nickname);
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.welcome_btn_register):
                onRegisterClick(view);
                break;
            case (R.id.welcome_btn_sign_in):
                onSignInClick(view);
                break;
        }
    }
}

