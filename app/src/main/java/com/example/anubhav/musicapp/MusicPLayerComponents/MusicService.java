package com.example.anubhav.musicapp.MusicPLayerComponents;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.rtp.AudioStream;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.SeekBar;

import com.example.anubhav.musicapp.Model.SongsModel;

import java.io.IOException;

/**
 * Created by anubhav on 30/3/17.
 */

public class MusicService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private IBinder musicServiceBinder = new MusicServiceBinder();
    private SongsModel songModel = null;
    private MediaPlayer musicPlayer;
    private Context context;

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

    public class MusicServiceBinder extends Binder{
        MusicService getServiceObjectFromBinder(){
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
}
