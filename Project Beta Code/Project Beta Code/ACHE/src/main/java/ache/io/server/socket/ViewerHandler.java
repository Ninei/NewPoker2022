package ache.io.server.socket;

import ache.ACHEContext;
import ache.io.codec.protocol.PROC_CloudX;
import ache.io.codec.protocol.PROC_UnionTable;
import ache.io.service.CloudServiceManager;
import ache.io.service.viewer.Viewer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.net.Inet4Address;
import java.net.SocketAddress;
import java.net.URLDecoder;

@Log4j2
public class ViewerHandler extends ChannelInboundHandlerAdapter implements ACHEContext {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame)msg;

            PROC_CloudX procCloudX = new PROC_CloudX();
            PROC_CloudX.getRootAsPROC_CloudX(binaryWebSocketFrame.content().nioBuffer(), procCloudX);

            cloudServiceManager.channelRead(user, procCloudX);
        } catch (Exception e) {
            throw e;
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.pipeline().get(SslHandler.class) != null) {
            ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                (GenericFutureListener<Future<Channel>>) future -> {
                    log.debug("Your session is protected by " +
                            ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
                            " cipher suite.\n");
                }
            );
        }
        user = new Viewer(ctx, this);
        cloudServiceManager.channelActive(ctx);
        log.info(LOG_NET_HEAD, ctx.channel().remoteAddress(), "ViewerHandler ChannelActive!!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = null;
        try {
             socketAddress = ctx.channel().remoteAddress();
        } catch (Exception ignore) {}
        String pid = user.getPID();
        cloudServiceManager.channelInactive(ctx, user);
        cloudServiceManager = null;
        user = null;
        log.info(LOG_NET_HEAD_EXT, socketAddress == null ? "Already Reset by peer" : socketAddress, "ViewerHandler ChannelInActive!! - PID:", pid);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                log.warn(IdleState.READER_IDLE);
                ctx.close(); // Call Channel Inactive
            }

            if (e.state() == IdleState.WRITER_IDLE) {
                ctx.channel().writeAndFlush(new PingWebSocketFrame());
                log.warn(IdleState.WRITER_IDLE);
                // 일반적인 상황에 클라우드 특성상 서비스앱과 뷰어앱이 다음과 같은 형태로 Health 체크 진행
                // PROC_Msg Info: {"cmd":"Health","type":"pong","trId":1638855132731}
                // 특정시간 이상 health 체크가 원활하지 못하는 경우, READER_IDLE 이벤트 발생하면 접속 종료 처리
            }

            if (e.state() == IdleState.ALL_IDLE) {
                log.warn("Not Scheduled!!, IdleTime is ZERO");
            }
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String errMsg = "exceptionCaught >> Target User: " + (user == null ? "NULL" : user.getTraceInfo());
        log.error(errMsg, cause);
        ctx.close(); // Call Channel Inactive
    }

    public void fireChannelMetadata(Object key, Object value) throws Exception {
        user.setMetadata(URLDecoder.decode(value.toString(), UTF8));
        cloudServiceManager.RCV_Login(user);
    }

    public ViewerHandler(CloudServiceManager cloudServiceManager) {
        super();
        this.cloudServiceManager = cloudServiceManager;
    }

    private Viewer user;
    private CloudServiceManager cloudServiceManager; // Singleton
}
