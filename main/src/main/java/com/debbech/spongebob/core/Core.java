package com.debbech.spongebob.core;

import com.debbech.spongebob.gui.Gui;
import com.debbech.spongebob.gui.StatusType;
import com.debbech.spongebob.input.InputSanitizer;
import com.debbech.spongebob.input.UserInput;

import java.nio.file.Path;

public class Core {
    public static void run(String[] args, Gui gui){
        UserInput userInput = null;
        try {
            gui.updateStatus("Validating input...", StatusType.NORM, true);
            userInput = new InputSanitizer().sanitize(args);
            Path buildDir = null;
            gui.updateStatus("Building directory...", StatusType.NORM, false);
            buildDir = new DiskManager().createBuildDir();
            gui.updateStatus("Creating .agg file", StatusType.NORM, false);
            Path aggFile = buildDir.resolve("agg");
            gui.updateStatus("Getting mp3 files...", StatusType.NORM, false);
            new DiskManager().getMp3s(aggFile, userInput.getMp3Path());
            FFMPEGManager fm = new FFMPEGManager();
            gui.updateStatus("Generating mp3 of concatinated mp3 files", StatusType.NORM, false);
            fm.generateConcatinatedMp3();
            gui.updateStatus("Generating final video...", StatusType.NORM, false);
            fm.generateMp4Video(userInput.getImage());
            gui.updateStatus("MP4 video generated.", StatusType.SUCC, false);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            gui.updateStatus(e.getMessage(), StatusType.ERR, false);
        }
    }
}
