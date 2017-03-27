package com.example.anubhav.musicapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by anubhav on 16/2/17.
 */

public class StartingActivity extends AppCompatActivity {
    private TextView logoName;
    private ImageView mainLoader,mainImage;
    private List<Integer> images;
    private Drawable start,end;
    private Random random;
    private Resources res;
    private boolean running = true;
    private Context context;
    private static final String 		appString				= Constants.APP_NAME;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    /*    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Error"+Thread.currentThread().getStackTrace()[2],paramThrowable.getLocalizedMessage());
            }
        });*/
        setContentView(R.layout.activity_main);
        init();
        context = this;
        final Drawable backgrounds[] = new Drawable[2];
        res = getResources();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateImages(images);
                backgrounds[0] = mainImage.getDrawable();
                backgrounds[1] = end;
                TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
                crossfader.setCrossFadeEnabled(true);
                crossfader.startTransition(10000);
                mainImage.setImageDrawable(crossfader);
                if(running){
                    handler.postDelayed(this,10000);
                }
            }
        },10000);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"CAC_Champagne.ttf");
        logoName.setTypeface(typeface,Typeface.BOLD_ITALIC);
        GlideDrawableImageViewTarget glideDrawableImageViewTarget = new GlideDrawableImageViewTarget(mainLoader);
        Glide.with(this).load(R.raw.infinity1).into(glideDrawableImageViewTarget);
        mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartingActivity.this,DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void init() {
        random = new Random(System.currentTimeMillis());
        images = new ArrayList<>();
        images.add(R.drawable.main1c);
        images.add(R.drawable.main2c);
        images.add(R.drawable.main3c);
        logoName = (TextView)findViewById(R.id.logo_name);
        mainLoader = (ImageView)findViewById(R.id.main_loader);
        mainImage = (ImageView)findViewById(R.id.mainBackImage);
    }
    private void updateImages(List<Integer> images) {
        int pos = random.nextInt(images.size()-1);
        start = res.getDrawable(images.get(pos));
        if(pos == images.size()-1){
            end = res.getDrawable(images.get(0));
        }else{
            end = res.getDrawable(images.get(pos+1));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
    }
}
