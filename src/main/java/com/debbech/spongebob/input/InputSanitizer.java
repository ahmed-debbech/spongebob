package com.debbech.spongebob.input;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InputSanitizer {

    public UserInput sanitize(String[] args) throws Exception{

        String image = args[0];
        String mp3Folder = args[1];

        Path mp3Path = Paths.get(mp3Folder);
        if (!Files.exists(mp3Path)) {
            throw new Exception("Mp3 folder does not exist");
        }

        if (!image.equals("X")) {
            Path imgPath = Paths.get(image);
            if (!Files.exists(imgPath) || !Files.isRegularFile(imgPath)) {
                throw new Exception("Image file does not exist.");
            }
        }

        UserInput ui = new UserInput(mp3Folder, image);
        return ui;
    }
}
