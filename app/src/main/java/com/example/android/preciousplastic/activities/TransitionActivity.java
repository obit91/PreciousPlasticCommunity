package com.example.android.preciousplastic.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.android.preciousplastic.utils.Transitions.TransitionTypes;

import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.utils.PPSession;
import com.example.android.preciousplastic.utils.Transitions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class TransitionActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private UserRepository userRepo;

    private static final String TAG = "TRANSITION_ACTIVITY";

    @Override
    protected void onStart() {
        super.onStart();

        // user access
        mAuth = PPSession.getFirebaseAuth();

        // db access
        userRepo = new UserRepository(this);

        Bundle b = getIntent().getExtras();
        TransitionTypes type = (TransitionTypes)b.getSerializable(Transitions.TRANSITION_TYPE);

        switch (type) {
            case REGISTER:
                initiateCreation(b);
                break;
            case SIGN_IN:
                initiateLogin(b);
                break;
            case SIGN_OUT:
                signOut();
                break;
            default:
                // ??
        }
    }

    /**
     * Retrieves login parameters and initiates the login procedure.
     * @param b input bundle.
     */
    private void initiateLogin(Bundle b) {
        String email = b.getString(Transitions.TRANSITION_EMAIL);
        String password = b.getString(Transitions.TRANSITION_PASS);
        loginUser(email, password);
    }

    /**
     * Logs an existing user to his account.
     *
     * @param email    user email.
     * @param password user password.
     */
    private void loginUser(String email, String password) {
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
                            Toast.makeText(TransitionActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Switches activity to the home intent (upon login).
     */
    private void loggedIn(String nickname) {
        userRepo.updateLastLogin(nickname);
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

    /**
     * Create new document in 'users' collection in database.
     * Document will have unique identifier of param nickName.
     */
    private void insertUser(FirebaseUser firebaseUser, final String nickname, boolean owner) {
        final User user = new User(firebaseUser, nickname, owner);
        DatabaseReference usersTable = PPSession.getUsersTable();
        usersTable.child(nickname).setValue(user, new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error == null) {
                    Log.i(TAG, "insertUser: created " + nickname);
                    PPSession.setCurrentUser(user);
                    loggedIn(nickname);
                } else {
                    Log.e(TAG, "insertUser: " + error.getMessage());
                }
            }
        });
    }

    /**
     * Retrieves user data from the bundle and initiates a new user creation procedure.
     * @param b input bundle.
     */
    private void initiateCreation(Bundle b) {
        String email = b.getString(Transitions.TRANSITION_EMAIL);
        String password = b.getString(Transitions.TRANSITION_PASS);
        String nickname = b.getString(Transitions.TRANSITION_NICKNAME);
        createUser(email, password, nickname, false);
    }

    /**
     * Creates a new user for authentication purposes & updates the users table in the db.
     *
     * @param email    desired user email.
     * @param password desired user password.
     */
    private void createUser(String email, String password, final String nickname, final boolean owner) {
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
                            Toast.makeText(TransitionActivity.this, "Authentication failed.\n" + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    /**
     * Signs out the current Firebase user.
     */
    private void signOut() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String nickname = user.getDisplayName();
            String msg = "SignOut: %s signed out.";
            mAuth.signOut();
            Log.i(TAG, String.format(msg, nickname));
        }
        finish();
    }

}
