package cn.t.extension.springboot.starters.mim;

import cn.t.extension.springboot.starters.mim.setting.MethodInvokeMonitorSetting;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * mim config
 *
 * @author <a href="mailto:yangjian@liby.ltd">研发部-杨建</a>
 * @version V1.0
 * @since 2020-11-02 21:48
 **/
public class MethodInvokeMonitorConfiguration {

    @Bean
    MethodInvokeMonitorSetting methodInvokeMonitorSetting(ConfigurableEnvironment environment) {
        return new MethodInvokeMonitorSetting(environment);
    }

    @Bean
    MethodInvokeMonitorBeanRegister methodInvokeMonitorBeanRegister(MethodInvokeMonitorSetting methodInvokeMonitorSetting) {
        return new MethodInvokeMonitorBeanRegister(methodInvokeMonitorSetting);
    }
}
