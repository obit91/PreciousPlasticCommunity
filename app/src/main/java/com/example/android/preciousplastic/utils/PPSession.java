package com.example.android.preciousplastic.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.example.android.preciousplastic.activities.BaseActivity;
import com.example.android.preciousplastic.adaptors.WorkspaceAdaptor;
import com.example.android.preciousplastic.fragments.BaseFragment;
import com.example.android.preciousplastic.utils.Transitions.TransitionTypes;

import com.example.android.preciousplastic.activities.HomeActivity;
import com.example.android.preciousplastic.activities.MainActivity;
import com.example.android.preciousplastic.activities.MapActivity;
import com.example.android.preciousplastic.activities.WorkspacesActivity;
import com.example.android.preciousplastic.db.DBConstants;
import com.example.android.preciousplastic.db.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Retrofit;

public class PPSession {

    private static final String TAG = "PPSession";

    public static final String currentIntentKey = "activeIntentKey";

    private static User mUser;
    private static FirebaseAuth mAuth;

    private static FirebaseDatabase mFirebaseDB;
    private static DatabaseReference mUsersTable;
    private static DatabaseReference mHazardsTable;

    private static Retrofit mRetrofit;

    private static BaseActivity mCurrentActivity;

    private static Context containerContext;
    private static HomeActivity homeActivity;
    private static MapActivity mapActivity;
    private static WorkspaceAdaptor workspaceAdaptor;
    private static WorkspacesActivity workspacesActivity;

    private static BaseFragment currentFragment;
    private static Class<? extends BaseFragment> currentFragmentClass;

    private static MainActivity mainActivity;

    private static boolean loggingIn;

    /**
     * Resets all of the session variables (except for DB objects).
     */
    public static void initSession(TransitionTypes type) {

        mUser = null;
        containerContext = null;
        homeActivity = null;
        mapActivity = null;
        workspaceAdaptor = null;
        currentFragment = null;
        currentFragmentClass = null;
        loggingIn = false;
        mRetrofit = null;

        if (mAuth != null && mAuth.getCurrentUser() != null) {
            String nickname = mAuth.getCurrentUser().getDisplayName();
            String msg = "SignOut: %s signed out.";
            mAuth.signOut();
            Log.i(TAG, String.format(msg, nickname));
        }
        mAuth = null;

        mainActivity.fireBaseAuthInit(type);
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static void setMainActivity(MainActivity mainActivity) {
        PPSession.mainActivity = mainActivity;
    }

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
        return mFirebaseDB;
    }

    public static void setFirebaseDB(FirebaseDatabase mFirebaseDB) {
        PPSession.mFirebaseDB = mFirebaseDB;
        mUsersTable = mFirebaseDB.getReference(DBConstants.USERS_COLLECTION);
        mHazardsTable = mFirebaseDB.getReference(DBConstants.HAZARDS_COLLECTIONS);

    }

    public static Class<? extends BaseFragment> getCurrentFragmentClass() {
        return currentFragmentClass;
    }

    public static BaseFragment getCurrentFragment() {
        return currentFragment;
    }

    public static void setCurrentFragment(BaseFragment currentFragment) {
        PPSession.currentFragment = currentFragment;
        PPSession.currentFragmentClass = currentFragment.getClass();
    }

    public static DatabaseReference getUsersTable() {
        return PPSession.mUsersTable;
    }

    public static DatabaseReference getHazardsTable() {
        return PPSession.mHazardsTable;
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

    public static void setMapActivity(MapActivity mapActivity){
        PPSession.mapActivity = mapActivity;
    }

    public static MapActivity getMapActivity() { return mapActivity; }

    public static void setWorkspacesActivity(WorkspacesActivity workspacesActivity){
        PPSession.workspacesActivity = workspacesActivity;
    }

    public static WorkspacesActivity getWorkspacesActivity(){ return workspacesActivity; }


    public static void setWorkspaceAdaptor(WorkspaceAdaptor workspaceAdaptor){
        PPSession.workspaceAdaptor = workspaceAdaptor;
    }

    public static WorkspaceAdaptor getWorkspaceAdaptor(){ return workspaceAdaptor; }

    public static String getEmail() {
        return mAuth.getCurrentUser().getEmail();
    }

    public static boolean isLoggingIn() {
        return loggingIn;
    }

    public static void setLoggingIn(boolean loggingIn) {
        PPSession.loggingIn = loggingIn;
    }

    public static Retrofit getRetrofit() {
        return mRetrofit;
    }

    public static void setRetrofit(Retrofit mRetrofit) {
        PPSession.mRetrofit = mRetrofit;
    }

    public static BaseActivity getCurrentActivity() {
        return mCurrentActivity;
    }

    public static void setCurrentActivity(BaseActivity mCurrentActivity) {
        PPSession.mCurrentActivity = mCurrentActivity;
    }

}
