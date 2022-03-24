package io.ninei.server.Web;

import io.ninei.server.DefaultServer;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class DefaultWebServer implements DefaultServer {

    @Override
    public void start() throws Exception {
        log.info("WebServer Active!!");
    }

    @Override
    public void stop() throws Exception {
        log.info("WebServer Stop!!");
    }
}
