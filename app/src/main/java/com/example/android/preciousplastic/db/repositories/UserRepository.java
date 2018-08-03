package com.example.android.preciousplastic.db.repositories;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.android.preciousplastic.db.DBConstants;
import com.example.android.preciousplastic.db.entities.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;

public class UserRepository {

    private DatabaseReference mDatabase;
    private DatabaseReference mUsersTable;
    private Context mContext;

    public UserRepository(Context context) {
        mDatabase = FirebaseDatabase.getInstance().getReference(DBConstants.DATABASE);
        mUsersTable = mDatabase.child(DBConstants.USERS_COLLECTION);
        mContext = context;

        // Log updated events in db, users collection
        mUsersTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count " ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
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
     * Create new document in 'users' collection in database.
     * Document will have unique identifier of param nickName.
     */
    public void insertUser(FirebaseUser firebaseUser, String nickname) {
        User user = new User(firebaseUser, nickname);
        mUsersTable.child(user.getUid()).setValue(user, new DatabaseReference.CompletionListener(){
            public void onComplete (DatabaseError error, DatabaseReference ref){
                String msg;
                if (error == null){
                    msg = "User inserted!";
                } else{
                    msg = "Error: " + error;
                }
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                Log.d("Insert User", msg);
            }
        });
    }

    public void updateLastLogin(String uid){
        Timestamp loginTime = new Timestamp(System.currentTimeMillis());
        mUsersTable.child(uid).child("lastLogin").setValue(loginTime, new DatabaseReference.CompletionListener(){
            public void onComplete (DatabaseError error, DatabaseReference ref){
                String msg;
                if (error == null){
                    msg = "updated successfully";
                } else{
                    msg = "Error: " + error;
                }
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                Log.d("update login time", msg);
            }
        });
    }
}
