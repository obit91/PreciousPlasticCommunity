package com.example.android.preciousplastic.utils;

import android.app.Activity;
import android.content.Intent;

import com.example.android.preciousplastic.activities.LoadingActivity;

import java.io.Serializable;
import java.util.Map;

public class Transitions {

    public enum TransitionTypes {
        REGISTER,
        SIGN_IN,
        AUTO_SIGN_IN,
        SIGN_OUT
    }

    public static final String TRANSITION_TYPE = "type";
    public static final String TRANSITION_EMAIL = "email";
    public static final String TRANSITION_PASS = "pass";
    public static final String TRANSITION_NICKNAME = "nick";
    public static final String TRANSITION_OWNER = "owner";
    public static final String TRANSITION_MACHINE_SHREDDER = "shredder";
    public static final String TRANSITION_MACHINE_INJECTION = "injection";
    public static final String TRANSITION_MACHINE_EXTRUSION = "extrusion";
    public static final String TRANSITION_MACHINE_COMPRESSION = "compression";

    /**
     * Activates a fun splash screen for loading.
     * @param caller Activity requesting the transition.
     * @param args A map containing arguments and values for the transition activity.
     *             The map must contain a transition type.
     */
    public static void activateTransition(Activity caller, Map<String, Serializable> args) {
        Intent i = new Intent(caller, LoadingActivity.class);
        for (String arg : args.keySet()) {
            i.putExtra(arg, args.get(arg));
        }
        caller.startActivity(i);
    }

}
