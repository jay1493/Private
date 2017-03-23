package com.example.anubhav.musicapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.anubhav.musicapp.Fragments.FragMusic;
import com.example.anubhav.musicapp.Fragments.FragMusicSearch;
import com.example.anubhav.musicapp.Fragments.MainSongsFragment;
import com.example.anubhav.musicapp.GNSDKComp.*;
import com.gracenote.gnsdk.GnDescriptor;
import com.gracenote.gnsdk.GnError;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnLanguage;
import com.gracenote.gnsdk.GnLicenseInputMode;
import com.gracenote.gnsdk.GnList;
import com.gracenote.gnsdk.GnLocale;
import com.gracenote.gnsdk.GnLocaleGroup;
import com.gracenote.gnsdk.GnLookupData;
import com.gracenote.gnsdk.GnLookupLocalStream;
import com.gracenote.gnsdk.GnManager;
import com.gracenote.gnsdk.GnMic;
import com.gracenote.gnsdk.GnMusicIdStream;
import com.gracenote.gnsdk.GnMusicIdStreamIdentifyingStatus;
import com.gracenote.gnsdk.GnMusicIdStreamPreset;
import com.gracenote.gnsdk.GnMusicIdStreamProcessingStatus;
import com.gracenote.gnsdk.GnRegion;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.gracenote.gnsdk.GnStatus;
import com.gracenote.gnsdk.GnStorageSqlite;
import com.gracenote.gnsdk.GnUser;
import com.gracenote.gnsdk.GnUserStore;
import com.gracenote.gnsdk.IGnCancellable;
import com.gracenote.gnsdk.IGnMusicIdStreamEvents;
import com.gracenote.gnsdk.IGnSystemEvents;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

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
    private MainSongsFragment mainSongFragment;
    private FragmentManager fragmentManager;
    private AnimationDrawable animationDrawable;
    private ImageView searchSong,listenSong;
    private Context context;
    private LinearLayout mainDashboardLayout;
    private final int Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS = 1991;
    private final int Manifest_permission_READ_EXTERNAL_STORAGE = 1992;
    private final int Manifest_permission_WRITE_EXTERNAL_STORAGE = 1993;
    private final int Manifest_permission_RECORD_AUDIO = 1994;
//    private final int Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS = 1991;

    /**
     *
     * GNSDK Params...
     */
    static final String 				gnsdkClientId 			= Constants.GNSDK_CLIENT_ID;
    static final String 				gnsdkClientTag 			= Constants.GNSDK_CLIENT_TAG;
    static final String 				gnsdkLicenseFilename 	= "licence.txt";
    private static final String    		gnsdkLogFilename 		= "sample.log";
    private static final String 		appString				= Constants.APP_NAME;
    private String gnsdkLicence ="";
    private GnManager gnManager;
    private GnUser gnUser;
    private AudioVisualizeAdapter gnMicrophone;
    private GnMusicIdStream gnMusicIdStream;
    private AudioVisualDisplay audioVisualDisplay;

    protected volatile boolean 			lastLookup_local		 = false;	// indicates whether the match came from local storage
    protected volatile long				lastLookup_matchTime 	 = 0;  		// total lookup time for query
    protected volatile long				lastLookup_startTime;  				// start time of query
    private volatile boolean			audioProcessingStarted   = false;
    private volatile boolean			analyzingCollection 	 = false;
    private volatile boolean			analyzeCancelled 	 	 = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dashboard);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mainSongFragment = MainSongsFragment.getInstance(null);
        context = this;
        init();
        settingUpGnSDK();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(videoLoader);
        Glide.with(this).load(R.raw.ripple1).into(imageViewTarget);
        /*videoView.initialize(getResources().getString(R.string.youtubeAPI),this);*/
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.mainLayout, mainSongFragment,MUSICFRAG);
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
        listenSong.setOnClickListener(this);

    }

    private void settingUpGnSDK() {
        audioVisualDisplay = new AudioVisualDisplay((Activity)context,mainLayout,0,mainDashboardLayout);
        gnsdkLicence = loadAssetAsString(gnsdkLicenseFilename);
        try {
            gnManager = new GnManager( context, gnsdkLicence, GnLicenseInputMode.kLicenseInputModeString );
            gnManager.systemEventHandler( new SystemEvents());
            gnUser = new GnUser( new GnUserStore(context), gnsdkClientId, gnsdkClientTag, appString );
            GnStorageSqlite.enable();
            GnLookupLocalStream.enable();
            Thread localeThread = new Thread(
                    new LocaleLoadRunnable(GnLocaleGroup.kLocaleGroupMusic,
                            GnLanguage.kLanguageEnglish,
                            GnRegion.kRegionGlobal,
                            GnDescriptor.kDescriptorDefault,
                            gnUser)
            );
            localeThread.start();
            Thread ingestThread = new Thread( new LocalBundleIngestRunnable(context) );
            ingestThread.start();
            gnMicrophone = new AudioVisualizeAdapter( new GnMic(),audioVisualDisplay);
            gnMusicIdStream = new GnMusicIdStream( gnUser, GnMusicIdStreamPreset.kPresetMicrophone, new MusicIDStreamEvents() );
            gnMusicIdStream.options().lookupData(GnLookupData.kLookupDataContent, true);
            gnMusicIdStream.options().lookupData(GnLookupData.kLookupDataSonicData, true);
            gnMusicIdStream.options().resultSingle( true );


        } catch (GnException e) {
            e.printStackTrace();
        }
    }

    private String loadAssetAsString(String gnsdkLicenseFilename) {
        InputStream inputStream = null;
        String assetString ="";
        try {
            inputStream  =  this.getAssets().open(gnsdkLicenseFilename);
            if(inputStream!=null){
                java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");

                assetString = s.hasNext() ? s.next() : "";
                inputStream.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return assetString;
    }


    private void init() {
        mainDashboardLayout = (LinearLayout)findViewById(R.id.mainDashboard);
        searchSong = (ImageView)findViewById(R.id.searchSong);
        listenSong = (ImageView) findViewById(R.id.listenSong);
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
            case R.id.listenSong:
                try {
                    audioVisualDisplay.setDisplay(true,false);

                    gnMusicIdStream.identifyAlbumAsync();
                    if ( gnMusicIdStream != null ) {

                        // Create a thread to process the data pulled from GnMic
                        // Internally pulling data is a blocking call, repeatedly called until
                        // audio processing is stopped. This cannot be called on the main thread.
                        Thread audioProcessThread = new Thread(new AudioProcessRunnable());
                        audioProcessThread.start();

                    }
                } catch (GnException e) {
                    e.printStackTrace();
                }
                lastLookup_startTime = SystemClock.elapsedRealtime();
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

        if ( gnMusicIdStream != null ) {

            try {

                // to ensure no pending identifications deliver results while your app is
                // paused it is good practice to call cancel
                // it is safe to call identifyCancel if no identify is pending
                gnMusicIdStream.identifyCancel();

                // stopping audio processing stops the audio processing thread started
                // in onResume
                gnMusicIdStream.audioProcessStop();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!animationDrawable.isRunning()){
            animationDrawable.start();
        }
        if ( gnMusicIdStream != null ) {

            // Create a thread to process the data pulled from GnMic
            // Internally pulling data is a blocking call, repeatedly called until
            // audio processing is stopped. This cannot be called on the main thread.
            Thread audioProcessThread = new Thread(new AudioProcessRunnable());
            audioProcessThread.start();

        }
        permissions();
    }
    class AudioProcessRunnable implements Runnable {

        @Override
        public void run() {
            try {

                // start audio processing with GnMic, GnMusicIdStream pulls data from GnMic internally
                gnMusicIdStream.audioProcessStart( gnMicrophone );

            } catch (GnException e) {

                Log.e( appString, e.errorCode() + ", " + e.errorDescription() + ", " + e.errorModule() );

            }
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
                       fragmentTransaction.replace(R.id.mainLayout, mainSongFragment);
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


       @TargetApi(Build.VERSION_CODES.M)
    private void permissions() {
           if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
               requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}, Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS);
           }else if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED || context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED || context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED) {
               if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                   requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Manifest_permission_READ_EXTERNAL_STORAGE);
               }
               if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                   requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Manifest_permission_WRITE_EXTERNAL_STORAGE);

               }
               if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                   requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, Manifest_permission_RECORD_AUDIO);

               }
           }
        return;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Manifest_permission_READ_EXTERNAL_STORAGE_ALL_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

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

        }
    }
   private class SystemEvents implements IGnSystemEvents{

        @Override
        public void localeUpdateNeeded(GnLocale gnLocale) {
            Thread localeUpdateThread = new Thread(new LocaleUpdateRunnable(gnLocale,gnUser));
            localeUpdateThread.start();
        }

        @Override
        public void listUpdateNeeded(GnList gnList) {
            Thread listUpdateThread = new Thread(new ListUpdateRunnable(gnList,gnUser));
            listUpdateThread.start();
        }

        @Override
        public void systemMemoryWarning(long l, long l1) {

        }
    }
    private class MusicIDStreamEvents implements IGnMusicIdStreamEvents {

        HashMap<String, String> gnStatus_to_displayStatus;

        public MusicIDStreamEvents(){
            gnStatus_to_displayStatus = new HashMap<String,String>();
            gnStatus_to_displayStatus.put(GnMusicIdStreamIdentifyingStatus.kStatusIdentifyingStarted.toString(), "Identification started");
            gnStatus_to_displayStatus.put(GnMusicIdStreamIdentifyingStatus.kStatusIdentifyingFpGenerated.toString(), "Fingerprinting complete");
            gnStatus_to_displayStatus.put(GnMusicIdStreamIdentifyingStatus.kStatusIdentifyingLocalQueryStarted.toString(), "Lookup started");
            gnStatus_to_displayStatus.put(GnMusicIdStreamIdentifyingStatus.kStatusIdentifyingOnlineQueryStarted.toString(), "Lookup started");
//			gnStatus_to_displayStatus.put(GnMusicIdStreamIdentifyingStatus.kStatusIdentifyingEnded.toString(), "Identification complete");
        }

        @Override
        public void statusEvent(GnStatus status, long percentComplete, long bytesTotalSent, long bytesTotalReceived, IGnCancellable cancellable ) {

        }

        @Override
        public void musicIdStreamProcessingStatusEvent(GnMusicIdStreamProcessingStatus status, IGnCancellable canceller ) {

            if(GnMusicIdStreamProcessingStatus.kStatusProcessingAudioStarted.compareTo(status) == 0)
            {
                audioProcessingStarted = true;
                ((Activity)context).runOnUiThread(new Runnable (){
                    public void run(){
                        listenSong.setVisibility(View.VISIBLE);
                    }
                });

            }

        }

        @Override
        public void musicIdStreamIdentifyingStatusEvent( GnMusicIdStreamIdentifyingStatus status, IGnCancellable canceller ) {
            if(gnStatus_to_displayStatus.containsKey(status.toString())){
                //Give status of Latest Music-Id Stream Lookup...
            }

            if(status.compareTo( GnMusicIdStreamIdentifyingStatus.kStatusIdentifyingLocalQueryStarted ) == 0 ){
                lastLookup_local = true;
            }
            else if(status.compareTo( GnMusicIdStreamIdentifyingStatus.kStatusIdentifyingOnlineQueryStarted ) == 0){
                lastLookup_local = false;
            }

            if ( status == GnMusicIdStreamIdentifyingStatus.kStatusIdentifyingEnded )
            {
//                setUIState( UIState.READY );
            }
        }


        @Override
        public void musicIdStreamAlbumResult(GnResponseAlbums result, IGnCancellable canceller ) {
            lastLookup_matchTime = SystemClock.elapsedRealtime() - lastLookup_startTime;
            ((Activity)context).runOnUiThread(new UpdateResultsRunnable(result,mainLayout,context,gnUser));
        }

        @Override
        public void musicIdStreamIdentifyCompletedWithError(GnError error) {
            if ( error.isCancelled() )
                Toast.makeText(context, "Cancelled Lookup For Music-Id Stream...", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Oops! Got an error while looking for Music Stream: "+error.errorDescription(), Toast.LENGTH_SHORT).show();
        }
    }



}
