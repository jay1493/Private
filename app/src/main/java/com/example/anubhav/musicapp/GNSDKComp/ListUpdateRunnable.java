package com.example.anubhav.musicapp.GNSDKComp;

/**
 * Created by anubhav on 21/3/17.
 */

import android.util.Log;

import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnList;
import com.gracenote.gnsdk.GnUser;

/**
 * Updates a list
 */
public class ListUpdateRunnable implements Runnable {
    GnList list;
    GnUser user;


    public ListUpdateRunnable(
            GnList			list,
            GnUser			user) {
        this.list 		= list;
        this.user 		= user;
    }

    @Override
    public void run() {
        try {
            list.update(user);
        } catch (GnException e) {
            e.printStackTrace();
        }
    }
}
