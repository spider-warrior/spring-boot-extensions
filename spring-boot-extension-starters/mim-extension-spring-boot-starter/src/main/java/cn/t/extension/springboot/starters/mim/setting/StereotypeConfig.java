package cn.t.extension.springboot.starters.mim.setting;

import java.util.List;

/**
 * @author <a href="mailto:yangjian@liby.ltd">研发部-杨建</a>
 * @version V1.0
 * @since 2020-11-03 19:04
 **/
public class StereotypeConfig {
    private String logLevel;
    private String logHome = "${user.home}/logs/${appName}/mim";
    private int logMaxHistory = 10;
    private int logMaxFileSize = 20480;
    private List<String> expressionList;

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogHome() {
        return logHome;
    }

    public void setLogHome(String logHome) {
        this.logHome = logHome;
    }

    public int getLogMaxHistory() {
        return logMaxHistory;
    }

    public void setLogMaxHistory(int logMaxHistory) {
        this.logMaxHistory = logMaxHistory;
    }

    public int getLogMaxFileSize() {
        return logMaxFileSize;
    }

    public void setLogMaxFileSize(int logMaxFileSize) {
        this.logMaxFileSize = logMaxFileSize;
    }

    public List<String> getExpressionList() {
        return expressionList;
    }

    public void setExpressionList(List<String> expressionList) {
        this.expressionList = expressionList;
    }

    @Override
    public String toString() {
        return "StereotypeConfig{" +
            "logLevel='" + logLevel + '\'' +
            ", logMaxHistory=" + logMaxHistory +
            ", logMaxFileSize=" + logMaxFileSize +
            ", expressionList=" + expressionList +
            '}';
    }
}
