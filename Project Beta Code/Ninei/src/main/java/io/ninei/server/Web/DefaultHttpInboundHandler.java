package io.ninei.server.Web;

import io.netty.channel.*;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.ssl.SslHandler;
import io.ninei.global.DefaultContext;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class DefaultHttpInboundHandler extends ChannelInboundHandlerAdapter implements DefaultContext {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
//            log.info(LOG_NET_HEAD, ctx.channel().remoteAddress(), "Web Request Received!!");
            HttpHeaders headers = httpRequest.headers();
            if ("Upgrade".equalsIgnoreCase(headers.get(HttpHeaderNames.CONNECTION)) &&
                "WebSocket".equalsIgnoreCase(headers.get(HttpHeaderNames.UPGRADE))) {
                //Adding new handler to the existing pipeline to handle WebSocket Messages
                ctx.pipeline().replace(this, "websocketHandler", defaultWebSocketInboundHandler);
                //Do the Handshake to upgrade connection from HTTP to WebSocket
                handleHandshake(ctx, httpRequest);
            } else {
                log.warn("Incoming request is wrong header!!- {}", ctx.channel().remoteAddress());
                ctx.close();
            }
        } else {
            log.warn("Incoming request is unknown!!!! - {}", ctx.channel().remoteAddress());
            ctx.close();
        }
    }

    public void upgradeComplete(ChannelHandlerContext ctx, HttpRequest httpRequest, String websocketURL) throws Exception {
        log.info(LOG_NET_HEAD, ctx.channel().remoteAddress(), "Handshake Complete!! - " + websocketURL);
    }

    /** Do the handshaking for WebSocket request */
    protected void handleHandshake(ChannelHandlerContext ctx, HttpRequest req) {
        String websocketURL = getWebSocketURL(ctx.pipeline(), req);
       wsFactory = new WebSocketServerHandshakerFactory(websocketURL, null, true);
       webSocketServerHandshaker = wsFactory.newHandshaker(req);
        if (webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            log.error("HandSake Fail!! - UnsupportedVersionResponse - {}", req.uri());
        } else {
            webSocketServerHandshaker.handshake(ctx.channel(), req).addListener(future -> {
                if(future.isSuccess()) {
                    upgradeComplete(ctx, req, websocketURL);
                } else {
                    log.error("HandSake Fail!! - {}", future.cause());
                }
            });
        }
    }

    protected String getWebSocketURL(ChannelPipeline cp, HttpRequest req) {
        String url =  cp.get(SslHandler.class) != null ? "wss" : "ws" + "://" + req.headers().get("Host") + req.uri();
        return url;
    }

    public DefaultHttpInboundHandler(DefaultWebSocketInboundHandler handler) {
        defaultWebSocketInboundHandler = handler;
    }

    protected DefaultWebSocketInboundHandler defaultWebSocketInboundHandler;
    protected WebSocketServerHandshakerFactory wsFactory = null;
    protected WebSocketServerHandshaker webSocketServerHandshaker = null;
}
