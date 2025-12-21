package com.debbech.spongebob.control;

import com.debbech.spongebob.Config;
import com.debbech.spongebob.control.model.SetPlaylistReq;
import com.debbech.spongebob.model.StoredTrack;
import com.debbech.spongebob.model.TrackStatus;
import com.debbech.spongebob.service.Library;
import com.debbech.spongebob.service.Processor;
import com.debbech.spongebob.soundcloud.CurrentPlaylist;
import com.debbech.spongebob.soundcloud.Data;
import com.debbech.spongebob.soundcloud.Download;
import com.debbech.spongebob.soundcloud.PlaylistValidator;
import com.debbech.spongebob.websocket.WsServer;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RestController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public void startRestServer(){
        Javalin.create()
                .post("setPlaylist", this::handleSetPlaylist)
                .get("downloadPlaylist", this::handleDownloadPlaylist)
                .get("setThreshold/<value>", this::handleSetThreshold)
                .start(Config.getInstance().PORT);

        Javalin.create(config -> {
            config.router.mount(router -> {
                router.ws("/chat", ws -> {
                    ws.onConnect(ctx -> {
                        WsServer.getInstance().onConnect(ctx);
                    });
                    ws.onClose(ctx -> {
                        WsServer.getInstance().onClose(ctx);
                    });
                    ws.onMessage(ctx -> {
                        WsServer.getInstance().onMessage(ctx);
                    });
                });
            });
        }).start(Config.getInstance().websocket_port);
    }

    private void handleSetThreshold(Context ctx){
        log.info("setting new threshold {}", ctx.pathParam("value"));
        if(ctx.pathParam("value").isEmpty()){
            log.error("threshold value is not set when calling the api");
            ctx.status(400);
            ctx.result("{\"is_set\":false}");
            return;
        }
        try {
            int newVal = Integer.parseInt(ctx.pathParam("value"));
            Processor.thresholdToProcess = newVal;
        }catch (NumberFormatException e){
            log.error("not a number set in threshold value");
            ctx.status(400);
            ctx.result("{\"is_set\":false}");
            return;
        }
        ctx.status(200);
        ctx.result("{\"is_set\":true}");
    }

    private void handleDownloadPlaylist(Context ctx) {
        log.info("trying to download playlist");
        try {

            Download download = new Download();

            if(!CurrentPlaylist.getInstance().isGettingDownloaded()) {
                CurrentPlaylist.getInstance().markAsDownloading();
                download.start(CurrentPlaylist.getInstance().getTrackList());
                CurrentPlaylist.getInstance().reset();
            }else{
                CurrentPlaylist.getInstance().reset();
                throw new Exception("already downloading this playlist");
            }

            ctx.status(200);
            ctx.result("{\"is_valid\":true}");
        } catch (Exception e) {
            log.error("error occured while downloading playlist: {}", e.getMessage());
            ctx.status(400);
            ctx.result("{\"is_valid\":false, \"because\": \"" + e.getMessage() + "\"}");
        }
    }


    private void handleSetPlaylist(Context ctx) {
        log.info("checking if playlist is valid and public");
        try {
            if(CurrentPlaylist.getInstance().isGettingDownloaded()) {
                throw new Exception("already downloading this playlist");
            }

            Gson gson = new Gson();
            SetPlaylistReq playlist = gson.fromJson(ctx.body(), SetPlaylistReq.class);

            PlaylistValidator playlistValidator = new PlaylistValidator();
            List<Data.Track> tracklist = playlistValidator.validate(playlist.playlistUrl);

            for(Data.Track track : tracklist) {
                StoredTrack ss = new StoredTrack(track, TrackStatus.UNPROCESSED);
                Library.getInstance().addOnlyNew(List.of(ss));
            }

            String resp = gson.toJson(tracklist);
            ctx.status(200);
            ctx.result(resp);
        } catch (Exception e) {
            log.error("error occured while setting playlist: {}", e.getMessage());
            ctx.status(400);
            ctx.result("{\"is_valid\":false, \"because\": \"" + e.getMessage() + "\"}");
        }
    }

}
