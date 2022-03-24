package ache.io.server.socket;

import ache.ACHEContext;
import ache.io.config.ACHEConfig;
import ache.io.config.SecurityConfig;
import ache.io.service.CloudServiceManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class ViewerChannelInitializer extends ChannelInitializer<Channel> implements ACHEContext {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        try {
            final SslContext sslCtx = SecurityConfig.initSslContext(rootConfig.isLive());
            ChannelPipeline pipeline = ch.pipeline();

            if (sslCtx != null) { pipeline.addLast(sslCtx.newHandler(ch.alloc()));}

            // in & out bound handler
            pipeline.addLast(new HttpServerCodec()); // Inbound ↓  & Outbound ↑
            pipeline.addLast(new WebSocketServerCompressionHandler()); // Inbound ↓ & Outbound ↑

            final ViewerHandler viewerHandler = new ViewerHandler(cloudServiceManager);

            // inbound handler, replaced webSocket
            pipeline.addLast(new HttpInboundHandler(new ViewerWebsocketInboundHandler(viewerHandler))); // Inbound ↓
            pipeline.addLast(new IdleStateHandler(60, 60, 0, TimeUnit.SECONDS));

            pipeline.addLast(viewerHandler); // Inbound ↓

        } catch (Exception e) {
            throw e;
        }
    }

    public ViewerChannelInitializer(ACHEConfig acheConfig, CloudServiceManager cloudServiceManager) {
        this.rootConfig = acheConfig;
        this.cloudServiceManager = cloudServiceManager;
    }

    private final ACHEConfig rootConfig;
    private final CloudServiceManager cloudServiceManager;
}
