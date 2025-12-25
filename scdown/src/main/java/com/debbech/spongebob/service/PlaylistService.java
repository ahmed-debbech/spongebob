package com.debbech.spongebob.service;

import com.debbech.spongebob.persistence.Database;

public class PlaylistService {

    public static long getCurrentPlaylist(){
        return new Database().getCurrentPlaylistNumber();
    }

    public static void incCurrentPlaylist(){
        new Database().incrementCurrentPlaylistNumber();
    }

    public static void storePlaylistLink(String link){
        new Database().setCurrentPlaylistLink(link);
    }

    public static String getPlaylistLink(){
        return new Database().getCurrentPlaylistLink();
    }
}

