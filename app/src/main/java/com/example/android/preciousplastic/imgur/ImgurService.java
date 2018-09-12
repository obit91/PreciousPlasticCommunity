package com.example.android.preciousplastic.imgur;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ImgurService {

    @Headers(ImgurConstants.AUTHORIZATION_CLIENT_ID_HEADER)
    @GET("album/" + ImgurConstants.ALBUM)
    Call<ImgurResponseData> getAlbum();


    @Headers({
            ImgurConstants.AUTHORIZATION_CLIENT_ID_HEADER,
            ImgurConstants.CONTENT_TYPE_FORM_DATA
            })
    @Multipart
    @POST("upload?album=" + ImgurConstants.ALBUM)
    Call<ImgurResponseData> uploadImage(@Part("image") RequestBody filePart,
                                        @Query("title") String title,
                                        @Query("description") String description);
}