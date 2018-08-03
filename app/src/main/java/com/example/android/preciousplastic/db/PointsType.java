package com.example.android.preciousplastic.db;

public enum PointsType {
    TYPE_1,
    TYPE_2,
    TYPE_3,
    TYPE_4,
    TYPE_5,
    TYPE_6,
    TYPE_7,
    TYPE_8;

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
            case (8):
                return TYPE_8;
            default:
                return null;
        }
    }
}
