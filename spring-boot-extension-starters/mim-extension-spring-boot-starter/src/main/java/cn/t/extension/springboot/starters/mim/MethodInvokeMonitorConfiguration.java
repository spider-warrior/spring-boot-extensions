package cn.t.extension.springboot.starters.mim;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * mim config
 *
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2020-11-02 21:48
 **/
public class MethodInvokeMonitorConfiguration {
    @Bean
    MethodInvokeMonitorBeanRegister methodInvokeMonitorBeanRegister(ConfigurableEnvironment environment) {
        return new MethodInvokeMonitorBeanRegister(environment);
    }
}
