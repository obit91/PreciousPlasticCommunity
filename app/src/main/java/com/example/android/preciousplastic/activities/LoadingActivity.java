package com.example.android.preciousplastic.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.Workspace;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.fragments.FragmentCompleteRecycle;
import com.example.android.preciousplastic.imgur.ImgurBazarItem;
import com.example.android.preciousplastic.utils.EventNotifier;
import com.example.android.preciousplastic.utils.OnBackPressed;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LoadingActivity extends BaseActivity {

    public static final int LONGEST_QUOTE = 3;
    private final int MAX_NUM_OF_LOADING_IMAGES = 4;
    private final int MAX_NUM_OF_QUOTES = 5;

    private static final int TRANSITION_TIME = 1500;

    private FirebaseAuth mAuth;
    private UserRepository userRepo;

    private static final String TAG = "LOADING_ACTIVITY";

    private ArrayList<Integer> loadingScreenImages;
    private Integer chosenImageIndex;
    private ImageView chosenImage;
    private TextView quote;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        random = new Random();
        randomize_load_image();
        randomize_quote();
    }

    private void randomize_quote() {
        quote = (TextView) findViewById(R.id.fragment_transition_tv_quote);
        String[] quotes;
        quotes = getResources().getStringArray(R.array.quotesArray);

        String[] authors;
        authors = getResources().getStringArray(R.array.authorsArray);

        int randQuoteNum = random.nextInt(quotes.length);

        quote.setText(String.format("%s \n\t\t\t -%s", quotes[randQuoteNum],
                authors[randQuoteNum]));
    }


    private void randomize_load_image() {
        loadingScreenImages = new ArrayList<>();
        chosenImage = (ImageView) findViewById(R.id.fragment_transition_iv);
        // add 4 loading screens.
        loadingScreenImages.add(R.drawable.extrusion_png);
        loadingScreenImages.add(R.drawable.compression_png);
        loadingScreenImages.add(R.drawable.injection_png);
        loadingScreenImages.add(R.drawable.shredder_png);

        chosenImageIndex = random.nextInt(MAX_NUM_OF_LOADING_IMAGES - 1);

        chosenImage.setImageResource(loadingScreenImages.get(chosenImageIndex));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // user access
        mAuth = PPSession.getFirebaseAuth();

        // db access
        userRepo = new UserRepository(this);

        Bundle b = getIntent().getExtras();
        Transitions.TransitionTypes type = (Transitions.TransitionTypes) b.getSerializable(Transitions.TRANSITION_TYPE);

        switch (type) {
            case REGISTER:
                initiateCreation(b);
                break;
            case SIGN_IN:
                initiateLogin(b);
                break;
            case AUTO_SIGN_IN:
                skipProcedure();
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
     *
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
        PPSession.setLoggingIn(true);
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
                            Toast.makeText(LoadingActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                        PPSession.setLoggingIn(false);
                    }
                });
    }

    /**
     * Called when the user is already logged in.
     */
    private void skipProcedure() {
        loggedIn(mAuth.getCurrentUser().getDisplayName());
    }

    /**
     * Switches activity to the home intent (upon login).
     */
    private void loggedIn(String nickname) {
        LoginNotifier loginNotifier = new LoginNotifier(getApplicationContext());
        userRepo.updateLastLogin(nickname, loginNotifier);
    }

    /**
     * On successful trade switch to success screen, else show a failure message.
     */
    private class LoginNotifier extends EventNotifier {

        private Context delegate;

        public LoginNotifier(Context delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onResponse(Object dataSnapshotObj) {
            boolean success = (boolean) dataSnapshotObj;
            if (success) {
                Intent homeIntent = new Intent(delegate, HomeActivity.class);
                try {
                    if (chosenImageIndex == LONGEST_QUOTE) {
                        Thread.sleep(2 * TRANSITION_TIME);
                    } else {
                        Thread.sleep(TRANSITION_TIME);
                    }

                } catch (InterruptedException e) {
                    Log.d(TAG, "LoggedIn: failed to sleep, someone's rushing.");
                }
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            } else {
                Toast.makeText(delegate, "Nickname does not exist.", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(delegate, WelcomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                delegate.startActivity(i);
            }
        }
    }

    /**
     * Creates a new workspace based on the input of ownerMachines map.
     *
     * @param ownerMachines a map containing different machine types and indicators if exist.
     * @return a workspace corresponding to the map.
     */
    private Workspace parseWorkspace(Map<String, Boolean> ownerMachines) {
        boolean shredder = false;
        boolean injection = false;
        boolean extrusion = false;
        boolean compression = false;
        if (ownerMachines.get(Transitions.TRANSITION_MACHINE_SHREDDER)) {
            shredder = true;
        }
        if (ownerMachines.get(Transitions.TRANSITION_MACHINE_INJECTION)) {
            injection = true;
        }
        if (ownerMachines.get(Transitions.TRANSITION_MACHINE_EXTRUSION)) {
            extrusion = true;
        }
        if (ownerMachines.get(Transitions.TRANSITION_MACHINE_COMPRESSION)) {
            compression = true;
        }
        return new Workspace(shredder, injection, extrusion, compression);
    }

    /**
     * Create new document in 'users' collection in database.
     * Document will have unique identifier of param nickName.
     */
    private void insertUser(FirebaseUser firebaseUser, final String nickname, Map<String, Boolean> ownerMachines) {
        boolean isOwner = ownerMachines != null;
        final User user = new User(firebaseUser, nickname, isOwner);
        if (isOwner) {
            Workspace workspace = parseWorkspace(ownerMachines);
            user.setWorkspace(workspace);
        }
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
     * Updates indicators if machines exist of a new owner.
     *
     * @param b Activity bundle.
     * @return
     */
    private Map<String, Boolean> updateMachines(Bundle b) {
        HashMap<String, Boolean> machines = new HashMap<>();
        String temp;

        // can be more elegant .. will take longer to design.
        temp = Transitions.TRANSITION_MACHINE_SHREDDER;
        machines.put(temp, b.getBoolean(temp));
        temp = Transitions.TRANSITION_MACHINE_INJECTION;
        machines.put(temp, b.getBoolean(temp));
        temp = Transitions.TRANSITION_MACHINE_EXTRUSION;
        machines.put(temp, b.getBoolean(temp));
        temp = Transitions.TRANSITION_MACHINE_COMPRESSION;
        machines.put(temp, b.getBoolean(temp));

        return machines;
    }

    /**
     * Retrieves user data from the bundle and initiates a new user creation procedure.
     *
     * @param b input bundle.
     */
    private void initiateCreation(Bundle b) {
        String email = b.getString(Transitions.TRANSITION_EMAIL);
        String password = b.getString(Transitions.TRANSITION_PASS);
        String nickname = b.getString(Transitions.TRANSITION_NICKNAME);
        boolean owner = b.getBoolean(Transitions.TRANSITION_OWNER);
        Map<String, Boolean> machines = null;
        if (owner) {
            machines = updateMachines(b);
        }
        createUser(email, password, nickname, machines);
    }

    /**
     * Creates a new user for authentication purposes & updates the users table in the db.
     *
     * @param email    desired user email.
     * @param password desired user password.
     */
    private void createUser(String email, String password, final String nickname, final Map<String, Boolean> ownerMachines) {
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
                            insertUser(user, nickname, ownerMachines);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoadingActivity.this, "Authentication failed.\n" + task.getException(),
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                        // ...
                    }
                });
    }

    /**
     * Signs out the current Firebase user.
     */
    private void signOut() {
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null) {
//            String nickname = user.getDisplayName();
//            String msg = "SignOut: %s signed out.";
//            mAuth.signOut();
//            Log.i(TAG, String.format(msg, nickname));
//        }
//        finish();
    }


}
