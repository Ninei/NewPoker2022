package ache.io.server.socket;

import ache.ACHEContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.ninei.server.Web.DefaultHttpInboundHandler;
import io.ninei.server.Web.DefaultWebSocketInboundHandler;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class HttpInboundHandler extends DefaultHttpInboundHandler implements ACHEContext {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            log.info(LOG_NET_HEAD, ctx.channel().remoteAddress(), "Web Request Received!!");
            HttpHeaders headers = httpRequest.headers();
            if ("Upgrade".equalsIgnoreCase(headers.get(HttpHeaderNames.CONNECTION)) &&
                "WebSocket".equalsIgnoreCase(headers.get(HttpHeaderNames.UPGRADE))) {
                //Adding new handler to the existing pipeline to handle WebSocket Messages
                ctx.pipeline().replace(this, "websocketHandler", defaultWebSocketInboundHandler);
                //Do the Handshake to upgrade connection from HTTP to WebSocket
                handleHandshake(ctx, httpRequest);
                defaultWebSocketInboundHandler.fireChannelMetadata(HTTP_REQUEST_NAME_URL, httpRequest.uri());
            } else {
                log.warn("Incoming request is wrong header!!- {}", ctx.channel().remoteAddress());
                ctx.close();
            }
        } else {
            log.warn("Incoming request is unknown!!!! - {}", ctx.channel().remoteAddress());
            ctx.close();
        }
    }

    public HttpInboundHandler(DefaultWebSocketInboundHandler handler) {
        super(handler);
    }

}
