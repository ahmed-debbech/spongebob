package com.debbech.spongebob.control;

import com.debbech.spongebob.Config;
import com.debbech.spongebob.control.model.SetPlaylistReq;
import com.debbech.spongebob.soundcloud.Data;
import com.debbech.spongebob.soundcloud.PlaylistValidator;
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
                .get("setPlaylist", this::handleSetPlaylist)
                .start(Config.getInstance().PORT);
    }

    private void handleSetPlaylist(Context ctx){
        log.info("checking if playlist is valid and public");
        try {

            Gson gson = new Gson();
            SetPlaylistReq playlist = gson.fromJson(ctx.body(), SetPlaylistReq.class);

            PlaylistValidator playlistValidator = new PlaylistValidator();
            List< Data.Track> tracklist = playlistValidator.validate(playlist.playlistUrl);

            String resp = gson.toJson(tracklist);
            ctx.status(200);
            ctx.result(resp);
        }catch(Exception e){
            log.error("error occured while setting playlist: {}", e.getMessage());
            ctx.status(400);
            ctx.result("{\"is_valid\":false, \"because\": \""+e.getMessage()+"\"}");
        }
    }

}
