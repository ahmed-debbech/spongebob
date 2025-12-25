package com.debbech.spongebob.model;

import com.google.gson.Gson;

public class ProcessRequestMessage {

    public String playlistDirectoryName;

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
