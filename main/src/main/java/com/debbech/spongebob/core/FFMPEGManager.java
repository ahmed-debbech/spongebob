package com.debbech.spongebob.core;

import com.debbech.spongebob.Config;

import java.nio.file.Paths;

public class FFMPEGManager {

    public void generateConcatinatedMp3() throws Exception{
        String concatCmd = "./ffmpeg -f concat -safe 0 -i ./build/agg -c copy ./build/output.mp3 -y";

        execute(concatCmd);
    }
    public void generateMp4Video(String imgPath) throws Exception{

        String finalCmd;
        String p = Paths.get(imgPath).normalize().toString();
        String quotedPath = System.getProperty("os.name").toLowerCase().contains("win")
                ? "\"" + p + "\""
                : "'" + p + "'";
        if (!imgPath.equals("X")) {
            finalCmd = "./ffmpeg -loop 1 -i " + quotedPath +
                    " -i ./build/output.mp3 -c:v libx264 -tune stillimage -c:a aac -b:a 192k -shortest ./build/output.mp4 -y";
        } else {
            finalCmd = "./ffmpeg -stream_loop -1 -f lavfi -i color=c=black:s=854x480:r=30 " +
                    "-i ./build/output.mp3 -c:v libx264 -tune stillimage -c:a aac -b:a 192k " +
                    "-pix_fmt yuv420p -shortest ./build/output.mp4 -y";
        }

        execute(finalCmd);
    }
    private static void execute(String command) throws Exception {
        try {
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
            pb.inheritIO(); // show ffmpeg output directly
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            throw new Exception("Error executing command: " + e.getMessage());
        }
    }
}
