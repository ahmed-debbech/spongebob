package com.debbech.spongebob.youtube;

import com.debbech.spongebob.Config;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

public class YoutubeCore {

    private static YoutubeCore instance = null;
    private TokenResp userTokens = null;
    private static boolean authDone = false;

    private YoutubeCore(){}

    public static YoutubeCore getInstance(){
        if(instance == null){
            instance = new YoutubeCore();
            authDone = false;
        }
        return instance;
    }

    public void upload() throws Exception{
        //the necessary scope
        //https://www.googleapis.com/auth/youtube.upload
        System.out.println("uploading to yt...");
        String callbackurl = "https://accounts.google.com/o/oauth2/v2/auth?scope=" +(new URL("https://www.googleapis.com/auth/youtube.upload")) +"&response_type=code&redirect_uri=http%3A//127.0.0.1%3A"+Config.getInstance().PORT+"&client_id="+
                Config.getInstance().getGoogleSecret().installed.client_id;
        try{
            LocalServer.start();
            System.out.println("calling this url -> " + callbackurl);
            new Browser().launchBrowser(callbackurl);
            while(!authDone){}
            System.out.println("Auth is done successfully with youtube, proceeding with uploading the video...");


        }catch(Exception e){
            throw e;
        }
    }

    public void setUserTokens(TokenResp tr){
        this.userTokens = tr;
    }

}
