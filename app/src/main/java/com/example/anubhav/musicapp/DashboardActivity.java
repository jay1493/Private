package com.example.anubhav.musicapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.anubhav.musicapp.Fragments.FragMusic;
import com.example.anubhav.musicapp.Fragments.FragMusicSearch;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.IOException;

/**
 * Created by anubhav on 19/2/17.
 */

public class DashboardActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, View.OnClickListener, TextView.OnEditorActionListener {

    public static final String MUSICFRAGSERACH = "MUSICFRAGSERACH";
    public static final String MUSICFRAG = "MUSICFRAG";
    private static final String songFetchKey = "SongKey";
    private SurfaceView videoView;
    private android.widget.MediaController mediaController;
    private ImageView videoLoader;
    private Toolbar toolbar;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private EditText etSearchSong;
    private LinearLayout mainLayout;
    private FragMusic fragMusic;
    private FragmentManager fragmentManager;
    private AnimationDrawable animationDrawable;
    private ImageView searchSong;
//    private final int Manifest_permission_READ_EXTERNAL_STORAGE = 1991;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dashboard);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        fragMusic = new FragMusic();
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(videoLoader);
        Glide.with(this).load(R.raw.ripple1).into(imageViewTarget);
        /*videoView.initialize(getResources().getString(R.string.youtubeAPI),this);*/
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mainLayout,fragMusic,MUSICFRAG);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
        fragmentTransaction.addToBackStack(MUSICFRAG);
        fragmentTransaction.commit();
//        permissions();
        mainLayout.setBackgroundResource(R.drawable.background_dashboard);
        animationDrawable = (AnimationDrawable)mainLayout.getBackground();
        animationDrawable.start();
        surfaceHolder = videoView.getHolder();
        surfaceHolder.addCallback(this);
        etSearchSong.setOnClickListener(this);
        searchSong.setOnClickListener(this);
        etSearchSong.setOnEditorActionListener(this);

    }


    private void init() {
        searchSong = (ImageView)findViewById(R.id.searchSong);
        toolbar = (Toolbar)findViewById(R.id.toolbar_dashboard);
        videoView = (SurfaceView) findViewById(R.id.video);
        videoLoader = (ImageView)findViewById(R.id.videoLoader);
        etSearchSong = (EditText)findViewById(R.id.et_searchSong);
        mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDisplay(surfaceHolder);
        try {
            AssetFileDescriptor afd = getAssets().openFd("video.3gp");
            mediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(), afd.getLength());
            mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    Toast.makeText(DashboardActivity.this, "Unable to play video", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            mediaPlayer.setOnPreparedListener(this);

        } catch (IOException e) {
            Log.e("", "surfaceCreated: "+e.toString() );
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        videoView.requestFocus();
        mediaPlayer.start();
        videoLoader.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.searchSong:
                 searchSong();
                break;
            case R.id.et_searchSong:
                etSearchSong.requestFocus();
                /** Inflate an overlapping view, to show searching is begin*/
                //Default position 0
                FragMusicSearch fragMusicSearch = FragMusicSearch.getInstance(null);
                if(fragMusicSearch.isVisible()){
                    /** Inflate an overlapping view, to show searching is resumed*/

                }else {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.replace(R.id.mainLayout, fragMusicSearch);
                    fragmentTransaction.commit();
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
        if(animationDrawable.isRunning()){
            animationDrawable.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!animationDrawable.isRunning()){
            animationDrawable.start();
        }
    }

    @Override
    public void onBackPressed() {
        if(fragmentManager.getBackStackEntryCount()>0) {
           for(int i = fragmentManager.getBackStackEntryCount()-1;i>=0;i--){
               FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
               FragmentManager.BackStackEntry backStackEntry= fragmentManager.getBackStackEntryAt(i);
               switch (backStackEntry.getName()){
                   case MUSICFRAG:
                       fragmentTransaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
                       fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                       fragmentTransaction.replace(R.id.mainLayout,fragMusic);
                       fragmentTransaction.commit();

                       break;
               }
           }
        }else{
            super.onBackPressed();
        }

    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if(i == EditorInfo.IME_ACTION_SEARCH){
            searchSong();
            return true;
        }
        return false;
    }


    private void searchSong() {

        etSearchSong.setText("closer");
            /** Inflate an overlapping view, to show searching is resumed*/
           if(!etSearchSong.getText().toString().trim().equalsIgnoreCase("")) {
               Bundle bundle = new Bundle();
               bundle.putString(songFetchKey,etSearchSong.getText().toString().trim());
               FragMusicSearch fragMusicSearch = FragMusicSearch.getInstance(bundle);
               if(fragMusicSearch.isVisible()) {
                   fragMusicSearch.performSearch(etSearchSong.getText().toString().trim());
               } else {
                   FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                   fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                   fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                   fragmentTransaction.replace(R.id.mainLayout, fragMusicSearch, MUSICFRAGSERACH);
                   fragmentTransaction.commit();

               }
           }else{
               //Todo: Show dialog that empty search is queried..
           }

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }
    /*   @TargetApi(Build.VERSION_CODES.M)
    private void permissions() {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},Manifest_permission_READ_EXTERNAL_STORAGE);
        }else{
            if(fragMusic!=null) {
                fragMusic.initializeLoader();
            }else{

            }
        }
        return;

    }
*/
   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Manifest_permission_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fragMusic.initializeLoader();
                } else {
                    // User refused to grant permission.
                    permissions();
                }
                break;
        }
    }*/


}
