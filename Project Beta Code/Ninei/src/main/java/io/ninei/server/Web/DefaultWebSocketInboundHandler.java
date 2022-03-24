package io.ninei.server.Web;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.ReferenceCountUtil;
import io.ninei.global.DefaultContext;
import lombok.extern.log4j.Log4j2;

import java.net.SocketAddress;

@Log4j2
public abstract class DefaultWebSocketInboundHandler extends ChannelInboundHandlerAdapter implements DefaultContext {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            if (msg instanceof BinaryWebSocketFrame) {
//                log.debug(LOG_HEAD_FORMAT, ctx.channel().remoteAddress(), "BinaryWebSocketFrame Received : "+ ((BinaryWebSocketFrame) msg).content());
                binaryFrameRead(ctx, (BinaryWebSocketFrame)msg);
            } else if (msg instanceof TextWebSocketFrame) {
//                log.debug(LOG_NET_HEAD, ctx.channel().remoteAddress(), "TextWebSocketFrame Received : " + ((TextWebSocketFrame) msg).text());
                textFrameRead(ctx, (TextWebSocketFrame) msg);
            } else if (msg instanceof PingWebSocketFrame) {
//                log.debug(LOG_NET_HEAD, ctx.channel().remoteAddress(), "PingWebSocketFrame Received : "+ ((PingWebSocketFrame) msg).content());
                pingFrameRead(ctx, (PingWebSocketFrame)msg);
            } else if (msg instanceof PongWebSocketFrame) {
//                log.debug(LOG_NET_HEAD, ctx.channel().remoteAddress(), "PongWebSocketFrame Received : " + ((PongWebSocketFrame) msg).content());
                pongFrameRead(ctx, (PongWebSocketFrame) msg);
            } else if (msg instanceof CloseWebSocketFrame) { // Refresh, Close 등의 이벤트 발생시 수신
                closeFrameRead(ctx, (CloseWebSocketFrame)msg);
            } else {
                unsupportedFrame(ctx, msg);
            }
        } else {
            wrongWebsocketFrame(ctx, msg);
        }
    }

    protected void binaryFrameRead(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
        log.info(LOG_NET_HEAD_EXT, ctx.channel().remoteAddress(), "binaryFrameRead: ", msg.content());
        ReferenceCountUtil.release(msg);
    }

    protected void textFrameRead(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info(LOG_NET_HEAD_EXT, ctx.channel().remoteAddress(), "textFrameRead Received: ", msg.text());
        ReferenceCountUtil.release(msg);
    }

    protected void pingFrameRead(ChannelHandlerContext ctx, PingWebSocketFrame msg) {
        PongWebSocketFrame pongWebSocketFrame = new PongWebSocketFrame(msg.content());
        ReferenceCountUtil.release(msg);
        ctx.writeAndFlush(pongWebSocketFrame); // Write in Not ReferenceCountUtil.release()
    }

    protected void pongFrameRead(ChannelHandlerContext ctx, PongWebSocketFrame msg) {
        ReferenceCountUtil.release(msg);
        ctx.writeAndFlush(msg);
    }

    protected void closeFrameRead(ChannelHandlerContext ctx, CloseWebSocketFrame msg) {
        log.info(LOG_NET_HEAD, ctx.channel().remoteAddress(), "CloseWebSocketFrame Received");
        closeWebsocketFrame(ctx, msg);
    }

    protected void unsupportedFrame(ChannelHandlerContext ctx, Object msg) {
        log.warn(LOG_NET_HEAD, ctx.channel().remoteAddress(),"Unsupported WebSocketFrame");
        closeWebsocketFrame(ctx, msg);
    }

    protected void wrongWebsocketFrame(ChannelHandlerContext ctx, Object msg) {
        log.warn(LOG_NET_HEAD, ctx.channel().remoteAddress(),"Wrong WebsocketFrame!!");
        closeWebsocketFrame(ctx, msg);
    }

    private void closeWebsocketFrame(ChannelHandlerContext ctx, Object msg) {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        ReferenceCountUtil.release(msg);
        ctx.channel().close();
        log.info(LOG_NET_HEAD, socketAddress,"Connection Closed!!");
    }

    public void fireChannelMetadata(Object key, Object value) throws Exception {
//        log.info("{} , {}", key, value);
    }
}
