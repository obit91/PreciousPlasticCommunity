package com.example.android.preciousplastic.db.entities;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    private String uid;
    private String email;
    private String nickname;
    private Timestamp timeCreated;
    private Timestamp lastLogin;

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
        this.timeCreated = new Timestamp(System.currentTimeMillis());
        this.lastLogin = new Timestamp(System.currentTimeMillis());
        this.nickname = nickname;
    }

    @Exclude
    public String toString(){
        return String.format("Nickname: %s\nEmail: %s\nLast Login: %s",
                nickname,
                email,
                String.valueOf(lastLogin));
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("email", email);
        result.put("nickname", nickname);
        result.put("timeCreated", timeCreated);
        result.put("lastLogin", lastLogin);

        return result;
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

    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
}
