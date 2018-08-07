package com.example.android.preciousplastic.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.android.preciousplastic.R;
import com.example.android.preciousplastic.utils.PPSession;

import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;

public class PPSimpleFastPointOverlayActivity extends SimpleFastPointOverlay {

    public PPSimpleFastPointOverlayActivity(PointAdapter pointList, SimpleFastPointOverlayOptions style) {
        super(pointList, style);
    }
    public PPSimpleFastPointOverlayActivity(PointAdapter pointList) {
        super(pointList);
    }
    protected void drawPointAt(Canvas canvas, float x, float y, boolean showLabel, String label, Paint pointStyle, Paint textStyle){
        BitmapFactory.Options opt =  new BitmapFactory.Options();
        opt.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeResource(PPSession.getContainerContext().getResources(), R.drawable.precious_plastic_logo_small, opt);
        canvas.drawBitmap(bitmap, x, y, pointStyle);
    }

}
