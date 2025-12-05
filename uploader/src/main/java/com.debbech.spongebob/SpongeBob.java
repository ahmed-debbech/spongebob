package com.debbech.spongebob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;


public class SpongeBob {

    private static Logger log = LoggerFactory.getLogger(SpongeBob.class);

    public static void main(String[] args) {

        log.info("Spongebob uploader started up & running...");
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

    }

}
