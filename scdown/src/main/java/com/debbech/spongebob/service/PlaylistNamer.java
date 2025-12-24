package com.debbech.spongebob.service;

import com.debbech.spongebob.persistence.Database;

public class PlaylistNamer {

    public static long getCurrentPlaylist(){
        return new Database().getCurrentPlaylistNumber();
    }

    public static void incCurrentPlaylist(){
        new Database().incrementCurrentPlaylistNumber();
    }
}

