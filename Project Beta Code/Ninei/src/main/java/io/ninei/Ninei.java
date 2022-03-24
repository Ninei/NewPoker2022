package io.ninei;

import io.ninei.global.DefaultConfig;
import io.ninei.global.DefaultContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@SpringBootApplication
public class Ninei implements DefaultContext {

    @GetMapping("/hello")
    public String hello() {
        return "Hello WebFlux!!!";
    }

    @GetMapping("/hello/mono")
    public Mono helloMono() {
        return Mono.just("Hello Mono!!!");
    }

    @GetMapping("/hello/flux")
    public Flux helloFlux() {
        return Flux.just("Hello Flux!!!", "Hello Reactor 3!!!", "Hello Reactive Streams!!!");
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Ninei.class, args);

        try {

            DefaultConfig.initConfigure(context);
            String version = DefaultConfig.getBuildVersion();

//            log.warn("\n" +
            System.out.println("\n" +
                "                              ___   \n"+
                "  ___    _             _____ |   |  \n" +
                " |   \\  | | _  __   _ |  ___| | |     \n" +
                " | |\\ \\ | || ||  \\ | || |__   | |   \n" +
                " | | \\ \\| || || \\ \\| ||  __|  | |   \n" +
                " |_|  \\___||_||_|\\___|| |___  | |  \n" +
                " =====================|_____||___|================\n"+
                ":: HSF-RealTime Game Server :: ("+version+")\n");
        } catch (Exception e) {
            log.error("Global Main Exception Catch", e);
        }
    }
}
