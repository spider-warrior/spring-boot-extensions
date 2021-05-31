package cn.t.extension.springboot.starters.web;

import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.stream.Collectors;

/**
 * springboot web auto configuration
 *
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2021-02-24 17:32
 **/
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
@AutoConfigureBefore(value = {ValidationAutoConfiguration.class, WebMvcAutoConfiguration.class})
@EnableConfigurationProperties(value = {SpringBootWebProperties.class})
public class SpringBootWebAutoConfiguration {

    private final SpringBootWebProperties springBootWebProperties;
    private final ServerProperties serverProperties;
    private final DispatcherServletPath dispatcherServletPath;

    @Bean
    GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    public Validator validator(){
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(springBootWebProperties.getFailFast())
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

//    @Bean
//    AppErrorPageRegistrar appErrorPageRegistrar() {
//        return new AppErrorPageRegistrar(serverProperties, dispatcherServletPath);
//    }

    @Bean
    GlobalErrorController defaultHandler(ErrorAttributes errorAttributes, ObjectProvider<ErrorViewResolver> errorViewResolvers) {
        return new GlobalErrorController(errorAttributes, this.serverProperties.getError(), errorViewResolvers.orderedStream().collect(Collectors.toList()));
    }

    public SpringBootWebAutoConfiguration(SpringBootWebProperties springBootWebProperties, ServerProperties serverProperties, DispatcherServletPath dispatcherServletPath) {
        this.springBootWebProperties = springBootWebProperties;
        this.serverProperties = serverProperties;
        this.dispatcherServletPath = dispatcherServletPath;
    }
}
