package com.example.anubhav.musicapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.anubhav.musicapp.Model.SongsModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class TestingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ImageView mainLoader;
    private FloatingActionButton fab;
    private LinearLayout swipingLayout,in;
    private RelativeLayout rootSwipeLayout;
    private ImageView notifLeft, notifRight;
    private int totalWidth;
    private ArrayList<SongsModel> songsModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_layout);
        swipingLayout = (LinearLayout) findViewById(R.id.swipeMain_Layout);
        rootSwipeLayout = (RelativeLayout) findViewById(R.id.swipeLayout);
        notifLeft = (ImageView) findViewById(R.id.deleteNotif_left);
        notifRight = (ImageView) findViewById(R.id.deleteNotif_right);

//        swipingLayout.setOnTouchListener(new Swipe(this){
//            @Override
//            public void onSwipeLeft(boolean isOverload) {
//                if(notifLeft.getVisibility() == View.VISIBLE && notifRight.getVisibility() == View.GONE){
//                    Fade fadeIn = new Fade(Fade.IN);
//                    fadeIn.addListener(new Transition.TransitionListener() {
//                        @Override
//                        public void onTransitionStart(Transition transition) {
//
//                        }
//
//                        @Override
//                        public void onTransitionEnd(Transition transition) {
//                            notifRight.setVisibility(View.VISIBLE);
//                            notifLeft.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onTransitionCancel(Transition transition) {
//
//                        }
//
//                        @Override
//                        public void onTransitionPause(Transition transition) {
//
//                        }
//
//                        @Override
//                        public void onTransitionResume(Transition transition) {
//
//                        }
//                    });
//                    TransitionManager.beginDelayedTransition(rootSwipeLayout,fadeIn);
//                    notifRight.setAlpha(1f);
//
//                }else if(notifRight.getVisibility() == View.GONE && notifLeft.getVisibility() == View.GONE){
//                   /* Fade fadeIn = new Fade(Fade.IN);
//                    TransitionManager.beginDelayedTransition(rootSwipeLayout,fadeIn);*/
//                    notifRight.setAlpha(1f);
//                    notifRight.setVisibility(View.VISIBLE);
//                }
//                if(isOverload){
//                    Fade fadeOut = new Fade(Fade.OUT);
//                    fadeOut.setDuration(5);
//                    fadeOut.addListener(new Transition.TransitionListener() {
//                        @Override
//                        public void onTransitionStart(Transition transition) {
//
//                        }
//
//                        @Override
//                        public void onTransitionEnd(Transition transition) {
//                            notifRight.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onTransitionCancel(Transition transition) {
//
//                        }
//
//                        @Override
//                        public void onTransitionPause(Transition transition) {
//
//                        }
//
//                        @Override
//                        public void onTransitionResume(Transition transition) {
//
//                        }
//                    });
//                    TransitionManager.beginDelayedTransition(rootSwipeLayout,fadeOut);
//                    notifRight.setAlpha(0.1f);
//                }
//            }
//
//            @Override
//            public void onSwipeRight(boolean isOverload) {
//                if(notifRight.getVisibility() == View.VISIBLE && notifLeft.getVisibility() == View.GONE){
//                    Fade fadeIn = new Fade(Fade.IN);
//                    fadeIn.addListener(new Transition.TransitionListener() {
//                        @Override
//                        public void onTransitionStart(Transition transition) {
//
//                        }
//
//                        @Override
//                        public void onTransitionEnd(Transition transition) {
//                            notifLeft.setVisibility(View.VISIBLE);
//                            notifRight.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onTransitionCancel(Transition transition) {
//
//                        }
//
//                        @Override
//                        public void onTransitionPause(Transition transition) {
//
//                        }
//
//                        @Override
//                        public void onTransitionResume(Transition transition) {
//
//                        }
//                    });
//                    TransitionManager.beginDelayedTransition(rootSwipeLayout,fadeIn);
//                    notifLeft.setAlpha(1f);
//
//                }else if(notifLeft.getVisibility() == View.GONE && notifRight.getVisibility() == View.GONE){
//                    /*Fade fadeIn = new Fade(Fade.IN);
//                    TransitionManager.beginDelayedTransition(rootSwipeLayout,fadeIn);*/
//                    notifLeft.setAlpha(1f);
//                    notifLeft.setVisibility(View.VISIBLE);
//
//                }
//                if(isOverload){
//                    Fade fadeOut = new Fade(Fade.OUT);
//                    fadeOut.setDuration(5);
//                    fadeOut.addListener(new Transition.TransitionListener() {
//                        @Override
//                        public void onTransitionStart(Transition transition) {
//
//                        }
//
//                        @Override
//                        public void onTransitionEnd(Transition transition) {
//                            notifLeft.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onTransitionCancel(Transition transition) {
//
//                        }
//
//                        @Override
//                        public void onTransitionPause(Transition transition) {
//
//                        }
//
//                        @Override
//                        public void onTransitionResume(Transition transition) {
//
//                        }
//                    });
//                    TransitionManager.beginDelayedTransition(rootSwipeLayout,fadeOut);
//                    notifLeft.setAlpha(0.1f);
//                }
//            }
//        });
        getSupportLoaderManager().initLoader(1,null,TestingActivity.this);

/*      Todo:Transition Code is here....
        final FrameLayout root = (FrameLayout)findViewById(R.id.root);
        final ChangeBounds autoTransition = new ChangeBounds();
        autoTransition.setDuration(4000);
        final FloatingActionButton fab = new FloatingActionButton(this);
        fab.setDrawingCacheBackgroundColor(getResources().getColor(R.color.button_loader_color));
        fab.setBackgroundTintMode(PorterDuff.Mode.MULTIPLY);
        fab.setBackgroundColor(getResources().getColor(R.color.button_loader_color));
        fab.setBackgroundTintList(new ColorStateList(new int[][]{{0}},new int[]{getResources().getColor(R.color.button_loader_color)}));
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(fab);
        Glide.with(this).load(R.raw.ripple).into(imageViewTarget);
        fab.setLayoutParams(root.getLayoutParams());

        final Button btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(root,autoTransition);
                btn.setVisibility(View.GONE);
                btn.setOnClickListener(null);
                root.addView(fab);
                root.invalidate();

            }
        });*/

        /*init();
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mainLoader);
        Glide.with(this).load(R.raw.infinity).into(imageViewTarget);*/
    }

    private void init() {
        mainLoader = (ImageView) findViewById(R.id.main_loader);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.fromFile(new File(Constants.MUSIC_SAVE_PATH));
        return new CursorLoader(this, uri,null,null,null,MediaStore.Audio.Media.TITLE+ " ASC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data!=null && data.moveToFirst()) {
            int songId = data.getColumnIndex(MediaStore.Audio.Media._ID);
            int albumId = data.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int albumName = data.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int songDuration = data.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int songTitle = data.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songTrack = data.getColumnIndex(MediaStore.Audio.Media.TRACK);
            int songArtist = data.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do {
                SongsModel songsModel = new SongsModel();
                songsModel.setSongId(String.valueOf(data.getLong(songId)));
                songsModel.setSongAlbumId(String.valueOf(data.getLong(albumId)));
                songsModel.setSongAlbumName(data.getString(albumName));
                songsModel.setSongDuration(String.valueOf(data.getLong(songDuration)));
                songsModelArrayList.add(songsModel);
            }while (data.moveToNext());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    class Swipe implements View.OnTouchListener{



        private GestureDetector gestureDetector;
        private int widthThreshhold = 200;
        private float initialX;
        private float initialY;
        private float intXPos;
        private float intYPos;
        private float distanceX;
        private float distanceY;


        public Swipe(Context context) {
            this.gestureDetector = new GestureDetector(context,new GestureListener());

        }

        class GestureListener extends GestureDetector.SimpleOnGestureListener{


            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if(Math.abs(distanceX) > Math.abs(distanceY) && (Math.abs(distanceX)>=0 && Math.abs(distanceX)<= rootSwipeLayout.getWidth()/4)){
                    if(distanceX > 0){
                        onSwipeRight(false);
                    }else{
                        onSwipeLeft(false);
                    }
                    return true;
                }
                return false;
            }
        }

        public void onSwipeLeft(boolean isOverload) {

        }

        public void onSwipeRight(boolean isOverload) {

        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    initialX = motionEvent.getRawX();
                    initialY = motionEvent.getRawY();
                    intXPos = view.getLeft();
                    intYPos = view.getRight();


                    return true;
                case MotionEvent.ACTION_UP:
                    if(Math.abs(distanceX) > Math.abs(distanceY) && (Math.abs(distanceX)>=rootSwipeLayout.getWidth()/20 && Math.abs(distanceX)<= rootSwipeLayout.getWidth()/3)) {
                        if(distanceX>0){
                            if(notifLeft.getVisibility()==View.VISIBLE){
                               swipingLayout.setTranslationX(notifLeft.getWidth()-100);

                            }
                        }else{
                            if(notifRight.getVisibility()==View.VISIBLE){
                                swipingLayout.setTranslationX(-(notifRight.getWidth()-100));
                            }
                        }

                    }else{
                        if(distanceX>0 && Math.abs(distanceX)>=rootSwipeLayout.getWidth() - 150)
                            swipingLayout.setX(rootSwipeLayout.getWidth()+100);
                        else
                            swipingLayout.setX(-(rootSwipeLayout.getWidth()+100));
                    }

                        return false;
                case MotionEvent.ACTION_MOVE:
                    distanceX = motionEvent.getRawX() - initialX;
                    distanceY = motionEvent.getRawY() - initialY;
                    if(Math.abs(distanceX) > Math.abs(distanceY) && (Math.abs(distanceX)>=rootSwipeLayout.getWidth()/20)) {
                        if (distanceX > 0) {
                            if(Math.abs(distanceX)>rootSwipeLayout.getWidth()/3 && Math.abs(distanceX)<= rootSwipeLayout.getWidth()){
                                onSwipeRight(true);
                            }else {
                                onSwipeRight(false);
                            }

                        }else {
                            if(Math.abs(distanceX)>rootSwipeLayout.getWidth()/3 && Math.abs(distanceX)<= rootSwipeLayout.getWidth()){
                                onSwipeLeft(true);
                            }else {
                                onSwipeLeft(false);
                            }
                        }
                        swipingLayout.setTranslationX(distanceX);
                        return true;
                    }
                    return true;
            }
            return false;
        }

    }
}
