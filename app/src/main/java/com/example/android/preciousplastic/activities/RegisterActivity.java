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

    private SwitchCompat mOwnerSwitchButton = null;
    private CheckBox mShredderCheckBox = null;
    private CheckBox mInjectionCheckBox = null;
    private CheckBox mExtrusionCheckBox = null;
    private CheckBox mCompressionCheckBox = null;

    private Button mRegisterButton = null;

    Set<CheckBox> machines = new HashSet<>();

    Map<String, Boolean> availableUser;
    private static final String NICK_AVILABLE = "nickname_available";
    private static final String EMAIL_AVILABLE = "email_available";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_register);

        // setting listeners
        mRegisterButton = (Button) findViewById(R.id.register_btn_register);
        mRegisterButton.setOnClickListener(this);

        // gui access
        emailTextView = (TextView) findViewById(R.id.register_text_email);
        passwordTextView = (TextView) findViewById(R.id.register_text_password);
        nicknameTextView = (TextView) findViewById(R.id.register_text_nickname);

        mOwnerSwitchButton = (SwitchCompat) findViewById(R.id.register_switch_btn_owner);
        mShredderCheckBox = (CheckBox) findViewById(R.id.register_checkbox_shredder);
        mInjectionCheckBox = (CheckBox) findViewById(R.id.register_checkbox_injection);
        mExtrusionCheckBox = (CheckBox) findViewById(R.id.register_checkbox_extrusion);
        mCompressionCheckBox = (CheckBox) findViewById(R.id.register_checkbox_compression);
    }

    @Override
    public void onStart() {
        super.onStart();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        machines.add(mShredderCheckBox);
        machines.add(mInjectionCheckBox);
        machines.add(mExtrusionCheckBox);
        machines.add(mCompressionCheckBox);

        disableMachines();

        mOwnerSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

    /**
     * Updates whether the buttons are clickable or not.
     */
    private void setButtonsClickable(boolean isClickable) {
        mRegisterButton.setClickable(isClickable);
        mOwnerSwitchButton.setClickable(isClickable);
        mShredderCheckBox.setClickable(isClickable);
        mInjectionCheckBox.setClickable(isClickable);
        mExtrusionCheckBox.setClickable(isClickable);
        mCompressionCheckBox.setClickable(isClickable);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onRegisterClick(View view) {

        setButtonsClickable(false);

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
            setButtonsClickable(true);
            return;
        }

        // flush stringBuilder object.
        msg.setLength(0);

        if (mOwnerSwitchButton.isChecked()) {
            int checkedMachines = 0;
            for (CheckBox machine : machines) {
                if (machine.isChecked()) {
                    checkedMachines++;
                }
            }
            msg.append("At least one machine must be selected when choosing to become an owner.");
            if (checkedMachines == 0) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                setButtonsClickable(true);
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

        if (mOwnerSwitchButton.isChecked()) {
            args.put(Transitions.TRANSITION_OWNER, true);
            args.put(Transitions.TRANSITION_MACHINE_SHREDDER, mShredderCheckBox.isChecked());
            args.put(Transitions.TRANSITION_MACHINE_INJECTION, mInjectionCheckBox.isChecked());
            args.put(Transitions.TRANSITION_MACHINE_EXTRUSION, mExtrusionCheckBox.isChecked());
            args.put(Transitions.TRANSITION_MACHINE_COMPRESSION, mCompressionCheckBox.isChecked());
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
            setButtonsClickable(true);
            availableUser = new HashMap<>();
        }
        // we've parsed one element, awaiting next entry to the function.
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
                    setButtonsClickable(true);
                    Toast.makeText(getBaseContext(), "Nickname already in use", Toast.LENGTH_SHORT).show();
                    availableUser = new HashMap<>();
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
                    setButtonsClickable(true);
                    Toast.makeText(getBaseContext(), "Email already in use", Toast.LENGTH_SHORT).show();
                    availableUser = new HashMap<>();
                }
            } catch (ClassCastException e){
                onError(e.toString());
                Log.e(TAG, e.getMessage());
            }
        }
    }
}

