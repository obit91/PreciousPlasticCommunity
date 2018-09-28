package com.community.android.preciousplastic.db;

public enum PointsType {
    TYPE_1(1),
    TYPE_2(2),
    TYPE_3(3),
    TYPE_4(4),
    TYPE_5(5),
    TYPE_6(6),
    TYPE_7(7),
    TYPE_TOTAL(-1);

    // points worth by type
    private final double value;

    PointsType(double value) {
        this.value = value;
    }

    public double getPointValue() {
        return value;
    }

    public static PointsType getType(int type) {
        switch (type) {
            case (1):
                return TYPE_1;
            case (2):
                return TYPE_2;
            case (3):
                return TYPE_3;
            case (4):
                return TYPE_4;
            case (5):
                return TYPE_5;
            case (6):
                return TYPE_6;
            case (7):
                return TYPE_7;
            default:
                return null;
        }
    }
}
