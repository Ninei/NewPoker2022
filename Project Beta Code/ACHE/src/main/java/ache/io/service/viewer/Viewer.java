package ache.io.service.viewer;

import ache.ACHEContext;
import ache.io.browser.ScreenCast;
import ache.io.browser.XViewPoint;
import ache.io.codec.protocol.HashKeyConvertor;
import ache.io.codec.protocol.PROC_CloudX;
import ache.io.codec.protocol.PROC_KeyEvent;
import ache.io.codec.protocol.PROC_Msg;
import ache.io.server.socket.ViewerHandler;
import ache.io.service.cloudApp.CloudApp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.ninei.service.DefaultUser;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.awt.*;

import static java.lang.Integer.parseInt;

@Getter
@Log4j2
public class Viewer extends DefaultUser implements ACHEContext {

    public void startScreencast() throws Exception {
        screenCast.doScreenCast();
        log.info("Page ScreenCast!! - {}", getTraceInfo());
        // FIXME: 첫화면 STB 테스트하고 적용 여부 결정
//        if(cloudApp != null && screenCast != null) {
//            screenCast.doScreenCast();
//            log.info("Page ScreenCast!! - {} X {}", getTraceInfo(), cloudApp.getTraceInfo());
//        }
    }

    public void sendMessageToCloudApp(PROC_CloudX procCloudX) throws Exception {
        procCloudX.procTable(proc_Msg);
        if(cloudApp != null) {
            dispatcher.SEND_MessageToCloudApp(cloudApp, proc_Msg.msg());
        }
    }

    public void RCV_InitImage() throws Exception {
        // 클라이언트 >> 클라우드 화면 요청 시작 시그널, 알티로직에서 사용하지 않음..
        dispatcher.SEND_InitImage(new byte[]{0});
        screenCast.doScreenCast(); // 캡쳐 시작
    }

    public void RCV_KeyEvent(PROC_CloudX procCloudX) throws Exception {
        if(screenCast == null) { // 바인딩 전에 키호출이 중복해서 들어오는 경우 무시
            log.info("Screencast is NULL!!");
            return;
        }
        procCloudX.procTable(proc_KeyEvent);
        if(proc_KeyEvent.encCode() != null && HashKeyConvertor.getKeycode(proc_KeyEvent.encCode()) != NONE) {
            screenCast.keyEventDispatcher(HashKeyConvertor.getKeycode(proc_KeyEvent.encCode()));
        } else {
            screenCast.keyEventDispatcher(proc_KeyEvent.code());
        }
    }

    public void SEND_ImageList(XViewPoint[] regionList, byte[][] bufferList, double timeStamp) throws Exception {
        dispatcher.SEND_ImageList(regionList, bufferList, timeStamp);
    }

    public void bind(CloudApp target) {
        cloudApp = target;
        cloudApp.bind(this);
    }

    public void pageOpened(ScreenCast screenCast) {
        this.screenCast = screenCast;
        log.info("Page Opening Complete!! - {}", getTraceInfo());
    }

    public void closePage() throws Exception {
        logout();
        if(screenCast != null) {
            screenCast.closePage();
        }
        this.screenCast = null;
        log.info("Page Closed!! >> {}", getTraceInfo());
    }

    public void setMetadata(String data) throws Exception {
        QueryURL = data.substring(data.lastIndexOf("&url=")+5);
        String[] pairs = QueryURL.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if(pair.substring(0,idx).equalsIgnoreCase("stboxId")) {
                this.SAID = pair.substring(idx + 1);
            } else if(pair.substring(0,idx).equalsIgnoreCase("platform")) {
                this.Platform = pair.substring(idx + 1);
            } else {
                if (pair.substring(0, idx).equalsIgnoreCase("cropRowCount")) {
                    this.CropRowCount = parseInt(pair.substring(idx + 1));
                } else if (pair.substring(0, idx).equalsIgnoreCase("cropColCount")) {
                    this.CropColCount = parseInt(pair.substring(idx + 1));
                }
            }
        }
        log.info("stboxId: {}, platform: {}, cropRowCount: {}, cropColCount: {}", SAID, Platform, CropRowCount, CropColCount);
    }

    @Override
    public void login() throws Exception {
        login(PID, getDisplayName(), getUuid());  // PID = Uuid, login pid = displayId
        setDisplayName(new String[]{"SAID", "PID"}, new String[]{getSAID(), getPID()});
    }

    @Override
    public void dispose() throws Exception {
        if(dispatcher != null) dispatcher.destroy(); dispatcher = null;
        if(proc_KeyEvent != null) proc_KeyEvent.__reset(); proc_KeyEvent = null;
        if(proc_Msg != null) proc_Msg.__reset(); proc_Msg = null;
        PID = null; SAID = null; Platform = null; QueryURL = null;
        screenCast = null;
        cloudApp = null;
        viewerHandler = null;
    }

    public void fireUserException(Throwable e) {
        try {
            viewerHandler.exceptionCaught(getUuContext(), e);
        } catch (Exception exception) {
            log.error("Viewer - fireChannelException()", exception);
        }
    }

    public Viewer(ChannelHandlerContext context, ViewerHandler viewerHandler) {
        super(context);
        this.PID = this.getUuid();
        this.dispatcher = new ViewerDispatcher(this);
        this.viewerHandler = viewerHandler;
    }

    private PROC_KeyEvent proc_KeyEvent = new PROC_KeyEvent();
    private PROC_Msg proc_Msg = new PROC_Msg();

    private String PID, SAID, Platform, QueryURL;
    private int CropRowCount=CROP_DEFAULT_ROW_COUNT, CropColCount=CROP_DEFAULT_COL_COUNT;

    private ChannelInboundHandler viewerHandler;
    private ViewerDispatcher dispatcher;
    private ScreenCast screenCast;
    private CloudApp cloudApp;
}
