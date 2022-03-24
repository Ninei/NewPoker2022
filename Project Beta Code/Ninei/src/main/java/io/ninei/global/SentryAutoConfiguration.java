package io.ninei.global;

//import io.sentry.Sentry;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;

@Log4j2
public class SentryAutoConfiguration {

    @Bean
    public void init() {
//        Sentry.init();
//        log.info("Init(), {}", Sentry.getContext());
    }

    public SentryAutoConfiguration() {
        log.info("SentryAutoConfiguration");
    }
}
