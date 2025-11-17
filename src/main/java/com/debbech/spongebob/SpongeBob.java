package com.debbech.spongebob;

import com.debbech.spongebob.gui.Gui;
import com.debbech.spongebob.youtube.GoogleSecret;
import com.debbech.spongebob.youtube.YoutubeCore;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;


public class SpongeBob {

    public static void main(String[] args) {
        PrintStream fileOut = null;
        try {
            fileOut = new PrintStream("spongebob.log");
            System.setOut(fileOut);
            System.setErr(fileOut);
        } catch (FileNotFoundException e) {
            System.err.println("could not setup logging file");
        }

        String filePath = "google.json";
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            GoogleSecret gs = gson.fromJson(reader, GoogleSecret.class);
            YoutubeCore.getInstance().setSecret(gs);
        } catch (Exception e) {
            System.err.println(("could not load google secret store : " + e.getMessage()));
        }

        Gui gui = new Gui();
        gui.initGui();

    }

}
