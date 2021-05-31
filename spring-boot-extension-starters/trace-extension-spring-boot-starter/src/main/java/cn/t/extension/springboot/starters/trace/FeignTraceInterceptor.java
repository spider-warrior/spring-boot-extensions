package cn.t.extension.springboot.starters.trace;

import cn.t.common.trace.generic.TraceIdGenerator;
import cn.t.util.common.StringUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

import static cn.t.common.trace.generic.TraceConstants.*;
import static cn.t.common.trace.generic.TraceConstants.TRACE_ID_HEADER_NAME;


/**
 * 控制器日志拦截器(没想好如何使用)
 *
 * @author yj
 * @since 2020-04-13 11:52
 **/
public class FeignTraceInterceptor implements RequestInterceptor {

    private final String applicationName;

    @Override
    public void apply(RequestTemplate template) {
        Map<String, Collection<String>> headers = template.headers();
        if(CollectionUtils.isEmpty(headers.get(TRACE_ID_HEADER_NAME))) {
            String traceId = MDC.get(TRACE_ID_NAME);
            if(StringUtil.isEmpty(traceId)) {
                traceId = TraceIdGenerator.generateTraceId(applicationName);
            }
            template.header(TRACE_ID_HEADER_NAME, traceId);
        }
        if(CollectionUtils.isEmpty(headers.get(TRACE_CLIENT_ID_HEADER_NAME))) {
            template.header(TRACE_CLIENT_ID_HEADER_NAME, MDC.get(CLIENT_ID_NAME));
        }
    }

    public FeignTraceInterceptor(String applicationName) {
        this.applicationName = applicationName;
    }
}
