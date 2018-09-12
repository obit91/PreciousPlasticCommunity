package com.example.android.preciousplastic.imgur;

public class ImgurResponseData {

    private ImgurData data = null;
    private boolean success = false;
    private String method = null;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

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