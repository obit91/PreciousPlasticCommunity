package com.example.android.preciousplastic.db;

public class UserPoints {

    private int type1;
    private int type2;
    private int type3;
    private int type4;
    private int type5;
    private int type6;
    private int type7;
    private int type8;

    public UserPoints() {

    }

    public int getType1() {
        return type1;
    }

    public void setType1(int type1) {
        this.type1 = type1;
    }

    public int getType2() {
        return type2;
    }

    public void setType2(int type2) {
        this.type2 = type2;
    }

    public int getType3() {
        return type3;
    }

    public void setType3(int type3) {
        this.type3 = type3;
    }

    public int getType4() {
        return type4;
    }

    public void setType4(int type4) {
        this.type4 = type4;
    }

    public int getType5() {
        return type5;
    }

    public void setType5(int type5) {
        this.type5 = type5;
    }

    public int getType6() {
        return type6;
    }

    public void setType6(int type6) {
        this.type6 = type6;
    }

    public int getType7() {
        return type7;
    }

    public void setType7(int type7) {
        this.type7 = type7;
    }

    public int getType8() {
        return type8;
    }

    public void setType8(int type8) {
        this.type8 = type8;
    }

    /**
     * Increments user points by value.
     * @param type point type to update.
     * @param value value to add.
     */
    public void incrementType(PointsType type, int value) {
        updateType(type, value, false);
    }

    /**
     * Decrements user points by value.
     * @param type point type to update.
     * @param value value to add.
     */
    public void decrementType(PointsType type, int value) {
        updateType(type, value, true);
    }

    /**
     * Updates user points by value.
     * @param type point type to update.
     * @param value value to add.
     * @param subtraction true for subtractions, false for addition.
     */
    private void updateType(PointsType type, int value, boolean subtraction) {

        if (subtraction) {
            value *= -1;
        }

        switch (type) {
            case TYPE_1:
                type1 += value;
                break;
            case TYPE_2:
                type2 += value;
                break;
            case TYPE_3:
                type3 += value;
                break;
            case TYPE_4:
                type4 += value;
                break;
            case TYPE_5:
                type5 += value;
                break;
            case TYPE_6:
                type6 += value;
                break;
            case TYPE_7:
                type7 += value;
                break;
            case TYPE_8:
                type8 += value;
                break;
        }
    }
}