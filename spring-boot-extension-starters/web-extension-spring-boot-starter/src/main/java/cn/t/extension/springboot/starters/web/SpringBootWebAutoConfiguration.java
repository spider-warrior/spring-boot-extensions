package cn.t.extension.springboot.starters.web;

import cn.t.extension.springboot.starters.web.jackson.NullValueModule;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
@AutoConfigureBefore(value = {ValidationAutoConfiguration.class, WebMvcAutoConfiguration.class})
@EnableConfigurationProperties(value = {SpringBootWebProperties.class})
public class SpringBootWebAutoConfiguration {

    private final SpringBootWebProperties springBootWebProperties;
    private final ServerProperties serverProperties;
//    private final DispatcherServletPath dispatcherServletPath;

    /* **************************************************** global exception handler ************************************************************/
    @Bean
    GlobalExceptionHandler globalExceptionHandler(List<ErrorHandler> errorHandlerList) {
        return new GlobalExceptionHandler(errorHandlerList);
    }

//    @Bean
//    AppErrorPageRegistrar appErrorPageRegistrar() {
//        return new AppErrorPageRegistrar(serverProperties, dispatcherServletPath);
//    }

    @Bean
    GlobalErrorController defaultHandler(ErrorAttributes errorAttributes, ObjectProvider<ErrorViewResolver> errorViewResolvers) {
        return new GlobalErrorController(errorAttributes, this.serverProperties.getError(), errorViewResolvers.orderedStream().collect(Collectors.toList()));
    }

    /* **************************************************** param validator ************************************************************/
    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(springBootWebProperties.getFailFast())
            .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    /* **************************************************** jackson ************************************************************/
    @Bean
    SimpleModule nullValueModule() {
        return new NullValueModule();
    }

    @ConditionalOnMissingBean
    @Bean
    public LocalDateTimeSerializer localDateTimeSerializer() {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @ConditionalOnMissingBean
    @Bean
    public LocalDateTimeDeserializer localDateTimeDeserializer() {
        return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd[ [HH][:mm][:ss][.SSS]]"));
    }

    @ConditionalOnMissingBean
    @Bean
    public LocalDateSerializer localDateSerializer() {
        return new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @ConditionalOnMissingBean
    @Bean
    public LocalDateDeserializer localDateDeserializer() {
        return new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomDefault(
        LocalDateTimeSerializer localDateTimeSerializer,
        LocalDateTimeDeserializer localDateTimeDeserializer,
        LocalDateSerializer localDateSerializer,
        LocalDateDeserializer localDateDeserializer) {
        return (jacksonObjectMapperBuilder) -> {
            jacksonObjectMapperBuilder.serializerByType(LocalDateTime.class, localDateTimeSerializer);
            jacksonObjectMapperBuilder.deserializerByType(LocalDateTime.class, localDateTimeDeserializer);
            jacksonObjectMapperBuilder.serializerByType(LocalDate.class, localDateSerializer);
            jacksonObjectMapperBuilder.deserializerByType(LocalDate.class, localDateDeserializer);
        };
    }

    public SpringBootWebAutoConfiguration(SpringBootWebProperties springBootWebProperties, ServerProperties serverProperties) {
        this.springBootWebProperties = springBootWebProperties;
        this.serverProperties = serverProperties;
    }
}
