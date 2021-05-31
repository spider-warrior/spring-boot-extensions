package cn.t.extension.springboot.starters.mim;

/**
 * MethodInvokeMonitor常量
 *
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2020-11-03 17:41
 **/
public class MethodInvokeMonitorConstants {
    public static final String MIM_DAO = "dao";
    public static final String MIM_SERVICE = "service";
    public static final String MIM_CONTROLLER = "controller";
    public static final String MIM_RPC = "rpc";
    public static final String MIM_HTTP = "http";

    public static final String ADVISOR_BEAN_NAME_PREFIX = "mim-advisor-";
    public static final String POINTCUT_BEAN_NAME_PREFIX = "mim-pointcut-";
    public static final String ADVICE_BEAN_NAME_PREFIX = "mim-advice-";
    public static final String LOGGER_NAME_PREFIX = "mim-";

}
