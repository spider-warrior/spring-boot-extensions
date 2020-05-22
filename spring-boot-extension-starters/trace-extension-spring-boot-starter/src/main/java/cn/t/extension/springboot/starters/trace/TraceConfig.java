package cn.t.extension.springboot.starters.trace;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @author yj
 * @since 2020-05-21 21:56
 **/
@ConditionalOnProperty("extension.trace")
@EnableConfigurationProperties(TraceExtensionProperties.class)
@Configuration
public class TraceConfig {

    public TraceConfig(TraceExtensionProperties traceExtensionProperties) {
        LogbackUtil.addFileAppenderLogger("app-trace", traceExtensionProperties.getTraceHome() + File.separator + "app-trace", traceExtensionProperties.getMaxHistories());
    }

}
