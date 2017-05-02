package com.example.anubhav.musicapp.MusicPLayerComponents;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.rtp.AudioStream;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.SeekBar;

import com.example.anubhav.musicapp.Constants;
import com.example.anubhav.musicapp.DashboardActivity;
import com.example.anubhav.musicapp.Model.SongsModel;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by anubhav on 30/3/17.
 */

public class MusicService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private IBinder musicServiceBinder = new MusicServiceBinder();
    private SongsModel songModel = null;
    private MediaPlayer musicPlayer;
    private Context context;
    private SongsModel currentPlayingSong;
    private ArrayList<SongsModel> copyPlaylistList;
    private ServiceConnection serviceConnection;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicServiceBinder;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    public void pauseMediaPlayer() {
        if(musicPlayer!=null){
            musicPlayer.pause();
        }
    }

    public void resumePlayback() {
        if(musicPlayer!=null){
            musicPlayer.start();
        }
    }
    public boolean isMediaPlayerRunning(){
        if(musicPlayer!=null && musicPlayer.isPlaying()){
            return true;
        }else{
            return false;
        }
    }

    public class MusicServiceBinder extends Binder{
        public MusicService getServiceObjectFromBinder(){
            return MusicService.this;
        }
    }
    public void setSongModel(SongsModel model){
        if(model!=null) {
            songModel = model;
        }else{
            songModel = null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    private void initMusicPlayer() {
        musicPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        musicPlayer.setOnErrorListener(this);
        musicPlayer.setOnPreparedListener(this);
        musicPlayer.setOnCompletionListener(this);
    }
    public void playSong(Context ctx){
        context = ctx;
        if(songModel!=null) {
            musicPlayer.reset();
            long currSongId = Long.valueOf(songModel.getSongId());
            Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,currSongId);
            try {
                musicPlayer.setDataSource(getApplicationContext(),trackUri);
                musicPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void forwardSongBySeekbar(int progress){
        musicPlayer.seekTo(progress*1000);
    }

    public int getMediaPlayerPos(){
        if(musicPlayer!=null){
            return musicPlayer.getCurrentPosition();
        }
        return 1;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        //Return Flags if you want to
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        musicPlayer.stop();
        musicPlayer.release();
        return false;

    }
    public void setCurrentPlayingSong(SongsModel song){
        currentPlayingSong = song;
    }
    public void setCopyPlaylist(ArrayList<SongsModel> copyPlaylist){
        copyPlaylistList = copyPlaylist;
    }
    public void setServiceConnection(ServiceConnection connection){
        serviceConnection = connection;
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if(currentPlayingSong!=null) {
            Intent startIntent = new Intent(this, MusicBackgroundService.class);
            startIntent.setAction(Constants.ACTION_STARTFOREGROUND_ACTION);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.FORWARD_CURRENT_SONG_TO_BACKGROUND, currentPlayingSong);
            if (copyPlaylistList != null && copyPlaylistList.size() > 0) {
                bundle.putSerializable(Constants.FORWARD_COPYPLAYLIST_TO_BACKGROUND, copyPlaylistList);
            }
            if(isMediaPlayerRunning()){

                bundle.putInt(Constants.FORWARD_CURRENT_SONG_ACTIVE_POSITION,getMediaPlayerPos());
                bundle.putBoolean(Constants.FORWARD_CURRENT_SONG_IS_ACTIVE,true);
            }else{
                bundle.putInt(Constants.FORWARD_CURRENT_SONG_ACTIVE_POSITION,-1);
                bundle.putBoolean(Constants.FORWARD_CURRENT_SONG_IS_ACTIVE,false);
            }
            startIntent.putExtras(bundle);
            startService(startIntent);
        }
//        unbindService(serviceConnection);
        stopSelf();
    }
}
