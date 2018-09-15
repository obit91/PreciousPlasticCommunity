package com.example.android.preciousplastic.imgur;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ImgurService {

    @Headers(ImgurConstants.AUTHORIZATION_CLIENT_ID_HEADER)
    @GET("image/{id}")
    Call<ImgurResponseUpload> getImage(@Path("id") String id);

    @Headers({
            ImgurConstants.AUTHORIZATION_CLIENT_ID_HEADER,
    })
    @Multipart
    @POST("upload")
    Call<ImgurResponseUpload> uploadImage(@Part MultipartBody.Part file,
                                          @Query("title") String title,
                                          @Query("description") String description);

    @Headers(ImgurConstants.AUTHORIZATION_CLIENT_ID_HEADER)
    @DELETE("image/{deleteHash}")
    Call<ImgurResponseDelete> deleteImage(@Path("deleteHash") String deleteHash);
}