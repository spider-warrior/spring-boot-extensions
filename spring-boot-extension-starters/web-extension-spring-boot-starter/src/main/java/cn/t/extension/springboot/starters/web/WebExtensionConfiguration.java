package cn.t.extension.springboot.starters.web;

import cn.t.base.common.response.ResultVoWrapper;
import cn.t.extension.springboot.starters.web.component.WebExtensionProperties;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@AutoConfigureBefore(value = ValidationAutoConfiguration.class)
@EnableConfigurationProperties(value = {WebExtensionProperties.class})
@ComponentScan(basePackages = "cn.t.extension.springboot.starters.web.component")
@Configuration
public class WebExtensionConfiguration {

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

    public WebExtensionConfiguration( WebExtensionProperties webExtensionProperties) {
        this.webExtensionProperties = webExtensionProperties;
    }
}
