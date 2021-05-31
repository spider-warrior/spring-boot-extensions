package cn.t.extension.springboot.starters.trace;

import feign.Feign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author yj
 * @since 2020-05-21 21:56
 **/
@ConditionalOnClass(Feign.class)
@AutoConfigureBefore(FeignAutoConfiguration.class)
public class FeignTraceConfig {

    @Bean
    FeignTraceInterceptor feignTraceInterceptor(@Value("${spring.application.name:}") String applicationName) {
        return new FeignTraceInterceptor(applicationName);
    }

}
