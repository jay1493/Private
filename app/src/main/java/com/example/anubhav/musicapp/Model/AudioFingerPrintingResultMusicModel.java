package com.example.anubhav.musicapp.Model;

import java.util.List;

/**
 * Created by anubhav on 27/3/17.
 */

public class AudioFingerPrintingResultMusicModel {
    private String songTitle;
    private String album;
    private String songReleaseDate;
    private List<AudioFingerprintResultsArtistModel> artistModel;
    private List<AudioFingerprintResultsGenreModel> genreModel;

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSongReleaseDate() {
        return songReleaseDate;
    }

    public void setSongReleaseDate(String songReleaseDate) {
        this.songReleaseDate = songReleaseDate;
    }

    public List<AudioFingerprintResultsArtistModel> getArtistModel() {
        return artistModel;
    }

    public void setArtistModel(List<AudioFingerprintResultsArtistModel> artistModel) {
        this.artistModel = artistModel;
    }

    public List<AudioFingerprintResultsGenreModel> getGenreModel() {
        return genreModel;
    }

    public void setGenreModel(List<AudioFingerprintResultsGenreModel> genreModel) {
        this.genreModel = genreModel;
    }
}
