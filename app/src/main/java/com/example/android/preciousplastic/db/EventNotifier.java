package com.example.android.preciousplastic.db;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class EventNotifier {

    public void onDataChange(DataSnapshot dataSnapshot){
        Log.i("EventNotifier data", "Received");
    }

    public void onCancelled(DatabaseError databaseError){
        Log.e("EventNotifier error", databaseError.toString());
    }

}
