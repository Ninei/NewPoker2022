package io.ninei.server;

import io.ninei.global.DefaultContext;

public interface DefaultServer extends DefaultContext {

    void start() throws Exception;
    void stop() throws  Exception;
}
