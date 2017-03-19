package com.example.anubhav.musicapp.Model;

import java.util.List;

/**
 * Created by anubhav on 19/3/17.
 */

public class DownloadVidsModel {
    private String iconUrl;
    private String title;
    private List<AudioLinksModel> audioLinks;
    private List<VideoLinksModel> videoLinks;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AudioLinksModel> getAudioLinks() {
        return audioLinks;
    }

    public void setAudioLinks(List<AudioLinksModel> audioLinks) {
        this.audioLinks = audioLinks;
    }

    public List<VideoLinksModel> getVideoLinks() {
        return videoLinks;
    }

    public void setVideoLinks(List<VideoLinksModel> videoLinks) {
        this.videoLinks = videoLinks;
    }
}
