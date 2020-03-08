package cn.t.extension.springboot.starters.web.component;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * WebExtension配置属性
 *
 * @author <a href="mailto:jian.yang@liby.ltd">野生程序员-杨建</a>
 * @version V1.0
 * @since 2020-03-08 20:53
 **/
@ConfigurationProperties(prefix = "extension.validator")
public class WebExtensionProperties {
    private Boolean failFast;

    public Boolean getFailFast() {
        return failFast;
    }

    public void setFailFast(Boolean failFast) {
        this.failFast = failFast;
    }
}
