package com.community.android.preciousplastic.db;

import com.community.android.preciousplastic.db.entities.User;
import com.community.android.preciousplastic.utils.PPSession;

public enum UserRank {
    CARING_PERSON(100, "Caring Person"),
    ENTHUSIASTIC(200, "Enthusiastic"),
    ROOKIE(300, "Rookie"),
    HANDYMAN(400, "Handyman"),
    TRAINEE(500, "Trainee"),
    APPRENTICE(600, "Apprentice"),
    YOUNG_GRASSHOPPER(700, "Young Grasshopper"),
    CRAFTSMAN(800, "Craftsman"),
    WIZARD(900, "Wizard"),
    ARTISAN(1000, "Artisan"),
    EXPERT(1100, "Expert"),
    MASTER(1200, "Master"),
    GRAND_MASTER(1300, "Grand Master"),
    MOTHER_EARTH(1400, "Mother Earth"),
    GAIA(1500, "Gaia Goddess");

    private final int requiredExp;
    private final String title;

    private static UserRank[] values = values();

    UserRank(int requiredExp, String title) {
        this.requiredExp = requiredExp;
        this.title = title;
    }

    public int getRequiredExp() {
        return requiredExp;
    }

    public String getTitle() {
        return title;
    }

    public UserRank getNextRank() {
        return values[this.ordinal() + 1];
    }

    public UserRank getPrevRank() {
        if (this.ordinal() == 0) {
            return this;
        } else {
            return values[this.ordinal() - 1];
        }
    }

    public boolean isMaxRank() {
        return this.ordinal() == values.length - 1;
    }
}
