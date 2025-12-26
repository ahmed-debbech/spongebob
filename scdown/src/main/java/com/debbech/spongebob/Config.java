package com.debbech.spongebob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

    public int PORT = 20000;
    public int websocket_port = 20001;
    public String out_proc_qu = "DIRECTORY_READY_TO_PROCESS";
    public String rabbitMqHost = "rabbitmq";

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
