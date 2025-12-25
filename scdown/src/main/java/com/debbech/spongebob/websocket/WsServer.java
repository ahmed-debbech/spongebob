package com.debbech.spongebob.websocket;

import com.debbech.spongebob.service.Processor;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class WsServer {

    private static Logger log = LoggerFactory.getLogger(WsServer.class);
    private static final Map<WsContext, Integer> clients = new ConcurrentHashMap<>();
    private static WsServer instance;

    private WsServer(){

    }

    public static WsServer getInstance(){
        if(instance == null){
            instance = new WsServer();
        }
        return instance;
    }

    public void onMessage(WsMessageContext ctxx){

        synchronized(clients){
            log.info("new message sent by client");
            clients.keySet().stream().filter(ctx -> ctx.session.isOpen()).forEach(session -> {
                session.send(
                        Map.of(
                                "userMessage", ctxx.message(),
                                "userlist", clients.values()
                        )
                );
            });
        }

    }

    public void adminBroadcast(String msg){
        synchronized(clients){
            log.info("new message sent by admin");
            clients.keySet().stream().filter(ctx -> ctx.session.isOpen()).forEach(session -> {
                session.send(msg);
            });
        }
    }

    public void onConnect(WsContext ctx){
        if(clients.isEmpty()) {
            log.info("new client connected");
            // Add session to the connected sessions set
            Random r= new Random();
            int i = r.nextInt(99);
            clients.put(ctx, i);
            adminBroadcast(Processor.getStatus());
        }else{
            ctx.closeSession();
        }
    }

    public void onClose(@NotNull WsContext ctx) {
        // Remove session from the connected sessions set
        log.info("a client disconnected");
        clients.remove(ctx);
    }
}