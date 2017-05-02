package com.example.anubhav.musicapp.MusicPLayerComponents;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.example.anubhav.musicapp.Constants;
import com.example.anubhav.musicapp.DashboardActivity;
import com.example.anubhav.musicapp.Model.SongsModel;
import com.example.anubhav.musicapp.R;
import com.example.anubhav.musicapp.StartingActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by anubhav on 30/4/17.
 */

public class MusicBackgroundService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private SongsModel currentSongModel;
    private ArrayList<SongsModel> copyPlayList;
    private boolean isSongActive;
    private int songMediaPlayerPos;
    private MediaPlayer musicPlayer;
    private Notification notification;
    private Notification.Builder notificationBuilder;
    Bitmap notifLargeIcon = null;
    Bitmap largeIcon = null;
    String currentSong ="";
    private PendingIntent pendingIntent,pnextIntent,ppreviousIntent,pplayIntent;
    private SongsModel currentSongFromService;
    private NotificationManager mNotificationManager;
    private int forwardMusic = -1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
    public void playSong(int songMediaPlayerPos,SongsModel currentSongFromService){
        if(currentSongFromService!=null) {
            currentSongModel = currentSongFromService;
            musicPlayer.reset();
            long currSongId = Long.valueOf(currentSongModel.getSongId());
            Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,currSongId);
            try {
                if(songMediaPlayerPos!=-1) {
                    forwardMusic = songMediaPlayerPos;
                }else{
                    forwardMusic = -1;
                }
                musicPlayer.setDataSource(getApplicationContext(),trackUri);
                musicPlayer.prepareAsync();
                if(currentSongModel!=null){
                    largeIcon = BitmapFactory.decodeFile(currentSongModel.getSongAlbumCover());
                    if(largeIcon!=null && largeIcon.getRowBytes()>0){

                    }else{
                        largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.index);
                    }
                    currentSong = currentSongModel.getSongTitle();
                }else{
                    largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.index);
                    currentSong = "Now Playing";
                }
                notifLargeIcon = Bitmap.createScaledBitmap(largeIcon,128,128,false);
                notificationBuilder = getPauseNotifBuilder();
                notification = notificationBuilder.build();
                mNotificationManager.notify(101, notification);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Notification.Builder getPauseNotifBuilder() {
        return new Notification.Builder(this)
                .setContentTitle(Constants.APP_NAME)
                .setContentText(currentSong).setSmallIcon(R.drawable.notif_icon).setLargeIcon(notifLargeIcon)
                .setContentIntent(pendingIntent).setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX).setWhen(0)
                .addAction(android.R.drawable.ic_media_previous,
                        "Previous", ppreviousIntent)
                .addAction(android.R.drawable.ic_media_pause, "Pause",
                        pplayIntent)
                .addAction(android.R.drawable.ic_media_next, "Next",
                        pnextIntent);
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("BackgroundMusicService", "Inside Service");
        if(intent!=null) {
            if (intent.getAction().equals(Constants.ACTION_STARTFOREGROUND_ACTION)) {
                Log.i("BackgroundMusicService", "ACTION_STARTFOREGROUND");
                if(intent.getExtras()!=null){
                    Bundle bundle = intent.getExtras();
                    isSongActive = bundle.getBoolean(Constants.FORWARD_CURRENT_SONG_IS_ACTIVE);
                    if(bundle.getInt(Constants.FORWARD_CURRENT_SONG_ACTIVE_POSITION)!=-1){
                       songMediaPlayerPos = bundle.getInt(Constants.FORWARD_CURRENT_SONG_ACTIVE_POSITION);
                    }
                    currentSongFromService = (SongsModel) bundle.getSerializable(Constants.FORWARD_CURRENT_SONG_TO_BACKGROUND);
                    if(bundle.getSerializable(Constants.FORWARD_COPYPLAYLIST_TO_BACKGROUND)!=null){
                        copyPlayList = (ArrayList<SongsModel>) bundle.getSerializable(Constants.FORWARD_COPYPLAYLIST_TO_BACKGROUND);
                    }
                }
                setUpPendingIntents();
                if(currentSongModel!=null){
                    largeIcon = BitmapFactory.decodeFile(currentSongModel.getSongAlbumCover());
                    if(largeIcon!=null && largeIcon.getRowBytes()>0){

                    }else{
                        largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.index);
                    }
                    currentSong = currentSongModel.getSongTitle();
                }else{
                    largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.index);
                    currentSong = "Now Playing";
                }
                notifLargeIcon = Bitmap.createScaledBitmap(largeIcon,128,128,false);
                if(isSongActive){
                    playSong(songMediaPlayerPos,currentSongFromService);
                }else{
                    notificationBuilder = getPlayNotifBuilder();
                    notification = notificationBuilder.build();

                }

                startForeground(101, notification);

            } else if (intent.getAction().equals(Constants.ACTION_PREV_ACTION)) {
                Log.i("BackgroundMusicService", "Clicked Previous");
                skipPrevious();
            } else if (intent.getAction().equals(Constants.ACTION_PLAY_ACTION)) {
                Log.i("BackgroundMusicService", "Clicked Play");
                if(musicPlayer.isPlaying()){
                    musicPlayer.pause();
                    if(currentSongModel!=null){
                        largeIcon = BitmapFactory.decodeFile(currentSongModel.getSongAlbumCover());
                        if(largeIcon!=null && largeIcon.getRowBytes()>0){

                        }else{
                            largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.index);
                        }
                        currentSong = currentSongModel.getSongTitle();
                    }else{
                        largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.index);
                        currentSong = "Now Playing";
                    }
                    notifLargeIcon = Bitmap.createScaledBitmap(largeIcon,128,128,false);
                    notificationBuilder = getPlayNotifBuilder();
                    notification = notificationBuilder.build();
                    mNotificationManager.notify(101, notification);
                }else{
                    musicPlayer.start();
                    if(currentSongModel!=null){
                        largeIcon = BitmapFactory.decodeFile(currentSongModel.getSongAlbumCover());
                        if(largeIcon!=null && largeIcon.getRowBytes()>0){

                        }else{
                            largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.index);
                        }
                        currentSong = currentSongModel.getSongTitle();
                    }else{
                        largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.index);
                        currentSong = "Now Playing";
                    }
                    notifLargeIcon = Bitmap.createScaledBitmap(largeIcon,128,128,false);
                    notificationBuilder = getPauseNotifBuilder();
                    notification = notificationBuilder.build();
                    mNotificationManager.notify(101, notification);
                }

            } else if (intent.getAction().equals(Constants.ACTION_NEXT_ACTION)) {
                Log.i("BackgroundMusicService", "Clicked Next");
                skipNext();
            } else if (intent.getAction().equals(
                    Constants.ACTION_STOPFOREGROUND_ACTION)) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.BACKGROUND_SHARED_PREFS,MODE_MULTI_PROCESS);
                Gson gson = new Gson();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.BACKGROUND_SHARED_PREFS_MODEL,gson.toJson(currentSongModel));
                editor.commit();
                Log.i("BackgroundMusicService", "Received Stop Foreground Intent");
                stopForeground(true);
                stopSelf();
            }
        }
        return START_REDELIVER_INTENT;
    }

    private void skipPrevious() {
        if (copyPlayList != null && copyPlayList.size() > 0) {

            if(currentSongModel!=null){
                SongsModel songsModel = currentSongModel;
                currentSongModel = copyPlayList.get(0);
                if(copyPlayList.contains(currentSongModel)) {
                    copyPlayList.remove(currentSongModel);
                }
//                        inflateMusicLayout(currentPlayingSong);
                playSong(-1,currentSongModel);
                if(!copyPlayList.contains(songsModel)) {
                    copyPlayList.add(songsModel);
                }
                /*if(playlistAdapter!=null){
                    playlistAdapter.notifyDataSetChanged();
                }*/
            }else{
                currentSongModel = copyPlayList.get(0);

            }
        }else{
//                    initializeSnackBar(getResources().getString(R.string.Current_playlist_is_empty));
        }
    }

    private void skipNext() {
        if (copyPlayList != null && copyPlayList.size() > 0) {

            if(currentSongModel!=null){
                SongsModel songsModel = currentSongModel;
                currentSongModel = copyPlayList.get(0);
                if(copyPlayList.contains(currentSongModel)) {
                    copyPlayList.remove(currentSongModel);
                }
                playSong(-1,currentSongModel);
                if(!copyPlayList.contains(songsModel)) {
                    copyPlayList.add(songsModel);
                }
            }else{
                currentSongModel = copyPlayList.get(0);

            }
        }else{
//                    initializeSnackBar(getResources().getString(R.string.Current_playlist_is_empty));
        }
    }

    private Notification.Builder getPlayNotifBuilder() {
        return new Notification.Builder(this)
                .setContentTitle(Constants.APP_NAME)
                .setContentText(currentSong).setSmallIcon(R.drawable.notif_icon).setLargeIcon(notifLargeIcon)
                .setContentIntent(pendingIntent).setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX).setWhen(0)
                .addAction(android.R.drawable.ic_media_previous,
                        "Previous", ppreviousIntent)
                .addAction(android.R.drawable.ic_media_play, "Play",
                        pplayIntent)
                .addAction(android.R.drawable.ic_media_next, "Next",
                        pnextIntent);
    }

    private void setUpPendingIntents() {
        Intent notificationIntent = new Intent(this, DashboardActivity.class);
        notificationIntent.setAction(Constants.ACTION_MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.FORWARD_CURRENT_SONG_ACTIVE_POSITION,getMediaPlayerPos());
        bundle.putSerializable(Constants.FORWARD_CURRENT_SONG_TO_BACKGROUND, currentSongModel);
        if (copyPlayList != null && copyPlayList.size() > 0) {
            bundle.putSerializable(Constants.FORWARD_COPYPLAYLIST_TO_BACKGROUND, copyPlayList);
        }
        notificationIntent.putExtras(bundle);
        pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);


        Intent previousIntent = new Intent(this, MusicBackgroundService.class);
        previousIntent.setAction(Constants.ACTION_PREV_ACTION);
        ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, MusicBackgroundService.class);
        playIntent.setAction(Constants.ACTION_PLAY_ACTION);
        pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, MusicBackgroundService.class);
        nextIntent.setAction(Constants.ACTION_NEXT_ACTION);
        pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
     mp.start();
        if(forwardMusic!=-1){
            mp.seekTo(forwardMusic);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("BackgroundService", "onCompletion:1 " +mp.getCurrentPosition());
        Log.e("BackgroundService", "onCompletion:2 " +mp.getDuration());
        Log.e("BackgroundService", "onCompletion:3 " +currentSongModel.getSongDuration());
        if(Integer.parseInt(currentSongModel.getSongDuration()) == mp.getCurrentPosition()){
          //Change Song
            skipNext();
        }
    }

    @Override
    public void onDestroy() {
        musicPlayer.stop();
        musicPlayer.release();
        super.onDestroy();
    }
}
