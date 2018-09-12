package com.example.android.preciousplastic.imgur;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImgurAsyncPostImage extends AsyncTask<Void, Void, ImgurData> {

    private static final String TAG = "ASYNC_POST_IMGUR";

    public ImgurAccessResponse delegate = null;
    public File imageFile;
    public String title;
    public String description;

    @Override
    protected ImgurData doInBackground(Void... voids) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.imgur.com/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ImgurService service = retrofit.create(ImgurService.class);
//        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", imageFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), imageFile));
//        RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
//        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", null, reqFile);
//        Call<ImgurResponseData> method = service.uploadImage(filePart, title, description);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile.getAbsolutePath());
        Call<ImgurResponseData> method = service.uploadImage(requestBody, title, description);
        Response<ImgurResponseData> resp;
        ImgurResponseData respData = null;
        try {
            resp = method.execute();
            if (resp.isSuccessful())
                respData = resp.body();
            else {
                Log.e(TAG, "AsyncPost: " + resp.code());
                return null;
            }
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