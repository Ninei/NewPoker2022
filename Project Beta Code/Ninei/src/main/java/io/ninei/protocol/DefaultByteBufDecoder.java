package io.ninei.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public abstract class DefaultByteBufDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) return;
        decodeAfter(byteBuf.readBytes(byteBuf.readableBytes()), list);
    }

    public abstract void decodeAfter(ByteBuf buffer, List<Object> list) throws Exception;
}
