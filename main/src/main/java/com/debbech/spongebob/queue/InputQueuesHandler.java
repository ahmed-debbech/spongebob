package com.debbech.spongebob.queue;

import com.debbech.spongebob.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputQueuesHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public void handle_in_proc_qu(String message){
        log.info("Handling event from {} ", Config.getInstance().in_proc_qu);
    }
}
