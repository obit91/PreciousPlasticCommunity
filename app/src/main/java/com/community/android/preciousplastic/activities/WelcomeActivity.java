package com.community.android.preciousplastic.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.community.android.preciousplastic.R;
import com.community.android.preciousplastic.services.ConnectivityMonitorService;
import com.community.android.preciousplastic.utils.Transitions;
import com.community.android.preciousplastic.utils.Transitions.TransitionTypes;
import com.community.android.preciousplastic.utils.ViewTools;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "WELCOME_ACTIVITY";

    private static boolean active = false;

    private TextView emailTextView = null;
    private TextView passwordTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initiates the connectivity monitor service
        Intent monitorConnectivityIntent = new Intent(this, ConnectivityMonitorService.class);
        startService(monitorConnectivityIntent);

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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (!ViewTools.isTextViewNull(emailTextView)) {
            savedInstanceState.putString("mail", emailTextView.getText().toString());
        }

        if (!ViewTools.isTextViewNull(passwordTextView)) {
            savedInstanceState.putString("pass", passwordTextView.getText().toString());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String mail = savedInstanceState.getString("mail");
        String pass = savedInstanceState.getString("pass");
        if (mail != null) {
            emailTextView.setText(mail);
        }
        if (pass != null) {
            passwordTextView.setText(pass);
        }
    }

    @Override
    public void onBackPressed() {
        // minimizes activity on back press
        moveTaskToBack(true);
    }

    public static boolean isActive() {
        return active;
    }

    public void onSignInClick(View view) {

        boolean invalidInput = false;
        StringBuilder msg = new StringBuilder();
        msg.append("The following fields are missing: ");

        if ( ViewTools.isTextViewNull(passwordTextView)) {
            msg.append("password").append("\n");
            passwordTextView.setHintTextColor(Color.RED);
            invalidInput = true;
        }
        if (ViewTools.isTextViewNull(emailTextView)) {
            msg.append("email").append("\n");
            emailTextView.setHintTextColor(Color.RED);
            invalidInput = true;
        }

        if (invalidInput) {
            Toast.makeText(this, msg.substring(0, msg.length() - 1), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Serializable> args = new HashMap<>();

        TransitionTypes type = TransitionTypes.SIGN_IN;

        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        args.put(Transitions.TRANSITION_TYPE, type);
        args.put(Transitions.TRANSITION_EMAIL, email);
        args.put(Transitions.TRANSITION_PASS, password);

        finish();
        Transitions.activateTransition(this, args);
    }

    /**
     * Go to registration screen.
     * @param view
     */
    public void onRegisterClick(View view) {
        Intent i = new Intent(this, RegisterActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
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

