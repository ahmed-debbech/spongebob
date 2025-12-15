package com.debbech.spongebob.soundcloud;

import com.debbech.spongebob.websocket.WsServer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Download {

    public void start(List<Data.Track> trackList) throws Exception {

        for(Data.Track tr : trackList){
            try {
                ProcessBuilder pb = new ProcessBuilder("sh", "-c", "./scdl_bin -p download -b " + tr.permalink_url.toString());
                pb.inheritIO();
                Process process = pb.start();
                process.waitFor();
                WsServer.getInstance().adminBroadcast("Downloaded " + tr.title);
            } catch (Exception e) {
                File obj = new File("./download");
                deleteDirectory(obj);
                throw new Exception("Error executing command: " + e.getMessage());
            }
        }

        List<Path> files;
        try (var stream = Files.list(Paths.get("./download"))) {
            files = stream
                    .filter(Files::isRegularFile)
                    .toList();
        }

        for (Path oggFile : files){
            try {
                new ProcessBuilder(
                        "sh",
                        "-c",
                        "ffmpeg -i \""+oggFile+"\" -map_metadata 0 -vn -c:a libmp3lame -q:a 0 \""+oggFile+".mp3\""
                ).start().waitFor();

                WsServer.getInstance().adminBroadcast("converted " + oggFile +" to mp3");

                Files.delete(oggFile);
            }catch (Exception e){
                File obj = new File("./download");
                deleteDirectory(obj);
                throw new Exception("Error executing command for converting ogg -> mp3: " + e.getMessage());
            }
        }

        WsServer.getInstance().adminBroadcast("zipping files...");

        SimpleDateFormat sdf
                = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String now = sdf.format(new Date());

        try{
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", "zip -r playlist-"+now+".zip download ");
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            File obj = new File("./download");
            deleteDirectory(obj);
            throw new Exception("could not zip folder: " + e.getMessage());
        }

        File obj1 = new File("./scdownloads");
        deleteDirectory(obj1);

        Files.move
                (Paths.get("playlist-"+now+".zip"),
                        Paths.get("./scdownloads/playlist-"+now+".zip"));

        File obj = new File("./download");
        deleteDirectory(obj);

        WsServer.getInstance().adminBroadcast("Download ready!");

        WsServer.getInstance().adminBroadcast("[DONE]");
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
