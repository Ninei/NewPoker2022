package ache.io.server.socket;

import ache.ACHEContext;
import ache.io.exception.ExceptionHandler;
import ache.io.service.CloudServiceManager;
import ache.io.service.cloudApp.CloudApp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.log4j.Log4j2;

import java.net.Inet4Address;
import java.net.SocketAddress;
import java.net.URLDecoder;

@Log4j2
public class CloudAppHandler extends ChannelInboundHandlerAdapter implements ACHEContext {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) msg;
//            log.debug(NET_RECV_CLOUD_APP_READ, textWebSocketFrame.text(),  user.getTraceInfo());
            cloudServiceManager.RCV_Msg(user, textWebSocketFrame.text());
        } catch (Exception e) {
            throw e;
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        user = new CloudApp(ctx, this);
        cloudServiceManager.channelActive(ctx);
        log.info(LOG_NET_HEAD, ctx.channel().remoteAddress(), "CloudAppHandler ChannelActive!!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        String pid = user.getPID();
        cloudServiceManager.channelInactive(ctx, user);
        cloudServiceManager = null;
        user = null;
        log.info(LOG_NET_HEAD_EXT, socketAddress, "CloudAppHandler ChannelInActive!! - PID:", pid);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String errMsg = "FromEngine: "+  Inet4Address.getLocalHost().getHostAddress() + " >> Target User: " + (user == null ? "NULL" : user.getTraceInfo());
        log.error(errMsg, cause);
        ctx.close();
    }

    public void fireChannelMetadata(Object key, Object value) throws Exception {
        user.setMetadata(URLDecoder.decode(value.toString(), UTF8));
        cloudServiceManager.RCV_Login(user);
    }

    public CloudAppHandler(CloudServiceManager cloudServiceManager) {
        super();
        this.cloudServiceManager = cloudServiceManager;
    }

    private CloudApp user;
    private CloudServiceManager cloudServiceManager; // Singleton
}
