package com.debbech.spongebob.queue.messages;

import com.google.gson.Gson;

public class ProcessRequestMessage {

    public String playlistDirectoryName;

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
