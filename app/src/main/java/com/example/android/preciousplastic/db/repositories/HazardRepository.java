package com.example.android.preciousplastic.db.repositories;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.preciousplastic.db.entities.Hazard;
import com.example.android.preciousplastic.utils.PPSession;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

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
        final String hazardId = "0";  //todo SOMETHING
        final Hazard hazard = new Hazard(firebaseUser, hazardId, location, desc);
        DatabaseReference hazardsTable = PPSession.getHazardsTable();
        hazardsTable.child(hazardId).setValue(hazard, new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error == null) {
                    Log.i(TAG, "insertHazard: created " + hazardId);
                } else {
                    Log.e(TAG, "insertHazard: " + error.getMessage());
                }
            }
        });
    }
}
