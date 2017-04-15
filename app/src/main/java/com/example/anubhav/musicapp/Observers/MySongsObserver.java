package com.example.anubhav.musicapp.Observers;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.anubhav.musicapp.Interfaces.ObserverListener;
import com.example.anubhav.musicapp.Model.MusicModel;

/**
 * Created by anubhav on 29/3/17.
 */

public class MySongsObserver extends ContentObserver {
    private final Handler handlerFromActivity;
    private MusicModel musicModel;
    private ObserverListener observerListener;
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public MySongsObserver(Handler handler, MusicModel musicModel,ObserverListener observe) {
        super(handler);
        handlerFromActivity = handler;
        this.musicModel = musicModel;
        this.observerListener = observe;
    }

    @Override
    public void onChange(boolean selfChange) {
        this.onChange(selfChange,null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        observerListener.isProcessCompleted(true);
        Log.d("", "onChange: Inside Observer============================");


    }
}
