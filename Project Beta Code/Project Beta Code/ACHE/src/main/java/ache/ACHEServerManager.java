package ache;

import ache.io.server.socket.WebsocketServer;
import ache.io.server.web.WebServer;
import io.ninei.server.DefaultServerManager;
import org.springframework.stereotype.Component;

@Component
public class ACHEServerManager extends DefaultServerManager implements ACHEContext {

    public void addServer() throws Exception {
//        addServer(webServer);
        addServer(websocketServer);
    }

    private ACHEServerManager(WebsocketServer websocketServer, WebServer webServer) {
        this.websocketServer = websocketServer;
        this.webServer = webServer;
    }

    private final WebsocketServer websocketServer;
    private final WebServer webServer;
}
