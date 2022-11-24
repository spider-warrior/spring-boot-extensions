package cn.t.extension.springboot.starters.mim;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.joran.spi.*;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.qos.logback.core.util.FileSize.MB_COEFFICIENT;

/**
 * 日志util
 * @author yj
 * @since 2020-05-21 12:42
 *
 * 重新配置日志参考
 * org.springframework.boot.logging.logback.LogbackLoggingSystem#stopAndReset
 * LoggingApplicationListener
 **/
public class LogbackUtil {

    private static volatile InterpretationContext interpretationContext;
    private static final Object lock = new Object();

    public static Logger createLogger(String loggerName, String level, String logFilePath, String logFilePathPattern, int maxHistory, int maxFileSizeInMb, String appenderName, Encoder<ILoggingEvent> encoder) {
        Logger logger = LoggerFactory.getLogger(loggerName);
        configLogger(logger, level, logFilePath, logFilePathPattern, maxHistory, maxFileSizeInMb, appenderName, encoder);
        return logger;
    }

    public static void configLogger(Logger logger, String level, String logFilePath, String logFilePathPattern, int maxHistory, int maxFileSizeInMb, String appenderName, Encoder<ILoggingEvent> encoder) {
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if(logger instanceof ch.qos.logback.classic.Logger && loggerFactory instanceof LoggerContext) {
            ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger)logger;
            LoggerContext loggerContext = (LoggerContext)loggerFactory;
            logbackLogger.setLevel(Level.toLevel(level));
            logbackLogger.setAdditive(false);
            RollingFileAppender<ILoggingEvent> appender = (RollingFileAppender<ILoggingEvent>)logbackLogger.getAppender(appenderName);
            if(appender == null) {
                logbackLogger.addAppender(createRollingFileBasedTimeAppender(loggerContext, appenderName, logFilePath, logFilePathPattern, maxHistory, maxFileSizeInMb, encoder));
            }
        } else {
            throw new UnsupportedLogContextException(loggerFactory.getClass().getName());
        }
    }

    private static RollingFileAppender<ILoggingEvent> createRollingFileBasedTimeAppender(LoggerContext context, String appenderName, String logFilePath, String logFilePathPattern, int maxHistory, int maxFileSizeInMb, Encoder<ILoggingEvent> encoder) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setName(appenderName);
        appender.setContext(context);
        appender.setFile(parseValue(context, logFilePath));

        //rolling policy
        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(appender);
        rollingPolicy.setFileNamePattern(parseValue(context, logFilePathPattern));
        rollingPolicy.setMaxHistory(maxHistory);
        rollingPolicy.setMaxFileSize(new FileSize(maxFileSizeInMb * MB_COEFFICIENT));
        rollingPolicy.start();
        appender.setRollingPolicy(rollingPolicy);

        encoder.setContext(context);
        encoder.start();
        appender.setEncoder(encoder);
        appender.start();
        return appender;
    }

    private static String parseValue(Context context, String value) {
        if(interpretationContext == null) {
            synchronized (lock) {
                if(interpretationContext == null) {
                    RuleStore rs = new SimpleRuleStore(context);
                    Interpreter interpreter = new Interpreter(context, rs, new ElementPath());
                    interpretationContext = interpreter.getInterpretationContext();
                }
            }
        }
        return interpretationContext.subst(value);
    }

    public static class UnsupportedLogContextException extends RuntimeException {
        public UnsupportedLogContextException(String message) {
            super(message);
        }
    }
}
