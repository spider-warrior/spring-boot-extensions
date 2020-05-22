package cn.t.extension.springboot.starters.trace;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.File;

/**
 * @author yj
 * @since 2020-05-21 21:56
 **/
@ConditionalOnProperty("extension.trace")
@EnableConfigurationProperties(TraceExtensionProperties.class)
@Configuration
public class TraceConfig {

    private final String appName;

    @ConditionalOnClass(OncePerRequestFilter.class)
    @Bean
    TraceIdFilter traceIdFilter() {
        return new TraceIdFilter(appName);
    }

    public TraceConfig(@Value("${spring.application.name:undefiled}") String appName, TraceExtensionProperties traceExtensionProperties) {
        this.appName = appName;
        LogbackUtil.addFileAppenderLogger(appName, traceExtensionProperties.getTraceHome() + File.separator + "app-trace", traceExtensionProperties.getMaxHistories());
    }

}
