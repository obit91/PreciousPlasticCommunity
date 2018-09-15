package com.example.android.preciousplastic.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCompressor {

    private static final String TAG = "IMAGE_COMPRESSOR";

    public static void saveScaledPic(String photoPath) {
        // Get the dimensions of the View
        int targetW = 200;
        int targetH = 250;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
//        mImageView.setImageBitmap(bitmap);

        String newPath = photoPath + "_icon";
        File f = new File(newPath);
        try {
            boolean test = f.createNewFile();
            if (!test) {
                Log.e(TAG, "scaleFile: failed to create new file.");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

            try (FileOutputStream fo = new FileOutputStream(newPath)) {
                fo.write(out.toByteArray());
                fo.close();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get64BaseImage (Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
