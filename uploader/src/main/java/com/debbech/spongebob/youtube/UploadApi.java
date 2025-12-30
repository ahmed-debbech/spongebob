package com.debbech.spongebob.youtube;

import com.debbech.spongebob.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
                    "./upload_bin -quiet -secrets google.json -filename "+ yv.getPathOnDisk()
                    + " -title " + yv.getPathOnDisk().split("/")[yv.getPathOnDisk().split("/").length-1]
                    ;
            try {
                ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
                pb.inheritIO(); // show uploader_bin output directly
                Process process = pb.start();
                process.waitFor();
            } catch (Exception e) {
                throw new Exception("Error executing command: " + e.getMessage());
            }
        }catch (Exception e){
            throw e;
        }
    }
}

