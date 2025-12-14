package com.debbech.spongebob.soundcloud;

import java.util.List;

public class CurrentPlaylist {

    public static CurrentPlaylist currentPlaylist;

    private String playlistUrl;
    private List<Data.Track> trackList;
    private boolean isDownloading = false;

    private CurrentPlaylist(){

    }

    public static CurrentPlaylist getInstance(){
        if( currentPlaylist == null){
            currentPlaylist = new CurrentPlaylist();
        }
        return currentPlaylist;
    }

    @Override
    public String toString() {
        return "CurrentPlaylist{" +
                "playlistUrl='" + playlistUrl + '\'' +
                ", trackList=" + trackList +
                '}';
    }

    public void reset(){
        this.isDownloading = false;
    }

    public boolean isGettingDownloaded(){
        return isDownloading;
    }

    public void markAsDownloading(){
        isDownloading = true;
    }

    public String getPlaylistUrl() {
        return playlistUrl;
    }

    public void setPlaylistUrl(String playlistUrl) {
        this.playlistUrl = playlistUrl;
        isDownloading = false;
    }

    public List<Data.Track> getTrackList() {
        return trackList;
    }

    public void setTrackList(List<Data.Track> trackList) {
        this.trackList = trackList;
        isDownloading = false;
    }
}
