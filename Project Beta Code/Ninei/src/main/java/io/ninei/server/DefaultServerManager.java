package io.ninei.server;

import io.ninei.global.DefaultContext;
import java.util.ArrayList;

public abstract class DefaultServerManager implements DefaultContext {

    public void startGroup() throws Exception {
        for (int i=0; i<serverGroup.size(); i++) {
            serverGroup.get(i).start();
        }
    }

    public void stopGroup() throws Exception {
        for (int i=0; i<serverGroup.size(); i++) {
            serverGroup.get(i).stop();
        }
    }

    protected void addServer(DefaultServer server) throws Exception {
        serverGroup.add(server);
    }

    private ArrayList<DefaultServer> serverGroup = new ArrayList<>();
}
