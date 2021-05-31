package cn.t.extension.springboot.starters.trace;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;

/**
 * @author yj
 * @since 2020-05-21 21:56
 **/
public class AsyncTraceConfig {

    @ConditionalOnBean(AsyncAnnotationBeanPostProcessor.class)
    @Bean
    TracedTaskDecorator tracedTaskDecorator() {
        return new TracedTaskDecorator();
    }

}
