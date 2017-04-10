package com.example.anubhav.musicapp.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by anubhav on 1/4/17.
 */

public class PlaylistModel implements Serializable {
    private ArrayList<SongsModel> songsModelList;

    public PlaylistModel() {
        songsModelList = new ArrayList<>();
    }

    public ArrayList<SongsModel> getSongsModelList() {
        ArrayList<SongsModel> tempList = new ArrayList<>();
        for(SongsModel songsModel : songsModelList){
            tempList.add(songsModel);
        }
        return tempList;
    }
    public void putSong(SongsModel songsModel){
        boolean isDuplicate = false;
        if(songsModelList!=null) {
            //Don't add duplicate songs
            for(SongsModel song : songsModelList){
                if(song.getSongId().equals(songsModel.getSongId())){
                    isDuplicate = true;
                    return;
                }
            }
            if(!isDuplicate) {
                songsModelList.add(songsModel);
            }
        }
    }
    public void removeSong(SongsModel songsModel){
        songsModelList.remove(songsModel);
    }
}
