package com.example.anubhav.musicapp.Model;

import java.util.List;

/**
 * Created by anubhav on 27/3/17.
 */

public class AudioFingerPrintingResultModel {
    private String errorCode;
    private String errorMsg;
    private List<AudioFingerPrintingResultMusicModel> musicList;


    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<AudioFingerPrintingResultMusicModel> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<AudioFingerPrintingResultMusicModel> musicList) {
        this.musicList = musicList;
    }
}
