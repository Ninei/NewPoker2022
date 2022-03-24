package poker.io.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.ninei.server.Web.DefaultHttpInboundHandler;
import poker.io.config.PokerConfig;
import poker.io.codec.PokerDecoder;
import poker.io.codec.PokerEncoder;
import poker.io.config.SecurityConfig;
import poker.io.service.PokerServiceManager;

public class PlayServerChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        try {
            final SslContext sslCtx = SecurityConfig.initSslContext(pokerConfig.isLive());
            ChannelPipeline pipeline = ch.pipeline();
//            if(pokerConfigure.getRootLogLevel().equalsIgnoreCase("debug")) {
//                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
//            }
            //FIXME: 로깅 핸들러를 달지 않으면 3명이상 방 입장시 에러 발생, 서버가 느려지는 듯
            pipeline.addLast(new LoggingHandler(LogLevel.INFO));

            if (sslCtx != null) {
                pipeline.addLast(sslCtx.newHandler(ch.alloc()));
            }

            switch ("websocket") {
                case "websocket":
                    // in & out bound handler
                    pipeline.addLast(new HttpServerCodec());
                    // inbound business logic handler
                    pipeline.addLast(new DefaultHttpInboundHandler(new PlayDefaultWebSocketInboundHandler())); // replaced webSocket, read
                case "tcp":
                default:
                    // inbound handler
                    pipeline.addLast(new PokerDecoder());

                    // outbound handler
                    pipeline.addLast(new PokerEncoder());

                    // inbound business logic handler
                    pipeline.addLast(new PlayServerHandler(pokerServiceManager));

            }
        } catch (Exception e) {
            throw new Exception();
        }
    }

    public PlayServerChannelInitializer(PokerConfig configure, PokerServiceManager pokerServiceManager) {
        this.pokerConfig = configure;
        this.pokerServiceManager = pokerServiceManager;
    }

    private static final StringDecoder stringDecoder = new StringDecoder();
    private static final StringEncoder stringEncoder = new StringEncoder();

    private final PokerConfig pokerConfig;
    private final PokerServiceManager pokerServiceManager;
}
