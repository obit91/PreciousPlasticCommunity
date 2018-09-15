package com.example.android.preciousplastic.imgur;

import java.io.File;

public class ImgurRequestsGenerator {

    /**
     * Generates a post request.
     * @param delegate caller context.
     * @param imageFile file to upload.
     * @param title image title.
     * @param description image description.
     * @return an async post request.
     */
    public static ImgurAsyncGenericTask generatePOST(UploadTask delegate, File imageFile,
                                                     String title, String description) {
        ImgurAsyncPostImage asyncPostImage = new ImgurAsyncPostImage();
        asyncPostImage.delegate = delegate;
        asyncPostImage.imageFile = imageFile;
        asyncPostImage.title = title;
        asyncPostImage.description = description;
        return asyncPostImage;
    }

    /**
     *
     * @param delegate caller context.
     * @param imgurBazarItem item we wish to remove.
     * @return an async delete request.
     */
    public static ImgurAsyncGenericTask generateDEL(ImgurAccessResponse delegate,
                                                    ImgurBazarItem imgurBazarItem) {
        ImgurAsyncDeleteImage asyncDeleteImage = new ImgurAsyncDeleteImage();
        asyncDeleteImage.delegate = delegate;
        asyncDeleteImage.imgurBazarItem = imgurBazarItem;
        return asyncDeleteImage;
    }

}
