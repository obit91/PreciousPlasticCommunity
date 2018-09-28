package com.community.android.preciousplastic.utils;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public class EventNotifier {

    public void onResponse(Object response){
        Log.i("EventNotifier Parent", "Received");
    }
    public void onError(String response){
        Log.e("EventNotifier Error", response);
    }
}
