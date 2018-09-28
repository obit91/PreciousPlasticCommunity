package com.community.android.preciousplastic.imgur;

import com.community.android.preciousplastic.utils.PPSession;

import retrofit2.Call;
import retrofit2.Retrofit;

public class ImgurAsyncGetImage extends ImgurAsyncGenericTask<ImgurData> {

    ImgurAccessResponse<ImgurData> delegate = null;
    ImgurBazarItem imgurBazarItem;

    @Override
    protected Call<ImgurResponseImage> generateMethod() {
        final Retrofit retrofit = PPSession.getRetrofit();
        ImgurService service = retrofit.create(ImgurService.class);
        Call<ImgurResponseImage> method = service.getImage(imgurBazarItem.getId());
        return method;
    }

    @Override
    protected void onPostExecute(ImgurData result) {
        if(delegate != null)
        {
            delegate.getResult(result);
        }
    }
}