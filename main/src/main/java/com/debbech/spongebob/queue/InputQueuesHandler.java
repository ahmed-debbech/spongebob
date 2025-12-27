package com.debbech.spongebob.queue;

import com.debbech.spongebob.Config;
import com.debbech.spongebob.core.Core;
import com.debbech.spongebob.queue.messages.ProcessRequestMessage;
import com.debbech.spongebob.queue.messages.UploadRequestMessage;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputQueuesHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public boolean handle_in_proc_qu(String message){
        log.info("Handling event from {} ", Config.getInstance().in_proc_qu);
        try {
            log.info("message {}", message);
            Core.run(new String[]{"X", Config.getInstance().container_mp3_path + "/" + ProcessRequestMessage.fromJson(message).playlistDirectoryName});

            UploadRequestMessage urm = new UploadRequestMessage();
            urm.video_name = "output.mp4";
            Gson g = new Gson();
            OutputQueues.publish_out_upl_yt(g.toJson(urm));
        }catch(Exception e){
            log.error("An error occured while processing and rendering a new mp4 video: {}", e.getMessage());
            return false;
        }
        return true;
    }
}
