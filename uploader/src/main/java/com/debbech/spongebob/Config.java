package com.debbech.spongebob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class Config {

    public String in_upl_yt = "UPLOAD_TO_YOUTUBE";
    public String rabbitMqHost = "rabbitmq";
    public String google_redirect_uri = "http://localhost:3600"; //this should be the same in dash service
    public int authServerPort = 3900;
    public int processAttempts = 5;
    public String container_vids_dir = "/app/vids/";
    public String googleSecretFile = "google.json";

    private static Config config = null;
    private static Logger log = LoggerFactory.getLogger(Config.class);

    private Config(){

    }

    public static Config getInstance(){
        if(config == null) {
            config = new Config();
        }
        return config;
    }

}
