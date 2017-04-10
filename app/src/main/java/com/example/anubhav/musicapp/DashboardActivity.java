package com.example.anubhav.musicapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.acrcloud.rec.sdk.ACRCloudClient;
import com.acrcloud.rec.sdk.ACRCloudConfig;
import com.acrcloud.rec.sdk.IACRCloudListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.anubhav.musicapp.Adapters.FingerprintResultsAdapter;
import com.example.anubhav.musicapp.Adapters.SongsListAdapter;
import com.example.anubhav.musicapp.Fragments.FragMusicSearch;
import com.example.anubhav.musicapp.Fragments.MainChildSongsFragment;
import com.example.anubhav.musicapp.Fragments.MainSongsFragment;
import com.example.anubhav.musicapp.Interfaces.ItemClickListener;
import com.example.anubhav.musicapp.Interfaces.ObserverListener;
import com.example.anubhav.musicapp.Model.AudioFingerPrintingResultModel;
import com.example.anubhav.musicapp.Model.AudioFingerPrintingResultMusicModel;
import com.example.anubhav.musicapp.Model.AudioFingerprintResultsArtistModel;
import com.example.anubhav.musicapp.Model.AudioFingerprintResultsGenreModel;
import com.example.anubhav.musicapp.Model.MusicModel;
import com.example.anubhav.musicapp.Model.PlaylistModel;
import com.example.anubhav.musicapp.Model.SongsModel;
import com.example.anubhav.musicapp.MusicPLayerComponents.MusicService;
import com.example.anubhav.musicapp.Observers.MySongsObserver;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anubhav on 19/2/17.
 */

public class DashboardActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, View.OnClickListener, TextView.OnEditorActionListener, IACRCloudListener,MainChildSongsFragment.SongClickListener, SlidingUpPanelLayout.PanelSlideListener,MainChildSongsFragment.SongAddToPlaylistListener, SongsListAdapter.SongOptionsDeleteFromPlaylistListener {

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
    private MainSongsFragment mainSongFragment;
    private FragmentManager fragmentManager;
    private AnimationDrawable animationDrawable;
    private ImageView searchSong,listenSong;
    private Context context;
    private FrameLayout mainDashboardLayout;
    private final int Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS = 1991;
    private final int Manifest_permission_READ_EXTERNAL_STORAGE = 1992;
    private final int Manifest_permission_WRITE_EXTERNAL_STORAGE = 1993;
    private final int Manifest_permission_RECORD_AUDIO = 1994;
    private final int Manifest_permission_LOCATION = 1995;
    private final int Manifest_permission_WAKE_LOCK =1996;
    //    private final int Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS = 1991;
    private static final String 		appString				= Constants.APP_NAME;
    private boolean activityCreatedForFirstTime = true;

    private AssetFileDescriptor videoLoadingFromAssets;
    /**
     *  Params of Audio FingerPrint
     */
    private ACRCloudClient mClient;

    private ACRCloudConfig mConfig;

    private TextView mVolume, mResult, tv_time;
    private boolean mProcessing = false;

    private boolean initState = false;
    private String path = "";

    private AudioVisualize audioVisualize;
    private long startTime = 0;
    private long stopTime = 0;

    static{
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private AudioFingerPrintingResultModel audioFingerPrintingResultModel;
    private LinearLayout fragmentLayout,albumSearchLayout;
    private MusicModel musicModel;
    private MySongsObserver mySongsObserver;
    private SongsModel songModelReceivedFromMusicPLayer;

    /**
     *
     * @MusicPlayer params
     */
    private TextView songName,songArtistName,currentTimer,selectedSongName, selectedSongArtistName;
    private ImageView playlist_Or_pauseButton,songAlbumImage,playPause,selectedSongAlbumImage,selectedSongOptions;
    private LinearLayout albumImageLayout;
    private RelativeLayout playlistLayout;
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
            musicService.setSongModel(songModelReceivedFromMusicPLayer);
            musicService.playSong(DashboardActivity.this);
            playPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_playback));
            if(musicService!=null && seekBar!=null){
                //Connected Service
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

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceConnected = false;
        }
    };
    private java.text.DecimalFormat decimalFormat;
    private float initialDurationOfSongInMins;
    private boolean fromUser;
    private int currentProgress;
    private SlidingUpPanelLayout slidingLayout;
    private LinearLayout dragLayout;
    private SharedPreferences playlistSharedPrefs;
    private PlaylistModel playlistModel;
    private Gson gson;
    private RecyclerView playlistRecyclerView;
    private SongsListAdapter playlistAdapter;
    private SongsModel currentPlayingSong;
    private ArrayList<SongsModel> copyPlaylistList;
    private ImageView skipPrevious,skipNext;
    private SongsModel retrievedCurrentSong = null;
    private SharedPreferences currentPlayingSongSharedPrefs;
    private SongsModel currentPlayingSongFromPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dashboard);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        context = this;
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(videoLoader);
        Glide.with(this).load(R.raw.ripple1).into(imageViewTarget);
        mainLayout.setBackgroundResource(R.drawable.background_dashboard);
        animationDrawable = (AnimationDrawable)mainLayout.getBackground();
        animationDrawable.start();
        etSearchSong.setOnClickListener(this);
        searchSong.setOnClickListener(this);
        etSearchSong.setOnEditorActionListener(this);
        listenSong.setOnClickListener(this);
        setUpAudioFingerPrinting();
        gson = new Gson();
        if(getIntent()!=null && getIntent().getExtras()!=null){
            musicModel = (MusicModel) getIntent().getSerializableExtra(Constants.SEND_MUSIC_AS_EXTRA);
        }else{
            musicModel = null;
        }
        currentPlayingSongSharedPrefs = getSharedPreferences(Constants.CURRENT_SONG_SHARED_PREFS,MODE_PRIVATE);
        if(currentPlayingSongSharedPrefs.getString(Constants.CURRENT_SONG_FROM_PREFS,null)!=null){
            //We have saved current song before...
            currentPlayingSongFromPrefs = gson.fromJson(currentPlayingSongSharedPrefs.getString(Constants.CURRENT_SONG_FROM_PREFS,null),SongsModel.class);
            inflateMusicLayoutFromSavedSongsModel(currentPlayingSongFromPrefs);
        }else{
            currentPlayingSongFromPrefs = null;
        }

        playlistSharedPrefs = getSharedPreferences(Constants.PLAYLIST_SHARED_PREFS,MODE_PRIVATE);
        if(playlistSharedPrefs.getString(Constants.PLAYLIST_FROM_SHARED_PREFS,null) == null){
            //Noting in playlist nodel
            playlistModel = new PlaylistModel();
        }else{
            playlistModel = gson.fromJson(playlistSharedPrefs.getString(Constants.PLAYLIST_FROM_SHARED_PREFS,null),PlaylistModel.class);
            if(copyPlaylistList!=null){
                copyPlaylistList = playlistModel.getSongsModelList();
                if(currentPlayingSong!=null){
                    if(copyPlaylistList.contains(currentPlayingSong)) {
                        copyPlaylistList.remove(currentPlayingSong);
                    }
                }
            }
        }
    }

    private void setUpAudioFingerPrinting() {
        audioVisualize = new AudioVisualize(this,albumSearchLayout,mainDashboardLayout);
        audioVisualize.startVisualize();
        path = Constants.LOCAL_FINGERPRINT_PATH;
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        this.mConfig = new ACRCloudConfig();
        this.mConfig.acrcloudListener = this;
        this.mConfig.context = this;
        this.mConfig.host = Constants.ACR_HOST;
        this.mConfig.dbPath = path; // offline db path, you can change it with other path which this app can access.
        this.mConfig.accessKey = Constants.ACR_ACCESS_KEY;
        this.mConfig.accessSecret = Constants.ACR_SECRET_KEY;
        this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_REMOTE;
        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_LOCAL;
        //this.mConfig.reqMode = ACRCloudConfig.ACRCloudRecMode.REC_MODE_BOTH;

        this.mClient = new ACRCloudClient();
        this.initState = this.mClient.initWithConfig(this.mConfig);
        if (this.initState) {
            this.mClient.startPreRecord(3000); //start prerecord, you can call "this.mClient.stopPreRecord()" to stop prerecord.
        }
    }

    private void setUpInitialHomeFragment() {
        Bundle bundle = null;
        if(musicModel!=null){
            bundle = new Bundle();
            bundle.putSerializable(Constants.SEND_MUSIC_AS_EXTRA,musicModel);
        }
        mainSongFragment = MainSongsFragment.getInstance(bundle);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentView, mainSongFragment,MUSICFRAG);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
        fragmentTransaction.addToBackStack(MUSICFRAG);
        fragmentTransaction.commit();
    }



    private void init() {
        try {
            videoLoadingFromAssets = getAssets().openFd("video.3gp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainDashboardLayout = (FrameLayout) findViewById(R.id.mainDashboard);
        searchSong = (ImageView)findViewById(R.id.searchSong);
        listenSong = (ImageView) findViewById(R.id.listenSong);
        toolbar = (Toolbar)findViewById(R.id.toolbar_dashboard);
        videoLoader = (ImageView)findViewById(R.id.videoLoader);
        etSearchSong = (EditText)findViewById(R.id.et_searchSong);
        mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
        fragmentLayout = (LinearLayout)findViewById(R.id.fragmentView);
        albumSearchLayout = (LinearLayout)findViewById(R.id.albumSearchView);
        decimalFormat = new java.text.DecimalFormat("0.00");
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingLayout.addPanelSlideListener(this);
        slidingLayout.setTouchEnabled(true);
        slidingLayout.setMotionEventSplittingEnabled(true);
        slidingLayout.setEnabled(true);
        slidingLayout.setDragView(R.id.dragView);
        initExpandedView();
        copyPlaylistList = new ArrayList<>();
    }

    private void initExpandedView() {
        //Todo: Handle all button click events
        dragLayout = (LinearLayout) findViewById(R.id.dragLayout);
        songName = (TextView) findViewById(R.id.songName_In_onScreen_Layout);
        playlistRecyclerView = (RecyclerView) findViewById(R.id.playlistList);
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        songName.setText(getResources().getString(R.string.Not_Playing));
        songArtistName = (TextView) findViewById(R.id.song_ArtistName_In_onScreen_Layout);
        songArtistName.setText(getResources().getString(R.string.Unknown));
        playlist_Or_pauseButton = (ImageView) findViewById(R.id.pause_play_button_in_onScreen_layout);
        albumImageLayout = (LinearLayout) findViewById(R.id.playbackImage_musicLayout);
        songAlbumImage = (ImageView) findViewById(R.id.songAlbumImage_musicLayout);
        playlistLayout = (RelativeLayout) findViewById(R.id.playlist_musicLayout);
        skipPrevious = (ImageView) findViewById(R.id.skipPrevious_song);
        skipNext     = (ImageView) findViewById(R.id.skipNext_song);
        seekBar = (SeekBar) findViewById(R.id.music_seekbar);
        playPause = (ImageView) findViewById(R.id.playPause_song_musicLayout);
        currentTimer = (TextView) findViewById(R.id.currentTimer);
        seekbarHandler = new Handler();
        decimalFormat = new java.text.DecimalFormat("0.00");
        playPause.setOnClickListener(this);
        skipNext.setOnClickListener(this);
        skipPrevious.setOnClickListener(this);
        seekBar.setEnabled(false);
        playlist_Or_pauseButton.setOnClickListener(this);
        playPause.setOnClickListener(this);

    }

    private void initializeMediaPlayer(SurfaceHolder surfaceHolder) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDisplay(surfaceHolder);
        try {
            mediaPlayer.setDataSource(videoLoadingFromAssets.getFileDescriptor(), videoLoadingFromAssets.getStartOffset(), videoLoadingFromAssets.getLength());
            mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setVolume(0f,0f);
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    Toast.makeText(DashboardActivity.this, "Unable to play video", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(videoLoader!=null && videoLoader.getVisibility() == View.VISIBLE){
                        videoLoader.setVisibility(View.GONE);
                    }
                }
            });
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            Log.e("", "surfaceCreated: "+e.toString() );
            e.printStackTrace();
        }
    }

    public void inflateMusicLayout(SongsModel songsModel){
        if(songsModel!=null){
            songModelReceivedFromMusicPLayer = songsModel;
            inflateExpandedView();
            initialDurationOfSongInSecs = (Integer.parseInt(songModelReceivedFromMusicPLayer.getSongDuration()))/1000;
            try {
                initialDurationOfSongInMins = Float.parseFloat(decimalFormat.format(initialDurationOfSongInSecs/60));
                currentTimer.setText(String.valueOf(decimalFormat.format(initialDurationOfSongInMins)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(seekBar!=null){
                seekBar.setEnabled(true);
                seekBar.setMax((int) initialDurationOfSongInSecs);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser){
                            musicService.forwardSongBySeekbar(progress);
                        }
                        if(progress == seekBar.getMax()){
                            //Full Song Played.,change pause image to play
                            if (copyPlaylistList != null && copyPlaylistList.size() > 0) {

                                if(currentPlayingSong!=null){
                                    SongsModel songsModel = currentPlayingSong;
                                    currentPlayingSong = copyPlaylistList.get(0);
                                    if(copyPlaylistList.contains(currentPlayingSong)) {
                                        copyPlaylistList.remove(currentPlayingSong);
                                    }
                                    inflateMusicLayout(currentPlayingSong);
                                    if(!copyPlaylistList.contains(songsModel)) {
                                        copyPlaylistList.add(songsModel);
                                    }
                                    if(playlistAdapter!=null){
                                        playlistAdapter.notifyDataSetChanged();
                                    }
                                }else{
                                    currentPlayingSong = copyPlaylistList.get(0);

                                }
                            }else {
                                if (playPause.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(DashboardActivity.this, R.drawable.pause_playback).getConstantState()) {
                                    playPause.setImageDrawable(getResources().getDrawable(R.drawable.play_playback));
                                    currentTimer.setText("--:--");
                                }
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
                        seekbarHandler.removeCallbacks(seekbarRunnable);

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        seekbarHandler.postDelayed(seekbarRunnable,1000);
                        fromUser = true;
                    }
                });
            }
            if(!isServiceConnected){
                //Service is UnBounded...
                musicServiceIntent = new Intent(this,MusicService.class);
                bindService(musicServiceIntent,serviceConnection,BIND_AUTO_CREATE);
                startService(musicServiceIntent);
            }else{
                //Service is already connected...Just Change Song...
                musicService.setSongModel(songModelReceivedFromMusicPLayer);
                musicService.playSong(DashboardActivity.this);
                if(playPause.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.play_playback).getConstantState()){
                    playPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_playback));
                }

            }
            if(playlistAdapter!=null){
                playlistAdapter.notifyDataSetChanged();
            }

        }
    }
    public void inflateMusicLayoutFromSavedSongsModel(SongsModel songsModel){
        if(songsModel!=null){
            songModelReceivedFromMusicPLayer = songsModel;
            currentPlayingSong = songModelReceivedFromMusicPLayer;
            setUpValuesFromSongModel();
            initialDurationOfSongInSecs = (Integer.parseInt(songModelReceivedFromMusicPLayer.getSongDuration()))/1000;
            try {
                initialDurationOfSongInMins = Float.parseFloat(decimalFormat.format(initialDurationOfSongInSecs/60));
                currentTimer.setText(String.valueOf(decimalFormat.format(initialDurationOfSongInMins)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(seekBar!=null){
                seekBar.setEnabled(true);
                seekBar.setMax((int) initialDurationOfSongInSecs);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser){
                            musicService.forwardSongBySeekbar(progress);
                        }
                        if(progress == seekBar.getMax()){
                            //Full Song Played.,change pause image to play
                            if(playPause.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(DashboardActivity.this,R.drawable.pause_playback).getConstantState()){
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
                        seekbarHandler.removeCallbacks(seekbarRunnable);

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        seekbarHandler.postDelayed(seekbarRunnable,1000);
                        fromUser = true;
                    }
                });
            }
            if(playlistAdapter!=null){
                playlistAdapter.notifyDataSetChanged();
            }

        }
    }

    private void inflateExpandedView() {
        setUpValuesFromSongModel();
        if(playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.play_playback).getConstantState()){
            playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.pause_playback));
        }
    }

    private void setUpValuesFromSongModel() {
        songName.setText(songModelReceivedFromMusicPLayer.getSongTitle());
        songName.requestFocus();
        songArtistName.setText(songModelReceivedFromMusicPLayer.getSongArtist());
        Bitmap albumCover = BitmapFactory.decodeFile(songModelReceivedFromMusicPLayer.getSongAlbumCover());
        if(albumCover!=null && albumCover.getRowBytes() > 0){
            songAlbumImage.setImageBitmap(albumCover);
        }else{
            songAlbumImage.setImageDrawable(getResources().getDrawable(R.drawable.index));
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if(surfaceHolder!=null) {
            initializeMediaPlayer(surfaceHolder);
            if (videoLoader.getVisibility() == View.GONE) {
                videoLoader.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        surfaceHolder.removeCallback(this);
        surfaceHolder = null;
        mediaPlayer = null;
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
            case R.id.pause_play_button_in_onScreen_layout:
                if(playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.pause_playback).getConstantState()){
                    //Currently playing
                    if(seekBar!=null && seekBar.isEnabled() && musicService!=null){
                        //In secs
                        currentProgress = musicService.getMediaPlayerPos();
                        musicService.pauseMediaPlayer();
                        playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.play_playback));
                    }
                }else if(playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.play_playback).getConstantState()){
                    //Currently Paused
                    if(seekBar!=null && seekBar.isEnabled() && musicService!=null){
                        //In secs
                        musicService.resumePlayback();
                        playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.pause_playback));
                    }else if(seekBar!=null && seekBar.isEnabled() && musicService==null){
                        if(!isServiceConnected){
                            //Service is UnBounded...
                            currentPlayingSong = songModelReceivedFromMusicPLayer;
                            inflateMusicLayout(currentPlayingSong);
                            playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.pause_playback));
                        }
                    }
                }else if(playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.playlist).getConstantState()){
                    //Setup Playlist...
                    if(currentPlayingSong!=null) {
                        if(copyPlaylistList!=null && copyPlaylistList.contains(currentPlayingSong)) {
                            copyPlaylistList.remove(currentPlayingSong);
                        }else if(copyPlaylistList != null && copyPlaylistList.size() == 0){
                            copyPlaylistList = playlistModel.getSongsModelList();
                            if(copyPlaylistList.contains(currentPlayingSong)){
                                copyPlaylistList.remove(currentPlayingSong);
                            }
                        }
                    }else{
                        copyPlaylistList = playlistModel.getSongsModelList();
                    }
                    if(copyPlaylistList!=null && copyPlaylistList.size() >0) {
                        songAlbumImage.setVisibility(View.GONE);
                        playlistLayout.setVisibility(View.VISIBLE);

                        playlistAdapter = new SongsListAdapter(context, copyPlaylistList, new ItemClickListener() {
                            @Override
                            public void itemClick(View view, int position) {
                                SongsModel tempCurrentModel = copyPlaylistList.get(position);
                                if(currentPlayingSong!=null){
                                    if(!copyPlaylistList.contains(currentPlayingSong)) {
                                        copyPlaylistList.add(currentPlayingSong);
                                    }
                                }
                                currentPlayingSong = tempCurrentModel;
                                inflateMusicLayout(currentPlayingSong);
//                                playlistModel.removeCurrentSong(currentPlayingSong);
                                if(copyPlaylistList.contains(currentPlayingSong)) {
                                    copyPlaylistList.remove(currentPlayingSong);
                                }
                                playlistAdapter.notifyDataSetChanged();
                            }
                        }, true, null, this);
                        playlistRecyclerView.setAdapter(playlistAdapter);
                        playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.switch_to_image));
                    }
                }else if(playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.switch_to_image).getConstantState()){
                    //Switch Layouts
                    playlistLayout.setVisibility(View.GONE);
                    songAlbumImage.setVisibility(View.VISIBLE);
                    playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.playlist));
                }
                break;
            case R.id.playPause_song_musicLayout:
                if(playPause.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.pause_playback).getConstantState()){
                    //Currently playing
                    if(seekBar!=null && seekBar.isEnabled() && musicService!=null){
                        //In secs
                        currentProgress = musicService.getMediaPlayerPos();
                        musicService.pauseMediaPlayer();
                        playPause.setImageDrawable(getResources().getDrawable(R.drawable.play_playback));
                    }
                }else{
                    //Currently Paused
                    if(currentPlayingSong == null && copyPlaylistList.size()>0){
                        currentPlayingSong = copyPlaylistList.get(0);
                        if(copyPlaylistList.contains(currentPlayingSong)){
                            copyPlaylistList.remove(currentPlayingSong);
                        }
                        if(copyPlaylistList.size() == 0){
                            albumImageLayout.setVisibility(View.VISIBLE);
                            playlistLayout.setVisibility(View.GONE);
                            playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.playlist));
                        }
                        inflateMusicLayout(currentPlayingSong);
                        if(playlistAdapter!=null){
                            playlistAdapter.notifyDataSetChanged();
                        }
                    }
                    if(seekBar!=null && seekBar.isEnabled() && musicService!=null){
                        //In secs
                        musicService.resumePlayback();
                        playPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_playback));
                    }else if(seekBar!=null && seekBar.isEnabled() && musicService==null){
                        if(!isServiceConnected){
                            //Service is UnBounded...
                            currentPlayingSong = songModelReceivedFromMusicPLayer;
                            inflateMusicLayout(currentPlayingSong);
                            playPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_playback));
                        }
                    }
                }
                break;
            case R.id.listenSong:
                if(listenSong.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.listen_song).getConstantState()){
                    if(mediaPlayer!=null){
                        mediaPlayer.setVolume(0f,0f);
                    }
                    resetAlbumSearchLayout(false);
                    startFingerprinting();
                    listenSong.setImageDrawable(getResources().getDrawable(R.drawable.listenting_song));
                }else{
                    if(mediaPlayer!=null){
                        mediaPlayer.setVolume(0f,0f);
                    }
                    resetAlbumSearchLayout(true);
                    stopFingerprinting();
                    cancel();
                    listenSong.setImageDrawable(getResources().getDrawable(R.drawable.listen_song));

                }
                break;
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
                    fragmentTransaction.replace(R.id.fragmentView, fragMusicSearch);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.skipPrevious_song:
                if (copyPlaylistList != null && copyPlaylistList.size() > 0) {

                    if(currentPlayingSong!=null){
                        SongsModel songsModel = currentPlayingSong;
                        currentPlayingSong = copyPlaylistList.get(copyPlaylistList.size()-1);
                        if(copyPlaylistList.contains(currentPlayingSong)) {
                            copyPlaylistList.remove(currentPlayingSong);
                        }
                        inflateMusicLayout(currentPlayingSong);
                        if(!copyPlaylistList.contains(songsModel)) {
                            copyPlaylistList.add(songsModel);
                        }
                        if(playlistAdapter!=null){
                            playlistAdapter.notifyDataSetChanged();
                        }
                    }else{
                        currentPlayingSong = copyPlaylistList.get(copyPlaylistList.size()-1);
                        inflateMusicLayout(currentPlayingSong);
                        if(copyPlaylistList.contains(currentPlayingSong)) {
                            copyPlaylistList.remove(currentPlayingSong);
                        }
                        if(playlistAdapter!=null){
                            playlistAdapter.notifyDataSetChanged();
                        }

                    }
                }else{
                    initializeSnackBar(getResources().getString(R.string.Current_playlist_is_empty));
                }
                break;
            case R.id.skipNext_song:
                if (copyPlaylistList != null && copyPlaylistList.size() > 0) {

                    if(currentPlayingSong!=null){
                        SongsModel songsModel = currentPlayingSong;
                        currentPlayingSong = copyPlaylistList.get(0);
                        if(copyPlaylistList.contains(currentPlayingSong)) {
                            copyPlaylistList.remove(currentPlayingSong);
                        }
                        inflateMusicLayout(currentPlayingSong);
                        if(!copyPlaylistList.contains(songsModel)) {
                            copyPlaylistList.add(songsModel);
                        }
                        if(playlistAdapter!=null){
                            playlistAdapter.notifyDataSetChanged();
                        }
                    }else{
                        currentPlayingSong = copyPlaylistList.get(0);

                    }
                }else{
                    initializeSnackBar(getResources().getString(R.string.Current_playlist_is_empty));
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityCreatedForFirstTime = false;
        if(mediaPlayer!=null){
            surfaceHolder.removeCallback(this);
            surfaceHolder = null;
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityCreatedForFirstTime = false;
        if(mediaPlayer!=null){
            surfaceHolder.removeCallback(this);
            surfaceHolder = null;
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;

        }
        if(animationDrawable.isRunning()){
            animationDrawable.stop();
        }
        String playlist = gson.toJson(playlistModel);
        if(currentPlayingSong!=null){
            String currentPlayingSongToSave = gson.toJson(currentPlayingSong);
            if(currentPlayingSongSharedPrefs.getString(Constants.CURRENT_SONG_FROM_PREFS,null)!=null &&
                    !currentPlayingSongToSave.equals(currentPlayingSongSharedPrefs.getString(Constants.CURRENT_SONG_FROM_PREFS,null))){
                SharedPreferences.Editor saveCurrentSongEditor = currentPlayingSongSharedPrefs.edit();
                saveCurrentSongEditor.putString(Constants.CURRENT_SONG_FROM_PREFS,currentPlayingSongToSave);
                saveCurrentSongEditor.apply();
            }else if(currentPlayingSongSharedPrefs.getString(Constants.CURRENT_SONG_FROM_PREFS,null)==null){
                SharedPreferences.Editor saveCurrentSongEditor = currentPlayingSongSharedPrefs.edit();
                saveCurrentSongEditor.putString(Constants.CURRENT_SONG_FROM_PREFS,currentPlayingSongToSave);
                saveCurrentSongEditor.apply();
            }
        }
        if(playlistSharedPrefs.getString(Constants.PLAYLIST_FROM_SHARED_PREFS,null) !=null &&
                !playlist.equals(playlistSharedPrefs.getString(Constants.PLAYLIST_FROM_SHARED_PREFS,null))) {
            SharedPreferences.Editor playlistEditor = playlistSharedPrefs.edit();
            playlistEditor.putString(Constants.PLAYLIST_FROM_SHARED_PREFS,playlist);
            playlistEditor.apply();
        }else if(playlistSharedPrefs.getString(Constants.PLAYLIST_FROM_SHARED_PREFS,null) == null){
            SharedPreferences.Editor playlistEditor = playlistSharedPrefs.edit();
            playlistEditor.putString(Constants.PLAYLIST_FROM_SHARED_PREFS,playlist);
            playlistEditor.apply();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView = (SurfaceView) findViewById(R.id.video);
        surfaceHolder = videoView.getHolder();
        surfaceHolder.addCallback(this);

        if(!animationDrawable.isRunning()){
            animationDrawable.start();
        }
        permissions();

    }

    private void setUpContentObserver() {
        setObserverOnActivity(new ObserverListener() {
            @Override
            public void isProcessCompleted(boolean isComplete) {
                if(isComplete){
                    //Change is there
                    //update music model field...
                    callLoaders(DashboardActivity.this);

                }else {
                    //No change
                    //Do nothing, as we have musicModel from Intent....
                }
            }
        },context);
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
                       fragmentTransaction.replace(R.id.fragmentView, mainSongFragment);
                       fragmentTransaction.commit();

                       break;
               }
           }
            if (slidingLayout != null &&
                    (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
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
//Todo: Remove the below file, and fix the keyboard issue:
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
                   fragmentTransaction.replace(R.id.fragmentView, fragMusicSearch, MUSICFRAGSERACH);
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


    private void permissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.WAKE_LOCK)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WAKE_LOCK}, Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS);

            } else if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    || context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    || context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED
                    || context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                    || context.checkSelfPermission(Manifest.permission.WAKE_LOCK)!= PackageManager.PERMISSION_GRANTED) {
                if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Manifest_permission_READ_EXTERNAL_STORAGE);
                }
                if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Manifest_permission_WRITE_EXTERNAL_STORAGE);

                }
                if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, Manifest_permission_RECORD_AUDIO);
                }
                if(context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Manifest_permission_LOCATION);
                }
                if(context.checkSelfPermission(Manifest.permission.WAKE_LOCK)!=PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.WAKE_LOCK},Manifest_permission_WAKE_LOCK);
                }
                setUpInitialHomeFragment();
                setUpContentObserver();
            }
            else{
                setUpContentObserver();
                if(activityCreatedForFirstTime) {
                    setUpInitialHomeFragment();
                }
            }
            return;
        }else{
            setUpContentObserver();
        }
        if(activityCreatedForFirstTime) {
            setUpInitialHomeFragment();
        }
        return;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                    setUpInitialHomeFragment();
                    setUpContentObserver();
                } else {
                    // User refused to grant permission.
                    permissions();
                }
                break;
            case Manifest_permission_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // User refused to grant permission.
                    permissions();
                }
                break;
            case Manifest_permission_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // User refused to grant permission.
                    permissions();
                }
                break;
            case Manifest_permission_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // User refused to grant permission.
                    permissions();
                }
                break;
            case Manifest_permission_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // User refused to grant permission.
                    permissions();
                }
                break;
            case Manifest_permission_WAKE_LOCK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // User refused to grant permission.
                    permissions();
                }
                break;
        }
    }


    @Override
    public void onResult(String s) {
        resetListenSongVisualizationLayout();
        audioFingerPrintingResultModel = new AudioFingerPrintingResultModel();
        List<AudioFingerPrintingResultMusicModel> musicList = new ArrayList<>();
        List<AudioFingerprintResultsGenreModel> genreList = new ArrayList<>();
        List<AudioFingerprintResultsArtistModel> artistList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            if(jsonObject.has("status")){
                JSONObject statusObject = jsonObject.getJSONObject("status");
                if(String.valueOf(statusObject.get("msg")).equalsIgnoreCase(context.getResources().getString(R.string.Success)) && ((Integer)(statusObject.get("code"))).equals(Integer.valueOf(0))){
                    //Success
                    audioFingerPrintingResultModel.setErrorCode(String.valueOf((Integer)statusObject.get("code")));
                    audioFingerPrintingResultModel.setErrorMsg(String.valueOf(statusObject.get("msg")));
                    if(jsonObject.has("metadata")){
                        JSONObject metaData = jsonObject.getJSONObject("metadata");
                        if(metaData.has("music")){
                            JSONArray musicArray = metaData.getJSONArray("music");
                            for(int i=0;i<musicArray.length();i++){
                                JSONObject musicObject = (JSONObject) musicArray.get(i);
                                AudioFingerPrintingResultMusicModel music = new AudioFingerPrintingResultMusicModel();
                                if(musicObject.has("artists")) {
                                    JSONArray artistArray = musicObject.getJSONArray("artists");
                                    for (int j=0;j<artistArray.length();j++){
                                        AudioFingerprintResultsArtistModel artist = new AudioFingerprintResultsArtistModel();
                                        JSONObject artistObject = (JSONObject) artistArray.get(j);
                                        artist.setArtistName(String.valueOf(artistObject.get("name")));
                                        artistList.add(artist);
                                    }
                                    music.setArtistModel(artistList);
                                }
                                if(musicObject.has("title")){
                                    music.setSongTitle(String.valueOf(musicObject.get("title")));
                                }
                                if(musicObject.has("genres")){
                                    JSONArray genreArray = musicObject.getJSONArray("genres");
                                    for (int j=0;j<genreArray.length();j++){
                                        AudioFingerprintResultsGenreModel genre = new AudioFingerprintResultsGenreModel();
                                        JSONObject genreObject = (JSONObject) genreArray.get(j);
                                        genre.setGenreName(String.valueOf(genreObject.get("name")));
                                        genreList.add(genre);
                                    }
                                    music.setGenreModel(genreList);
                                }
                                JSONObject albumObject = musicObject.getJSONObject("album");
                                music.setAlbum(String.valueOf(albumObject.get("name")));
                                musicList.add(music);
                            }
                            audioFingerPrintingResultModel.setMusicList(musicList);
                            fingerprintingRecordsFound(audioFingerPrintingResultModel);
                        }else{
                            initializeSnackBar(getResources().getString(R.string.no_match_found_of_fingerprinting));
                        }
                    }else{
                        initializeSnackBar(getResources().getString(R.string.no_match_found_of_fingerprinting));
                    }

                }else{
                    //Failure
                    audioFingerPrintingResultModel.setErrorCode(String.valueOf((Integer)statusObject.get("code")));
                    audioFingerPrintingResultModel.setErrorMsg(String.valueOf(statusObject.get("msg")));
                    audioFingerPrintingResultModel.setMusicList(null);
                    initializeSnackBar(getResources().getString(R.string.error_occurred_in_fingerprinting));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void fingerprintingRecordsFound(AudioFingerPrintingResultModel resultModel) {
        albumSearchLayout.removeAllViews();
        View view = LayoutInflater.from(context).inflate(R.layout.fingerprint_results,null);
        ImageView close = (ImageView) view.findViewById(R.id.closeRessults);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAlbumSearchLayout(true);

            }
        });
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.fingerprintResultsPager);
        FingerprintResultsAdapter fingerprintResultsAdapter = new FingerprintResultsAdapter(context, resultModel, new FingerprintResultsAdapter.FingerprintResultsClickListener() {
            @Override
            public void onPagerClick(View view, int pos) {
                //Todo: Handle Click Events Here...
            }
        });
        viewPager.setAdapter(fingerprintResultsAdapter);
        if(view.getParent()!=null){
            ((ViewGroup)view.getParent()).removeView(view);
        }
        LinearLayout.LayoutParams albumParams = (LinearLayout.LayoutParams) albumSearchLayout.getLayoutParams();
        albumParams.height = 0;
        albumParams.weight = 3.5f;
        albumSearchLayout.setLayoutParams(albumParams);
        LinearLayout.LayoutParams fragmentLayoutLayoutParams = (LinearLayout.LayoutParams) fragmentLayout.getLayoutParams();
        fragmentLayoutLayoutParams.height = 0;
        fragmentLayoutLayoutParams.weight = 3.5f;
        fragmentLayout.setLayoutParams(fragmentLayoutLayoutParams);
        albumSearchLayout.addView(view,0);
        mainDashboardLayout.postInvalidate();

    }

    private void resetAlbumSearchLayout(boolean wantToResetLayout) {
        LinearLayout.LayoutParams albumParams = (LinearLayout.LayoutParams) albumSearchLayout.getLayoutParams();
        albumParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        if(!wantToResetLayout) {
            albumParams.height = 0;
            albumParams.weight = 3.0f;
        }else{
            albumParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        albumSearchLayout.setLayoutParams(albumParams);
        LinearLayout.LayoutParams fragmentLayoutLayoutParams = (LinearLayout.LayoutParams) fragmentLayout.getLayoutParams();
        if(!wantToResetLayout) {
            fragmentLayoutLayoutParams.height = 0;
            fragmentLayoutLayoutParams.weight = 4.0f;
        }else{
            fragmentLayoutLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        fragmentLayout.setLayoutParams(fragmentLayoutLayoutParams);
        albumSearchLayout.removeAllViews();
    }

    private void resetListenSongVisualizationLayout() {
        if(listenSong.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.listenting_song).getConstantState()) {
            if(mediaPlayer!=null){
                mediaPlayer.setVolume(0f,0f);
            }
            albumSearchLayout.removeAllViews();
            stopFingerprinting();
            cancel();
            listenSong.setImageDrawable(getResources().getDrawable(R.drawable.listen_song));
        }


    }

    @Override
    public void onVolumeChanged(double v) {
        audioVisualize.changeVisualization(true,(float)v);
    }

    @Override
    protected void onDestroy() {
        if (this.mClient != null) {
            this.mClient.release();
            this.initState = false;
            this.mClient = null;
        }
        stopService(musicServiceIntent);
        musicService = null;
        super.onDestroy();

    }

    private void startFingerprinting(){
        if (!this.initState) {
            Toast.makeText(this, "Oops!, an error seems to occur.Try Again Later!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mProcessing) {
            mProcessing = true;
            if (this.mClient == null || !this.mClient.startRecognize()) {
                mProcessing = false;
            }
            startTime = System.currentTimeMillis();
        }
    }

    private void stopFingerprinting(){
        if (mProcessing && this.mClient != null) {
            this.mClient.stopRecordToRecognize();
        }
        mProcessing = false;
        stopTime = System.currentTimeMillis();
//        audioVisualize.changeVisualization(false,0f);
    }
    private void cancel() {
        if (mProcessing && this.mClient != null) {
            mProcessing = false;
            this.mClient.cancel();
        }
    }


    @Override
    public void onSongClick(SongsModel song, boolean isExpanded) {
        //Todo: i think the duplicacy is not handled here...
        inflateMusicLayout(song);
        if(playlistModel!=null){
            if(currentPlayingSong!=null) {
                if(!copyPlaylistList.contains(currentPlayingSong)) {
                    copyPlaylistList.add(currentPlayingSong);
                }
            }
            currentPlayingSong = song;
            //Reduntant check of currentPlaying song in copyList
            if(copyPlaylistList.contains(currentPlayingSong)){
                copyPlaylistList.remove(currentPlayingSong);
            }
            if(playlistAdapter!=null){
                playlistAdapter.notifyDataSetChanged();
            }
            playlistModel.putSong(song);
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        if(previousState == SlidingUpPanelLayout.PanelState.EXPANDED && newState == SlidingUpPanelLayout.PanelState.DRAGGING){
            if(playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.playlist).getConstantState()
                    || playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.switch_to_image).getConstantState()){
                Drawable expandedDrawable = playPause.getDrawable();
                playlist_Or_pauseButton.setImageDrawable(expandedDrawable);
            }
        }else if(previousState == SlidingUpPanelLayout.PanelState.COLLAPSED && newState == SlidingUpPanelLayout.PanelState.DRAGGING){
            if(playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.play_playback).getConstantState()
                    || playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.pause_playback).getConstantState()){
                    playPause.setImageDrawable(playlist_Or_pauseButton.getDrawable());
                if(playlistLayout.getVisibility() == View.VISIBLE) {
                    playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.switch_to_image));
                }else{
                    playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.playlist));
                }
            }
        }
    }


    @Override
    public void addInPlaylist(SongsModel songsModel, int pos) {
        if(playlistModel!=null){
             if(!copyPlaylistList.contains(songsModel)) {
                 if((currentPlayingSong != null && !(currentPlayingSong.getSongId().equalsIgnoreCase(songsModel.getSongId()))) || currentPlayingSong == null) {
                     initializeSnackBar(getResources().getString(R.string.Song_added_to_playlist));
                     copyPlaylistList.add(songsModel);
                 }
             }

            playlistModel.putSong(songsModel);
            if(copyPlaylistList.size()>1){
                 if(playlistAdapter!=null){
                     playlistAdapter.notifyDataSetChanged();
                 }
             }
             if(copyPlaylistList.size() == 1){
                 if(currentPlayingSong!=null){

                     if(playlistAdapter!=null){
                         playlistAdapter.notifyDataSetChanged();
                     }
                 }else{
                     currentPlayingSong = songsModel;
                     inflateMusicLayout(songsModel);
                 }
             }

        }

    }

    @Override
    public void deleteFromPlaylist(SongsModel songsModel, int pos) {
        if(playlistModel!=null){
            playlistModel.removeSong(songsModel);
            if(currentPlayingSong != songsModel){
                if(copyPlaylistList.contains(songsModel)) {
                    copyPlaylistList.remove(songsModel);
                }
                if(copyPlaylistList.size() == 0){
                    songAlbumImage.setVisibility(View.VISIBLE);
                    playlistLayout.setVisibility(View.GONE);
                    playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.playlist));
                }
            }else{
            }
            initializeSnackBar(getResources().getString(R.string.Song_removed_from_playlist));
            if(playlistAdapter!=null){
                playlistAdapter.notifyDataSetChanged();
            }
        }
    }

    public void initializeSnackBar(String str){
        Snackbar snackbar = Snackbar.make(mainDashboardLayout,str,Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
