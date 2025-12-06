package com.debbech.spongebob;

import com.debbech.spongebob.youtube.GoogleSecret;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;

public class Config {

    public String in_proc_qu = "DIRECTORY_READY_TO_PROCESS";
    public int PORT = 9004;
    public int timeToStopCallbackServer = 600; // 10 min
    public boolean bypassGoogleAuth = true;
    public String rabbitMqHost = "rabbitmq";
    public String googleSecretFile = "google.json";


    private static Config config = null;
    private static Logger log = LoggerFactory.getLogger(SpongeBob.class);
    private GoogleSecret googleSecret;

    private Config(){

    }

    public static Config getInstance(){
        if(config == null) {
            config = new Config();
        }
        return config;
    }

    public void setGoogleConfig(){
        String filePath = this.googleSecretFile;
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            GoogleSecret gs = gson.fromJson(reader, GoogleSecret.class);
            Config.getInstance().setGoogleSecret(gs);
        } catch (Exception e) {
            log.error(("could not load google secret store : " + e.getMessage()));
        }
    }
    public GoogleSecret getGoogleSecret(){
        return this.googleSecret;
    }
    public void setGoogleSecret(GoogleSecret googleSecret){
        this.googleSecret = googleSecret;
    }
}
