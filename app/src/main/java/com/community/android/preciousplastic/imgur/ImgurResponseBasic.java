package com.community.android.preciousplastic.imgur;

public abstract class ImgurResponseBasic<T> {

    protected abstract T getData();

    protected boolean success;
    protected int status;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
