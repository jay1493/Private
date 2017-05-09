package com.example.anubhav.musicapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.anubhav.musicapp.Fragments.MainChildSongsFragment;
import com.example.anubhav.musicapp.Model.AlbumModel;
import com.example.anubhav.musicapp.Model.MusicModel;
import com.example.anubhav.musicapp.Model.SongsModel;
import com.example.anubhav.musicapp.Observers.MySongsObserver;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by anubhav on 29/3/17.
 */

public class BaseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private MySongsObserver mySongsObserver;
    private SharedPreferences saveMusicModelSharedPrefs;
    private MusicModel musicModel;
    private boolean isSongsLoadingComplete,isAlbumsLoadingComplete;
    private HashMap albumModelHashMap;
    private ArrayList<SongsModel> songsModelArrayList;
    private ArrayList<AlbumModel> albumModelArrayList;
    private ProgressDialog progressDialog;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        saveMusicModelSharedPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME,MODE_PRIVATE);
        String musicJson = saveMusicModelSharedPrefs.getString(Constants.SHARED_PREFS_SAVED_MODEL,null);
        if(musicJson!=null){
            Log.e("", "onCreate: In Base Activity (where musicJson!=null)");
            Gson gson = new Gson();
            musicModel = gson.fromJson(musicJson,MusicModel.class);
        }
    }
    public MusicModel getMusicModel(){
        return musicModel;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    public void callLoaders(final Context context){
        albumModelArrayList = new ArrayList<>();
        songsModelArrayList = new ArrayList<>();
        albumModelHashMap = new HashMap();
        musicModel = new MusicModel();
        handler = new Handler(context.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(context.getResources().getString(R.string.Updating_List_For_You_Hold_On));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                getSupportLoaderManager().initLoader(1,null,BaseActivity.this);
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i){
            case 0:
                return new CursorLoader(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.TITLE+ " ASC");
            case 1:
                return new CursorLoader(this, MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,null,null,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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
                getSupportLoaderManager().initLoader(0,null,BaseActivity.this);
                break;
        }
        if(isAlbumsLoadingComplete && isSongsLoadingComplete){
            musicModel.setAllAlbums(albumModelArrayList);
            musicModel.setAllSongs(songsModelArrayList);
            Gson gson = new Gson();
            String musicJson = gson.toJson(musicModel);
            SharedPreferences.Editor editorPrefs = saveMusicModelSharedPrefs.edit();
            editorPrefs.putString(Constants.SHARED_PREFS_SAVED_MODEL,musicJson);
            editorPrefs.apply();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    MainChildSongsFragment.notifyAdapterFromActivity(musicModel);
                }
            });

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        albumModelArrayList = new ArrayList<>();
        songsModelArrayList = new ArrayList<>();
    }

}
