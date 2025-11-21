package com.debbech.spongebob;

import com.debbech.spongebob.gui.Gui;
import com.debbech.spongebob.youtube.GoogleSecret;
import com.debbech.spongebob.youtube.YoutubeCore;
import com.google.gson.Gson;

import java.io.*;
import java.time.LocalDateTime;


public class SpongeBob {

    public static void main(String[] args) {

        try {
            PrintStream fileOut = null;
            FileOutputStream fos = new FileOutputStream(new File("spongebob.log"), true);
            fileOut = new PrintStream(fos);
            System.setOut(fileOut);
            System.setErr(fileOut);
            System.out.println("Launched Spongebob on "+ LocalDateTime.now());
        } catch (FileNotFoundException e) {
            System.err.println("could not setup logging file");
        }

        String filePath = "google.json";
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            GoogleSecret gs = gson.fromJson(reader, GoogleSecret.class);
            Config.getInstance().setGoogleSecret(gs);
        } catch (Exception e) {
            System.err.println(("could not load google secret store : " + e.getMessage()));
        }

        Gui gui = new Gui();
        gui.initGui();

    }

}
