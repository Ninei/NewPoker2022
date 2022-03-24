package ache;

import ache.io.browser.ChromiumLauncher;
import ache.io.config.ACHEConfig;
import ache.io.exception.ExceptionHandler;
import ache.io.tool.MonitoringAgent;
import io.netty.util.ResourceLeakDetector;
import io.sentry.SentryLevel;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet4Address;

@Log4j2
@EnableScheduling
@RestController
@SpringBootApplication
//@MapperScan(basePackages = "ache.io.repository")
public class ACHE {

    public static void main(String[] args) {

        try {
            ConfigurableApplicationContext context = SpringApplication.run(ACHE.class, args);
            ACHEConfig.initConfigure(context);
            String version = ACHEConfig.getBuildVersion();

            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
            String host = Inet4Address.getLocalHost().getHostName();

            System.out.println("HostName: " + host);
            System.out.println("\n" +
                "                              ___   \n" +
                "  ___    _             _____ |   |  \n" +
                " |   \\  | | _  __   _ |  ___| | |     \n" +
                " | |\\ \\ | || ||  \\ | || |__   | |   \n" +
                " | | \\ \\| || || \\ \\| ||  __|  | |   \n" +
                " |_|  \\___||_||_|\\___|| |___  | |  \n" +
                " =====================|_____||___|================\n" +
                ":: HSF-RealTime Game Server :: (" + version + ")\n");

            MonitoringAgent monitoringAgent = context.getBean(MonitoringAgent.class);
            monitoringAgent.start();

            ChromiumLauncher chromiumLauncher = context.getBean(ChromiumLauncher.class);
            chromiumLauncher.createBrowserPool();

            ACHEServerManager acheServerManager = context.getBean(ACHEServerManager.class);
            acheServerManager.addServer();
            acheServerManager.startGroup();
            acheServerManager.stopGroup();

            monitoringAgent.destroy();
            chromiumLauncher.destroy();

            ExceptionHandler.fireMsg(host + ">> Server is Stopped!!", SentryLevel.INFO);

        } catch (Exception e) {
            log.error("Main Global Exception!! - ", e);
        }
    }
}


