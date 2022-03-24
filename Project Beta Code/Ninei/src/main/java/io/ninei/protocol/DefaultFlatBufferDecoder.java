package io.ninei.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public abstract class DefaultFlatBufferDecoder extends DefaultByteBufDecoder {

    @Override
    public void decodeAfter(ByteBuf byteBuf, List<Object> list) throws Exception {
        list.add(byteBuf);
    }
}
