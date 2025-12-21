package com.debbech.spongebob.service;

import com.debbech.spongebob.model.StoredTrack;
import com.debbech.spongebob.model.TrackStatus;
import com.debbech.spongebob.persistence.Database;
import com.debbech.spongebob.soundcloud.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Library {

    private static Logger log = LoggerFactory.getLogger(Library.class);

    private static Library instance = null;

    private static Map<String, StoredTrack> collectionInDB;
    private static long processingIndex;

    public void add(List<StoredTrack> tracksToBeAdded) {
        for(StoredTrack st : tracksToBeAdded) {
            new Database().storeTrack(st);
        }
        refreshInMemoryLibrary();
    }

    public void remove(StoredTrack track) {
        StoredTrack st = new Database().getStoredTrack(track.getTrack().id.toString());
        if(st == null){
            log.warn("could not remove object {} because it does not exists", track.getTrack().id.toString());
            return;
        }
        if(!st.getStatus().name().equals(TrackStatus.UNPROCESSED.name())){
            log.info("could not remove object {} because it is already processes", track.getTrack().id.toString());
            return;
        }
        new Database().removeStoredTrack(track.getTrack().id.toString());
        refreshInMemoryLibrary();
    }

    private Library(){

    }

    public List<StoredTrack> getUnprocessedTracks(){

        List<StoredTrack> unproc = new ArrayList<>();
        for(Map.Entry<String, StoredTrack> t : collectionInDB.entrySet()){
            if(t.getValue().getStatus().name().equals(TrackStatus.UNPROCESSED.name())){
                unproc.add(t.getValue());
            }
        }
        return unproc;
    }

    public List<StoredTrack> getDownloadedTracks(){

        List<StoredTrack> unproc = new ArrayList<>();
        for(Map.Entry<String, StoredTrack> t : collectionInDB.entrySet()){
            if(t.getValue().getStatus().name().equals(TrackStatus.DOWNLOADED.name())){
                unproc.add(t.getValue());
            }
        }
        return unproc;
    }


    public StoredTrack getOne(String id){
        return collectionInDB.get(id);
    }

    private static void refreshInMemoryLibrary() {
        collectionInDB = new Database().getAllStoredTracks();
    }

    public static Library getInstance(){
        if(instance == null){
            refreshInMemoryLibrary();
            instance = new Library();
        }
        return instance;
    }
}
