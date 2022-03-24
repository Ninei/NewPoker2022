package ache.io.codec;

import ache.ACHEContext;
import ache.io.browser.XViewPoint;
import ache.io.codec.protocol.*;
import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.ninei.service.DefaultUser;
import org.springframework.stereotype.Component;

@Component
public class WebsocketProtocolFactory implements ACHEContext {

    public BinaryWebSocketFrame Proc_ImageList(XViewPoint[] regionList, byte[][] bufferList, double timeStamp) throws Exception {
        FlatBufferBuilder fbb = new FlatBufferBuilder();
        int[] listOffSet = new int[regionList.length];
        for(int i=0; i<regionList.length; i++) {
            int imgBufOffSet = PROC_Image.createBufferVector(fbb, bufferList[i]);
            listOffSet[i] = PROC_Image.createPROC_Image(fbb, regionList[i].sx, regionList[i].sy,
                regionList[i].width, regionList[i].height, imgBufOffSet, fbb.createString("empty"));
        }
        int imgListOffSet = PROC_ImageList.createImgListVector(fbb, listOffSet);
        int procOffSet = PROC_ImageList.createPROC_ImageList(fbb, imgListOffSet, timeStamp);
        return createRootProc(fbb, PROC_UnionTable.PROC_ImageList, procOffSet);
    }

    public BinaryWebSocketFrame Proc_InitImage(byte[] buffer) throws Exception {
        FlatBufferBuilder fbb = new FlatBufferBuilder();
        int imgBufOffset = PROC_Image.createBufferVector(fbb, buffer);
        int imgOffset = PROC_Image.createPROC_Image(fbb, 0, 0, 1280, 720, imgBufOffset, fbb.createString("empty"));
        int initImgOffset= PROC_InitImage.createPROC_InitImage(fbb, imgOffset);
        return createRootProc(fbb, PROC_UnionTable.PROC_InitImage, initImgOffset);
    }

    public BinaryWebSocketFrame Proc_Msg(String msg) throws Exception {
        FlatBufferBuilder fbb = new FlatBufferBuilder();
        int msgOffset = fbb.createString(msg);
        int endMsgOffset = PROC_Msg.createPROC_Msg(fbb, msgOffset);
        return createRootProc(fbb, PROC_UnionTable.PROC_Msg, endMsgOffset);
    }

    public BinaryWebSocketFrame Proc_Login(DefaultUser user) throws Exception {
        FlatBufferBuilder fbb = new FlatBufferBuilder();
        fbb.clear();
        int userOffset = createUser(fbb, user);
        PROC_Login.startPROC_Login(fbb);
        PROC_Login.addUser(fbb, userOffset);
        int endLoginOffset = PROC_Login.endPROC_Login(fbb);
        return createRootProc(fbb, PROC_UnionTable.PROC_Login, endLoginOffset);
    }

    private int createUser(FlatBufferBuilder fbb, DefaultUser user) throws Exception {
        int idOffSet = fbb.createString(user.getUuid());
        int nameOffset = fbb.createString(user.getDisplayName());
        int saidOffset = fbb.createString("");
        return PROC_User.createPROC_User(fbb, idOffSet, nameOffset, saidOffset);
    }

    private BinaryWebSocketFrame createRootProc(FlatBufferBuilder fbb, byte tableType, int offset) throws Exception {
        PROC_CloudX.startPROC_CloudX(fbb);
        PROC_CloudX.addProcTableType(fbb, tableType);
        PROC_CloudX.addProcTable(fbb, offset);
        PROC_CloudX.finishPROC_CloudXBuffer(fbb, PROC_CloudX.endPROC_CloudX(fbb));
        byte[] sizedFbb = fbb.sizedByteArray();
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(sizedFbb.length);
        return new BinaryWebSocketFrame(byteBuf.writeBytes(sizedFbb));
    }

    public TextWebSocketFrame createTextWebSocketFrame(String msg) throws Exception {
        return new TextWebSocketFrame(msg);
    }

    public void destroy() throws Exception {}
}
