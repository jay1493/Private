package com.example.anubhav.musicapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.crashlytics.android.Crashlytics;
import com.example.anubhav.musicapp.Interfaces.ObserverListener;
import com.example.anubhav.musicapp.Model.AlbumModel;
import com.example.anubhav.musicapp.Model.MusicModel;
import com.example.anubhav.musicapp.Model.SongsModel;
import com.example.anubhav.musicapp.Observers.MySongsObserver;
import com.google.gson.Gson;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by anubhav on 16/2/17.
 */

public class StartingActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final int Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS = 1991;
    private final int Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS_MINUS_LOCATION = 1992;
    private final int Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS_MINUS_LOCATION_AND_WAKE_LOCK = 1993;
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
    private MySongsObserver mySongsObserver;
    private boolean contentObserverExecuted;
    private Handler handlerToForwardIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
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
                crossfader.startTransition(5000);
                mainImage.setImageDrawable(crossfader);
                if(running){
                    handler.postDelayed(this,5000);
                }
            }
        },5000);
        permissions();
        Typeface typeface = Typeface.createFromAsset(getAssets(),"CAC_Champagne.ttf");
        logoName.setTypeface(typeface,Typeface.BOLD_ITALIC);
        GlideDrawableImageViewTarget glideDrawableImageViewTarget = new GlideDrawableImageViewTarget(mainLoader);
        Glide.with(this).load(R.raw.infinity1).into(glideDrawableImageViewTarget);
        settingContentObserver();

    }

    private void settingContentObserver() {
        mySongsObserver = new MySongsObserver(new Handler(context.getMainLooper()),musicModel,new ObserverListener(){

            @Override
            public void isProcessCompleted(boolean isComplete) {
                if(isComplete) {
                        contentObserverExecuted = true;
                        getSupportLoaderManager().initLoader(1, null, StartingActivity.this);
                        return;
                }else{
                    //No change is there
                    if(getMusicModel()!=null) {
                        musicModel = getMusicModel();
                        handlerToForwardIntent.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(StartingActivity.this,DashboardActivity.class);
                                intent.putExtra(Constants.SEND_MUSIC_AS_EXTRA,musicModel);
                                startActivity(intent);
                                finish();
                            }
                        },9000);
                    }else{
                        getSupportLoaderManager().initLoader(1, null, StartingActivity.this);
                    }
                    Log.d("", "isProcessCompleted: SavedModel");
                    return;

                }
            }
        });
        getContentResolver().registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,true,mySongsObserver);

    }

    private void isLoaderNeedsToLoad() {
        if(getMusicModel()==null) {
            getSupportLoaderManager().initLoader(1, null, this);
        }else{
            musicModel = getMusicModel();
            handlerToForwardIntent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(StartingActivity.this,DashboardActivity.class);
                    intent.putExtra(Constants.SEND_MUSIC_AS_EXTRA,musicModel);
                    startActivity(intent);
                    finish();
                }
            },9000);
            //But check for a change
          /*  setObserverOnActivity(new ObserverListener() {
                @Override
                public void isProcessCompleted(boolean isComplete) {
                       if(isComplete){
                           //Change
                           getSupportLoaderManager().initLoader(1, null, StartingActivity.this);
                           Log.d("", "isProcessCompleted: StartLoader");
                           return;
                       }else{
                           //No change is there
                           musicModel = getMusicModel();
                           mainImage.setOnClickListener(StartingActivity.this);
                           Log.d("", "isProcessCompleted: SavedModel");
                           return;
                       }
                }
            },context);*/
            Log.d("", "isProcessCompleted: OutsideFunction");
        }
    }


    private void init() {
        saveMusicModelSharedPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME,MODE_MULTI_PROCESS);
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
        handlerToForwardIntent = new Handler();
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
            if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.WAKE_LOCK)!= PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WAKE_LOCK,Manifest.permission.READ_PHONE_STATE}, Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS);

            }else  if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.WAKE_LOCK)!= PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.WAKE_LOCK,Manifest.permission.READ_PHONE_STATE}, Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS_MINUS_LOCATION);


            }else if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_PHONE_STATE}, Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS_MINUS_LOCATION_AND_WAKE_LOCK);


            }else{
                isLoaderNeedsToLoad();
            }
            return;
        }else{
            isLoaderNeedsToLoad();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED && grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                    isLoaderNeedsToLoad();
                } else {
                    // User refused to grant permission.
                    permissions();
                }
                break;
            case Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS_MINUS_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                    isLoaderNeedsToLoad();
                } else {
                    // User refused to grant permission.
                    permissions();
                }
                break;
            case Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS_MINUS_LOCATION_AND_WAKE_LOCK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    isLoaderNeedsToLoad();
                } else {
                    // User refused to grant permission.
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
                              if(data.getLong(songDuration)/(1000*60) >= 1) {
                                  songsModel.setSongTitle(data.getString(songTitle));
                                  songsModel.setTrackNoOfSongInAlbum(String.valueOf(data.getInt(songTrack)));
                                  songsModel.setSongArtist(data.getString(songArtist));
                                  AlbumModel albumModel = (AlbumModel) albumModelHashMap.get(songsModel.getSongAlbumId());
                                  if (albumModel != null) {
                                      songsModel.setSongAlbumCover(albumModel.getAlbumCover());
                                      albumModel.putSongs(songsModel);
                                  }
                                  songsModelArrayList.add(songsModel);
                              }
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
              handlerToForwardIntent.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      Intent intent = new Intent(StartingActivity.this,DashboardActivity.class);
                      intent.putExtra(Constants.SEND_MUSIC_AS_EXTRA,musicModel);
                      startActivity(intent);
                      finish();
                  }
              },9000);
          }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        albumModelArrayList = new ArrayList<>();
        songsModelArrayList = new ArrayList<>();
    }

}
