package com.example.android.preciousplastic.session;

import com.example.android.preciousplastic.db.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class PPSession {

    private static User mUser;
    private static FirebaseAuth mAuth;
    private static String uid;
    private static FirebaseDatabase firebaseDB;

    public static void setUser(User user) {
        mUser = user;
        uid = user.getUid();
    }

    public static User currentUser() {
        return mUser;
    }

    public static void setFirebaseAuth(FirebaseAuth auth) {
        mAuth = auth;
    }

    public static FirebaseAuth getFirebaseAuth() {
        return mAuth;
    }

    public static String getUid() {
        return uid;
    }

    public static FirebaseDatabase getFirebaseDB() {
        return firebaseDB;
    }

    public static void setFirebaseDB(FirebaseDatabase firebaseDB) {
        PPSession.firebaseDB = firebaseDB;
    }
}
