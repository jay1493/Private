package com.example.anubhav.musicapp.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by anubhav on 29/3/17.
 */

public class MusicModel implements Serializable {

    private ArrayList<AlbumModel> allAlbums;
    private ArrayList<SongsModel> allSongs;

    public ArrayList<AlbumModel> getAllAlbums() {
        return allAlbums;
    }

    public void setAllAlbums(ArrayList<AlbumModel> allAlbums) {
        this.allAlbums = allAlbums;
    }

    public ArrayList<SongsModel> getAllSongs() {
        return allSongs;
    }

    public void setAllSongs(ArrayList<SongsModel> allSongs) {
        this.allSongs = allSongs;
    }
}
