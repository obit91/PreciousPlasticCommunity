package com.example.android.preciousplastic.db.repositories;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.android.preciousplastic.db.DBConstants;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.session.Session;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private static final String TAG = "USER REPOSITORY";

    private DatabaseReference mUsersTable;
    private Context mContext;

    public UserRepository(Context context) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(DBConstants.DATABASE);
        mUsersTable = mDatabase.child(DBConstants.USERS_COLLECTION);
        mContext = context;

        // Log updated events in db, users collection
        mUsersTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count ", "" + snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Log.e("Get Post", postSnapshot.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Get Post Failed", databaseError.getMessage());
            }
        });
    }

    /**
     * Listens for changes in the current user.
     */
    private void currentUserListener(String uid) {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object.
                Session.setUser(dataSnapshot.getValue(User.class));
                Log.d(TAG, "ouserListener: id - " + Session.getUid());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.w(TAG, "userListener:onCancelled", databaseError.toException());
            }
        };
        mUsersTable.child(uid).addValueEventListener(userListener);
    }

    /**
     * Create new document in 'users' collection in database.
     * Document will have unique identifier of param nickName.
     */
    public void insertUser(FirebaseUser firebaseUser, String nickname) {
        User user = new User(firebaseUser, nickname);
        mUsersTable.child(user.getUid()).setValue(user, new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                String msg;
                if (error == null) {
                    msg = "User inserted!";
                } else {
                    msg = "Error: " + error;
                }
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                Log.d("Insert User", msg);
            }
        });
    }

    public void updateLastLogin(String uid) {
        mUsersTable.child(uid).child("lastLogin").setValue(System.nanoTime(), new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                String msg;
                if (error == null) {
                    msg = "updated successfully";
                } else {
                    msg = "Error: " + error;
                }
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                Log.d("update login time", msg);
            }
        });
        currentUserListener(uid);
    }

    /**
     * Commits the user fields to the db.
     *
     * @param user user to update.
     */
    public void updateUser(User user) {
        Map<String, Object> tablesToUpdate = new HashMap<>();
        tablesToUpdate.put(generateUidPath(user), user.toMap());
        mUsersTable.updateChildren(tablesToUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "updated user", Toast.LENGTH_SHORT).show();
                        Log.d("update user info", "updated user");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "failed to update user", Toast.LENGTH_SHORT).show();
                        Log.d("update user info", "failed to update user");
                    }
                });
    }

    /**
     * Generates a path to the uid of the user in the users table.
     *
     * @param user user to update.
     * @return path to use for updates.
     */
    private String generateUidPath(User user) {
        return String.format("/%s/ + %s",
                DBConstants.USERS_COLLECTION,
                user.getUid());
    }
}
