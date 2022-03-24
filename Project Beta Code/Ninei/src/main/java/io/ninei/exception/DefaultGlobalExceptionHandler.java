package io.ninei.exception;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Log4j2
@ControllerAdvice
public class DefaultGlobalExceptionHandler {
    public static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(value = NullPointerException.class)
    public void defaultErrorHandler(Exception e) throws Exception {
        log.error("/n/n/n/n/Exception");
    }

    public static void fireWarningMsg(String msg) {
        fireMsg(msg, SentryLevel.WARNING);
    }

    public static void fireMsg(String msg, SentryLevel level) {
        Sentry.captureMessage(msg, level);
    }

    public static void fireException(String msg, Throwable e) {
        log.error(msg, e);
    }

    public static String HOST_NAME;

    static {
        try {
            HOST_NAME = InetAddress.getLocalHost().getHostName() + " - ";
        } catch (UnknownHostException e) {
            HOST_NAME = "Unknown";
            log.error("DefaultGlobalExceptionHandler", e);
        }
    }
}
