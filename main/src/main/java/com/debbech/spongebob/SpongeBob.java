package com.debbech.spongebob;

import com.debbech.spongebob.control.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SpongeBob {

    private static Logger log = LoggerFactory.getLogger(SpongeBob.class);

    public static void main(String[] args) {

        log.info("Spongebob main started up & running...");

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

        Controller controller = new Controller();
        controller.listenForEvents();
    }

}
