package cn.t.extension.springboot.starters.trace;


import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yj
 * @since 2020-05-21 21:57
 **/
@ConfigurationProperties(prefix = "extension.trace")
public class TraceExtensionProperties {

    private int maxHistories = 10;

    public int getMaxHistories() {
        return maxHistories;
    }

    public void setMaxHistories(int maxHistories) {
        this.maxHistories = maxHistories;
    }
}
