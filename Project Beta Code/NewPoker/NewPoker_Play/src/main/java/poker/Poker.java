package poker;

import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RestController;
import poker.io.config.PokerConfig;

@Log4j2
@RestController
@SpringBootApplication
@MapperScan(basePackages = "poker.io.repository")
public class Poker implements PokerContext {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Poker.class, args);

        try {
            PokerConfig.initConfigure(context);
            String version = PokerConfig.getBuildVersion();

            log.warn("\n" +
                "                              ___   \n"+
                "  ___    _             _____ |   |  \n" +
                " |   \\  | | _  __   _ |  ___| | |     \n" +
                " | |\\ \\ | || ||  \\ | || |__   | |   \n" +
                " | | \\ \\| || || \\ \\| ||  __|  | |   \n" +
                " |_|  \\___||_||_|\\___|| |___  | |  \n" +
                " =====================|_____||___|================\n"+
                ":: HSF-RealTime Game Server :: ("+version+")\n");

//            int a = 1/0; // TODO: 에러 체크를 위해...

            PokerServerManager pokerServerManager = context.getBean(PokerServerManager.class);
            pokerServerManager.addServer();
            pokerServerManager.startGroup();
            pokerServerManager.stopGroup();
        } catch (Exception e) {
            log.error("Main Global Exception!! - {}", e);
        }
    }
}
