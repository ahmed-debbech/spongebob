package com.debbech.spongebob.core;

import com.debbech.spongebob.gui.Gui;
import com.debbech.spongebob.gui.StatusType;
import com.debbech.spongebob.input.InputSanitizer;
import com.debbech.spongebob.input.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Core {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public static void run(String[] args){
        UserInput userInput = null;
        try {
            userInput = new InputSanitizer().sanitize(args);
            Path buildDir = null;
            buildDir = new DiskManager().createBuildDir();
            Path aggFile = buildDir.resolve("agg");
            new DiskManager().getMp3s(aggFile, userInput.getMp3Path());
            FFMPEGManager fm = new FFMPEGManager();
            fm.generateConcatinatedMp3();
            fm.generateMp4Video(userInput.getImage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
