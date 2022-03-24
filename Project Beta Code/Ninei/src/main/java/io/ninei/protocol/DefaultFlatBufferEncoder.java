package io.ninei.protocol;

import com.google.flatbuffers.Table;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.log4j.Log4j2;

import java.util.List;

public abstract class DefaultFlatBufferEncoder extends MessageToMessageEncoder<Table> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Table data, List<Object> out) throws Exception {
        ByteBuf byteBuf = ctx.alloc().directBuffer().writeBytes(data.getByteBuffer());
        encodeAfter(ctx, byteBuf, out);
    }

    protected abstract void encodeAfter(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception;
}
