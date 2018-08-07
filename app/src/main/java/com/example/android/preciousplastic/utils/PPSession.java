package com.example.android.preciousplastic.utils;

import android.content.Context;

import com.example.android.preciousplastic.activities.HomeActivity;
import com.example.android.preciousplastic.db.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class PPSession {

    private static User mUser;
    private static FirebaseAuth mAuth;
    private static FirebaseDatabase firebaseDB;
    private static Context containerContext;
    private static HomeActivity homeActivity;

    public static void setCurrentUser(User user) {
        mUser = user;
    }

    public static User getCurrentUser() {
        return mUser;
    }

    public static void setFirebaseAuth(FirebaseAuth auth) {
        mAuth = auth;
    }

    public static FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    public static FirebaseDatabase getFirebaseDB() {
        return firebaseDB;
    }

    public static void setFirebaseDB(FirebaseDatabase firebaseDB) {
        PPSession.firebaseDB = firebaseDB;
    }

    public static Context getContainerContext() {
        return containerContext;
    }

    public static HomeActivity getHomeActivity() {
        return homeActivity;
    }

    public static void setContainerContext(HomeActivity containerContext) {
        PPSession.homeActivity = containerContext;
        PPSession.containerContext = containerContext;
    }

    public static String getEmail() {
        return mAuth.getCurrentUser().getEmail();
    }
}
