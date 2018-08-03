package com.example.android.preciousplastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.session.Session;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth = Session.getFirebaseAuth();

    private final String TAG = "SIGN_IN_ACTIVITY";

    private UserRepository userRepo;

    private TextView userTextView = null;
    private TextView passwordTextView = null;
    private CheckBox ownerCheckBox = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // setting listeners
        Button registerButton = (Button)findViewById(R.id.button_register);
        ImageButton signInButton = (ImageButton)findViewById(R.id.btn_log_in);
        registerButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);

        // db access
        userRepo = new UserRepository(this);

        // gui access
        userTextView = (TextView) findViewById(R.id.text_email);
        passwordTextView = (TextView) findViewById(R.id.text_password);
        ownerCheckBox = (CheckBox)findViewById(R.id.checkbox_owner);
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
        loginUser(userTextView.getText().toString(), passwordTextView.getText().toString());
    }

    public void onRegisterClick(View view) {
        Toast.makeText(this, "register", Toast.LENGTH_SHORT).show();
        System.out.println(userTextView.getText().toString());
        System.out.println(passwordTextView.getText().toString());
        createUser(userTextView.getText().toString(), passwordTextView.getText().toString(), "derp");
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
    void createUser(String email, String password, final String nickname) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userRepo.insertUser(user, nickname, ownerCheckBox.isActivated());
                            loggedIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.\n" + task.getException(),
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
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
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
            case (R.id.button_register):
                onRegisterClick(view);
                break;
            case (R.id.btn_log_in):
                onSignInClick(view);
                break;
        }
    }
}

