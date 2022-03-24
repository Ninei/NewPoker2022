package poker.io.config;

import io.ninei.global.DefaultConfig;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import poker.PokerContext;

@Getter
@Configuration
public class PokerConfig extends DefaultConfig implements PokerContext {

    @Value("${server.address}")
    protected String serverAddress;

    @Value("${netty.port}")
    protected int nettyPort;

    @Value("${netty.so.keepAlive}")
    protected boolean keepAlive;

    @Value("${netty.so.backlog}")
    protected int backlog;

    @Value("${netty.transfer.type}")
    protected String nettyTransferType;

    @Value("${netty.transfer.path}")
    protected String nettyTransferPath;

    @Value("${logging.level.root}")
    protected String rootLogLevel;

    @Value("${spring.profiles.active}")
    protected String active;

    public boolean isLive() { return isLive(active); }
}
