package ache.io.service.cloudApp;

import ache.ACHEContext;
import ache.io.codec.WebsocketProtocolFactory;
import ache.io.service.viewer.Viewer;
import io.netty.channel.ChannelFuture;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CloudAppDispatcher implements ACHEContext {

    public void SEND_MessageToViewer(Viewer viewer, String msg) throws Exception {
//        log.debug(NET_SEND_MSG_TO_VIEWER, msg, selfUser.getTraceInfo(), viewer.getTraceInfo());
        viewer.sendMessage(pf.Proc_Msg(msg));
    }

    public void destroy() throws Exception {
        if(pf != null) pf.destroy(); pf = null;
        selfUser = null;
    }

    public CloudAppDispatcher(CloudApp user) {
        selfUser = user;
    }

    protected CloudApp selfUser;
    protected WebsocketProtocolFactory pf = new WebsocketProtocolFactory();
}