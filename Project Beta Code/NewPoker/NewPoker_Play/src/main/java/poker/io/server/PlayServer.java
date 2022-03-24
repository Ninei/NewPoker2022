package poker.io.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.ninei.server.RealTime.RealtimeTCPServer;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import poker.io.config.PokerConfig;
import poker.io.service.PokerServiceManager;

@Log4j2
@Component
public class PlayServer extends RealtimeTCPServer {

    @Override
    public void start() throws Exception {

        final EventLoopGroup boss = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        final EventLoopGroup worker = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            if (Epoll.isAvailable()) { // Linux Only, 검증안됨...
                bootstrap.option(ChannelOption.SO_BACKLOG, configure.getBacklog())
                    .channel(EpollServerSocketChannel.class)
                    .childOption(ChannelOption.SO_LINGER, 0)
                    .childOption(ChannelOption.SO_REUSEADDR, true);
                log.info("Epoll Mode Active!!");
            } else {
                bootstrap.channel(NioServerSocketChannel.class);
                log.info("Nio Mode Active!!");
            }

            // FIXME: 테스트 필요
            bootstrap.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3 * 1000);

            bootstrap.group(boss, worker)
                .handler(new LoggingHandler(this.configure.getRootLogLevel()))
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new PlayServerChannelInitializer(configure, pokerServiceManager))
                .childOption(ChannelOption.SO_KEEPALIVE, configure.isKeepAlive());

            bootstrap.bind(configure.getNettyPort()).sync().channel().closeFuture().sync();
        } catch (Exception e) {
            throw e;
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    private PlayServer(PokerConfig pokerConfig, PokerServiceManager pokerServiceManager) {
        super();
        this.configure = pokerConfig;
        this.pokerServiceManager = pokerServiceManager;
    }

    private final PokerConfig configure;
    private final PokerServiceManager pokerServiceManager;
}
