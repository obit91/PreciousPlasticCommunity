package com.community.android.preciousplastic.db.entities;

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
    private String repoter;
    private GeoPoint location;
    private double lng;
    private double lat;
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
        this.repoter = user.getDisplayName();
        this.location = location;
        this.lng = location.getLongitude();
        this.lat = location.getLatitude();
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
        result.put("reporter", repoter);
        result.put("lng", lng);
        result.put("lat", lat);
        result.put("description", description);
        result.put("cleaned", cleaned);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRepoter() {
        return repoter;
    }

    public void setRepoter(String repoter) {
        this.repoter = repoter;
    }

    public boolean isCleaned() {
        return cleaned;
    }

    public void setCleaned(boolean cleaned) {
        this.cleaned = cleaned;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }


}
