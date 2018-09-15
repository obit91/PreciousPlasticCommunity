package com.example.android.preciousplastic.imgur;

import com.example.android.preciousplastic.utils.PPSession;

import retrofit2.Call;
import retrofit2.Retrofit;

public class ImgurAsyncDeleteImage extends ImgurAsyncGenericTask<Boolean> {

    ImgurAccessResponse<Boolean> delegate = null;
    ImgurBazarItem imgurBazarItem;

    @Override
    protected Call<ImgurResponseDelete> generateMethod() {
        final Retrofit retrofit = PPSession.getRetrofit();
        ImgurService service = retrofit.create(ImgurService.class);
        Call<ImgurResponseDelete> method = service.deleteImage(imgurBazarItem.getDeletehash());
        return method;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(delegate != null)
        {
            delegate.getResult(result);
        }
    }
}