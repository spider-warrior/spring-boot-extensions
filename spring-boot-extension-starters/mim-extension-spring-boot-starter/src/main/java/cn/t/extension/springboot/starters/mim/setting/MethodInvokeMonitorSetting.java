package cn.t.extension.springboot.starters.mim.setting;

/**
 * MethodInvokeMonitor setting
 *
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2020-11-03 09:56
 **/
public class MethodInvokeMonitorSetting {
    private String logHome;
    private StereotypeConfig daoConfig;
    private StereotypeConfig serviceConfig;
    private StereotypeConfig controllerConfig;
    private StereotypeConfig rpcConfig;
    private StereotypeConfig httpConfig;

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

    public StereotypeConfig getHttpConfig() {
        return httpConfig;
    }

    public void setHttpConfig(StereotypeConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    @Override
    public String toString() {
        return "MethodInvokeMonitorSettings{" +
                "logHome='" + logHome + '\'' +
                ", daoConfig=" + daoConfig +
                ", serviceConfig=" + serviceConfig +
                ", controllerConfig=" + controllerConfig +
                ", rpcConfig=" + rpcConfig +
                ", httpConfig=" + httpConfig +
                '}';
    }



}
