package com.debbech.spongebob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

    public int PORT = 7071;
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
