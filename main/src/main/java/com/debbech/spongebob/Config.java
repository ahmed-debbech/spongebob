package com.debbech.spongebob;


public class Config {

    public String in_proc_qu = "DIRECTORY_READY_TO_PROCESS";
    public String out_ulp_yt = "UPLOAD_TO_YOUTUBE";
    public String rabbitMqHost = "rabbitmq";
    public String container_mp3_path = "/app/mp3";
    public String container_output_path = "/app/output";
    public int processAttempts = 5;

    private static Config config = null;

    private Config(){

    }

    public static Config getInstance(){
        if(config == null) {
            config = new Config();
        }
        return config;
    }

}
