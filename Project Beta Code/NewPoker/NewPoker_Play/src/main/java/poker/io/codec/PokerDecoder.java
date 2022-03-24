package poker.io.codec;

import io.netty.buffer.ByteBuf;
import io.ninei.pool.DefaultObjectPool;
import io.ninei.protocol.DefaultByteBufDecoder;
import poker.io.codec.protocol.PROC_Poker;
import poker.io.service.play.PlayContext;

import java.util.List;

public class PokerDecoder extends DefaultByteBufDecoder {

    @Override
    public void decodeAfter(ByteBuf buffer, List<Object> list) throws Exception {
        PROC_Poker procPoker = null;
        try {
            procPoker = protocolPoker.getObject();
            list.add(PROC_Poker.getRootAsPROC_Poker(buffer.nioBuffer(), procPoker));
        } finally {
            buffer.release();
            protocolPoker.releaseObject(procPoker);
        }
    }

    private Protocol_Poker protocolPoker = new Protocol_Poker();
    private class Protocol_Poker extends DefaultObjectPool<PROC_Poker> {

        @Override
        protected void cleanUp(PROC_Poker procPoker) {
            if(procPoker != null && procPoker.getByteBuffer() != null) procPoker.getByteBuffer().clear();
        }

        public Protocol_Poker() {
            super(PROC_Poker.class, PlayContext.POOLING_NORMAL_EXP_TIME);
        }
    }
}
