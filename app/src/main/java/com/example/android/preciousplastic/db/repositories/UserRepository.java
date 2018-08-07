package com.example.android.preciousplastic.db.repositories;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.android.preciousplastic.db.DBConstants;
import com.example.android.preciousplastic.db.PointsType;
import com.example.android.preciousplastic.db.UserPoints;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.utils.PPSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private static final String TAG = "USER_REPOSITORY";

    private DatabaseReference mUsersTable;
    private Context mContext;

    public UserRepository(Context context) {
        mUsersTable = PPSession.getFirebaseDB().getReference(DBConstants.USERS_COLLECTION);
        mContext = context;
    }

    /**
     * Listens for changes in the current user.
     */
    private void currentUserListener(String uid) {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get User object.
                PPSession.setUser(dataSnapshot.getValue(User.class));
                Log.i(TAG, "userListener: id - " + PPSession.getUid());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.e(TAG, "userListener:onCancelled", databaseError.toException());
            }
        };
        mUsersTable.child(uid).addValueEventListener(userListener);
    }

    /**
     * Create new document in 'users' collection in database.
     * Document will have unique identifier of param nickName.
     */
    public void insertUser(FirebaseUser firebaseUser, final String nickname, boolean owner) {
        User user = new User(firebaseUser, nickname, owner);
        mUsersTable.child(nickname).setValue(user, new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error == null) {
                    Log.i(TAG, "insertUser: created " + nickname);
                } else {
                    Log.e(TAG, "insertUser: " + error.getMessage());
                }
            }
        });
    }

    public void updateLastLogin(String uid) {
        currentUserListener(uid);
        mUsersTable.child(uid).child("lastLogin").setValue(System.nanoTime(), new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error == null) {
                    Log.i(TAG, "updateLastLogin: " + System.nanoTime());
                } else {
                    Log.e(TAG, "updateLastLogin: " + error.getMessage());
                }
            }
        });
    }

    /**
     * Commits the user fields to the db.
     *
     * @param user user to update.
     */
    public void updateUser(User user) {
        Map<String, Object> tablesToUpdate = new HashMap<>();
        tablesToUpdate.put(user.getNickname(), user.toMap());
        mUsersTable.updateChildren(tablesToUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "updateUser: incremented score");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "updateUser: " + e.getMessage());
                    }
                });
    }

    public void updateUserPoints(final String nickname, final PointsType type, final long score) {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                writeUserPoints(user, type, score);
                String msg = "updateUserPoints: <nick, type, score> = <%s><%s><%d>";
                Log.i(TAG, String.format(msg, nickname, type, score));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.e(TAG, "updateUserPoints:onCancelled", databaseError.toException());
            }
        };
        mUsersTable.child(nickname).addListenerForSingleValueEvent(userListener);
    }

    private void writeUserPoints(final User user, final PointsType type, final long score) {
        UserPoints userPoints = user.getPoints();
        userPoints.incrementType(type, score);
        updateUser(user);
        String message = "User %s: incremented %d points of %s";
        Toast.makeText(mContext,
                String.format(message, user.getNickname(), score, type),
                Toast.LENGTH_SHORT).show();
    }
}
