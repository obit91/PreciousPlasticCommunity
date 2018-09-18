package com.example.android.preciousplastic.db.repositories;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.preciousplastic.utils.EventNotifier;
import com.example.android.preciousplastic.db.PointsType;
import com.example.android.preciousplastic.db.UserPoints;
import com.example.android.preciousplastic.db.entities.User;
import com.example.android.preciousplastic.utils.PPSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private static final String TAG = "USER_REPOSITORY";

    private Context mContext;

    public UserRepository(Context context) {
        mContext = context;
    }

    /**
     * Updates the last login of the user.
     * @param nickname user that logged in.
     */
    public void updateLastLogin(String nickname) {
        PPSession.getUsersTable().child(nickname).child("lastLogin").setValue(System.nanoTime(), new DatabaseReference.CompletionListener() {
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
     * @param notifier
     */
    public void updateUser(User user, final EventNotifier notifier) {
        Map<String, Object> tablesToUpdate = new HashMap<>();
        tablesToUpdate.put(user.getNickname(), user.toMap());
        PPSession.getUsersTable().updateChildren(tablesToUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "updateUser: user updated.");
                        if (notifier != null) {
                            notifier.onResponse(true);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "updateUser: " + e.getMessage());
                        if (notifier != null) {
                            notifier.onResponse(false);
                        }
                    }
                });
    }

    public void updateUserPoints(final String nickname, final PointsType type, final double score, final EventNotifier notifier) {
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    notifier.onResponse(false);
                    return;
                }
                writeUserPoints(user, type, score, notifier);
                String msg = "updateUserPoints: <nick, type, score> = <%s><%s><%.2f>";
                Log.i(TAG, String.format(msg, nickname, type, score));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.e(TAG, "updateUserPoints:onFirebaseCancelled", databaseError.toException());
            }
        };
        PPSession.getUsersTable().child(nickname).addListenerForSingleValueEvent(userListener);
    }

    private void writeUserPoints(final User user, final PointsType type, final double score, EventNotifier notifier) {
        UserPoints userPoints = user.getPoints();
        userPoints.incrementType(type, score);
        user.checkPromotion();
        updateUser(user, notifier);
    }

    public void becomeOwner() {

        final String nickname = PPSession.getCurrentUser().getNickname();

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    Log.e(TAG, "becomeOwner: failed to retrieve current user.");
                } else {
                    user.makeOwner();
                    updateUser(user, null);
                    PPSession.setCurrentUser(user);
                    String msg = String.format("becomeOwner: %s has become an owner.", user.getNickname());
                    Log.i(TAG, String.format(msg, nickname));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting User failed, log a message
                Log.e(TAG, "becomeOwner:onFirebaseCancelled", databaseError.toException());
            }
        };
        PPSession.getUsersTable().child(nickname).addListenerForSingleValueEvent(userListener);
    }

    /**
     * Checks whether a nickname is already in use.
     * @param eventNotifier object to notify of query results.
     */
    public boolean isNicknameAvailable(String nickname, final EventNotifier eventNotifier){
        final DatabaseReference usersTable = PPSession.getUsersTable();
        Query query = usersTable.orderByChild("nickname").equalTo(nickname).limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventNotifier.onResponse(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                eventNotifier.onError(databaseError.getMessage());
            }
        });
        return true;
    }

    /**
     * Checks whether an email is already in use.
     * @param eventNotifier object to notify of query results.
     */
    public boolean isEmailAvailable(String email, final EventNotifier eventNotifier){
        final DatabaseReference usersTable = PPSession.getUsersTable();
        Query query = usersTable.orderByChild("email").equalTo(email).limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventNotifier.onResponse(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                eventNotifier.onError(databaseError.getMessage());
            }
        });
        return true;
    }
}
