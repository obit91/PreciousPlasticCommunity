package com.example.android.preciousplastic.db;

public class UserPoints {

    private long type1;
    private long type2;
    private long type3;
    private long type4;
    private long type5;
    private long type6;
    private long type7;
    private long type8;
    private long totalPoints;

    public UserPoints() {

    }

    public long getType1() {
        return type1;
    }

    public String getType1AsString() {
        return String.valueOf(type1);
    }

    public void setType1(long type1) {
        this.type1 = type1;
    }

    public long getType2() {
        return type2;
    }

    public String getType2AsString() {
        return String.valueOf(type2);
    }

    public void setType2(long type2) {
        this.type2 = type2;
    }

    public long getType3() {
        return type3;
    }

    public String getType3AsString() {
        return String.valueOf(type3);
    }

    public void setType3(long type3) {
        this.type3 = type3;
    }

    public long getType4() {
        return type4;
    }

    public String getType4AsString() {
        return String.valueOf(type4);
    }

    public void setType4(long type4) {
        this.type4 = type4;
    }

    public long getType5() {
        return type5;
    }

    public String getType5AsString() {
        return String.valueOf(type5);
    }

    public void setType5(long type5) {
        this.type5 = type5;
    }

    public long getType6() {
        return type6;
    }

    public String getType6AsString() {
        return String.valueOf(type6);
    }

    public void setType6(long type6) {
        this.type6 = type6;
    }

    public long getType7() {
        return type7;
    }

    public String getType7AsString() {
        return String.valueOf(type7);
    }

    public void setType7(long type7) {
        this.type7 = type7;
    }

    public long getType8() {
        return type8;
    }

    public String getType8AsString() {
        return String.valueOf(type8);
    }

    public void setType8(long type8) {
        this.type8 = type8;
    }

    public long getTotalPoints() {
        return totalPoints;
    }

    public String getTotalPointsAsString() {
        return String.valueOf(totalPoints);
    }

    /**
     * Increments user points by value.
     *
     * @param type  point type to update.
     * @param value value to add.
     */
    public void incrementType(PointsType type, long value) {
        updateType(type, value, false);
    }

    /**
     * Decrements user points by value.
     *
     * @param type  point type to update.
     * @param value value to add.
     */
    public void decrementType(PointsType type, long value) {
        updateType(type, value, true);
    }

    /**
     * Updates user points by value.
     *
     * @param type        point type to update.
     * @param value       value to add.
     * @param subtraction true for subtractions, false for addition.
     */
    private void updateType(PointsType type, long value, boolean subtraction) {

        if (subtraction) {
            value *= -1;
        }

        switch (type) {
            case TYPE_1:
                type1 += value;
                type1 = Math.max(type1, 0);
                break;
            case TYPE_2:
                type2 += value;
                type2 = Math.max(type2, 0);
                break;
            case TYPE_3:
                type3 += value;
                type3 = Math.max(type3, 0);
                break;
            case TYPE_4:
                type4 += value;
                type4 = Math.max(type4, 0);
                break;
            case TYPE_5:
                type5 += value;
                type5 = Math.max(type5, 0);
                break;
            case TYPE_6:
                type6 += value;
                type6 = Math.max(type6, 0);
                break;
            case TYPE_7:
                type7 += value;
                type7 = Math.max(type7, 0);
                break;
            case TYPE_8:
                type8 += value;
                type8 = Math.max(type8, 0);
                break;
        }
        totalPoints += value;
    }
}