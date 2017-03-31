package com.example.anubhav.musicapp;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by anubhav on 27/3/17.
 */

public class AudioVisualize {
    private Context context;
    private float minScaleFactor = 0.50f;
    private float maxScaleFactor = 1.50f;
    private ImageView mainImage;
    private ImageView centerLogo;
    private LinearLayout mainContainer;
    private float mainImageHeight;
    private float mainImageWidth;
    private float logoHeight;
    private float logoWidth;
    private FrameLayout.LayoutParams logoParams;
    private FrameLayout.LayoutParams mainImageParams;
    private LinearLayout parentLayout;
    private FrameLayout rootLayout;
    private View view;

    public AudioVisualize(Context context, LinearLayout parentLayout, FrameLayout rootLayout) {
        this.context = context;
        this.parentLayout = parentLayout;
        this.rootLayout = rootLayout;
    }

    public void startVisualize(){
        /**
         * Initialize this when the button to record is clicked...
         */
        view = LayoutInflater.from(context).inflate(R.layout.visual_audio,null);
        mainImage = (ImageView) view.findViewById(R.id.mainRing);
        mainImageParams = (FrameLayout.LayoutParams) mainImage.getLayoutParams();
        BitmapDrawable mainImageDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.colored_ring);
        mainImageHeight = mainImageDrawable.getBitmap().getHeight()*minScaleFactor;
        mainImageWidth = mainImageDrawable.getBitmap().getWidth()*minScaleFactor;
        centerLogo = (ImageView) view.findViewById(R.id.imageViewAudioVisTopLayer);
        logoParams = (FrameLayout.LayoutParams) centerLogo.getLayoutParams();
        BitmapDrawable logoDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.center_visualize_logo);
        logoHeight = logoDrawable.getBitmap().getHeight()*minScaleFactor;
        logoWidth = logoDrawable.getBitmap().getWidth()*minScaleFactor;

        mainContainer = (LinearLayout) view.findViewById(R.id.mainVisualizeContainer);
        mainContainer.setMinimumHeight((int) (mainImageHeight*maxScaleFactor +20));

    }
    public void changeVisualization(boolean showLayout,float volume){

        if(showLayout){
            float changeFactor = minScaleFactor + volume;
            if(changeFactor > maxScaleFactor){
                changeFactor = maxScaleFactor;
            }
            mainImageParams.height = (int) (mainImageHeight*changeFactor);
            mainImageParams.width = (int) (mainImageWidth*changeFactor);
            mainImage.setLayoutParams(mainImageParams);

            logoParams.height = (int) (logoHeight*changeFactor);
            logoParams.width = (int) (logoWidth*changeFactor);
            centerLogo.setLayoutParams(logoParams);
            if(view.getParent()!=null){
                ((ViewGroup)view.getParent()).removeView(view);
            }
            parentLayout.addView(view,0);
        }else{
            parentLayout.removeView(view);
        }
        rootLayout.postInvalidate();
    }
}
