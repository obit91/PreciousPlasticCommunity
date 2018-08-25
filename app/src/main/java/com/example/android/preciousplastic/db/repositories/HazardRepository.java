package com.example.android.preciousplastic.db.repositories;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.preciousplastic.db.EventNotifier;
import com.example.android.preciousplastic.db.entities.Hazard;
import com.example.android.preciousplastic.utils.PPSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.util.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class HazardRepository {

    private static final String TAG = "HAZARD_REPOSITORY";

    private Context mContext;

    public HazardRepository(Context context) {
        mContext = context;
    }

    /**
     * Commits the hazard fields to the db.
     * @param hazard hazard to update.
     */
    public void updateHazard(Hazard hazard) {
        Map<String, Object> tablesToUpdate = new HashMap<>();
        tablesToUpdate.put(hazard.getId(), hazard.toMap());
        PPSession.getHazardsTable().updateChildren(tablesToUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "updateHazard: success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "updateHazard: " + e.getMessage());
                    }
                });
    }

    /**
     * Create new document in 'hazards' collection in database.
     * Document will have unique identifier of param 'id'.
     */
    public void insertHazard(FirebaseUser firebaseUser, final GeoPoint location, final String desc) {
        DatabaseReference hazardsTable = PPSession.getHazardsTable();
        DatabaseReference newHazard = hazardsTable.push();
        final String hazardId = newHazard.getKey();
        final Hazard hazard = new Hazard(firebaseUser, hazardId, location, desc);
        newHazard.setValue(hazard, new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError error, DatabaseReference ref) {
            if (error == null) {
                Log.i(TAG, "insertHazard: created " + hazardId);
            } else {
                Log.e(TAG, "insertHazard: " + error.getMessage());
            }
            }
        });
    }

    /**
     * Read data from hazards table.
     * @param eventNotifier object to notify of query results.
     */
    public void getHazards(final EventNotifier eventNotifier){
        DatabaseReference hazardsTable = PPSession.getHazardsTable();
        hazardsTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventNotifier.onDataChange(dataSnapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                eventNotifier.onCancelled(databaseError);
            }
        });
    }
}
