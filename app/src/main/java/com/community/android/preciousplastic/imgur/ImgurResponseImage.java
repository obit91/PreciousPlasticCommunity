package com.community.android.preciousplastic.imgur;

public class ImgurResponseImage extends ImgurResponseBasic {

    private ImgurData data = null;
    protected String method = null;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ImgurData getData() {
        return data;
    }

    public void setData(ImgurData imgurData) {
        this.data = data;
    }
}