package cn.t.extension.springboot.starters.mim;

import cn.t.extension.springboot.starters.mim.advisor.MethodInvokeMonitorAroundAdvice;
import cn.t.extension.springboot.starters.mim.setting.MethodInvokeMonitorSetting;
import cn.t.extension.springboot.starters.mim.setting.StereotypeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * MethodInvokeMonitor bean register
 *
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2020-11-03 16:23
 **/
public class MethodInvokeMonitorBeanRegister implements BeanDefinitionRegistryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MethodInvokeMonitorBeanRegister.class);
    private final MethodInvokeMonitorSetting methodInvokeMonitorSetting;

    private void registerMimMonitor(BeanDefinitionRegistry registry, String type, String logHome, StereotypeConfig stereotypeConfig) {
        if(stereotypeConfig == null) {
            logger.info("mim type: {} not config", type);
            return;
        }
        List<String> expressionList = stereotypeConfig.getExpressionList();
        if(CollectionUtils.isEmpty(expressionList)) {
            logger.info("mim type: {} with empty configList", type);
            return;
        }
        BeanDefinitionBuilder adviceBeanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(MethodInvokeMonitorAroundAdvice.class);
        adviceBeanDefinitionBuilder.addConstructorArgValue(buildLoggerName(type));
        adviceBeanDefinitionBuilder.addConstructorArgValue(logHome);
        adviceBeanDefinitionBuilder.addConstructorArgValue(stereotypeConfig.getLogLevel());
        AbstractBeanDefinition adviceBeanDefinition = adviceBeanDefinitionBuilder.getBeanDefinition();
        adviceBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        String adviceBeanName = buildAdviceBeanName(type);
        registry.registerBeanDefinition(buildAdviceBeanName(type), adviceBeanDefinition);
        for(int i=0; i<expressionList.size(); i++) {
            BeanDefinitionBuilder pointcutBeanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(AspectJExpressionPointcut.class);
            pointcutBeanDefinitionBuilder.addPropertyValue("expression", expressionList.get(i));
            AbstractBeanDefinition pointcutBeanDefinition = pointcutBeanDefinitionBuilder.getBeanDefinition();
            pointcutBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
            String pointcutBeanDefinitionBeanName = buildPointcutBeanName(type, i);
            registry.registerBeanDefinition(pointcutBeanDefinitionBeanName, pointcutBeanDefinition);

            BeanDefinitionBuilder advisorBeanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(DefaultPointcutAdvisor.class);
            advisorBeanDefinitionBuilder.addPropertyReference("pointcut", pointcutBeanDefinitionBeanName);
            advisorBeanDefinitionBuilder.addPropertyReference("advice", adviceBeanName);
            AbstractBeanDefinition advisorBeanDefinition = advisorBeanDefinitionBuilder.getBeanDefinition();
            advisorBeanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
            registry.registerBeanDefinition(buildAdvisorBeanName(type, i), advisorBeanDefinition);
        }
    }

    private String buildAdvisorBeanName(String type, int index) {
        return MethodInvokeMonitorConstants.ADVISOR_BEAN_NAME_PREFIX + type + "_" + index;
    }


    private String buildPointcutBeanName(String type, int index) {
        return MethodInvokeMonitorConstants.POINTCUT_BEAN_NAME_PREFIX + type + "_" + index;
    }

    private String buildAdviceBeanName(String type) {
        return MethodInvokeMonitorConstants.ADVICE_BEAN_NAME_PREFIX + type;
    }

    private String buildLoggerName(String type) {
        return MethodInvokeMonitorConstants.LOGGER_NAME_PREFIX + type;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        registerMimMonitor(registry, MethodInvokeMonitorConstants.MIM_DAO, methodInvokeMonitorSetting.getLogHome(), methodInvokeMonitorSetting.getDaoConfig());
        registerMimMonitor(registry, MethodInvokeMonitorConstants.MIM_SERVICE, methodInvokeMonitorSetting.getLogHome(), methodInvokeMonitorSetting.getServiceConfig());
        registerMimMonitor(registry, MethodInvokeMonitorConstants.MIM_CONTROLLER, methodInvokeMonitorSetting.getLogHome(), methodInvokeMonitorSetting.getControllerConfig());
        registerMimMonitor(registry, MethodInvokeMonitorConstants.MIM_RPC, methodInvokeMonitorSetting.getLogHome(), methodInvokeMonitorSetting.getRpcConfig());
        registerMimMonitor(registry, MethodInvokeMonitorConstants.MIM_HTTP, methodInvokeMonitorSetting.getLogHome(), methodInvokeMonitorSetting.getHttpConfig());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    public MethodInvokeMonitorBeanRegister(ConfigurableEnvironment environment) {
        this.methodInvokeMonitorSetting = Binder.get(environment).bind("starter.method-invoke-monitor", MethodInvokeMonitorSetting.class).orElse(new MethodInvokeMonitorSetting());
    }
}
