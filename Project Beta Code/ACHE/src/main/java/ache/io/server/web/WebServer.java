package ache.io.server.web;

import ache.ACHEContext;
import io.ninei.server.Web.DefaultWebServer;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Log4j2
@RestController
public class WebServer extends DefaultWebServer implements ACHEContext {
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

    @PostMapping("/connect")
    public String connect(@RequestHeader MultiValueMap<String, String> headers) {
        headers.forEach((key, value) -> {
            log.info("{}", String.format(
                "Header '%s' = %s", key, value.stream().collect(Collectors.joining("|"))));
        });
        return "Connection";
    }

    @RequestMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> download(@PathVariable String fileName) throws IOException {
        Path path = Paths.get("./download/"+fileName);
        String contentType = Files.probeContentType(path);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        Resource resource = new InputStreamResource(Files.newInputStream(path));
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @RequestMapping("/view/{fileName:.+}")
    public ResponseEntity<Resource> view(@PathVariable String fileName) throws IOException {
        Path path = Paths.get("./download/"+fileName);
        String contentType = Files.probeContentType(path);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);
        Resource resource = new InputStreamResource(Files.newInputStream(path));
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

//    protected WebServer(TB_PestInfoController pestInfoTBController, TB_MapInfoController mapInfoTBController) {
//        this.pestInfoTBController = pestInfoTBController;
//        this.mapInfoTBController = mapInfoTBController;
//    }

//    private TB_PestInfoController pestInfoTBController;
//    private TB_MapInfoController mapInfoTBController;
}
