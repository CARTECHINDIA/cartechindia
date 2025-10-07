package com.cartechindia.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class LoggerConfig {

    private static final String LOG_DIR = "logs";

    @PostConstruct
    public void setupFileLogger() {

        // Ensure logs directory exists
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            if (created) {
                System.out.println("Created logs directory: " + LOG_DIR);
            }
        }

        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        // File appender
        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
        fileAppender.setContext(rootLogger.getLoggerContext());
        fileAppender.setAppend(true); // keep appending to current log
        fileAppender.setPrudent(false);

        // Encoder
        PatternLayoutEncoder fileEncoder = new PatternLayoutEncoder();
        fileEncoder.setContext(rootLogger.getLoggerContext());
        fileEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n");
        fileEncoder.start();
        fileAppender.setEncoder(fileEncoder);

        // Rolling policy
        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setContext(rootLogger.getLoggerContext());
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.setFileNamePattern(LOG_DIR + "/application-%d{yyyy-MM-dd}.log");
        rollingPolicy.setMaxHistory(30);
        rollingPolicy.setCleanHistoryOnStart(true); // optional: roll over old logs on startup
        rollingPolicy.start();

        fileAppender.setRollingPolicy(rollingPolicy);
        fileAppender.start();

        // Attach appender to your package logger
        Logger appLogger = (Logger) LoggerFactory.getLogger("com.cartechindia");
        appLogger.setLevel(Level.DEBUG);
        appLogger.addAppender(fileAppender);

        // Sample startup entries
        appLogger.info("Logger initialized: CarTechIndia logging started");
        appLogger.debug("Sample DEBUG entry: Service layer ready");
        appLogger.warn("Sample WARN entry: Check configuration if needed");
        appLogger.error("Sample ERROR entry: No actual error, just example");
    }
}
