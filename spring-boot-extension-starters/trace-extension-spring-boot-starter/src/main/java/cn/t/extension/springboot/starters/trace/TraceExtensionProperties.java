package cn.t.extension.springboot.starters.trace;


import cn.t.util.common.StringUtil;
import cn.t.util.common.SystemUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

/**
 * @author yj
 * @since 2020-05-21 21:57
 **/
@ConfigurationProperties(prefix = "extension.trace")
public class TraceExtensionProperties {

    private String appName;
    private String traceHome = SystemUtil.getSysProperty("user.home") + File.separator + "logs" + File.separator + "trace";
    private String traceFileName;
    private int maxHistories = 10;
    private int maxFieSize = 10;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTraceHome() {
        return this.traceHome;
    }

    public void setTraceHome(String traceHome) {
        this.traceHome = traceHome;
    }

    public String getTraceFileName() {
        if(StringUtil.isEmpty(traceFileName)) {
            return (StringUtil.isEmpty(getAppName()) ? "undefined" : getAppName()) + File.separator + "trace.log";
        } else {
            return traceFileName;
        }
    }

    public void setTraceFileName(String traceFileName) {
        this.traceFileName = traceFileName;
    }

    public int getMaxHistories() {
        return maxHistories;
    }

    public void setMaxHistories(int maxHistories) {
        this.maxHistories = maxHistories;
    }

    public int getMaxFieSize() {
        return maxFieSize;
    }

    public void setMaxFieSize(int maxFieSize) {
        this.maxFieSize = maxFieSize;
    }
}
