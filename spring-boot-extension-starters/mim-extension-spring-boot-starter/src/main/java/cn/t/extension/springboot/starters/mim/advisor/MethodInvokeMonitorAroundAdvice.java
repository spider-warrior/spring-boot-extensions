package cn.t.extension.springboot.starters.mim.advisor;

import cn.t.common.trace.generic.TraceConstants;
import cn.t.extension.springboot.starters.mim.LogbackUtil;
import cn.t.extension.springboot.starters.mim.anno.MonitorConfig;
import cn.t.util.common.JsonUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * MethodInvokeMonitor around advice
 * 动态配置: EnvironmentChangeEvent
 * todo 扩展LoggerContextListener实现stop,reset监听
 *
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2020-11-03 16:42
 **/
public class MethodInvokeMonitorAroundAdvice implements MethodInterceptor, ApplicationListener<ContextRefreshedEvent> {

    private static final String DEFAULT_LOG_HOME = "${user.home}/logs/${appName}";
    private static final Logger logger = LoggerFactory.getLogger(MethodInvokeMonitorAroundAdvice.class);
    private InternalInterceptor interceptor = new NoActionInterceptor();
    private final String loggerName;
    private final String logLevel;
    private final String logHome;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return interceptor.invoke(invocation);
    }

    public MethodInvokeMonitorAroundAdvice(String loggerName, String logHome, String logLevel) {
        if(StringUtils.isEmpty(logHome)) {
            logHome = DEFAULT_LOG_HOME;
        }
        this.loggerName = loggerName;
        this.logHome = logHome;
        this.logLevel = logLevel;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        if(applicationContext.getParent() != null && applicationContext.getParent().getParent() == null) {
            interceptor = new MimInterceptor(LogbackUtil.buildLogger(loggerName, this.logLevel, this.logHome, this.loggerName, 10, 10240));
        }
    }

    @FunctionalInterface
    private interface InternalInterceptor {
        Object invoke(MethodInvocation invocation) throws Throwable;
    }
    private static class NoActionInterceptor implements InternalInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            return invocation.proceed();
        }
    }
    private static class MimInterceptor implements InternalInterceptor {
        private final Logger aopLogger;

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method method = invocation.getMethod();
            MonitorConfig monitorConfig = method.getAnnotation(MonitorConfig.class);
            if(monitorConfig != null && monitorConfig.ignore()) {
                return invocation.proceed();
            } else {
                long start = System.currentTimeMillis();
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
                }
            }
        }

        private void interceptorMethod(MethodInvocation invocation, MonitorConfig monitorConfig, Method method, Object result, Exception exception, long start) {
            long rt = System.currentTimeMillis() - start;
            boolean inputPrint;
            boolean outputPrint;
            if(monitorConfig == null) {
                inputPrint = true;
                outputPrint = true;
            } else {
                inputPrint = monitorConfig.inputPrint();
                outputPrint = monitorConfig.outputPrint();
            }
            String inputString = buildInputString(inputPrint, invocation.getArguments(), method.getParameters());
            boolean success = exception == null;
            MDC.put(TraceConstants.TRACE_CLASS_NAME, method.getDeclaringClass().getName());
            MDC.put(TraceConstants.TRACE_METHOD_NAME, method.getName());
            MDC.put(TraceConstants.TRACE_SUCCESS_NAME, String.valueOf(success));
            MDC.put(TraceConstants.TRACE_RT_NAME, String.valueOf(rt));
            long mt = System.currentTimeMillis() - start;
            if(success) {
                aopLogger.info("mt={}, input={}, output={}", mt, inputString, buildOutputString(outputPrint, method.getReturnType(), result));
            } else {
                aopLogger.info("mt={}, input={}, error={}", mt, inputString, buildExceptionString(exception));
            }
            MDC.remove(TraceConstants.TRACE_CLASS_NAME);
            MDC.remove(TraceConstants.TRACE_METHOD_NAME);
            MDC.remove(TraceConstants.TRACE_SUCCESS_NAME);
            MDC.remove(TraceConstants.TRACE_RT_NAME);
        }

        private String buildInputString(boolean inputPrint, Object[] arguments, Parameter[] parameters) {
            if(!inputPrint) {
                return "ignored";
            } else {
                Map<String, Object> inputMap = new HashMap<>();
                for(int i=0; i<parameters.length; i++) {
                    inputMap.put(parameters[i].getName(), arguments[i]);
                }
                return inputMap.toString();
            }
        }

        private String buildOutputString(boolean outputPrint, Class<?> type, Object result) {
            if(!outputPrint) {
                return "ignored";
            } else {
                if(void.class.equals(type)) {
                    return "type: void";
                } else {
                    String resultJson = "serializeFailed";
                    try {
                        resultJson = JsonUtil.serialize(result);
                    } catch (Exception e){
                        logger.error("", e);
                    }
                    return "type: " + type.getSimpleName() + ", result: " + resultJson;
                }
            }
        }

        private String buildExceptionString(Exception e) {
            if(e == null) {
                return "";
            }
            return e.getMessage();
        }

        public MimInterceptor(Logger aopLogger) {
            this.aopLogger = aopLogger;
        }
    }
}
