package cn.t.extension.springboot.starters.mim.anno;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MonitorConfig {
    /**
     * 是否忽略
     */
    boolean ignore() default false;

    /**
     * 是否打印入参
     */
    boolean inputPrint() default true;

    /**
     * 是否打印出参
     */
    boolean outputPrint() default true;
}
