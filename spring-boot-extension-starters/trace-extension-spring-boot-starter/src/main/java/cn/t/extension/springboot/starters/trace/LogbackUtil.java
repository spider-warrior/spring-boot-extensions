package cn.t.extension.springboot.starters.trace;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import cn.t.common.trace.loback.JsonLogLayout;
import org.slf4j.LoggerFactory;

/**
 * @author yj
 * @since 2020-05-21 12:42
 **/
public class LogbackUtil {

    public static void addFileAppenderLogger(String name, String fileName, int maxHistory) {
        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(name);
        logger.setAdditive(false);
        logger.addAppender(buildRollingFileAppender(loggerContext, fileName, maxHistory));
        loggerContext.getLoggerList().add(logger);
    }

    private static RollingFileAppender<ILoggingEvent> buildRollingFileAppender(LoggerContext loggerContext, String fileName, int maxHistory) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setName("TracedRollingFileAppender");

        //rolling policy
        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setParent(appender);
        rollingPolicy.setFileNamePattern(fileName + ".%d{yyyy-MM-dd}.%i.log");
        rollingPolicy.setMaxHistory(maxHistory);
        rollingPolicy.setMaxFileSize(FileSize.valueOf("100MB"));
        rollingPolicy.start();

        appender.setFile(fileName);
        appender.setContext(loggerContext);
        appender.setRollingPolicy(rollingPolicy);
        appender.setLayout(new JsonLogLayout());
        appender.start();
        return appender;
    }
}
