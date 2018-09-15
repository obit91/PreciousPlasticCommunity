package com.example.android.preciousplastic.imgur;

import com.example.android.preciousplastic.utils.ProgressRequestBody;

/**
 * An empty interface just to extends both ImgurAccessResponse & ProgressRequestBody.
 */
public interface UploadTask extends ImgurAccessResponse<ImgurData>, ProgressRequestBody.UploadCallbacks {

}
