package com.debbech.spongebob.queue;

import com.debbech.spongebob.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputQueuesHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public boolean handle_in_upl_yt(String message){
        log.info("Handling event from {} ", Config.getInstance().in_upl_yt);
        try {
            //TODO DO the upload
        }catch(Exception e){
            log.error("An error occured while uploading video to youtube because: {}", e.getMessage());
            return false;
        }
        return true;
    }
}
