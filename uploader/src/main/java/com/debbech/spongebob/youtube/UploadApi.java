package com.debbech.spongebob.youtube;

import com.debbech.spongebob.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

public class UploadApi {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private YoutubeVideo video;

    public void doUpload(YoutubeVideo yv) throws Exception{
        log.info("starting effective upload at "+  LocalDateTime.now());
        this.video = yv;
        try {
            log.info("youtube {}",yv.getPathOnDisk());

            String command = ""+
                    "./upload_bin -secrets google.json -filename "+ yv.getPathOnDisk()
                    + " -title " + yv.getPathOnDisk().split("/")[yv.getPathOnDisk().split("/").length-1]
                    ;
            try {
                ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
                Process process = pb.start();

                YoutubeCore.getInstance().progress = null;

                new Thread(() -> {
                    try (BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = out.readLine()) != null) {
                            if(line.startsWith("Progress:")){
                                YoutubeCore.getInstance().progress = line;
                            }
                        }
                    } catch (Exception ignored) {
                        log.error("cant keep up with logs for the uploader command");
                        YoutubeCore.getInstance().progress = null;
                    }
                }).start();

                int exitCode = process.waitFor();
                YoutubeCore.getInstance().progress = null;

                if (exitCode != 0) {
                    log.error("video upload failed to be uploaded after binary finished with exit code 1");
                    throw new Exception("Video failed to be uploaded with exit code " + exitCode);
                }
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }catch (Exception e){
            YoutubeCore.getInstance().informDashService(false);
            throw e;
        }
    }
}

