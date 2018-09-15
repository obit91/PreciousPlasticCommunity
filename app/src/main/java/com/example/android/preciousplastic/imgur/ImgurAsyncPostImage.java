package com.example.android.preciousplastic.imgur;

import android.util.Log;

import com.example.android.preciousplastic.utils.PPSession;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class ImgurAsyncPostImage extends ImgurAsyncGenericTask<ImgurData> {

    ImgurAccessResponse<ImgurData> delegate = null;
    File imageFile;
    String title;
    String description;

    @Override
    protected Call<ImgurResponseUpload> generateMethod() {
        final Retrofit retrofit = PPSession.getRetrofit();
        ImgurService service = retrofit.create(ImgurService.class);
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", null, reqFile);
        Call<ImgurResponseUpload> method = service.uploadImage(body, title, description);
        return method;
    }

    @Override
    protected void onPostExecute(ImgurData result) {
        if(delegate != null)
        {
            delegate.getResult(result);
        }
        final boolean delete = imageFile.delete();
        if (delete) {
            Log.d(TAG, "PostBackground: deleted temp image file.");
        } else {
            Log.e(TAG, "PostBackground: failed to delete temp image file.");
        }
    }
}