package ache.io.tool;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Log4j2
@Service
public class ACHESchedule {

//    @Scheduled(cron = "0 0/22 10 * * ?") // 매일 10시 00분 시작해서 22분 주기로 실행
    @Scheduled(cron = "0 10 4 * * ?") // 매일 4시 10분에 실행
    public void deleteSystemCacheMemory() {
        try {
            log.info("Schedule~~ Delete System Cache Memory");
            shellCmd("sudo sysctl -w vm.drop_caches=3");
            shellCmd("sudo sysctl -w vm.drop_caches=2");
            log.info("Try Scheduled Command - Complete System Cache Memory");
        } catch (Exception e) {
            log.error(e);
        }
    }

    private static void shellCmd(String command) throws Exception {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command);
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while((line = br.readLine()) != null) {
            log.info(line);
        }
    }
}
