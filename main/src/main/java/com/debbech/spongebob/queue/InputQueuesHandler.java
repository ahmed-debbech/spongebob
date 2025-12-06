package com.debbech.spongebob.queue;

import com.debbech.spongebob.Config;
import com.debbech.spongebob.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputQueuesHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public boolean handle_in_proc_qu(String message){
        log.info("Handling event from {} ", Config.getInstance().in_proc_qu);
        try {
            Core.run(new String[]{"X", Config.getInstance().container_mp3_path});
            OutputQueues.publish_out_upl_yt("done");
        }catch(Exception e){
            log.error("An error occured while processing and rendering a new mp4 video: {}", e.getMessage());
            return false;
        }
        return true;
    }
}
