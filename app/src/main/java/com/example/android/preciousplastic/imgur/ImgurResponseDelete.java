package com.example.android.preciousplastic.imgur;

public class ImgurResponseDelete extends ImgurResponseBasic {

    private Boolean data;

    @Override
    public Boolean getData() {
        return data;
    }

    public void setData(Boolean data) {
        this.data = data;
    }
}