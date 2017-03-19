package com.example.anubhav.musicapp.Model;

/**
 * Created by anubhav on 19/3/17.
 */

    public class YouTubeSearchedVideos {

        private int id;// Currently Not Used
        private String title;
        private String token;
        private String[] genreId = null;
        private String pubDate;
        private String firstReleaseDate;
        private String duration;
        private String language;
        private String videoId;
        private int numLikes;
        private int viewCount;
        private String rating;
        private String director;
        private String actors;
        private String rtspUrl;
        private String desc;
        private String iconUrl;
        private boolean myFav = false;
        private int parentId;
        private int countHint;
        private String updated;
        private String localPath;

        private Boolean addedToPlayer = false;
        private Boolean tempDeletedItem = false;

        private Boolean isAd = false;

        public YouTubeSearchedVideos() {

        }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getGenreId() {
        return genreId;
    }

    public void setGenreId(String[] genreId) {
        this.genreId = genreId;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getFirstReleaseDate() {
        return firstReleaseDate;
    }

    public void setFirstReleaseDate(String firstReleaseDate) {
        this.firstReleaseDate = firstReleaseDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getRtspUrl() {
        return rtspUrl;
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isMyFav() {
        return myFav;
    }

    public void setMyFav(boolean myFav) {
        this.myFav = myFav;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getCountHint() {
        return countHint;
    }

    public void setCountHint(int countHint) {
        this.countHint = countHint;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public Boolean getAddedToPlayer() {
        return addedToPlayer;
    }

    public void setAddedToPlayer(Boolean addedToPlayer) {
        this.addedToPlayer = addedToPlayer;
    }

    public Boolean getTempDeletedItem() {
        return tempDeletedItem;
    }

    public void setTempDeletedItem(Boolean tempDeletedItem) {
        this.tempDeletedItem = tempDeletedItem;
    }

    public Boolean getAd() {
        return isAd;
    }

    public void setAd(Boolean ad) {
        isAd = ad;
    }
}
