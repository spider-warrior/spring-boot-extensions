package cn.t.extension.springboot.starters.mim.setting;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * MethodInvokeMonitor setting
 *
 * @author <a href="mailto:yangjian@liby.ltd">研发部-杨建</a>
 * @version V1.0
 * @since 2020-11-03 09:56
 **/
public class MethodInvokeMonitorSetting implements ApplicationListener<EnvironmentChangeEvent>, Ordered {
    private final ConfigurableEnvironment environment;

    private String logHome;
    private StereotypeConfig daoConfig;
    private StereotypeConfig serviceConfig;
    private StereotypeConfig controllerConfig;
    private StereotypeConfig rpcConfig;
    private StereotypeConfig invokeRpcConfig;
    private StereotypeConfig jobConfig;

    public ConfigurableEnvironment getEnvironment() {
        return environment;
    }

    public String getLogHome() {
        return logHome;
    }

    public void setLogHome(String logHome) {
        this.logHome = logHome;
    }

    public StereotypeConfig getDaoConfig() {
        return daoConfig;
    }

    public void setDaoConfig(StereotypeConfig daoConfig) {
        this.daoConfig = daoConfig;
    }

    public StereotypeConfig getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(StereotypeConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    public StereotypeConfig getControllerConfig() {
        return controllerConfig;
    }

    public void setControllerConfig(StereotypeConfig controllerConfig) {
        this.controllerConfig = controllerConfig;
    }

    public StereotypeConfig getRpcConfig() {
        return rpcConfig;
    }

    public void setRpcConfig(StereotypeConfig rpcConfig) {
        this.rpcConfig = rpcConfig;
    }

    public StereotypeConfig getInvokeRpcConfig() {
        return invokeRpcConfig;
    }

    public void setInvokeRpcConfig(StereotypeConfig invokeRpcConfig) {
        this.invokeRpcConfig = invokeRpcConfig;
    }

    public StereotypeConfig getJobConfig() {
        return jobConfig;
    }

    public void setJobConfig(StereotypeConfig jobConfig) {
        this.jobConfig = jobConfig;
    }

    public MethodInvokeMonitorSetting(ConfigurableEnvironment environment) {
        this.environment = environment;
        Binder.get(this.environment).bind("starter.method-invoke-monitor", Bindable.ofInstance(this));
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        Binder.get(this.environment).bind("starter.method-invoke-monitor", Bindable.ofInstance(this));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public String toString() {
        return "MethodInvokeMonitorSetting{" +
            "environment=" + environment +
            ", logHome='" + logHome + '\'' +
            ", daoConfig=" + daoConfig +
            ", serviceConfig=" + serviceConfig +
            ", controllerConfig=" + controllerConfig +
            ", rpcConfig=" + rpcConfig +
            ", invokeRpcConfig=" + invokeRpcConfig +
            ", jobConfig=" + jobConfig +
            '}';
    }
}
