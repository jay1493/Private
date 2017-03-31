package com.example.anubhav.musicapp.MusicPLayerComponents;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anubhav.musicapp.Constants;
import com.example.anubhav.musicapp.Model.SongsModel;
import com.example.anubhav.musicapp.R;

import java.text.ParseException;

/**
 * Created by anubhav on 30/3/17.
 */

public class MusicPLayer extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private SongsModel songModel;
    private TextView songName,songArtistName,currentTimer;
    private ImageView playlistButton,songAlbumImage,playPause;
    private LinearLayout albumImageLayout,playlistLayout;
    private SeekBar seekBar;
    private MusicService musicService;
    private Intent musicServiceIntent;
    private boolean isServiceConnected;
    private Runnable seekbarRunnable;
    private Handler seekbarHandler;
    private float initialDurationOfSongInSecs;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicServiceBinder musicServiceBinder = (MusicService.MusicServiceBinder)service;
            musicService = musicServiceBinder.getServiceObjectFromBinder();
            isServiceConnected = true;
            /** Below two lines would be used to start a new song*/
            musicService.setSongModel(songModel);
            musicService.playSong(MusicPLayer.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceConnected = false;
        }
    };
    private java.text.DecimalFormat decimalFormat;
    private float initialDurationOfSongInMins;
    private Context context;
    private boolean fromUser;
    private int currentProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.music_play_main_layout);
        init();
        if(getIntent()!=null && getIntent().getExtras()!=null){
            songModel = (SongsModel) getIntent().getSerializableExtra(Constants.PLAY_MUSIC_SONGS_LIST_SONG);
            initialDurationOfSongInSecs = (Integer.parseInt(songModel.getSongDuration()))/1000;
            seekBar.setMax((int) initialDurationOfSongInSecs);
            try {
                initialDurationOfSongInMins = Float.parseFloat(decimalFormat.format(initialDurationOfSongInSecs/60));
                currentTimer.setText(String.valueOf(decimalFormat.format(initialDurationOfSongInMins)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            songModel = null;
        }
        if(songModel!=null) {
            setUpValuesFromSongModel();
        }
        musicServiceIntent = new Intent(this,MusicService.class);
        bindService(musicServiceIntent,serviceConnection,BIND_AUTO_CREATE);
        startService(musicServiceIntent);
        seekBar.setOnSeekBarChangeListener(this);
    }
    private void setUpValuesFromSongModel() {
        songName.setText(songModel.getSongTitle());
        songName.requestFocus();
        songArtistName.setText(songModel.getSongArtist());
        Bitmap albumCover = BitmapFactory.decodeFile(songModel.getSongAlbumCover());
        if(albumCover!=null && albumCover.getRowBytes() > 0){
            songAlbumImage.setImageBitmap(albumCover);
        }

    }

    private void init() {
        context = this;
        songName = (TextView) findViewById(R.id.songName_musicLayout);
        songArtistName = (TextView) findViewById(R.id.song_ArtistName_musicLayout);
        playlistButton = (ImageView) findViewById(R.id.playlistButton_musicLayout);
        albumImageLayout = (LinearLayout) findViewById(R.id.playbackImage_musicLayout);
        songAlbumImage = (ImageView) findViewById(R.id.songAlbumImage_musicLayout);
        playlistLayout = (LinearLayout) findViewById(R.id.playlist_musicLayout);
        seekBar = (SeekBar) findViewById(R.id.music_seekbar);
        playPause = (ImageView) findViewById(R.id.playPause_song_musicLayout);
        currentTimer = (TextView) findViewById(R.id.currentTimer);
        seekbarHandler = new Handler();
        decimalFormat = new java.text.DecimalFormat("0.00");
        playPause.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        seekbarRunnable = new Runnable() {
            @Override
            public void run() {
                if(musicService!=null && seekBar!=null){
                    //680,1729,3726 etc... getMediaPlayerPos return secs in millis..
                    seekBar.setProgress(musicService.getMediaPlayerPos()/1000);
                }
                if(seekbarHandler!=null) {
                    seekbarHandler.postDelayed(this, 1000);
                }
            }
        };
        runOnUiThread(seekbarRunnable);


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(musicServiceIntent);
        musicService = null;
        super.onDestroy();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.e("", "onProgressChanged: "+progress );
        if(fromUser){
            musicService.forwardSongBySeekbar(progress);
        }
        if(progress == seekBar.getMax()){
            //Full Song Played.,change pause image to play
            if(playPause.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(MusicPLayer.this,R.drawable.pause_playback).getConstantState()){
                playPause.setImageDrawable(getResources().getDrawable(R.drawable.play_playback));
                currentTimer.setText("--:--");
            }
        }else{
            //Currently running
            if(musicService!=null){
                try {
                    float mediaProgress = musicService.getMediaPlayerPos();
                    float diffInMins = initialDurationOfSongInMins - Float.parseFloat(decimalFormat.format(mediaProgress/(60*1000)));
                    currentTimer.setText(String.valueOf(decimalFormat.format(diffInMins)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.e("", "onStartTrackingTouch: " );
        seekbarHandler.removeCallbacks(seekbarRunnable);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        seekbarHandler.postDelayed(seekbarRunnable,1000);
        fromUser = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.playPause_song_musicLayout:
                if(playPause.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.pause_playback).getConstantState()){
                    //Currently playing
                    if(seekBar!=null && musicService!=null){
                        //In secs
                        currentProgress = musicService.getMediaPlayerPos();
                        musicService.pauseMediaPlayer();
                        playPause.setImageDrawable(getResources().getDrawable(R.drawable.play_playback));
                    }
                }else{
                    //Currently Paused
                    if(seekBar!=null && musicService!=null){
                        //In secs
                        musicService.resumePlayback();
                        playPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_playback));
                    }
                }
                break;
        }
    }
}
