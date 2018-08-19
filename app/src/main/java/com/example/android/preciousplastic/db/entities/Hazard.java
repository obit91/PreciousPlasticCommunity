package com.example.android.preciousplastic.db.entities;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.osmdroid.util.GeoPoint;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Hazard {

    private String id;
    private String description;
    private String uid;
    private GeoPoint location;
    private boolean cleaned;

    /**
     * Default constructor required for calls to DataSnapshot.getValue(User.class)
     */
    public Hazard() { }

    /**
     * Firebase registration constructor.
     * @param user          firebase user.
     * @param id            unique hazard id in table.
     * @param location      hazard's location.
     * @param description   short description of hazard.
     */
    public Hazard(FirebaseUser user, String id, GeoPoint location, String description) {
        this.id = id;
        this.uid = user.getUid();
        this.location = location;
        this.description = description;
    }

    @Exclude
    public String toString() {
        return String.format("id: %s\nDescription: %s\nLocation: %s\nUser reported: %s\nCleaned:%s",
                id, description, String.valueOf(location), uid, cleaned);
    }

    /**
     * Creates a user map for db updates.
     * @return a map containing the user values.
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("uid", uid);
        result.put("location", location);
        result.put("description", description);
        result.put("cleaned", cleaned);
        return result;
    }

    public String getId() { return id; }

    public String getUid() {
        return uid;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getDescription() { return description; }

    public void setCleaned(boolean cleaned) {this.cleaned = cleaned; }

    public boolean getCleaned() {return cleaned; }

}
