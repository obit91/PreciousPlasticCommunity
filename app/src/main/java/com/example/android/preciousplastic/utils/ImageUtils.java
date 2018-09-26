package com.example.android.preciousplastic.utils;

import android.content.Context;
import android.content.res.Resources;
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
        int compressRatio = 25;
        //convert bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressRatio, baos);


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
     * @return a timestamp based file name.
     */
    @NonNull
    public static String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "JPEG_" + timeStamp + "_";
    }

    /**
     * Calculates the image samples size based on the image dimensions.
     * @param options BitmapFactory options.
     * @param reqWidth width
     * @param reqHeight height
     * @return sample size.
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Returns a sampled bitmap from resources.
     * @param res resource to decode.
     * @param resId id of resource.
     * @param reqWidth width of the sampled image.
     * @param reqHeight height of the sampled image.
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
