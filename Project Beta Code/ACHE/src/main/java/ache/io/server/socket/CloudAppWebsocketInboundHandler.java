package ache.io.server.socket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.ninei.server.Web.DefaultWebSocketInboundHandler;

public class CloudAppWebsocketInboundHandler extends DefaultWebSocketInboundHandler {

    @Override
    protected void binaryFrameRead(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) {
        ctx.fireChannelRead(msg);
    }

    @Override
    protected void textFrameRead(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        ctx.fireChannelRead(msg);
    }

    @Override
    public void fireChannelMetadata(Object key, Object value) throws Exception {
        cloudAppHandler.fireChannelMetadata(key, value);
    }

    public CloudAppWebsocketInboundHandler(CloudAppHandler cloudAppHandler) {
        this.cloudAppHandler = cloudAppHandler;
    }

    private CloudAppHandler cloudAppHandler;
}
