package cn.t.extension.springboot.starters.mim.setting;

import java.util.List;

/**
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2020-11-03 19:04
 **/
public class StereotypeConfig {
    private String logLevel;
    private List<String> expressionList;

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
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
            ", expressionList=" + expressionList +
            '}';
    }
}
