package io.ninei.global;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.InetAddress;
import java.text.SimpleDateFormat;

public abstract class DefaultConfig {

    public static  ConfigurableApplicationContext getConfigurableApplicationContext() {
        return configurableApplicationContext;
    }

    public static void initConfigure(ConfigurableApplicationContext context) throws Exception {
        configurableApplicationContext = context;
    }

    // 개발 환경 Intellij 빌드시 build-info.properties 파일이 resource 경로에 미생성, war 빌드 시 포함
    public static String getBuildVersion() {
        String version = "Unknown";
        String activeProfile = configurableApplicationContext.getEnvironment().getActiveProfiles()[0];
        try {
            version = configurableApplicationContext.getBean(BuildProperties.class).getVersion()+ "_"+activeProfile;
        } catch (Exception ignore) {}

        return version;
    }

    private static ConfigurableApplicationContext configurableApplicationContext;
}
