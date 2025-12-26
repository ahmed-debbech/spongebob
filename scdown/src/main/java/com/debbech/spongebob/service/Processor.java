package com.debbech.spongebob.service;

import com.debbech.spongebob.model.SerializableStatus;
import com.debbech.spongebob.model.StoredTrack;
import com.debbech.spongebob.model.TrackStatus;
import com.debbech.spongebob.queue.OutputQueues;
import com.debbech.spongebob.model.ProcessRequestMessage;
import com.debbech.spongebob.soundcloud.Data;
import com.debbech.spongebob.soundcloud.Download;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;


public class Processor {

    private static Logger log = LoggerFactory.getLogger(Processor.class);

    public static int thresholdToProcess = 3;

    public static boolean process(){
        try {
            List<StoredTrack> newTracklist = Library.getInstance().getDownloadedTracks();
            if(newTracklist.isEmpty()) return true;
            log.info("you have {} tracks downloaded ready to process", newTracklist.size());
            if(newTracklist.size() < thresholdToProcess) {
                log.info("skipping...");
                return true;
            }

            PlaylistService.incCurrentPlaylist();
            ProcessRequestMessage p = new ProcessRequestMessage();
            p.playlistDirectoryName = newTracklist.get(0).getPlaylistDirName();
            OutputQueues.publish_out_proc_qu(p.toJson());

            Library.getInstance().add(newTracklist.subList(0, thresholdToProcess).stream().map((e)->{ e.setStatus(TrackStatus.IN_REVIEW); return e;}).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("could not process the new tracklist because {}", e.getMessage());
            return false;
        }
        return true;
    }

    public static void download(){

        List<StoredTrack> newTracklist = Library.getInstance().getUnprocessedTracks();
        if(newTracklist.isEmpty()) return;
        log.info("you have {} new unprocessed tracks to be downloaded", newTracklist.size());
        try {
            String plname = String.valueOf(PlaylistService.getCurrentPlaylist());
            new Download().startInternally(newTracklist, plname);
        } catch (Exception e) {
            log.error("could not download newly added tracks because {}", e.getMessage());
        }

    }

    public static String getStatus(){
        SerializableStatus ss = new SerializableStatus();
        String currentPlLink = PlaylistService.getPlaylistLink();
        ss.playlistLink = currentPlLink;
        ss.library = Library.getInstance().getAllStoredTracks();
        Gson g = new Gson();
        return g.toJson(ss, SerializableStatus.class);
    }
}
