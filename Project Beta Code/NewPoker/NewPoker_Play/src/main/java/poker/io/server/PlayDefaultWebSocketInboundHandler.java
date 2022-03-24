package poker.io.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.ninei.server.Web.DefaultWebSocketInboundHandler;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ChannelHandler.Sharable
public class PlayDefaultWebSocketInboundHandler extends DefaultWebSocketInboundHandler {

    @Override
    protected void binaryFrameRead(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) {
        // NOTE: Next Inbound Handler >> Poker FlatBuffer Decoder
        ctx.fireChannelRead(msg.content());
    }
}
