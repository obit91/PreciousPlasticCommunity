package com.example.android.preciousplastic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private final String TAG = "SIGN_IN_ACTIVITY";

    private TextView userTextView = null;
    private TextView passwordTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        userTextView = (TextView) findViewById(R.id.text_email);
        passwordTextView = (TextView) findViewById(R.id.text_password);

        // sign-in credentials.
        mAuth = FirebaseAuth.getInstance();
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

    public void onSignInClick(View view){
        Toast.makeText(this, "sign in", Toast.LENGTH_SHORT).show();
        loginUser(userTextView.getText().toString(), passwordTextView.getText().toString());
    }

    public void onRegisterClick(View view){
        Toast.makeText(this, "register", Toast.LENGTH_SHORT).show();
        createUser(userTextView.getText().toString(), passwordTextView.getText().toString());
    }

    public void onSignOutClick(View view){
        Toast.makeText(this, "sign out", Toast.LENGTH_SHORT).show();
        signOut();
    }

    /**
     * Creates a new user for authentication purposes & updates the users table in the db.
     * @param email desired user email.
     * @param password desired user password.
     */
    void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            MainActivity.dbHandler.insertUser(user);
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
     * @param email user email.
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
                            FirebaseUser user = mAuth.getCurrentUser();
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
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

    private void DbUsageExample(){
        // example of inserting and authenticating a user!
        MainActivity.dbHandler.authenticate("KerenMeron2", "myEasyPassword");
    }
}

