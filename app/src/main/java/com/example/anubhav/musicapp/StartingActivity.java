package com.example.anubhav.musicapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.anubhav.musicapp.Interfaces.ObserverListener;
import com.example.anubhav.musicapp.Model.AlbumModel;
import com.example.anubhav.musicapp.Model.MusicModel;
import com.example.anubhav.musicapp.Model.SongsModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by anubhav on 16/2/17.
 */

public class StartingActivity extends BaseActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final int READ_MUSIC_REQ_CODE = 0123;
    private TextView logoName;
    private ImageView mainLoader,mainImage;
    private List<Integer> images;
    private Drawable start,end;
    private Random random;
    private Resources res;
    private boolean running = true;
    private Context context;
    private static final String 		appString				= Constants.APP_NAME;
    private boolean isSongsLoadingComplete = false;
    private boolean isAlbumsLoadingComplete = false;
    private ArrayList<AlbumModel> albumModelArrayList;
    private ArrayList<SongsModel> songsModelArrayList;
    private HashMap<String,AlbumModel> albumModelHashMap;
    private MusicModel musicModel;
    private SharedPreferences saveMusicModelSharedPrefs;

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
        permissions();
        Typeface typeface = Typeface.createFromAsset(getAssets(),"CAC_Champagne.ttf");
        logoName.setTypeface(typeface,Typeface.BOLD_ITALIC);
        GlideDrawableImageViewTarget glideDrawableImageViewTarget = new GlideDrawableImageViewTarget(mainLoader);
        Glide.with(this).load(R.raw.infinity1).into(glideDrawableImageViewTarget);

    }

    private void isLoaderNeedsToLoad() {
        if(getMusicModel()==null) {
            getSupportLoaderManager().initLoader(1, null, this);
        }else{
            //But check for a change
            setObserverOnActivity(new ObserverListener() {
                @Override
                public void isProcessCompleted(boolean isComplete) {
                       if(isComplete){
                           //Change
                           getSupportLoaderManager().initLoader(1, null, StartingActivity.this);
                       }else{
                           //No change is there
                           musicModel = getMusicModel();
                           mainImage.setOnClickListener(StartingActivity.this);
                       }
                }
            },context);
        }
    }


    private void init() {
        saveMusicModelSharedPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME,MODE_PRIVATE);
        albumModelHashMap = new HashMap<>();
        songsModelArrayList = new ArrayList<>();
        albumModelArrayList = new ArrayList<>();
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

    private void permissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_MUSIC_REQ_CODE);
            }else{
                isLoaderNeedsToLoad();
            }
        }else {
            isLoaderNeedsToLoad();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case READ_MUSIC_REQ_CODE:
                if(grantResults!=null && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    isLoaderNeedsToLoad();
                }else{
                    permissions();
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i){
            case 0:
                return new CursorLoader(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.TITLE+ " ASC");
            case 1:
                return new CursorLoader(context, MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,null,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
          switch (loader.getId()){
              case 0:
                  if(loader!=null) {
                      isSongsLoadingComplete = true;
                      if(data!=null && data.moveToFirst()){
                          int songId = data.getColumnIndex(MediaStore.Audio.Media._ID);
                          int albumId = data.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                          int albumName = data.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                          int songDuration =  data.getColumnIndex(MediaStore.Audio.Media.DURATION);
                          int songTitle = data.getColumnIndex(MediaStore.Audio.Media.TITLE);
                          int songTrack = data.getColumnIndex(MediaStore.Audio.Media.TRACK);
                          int songArtist = data.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                          do{
                              SongsModel songsModel = new SongsModel();
                              songsModel.setSongId(String.valueOf(data.getLong(songId)));
                              songsModel.setSongAlbumId(String.valueOf(data.getLong(albumId)));
                              songsModel.setSongAlbumName(data.getString(albumName));
                              songsModel.setSongDuration(String.valueOf(data.getLong(songDuration)));
                              songsModel.setSongTitle(data.getString(songTitle));
                              songsModel.setTrackNoOfSongInAlbum(String.valueOf(data.getInt(songTrack)));
                              songsModel.setSongArtist(data.getString(songArtist));
                              AlbumModel albumModel = (AlbumModel) albumModelHashMap.get(songsModel.getSongAlbumId());
                              if(albumModel!=null){
                                  songsModel.setSongAlbumCover(albumModel.getAlbumCover());
                                  albumModel.putSongs(songsModel);
                              }
                              songsModelArrayList.add(songsModel);
                          }while (data.moveToNext());
                      }
                  }else{
                      return;
                  }
                  break;
              case 1:
                  if(loader!=null) {
                      isAlbumsLoadingComplete = true;
                      if (data != null && data.moveToFirst()) {
                          int albumId = data.getColumnIndex(MediaStore.Audio.Albums._ID);
                          int artistTitle = data.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
                          int albumTitle = data.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
                          int albumCover = data.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
                          int noOfSongs  = data.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
                          do {
                              AlbumModel albumModel = new AlbumModel();

                              albumModel.setAlbumId(String.valueOf(data.getLong(albumId)));
                              albumModel.setAlbumTitle(data.getString(albumTitle));
                              albumModel.setArtistTitle(data.getString(artistTitle));
                              albumModel.setAlbumCover(data.getString(albumCover));
                              albumModel.setNoOfSongs(String.valueOf(data.getInt(noOfSongs)));
                              albumModelHashMap.put(String.valueOf(data.getLong(albumId)),albumModel);
                              albumModelArrayList.add(albumModel);
                          } while (data.moveToNext());

                      }
                  }else{
                      return;
                  }
                  getSupportLoaderManager().initLoader(0,null,StartingActivity.this);
                  break;
          }
          if(isAlbumsLoadingComplete && isSongsLoadingComplete){
              musicModel = new MusicModel();
              musicModel.setAllAlbums(albumModelArrayList);
              musicModel.setAllSongs(songsModelArrayList);
              Gson gson = new Gson();
              String musicJson = gson.toJson(musicModel);
              SharedPreferences.Editor editorPrefs = saveMusicModelSharedPrefs.edit();
              editorPrefs.putString(Constants.SHARED_PREFS_SAVED_MODEL,musicJson);
              editorPrefs.apply();
              mainImage.setOnClickListener(StartingActivity.this);
          }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        albumModelArrayList = new ArrayList<>();
        songsModelArrayList = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainBackImage:
                Intent intent = new Intent(StartingActivity.this,DashboardActivity.class);
                intent.putExtra(Constants.SEND_MUSIC_AS_EXTRA,musicModel);
                startActivity(intent);
                finish();
                break;
        }
    }

}
