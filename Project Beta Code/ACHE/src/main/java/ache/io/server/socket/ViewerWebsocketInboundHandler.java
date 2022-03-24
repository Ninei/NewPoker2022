package ache.io.server.socket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import io.ninei.server.Web.DefaultWebSocketInboundHandler;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ViewerWebsocketInboundHandler extends DefaultWebSocketInboundHandler {

    @Override
    protected void binaryFrameRead(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
        ctx.fireChannelRead(msg);
    }

    @Override
    protected void textFrameRead(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        try {
            // 단축키로 브라우저 새로고침 하는 경우 등
            log.info("Receive TextWebSocketFrame: {}", msg.text());
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void fireChannelMetadata(Object key, Object value) throws Exception {
        viewerHandler.fireChannelMetadata(key, value);
    }

    public ViewerWebsocketInboundHandler(ViewerHandler viewerHandler) {
        this.viewerHandler = viewerHandler;
    }

    private ViewerHandler viewerHandler;
}