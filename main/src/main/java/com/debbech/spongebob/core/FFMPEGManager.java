package com.debbech.spongebob.core;

import java.nio.file.Paths;

public class FFMPEGManager {

    private boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

    public void generateConcatinatedMp3() throws Exception{
        String concatCmd = this.isWindows
                ? "ffmpeg.exe -f concat -safe 0 -i .\\build\\agg -c copy .\\build\\output.mp3 -y"
                : "./ffmpeg -f concat -safe 0 -i ./build/agg -c copy ./build/output.mp3 -y";

        execute(concatCmd);
    }
    public void generateMp4Video(String imgPath) throws Exception{

        String finalCmd;
        String p = Paths.get(imgPath).normalize().toString();
        String quotedPath = System.getProperty("os.name").toLowerCase().contains("win")
                ? "\"" + p + "\""
                : "'" + p + "'";
        if (!imgPath.equals("X")) {
            finalCmd = isWindows
                    ? "ffmpeg.exe -loop 1 -i " + quotedPath +
                    " -i .\\build\\output.mp3 -c:v libx264 -tune stillimage -c:a aac -b:a 192k -shortest .\\build\\output.mp4 -y"
                    : "./ffmpeg -loop 1 -i " + quotedPath +
                    " -i ./build/output.mp3 -c:v libx264 -tune stillimage -c:a aac -b:a 192k -shortest ./build/output.mp4 -y";
        } else {
            finalCmd = isWindows
                    ? "ffmpeg.exe -stream_loop -1 -f lavfi -i color=c=black:s=854x480:r=30 " +
                    "-i .\\build\\output.mp3 -c:v libx264 -tune stillimage -c:a aac -b:a 192k " +
                    "-pix_fmt yuv420p -shortest .\\build\\output.mp4 -y"
                    : "./ffmpeg -stream_loop -1 -f lavfi -i color=c=black:s=854x480:r=30 " +
                    "-i ./build/output.mp3 -c:v libx264 -tune stillimage -c:a aac -b:a 192k " +
                    "-pix_fmt yuv420p -shortest ./build/output.mp4 -y";
        }

        execute(finalCmd);
    }
    private static void execute(String command) throws Exception {
        try {
            boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");

            ProcessBuilder pb = (!isWin)? new ProcessBuilder("bash", "-c", command) :
                    new ProcessBuilder("cmd.exe", "/c", command);
            pb.inheritIO(); // show ffmpeg output directly
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            throw new Exception("Error executing command: " + e.getMessage());
        }
    }
}
