package com.debbech.spongebob.queue.messages;

import com.google.gson.Gson;

public class ProcessRequestMessage {

    public String playlistDirectoryName;

    public static ProcessRequestMessage fromJson(String m){
        Gson gson = new Gson();
        return  gson.fromJson(m,ProcessRequestMessage.class);
    }

}
