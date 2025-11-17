package com.debbech.spongebob.youtube;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public class YoutubeCore {

    private GoogleSecret googlSecret;
    private static YoutubeCore instance = null;

    private YoutubeCore(){

    }
    public static YoutubeCore getInstance(){
        if(instance == null){
            instance = new YoutubeCore();
        }
        return instance;
    }

    public void upload() throws Exception{
        //the necessary scope
        //https://www.googleapis.com/auth/youtube.upload
        System.out.println("uploading to yt...");
        String callbackurl = "https://accounts.google.com/o/oauth2/v2/auth?scope=" +(new URL("https://www.googleapis.com/auth/youtube.upload")) +"&response_type=code&redirect_uri=http%3A//127.0.0.1%3A9004&client_id="+
                googlSecret.installed.client_id;
        try{
            System.out.println("calling this url -> " + callbackurl);
            new Browser().launchBrowser(callbackurl);
        }catch(Exception e){
            throw e;
        }
    }
    public void setSecret(GoogleSecret s){
        this.googlSecret = s;
    }


}
