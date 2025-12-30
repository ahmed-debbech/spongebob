package com.debbech.spongebob.control;

import com.debbech.spongebob.Config;
import com.debbech.spongebob.youtube.TokenResp;
import com.debbech.spongebob.youtube.YoutubeCore;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthRestController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public void startAuthRestServer(){
        Javalin.create()
                .get("oauth", this::handleOauthCode)
                .start(Config.getInstance().authServerPort);
    }

    private void handleOauthCode(Context ctx){
        String code = ctx.queryParam("code");
        log.info("got 'code' from google's response");
        TokenResp tr = new TokenResp();
        try {
            tr = YoutubeCore.getInstance().getTokens(code);
        } catch (Exception e) {
            log.error("could not retrieve access tokens from google after receiving the code {}", e.getMessage());
            ctx.status(500);
            ctx.result("{\"success\":false, \"error\":"+e.getMessage()+"}");
            return;
        }
        log.info("Auth is done successfully with youtube");
        ctx.status(200);
        ctx.result("{\"success\":true}");
    }

}
