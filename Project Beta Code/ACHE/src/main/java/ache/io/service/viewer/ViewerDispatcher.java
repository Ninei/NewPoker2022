package ache.io.service.viewer;

import ache.ACHEContext;
import ache.io.browser.XViewPoint;
import ache.io.codec.WebsocketProtocolFactory;
import ache.io.service.cloudApp.CloudApp;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.log4j.Log4j2;

import java.awt.*;

@Log4j2
public class ViewerDispatcher implements ACHEContext {

    public void SEND_MessageToCloudApp(CloudApp cloudApp, String msg) throws Exception {
//        log.debug(NET_SEND_MSG_TO_CLOUD, msg, selfUser.getTraceInfo(), cloudApp.getTraceInfo());
        cloudApp.sendMessage(pf.createTextWebSocketFrame(msg));
    }

    public void SEND_ImageList(XViewPoint[] regionList, byte[][] bufferList, double timeStamp) throws Exception {
//        log.debug(NET_SEND_IMAGE_LIST, regionList.length, selfUser.getTraceInfo());
        sendMessage(pf.Proc_ImageList(regionList, bufferList, timeStamp));
    }

    public void SEND_InitImage(byte[] bufferList) throws Exception {
//        log.debug(NET_SEND_INIT_IMAGE, selfUser.getTraceInfo());
        sendMessage(pf.Proc_InitImage(bufferList));
    }

    public void SEND_Login() throws Exception {
        log.info(NET_SEND_LOGIN, selfUser.getTraceInfo());
        sendMessage(pf.Proc_Login(selfUser));
    }

    private void sendMessage(BinaryWebSocketFrame binaryWebSocketFrame) throws Exception {
        selfUser.sendMessage(binaryWebSocketFrame);
    }

    public void destroy() throws Exception {
        if(pf != null) pf.destroy(); pf = null;
        selfUser = null;
    }

    public ViewerDispatcher(Viewer user) {
        selfUser = user;
    }

    protected Viewer selfUser;
    protected WebsocketProtocolFactory pf = new WebsocketProtocolFactory();
}
