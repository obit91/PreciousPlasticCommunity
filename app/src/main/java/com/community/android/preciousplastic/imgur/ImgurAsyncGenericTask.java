package com.community.android.preciousplastic.imgur;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class ImgurAsyncGenericTask<T> extends AsyncTask<Void, Void, T>  {

    protected abstract Call generateMethod();
    protected static final String TAG = "IMGUR_ASYNC_TASK";

    public void execute() {
        super.execute();
    }

    @Override
    protected T doInBackground(Void... voids) {
        Call<ImgurResponseBasic> method = generateMethod();
        Response<ImgurResponseBasic> resp;
        ImgurResponseBasic<T> respData = null;
        try {
            resp = method.execute();
            if (resp.isSuccessful())
                respData = resp.body();
            else {
                Log.e(TAG, String.format("AsyncPost: %s - %d", resp.errorBody(), resp.code()));
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, String.format("AsyncPost: failed to execute request method %s",
                    method.request().body()));
            return null;
        }
        return respData.getData();
    }
}
