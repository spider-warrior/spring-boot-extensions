package cn.t.extension.springboot.starters.trace;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author <a href="mailto:yangjian@ifenxi.com">研发部-杨建</a>
 * @version V1.0
 * @since 2020-12-19 16:11
 **/
public class TracedTaskDecorator implements TaskDecorator {

    @Override
    public @NonNull Runnable decorate(@NonNull Runnable runnable) {
        Map<String, String> context = MDC.getCopyOfContextMap();
        if(!CollectionUtils.isEmpty(context)) {
            return () -> {
                MDC.setContextMap(context);
                try {
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        }
        return runnable;
    }
}
