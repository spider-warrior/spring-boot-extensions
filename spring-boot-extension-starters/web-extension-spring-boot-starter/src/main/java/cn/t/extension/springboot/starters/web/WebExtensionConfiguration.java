package cn.t.extension.springboot.starters.web;

import cn.t.base.common.response.ResultVoWrapper;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
@AutoConfigureBefore(value = {ValidationAutoConfiguration.class, WebMvcAutoConfiguration.class})
@EnableConfigurationProperties(value = {WebExtensionProperties.class})
@Configuration
public class WebExtensionConfiguration {

    private final ServerProperties serverProperties;
    private final WebExtensionProperties webExtensionProperties;

    @Bean
    public ResultVoWrapper resultVoWrapper() {
        return new ResultVoWrapper();
    }

    @Bean
    public Validator validator(){
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(webExtensionProperties.getFailFast())
            .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    @Bean
    GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler(resultVoWrapper());
    }

    @Bean
    WebErrorController appErrorController() {
        return new WebErrorController(serverProperties, resultVoWrapper());
    }

    public WebExtensionConfiguration(ServerProperties serverProperties, WebExtensionProperties webExtensionProperties) {
        this.serverProperties = serverProperties;
        this.webExtensionProperties = webExtensionProperties;
    }
}
