package com.debbech.spongebob.control;

import com.debbech.spongebob.queue.InputQueuesRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public void listenForEvents(){
        try {
            InputQueuesRegistrar.getInstance().registerAll();
        }catch (Exception e){
            log.error("{}", e.getMessage());
        }
    }
}
