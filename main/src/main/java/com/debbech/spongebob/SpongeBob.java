package com.debbech.spongebob;

import com.debbech.spongebob.youtube.GoogleSecret;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;


public class SpongeBob {

    private static Logger log = LoggerFactory.getLogger(SpongeBob.class);

    public static void main(String[] args) {

        /*try {
            PrintStream fileOut = null;
            FileOutputStream fos = new FileOutputStream(new File("spongebob.log"), true);
            fileOut = new PrintStream(fos);
            System.setOut(fileOut);
            System.setErr(fileOut);
            System.out.println("Launched Spongebob on "+ LocalDateTime.now());
        } catch (FileNotFoundException e) {
            System.err.println("could not setup logging file");
        }*/

        log.info("Spongebob main started up & running...");

        String filePath = "google.json";
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            GoogleSecret gs = gson.fromJson(reader, GoogleSecret.class);
            Config.getInstance().setGoogleSecret(gs);
        } catch (Exception e) {
            log.error(("could not load google secret store : " + e.getMessage()));
        }

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
