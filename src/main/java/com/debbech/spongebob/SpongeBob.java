package com.debbech.spongebob;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class SpongeBob {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Not enough parameters!");
            System.out.println("Usage: sponge path/to/image.jpg /path/to/mp3/folder");
            System.out.println("Example: sponge C:\\Users\\US\\Documents\\image.png C:\\Users\\US\\songs");
            System.out.println("Example without image: sponge X C:\\Users\\US\\songs");
            System.exit(1);
        }

        String image = args[0];
        String mp3Folder = args[1];

        Path mp3Path = Paths.get(mp3Folder);
        if (!Files.exists(mp3Path)) {
            System.err.println("Mp3 folder does not exist");
            System.exit(1);
        }

        if (!image.equals("X")) {
            Path imgPath = Paths.get(image);
            if (!Files.exists(imgPath) || !Files.isRegularFile(imgPath)) {
                System.err.println("Image file does not exist.");
                System.exit(1);
            }
        }

        Path buildDir = Paths.get("build");
        try {
            if (Files.exists(buildDir)) {
                deleteDirectory(buildDir);
            }
            Files.createDirectory(buildDir);
        } catch (IOException e) {
            System.err.println("Error creating build directory: " + e.getMessage());
            System.exit(1);
        }

        Path aggFile = buildDir.resolve("agg");
        boolean atLeastOne = false;
        try (BufferedWriter writer = Files.newBufferedWriter(aggFile)) {
            try (Stream<Path> files = Files.list(mp3Path)) {
                for (Path entry : files.collect(Collectors.toList())) {
                    if (entry.toString().toLowerCase().endsWith(".mp3")) {
                        writer.write("file '" + entry.toAbsolutePath().toString().replace("\\", "/") + "'");
                        writer.newLine();
                        atLeastOne = true;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing agg file: " + e.getMessage());
            System.exit(1);
        }

        if (!atLeastOne) {
            System.err.println("No MP3 found in the folder specified");
            System.exit(1);
        }

        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        String concatCmd = isWindows
                ? "ffmpeg.exe -f concat -safe 0 -i .\\build\\agg -c copy .\\build\\output.mp3 -y"
                : "./ffmpeg -f concat -safe 0 -i ./build/agg -c copy ./build/output.mp3 -y";

        execute(concatCmd);

        String finalCmd;
        if (!image.equals("X")) {
            finalCmd = isWindows
                    ? "ffmpeg.exe -loop 1 -i " + image +
                      " -i .\\build\\output.mp3 -c:v libx264 -tune stillimage -c:a aac -b:a 192k -shortest .\\build\\output.mp4 -y"
                    : "./ffmpeg -loop 1 -i " + image +
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

    private static void execute(String command) {
        try {
            boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");

            ProcessBuilder pb = (!isWin)? new ProcessBuilder("bash", "-c", command) : 
            new ProcessBuilder("cmd.exe", "/c", command);
            pb.inheritIO(); // show ffmpeg output directly
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            System.err.println("Error executing command: " + e.getMessage());
            System.exit(1);
        }
    }
    private static void deleteDirectory(Path path) throws IOException {
        if (Files.notExists(path)) return;
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        System.err.println("Failed to delete: " + p + " " + e.getMessage());
                    }
                });
    }
}
