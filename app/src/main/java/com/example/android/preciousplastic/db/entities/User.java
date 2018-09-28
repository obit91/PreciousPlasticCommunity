package com.example.android.preciousplastic.db.entities;

import com.example.android.preciousplastic.db.BazarOperations;
import com.example.android.preciousplastic.db.PointsType;
import com.example.android.preciousplastic.db.UserPoints;
import com.example.android.preciousplastic.db.UserRank;
import com.example.android.preciousplastic.db.Workspace;
import com.example.android.preciousplastic.db.repositories.UserRepository;
import com.example.android.preciousplastic.imgur.ImgurBazarItem;
import com.example.android.preciousplastic.utils.PPSession;
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
    private double timeCreated;
    private double lastLogin;
    private UserPoints points;
    private boolean owner;
    private Workspace workspace;
    private UserRank rank;

    /**
     * Default constructor required for calls to DataSnapshot.getValue(User.class)
     */
    public User() {

    }

    /**
     * Firebase registration constructor.
     *
     * @param user     firebase user.
     * @param nickname desired nickname.
     */
    public User(FirebaseUser user, String nickname, boolean owner) {
        this.uid = user.getUid();
        this.email = user.getEmail();
        this.timeCreated = System.nanoTime();
        this.lastLogin = System.nanoTime();
        this.nickname = nickname;
        this.points = new UserPoints();
        this.owner = owner;
        this.rank = UserRank.CARING_PERSON;

        if (owner) {
            this.workspace = new Workspace();
        }
    }

    /**
     * Generates a string of the user.
     */
    @Exclude
    public String toString() {
        return String.format("Nickname: %s\nEmail: %s\nLast Login: %s",
                nickname,
                email,
                String.valueOf(lastLogin));
    }

    /**
     * Creates a user map for db updates.
     *
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
        result.put("owner", owner);
        result.put("workspace", workspace);
        result.put("rank", rank);

        return result;
    }

    /**
     * Adds points of a certain type.
     *
     * @param type  type to update.
     * @param value number of points to remove.
     */
    public void removePoints(PointsType type, double value) {
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

    public double getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(double timeCreated) {
        this.timeCreated = timeCreated;
    }

    public double getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(double lastLogin) {
        this.lastLogin = lastLogin;
    }

    public UserPoints getPoints() {
        return points;
    }

    public void setPoints(UserPoints points) {
        this.points = points;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public UserRank getRank() {
        return rank;
    }

    public void setRank(UserRank rank) {
        this.rank = rank;
    }

    /**
     * Promotes a user to an owner account.
     */
    public void makeOwner() {
        if (!owner) {
            owner = true;
            workspace = new Workspace();
        }
    }

    /**
     * Checks whether the user is eligible for a promotion.
     */
    public void checkPromotion() {
        UserRank nextRank = rank;

        double totalPoints = points.getType1() + points.getType2() +
                             points.getType3() + points.getType4() +
                             points.getType5() + points.getType5() +
                             points.getType6() + points.getType7();

        while  (totalPoints >= nextRank.getRequiredExp() && !rank.isMaxRank()) {
            nextRank = rank.getNextRank();
            rank = nextRank;
        }
    }

    /**
     * Adds an item to the bazar.
     * @param bazarItem item we wish to sell.
     * @return true if the item was added, else - false.
     */
    public boolean addBazarItem(ImgurBazarItem bazarItem) {

        // naughty user
        if (!isOwner()) {
            return false;
        }

        bazarItem.setName(nickname);
        workspace.updateBazarItem(bazarItem, BazarOperations.ADD_ITEM);
        commitChanges();
        return true;
    }

    /**
     * Removes an item the user uploaded from the bazar.
     * @param imgurBazarItem item to remove.
     */
    public void removeBazarItem(ImgurBazarItem imgurBazarItem) {

        // naughty user
        if (!isOwner()) {
            return;
        }

        workspace.updateBazarItem(imgurBazarItem, BazarOperations.REMOVE_ITEM);
        commitChanges();
    }

    /**
     * Removes all items from the bazar.
     */
    public void removeAllItemsFromBazar() {
        workspace.updateBazarItem(null, BazarOperations.REMOVE_ALL);
        commitChanges();
    }

    /**
     * Updates current user in the fire-base db.
     */
    public void commitChanges() {
        UserRepository userRepository = new UserRepository(PPSession.getContainerContext());
        userRepository.updateUser(this, null);
    }

    public void updateLogin() {
        this.lastLogin = System.nanoTime();
    }
}
