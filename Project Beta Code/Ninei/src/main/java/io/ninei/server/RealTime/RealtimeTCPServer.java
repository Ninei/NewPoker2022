package io.ninei.server.RealTime;

import io.ninei.server.DefaultServer;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class RealtimeTCPServer implements DefaultServer {

    @Override
    public void start() throws Exception {
        log.warn("RealtimeTCPServer Active!!");
    }

    @Override
    public void stop() throws Exception {
        log.warn("RealtimeTCPServer Stop!!");
    }
}
