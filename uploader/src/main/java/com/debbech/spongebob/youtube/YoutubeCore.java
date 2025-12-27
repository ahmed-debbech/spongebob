package com.debbech.spongebob.youtube;

import com.debbech.spongebob.Config;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class YoutubeCore {

    private Logger log = LoggerFactory.getLogger(this.getClass());

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
        log.info("uploading to yt...");
        try{
            if(!isTokenValid()) throw new Exception("token has been expired.. please re-authenticate on dash service");
            UploadApi ua = new UploadApi();
            YoutubeVideo yv = new YoutubeVideo("/home/ahmed/pngs/8.mp4", new File("/home/ahmed/pngs/8.mp4").length() );
            ua.doUpload(yv, userTokens.access_token);
        }catch(Exception e){
            log.error("could not upload to youtube because {}", e.getMessage());
            throw e;
        }
    }

    private boolean isTokenValid(){
        if(this.userTokens == null) return false;
        if(this.userTokens.access_token.isEmpty()) return false;
        if(((System.currentTimeMillis()/1000) - this.userTokens.issued) > this.userTokens.expires_in){
            //todo add refresh token mechanism using this https://developers.google.com/oauthplayground/#step2&apisSelect=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fyoutube%2Chttps%3A%2F%2Fwww.googleapis.com%2Fauth%2Fyoutube.upload&url=https%3A%2F%2F&content_type=application%2Fjson&http_method=GET&useDefaultOauthCred=unchecked&oauthEndpointSelect=Google&oauthAuthEndpointValue=https%3A%2F%2Faccounts.google.com%2Fo%2Foauth2%2Fv2%2Fauth&oauthTokenEndpointValue=https%3A%2F%2Foauth2.googleapis.com%2Ftoken&includeCredentials=unchecked&accessTokenType=bearer&autoRefreshToken=unchecked&accessType=offline&prompt=consent&response_type=code&wrapLines=on
            return false;
        }
        return true;
    }


    public void setUserTokens(TokenResp tr){
        this.userTokens = tr;
        this.userTokens.issued = System.currentTimeMillis()/1000;
    }

    public TokenResp getTokens(String code) throws Exception{
        String x = "https://oauth2.googleapis.com/token";
        URL url = new URL(x);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("code", code);
        parameters.put("client_secret", Config.getInstance().getGoogleSecret().installed.client_secret);
        parameters.put("client_id", Config.getInstance().getGoogleSecret().installed.client_id );
        parameters.put("redirect_uri", Config.getInstance().google_redirect_uri+"/oauth");
        parameters.put("grant_type", "authorization_code");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(paramString(parameters));
        out.flush();
        out.close();
        int status = con.getResponseCode();

        Reader streamReader;
        if (status > 299) {
            streamReader = new InputStreamReader(con.getErrorStream());
            log.error("Failed with " + status + " error code in http request to get tokens from google");
        } else {
            streamReader = new InputStreamReader(con.getInputStream());
        }
        BufferedReader in = new BufferedReader(streamReader);
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        if(status > 299){
            throw new Exception(content.toString());
        }
        log.info("received access_token from token");
        Gson gson = new Gson();
        TokenResp tr = gson.fromJson(content.toString(), TokenResp.class);
        return tr;
    }

    private String paramString(Map<String, String> params){

        StringBuilder concatenatedString = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            concatenatedString.append(key).append("=").append(value).append("&");
        }
        String result = concatenatedString.toString().substring(0, concatenatedString.toString().length()-1);
        return result;
    }
}
