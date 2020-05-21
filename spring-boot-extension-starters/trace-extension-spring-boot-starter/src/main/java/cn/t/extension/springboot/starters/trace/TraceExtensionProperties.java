package cn.t.extension.springboot.starters.trace;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
 * @author yj
 * @since 2020-05-21 21:57
 **/
@ConfigurationProperties(prefix = "extension.trace")
public class TraceExtensionProperties {

    private String traceHome = "~" + File.separator + "logs";

    private int maxHistories = 10;

    public String getTraceHome() {
        return traceHome;
    }

    public void setTraceHome(String traceHome) {
        this.traceHome = traceHome;
    }

    public int getMaxHistories() {
        return maxHistories;
    }

    public void setMaxHistories(int maxHistories) {
        this.maxHistories = maxHistories;
    }
}
