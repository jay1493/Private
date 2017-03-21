package com.example.anubhav.musicapp.GNSDKComp;

import android.content.Context;

import com.example.anubhav.musicapp.DashboardActivity;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.gracenote.gnsdk.GnUser;

/**
 * Created by anubhav on 21/3/17.
 */

public class InsertChangesRunnable implements Runnable {
    GnResponseAlbums row;
    Context context;
    GnUser gnUser;

    InsertChangesRunnable(GnResponseAlbums row,Context context,GnUser user) {
        this.row = row;
        this.context = context;
        this.gnUser = user;
    }

    @Override
    public void run() {
        try {
            DatabaseAdapter db = new DatabaseAdapter(context,gnUser);
            db.open();
            db.insertChanges(row);
            db.close();
        } catch (GnException e) {
            // ignore
        }
    }
}
