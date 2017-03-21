package com.example.anubhav.musicapp.GNSDKComp;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableRow;

import com.example.anubhav.musicapp.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by anubhav on 21/3/17.
 */

public class AudioVisualDisplay {

    private Activity activity;
    private ViewGroup displayContainer;
    private ViewGroup parentContainer;
    private View view;
    ImageView bottomDisplayImageView;
    ImageView 							topDisplayImageView;
    private int							displayIndex;
    private float						zeroScaleFactor = 0.50f;
    private float						maxScaleFactor = 1.50f;
    private int							currentPercent = 50;
    private boolean						isDisplayed = false;
    private final int					zeroDelay = 150; // in milliseconds

    Timer zeroTimer;

    private FrameLayout.LayoutParams	bottomDisplayLayoutParams;
    private int							bottomDisplayImageHeight;
    private int							bottomDisplayImageWidth;
    private FrameLayout.LayoutParams 	topDisplayLayoutParams;
    private int							topDisplayImageHeight;
    private int							topDisplayImageWidth;

    public AudioVisualDisplay( Activity activity, ViewGroup displayContainer, int displayIndex ,ViewGroup parent) {
        this.activity = activity;
        this.displayContainer = displayContainer;
        this.displayIndex = displayIndex;
        this.parentContainer = parent;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.visual_audio,null);

        // bottom layer
        bottomDisplayImageView = (ImageView)view.findViewById(R.id.imageViewAudioVisBottomLayer);
        bottomDisplayLayoutParams = (FrameLayout.LayoutParams)bottomDisplayImageView.getLayoutParams();
        BitmapDrawable bd=(BitmapDrawable) activity.getResources().getDrawable(R.drawable.colored_ring);
        bottomDisplayImageHeight=(int)((float)bd.getBitmap().getHeight() * zeroScaleFactor);
        bottomDisplayImageWidth=(int)((float)bd.getBitmap().getWidth() * zeroScaleFactor);

        // top layer
        topDisplayImageView = (ImageView)view.findViewById(R.id.imageViewAudioVisTopLayer);
        topDisplayLayoutParams = (FrameLayout.LayoutParams)topDisplayImageView.getLayoutParams();
        bd=(BitmapDrawable) activity.getResources().getDrawable(R.drawable.gracenote_logo);
        topDisplayImageHeight=(int)((float)bd.getBitmap().getHeight() * zeroScaleFactor);
        topDisplayImageWidth=(int)((float)bd.getBitmap().getWidth() * zeroScaleFactor);

        // set the size of the visualization image view container large enough to hold vis image
        TableRow tableRow = (TableRow)view.findViewById(R.id.tableRowVisImageContainer);
        tableRow.setMinimumHeight((int)(((float)bottomDisplayImageHeight * maxScaleFactor)) + 20); // room for scaling plus some padding
        tableRow.setGravity(Gravity.CENTER);
//        setDisplay(true,false);
    }

    // display or hide the visualization view in the display container provided during construction
    public void setDisplay( boolean display, boolean doOnMainThread ){

        // why do we set amplitude percentage here?
        // when we display the visualizer image we want it scaled, but setting the layout parameters
        // in the constructor doesn't nothing, so we call it hear to prevent it showing up full size and
        // then scaling a fraction of a second later when the first amplitude percent change
        // comes in
        if ( display ){
            SetDisplayAmplitudeRunnable setDisplayAmplitudeRunnable = new SetDisplayAmplitudeRunnable(currentPercent);
            if ( doOnMainThread ){
                activity.runOnUiThread(setDisplayAmplitudeRunnable);
            }else{
                setDisplayAmplitudeRunnable.run();
            }
        }

        SetDisplayRunnable setDisplayRunnable = new SetDisplayRunnable(display);
        if ( doOnMainThread ){
            activity.runOnUiThread(setDisplayRunnable);
        }else{
            setDisplayRunnable.run();
        }

        parentContainer.postInvalidate();
    }

    void setAmplitudePercent( int amplitudePercent, boolean doOnMainThread ){
        if ( isDisplayed && (currentPercent != amplitudePercent)){
            SetDisplayAmplitudeRunnable setDisplayAmplitudeRunnable = new SetDisplayAmplitudeRunnable(amplitudePercent);
            if ( doOnMainThread ){
                activity.runOnUiThread(setDisplayAmplitudeRunnable);
            }else{
                setDisplayAmplitudeRunnable.run();
            }
            currentPercent = amplitudePercent;

            // zeroing timer - cancel if we got a new amplitude before
            try {
                if ( zeroTimer != null ) {
                    zeroTimer.cancel();
                    zeroTimer = null;
                }
                zeroTimer = new Timer();
                zeroTimer.schedule( new ZeroTimerTask(),zeroDelay);
            } catch (IllegalStateException e){
            }
        }
    }

    int displayHeight(){
        return bottomDisplayImageHeight;
    }

    int displayWidth(){
        return bottomDisplayImageWidth;
    }

    class SetDisplayRunnable implements Runnable {
        boolean display;

        SetDisplayRunnable(boolean display){
            this.display = display;
        }

        @Override
        public void run() {
            if ( isDisplayed && (display == false) ) {
                displayContainer.removeViewAt( displayIndex );
                isDisplayed = false;
            } else if ( isDisplayed == false ) {
                displayContainer.addView(view, displayIndex);
                isDisplayed = true;
            }

        }
    }

    class SetDisplayAmplitudeRunnable implements Runnable {
        int percent;

        SetDisplayAmplitudeRunnable(int percent){
            this.percent = percent;
        }

        @Override
        public void run() {
            float scaleFactor = zeroScaleFactor + ((float)percent/100); // zero position plus audio wave amplitude percent
            if ( scaleFactor > maxScaleFactor )
                scaleFactor = maxScaleFactor;
            bottomDisplayLayoutParams.height = (int)((float)bottomDisplayImageHeight * scaleFactor);
            bottomDisplayLayoutParams.width = (int)((float)bottomDisplayImageWidth * scaleFactor);
            bottomDisplayImageView.setLayoutParams( bottomDisplayLayoutParams );

            topDisplayLayoutParams.height = (int)((float)topDisplayImageHeight * zeroScaleFactor);
            topDisplayLayoutParams.width = (int)((float)topDisplayImageWidth * zeroScaleFactor);
            topDisplayImageView.setLayoutParams( topDisplayLayoutParams );
        }
    }

    class ZeroTimerTask extends TimerTask {

        @Override
        public void run() {
            zeroTimer = null;
            setAmplitudePercent(0,true);
        }

    }

}
