package com.example.android.preciousplastic;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;

public class DBHandler {

    private final String DATABASE = "precious-plastic";
    private final String USERS_COLLECTION = "users";

    // constants for tasks performed with firebase
    public final static String AUTHENTICATION = "User Authentication";

    private Context context;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    public DbResponse dbResponseDelegate;

    /**
     * onDbResponse method will be called when response from db is received,
     * and then other activities can be notified.
     * It solves the problem of asynchronous db calls.
     */
    public interface DbResponse{
        void onDbResponse(String taskType, boolean response);
    }

    public DBHandler(Context context, DbResponse delegate){
        this.context = context;
        this.dbResponseDelegate = delegate;
        database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(DATABASE);
        usersRef = dbRef.child(USERS_COLLECTION);

        // Log updated events in db, users collection
        usersRef.addValueEventListener(new ValueEventListener() {
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
    public void insertUser(String firstName, String lastName, String nickName, String password,
                           String email, String city, String state, String country){
        DBUser dbUser = new DBUser(firstName, lastName, nickName, password, email, city, state, country);
        usersRef.child(dbUser.nickName).setValue(dbUser, new DatabaseReference.CompletionListener(){
            public void onComplete (DatabaseError error, DatabaseReference ref){
                String msg;
                if (error == null){
                    msg = "User inserted!";
                } else{
                    msg = "Error: " + error;
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                Log.d("Insert User", msg);
            }
        });
    }

    /**
     * Check if user exists in db under exact given nickName and password.
     * dbResponseDelegate.onDbResponse will be notified of authentication response.
     */
    public void authenticate(final String nickName, final String password){

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // first check if nickName exists in db
                if (dataSnapshot.child(nickName).getValue() != null) {

                    // next check that password in db matches given password
                    DatabaseReference passwordRef = usersRef.child(nickName).child("password");
                    passwordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            boolean authenticated = (snapshot.getValue(String.class).equals(password));
                            dbResponseDelegate.onDbResponse(AUTHENTICATION, authenticated);
                            updateLastLogin(nickName);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("Get Post Failed", databaseError.getMessage());
                        }
                    });
                } else{
                    // notify on failed authentication
                    dbResponseDelegate.onDbResponse(AUTHENTICATION, false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Get Post Failed", databaseError.getMessage());
            }
        });
    }

    private void updateLastLogin(String user){
        Timestamp loginTime = new Timestamp(System.currentTimeMillis());
        usersRef.child(user).child("lastLogin").setValue(loginTime, new DatabaseReference.CompletionListener(){
            public void onComplete (DatabaseError error, DatabaseReference ref){
                String msg;
                if (error == null){
                    msg = "updated successfully";
                } else{
                    msg = "Error: " + error;
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                Log.d("update login time", msg);
            }
        });
    }

    private static class DBUser {
        String firstName = "";
        String lastName = "";
        String nickName = "";  // nickName is unique identifier of user
        String password = "";
        String email = "";
        String city = "";
        String state = "";
        String country = "";
        ArrayList<Integer> points = new ArrayList<>(7);  // points per each plastic type 1-7
        boolean shopOwner = false;
        Timestamp timeCreated;
        Timestamp lastLogin;

        DBUser(String firstName, String lastName, String nickName, String password, String email, String city,
                      String state, String country){
            this.firstName = firstName;
            this.lastName = lastName;
            this.nickName = nickName;
            this.password = password;
            this.email = email;
            this.city = city;
            this.state = state;
            this.country = country;
            this.timeCreated = new Timestamp(System.currentTimeMillis());
            this.lastLogin = new Timestamp(System.currentTimeMillis());
        }

        public String toString(){
            String mask = "Name: %s %s\nNickname: %s\nEmail: %s\nLocation: %s, %s, %s\nLast Login: %s";
            return mask.format(firstName, lastName, nickName, email, city, state, country, String.valueOf(lastLogin));
        }

    }


}
