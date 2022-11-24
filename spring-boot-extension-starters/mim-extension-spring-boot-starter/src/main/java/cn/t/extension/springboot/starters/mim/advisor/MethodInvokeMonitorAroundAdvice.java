package cn.t.extension.springboot.starters.mim.advisor;

import cn.t.common.trace.generic.TraceConstants;
import cn.t.common.trace.logback.encoder.MimLogEncoder;
import cn.t.extension.springboot.starters.mim.LogbackUtil;
import cn.t.extension.springboot.starters.mim.anno.MonitorConfig;
import cn.t.extension.springboot.starters.mim.setting.StereotypeConfig;
import cn.t.util.common.JsonUtil;
import cn.t.util.common.RandomUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MethodInvokeMonitor around advice
 * 动态配置: EnvironmentChangeEvent
 * todo 扩展LoggerContextListener实现stop,reset监听
 **/
public class MethodInvokeMonitorAroundAdvice implements MethodInterceptor, ApplicationListener<ApplicationEvent> {

    private static final AtomicLong index = new AtomicLong(1);
    private static final Logger logger = LoggerFactory.getLogger(MethodInvokeMonitorAroundAdvice.class);
    private static final MimLogEncoder mimLogEncoder = new MimLogEncoder();
    private final StereotypeConfig stereotypeConfig;
    private final Logger aopLogger;
    private final String logName;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        MonitorConfig monitorConfig = method.getAnnotation(MonitorConfig.class);
        if ((monitorConfig != null && monitorConfig.ignore()) || (!aopLogger.isWarnEnabled())) {
            return invocation.proceed();
        } else {
            long start = System.currentTimeMillis();
            String oldParentSpanId = MDC.get(TraceConstants.P_SPAN_ID_NAME);
            String oldSpanId = MDC.get(TraceConstants.SPAN_ID_NAME);
            //pSpan,span
            MDC.put(TraceConstants.P_SPAN_ID_NAME, oldSpanId);
            MDC.put(TraceConstants.SPAN_ID_NAME, generateSpanId(start));
            Exception exception = null;
            Object result = null;
            try {
                result = invocation.proceed();
                return result;
            } catch (Exception e) {
                exception = e;
                throw e;
            } finally {
                try {
                    interceptorMethod(invocation, monitorConfig, method, result, exception, start);
                } catch (Exception e) {
                    logger.error("mim异常", e);
                }
                MDC.put(TraceConstants.P_SPAN_ID_NAME, oldParentSpanId);
                MDC.put(TraceConstants.SPAN_ID_NAME, oldSpanId);
            }
        }
    }

    private void interceptorMethod(MethodInvocation invocation, MonitorConfig monitorConfig, Method method, Object result, Exception exception, long start) {
        long end = System.currentTimeMillis();
        long rt = end - start;
        boolean inputPrint;
        boolean outputPrint;
        if (monitorConfig == null) {
            inputPrint = true;
            outputPrint = true;
        } else {
            inputPrint = monitorConfig.inputPrint();
            outputPrint = monitorConfig.outputPrint();
        }
        String inputString = buildInputString(inputPrint, invocation.getArguments(), method.getParameters());
        boolean success = exception == null;
        //start,end
        MDC.put(TraceConstants.TRACE_START_TIME_NAME, String.valueOf(start));
        MDC.put(TraceConstants.TRACE_END_TIME_NAME, String.valueOf(end));
        //class,method,success,rt
        MDC.put(TraceConstants.TRACE_CLASS_NAME, method.getDeclaringClass().getName());
        MDC.put(TraceConstants.TRACE_METHOD_NAME, method.getName());
        MDC.put(TraceConstants.TRACE_SUCCESS_NAME, String.valueOf(success));
        MDC.put(TraceConstants.TRACE_RT_NAME, String.valueOf(rt));
        long mt = System.currentTimeMillis() - start;
        if (success) {
            aopLogger.info("mt={}, input={}, output={}", mt, inputString, buildOutputString(outputPrint, method.getReturnType(), result));
        } else {
            aopLogger.warn("mt={}, input={}, error={}", mt, inputString, buildExceptionString(exception));
        }
        //start,end
        MDC.remove(TraceConstants.TRACE_START_TIME_NAME);
        MDC.remove(TraceConstants.TRACE_END_TIME_NAME);
        ////class,method,success,rt
        MDC.remove(TraceConstants.TRACE_CLASS_NAME);
        MDC.remove(TraceConstants.TRACE_METHOD_NAME);
        MDC.remove(TraceConstants.TRACE_SUCCESS_NAME);
        MDC.remove(TraceConstants.TRACE_RT_NAME);
    }

    private String buildInputString(boolean inputPrint, Object[] arguments, Parameter[] parameters) {
        if (!inputPrint) {
            return "ignored";
        } else {
            Map<String, Object> inputMap = new HashMap<>();
            for (int i = 0; i < parameters.length; i++) {
                inputMap.put(parameters[i].getName(), arguments[i]);
            }
            return inputMap.toString();
        }
    }

    private String buildOutputString(boolean outputPrint, Class<?> type, Object result) {
        if (!outputPrint) {
            return "ignored";
        } else {
            if (void.class.equals(type)) {
                return "type: void";
            } else {
                String resultJson = "serializeFailed";
                try {
                    resultJson = JsonUtil.serialize(result);
                } catch (Exception e) {
                    logger.error("", e);
                }
                return "type: " + type.getSimpleName() + ", result: " + resultJson;
            }
        }
    }

    private String buildExceptionString(Exception e) {
        if (e == null) {
            return "";
        }
        return e.getMessage();
    }

    private String generateSpanId(long timestamp) {
        return timestamp + "_" + index.getAndIncrement() + "_" + RandomUtil.randomString(15);
    }

    public MethodInvokeMonitorAroundAdvice(String loggerName, StereotypeConfig stereotypeConfig) {
        this.logName = loggerName;
        this.stereotypeConfig = stereotypeConfig;
        this.aopLogger = LogbackUtil.createLogger(this.logName, this.stereotypeConfig.getLogLevel(), getLogFilePath(), getLogFilePathPattern(), this.stereotypeConfig.getLogMaxHistory(), this.stereotypeConfig.getLogMaxFileSize(), getMimLoggerAppenderName(), mimLogEncoder);
    }

    private String getLogFilePath() {
        return appendFilePath(this.stereotypeConfig.getLogHome(), this.logName + ".log");
    }

    private String getLogFilePathPattern() {
        return appendFilePath(this.stereotypeConfig.getLogHome(), this.logName + ".%d{yyyy-MM-dd}.%i.log");
    }

    private String getMimLoggerAppenderName() {
        return this.logName + "-appender";
    }

    private static String appendFilePath(String original, String append) {
        if (original.endsWith(File.separator)) {
            return original + append;
        } else {
            return original + File.separator + append;
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent || event.getClass().getName().equals("org.springframework.cloud.context.environment.EnvironmentChangeEvent")) {
            logger.info("ApplicationEvent: {}, source: {}", event, event.getSource());
            LogbackUtil.configLogger(aopLogger, this.stereotypeConfig.getLogLevel(), getLogFilePath(), getLogFilePathPattern(), this.stereotypeConfig.getLogMaxHistory(), this.stereotypeConfig.getLogMaxFileSize(), getMimLoggerAppenderName(), mimLogEncoder);
        }/* else if(event instanceof RefreshScopeRefreshedEvent) {
            logger.info("RefreshScopeRefreshedEvent: {}, source: {}", event, event.getSource());
            LogbackUtil.configLogger(aopLogger, this.stereotypeConfig.getLogLevel(), this.stereotypeConfig.getLogHome(), this.logName, this.stereotypeConfig.getLogMaxHistory(), this.stereotypeConfig.getLogMaxFileSize());
        }*/
    }
}
