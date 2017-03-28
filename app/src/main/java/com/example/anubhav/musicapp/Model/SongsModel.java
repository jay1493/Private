package com.example.anubhav.musicapp.Model;

import java.io.Serializable;

/**
 * Created by anubhav on 28/3/17.
 */

public class SongsModel implements Serializable {

    private String songId;
    private String songAlbumId;
    private String songAlbumName;
    private String songDuration;
    private String songTitle;
    private String trackNoOfSongInAlbum;
    private String songArtist;
    private String songAlbumCover;

    public String getSongAlbumCover() {
        return songAlbumCover;
    }

    public void setSongAlbumCover(String songAlbumCover) {
        this.songAlbumCover = songAlbumCover;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongAlbumId() {
        return songAlbumId;
    }

    public void setSongAlbumId(String songAlbumId) {
        this.songAlbumId = songAlbumId;
    }

    public String getSongAlbumName() {
        return songAlbumName;
    }

    public void setSongAlbumName(String songAlbumName) {
        this.songAlbumName = songAlbumName;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getTrackNoOfSongInAlbum() {
        return trackNoOfSongInAlbum;
    }

    public void setTrackNoOfSongInAlbum(String trackNoOfSongInAlbum) {
        this.trackNoOfSongInAlbum = trackNoOfSongInAlbum;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }
}
