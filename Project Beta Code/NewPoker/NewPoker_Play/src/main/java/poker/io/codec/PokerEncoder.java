package poker.io.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.ninei.protocol.DefaultFlatBufferEncoder;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class PokerEncoder extends DefaultFlatBufferEncoder {

    @Override
    protected void encodeAfter(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(byteBuf);
        log.debug("{} ==> {}", ctx.channel().localAddress(), "ACHEEncoder >> encodeAfter: " + byteBuf);
        out.add(binaryWebSocketFrame);
    }
}
