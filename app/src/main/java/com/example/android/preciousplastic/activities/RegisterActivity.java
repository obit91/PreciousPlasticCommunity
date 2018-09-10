package com.example.android.preciousplastic.activities;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.utils.Transitions;
import com.example.android.preciousplastic.utils.ViewTools;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "REGISTER_ACTIVITY";

    private TextView emailTextView = null;
    private TextView passwordTextView = null;
    private TextView nicknameTextView = null;

    private SwitchCompat ownerSwitchButton = null;
    private CheckBox shredderCheckBox = null;
    private CheckBox injectionCheckBox = null;
    private CheckBox extrusionCheckBox = null;
    private CheckBox compressionCheckBox = null;

    Set<CheckBox> machines = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_register);

        // setting listeners
        Button registerButton = (Button) findViewById(R.id.register_btn_register);
        registerButton.setOnClickListener(this);

        // gui access
        emailTextView = (TextView) findViewById(R.id.register_text_email);
        passwordTextView = (TextView) findViewById(R.id.register_text_password);
        nicknameTextView = (TextView) findViewById(R.id.register_text_nickname);

        ownerSwitchButton = (SwitchCompat) findViewById(R.id.register_switch_btn_owner);
        shredderCheckBox = (CheckBox) findViewById(R.id.register_checkbox_shredder);
        injectionCheckBox = (CheckBox) findViewById(R.id.register_checkbox_injection);
        extrusionCheckBox = (CheckBox) findViewById(R.id.register_checkbox_extrusion);
        compressionCheckBox = (CheckBox) findViewById(R.id.register_checkbox_compression);
    }

    @Override
    public void onStart() {
        super.onStart();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        machines.add(shredderCheckBox);
        machines.add(injectionCheckBox);
        machines.add(extrusionCheckBox);
        machines.add(compressionCheckBox);

        disableMachines();

        ownerSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enableMachines();
                } else {
                    disableMachines();
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onRegisterClick(View view) {

        boolean invalidInput = false;
        StringBuilder msg = new StringBuilder();
        msg.append("The following fields are missing: ");

        if (ViewTools.isTextViewNull(emailTextView)) {
            msg.append("email");
            emailTextView.setHintTextColor(Color.RED);
            invalidInput = true;
        }

        if ( ViewTools.isTextViewNull(passwordTextView)) {
            msg.append("password").append("\n");
            passwordTextView.setHintTextColor(Color.RED);
            invalidInput = true;
        }

        if ( ViewTools.isTextViewNull(nicknameTextView)) {
            msg.append("nickname").append("\n");
            nicknameTextView.setHintTextColor(Color.RED);
            invalidInput = true;
        }

        if (invalidInput) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }

        // flush stringBuilder object.
        msg.setLength(0);

        if (ownerSwitchButton.isChecked()) {
            int checkedMachines = 0;
            for (CheckBox machine : machines) {
                if (machine.isChecked()) {
                    checkedMachines++;
                }
            }
            msg.append("At least one machine must be selected when choosing to become an owner.");
            if (checkedMachines == 0) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Transitions.TransitionTypes type = Transitions.TransitionTypes.REGISTER;

        Map<String, Serializable> args = new HashMap<>();

        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        String nickname = nicknameTextView.getText().toString();
        args.put(Transitions.TRANSITION_TYPE, type);
        args.put(Transitions.TRANSITION_EMAIL, email);
        args.put(Transitions.TRANSITION_PASS, password);
        args.put(Transitions.TRANSITION_NICKNAME, nickname);

        if (ownerSwitchButton.isChecked()) {
            args.put(Transitions.TRANSITION_OWNER, true);
            args.put(Transitions.TRANSITION_MACHINE_SHREDDER, shredderCheckBox.isChecked());
            args.put(Transitions.TRANSITION_MACHINE_INJECTION, injectionCheckBox.isChecked());
            args.put(Transitions.TRANSITION_MACHINE_EXTRUSION, extrusionCheckBox.isChecked());
            args.put(Transitions.TRANSITION_MACHINE_COMPRESSION, compressionCheckBox.isChecked());
        }

        finish();
        Transitions.activateTransition(this, args);
    }

    /**
     * Sets all machines invisible and resets their state.
     */
    private void disableMachines() {
        for (CheckBox machine : machines) {
            machine.setChecked(false);
            machine.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Sets all machines visible.
     */
    private void enableMachines() {
        for (CheckBox machine : machines) {
            machine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.register_btn_register):
                onRegisterClick(view);
                break;
        }
    }
}

