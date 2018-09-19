package com.example.android.preciousplastic.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtils {

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

    /**
     * Creates a temp file from a bitmap.
     * @param chosenImage image to write to file.
     * @return the created temp file.
     */
    public static File generateTempFile(Context context, Bitmap chosenImage) {
        // create a new file
        File imageFile = new File(context.getCacheDir(), generateFileName());
        imageFile.deleteOnExit();
        bitmapToFile(chosenImage, imageFile);
        return imageFile;
    }

    public static File bitmapToFile(Bitmap bitmap, File outputFile) {
        //convert bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        //write the bytes in file
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(outputFile);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "generateTempFile: failed to create a temp file for gallery image.");
            return null;
        } catch (IOException e) {
            Log.e(TAG, "generateTempFile: failed to write image to temp file.");
            return null;
        }
        return outputFile;
    }

    /**
     * Generates a time-stamp based file name.
     * @return
     */
    @NonNull
    public static String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "JPEG_" + timeStamp + "_";
    }
}
