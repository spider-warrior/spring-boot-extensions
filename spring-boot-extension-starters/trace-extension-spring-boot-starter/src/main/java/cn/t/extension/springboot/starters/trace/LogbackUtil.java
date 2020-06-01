package cn.t.extension.springboot.starters.trace;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import cn.t.common.trace.logback.JsonLogLayout;
import org.slf4j.LoggerFactory;

import java.io.File;

import static ch.qos.logback.core.util.FileSize.KB_COEFFICIENT;

/**
 * @author yj
 * @since 2020-05-21 12:42
 **/
public class LogbackUtil {

    public static void addFileAppenderLogger(String directory, String fileName, int maxHistory, int maxFileSize) {
        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(buildRollingFileBasedTimeAppender(loggerContext, directory, fileName, maxHistory, maxFileSize));
    }

    private static RollingFileAppender<ILoggingEvent> buildRollingFileBasedTimeAppender(LoggerContext loggerContext, String directory, String fileName, int maxHistory, int maxFileSize) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setName("TracedRollingFileAppender");
        appender.setContext(loggerContext);
        appender.setFile(directory + File.separator +fileName);

        //rolling policy
        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setParent(appender);
        rollingPolicy.setFileNamePattern(fileName + ".%d{yyyy-MM-dd}.%i.log");
        rollingPolicy.setMaxHistory(maxHistory);
        rollingPolicy.setMaxFileSize(new FileSize(maxFileSize * KB_COEFFICIENT));
        rollingPolicy.start();
        appender.setRollingPolicy(rollingPolicy);

        JsonLogLayout jsonLogLayout = new JsonLogLayout();
        jsonLogLayout.start();
        appender.setLayout(jsonLogLayout);
        appender.start();
        return appender;
    }
}
