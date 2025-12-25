package com.debbech.spongebob.persistence;

import com.debbech.spongebob.model.StoredTrack;
import com.debbech.spongebob.model.TrackStatus;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

    public static RedissonClient redisClient;

    private void openSocket(){

        if(redisClient == null){
            Config config = new Config();
            config.useSingleServer()
                    .setAddress("redis://database:6379");

            redisClient = Redisson.create(config);
        }
        if(redisClient.isShutdown() || redisClient.isShuttingDown()) {
            redisClient.shutdown();
            Config config = new Config();
            config.useSingleServer()
                    .setAddress("redis://database:6379");

            redisClient = Redisson.create(config);
        }
    }

    public void storeTrack(StoredTrack storedTrack){
        openSocket();
        RBucket<StoredTrack> bucket = redisClient.getBucket("track:"+storedTrack.getTrack().id.toString());
        bucket.set(storedTrack);
    }
    public StoredTrack getStoredTrack(String id){
        openSocket();
        RBucket<StoredTrack> bucket = redisClient.getBucket(id);
        return bucket.get();
    }
    public Map<String, StoredTrack> getAllStoredTracks(){
        openSocket();
        Iterable<String> keys = redisClient.getKeys().getKeysByPattern("track:*");

        Map<String, StoredTrack> result = new ConcurrentHashMap<>();
        for (String key : keys) {
            StoredTrack st = redisClient.<StoredTrack>getBucket(key).get();
            if (st != null) {
                result.put(key, st);
            }
        }
        return result;
    }
    public void removeStoredTrack(String id){
        openSocket();
        RBucket<StoredTrack> bucket = redisClient.getBucket(id);
        bucket.delete();
    }

    public long getCurrentPlaylistNumber(){
        openSocket();
        RAtomicLong atomic = redisClient.getAtomicLong("currentPlaylistNumber");
        long value = atomic.get();
        return value;
    }

    public void incrementCurrentPlaylistNumber(){
        openSocket();
        RAtomicLong atomic = redisClient.getAtomicLong("currentPlaylistNumber");
        atomic.incrementAndGet();
    }

    public void setCurrentPlaylistLink(String link) {
        openSocket();
        RBucket<String> bucket = redisClient.getBucket("playlist_link");
        bucket.set(link);
    }

    public String getCurrentPlaylistLink() {
        openSocket();
        RBucket<String> bucket = redisClient.getBucket("playlist_link");
        return bucket.get();
    }
}
