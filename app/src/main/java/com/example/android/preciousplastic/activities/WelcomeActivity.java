package com.example.android.preciousplastic.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.utils.Transitions;
import com.example.android.preciousplastic.utils.Transitions.TransitionTypes;
import com.example.android.preciousplastic.utils.ViewTools;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "WELCOME_ACTIVITY";

    private static boolean active = false;

    private TextView emailTextView = null;
    private TextView passwordTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_welcome);

        // setting listeners
        Button registerButton = (Button) findViewById(R.id.welcome_btn_register);
        Button signInButton = (Button) findViewById(R.id.welcome_btn_log_in);
        registerButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);

        // gui access
        emailTextView = (TextView) findViewById(R.id.welcome_text_email);
        passwordTextView = (TextView) findViewById(R.id.welcome_text_password);
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    public static boolean isActive() {
        return active;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "lol no turning back", Toast.LENGTH_SHORT).show();
    }

    public void onSignInClick(View view) {
        TransitionTypes type = TransitionTypes.SIGN_IN;
        activateTransition(type);
    }

    /**
     * Activates a fun splash screen for loading.
     * @param type transition type.
     */
    private void activateTransition(TransitionTypes type) {
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        String nickname = email.substring(0, email.indexOf("@"));

        Intent i = new Intent(this, LoadingActivity.class);
        i.putExtra(Transitions.TRANSITION_TYPE, type);
        i.putExtra(Transitions.TRANSITION_EMAIL, email);
        i.putExtra(Transitions.TRANSITION_PASS, password);
        i.putExtra(Transitions.TRANSITION_NICKNAME, nickname);
        startActivity(i);
    }

    public void onRegisterClick(View view) {

        boolean invalidInput = false;

        if ( ViewTools.isTextViewNull(passwordTextView)) {
            Toast.makeText(this, "Password field missing.", Toast.LENGTH_SHORT).show();
            passwordTextView.setHighlightColor(Color.red(1));
            invalidInput = true;
        }
        if (ViewTools.isTextViewNull(emailTextView)) {
            Toast.makeText(this, "Email field missing.", Toast.LENGTH_SHORT).show();
            emailTextView.setHighlightColor(Color.red(1));
            invalidInput = true;
        }

        if (invalidInput) {
            return;
        }
        
        TransitionTypes type = TransitionTypes.REGISTER;
        activateTransition(type);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.welcome_btn_register):
                onRegisterClick(view);
                break;
            case (R.id.welcome_btn_log_in):
                onSignInClick(view);
                break;
        }
    }
}

