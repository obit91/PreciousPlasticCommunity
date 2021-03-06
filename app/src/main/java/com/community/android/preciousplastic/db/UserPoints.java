package com.community.android.preciousplastic.db;

public class UserPoints {

    private double type1;
    private double type2;
    private double type3;
    private double type4;
    private double type5;
    private double type6;
    private double type7;
    private double totalPurchasePoints;

    public UserPoints() {

    }

    public double getType1() {
        return type1;
    }

    public void setType1(double type1) {
        this.type1 = type1;
    }

    public String getType1AsString() {
        return String.valueOf(type1);
    }

    public double getType2() {
        return type2;
    }

    public void setType2(double type2) {
        this.type2 = type2;
    }

    public String getType2AsString() {
        return String.valueOf(type2);
    }

    public double getType3() {
        return type3;
    }

    public void setType3(double type3) {
        this.type3 = type3;
    }

    public String getType3AsString() {
        return String.valueOf(type3);
    }

    public double getType4() {
        return type4;
    }

    public void setType4(double type4) {
        this.type4 = type4;
    }

    public String getType4AsString() {
        return String.valueOf(type4);
    }

    public double getType5() {
        return type5;
    }

    public void setType5(double type5) {
        this.type5 = type5;
    }

    public String getType5AsString() {
        return String.valueOf(type5);
    }

    public double getType6() {
        return type6;
    }

    public void setType6(double type6) {
        this.type6 = type6;
    }

    public String getType6AsString() {
        return String.valueOf(type6);
    }

    public double getType7() {
        return type7;
    }

    public void setType7(double type7) {
        this.type7 = type7;
    }

    public String getType7AsString() {
        return String.valueOf(type7);
    }

    public double getTotalPurchasePoints() {
        return totalPurchasePoints;
    }

    public void setTotalPurchasePoints(double totalPurchasePoints) {
        this.totalPurchasePoints = totalPurchasePoints;
    }

    public String getTotalPurchasePointsAsString() {
        return String.valueOf(totalPurchasePoints);
    }



    public double getPointsSum() {
        return type1 + type2 + type3 + type4 + type5 + type6 + type7;
    }

    /**
     * Increments user points by value.
     *
     * @param type  point type to update.
     * @param value value to add.
     */
    public void incrementType(PointsType type, double value) {
        updateType(type, value, false);
    }

    /**
     * Decrements user points by value.
     *
     * @param type  point type to update.
     * @param value value to add.
     */
    public void decrementType(PointsType type, double value) {
        updateType(type, value, true);
    }

    /**
     * Updates user points by value.
     *
     * @param type        point type to update.
     * @param value       value to add.
     * @param subtraction true for subtractions, false for addition.
     */
    private void updateType(PointsType type, double value, boolean subtraction) {

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
            default:
                break;
        }
        totalPurchasePoints += value;
        totalPurchasePoints = Math.max(totalPurchasePoints, 0);
    }
}