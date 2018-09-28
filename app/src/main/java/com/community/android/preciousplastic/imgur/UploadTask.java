package com.community.android.preciousplastic.imgur;

import com.community.android.preciousplastic.utils.ProgressRequestBody;

/**
 * An empty interface just to extends both ImgurAccessResponse & ProgressRequestBody.
 */
public interface UploadTask extends ImgurAccessResponse<ImgurData>, ProgressRequestBody.UploadCallbacks {

}
