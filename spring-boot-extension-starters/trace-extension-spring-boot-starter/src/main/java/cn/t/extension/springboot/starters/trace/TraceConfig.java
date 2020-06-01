package cn.t.extension.springboot.starters.trace;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author yj
 * @since 2020-05-21 21:56
 **/
@EnableConfigurationProperties(TraceExtensionProperties.class)
@Configuration
public class TraceConfig {

    private final TraceExtensionProperties traceExtensionProperties;

    @ConditionalOnClass(OncePerRequestFilter.class)
    @Bean
    TraceIdFilter traceIdFilter() {
        return new TraceIdFilter(traceExtensionProperties.getAppName());
    }

    public TraceConfig(TraceExtensionProperties traceExtensionProperties) {
        this.traceExtensionProperties = traceExtensionProperties;
        LogbackUtil.addFileAppenderLogger(traceExtensionProperties.getTraceHome(), traceExtensionProperties.getTraceFileName(), traceExtensionProperties.getMaxHistories(), traceExtensionProperties.getMaxFieSize());
    }

}
