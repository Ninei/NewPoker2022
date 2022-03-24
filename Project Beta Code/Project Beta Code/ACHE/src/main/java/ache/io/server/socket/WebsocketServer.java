package ache.io.server.socket;

import ache.io.config.ACHEConfig;
import ache.io.service.CloudServiceManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.ninei.server.RealTime.RealtimeTCPServer;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class WebsocketServer extends RealtimeTCPServer {

    @Override
    public void start() throws Exception {

        final EventLoopGroup boss = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        final EventLoopGroup worker = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        log.info("{} Mode Active!!", Epoll.isAvailable() ? "Epoll" : "Nio");

        try {
            ViewerChannelInitializer viewerInitializer = new ViewerChannelInitializer(configure, cloudServiceManager);
            CloudAppChannelInitializer cloudAppInitializer = new CloudAppChannelInitializer(configure, cloudServiceManager);
            createBootstrap(boss, worker, configure.getCloudAppPort(), LogLevel.DEBUG, cloudAppInitializer);
            ChannelFuture f = createBootstrap(boss, worker, configure.getPlayPort(), LogLevel.DEBUG, viewerInitializer);
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            throw e;
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    private ChannelFuture createBootstrap(EventLoopGroup boss, EventLoopGroup worker, int port, LogLevel level,
                                          ChannelInitializer initializer) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
            .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024) // 동시 Connection() 요청 가능 개수, 전체 동접 연결유지 X
            .option(ChannelOption.SO_REUSEADDR, true) // TIME_WAIT 포트 재사용
            .childOption(ChannelOption.SO_LINGER, 0) // 소켓 close시 신뢰성있는 종료를 위해 4way-handshake 발생, 이때 TIME_WAIT 리소스 낭비 방지 위해 0으로 설정
            .childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000) // Connection 타임아웃, 연결유지 X
            .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true) // OS 커널값을 기준으로 서로 통신이 가능한 상태인지 확인
            .childHandler(initializer);
        return bootstrap.bind(port).sync();
    }

    private WebsocketServer(ACHEConfig rootConfig, CloudServiceManager cloudServiceManager) {
        super();
        this.configure = rootConfig;
        this.cloudServiceManager = cloudServiceManager;
    }

    private final ACHEConfig configure;
    private final CloudServiceManager cloudServiceManager;
}
