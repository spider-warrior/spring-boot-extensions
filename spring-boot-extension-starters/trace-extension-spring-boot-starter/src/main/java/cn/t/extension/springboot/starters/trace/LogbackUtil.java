package cn.t.extension.springboot.starters.trace;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.*;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import cn.t.common.trace.logback.MimLogLayout;
import cn.t.util.io.FileUtil;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import static ch.qos.logback.core.util.FileSize.KB_COEFFICIENT;

/**
 * @author yj
 * @since 2020-05-21 12:42
 **/
public class LogbackUtil {

    private static final String LOG_FILE_EXTENSION_NAME = ".log";
    private static final Object lock = new Object();
    private static InterpretationContext interpretationContext;

    public static void addFileAppenderLogger(String directory, String fileName, int maxHistory, int maxFileSize) {
        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(buildRollingFileBasedTimeAppender(loggerContext, directory, fileName, maxHistory, maxFileSize));
    }

    public static org.slf4j.Logger buildLogger(String loggerName, String logLevel, String directory, String fileName, int maxHistory, int maxFileSize) {
        org.slf4j.Logger logger = LoggerFactory.getLogger(loggerName);
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if(logger instanceof ch.qos.logback.classic.Logger && loggerFactory instanceof Context) {
            ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger)logger;
            logbackLogger.setLevel(Level.toLevel(logLevel));
            logbackLogger.setAdditive(false);
            logbackLogger.addAppender(buildRollingFileBasedTimeAppender((Context)loggerFactory, parseValue(logbackLogger.getLoggerContext(), directory), fileName, maxHistory, maxFileSize));
        }
        return logger;
    }

    private static RollingFileAppender<ILoggingEvent> buildRollingFileBasedTimeAppender(Context loggerContext, String directory, String fileName, int maxHistory, int maxFileSize) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setName("TracedRollingFileAppender");
        appender.setContext(loggerContext);
        appender.setFile(FileUtil.appendFilePath(directory, fileName + LOG_FILE_EXTENSION_NAME));

        //rolling policy
        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setParent(appender);
        rollingPolicy.setFileNamePattern(FileUtil.appendFilePath(directory, fileName + ".%d{yyyy-MM-dd}.%i" + LOG_FILE_EXTENSION_NAME));
        rollingPolicy.setMaxHistory(maxHistory);
        rollingPolicy.setMaxFileSize(new FileSize(maxFileSize * KB_COEFFICIENT));
        rollingPolicy.start();
        appender.setRollingPolicy(rollingPolicy);

        MimLogLayout mimLogLayout = new MimLogLayout();
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
}
