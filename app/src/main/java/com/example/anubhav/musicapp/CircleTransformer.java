package com.example.anubhav.musicapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by anubhav on 24/4/17.
 */

public class CircleTransformer extends BitmapTransformation {
    public CircleTransformer(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool,toTransform);
    }
    private static Bitmap circleCrop(BitmapPool pool, Bitmap source){
        if (source == null) return null;

        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        //Create Squared Bitmap
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
        //Now, extract a bitmap of config ARGB_8888 and of squared size("size")
        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            //if pool does'nt have this bitmap, then create one.(width,height,config)
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }
        //Make a canvas of squared bitmap, which is empty, but of square shaped.
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        //overlay this canvas with the image, which we made in squared shape.
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        //Now draw circle from canvas(which would lie inside rectangular area
        canvas.drawCircle(r, r, r, paint);
        return result;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
