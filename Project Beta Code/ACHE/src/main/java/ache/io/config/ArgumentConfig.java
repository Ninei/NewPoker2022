package ache.io.config;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Getter
@Log4j2
@Service
public class ArgumentConfig {


    private void initArguments() {
        try {
            Set<String> optionNames = arguments.getOptionNames();
            for (String optionName : optionNames) {
                List<String> optionValues = arguments.getOptionValues(optionName);
                for (String optionValue : optionValues) {
                    if (optionName.equalsIgnoreCase("browserCount")) {
                        browserCount = Integer.decode(optionValue);
                    } else if(optionName.equalsIgnoreCase("tcsBrokerURL")) {
                        proxyInfo_url = optionValue;
                    } else if(optionName.equalsIgnoreCase("shop")) {
                        proxyInfo_shop = optionValue;
                    } else if(optionName.equalsIgnoreCase("dmc")) {
                        proxyInfo_dmc = optionValue;
                    } else if(optionName.equalsIgnoreCase("device")) {
                        proxyInfo_device = optionValue;
                    } else if(optionName.equalsIgnoreCase("pingInterval")) {
                        proxyInfo_pingInterval = Long.decode(optionValue);
                    } else if(optionName.equalsIgnoreCase("userMax")) {
                        proxyInfo_max = Integer.decode(optionValue);
                    } else if(optionName.equalsIgnoreCase("userLimit")) {
                        proxyInfo_limit = Integer.decode(optionValue);
                    }
                }
            }
        } catch (Exception e) {
            log.error("ArgumentConfig Init Fail!!", e);
        }
    }

    private ArgumentConfig(ApplicationArguments arguments, ACHEConfig acheConfig) {
        this.arguments = arguments;
        this.acheConfig = acheConfig;
        initArguments();
    }


    // Jar Option Value list
    protected String proxyInfo_url;
    protected String proxyInfo_shop;
    protected String proxyInfo_dmc;
    protected String proxyInfo_device;
    protected int proxyInfo_max;
    protected int proxyInfo_limit;
    protected long proxyInfo_pingInterval;

    protected int browserCount;

    private final ApplicationArguments arguments;
    private final ACHEConfig acheConfig;
}
