package poker.io.server;

import io.ninei.server.Web.DefaultWebServer;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import poker.io.repository.NongBot;
import poker.io.controller.NongBotController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Log4j2
@RestController
public class WebServer extends DefaultWebServer {
    @GetMapping("/")
    public String index() { return "Hello"; }

    @GetMapping("/hello")
    public String hello() { return "Hello WebFlux!!!"; }

    @GetMapping("/hello/mono")
    public Mono helloMono() {
        return Mono.just("Hello Mono!!!");
    }

    @GetMapping("/hello/flux")
    public Flux helloFlux() {
        return Flux.just("Hello Flux!!!", "Hello Reactor 3!!!", "Hello Reactive Streams!!!");
    }

//    @Secured("ROLE_ANONYMOUS")
    @PostMapping("/login")
    public String registerNongbot(@RequestHeader MultiValueMap<String, String> headers) {
            headers.forEach((key, value) -> {
                log.info(String.format(
                    "Header '%s' = %s", key, value.stream().collect(Collectors.joining("|"))));
            });

//            return new ResponseEntity<String>(
//                String.format("Listed %d headers", headers.size()), HttpStatus.OK);
//
        this.nongBotController.registerNongBot(new NongBot("TEST_" + System.currentTimeMillis(), "TEST NAME"));
        return "Register Nongbot";
    }

    protected WebServer(NongBotController nongBotController) {
        this.nongBotController = nongBotController;
    }

    private NongBotController nongBotController;


//    registerNongBot(new NongBot("TEST_" + System.currentTimeMillis(), "TEST NAME"));
//    List<NongBot> nongBotList = getAllNongBot();
//        log.info("All NongBotList Count: " + nongBotList.size());
//        if(nongBotList.size() > 0) {
//        nongBotList.forEach(nongBot -> {
//            log.info(nongBot);
//        });
//        log.info(getNongBotById(nongBotList.get(nongBotList.size()-1).getId()));
//    }
}
