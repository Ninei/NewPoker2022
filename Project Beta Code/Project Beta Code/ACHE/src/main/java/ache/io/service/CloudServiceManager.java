package ache.io.service;

import ache.ACHEContext;
import ache.io.codec.protocol.PROC_CloudX;
import ache.io.codec.protocol.PROC_UnionTable;
import ache.io.exception.DefinedException;
import ache.io.service.cloudApp.CloudApp;
import ache.io.service.cloudApp.CloudAppRoomService;
import ache.io.service.cloudApp.CloudAppService;
import ache.io.service.viewer.ViewerRoomService;
import ache.io.service.viewer.Viewer;
import ache.io.service.viewer.ViewerService;
import ache.io.tool.MonitoringAgent;
import io.netty.channel.ChannelHandlerContext;
import io.ninei.service.DefaultService;
import io.ninei.service.DefaultUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public final class CloudServiceManager implements ACHEContext, DefaultService {

    public void channelRead(Viewer viewer, PROC_CloudX procCloudX) throws Exception {
        switch (procCloudX.procTableType()) {
            case PROC_UnionTable.PROC_Login:
                RCV_Login(viewer);
                break;
            case PROC_UnionTable.PROC_InitImage:
                RCV_InitImage(viewer);
                break;
            case PROC_UnionTable.PROC_KeyEvent:
                RCV_KeyEvent(viewer, procCloudX);
                break;
            case PROC_UnionTable.PROC_Msg:
                viewer.sendMessageToCloudApp(procCloudX);
                break;
            case PROC_UnionTable.PROC_ImageList:
                throw new DefinedException("PROC_ImageList is Not Acceptable Protocol!!");
            default:
                throw new DefinedException("Unknown Union Table");
        }
    }

    public void RCV_Msg(CloudApp cloudApp, String msg) throws Exception {
        cloudApp.sendMessageToViewer(msg);
    }

    public void RCV_InitImage(Viewer viewer) throws Exception {
        viewer.RCV_InitImage();
    }

    public void RCV_KeyEvent(Viewer viewer, PROC_CloudX procCloudX) throws Exception {
        viewer.RCV_KeyEvent(procCloudX);
    }

    public void RCV_Logout(Viewer viewer) throws Exception {
        viewerService.logout(viewer);
    }
    public void RCV_Logout(CloudApp cloudApp) throws Exception {
        cloudAppService.logout(cloudApp);
    }

    public void RCV_Login(Viewer user) throws Exception {
        viewerService.login(user);
    }
    public void RCV_Login(CloudApp cloudApp) throws Exception {
        cloudAppService.login(cloudApp);
    }

    public void broadcastGlobal(Object obj) throws Exception {
        globalChannelGroupService.broadcastChannelGroup(obj);
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        globalChannelGroupService.enterChannelGroup(ctx.channel());
    }

    public void channelInactive(ChannelHandlerContext ctx, DefaultUser user) throws Exception {
        globalChannelGroupService.exitChannelGroup(ctx.channel());
        if(user instanceof Viewer) RCV_Logout((Viewer) user);
        else RCV_Logout((CloudApp) user);
    }

    @Override
    public void destroy() {
        if(globalChannelGroupService != null) globalChannelGroupService.destroy(); globalChannelGroupService = null;
        if(viewerService != null) viewerService.destroy(); viewerService = null;
        if(cloudAppService != null) cloudAppService.destroy(); cloudAppService = null;
        if(viewerRoomService != null) viewerRoomService.destroy(); viewerRoomService = null;
        if(cloudAppRoomService != null) cloudAppRoomService.destroy(); cloudAppRoomService = null;
    }

    private CloudServiceManager(GlobalChannelGroupService globalChannelService,
                                ViewerRoomService viewerRoomService, ViewerService viewerService,
                                CloudAppRoomService cloudAppRoomService, CloudAppService cloudAppService) {
        this.globalChannelGroupService = globalChannelService;
        this.viewerRoomService = viewerRoomService;
        this.viewerService = viewerService;
        this.cloudAppRoomService = cloudAppRoomService;
        this.cloudAppService = cloudAppService;
    }

    private GlobalChannelGroupService globalChannelGroupService;
    private ViewerRoomService viewerRoomService;
    private ViewerService viewerService;
    private CloudAppRoomService cloudAppRoomService;
    private CloudAppService cloudAppService;
}
