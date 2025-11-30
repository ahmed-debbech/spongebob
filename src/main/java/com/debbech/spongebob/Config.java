package com.debbech.spongebob;

import com.debbech.spongebob.youtube.GoogleSecret;

public class Config {

    private static Config config = null;
    public int PORT = 9004;
    public int timeToStopCallbackServer = 600; // 10 min
    public boolean bypassGoogleAuth = true;

    private GoogleSecret googleSecret;

    private Config(){

    }

    public static Config getInstance(){
        if(config == null) {
            config = new Config();
        }
        return config;
    }

    public GoogleSecret getGoogleSecret(){
        return this.googleSecret;
    }
    public void setGoogleSecret(GoogleSecret googleSecret){
        this.googleSecret = googleSecret;
    }
}
