package com.debbech.spongebob;

import com.debbech.spongebob.control.Controller;
import com.debbech.spongebob.youtube.GoogleSecret;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;


public class SpongeBob {

    private static Logger log = LoggerFactory.getLogger(SpongeBob.class);

    public static void main(String[] args) {

        log.info("Spongebob main started up & running...");

        Config.getInstance().setGoogleConfig();

        new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    log.error("RUNTIME ERROR: app is exiting and can't stay up in background");
                    throw new RuntimeException(e);
                }
            }
        }).start();

        Controller controller = new Controller();
        controller.listenForEvents();
    }

}
