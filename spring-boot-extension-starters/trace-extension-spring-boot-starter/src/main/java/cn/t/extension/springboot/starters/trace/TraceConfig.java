package cn.t.extension.springboot.starters.trace;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yj
 * @since 2020-05-21 21:56
 **/

@EnableConfigurationProperties(TraceExtensionProperties.class)
@Configuration
public class TraceConfig {
    public TraceConfig(TraceExtensionProperties traceExtensionProperties) {
        LogbackUtil.addFileAppenderLogger("app-trace", "app-trace", traceExtensionProperties.getMaxHistories());
    }
}
