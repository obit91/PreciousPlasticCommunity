package com.example.android.preciousplastic.utils;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.example.android.preciousplastic.activities.HomeActivity;
import com.example.android.preciousplastic.activities.MapActivity;
import com.example.android.preciousplastic.activities.WorkspacesActivity;
import com.example.android.preciousplastic.db.DBConstants;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.fragments.WorkspaceAdaptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PPSession {

    private static User mUser;
    private static FirebaseAuth mAuth;

    private static FirebaseDatabase mFirebaseDB;
    private static DatabaseReference mUsersTable;
    private static DatabaseReference mHazardsTable;

    private static Context containerContext;
    private static HomeActivity homeActivity;

    private static MapActivity mapActivity;

    private static WorkspaceAdaptor workspaceAdaptor;
    private static WorkspacesActivity workspacesActivity;

    private static Fragment currentFragment;
    private static Class<? extends Fragment> currentFragmentClass;

    /**
     * Resets all of the session variables (except for DB objects).
     */
    public static void reset() {
        mUser = null;
        mAuth = null;
        containerContext = null;
        homeActivity = null;
        mapActivity = null;
        workspaceAdaptor = null;
        currentFragmentClass = null;
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

    public static Class<? extends Fragment> getCurrentFragmentClass() {
        return currentFragmentClass;
    }

    public static Fragment getCurrentFragment() {
        return currentFragment;
    }

    public static void setCurrentFragment(Fragment currentFragment) {
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
}
