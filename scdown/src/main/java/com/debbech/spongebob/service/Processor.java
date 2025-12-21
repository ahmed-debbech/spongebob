package com.debbech.spongebob.service;

import com.debbech.spongebob.model.StoredTrack;
import com.debbech.spongebob.model.TrackStatus;
import com.debbech.spongebob.soundcloud.Data;
import com.debbech.spongebob.soundcloud.Download;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class Processor {

    private static Logger log = LoggerFactory.getLogger(Processor.class);

    public static int thresholdToProcess = 4;

    public static boolean process(){
        log.info("[PROCESS_JOB]");
        try {
            List<StoredTrack> newTracklist = Library.getInstance().getDownloadedTracks();
            log.info("you have {} tracks downloaded ready to process", newTracklist.size());
            if(newTracklist.size() < thresholdToProcess) {
                log.info("skipping...");
                return true;
            }

            //fire request to queue
            //mark them as IN_REVIEW
            Library.getInstance().add(newTracklist.subList(0, thresholdToProcess).stream().map((e)->{ e.setStatus(TrackStatus.IN_REVIEW); return e;}).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("could not process the new tracklist because {}", e.getMessage());
            return false;
        }
        return true;
    }

    public static void download(){

        log.info("[DOWNLOAD_JOB]");
        List<StoredTrack> newTracklist = Library.getInstance().getUnprocessedTracks();
        log.info("you have {} new unprocessed tracks to be downloaded", newTracklist.size());
        try {
            new Download().startInternally(newTracklist);
        } catch (Exception e) {
            log.error("could not download newly added tracks because {}", e.getMessage());
        }

    }
}
