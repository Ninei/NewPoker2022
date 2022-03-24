package ache.io.service.viewer;

import ache.ACHEContext;
import ache.io.browser.ChromiumBrowser;
import ache.io.browser.ChromiumLauncher;
import io.ninei.service.DefaultUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public final class ViewerService extends DefaultUserService<Viewer> implements ACHEContext {

//    public class CallableImpl implements Callable<ScreenCast> {
//        public ScreenCast call() throws Exception {
//            // 작업 처리
//            return result;
//        }
//    }

    @Override
    public void login(Viewer viewer) throws Exception {
        viewer.login();
        enterRoom(viewer);
        log.info("Login OK!! - {}", viewer.getTraceInfo());
        ChromiumBrowser matchingBrowser = chromiumLauncher.getMatchingBrowser();

        Runnable r = () -> {
            matchingBrowser.openPage(viewer, (screenCast) -> {
                viewer.pageOpened(screenCast);
            });
        };
//        executor.submit(r);

        ThreadPoolExecutor executor = taskExecutor;
        log.info("prevTaskCount: " + executor.getActiveCount());
//        System.err.println(executor.getActiveCount());
        if(executor.getActiveCount() < 100) {
            taskExecutor.submit(() -> {
                matchingBrowser.openPage(viewer, (screenCast) -> {
                    viewer.pageOpened(screenCast);
                });
            }).get(30, TimeUnit.SECONDS);

            log.info("afterTaskCount: " + executor.getTaskCount());
            log.info("getCompletedTaskCount: " + executor.getCompletedTaskCount());
        } else {
            log.error("Thread Active Count is Over - {}, {}", Thread.currentThread().getThreadGroup().activeCount(), viewer.getTraceInfo());
            throw new Exception("Thread Active Count is Over - "+ Thread.currentThread().getThreadGroup().activeCount());
        }
    }

    @Override
    public void logout(Viewer viewer) throws Exception {
        try {
            viewer.closePage();
        } catch (Exception e) {
            log.error(e);
        } finally {
            exitRoom(viewer);
            viewer.logout();
            log.info("Logout OK!! - {}", viewer.getTraceInfo());
            viewer.destroy();
        }
    }

    @Override
    protected void dispose() {
        chromiumLauncher = null;
    }

    private ViewerService(ViewerRoomService roomService, ChromiumLauncher chromiumLauncher) {
        super(roomService);
        this.chromiumLauncher = chromiumLauncher;
    }

    private ChromiumLauncher chromiumLauncher;
    private ThreadPoolExecutor taskExecutor =  (ThreadPoolExecutor) Executors.newFixedThreadPool(400);
}
