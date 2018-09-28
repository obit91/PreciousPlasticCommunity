package com.community.android.preciousplastic.imgur;

import android.graphics.Bitmap;

public class UploadNode {

    private Bitmap bitmap;
    private Double price;

    public UploadNode(Bitmap bitmap, Double price) {
        this.bitmap = bitmap;
        this.price = price;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
