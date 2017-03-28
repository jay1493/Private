package com.example.anubhav.musicapp.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anubhav on 28/2/17.
 */

public class AlbumModel implements Serializable {
    private String albumId;
    private String albumTitle;
    private String albumCover;
    private String artistTitle;
    private ArrayList<SongsModel> songs;
    private String noOfSongs;
    public AlbumModel() {
        songs = new ArrayList<>();
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }

    public String getArtistTitle() {
        return artistTitle;
    }

    public void setArtistTitle(String artistTitle) {
        this.artistTitle = artistTitle;
    }

    public List<SongsModel> getSongs() {
        return songs;
    }

    public String getNoOfSongs() {
        return noOfSongs;
    }

    public void setNoOfSongs(String noOfSongs) {
        this.noOfSongs = noOfSongs;
    }

    public void putSongs(SongsModel songsModel){
        songs.add(songsModel);
    }
}
