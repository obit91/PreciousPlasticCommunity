package com.example.android.preciousplastic.activities;

import android.content.Intent;
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
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.session.PPSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth = PPSession.getFirebaseAuth();

    private final String TAG = "WELCOME_ACTIVITY";

    private UserRepository userRepo;

    private TextView emailTextView = null;
    private TextView passwordTextView = null;
    private CheckBox ownerCheckBox = null;

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

        // gui access
        emailTextView = (TextView) findViewById(R.id.welcome_text_email);
        passwordTextView = (TextView) findViewById(R.id.welcome_text_password);
        //TODO: fix missing checkbox owner
//        ownerCheckBox = (CheckBox)findViewById(R.id.checkbox_owner);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loggedIn();
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
        String nickname = "derp";
        createUser(email, password, nickname, true);
    }

    public void onSignOutClick(View view) {
        Toast.makeText(this, "sign out", Toast.LENGTH_SHORT).show();
        signOut();
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
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userRepo.insertUser(user, nickname, owner);
                            loggedIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
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
                            Log.d(TAG, "signInWithEmail:success");
                            loggedIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
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
        Log.d(TAG, mail + " Signed out.");
    }

    /**
     * Switches activity to the home intent (upon login).
     */
    void loggedIn() {
        String uid = mAuth.getCurrentUser().getUid();
        userRepo.updateLastLogin(uid);
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

