package cn.t.extension.springboot.starters.mim;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.*;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import cn.t.common.trace.logback.MimLogLayout;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static ch.qos.logback.core.util.FileSize.KB_COEFFICIENT;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(LogbackUtil.class);
    private static final String LOG_FILE_EXTENSION_NAME = ".log";
    private static final Object lock = new Object();
    private static InterpretationContext interpretationContext;

    public static Logger  buildLogger(String loggerName, String logLevel, String directory, String fileName, int maxHistory, int maxFileSize) {
        Logger logger = LoggerFactory.getLogger(loggerName);
        configLogger(logger, logLevel, directory, fileName, maxHistory, maxFileSize);
        return logger;
    }

    public static void configLogger(Logger logger, String logLevel, String directory, String fileName, int maxHistory, int maxFileSize) {
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if(logger instanceof ch.qos.logback.classic.Logger && loggerFactory instanceof LoggerContext) {
            ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger)logger;
            logbackLogger.setLevel(Level.toLevel(logLevel));
            logbackLogger.setAdditive(false);
            String appenderName = getMimLoggerAppender(logger.getName());
            RollingFileAppender<ILoggingEvent> appender = (RollingFileAppender<ILoggingEvent>)logbackLogger.getAppender(appenderName);
            if(appender == null) {
                logbackLogger.addAppender(buildRollingFileBasedTimeAppender((LoggerContext)loggerFactory, appenderName, parseValue(logbackLogger.getLoggerContext(), directory), fileName, maxHistory, maxFileSize));
            }
        } else {
            LOGGER.warn("不支持配置的日志类型: {}", logger.getClass().getName());
        }
    }

//    private static void addFileAppenderLogger(String directory, String fileName, int maxHistory, int maxFileSize) {
//        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
//        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
//        root.addAppender(buildRollingFileBasedTimeAppender(loggerContext, directory, fileName, maxHistory, maxFileSize));
//    }

    private static RollingFileAppender<ILoggingEvent> buildRollingFileBasedTimeAppender(LoggerContext loggerContext, String appenderName, String directory, String fileName, int maxHistory, int maxFileSize) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setName(appenderName);
        appender.setContext(loggerContext);
        appender.setFile(appendFilePath(directory, fileName + LOG_FILE_EXTENSION_NAME));

        //rolling policy
        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setParent(appender);
        rollingPolicy.setFileNamePattern(appendFilePath(directory, fileName + ".%d{yyyy-MM-dd}.%i" + LOG_FILE_EXTENSION_NAME));
        rollingPolicy.setMaxHistory(maxHistory);
        rollingPolicy.setMaxFileSize(new FileSize(maxFileSize * KB_COEFFICIENT));
        rollingPolicy.start();
        appender.setRollingPolicy(rollingPolicy);

        MimLogLayout mimLogLayout = new MimLogLayout();
        mimLogLayout.setContext(loggerContext);
        mimLogLayout.start();
        appender.setLayout(mimLogLayout);
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

    private static String getMimLoggerAppender(String loggerName) {
        return loggerName + "-appender";
    }

    private static String appendFilePath(String original, String append) {
        if(original.endsWith(File.separator)) {
            return original + append;
        } else {
            return original + File.separator + append;
        }
    }
}
