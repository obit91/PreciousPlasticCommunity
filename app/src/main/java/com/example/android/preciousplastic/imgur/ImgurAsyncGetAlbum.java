package com.example.android.preciousplastic.imgur;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImgurAsyncGetAlbum extends AsyncTask<Void, Void, ImgurData> {

    public ImgurAccessResponse delegate = null;

    @Override
    protected ImgurData doInBackground(Void... voids) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.imgur.com/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ImgurService service = retrofit.create(ImgurService.class);
        Call<ImgurResponseData> method = service.getAlbum();
        Response<ImgurResponseData> resp;
        ImgurResponseData respData = null;
        try {
            resp = method.execute();
            if (resp.isSuccessful())
                respData = resp.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respData.getData();
    }

    @Override
    protected void onPostExecute(ImgurData result) {
        if(delegate != null)
        {
            delegate.getResult(result);
        }
    }
}