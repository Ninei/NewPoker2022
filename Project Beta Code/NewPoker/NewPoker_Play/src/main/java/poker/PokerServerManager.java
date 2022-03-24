package poker;

import io.ninei.server.DefaultServerManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.io.server.PlayServer;
import poker.io.server.WebServer;

@Log4j2
@Component
public class PokerServerManager extends DefaultServerManager implements PokerContext {

    public void addServer() throws Exception {
        addServer(webServer);
        addServer(playServer);
    }

    private PokerServerManager(PlayServer playServer, WebServer webServer) {
        this.playServer = playServer;
        this.webServer = webServer;
    }

    private PlayServer playServer;
    private WebServer webServer;
}
