package cn.t.extension.springboot.starters.mybatis;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * WebExtension配置属性
 *
 * @author <a href="mailto:jian.yang@liby.ltd">野生程序员-杨建</a>
 * @version V1.0
 * @since 2020-03-08 20:53
 **/
@ConfigurationProperties(prefix = "extension.mybatis")
public class MybatisExtensionProperties {

    private String keyGeneratorType = "inMemoryKeyGenerator";


    public String getKeyGeneratorType() {
        return keyGeneratorType;
    }

    public void setKeyGeneratorType(String keyGeneratorType) {
        this.keyGeneratorType = keyGeneratorType;
    }
}
