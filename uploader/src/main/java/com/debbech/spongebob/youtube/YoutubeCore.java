package com.debbech.spongebob.youtube;

import com.debbech.spongebob.Config;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class YoutubeCore {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    public String progress = null;

    private static YoutubeCore instance = null;

    private YoutubeCore(){}

    public static YoutubeCore getInstance(){
        if(instance == null){
            instance = new YoutubeCore();
        }
        return instance;
    }

    public void upload(String videoName) throws Exception{
        //the necessary scope
        //https://www.googleapis.com/auth/youtube.upload
        log.info("uploading to yt...");
        try{
            UploadApi ua = new UploadApi();
            Path v = Paths.get(Config.getInstance().container_vids_dir).resolve(videoName);
            YoutubeVideo yv = new YoutubeVideo(v.toString(), new File(v.toString()).length() );
            ua.doUpload(yv);
        }catch(Exception e){
            log.error("could not upload to youtube because {}", e.getMessage());
            throw e;
        }
    }

    private GoogleSecret getGoogleSecret(){
        String filePath = Config.getInstance().googleSecretFile;
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            GoogleSecret gs = gson.fromJson(reader, GoogleSecret.class);
            return gs;
        } catch (Exception e) {
            log.error(("could not load google secret store : " + e.getMessage()));
        }
        return null;
    }

    public TokenResp getTokens(String code) throws Exception{

        GoogleSecret gs = getGoogleSecret();
        if(gs == null) throw new Exception("could not find google secrets file to authenticate user");

        String x = "https://oauth2.googleapis.com/token";
        URL url = new URL(x);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("code", code);
        parameters.put("client_secret", gs.installed.client_secret);
        parameters.put("client_id", gs.installed.client_id );
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

        createTokenFile(content.toString());
        return tr;
    }

    private void createTokenFile(String content) throws IOException {
        Files.writeString(Path.of("request.token"), content);
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
