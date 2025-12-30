package com.debbech.spongebob.queue;

import com.debbech.spongebob.Config;
import com.debbech.spongebob.queue.messages.UploadRequestMessage;
import com.debbech.spongebob.youtube.YoutubeCore;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputQueuesHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public boolean handle_in_upl_yt(String message){
        log.info("Handling event from {} ", Config.getInstance().in_upl_yt);
        try {
            Gson g = new Gson();
            UploadRequestMessage urm = g.fromJson(message, UploadRequestMessage.class);
            YoutubeCore.getInstance().upload(urm.video_name);
        }catch(Exception e){
            log.error("An error occured while uploading video to youtube because: {}", e.getMessage());
            return false;
        }
        return true;
    }
}
