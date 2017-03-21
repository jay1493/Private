package com.example.anubhav.musicapp.GNSDKComp;

import android.util.Log;

import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnLocale;
import com.gracenote.gnsdk.GnUser;

/**
 * Created by anubhav on 21/3/17.
 */

public class LocaleUpdateRunnable implements Runnable {
    GnLocale locale;
    GnUser user;


    public LocaleUpdateRunnable(
            GnLocale		locale,
            GnUser			user) {
        this.locale 	= locale;
        this.user 		= user;
    }

    @Override
    public void run() {
        try {
            locale.update(user);
        } catch (GnException e) {
           e.printStackTrace();
        }
    }
}