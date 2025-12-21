package com.debbech.spongebob;

import com.debbech.spongebob.control.RestController;
import com.debbech.spongebob.service.Library;
import com.debbech.spongebob.service.Processor;
import com.debbech.spongebob.service.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SpongeBob {

    private static Logger log = LoggerFactory.getLogger(SpongeBob.class);

    public static void main(String[] args) {

        log.info("Spongebob scdown started up & running...");


        new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    log.error("RUNTIME ERROR: app is exiting and can't stay up in background");
                    throw new RuntimeException(e);
                }
            }
        }).start();

        RestController authRestController = new RestController();
        authRestController.startRestServer();

        Library.getInstance();
        new Scheduler();
    }

}
