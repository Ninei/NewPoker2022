package ache.io.service.cloudApp;

import ache.ACHEContext;
import ache.io.browser.ChromiumLauncher;
import ache.io.service.viewer.Viewer;
import ache.io.service.viewer.ViewerRoomService;
import io.ninei.service.DefaultRoomService;
import io.ninei.service.DefaultUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Service
public class CloudAppService extends DefaultUserService<CloudApp> implements ACHEContext {

    @Override
    public void login(CloudApp cloudApp) throws Exception {
        cloudApp.login();
        enterRoom(cloudApp);
        log.info("Login OK!! - {}", cloudApp.getTraceInfo());
    }

    @Override
    public void logout(CloudApp cloudApp) throws Exception {
        exitRoom(cloudApp);
        cloudApp.logout();
        log.info("Logout OK!! - {}", cloudApp.getTraceInfo());
        cloudApp.destroy();
    }

    @Override
    protected void dispose() {

    }

    private CloudAppService(CloudAppRoomService roomService) {
        super(roomService);
    }
}
