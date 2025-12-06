package com.debbech.spongebob.core;

import com.debbech.spongebob.core.input.InputSanitizer;
import com.debbech.spongebob.core.input.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Core {

    private static Logger log = LoggerFactory.getLogger(Core.class);

    public static void run(String[] args) throws Exception{
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
            new DiskManager().moveMp4();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e);
        }
    }
}
