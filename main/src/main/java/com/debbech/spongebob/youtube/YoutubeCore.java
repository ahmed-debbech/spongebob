package com.debbech.spongebob.youtube;

import com.debbech.spongebob.Config;

import java.io.File;
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
            if(!Config.getInstance().bypassGoogleAuth) {
                doAuth(callbackurl);
            }else {
                this.userTokens = new TokenResp();
                this.userTokens.access_token = "e";
            }
            UploadApi ua = new UploadApi();
            YoutubeVideo yv = new YoutubeVideo("/home/ahmed/pngs/8.mp4", new File("/home/ahmed/pngs/8.mp4").length() );
            ua.doUpload(yv, userTokens.access_token);
        }catch(Exception e){
            throw e;
        }
    }

    private void doAuth(String callbackurl) throws Exception{
        setAuthNotDone();
        try {
            LocalServer.start();
            System.out.println("calling this url -> " + callbackurl);
            new Browser().launchBrowser(callbackurl);
            while(!authDone){
                Thread.sleep(1000);
            }
            System.out.println("Auth is done successfully with youtube, proceeding with uploading the video...");
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
    public synchronized static void setAuthNotDone(){
        authDone = false;
    }

    public synchronized static void setAuthDone(){
        authDone = true;
    }

    public void setUserTokens(TokenResp tr){
        this.userTokens = tr;
    }

}
