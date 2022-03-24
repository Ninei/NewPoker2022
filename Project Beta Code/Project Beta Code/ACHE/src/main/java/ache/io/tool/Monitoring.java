package ache.io.tool;

import ache.io.exception.ExceptionHandler;
import com.sun.management.OperatingSystemMXBean;
import lombok.extern.log4j.Log4j2;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

@Log4j2
public final class Monitoring {

    /**
     * CPU 사용량
     */
    public static boolean traceCPU(double limit) {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpu = osBean.getSystemCpuLoad();
        if(cpu * 100 >= limit) {
            String cpuUsage = String.format("CPU: %.2f", cpu * 100)+"%";
            log.warn(cpuUsage);
            ExceptionHandler.fireWarningMsg(cpuUsage);
            return true;
        }
        return false;
    }

    /**
     * Physical 메모리 사용량
     */
    public static void tracePhysicalMemory() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        log.info("PhysicalMemory: Used {} / Total {}",
            byteToString(osBean.getTotalPhysicalMemorySize()-osBean.getFreePhysicalMemorySize()),
            byteToString(osBean.getTotalPhysicalMemorySize()));
    }

    /**
     * Heap 메모리 사용량
     */
    public static void traceHeapMemory() {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        log.info("Heap: Used {} / Total {}, NoneHeapUsed: {}",
            byteToString(heapMemoryUsage.getUsed()), byteToString(heapMemoryUsage.getMax()),
            byteToString(nonHeapMemoryUsage.getUsed()));
    }

    public static String byteToString(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f%cB", bytes / 1000.0, ci.current());
    }
}
