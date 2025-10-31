package com.debbech.spongebob;

import com.debbech.spongebob.core.DiskManager;
import com.debbech.spongebob.core.FFMPEGManager;
import com.debbech.spongebob.input.InputSanitizer;
import com.debbech.spongebob.input.UserInput;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class SpongeBob {
    public static void main(String[] args) {

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
        }catch(Exception e){
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }



}
