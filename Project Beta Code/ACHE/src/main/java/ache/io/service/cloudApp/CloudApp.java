package ache.io.service.cloudApp;

import ache.io.server.socket.CloudAppHandler;
import ache.io.service.viewer.Viewer;
import io.netty.channel.ChannelHandlerContext;
import io.ninei.service.DefaultUser;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
public class CloudApp extends DefaultUser {

    public void sendMessageToViewer(String msg) throws Exception {
        if(viewer != null) dispatcher.SEND_MessageToViewer(viewer, msg);
    }

    public void bind(Viewer target) {
        viewer = target;
    }

    @Override
    public void login() throws Exception {
        login(PID, getDisplayName(), getUuid()); // displayId = pid
        setDisplayName(new String[]{"PID"}, new String[]{PID});
    }

    @Override
    public void dispose() throws Exception {
        PID = null; viewer = null;
        if(dispatcher != null) dispatcher.destroy(); dispatcher = null;
        cloudAppHandler = null;
    }

    public String getPID() { return PID; }

    public void fireUserException(Throwable e) {
        try {
            this.cloudAppHandler.exceptionCaught(getUuContext(), e);
        } catch (Exception exception) {
            log.error(exception);
        }
    }

    public void setMetadata(String data) throws Exception {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = data;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(pair.substring(0, idx), pair.substring(idx + 1));
            if(pair.substring(0, idx).equalsIgnoreCase("pid")) {
                this.PID = pair.substring(idx + 1); // viewer Uuid
            }
        }
    }

    public CloudApp(ChannelHandlerContext context, CloudAppHandler cloudAppHandler) {
        super(context);
        this.cloudAppHandler = cloudAppHandler;
        this.dispatcher = new CloudAppDispatcher(this);
    }

    private String PID;
    private Viewer viewer;
    private CloudAppHandler cloudAppHandler;
    private CloudAppDispatcher dispatcher;
}
