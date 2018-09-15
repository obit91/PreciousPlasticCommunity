package com.example.android.preciousplastic.imgur;

import android.os.AsyncTask;

import com.example.android.preciousplastic.utils.PPSession;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ImgurAsyncGetAlbum extends AsyncTask<Void, Void, ImgurData> {

    public ImgurAccessResponse delegate = null;

    @Override
    protected ImgurData doInBackground(Void... voids) {
        final Retrofit retrofit = PPSession.getRetrofit();

        ImgurService service = retrofit.create(ImgurService.class);
        Call<ImgurResponseUpload> method = service.getImage("aCaX6AT");
        Response<ImgurResponseUpload> resp;
        ImgurResponseUpload respData = null;
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