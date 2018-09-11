package com.example.android.preciousplastic.imgur;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImgurREST {

    private static final ImgurService imgurService = generateConnection();

    enum RequestTypes {
        GET_ALBUM,
        POST_IMAGE
    }

    private static ImgurService generateConnection() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.imgur.com/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ImgurService.class);
    }

    public static ImgurService getImgurService() {
        return  imgurService;
    }


}
