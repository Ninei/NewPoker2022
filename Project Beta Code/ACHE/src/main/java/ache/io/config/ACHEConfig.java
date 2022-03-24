package ache.io.config;

import ache.ACHEContext;
import ache.io.browser.ChromiumBrowser;
import io.netty.handler.logging.LogLevel;
import io.ninei.global.DefaultConfig;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

//@Log4j2
@Getter
@Configuration
public class ACHEConfig extends DefaultConfig implements ACHEContext {

    @Value("${server.address}")
    protected String serverAddress;

    @Value("${server.port}")
    protected String serverPort;

    @Value("${netty.play.port}")
    protected int playPort;

    @Value("${netty.cloudApp.port}")
    protected int cloudAppPort;

    @Value("${logging.level.root}")
    protected LogLevel rootLogLevel;

    @Value("${spring.profiles.active}")
    protected String active;

    public boolean isLive() { return isLive(active); }
}
