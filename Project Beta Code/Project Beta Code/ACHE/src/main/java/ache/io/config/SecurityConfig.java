package ache.io.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.WebSessionServerCsrfTokenRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import java.io.File;

@Configuration
@EnableWebFluxSecurity
@Log4j2
public class SecurityConfig {

    @Bean // CSRF 처리 및 로직
    SecurityWebFilterChain springSecurityFilterChain(final ServerHttpSecurity http) {
        http.csrf().requireCsrfProtectionMatcher(
            serverWebExchange -> ServerWebExchangeMatchers
                .pathMatchers("/urls-with-csrf-check/**").matches(serverWebExchange)
        ).and()
            .authorizeExchange()
            .pathMatchers("/download/{fileName:.+}")
//            .authenticated()
            .permitAll()
            .pathMatchers("/view/{fileName:.+}")
//            .authenticated()
            .permitAll()
            .pathMatchers("/addPestInfo")
            .authenticated()
            .pathMatchers("/getMapInfo")
            .authenticated()
            .pathMatchers("/kakao/**")
            .authenticated()
            .pathMatchers("/kakao/*.html")
            .authenticated()
            .pathMatchers("/checkConfig/**") // Admin Config Check
            .authenticated()
            .pathMatchers("/hello")
            .authenticated()
            .pathMatchers("/login")
            .authenticated()
            .pathMatchers("/who")
            .hasRole("USER")
            .pathMatchers("/primes")
            .hasAuthority("ROLE_USER")
            .pathMatchers("/admin")
            .access((mono, context) -> mono
                .map(auth -> auth.getAuthorities().stream()
                    .filter(e -> e.getAuthority().equals("ROLE_ADMIN"))
                    .count() > 0)
                .map(AuthorizationDecision::new)
            )
            .and()
            .csrf()
            .csrfTokenRepository(csrfTokenRepository())
            .and()
            .httpBasic()
            .and()
            // redirect error
//            .formLogin().loginPage("/login")
//            .and()
            .logout();

        return http.build();
    }

    @Bean
    public ServerCsrfTokenRepository csrfTokenRepository() {
        WebSessionServerCsrfTokenRepository repository = new WebSessionServerCsrfTokenRepository();
        repository.setHeaderName("X-CSRF-TK");
        return repository;
    }

    static final boolean SSL = System.getProperty("ssl") != null;

    public static SslContext initSslContext(boolean isLive) throws Exception {
        if(!SSL) return null;

        final SslContext sslContext;
        try {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            if(isLive) {
                File kcf = new File(SslHandler.class.getClassLoader().getResource("security/certificate.pem").getFile());
                File kf = new File(SslHandler.class.getClassLoader().getResource("security/key_pkcs8.pem").getFile());
                sslContext = SslContextBuilder.forServer(kcf, kf).build(); // PEM File Build
            } else {
                sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build(); // Self Build
            }
            log.info("Initiating SSL context");
        } catch (Exception e) { throw e; }
        return sslContext;
    }
}
