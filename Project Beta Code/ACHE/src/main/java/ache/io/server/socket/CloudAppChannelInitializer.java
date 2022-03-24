package ache.io.server.socket;

import ache.ACHEContext;
import ache.io.config.ACHEConfig;
import ache.io.config.SecurityConfig;
import ache.io.service.CloudServiceManager;
import io.netty.channel.*;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.ninei.server.Web.DefaultHttpInboundHandler;

import java.util.concurrent.TimeUnit;

public class CloudAppChannelInitializer extends ChannelInitializer<Channel> implements ACHEContext {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        try {
            final SslContext sslCtx = SecurityConfig.initSslContext(rootConfig.isLive());
            ChannelPipeline pipeline = ch.pipeline();

            if (sslCtx != null) { pipeline.addLast(sslCtx.newHandler(ch.alloc()));}

            // in & out bound handler
            pipeline.addLast(new HttpServerCodec()); // Inbound ↓  & Outbound ↑
            pipeline.addLast(new WebSocketServerCompressionHandler()); // Inbound ↓ & Outbound ↑

            final CloudAppHandler cloudAppHandler = new CloudAppHandler(cloudServiceManager);

            // inbound handler, replaced webSocket
            pipeline.addLast(new HttpInboundHandler(new CloudAppWebsocketInboundHandler(cloudAppHandler))); // Inbound ↓
//            pipeline.addLast(new IdleStateHandler(60, 60, 0, TimeUnit.SECONDS));

            pipeline.addLast(cloudAppHandler); // Inbound ↓

        } catch (Exception e) {
            throw e;
        }
    }

    public CloudAppChannelInitializer(ACHEConfig configure, CloudServiceManager cloudServiceManager) {
        this.rootConfig = configure;
        this.cloudServiceManager = cloudServiceManager;
    }

    private final ACHEConfig rootConfig;
    private final CloudServiceManager cloudServiceManager;
}
