package com.debbech.spongebob.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiskManager {

    public boolean getMp3s(Path aggFilePath, String mp3Path) throws Exception{
        boolean atLeastOne = false;
        try (BufferedWriter writer = Files.newBufferedWriter(aggFilePath)) {
            try (Stream<Path> files = Files.list(Path.of(mp3Path))) {
                for (Path entry : files.collect(Collectors.toList())) {
                    if (entry.toString().toLowerCase().endsWith(".mp3")) {
                        writer.write("file '" + entry.toAbsolutePath().toString().replace("\\", "/") + "'");
                        writer.newLine();
                        atLeastOne = true;
                    }
                }
            }
        }catch (Exception e){
            throw new Exception("could not write to agg file because: "+ e.getMessage());
        }
        if (!atLeastOne) {
            throw new Exception("No MP3 found in the folder specified");
        }
        return atLeastOne;

    }

    public Path createBuildDir() throws Exception {
        Path buildDir = Paths.get("build");
        try {
            if (Files.exists(buildDir)) {
                deleteDirectory(buildDir);
            }
            Files.createDirectory(buildDir);
        } catch (IOException e) {
            throw new Exception("Error creating build directory: " + e.getMessage());
        }
        return buildDir;
    }

    private void deleteDirectory(Path path) throws Exception  {
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
