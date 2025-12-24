package com.debbech.spongebob.model;

import com.debbech.spongebob.soundcloud.Data;

public class StoredTrack {
    private Data.Track track;
    private String playlistDirName;
    private TrackStatus status;

    @Override
    public String toString() {
        return "StoredTrack{" +
                "track=" + track +
                ", playlistDirName='" + playlistDirName + '\'' +
                ", status=" + status +
                '}';
    }

    public StoredTrack() {
    }

    public StoredTrack(Data.Track track, TrackStatus status, String pldir) {
        this.track = track;
        this.status = status;
        this.playlistDirName = pldir;
    }

    public String getPlaylistDirName() {
        return playlistDirName;
    }

    public void setPlaylistDirName(String playlistDirName) {
        this.playlistDirName = playlistDirName;
    }

    public Data.Track getTrack() {
        return track;
    }

    public void setTrack(Data.Track track) {
        this.track = track;
    }

    public TrackStatus getStatus() {
        return status;
    }

    public void setStatus(TrackStatus status) {
        this.status = status;
    }
}
