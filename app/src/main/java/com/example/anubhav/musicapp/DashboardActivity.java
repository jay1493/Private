package com.example.anubhav.musicapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
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
import com.example.anubhav.musicapp.MusicPLayerComponents.MusicBackgroundService;
import com.example.anubhav.musicapp.MusicPLayerComponents.MusicService;
import com.example.anubhav.musicapp.Observers.MySongsObserver;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.utils.SpotlightSequence;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;


/**
 * Created by anubhav on 19/2/17.
 */

public class DashboardActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, View.OnClickListener, TextView.OnEditorActionListener, IACRCloudListener,MainChildSongsFragment.SongClickListener, SlidingUpPanelLayout.PanelSlideListener,MainChildSongsFragment.SongAddToPlaylistListener, SongsListAdapter.SongOptionsDeleteFromPlaylistListener, AudioManager.OnAudioFocusChangeListener {

    public static final String MUSICFRAGSERACH = "MUSICFRAGSERACH";
    public static final String MUSICFRAG = "MUSICFRAG";
    private static final String songFetchKey = "SongKey";
    private static final String MUSICFRAGSERACH_BACKSTACK = "MUSICFRAGSERACH_BACKSTACK";
    private SurfaceView videoView;
    private android.widget.MediaController mediaController;
    private ImageView videoLoader;
    private Toolbar toolbar;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private EditText etSearchSong;
    private LinearLayout mainLayout;
    private MainSongsFragment mainSongFragment;
    private static FragmentManager fragmentManager;
    private ImageView searchSong,listenSong;
    private static Context context;
    private static FrameLayout mainDashboardLayout;
    private final int Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS = 1991;
    private final int Manifest_permission_READ_EXTERNAL_STORAGE = 1992;
    private final int Manifest_permission_WRITE_EXTERNAL_STORAGE = 1993;
    private final int Manifest_permission_RECORD_AUDIO = 1994;
    private final int Manifest_permission_LOCATION = 1995;
    private final int Manifest_permission_WAKE_LOCK =1996;
    private final int Manifest_permission_READ_PHONE_STATE = 1997;
    //    private final int Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS = 1991;
    private static final String 		appString				= Constants.APP_NAME;

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
    public static MusicModel musicModel;
    private MySongsObserver mySongsObserver;
    /**
     *
     * @MusicPlayer params
     */
    private SongsModel songModelReceivedFromMusicPLayer;
    private TextView songName,songArtistName,currentTimer,selectedSongName, selectedSongArtistName;
    private ImageView playlist_Or_pauseButton,songAlbumImage,playPause,selectedSongAlbumImage,selectedSongOptions;
    private FrameLayout albumImageLayout;
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
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                seekBar.setProgress(musicService.getMediaPlayerPos()/1000,true);
                            }else{
                                seekBar.setProgress(musicService.getMediaPlayerPos()/1000);
                            }
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
    private int searchedMusicPosition = -1;
    private int previousSongIndex = 1;
    private ImageView songImageOnScreen;
    private ImageView albumBlurImage;
    private boolean backgroundServiceActive = false;
    private IntentFilter intentFilter;
    private boolean pausedFromCall = false;
    private CustomAudioLoseListener customAudioLoseListener;
    private boolean isRevealEnabled = false;
    private ImageView videoPlaceHolder;
    private Drawable.ConstantState playFilledConstantState;
    private Drawable.ConstantState playlistConstantState;
    private Drawable.ConstantState switchToImageConstatntState;
    private boolean activityCreatedForFirstTime = true;
    private static Fragment attachedFragment;

    private BroadcastReceiver customBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null && intent.getExtras()!=null){
                Bundle bundle = intent.getExtras();
                SongsModel song = (SongsModel) bundle.getSerializable(Constants.BACKGROUND_UPDATE_BROADCAST_MODEL);
                inflateMusicLayoutFromSavedSongsModel(song,-1);

            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dashboard);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        context = this;
        intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BACKGROUND_UPDATE_BROADCAST);
        init();
        setSupportActionBar(toolbar);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(videoLoader);
        Glide.with(this).load(R.raw.ripple1).into(imageViewTarget);
     /*   mainLayout.setBackgroundResource(R.drawable.background_dashboard);
        animationDrawable = (AnimationDrawable)mainLayout.getBackground();
        animationDrawable.start();*/
        etSearchSong.setOnClickListener(this);
        searchSong.setOnClickListener(this);
        etSearchSong.setOnEditorActionListener(this);
        listenSong.setOnClickListener(this);
        setUpAudioFingerPrinting();
        gson = new Gson();
        if(TextUtils.isEmpty(getIntent().getAction())) {
            //FROM STARTING ACTIVITY....
            if (getIntent() != null && getIntent().getExtras() != null) {
                musicModel = (MusicModel) getIntent().getSerializableExtra(Constants.SEND_MUSIC_AS_EXTRA);
            } else {
                musicModel = null;
            }
        }else{
            musicModel = getMusicModel();
        }
        currentPlayingSongSharedPrefs = getSharedPreferences(Constants.CURRENT_SONG_SHARED_PREFS,MODE_PRIVATE);
        if(currentPlayingSongSharedPrefs.getString(Constants.CURRENT_SONG_FROM_PREFS,null)!=null){
            //We have saved current song before...
            currentPlayingSongFromPrefs = gson.fromJson(currentPlayingSongSharedPrefs.getString(Constants.CURRENT_SONG_FROM_PREFS,null),SongsModel.class);
            inflateMusicLayoutFromSavedSongsModel(currentPlayingSongFromPrefs,-1);
        }else{
            currentPlayingSongFromPrefs = null;
        }
        if(getIntent()!=null && !TextUtils.isEmpty(getIntent().getAction()) && getIntent().getAction().equalsIgnoreCase(Constants.ACTION_MAIN_ACTION)){
            registerReceiver(customBroadcastReceiver,intentFilter);
            Intent stopBackground = new Intent(this,MusicBackgroundService.class);
            stopBackground.setAction(Constants.ACTION_STOPFOREGROUND_ACTION);
            startService(stopBackground);
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
     /*   final Handler handler = new Handler(context.getMainLooper());
        fileObserver = new FileObserver(Constants.MUSIC_SAVE_PATH) {
            @Override
            public void onEvent(int event, String path) {
                if(event == FileObserver.CREATE || event == FileObserver.DELETE || event == FileObserver.DELETE_SELF) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(!contentObserverExecuted) {
                                callLoaders(context);
                                Toast.makeText(context, "File Observed", Toast.LENGTH_SHORT).show();
                                musicModel = getMusicModel();
                                MainChildSongsFragment.notifyAdapterFromActivity(musicModel);
                                fileObserverExecuted = true;
                            }
                        }
                    });
                }
            }
        };
        fileObserver.startWatching();*/
        setUpContentObserver();
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        customAudioLoseListener = new CustomAudioLoseListener();
        registerReceiver(customAudioLoseListener,intentFilter);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            isRevealEnabled = true;
        }
        initializeShowcaseForFirstTime();


    }

    private void initializeShowcaseForFirstTime() {
        SpotlightConfig spotlightConfig = new SpotlightConfig();
        spotlightConfig.setIntroAnimationDuration(400);
        spotlightConfig.setRevealAnimationEnabled(isRevealEnabled);
        spotlightConfig.setPerformClick(true);
        spotlightConfig.setFadingTextDuration(400);
        spotlightConfig.setHeadingTvColor(Color.parseColor("#eb273f"));
        spotlightConfig.setHeadingTvSize(32);
        spotlightConfig.setSubHeadingTvColor(Color.parseColor("#ffffff"));
        spotlightConfig.setSubHeadingTvSize(16);
        spotlightConfig.setMaskColor(Color.parseColor("#dc000000"));
        spotlightConfig.setLineAnimationDuration(400);
        spotlightConfig.setLineAndArcColor(Color.parseColor("#eb273f"));
        spotlightConfig.setDismissOnTouch(true);
        spotlightConfig.setDismissOnBackpress(true);
        SpotlightSequence spotlightSequence = SpotlightSequence.getInstance(this,spotlightConfig);
        spotlightSequence.addSpotlight(searchSong,"Want to download a song/video from youtube?","Just enter your song name and click search...and then just click on any video, to generate the download links for that video.","0");
        spotlightSequence.addSpotlight(listenSong,"Want to know which song is playing ?","Just click here...and wait for our application to recognize the current playing song. Fun Right!!","1");
        spotlightSequence.startSequence();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
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
        //noinspection RestrictedApi
        if(fragmentManager.getFragments()!=null && fragmentManager.getFragments().size()>0){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            fragmentTransaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
            fragmentTransaction.replace(R.id.fragmentView, mainSongFragment,MUSICFRAG);
            fragmentTransaction.commit();
        }else{
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
            fragmentTransaction.add(R.id.fragmentView, mainSongFragment,MUSICFRAG);
            fragmentTransaction.commit();
        }
        fragmentManager.executePendingTransactions();

    }



    private void init() {
        try {
            videoLoadingFromAssets = getAssets().openFd("video.3gp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        albumBlurImage = (ImageView) findViewById(R.id.outerAlbumBlurImage);
        mainDashboardLayout = (FrameLayout) findViewById(R.id.mainDashboard);
        searchSong = (ImageView)findViewById(R.id.searchSong);
        listenSong = (ImageView) findViewById(R.id.listenSong);
        toolbar = (Toolbar)findViewById(R.id.toolbar_dashboard);
        videoLoader = (ImageView)findViewById(R.id.videoLoader);
        videoPlaceHolder = (ImageView) findViewById(R.id.videoPlaceHolder);
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
        slidingLayout.setDragView(R.id.dragLayout);
        initExpandedView();
        copyPlaylistList = new ArrayList<>();
        //noinspection RestrictedApi
        playFilledConstantState = AppCompatDrawableManager.get().getDrawable(context,R.drawable.play_filled).getConstantState();
        //noinspection RestrictedApi
        playlistConstantState = AppCompatDrawableManager.get().getDrawable(context, R.drawable.playlist).getConstantState();
        //noinspection RestrictedApi
        switchToImageConstatntState = AppCompatDrawableManager.get().getDrawable(context, R.drawable.switch_to_image).getConstantState();

    }

    private void initExpandedView() {
        dragLayout = (LinearLayout) findViewById(R.id.dragLayout);
        songName = (TextView) findViewById(R.id.songName_In_onScreen_Layout);
        songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        songName.setMarqueeRepeatLimit(-1);
        songName.setClickable(true);
        songName.setSelected(true);
        songImageOnScreen = (ImageView) findViewById(R.id.albumImage_In_OnScreen_Layout);
        playlistRecyclerView = (RecyclerView) findViewById(R.id.playlistList);
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        songName.setText(getResources().getString(R.string.Not_Playing));
        songArtistName = (TextView) findViewById(R.id.song_ArtistName_In_onScreen_Layout);
        songArtistName.setText(getResources().getString(R.string.Unknown));
        Bitmap bitmapInitial = BitmapFactory.decodeResource(getResources(),R.drawable.index);
        Blurry.with(context).radius(10).sampling(8).animate(500).from(bitmapInitial).into(albumBlurImage);
        playlist_Or_pauseButton = (ImageView) findViewById(R.id.pause_play_button_in_onScreen_layout);
        albumImageLayout = (FrameLayout) findViewById(R.id.playbackImage_musicLayout);
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
                    videoPlaceHolder.setVisibility(View.VISIBLE);
                    return false;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(videoLoader!=null && videoLoader.getVisibility() == View.VISIBLE){
                        videoLoader.setVisibility(View.GONE);
                        videoPlaceHolder.setVisibility(View.GONE);
                    }
                }
            });
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    if(percent < 100){
                        Log.d("Muziek", "onBufferingUpdate:");
                        if (videoLoader.getVisibility() == View.GONE) {
                            videoLoader.setVisibility(View.VISIBLE);
                            videoPlaceHolder.setVisibility(View.VISIBLE);
                        }
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
                                //noinspection RestrictedApi
                                if (playPause.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(DashboardActivity.this, R.drawable.pause_playback).getConstantState()) {
                                    playPause.setImageDrawable(getResources().getDrawable(R.drawable.play_filled));
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
                //noinspection RestrictedApi
                if(playPause.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.play_filled).getConstantState()){
                    playPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_playback));
                }

            }
            if(playlistAdapter!=null){
                playlistAdapter.notifyDataSetChanged();
            }

        }
    }
    public void inflateMusicLayoutFromSavedSongsModel(SongsModel songsModel,int currentProgress){
        if(songsModel!=null){
            songModelReceivedFromMusicPLayer = songsModel;
            currentPlayingSong = songModelReceivedFromMusicPLayer;
            setUpValuesFromSongModel();
            if(!TextUtils.isEmpty(songModelReceivedFromMusicPLayer.getSongDuration())) {
                initialDurationOfSongInSecs = (Integer.parseInt(songModelReceivedFromMusicPLayer.getSongDuration())) / 1000;
                try {
                    initialDurationOfSongInMins = Float.parseFloat(decimalFormat.format(initialDurationOfSongInSecs / 60));
                    currentTimer.setText(String.valueOf(decimalFormat.format(initialDurationOfSongInMins)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                initialDurationOfSongInSecs = 0;
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
                            //noinspection RestrictedApi
                            if(playPause.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(DashboardActivity.this,R.drawable.pause_playback).getConstantState()){
                                playPause.setImageDrawable(getResources().getDrawable(R.drawable.play_filled));
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
        //noinspection RestrictedApi
        if(playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.play_filled).getConstantState()){
            playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.pause_playback));
        }
    }

    private void setUpValuesFromSongModel() {
        songName.setText(songModelReceivedFromMusicPLayer.getSongTitle());
        songName.requestFocus();
        songArtistName.setText(songModelReceivedFromMusicPLayer.getSongArtist());
        Bitmap albumCover = BitmapFactory.decodeFile(songModelReceivedFromMusicPLayer.getSongAlbumCover());
        if(albumCover!=null && albumCover.getRowBytes() > 0){
            songImageOnScreen.setImageBitmap(createCircularBitmap(albumCover));
            songImageOnScreen.setAdjustViewBounds(true);
            songAlbumImage.setImageBitmap(albumCover);
//            processBitmapGradient(albumCover);
            Blurry.with(context).radius(10).sampling(8).animate(500).from(albumCover).into(albumBlurImage);
        }else{
            songImageOnScreen.setImageDrawable(getResources().getDrawable(R.drawable.album_placeholder_copy));
            songImageOnScreen.setAdjustViewBounds(true);
            songAlbumImage.setImageDrawable(getResources().getDrawable(R.drawable.index));
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.index);
//            processBitmapGradient(bitmap);
            Blurry.with(context).radius(10).sampling(8).animate(500).from(bitmap).into(albumBlurImage);
        }
    }


    private void processBitmapGradient(final Bitmap albumCover) {
        Palette.from(albumCover).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int color;
                Palette.Swatch vibrantSwatch = palette.getDarkVibrantSwatch();
                int opaqueDarkVibrantColor = vibrantSwatch != null ? vibrantSwatch.getRgb() : 0;
                albumImageLayout.setBackgroundColor(opaqueDarkVibrantColor);
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if(surfaceHolder!=null) {
            initializeMediaPlayer(surfaceHolder);
            if (videoLoader.getVisibility() == View.GONE) {
                videoLoader.setVisibility(View.VISIBLE);
                videoPlaceHolder.setVisibility(View.VISIBLE);
            }
            Log.d("Muziek", "SurfaceCreated:");
        }else{
            videoPlaceHolder.setVisibility(View.VISIBLE);
            Log.d("Muziek", "SurfaceCreated: SurfaceHolder = null");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d("Muziek", "Destroyed");
        surfaceHolder.removeCallback(this);
        surfaceHolder = null;
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        videoPlaceHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Muziek", "Prepared");
        videoPlaceHolder.setVisibility(View.GONE);
        videoView.requestFocus();
        mediaPlayer.start();
        videoLoader.setVisibility(View.GONE);
    }

    @Override
    @SuppressLint("RestrictedApi")
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pause_play_button_in_onScreen_layout:
                //noinspection RestrictedApi
                if(playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.pause_playback).getConstantState()){
                    //Currently playing
                    if(seekBar!=null && seekBar.isEnabled() && musicService!=null){
                        //In secs
                        currentProgress = musicService.getMediaPlayerPos();
                        musicService.pauseMediaPlayer();
                        playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.play_filled));
                    }
                }else if(playlist_Or_pauseButton.getDrawable().getConstantState() == playFilledConstantState){
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
                }else {
                    if(playlist_Or_pauseButton.getDrawable().getConstantState() == playlistConstantState){
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
                            albumImageLayout.setVisibility(View.GONE);
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
                            }, true, null, this,false);
                            playlistRecyclerView.setAdapter(playlistAdapter);
                            playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.switch_to_image));
                        }
                    }else {
                        if(playlist_Or_pauseButton.getDrawable().getConstantState() == switchToImageConstatntState){
                            //Switch Layouts
                            playlistLayout.setVisibility(View.GONE);
                            albumImageLayout.setVisibility(View.VISIBLE);
                            playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.playlist));
                        }
                    }
                }
                break;
            case R.id.playPause_song_musicLayout:
                //noinspection RestrictedApi
                if(playPause.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.pause_playback).getConstantState()){
                    //Currently playing
                    if(seekBar!=null && seekBar.isEnabled() && musicService!=null){
                        //In secs
                        currentProgress = musicService.getMediaPlayerPos();
                        musicService.pauseMediaPlayer();
                        playPause.setImageDrawable(getResources().getDrawable(R.drawable.play_filled));
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
                if(connectedToNetwork()) {
                    //noinspection RestrictedApi
                    if (listenSong.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context, R.drawable.listen_song).getConstantState()) {
                        if (mediaPlayer != null) {
                            mediaPlayer.setVolume(0f, 0f);
                        }
                        resetAlbumSearchLayout(false);
                        startFingerprinting();
                        listenSong.setImageDrawable(getResources().getDrawable(R.drawable.listenting_song));
                    } else {
                        if (mediaPlayer != null) {
                            mediaPlayer.setVolume(0f, 0f);
                        }
                        resetAlbumSearchLayout(true);
                        stopFingerprinting();
                        cancel();
                        albumSearchLayout.removeAllViews();
                        listenSong.setImageDrawable(getResources().getDrawable(R.drawable.listen_song));

                    }
                }else{
                    initializeSnackBar(getResources().getString(R.string.functionality_disabled_as_you_are_not_connected_to_a_network));
                }
                break;
            case R.id.searchSong:
                 searchSong();
                break;
            case R.id.et_searchSong:
                etSearchSong.requestFocus();
                break;
            case R.id.skipPrevious_song:
                if (copyPlaylistList != null && copyPlaylistList.size() > 0) {

                    if(currentPlayingSong!=null){
                        SongsModel songsModel = currentPlayingSong;
                        if(copyPlaylistList.size()-(previousSongIndex)<0) {
                            previousSongIndex = 1;
                        }
                            currentPlayingSong = copyPlaylistList.get(copyPlaylistList.size() - previousSongIndex++);
                            if (copyPlaylistList.contains(currentPlayingSong)) {
                                copyPlaylistList.remove(currentPlayingSong);
                            }
                            inflateMusicLayout(currentPlayingSong);
                            if (!copyPlaylistList.contains(songsModel)) {
                                copyPlaylistList.add(songsModel);
                            }
                            if (playlistAdapter != null) {
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

    private boolean connectedToNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            return true;
        }else{
            return false;
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
            mediaPlayer.reset();
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
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer=null;

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
        getContentResolver().unregisterContentObserver(mySongsObserver);
        if(musicService!=null){
            musicService.setCurrentPlayingSong(currentPlayingSong);
            musicService.setCopyPlaylist(copyPlaylistList);
            musicService.setServiceConnection(serviceConnection);
        }
        if(getIntent()!=null && !TextUtils.isEmpty(getIntent().getAction()) && getIntent().getAction().equalsIgnoreCase(Constants.ACTION_MAIN_ACTION)) {
            unregisterReceiver(customBroadcastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView = (SurfaceView) findViewById(R.id.video);
        surfaceHolder = videoView.getHolder();
        surfaceHolder.addCallback(this);
        permissions();
        videoPlaceHolder.setVisibility(View.VISIBLE);

    }

    private void setUpContentObserver() {
        mySongsObserver = new MySongsObserver(new Handler(context.getMainLooper()),musicModel,new ObserverListener(){

            @Override
            public void isProcessCompleted(boolean isComplete) {
                if(isComplete) {

                        callLoaders(DashboardActivity.this);
                        Log.d("Dashboard", "isProcessCompleted: StartLoader");
//                      Toast.makeText(context, "Observer Observed", Toast.LENGTH_SHORT).show();
                        return;
                }else{
                    Log.d("Dashboard", "isProcessCompleted: SavedModel");
                    return;
                }
            }
        });
        getContentResolver().registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,true,mySongsObserver);
        Log.d("", "isProcessCompleted: OutsideFunction");
    }

    @Override
    public void onBackPressed() {
        if (slidingLayout != null &&
                (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }else {
            etSearchSong.setText("");
            etSearchSong.clearFocus();
            if(fragmentManager.findFragmentByTag("MUSICFRAG").isVisible()){
              moveTaskToBack(true);
            }else {
                super.onBackPressed();
            }
        }
       /* if(fragmentManager.getBackStackEntryCount()>0) {
            if(fragmentManager.getBackStackEntryCount() == 1 && attachedFragment.getTag().equalsIgnoreCase(fragmentManager.getBackStackEntryAt(0).getName())){
                //We are on the same fragment:
                super.onBackPressed();
                return;
            }
           for(int i = fragmentManager.getBackStackEntryCount()-1;i>=0;i--){
               FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
               FragmentManager.BackStackEntry backStackEntry= fragmentManager.getBackStackEntryAt(i);
               switch (backStackEntry.getName()){
                   case MUSICFRAG:
                       fragmentTransaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
                       fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                       fragmentTransaction.replace(R.id.fragmentView, mainSongFragment);
                       fragmentTransaction.commit();
                       etSearchSong.setText("");
                       etSearchSong.clearFocus();
                       break;
               }
           }
            if (slidingLayout != null &&
                    (slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        }else{
            super.onBackPressed();
        }*/

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
        if(!etSearchSong.getText().toString().trim().equalsIgnoreCase("")){
            if(searchSongInPrivateLibrary(etSearchSong.getText().toString().trim())){
                if(searchedMusicPosition != -1){
                    MainSongsFragment mainSongsFragment = (MainSongsFragment) getSupportFragmentManager().findFragmentByTag(MUSICFRAG);
                    if(mainSongsFragment!=null && mainSongsFragment.isVisible()){
                        MainSongsFragment.setCurrentViewPagerItem(getResources().getString(R.string.Songs),searchedMusicPosition);
                    }
                }

            }else{
                if(connectedToNetwork()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(songFetchKey, etSearchSong.getText().toString().trim());
                    FragMusicSearch fragMusicSearch = FragMusicSearch.getInstance(bundle);
                    etSearchSong.clearFocus();
                    if (fragMusicSearch.isVisible()) {
                        fragMusicSearch.performSearch(etSearchSong.getText().toString().trim());
                    } else {
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        fragmentTransaction.replace(R.id.fragmentView, fragMusicSearch, MUSICFRAGSERACH);
                        fragmentTransaction.addToBackStack(MUSICFRAGSERACH_BACKSTACK);
                        fragmentTransaction.commit();

                    }
                }else{
                    initializeSnackBar(getResources().getString(R.string.functionality_disabled_as_you_are_not_connected_to_a_network));
                }
            }
        }else{
            initializeSnackBar(getResources().getString(R.string.Enter_a_song_name_to_start_query));
        }


    }

    private boolean searchSongInPrivateLibrary(String query) {
        if(musicModel!=null && musicModel.getAllSongs()!=null && musicModel.getAllSongs().size()>0){
            for(SongsModel songsModel: musicModel.getAllSongs()){
                if(songsModel.getSongTitle().trim().equalsIgnoreCase(query) || songsModel.getSongTitle().contains(query)){
                    searchedMusicPosition =  musicModel.getAllSongs().indexOf(songsModel);
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        attachedFragment = fragment;
    }


    private void permissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WAKE_LOCK, Manifest.permission.READ_PHONE_STATE}, Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS);

            } else if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || context.checkSelfPermission(Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED
                    || context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Manifest_permission_READ_EXTERNAL_STORAGE);
                }
                if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Manifest_permission_WRITE_EXTERNAL_STORAGE);

                }
                if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, Manifest_permission_RECORD_AUDIO);
                }
                if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Manifest_permission_LOCATION);
                }
                if (context.checkSelfPermission(Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WAKE_LOCK}, Manifest_permission_WAKE_LOCK);
                }
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, Manifest_permission_READ_PHONE_STATE);
                }
                setUpInitialHomeFragment();
            }
            else{
                if(activityCreatedForFirstTime) {
                    setUpInitialHomeFragment();
                }
            }
            return;
        }else{
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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED && grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                    setUpInitialHomeFragment();
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
            case Manifest_permission_READ_PHONE_STATE:
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
                TextView songTitle = (TextView) view.findViewById(R.id.textFlipperItem);
                etSearchSong.setText(songTitle.getText().toString().trim());
                resetAlbumSearchLayout(true);

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
//            albumParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            albumParams.height = 0;
            albumParams.weight = 0f;

        }
        albumSearchLayout.setLayoutParams(albumParams);
        LinearLayout.LayoutParams fragmentLayoutLayoutParams = (LinearLayout.LayoutParams) fragmentLayout.getLayoutParams();
        if(!wantToResetLayout) {
            fragmentLayoutLayoutParams.height = 0;
            fragmentLayoutLayoutParams.weight = 4.0f;
        }else{
            fragmentLayoutLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            fragmentLayoutLayoutParams.weight = 7.0f;
        }
        fragmentLayout.setLayoutParams(fragmentLayoutLayoutParams);
        albumSearchLayout.removeAllViews();
    }

    private void resetListenSongVisualizationLayout() {
        //noinspection RestrictedApi
        if(listenSong.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.listenting_song).getConstantState()) {
            if(mediaPlayer!=null){
                mediaPlayer.setVolume(0f,0f);
            }
            resetAlbumSearchLayout(true);
            stopFingerprinting();
            cancel();
            albumSearchLayout.removeAllViews();
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
        if(musicService!=null){
            unbindService(serviceConnection);
//            stopService(musicServiceIntent);
            musicService = null;
        }
        unregisterReceiver(customAudioLoseListener);
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

          /*// TODO: 9/5/17 : Commented the status bar tranceparancy code, as we have put static transclucent status bar now..
            Window w = getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            ===============================*/
            //Todo: Below commented line is used to tell Lint to skip inspection of its below line:
            //noinspection RestrictedApi
            if(playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.playlist).getConstantState()
                    || playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.switch_to_image).getConstantState()){
                Drawable expandedDrawable = playPause.getDrawable();
                playlist_Or_pauseButton.setImageDrawable(expandedDrawable);

            }
        }else if(previousState == SlidingUpPanelLayout.PanelState.COLLAPSED && newState == SlidingUpPanelLayout.PanelState.DRAGGING){
            //noinspection RestrictedApi
            if(playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.play_filled).getConstantState()
                    || playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.pause_playback).getConstantState()){
                    playPause.setImageDrawable(playlist_Or_pauseButton.getDrawable());
                if(playlistLayout.getVisibility() == View.VISIBLE) {
                    playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.switch_to_image));
                }else{
                    playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.playlist));
                }

            }
        }else if(previousState == SlidingUpPanelLayout.PanelState.DRAGGING && newState == SlidingUpPanelLayout.PanelState.EXPANDED){


                /*TODO: 9/5/17 : Commented the status bar tranceparancy code, as we have put static transclucent status bar now..
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                String dynamicColor = "#181616";
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor(dynamicColor));
                }
                ===================================================*/

            //noinspection RestrictedApi
            if(playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.play_filled).getConstantState()
                    || playlist_Or_pauseButton.getDrawable().getConstantState() == AppCompatDrawableManager.get().getDrawable(context,R.drawable.pause_playback).getConstantState()) {
                if(playlistLayout.getVisibility() == View.VISIBLE){
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
                    albumImageLayout.setVisibility(View.VISIBLE);
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
    public static void initializeSnackBarFromService(String str){
        Snackbar snackbar = Snackbar.make(mainDashboardLayout,str,Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private Bitmap createCircularBitmap(Bitmap bitmap){
        if(bitmap == null){
            return null;
        }
        int size = Math.min(bitmap.getWidth(),bitmap.getHeight());
        int x = (bitmap.getWidth()-size)/2;
        int y = (bitmap.getHeight()-size)/2;
        Bitmap output = Bitmap.createBitmap(bitmap,x,y,size,size);
        Bitmap canvasBitmap = Bitmap.createBitmap(size,size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBitmap);
        Paint paint = new Paint();
        //overlay this canvas with the image, which we made in squared shape.
        paint.setShader(new BitmapShader(output, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        //Now draw circle from canvas(which would lie inside rectangular area
        canvas.drawCircle(r, r, r, paint);
        return canvasBitmap;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
        {
            // Pause
        }
        else if(focusChange == AudioManager.AUDIOFOCUS_GAIN)
        {
            if(musicService!=null && !musicService.isMediaPlayerRunning()) {
                musicService.resumePlayback();
                playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.pause_playback));

            }
        }
        else if(focusChange == AudioManager.AUDIOFOCUS_LOSS)
        {
            if(musicService!=null && musicService.isMediaPlayerRunning()) {
                currentProgress = musicService.getMediaPlayerPos();
                musicService.pauseMediaPlayer();
                playlist_Or_pauseButton.setImageDrawable(getResources().getDrawable(R.drawable.play_filled));
            }
        }
    }


    public class CustomAudioLoseListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(new CustomAudioLoseListener.CustomPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
        }
        class CustomPhoneStateListener extends PhoneStateListener {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                try{
                    switch (state){
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                            if(musicService!=null && musicService.isMediaPlayerRunning()){
                                //PAUSE
                                pausedFromCall = true;
                                musicService.pauseMediaPlayer();
                                playPause.setImageDrawable(getResources().getDrawable(R.drawable.play_filled));

                            }
                            break;
                        case TelephonyManager.CALL_STATE_RINGING:
                            if(musicService!=null && musicService.isMediaPlayerRunning()){
                                //PAUSE
                                pausedFromCall = true;
                                musicService.pauseMediaPlayer();
                                playPause.setImageDrawable(getResources().getDrawable(R.drawable.play_filled));
                            }
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            if(musicService!=null && musicService.isMediaPlayerRunning() && pausedFromCall){
                                //PLAY
                                pausedFromCall = false;
                                musicService.resumePlayback();
                                playPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_playback));
                            }
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }

    public static void refershLayout(MusicModel newModel){
        if(newModel!=null) {
            musicModel = newModel;

            if (fragmentManager.findFragmentByTag("MUSICFRAG").isVisible()) {
                Bundle bundle = null;
                if (musicModel != null) {
                    bundle = new Bundle();
                    bundle.putSerializable(Constants.SEND_MUSIC_AS_EXTRA, musicModel);
                }
                Toast.makeText(context, "Inside refersh", Toast.LENGTH_SHORT).show();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragmentManager.findFragmentByTag("MUSICFRAG"));
                MainSongsFragment mainSongFragment = MainSongsFragment.getInstance(bundle);
                fragmentTransaction.add(R.id.fragmentView, mainSongFragment, MUSICFRAG);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.addToBackStack(MUSICFRAG);
                fragmentTransaction.commit();
            } else {

            }
        }
    }

}
