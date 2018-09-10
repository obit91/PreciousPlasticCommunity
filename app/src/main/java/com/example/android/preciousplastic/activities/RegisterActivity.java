package com.example.android.preciousplastic.activities;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.db.EventNotifier;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.utils.Transitions;
import com.example.android.preciousplastic.utils.ViewTools;
import com.google.firebase.database.DataSnapshot;

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

    Map<String, Boolean> availableUser;
    private static final String NICK_AVILABLE = "nickname_available";
    private static final String EMAIL_AVILABLE = "email_available";

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

        availableUser = new HashMap<>();
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

        verifyNicknameAvailability(nicknameTextView.getText().toString());
        verifyEmailAvailability(emailTextView.getText().toString());
    }

    /**
     * Checks whether the nickname is available.
     * @param nickname nick to check.
     */
    private void verifyNicknameAvailability(String nickname) {
        UserRepository userRepository = new UserRepository(this);
        userRepository.isNicknameAvailable(nickname, new usersQueryNicknameEventNotifier());
    }

    /**
     * Checks whether the email is available.
     * @param email mail to check.
     */
    private void verifyEmailAvailability(String email) {
        UserRepository userRepository = new UserRepository(this);
        userRepository.isEmailAvailable(email, new usersQueryEmailEventNotifier());
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

    /**
     * The information entered is valid, register the user.
     */
    private void loadRegistrationTransition() {
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
     * Verifies that both email & nickname are available.
     */
    private void verifyUser() {
        if (availableUser.size() == 2) {
            if (availableUser.get(NICK_AVILABLE) && availableUser.get(EMAIL_AVILABLE)) {
                loadRegistrationTransition();
                return;
            }
            // reset the hashmap since we don't have a valid pair.
            availableUser = new HashMap<>();
        }
        // we've parsed one element.
        return;
    }

    /**
     * Notifies true if the nickname is available, else false.
     */
    private class usersQueryNicknameEventNotifier extends EventNotifier {

        @Override
        public void onResponse(Object dataSnapshotObj){
            DataSnapshot dataSnapshot;
            try {
                dataSnapshot = (DataSnapshot) dataSnapshotObj;
                if (dataSnapshot.getChildrenCount() == 0) {
                    availableUser.put(NICK_AVILABLE, true);
                    verifyUser();
                } else {
                    Toast.makeText(getBaseContext(), "Nickname already in use", Toast.LENGTH_SHORT).show();
                    availableUser.put(NICK_AVILABLE, false);
                }
            } catch (ClassCastException e){
                onError(e.toString());
                Log.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * Notifies true if the email is available, else false.
     */
    private class usersQueryEmailEventNotifier extends EventNotifier {

        @Override
        public void onResponse(Object dataSnapshotObj){
            DataSnapshot dataSnapshot;
            try {
                dataSnapshot = (DataSnapshot) dataSnapshotObj;
                if (dataSnapshot.getChildrenCount() == 0) {
                    availableUser.put(EMAIL_AVILABLE, true);
                    verifyUser();
                } else {
                    Toast.makeText(getBaseContext(), "Email already in use", Toast.LENGTH_SHORT).show();
                    availableUser.put(EMAIL_AVILABLE, false);
                }
            } catch (ClassCastException e){
                onError(e.toString());
                Log.e(TAG, e.getMessage());
            }
        }
    }
}

