package com.example.android.preciousplastic.db.entities;

import com.example.android.preciousplastic.db.PointsType;
import com.example.android.preciousplastic.db.UserPoints;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    private String uid;
    private String email;
    private String nickname;
    long timeCreated;
    long lastLogin;
    private UserPoints points;

    /**
     * Default constructor required for calls to DataSnapshot.getValue(User.class)
     */
    public User() {

    }

    /**
     * Firebase registration constructor.
     * @param user firebase user.
     * @param nickname desired nickname.
     */
    public User(FirebaseUser user, String nickname) {
        this.uid = user.getUid();
        this.email = user.getEmail();
        this.timeCreated = System.nanoTime();
        this.lastLogin = System.nanoTime();
        this.nickname = nickname;
        this.points = new UserPoints();
    }

    /**
     * Generates a string of the user.
     */
    @Exclude
    public String toString(){
        return String.format("Nickname: %s\nEmail: %s\nLast Login: %s",
                nickname,
                email,
                String.valueOf(lastLogin));
    }

    /**
     * Creates a user map for db updates.
     * @return a map containing the user values.
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("email", email);
        result.put("nickname", nickname);
        result.put("timeCreated", timeCreated);
        result.put("lastLogin", lastLogin);
        result.put("points", points);

        return result;
    }

    /**
     * Adds points of a certain type.
     * @param type type to update.
     * @param value number of points to add.
     */
    public void addPoints(PointsType type, int value) {
        points.incrementType(type, value);
    }

    /**
     * Adds points of a certain type.
     * @param type type to update.
     * @param value number of points to remove.
     */
    public void removePoints(PointsType type, int value) {
        points.decrementType(type, value);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public UserPoints getPoints() {
        return points;
    }

    public void setPoints(UserPoints points) {
        this.points = points;
    }
}
